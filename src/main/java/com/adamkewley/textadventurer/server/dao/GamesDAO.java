package com.adamkewley.textadventurer.server.dao;

import com.adamkewley.textadventurer.server.api.GameId;
import com.adamkewley.textadventurer.server.api.GameSummary;
import com.adamkewley.textadventurer.server.configuration.GameDetails;

import java.util.List;
import java.util.Optional;

public interface GamesDAO {

    List<GameSummary> getAllGameSummaries();
    Optional<GameDetails> getGameDetailsById(GameId gameId);
}
