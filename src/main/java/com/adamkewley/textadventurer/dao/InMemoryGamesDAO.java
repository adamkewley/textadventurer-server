package com.adamkewley.textadventurer.dao;

import com.adamkewley.textadventurer.api.GameId;
import com.adamkewley.textadventurer.api.GamePlayDetails;
import com.adamkewley.textadventurer.api.GameSummary;
import com.adamkewley.textadventurer.configuration.GameConfiguration;
import com.adamkewley.textadventurer.configuration.GameDetails;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class InMemoryGamesDAO implements GamesDAO {

    private final List<GameConfiguration> gameConfigurations;



    public InMemoryGamesDAO(List<GameConfiguration> gameConfigurations) {
        this.gameConfigurations = gameConfigurations;
    }



    @Override
    public List<GameSummary> getAllGameSummaries() {
        return this.gameConfigurations.stream().map(GameConfiguration::toGameSummary).collect(Collectors.toList());
    }

    @Override
    public Optional<GamePlayDetails> getGamePlayDetailsById(GameId gameId) {
        return gameConfigurations.stream().filter(gameConfiguration -> {
            return gameConfiguration.getGame().getId().equals(gameId);
        }).findFirst().map(GameConfiguration::toGameSummary).map(GameSummary::getPlay);
    }

    @Override
    public Optional<GameDetails> getGameDetailsById(GameId gameId) {
        return gameConfigurations.stream().filter(gameConfiguration -> {
            return gameConfiguration.getGame().getId().equals(gameId);
        }).findFirst().map(GameConfiguration::getGame);
    }
}
