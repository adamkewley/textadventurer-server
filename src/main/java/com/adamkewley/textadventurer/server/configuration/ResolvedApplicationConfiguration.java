package com.adamkewley.textadventurer.server.configuration;

import java.util.List;

public final class ResolvedApplicationConfiguration {

    private List<GameConfiguration> gameConfigurations;



    public ResolvedApplicationConfiguration(List<GameConfiguration> gameConfigurations) {
        this.gameConfigurations = gameConfigurations;
    }



    public List<GameConfiguration> getGameConfigurations() {
        return gameConfigurations;
    }
}
