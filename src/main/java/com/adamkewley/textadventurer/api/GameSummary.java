package com.adamkewley.textadventurer.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class GameSummary {

    @JsonProperty
    private GameId id;

    @JsonProperty
    private String name;

    @JsonProperty
    private String author;

    @JsonProperty
    private String description;

    @JsonProperty
    private GamePlayDetails play;



    public GameSummary(GameId id, String name, String author, String description, GamePlayDetails play) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.description = description;
        this.play = play;
    }



    public GameId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public GamePlayDetails getPlay() {
        return play;
    }
}
