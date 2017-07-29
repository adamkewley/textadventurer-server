package com.adamkewley.textadventurer.server;

import com.adamkewley.textadventurer.server.api.GameId;
import com.adamkewley.textadventurer.server.api.GamesResponse;
import com.adamkewley.textadventurer.server.configuration.GameConfiguration;
import com.adamkewley.textadventurer.server.configuration.GameDetails;
import com.adamkewley.textadventurer.server.configuration.ResolvedApplicationConfiguration;
import com.adamkewley.textadventurer.server.configuration.UnresolvedApplicationConfiguration;
import com.adamkewley.textadventurer.server.dao.GamesDAO;
import com.adamkewley.textadventurer.server.dao.InMemoryGamesDAO;
import com.adamkewley.textadventurer.server.resources.GameSocket;
import com.adamkewley.textadventurer.server.resources.GamesResource;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import fi.iki.elonen.NanoWSD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;


public class App extends NanoWSD {

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper().registerModule(new Jdk8Module());
    private static final ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory()).registerModule(new Jdk8Module());
    private static int serverPort = 8080;
    private static final String GAME_DETAILS_FILE_NAME = "details.yml";
    private static final String GAME_FILES_DIR_NAME = "game-files";
    private static final String GAMES_API_PATH = "/games";
    private static final Logger log = LoggerFactory.getLogger(App.class);

    private static Path gamesDirPath;



    public static void main(String[] args) {
        try {
            if (args.length != 2) {
                log.error("Application was not supplied with a port and configuration file as an argument!");
                System.exit(1);
            }

            serverPort = Integer.parseInt(args[0]);
            final Path configurationFilePath = Paths.get(args[1]);

            log.info("Reading configuration file at: " + configurationFilePath);

            final UnresolvedApplicationConfiguration unresolvedApplicationConfiguration =
                    YAML_MAPPER.readValue(configurationFilePath.toFile(), UnresolvedApplicationConfiguration.class);

            log.info("Configuration file deserialized");

            gamesDirPath = configurationFilePath.getParent().resolve(unresolvedApplicationConfiguration.getGames().getDir());

            if (!gamesDirPath.toFile().exists()) {
                log.error(gamesDirPath + ": No such directory");
                System.exit(1);
            }

            log.info("Reading through games folder at: " + gamesDirPath);

            final File gameDirs[] = gamesDirPath.toFile().listFiles();
            final ArrayList<GameConfiguration> gameConfigurations = new ArrayList<>();

            for (File gameDir : gameDirs) {
                if (gameDir.isDirectory()) {
                    log.info("Found game dir: " + gameDir);

                    final GameId gameId = new GameId(gameDir.toPath().getFileName().toString());
                    final GameConfiguration gameConfiguration = loadGameConfiguration(gameId, gameDir.toPath());

                    gameConfigurations.add(gameConfiguration);

                    log.info(gameId + ": successfully loaded");

                } else continue;
            }

            final ResolvedApplicationConfiguration resolvedApplicationConfiguration =
                    new ResolvedApplicationConfiguration(gameConfigurations);

            new App(resolvedApplicationConfiguration);
        } catch (IOException ex) {
            log.error("Could not start server: " + ex);
            System.exit(1);
        }
    }

    private static GameConfiguration loadGameConfiguration(GameId gameId, Path gameDir) throws IOException {

        log.info("Loading game details for " + gameId);
        final File gameDetailsFile = gameDir.resolve(GAME_DETAILS_FILE_NAME).toFile();

        if (!gameDetailsFile.exists()) {
            log.error("A game details file for " + gameId + " does not exist at " + gameDetailsFile.getAbsolutePath());
            System.exit(1);
            return null;
        } else {
            final GameConfiguration gameConfiguration = YAML_MAPPER.readValue(gameDetailsFile, GameConfiguration.class);
            gameConfiguration.getGame().setId(gameId);

            final Path gameFilesPath = gameDir.resolve(GAME_FILES_DIR_NAME);
            final File gameFiles = gameFilesPath.toFile();

            if (!gameFiles.exists()) {
                log.error("A " + GAME_FILES_DIR_NAME + " folder does not exist in " + gameDir);
                System.exit(1);
                return null;
            }

            gameConfiguration.setGameFilesPath(gameFilesPath);
            log.info("Game details for " + gameId + " loaded");

            return gameConfiguration;
        }
    }



    private final GamesDAO gamesDAO;
    private final GamesResource gamesResource;



    public App(ResolvedApplicationConfiguration resolvedApplicationConfiguration) throws IOException {

        super(serverPort);

        this.gamesDAO = new InMemoryGamesDAO(resolvedApplicationConfiguration.getGameConfigurations());
        this.gamesResource = new GamesResource(this.gamesDAO);

        start(-1, false);

        log.info("Server booted on port " + serverPort);
        log.info("GET " + GAMES_API_PATH + " to list games in JSON");

        System.in.read();
    }

    @Override
    public Response serveHttp(IHTTPSession session) {
        log.info("HTTP: " + Helpers.prettyPrint(session));

        final String uri = session.getUri();

        if (uri.contains(GAMES_API_PATH)) {
            final GamesResponse gamesResponse = gamesResource.getAllGames();
            final String gamesResponseJSON;
            try {
                gamesResponseJSON = JSON_MAPPER.writeValueAsString(gamesResponse);
                return newFixedLengthResponse(Response.Status.OK, "application/json", gamesResponseJSON);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        } else {
            return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "Not found");
        }
    }

    @Override
    protected WebSocket openWebSocket(IHTTPSession ihttpSession) {
        log.info("Websocket:" + Helpers.prettyPrint(ihttpSession));

        final GameId gameId = new GameId(Helpers.extractLastElementOfURI(ihttpSession.getUri()));

        final Optional<GameDetails> maybeGameDetails = this.gamesDAO.getGameDetailsById(gameId);

        if (maybeGameDetails.isPresent()) {
            final GameDetails gameDetails = maybeGameDetails.get();

            final Path gameWorkingDir = gamesDirPath.resolve(gameId.toString()).resolve("game-files");

            return new GameSocket(ihttpSession, gameDetails, gameWorkingDir);
        } else return null; // 404
    }
}
