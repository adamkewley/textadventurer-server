package com.adamkewley.textadventurer.dao;

import com.adamkewley.textadventurer.api.GameId;
import com.adamkewley.textadventurer.api.GamePlayDetails;
import com.adamkewley.textadventurer.api.GameSummary;
import com.adamkewley.textadventurer.configuration.GameDetails;

import java.util.List;
import java.util.Optional;

public interface GamesDAO {

    List<GameSummary> getAllGameSummaries();
    Optional<GamePlayDetails> getGamePlayDetailsById(GameId gameId);
    Optional<GameDetails> getGameDetailsById(GameId gameId);
}
