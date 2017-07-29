package com.adamkewley.textadventurer.server.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class GamesDir {

    @JsonProperty
    private String dir;

    public String getDir() {
        return dir;
    }
}
