package com.adamkewley.textadventurer.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public final class GameBootConfiguration {

    @JsonProperty
    private String application;

    @JsonProperty
    private List<String> withArgs;



    public String getApplication() {
        return application;
    }

    public List<String> getWithArgs() {
        return withArgs;
    }
}
