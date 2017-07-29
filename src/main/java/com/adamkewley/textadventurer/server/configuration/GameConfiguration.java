package com.adamkewley.textadventurer.server.configuration;

import com.adamkewley.textadventurer.server.Helpers;
import com.adamkewley.textadventurer.server.api.GameSummary;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.nio.file.Path;

public class GameConfiguration {

    @JsonProperty
    private GameDetails game;

    private Path gameFilesPath;



    public GameDetails getGame() {
        return game;
    }

    public Path getGameFilesPath() {
        return  gameFilesPath;
    }

    public void setGameFilesPath(Path gameFilesPath) {
        this.gameFilesPath = gameFilesPath;
    }

    public GameSummary toGameSummary() {
        return new GameSummary(
                this.game.getId(),
                this.game.getName(),
                this.game.getAuthor().getName(),
                this.game.getDescription(),
                Helpers.createGamePlayDetails(this.game.getId()));
    }
}
