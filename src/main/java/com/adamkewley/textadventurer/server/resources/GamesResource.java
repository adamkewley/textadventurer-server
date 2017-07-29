package com.adamkewley.textadventurer.server.resources;

import com.adamkewley.textadventurer.server.api.GameSummary;
import com.adamkewley.textadventurer.server.api.GamesResponse;
import com.adamkewley.textadventurer.server.dao.GamesDAO;

import java.util.List;
import java.util.Objects;

public final class GamesResource {

    private GamesDAO gamesDAO;



    public GamesResource(GamesDAO gamesDAO) {
        Objects.requireNonNull(gamesDAO);

        this.gamesDAO = gamesDAO;
    }



    public GamesResponse getAllGames() {

        final List<GameSummary> summaries = this.gamesDAO.getAllGameSummaries();

        return new GamesResponse(summaries);
    }
}
