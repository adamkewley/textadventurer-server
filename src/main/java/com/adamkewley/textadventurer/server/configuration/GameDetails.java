package com.adamkewley.textadventurer.server.configuration;

import com.adamkewley.textadventurer.server.api.GameId;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class GameDetails {

    @JsonProperty
    private GameId id;

    @JsonProperty
    private String name;

    @JsonProperty
    private String description;

    @JsonProperty
    private GameAuthor author;

    @JsonProperty
    private GameBootConfiguration boot;



    public GameId getId() {
        return id;
    }

    public void setId(GameId gameId) {
        this.id = gameId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public GameAuthor getAuthor() {
        return author;
    }

    public GameBootConfiguration getBoot() {
        return boot;
    }
}
