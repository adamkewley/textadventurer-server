package com.adamkewley.textadventurer.server.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GamePlayDetails {

    @JsonProperty
    private String url;



    public GamePlayDetails(String url) {
        this.url = url;
    }



    public String getUrl() {
        return url;
    }
}
