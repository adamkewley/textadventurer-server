package com.adamkewley.textadventurer.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public final class GamesResponse {

    @JsonProperty
    private List<GameSummary> games;



    public GamesResponse(List<GameSummary> games) {
        this.games = games;
    }



    public List<GameSummary> getGames() {
        return games;
    }
}
