package com.adamkewley.textadventurer;

import com.adamkewley.textadventurer.api.GameId;
import com.adamkewley.textadventurer.api.GamePlayDetails;
import fi.iki.elonen.NanoHTTPD;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Helpers {

    private static final Pattern uriPattern = Pattern.compile("[^/]+?$");

    public static GamePlayDetails createGamePlayDetails(GameId gameId) {
        return new GamePlayDetails("/play/" + gameId.toString());
    }

    public static String prettyPrint(NanoHTTPD.IHTTPSession session) {
        return session.getRemoteHostName() + " - " + session.getMethod() + " " + session.getUri();
    }

    public static String extractLastElementOfURI(String uri) {
        final Matcher matcher = uriPattern.matcher(uri);
        matcher.find();

        return matcher.group(0);
    }
}
