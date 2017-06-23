package com.adamkewley.textadventurer.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class GamesDir {

    @JsonProperty
    private String dir;

    public String getDir() {
        return dir;
    }
}
