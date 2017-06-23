package com.adamkewley.textadventurer.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class GameAuthor {

    @JsonProperty
    private String name;



    public String getName() {
        return name;
    }
}
