package com.adamkewley.textadventurer.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public final class GameId {

    private final String id;



    @JsonCreator
    public GameId(String id) {
        this.id = id;
    }



    @Override
    @JsonValue
    public String toString() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GameId gameId = (GameId) o;

        return id != null ? id.equals(gameId.id) : gameId.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
