package com.adamkewley.textadventurer.server.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Unresolved because the application configuration contains
 * paths to the game definitions. These need to be be deserialized
 * and any paths within *resolved* relative to each config file.
 */
public final class UnresolvedApplicationConfiguration {

    @JsonProperty
    private GamesDir games;



    public GamesDir getGames() {
        return games;
    }
}
