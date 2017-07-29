package com.adamkewley.textadventurer.server.resources;

import com.adamkewley.textadventurer.server.ObservableProcess;
import com.adamkewley.textadventurer.server.configuration.GameBootConfiguration;
import com.adamkewley.textadventurer.server.configuration.GameDetails;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoWSD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

public final class GameSocket extends NanoWSD.WebSocket {

    private Logger log = LoggerFactory.getLogger(GameSocket.class);
    private ObservableProcess process;
    private final GameDetails gameDetails;
    private final Path workingDir;



    public GameSocket(NanoHTTPD.IHTTPSession handshakeRequest, GameDetails gameDetails, Path workingDir) {
        super(handshakeRequest);
        this.gameDetails = gameDetails;
        this.workingDir = workingDir;
    }



    protected void onOpen() {
        final GameBootConfiguration bootConfiguration = this.gameDetails.getBoot();

        this.process = new ObservableProcess(bootConfiguration.getApplication(), bootConfiguration.getWithArgs(), workingDir);

        this.process.getAllOutputsObservable().subscribe(msg -> {
            try {
                send(msg);
            } catch (IOException e) {
                log.error("Could not send a message to the client. Killing process" );
                this.process.destroy();
            }
        }, (err) -> {}, () -> {
            try {
                this.close(NanoWSD.WebSocketFrame.CloseCode.GoingAway, "Process ended", false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        try {
            this.process.start();
        } catch (IOException e) {
            log.error("Could not start a subprocess - killing and closing connection");
            this.process.destroy();
            try {
                close(NanoWSD.WebSocketFrame.CloseCode.InternalServerError, "Error starting game", true);
            } catch (IOException e1) {
                throw new RuntimeException(e1);
            }
        }
    }

    protected void onClose(NanoWSD.WebSocketFrame.CloseCode closeCode, String s, boolean b) {
        this.process.destroy();
    }

    protected void onMessage(NanoWSD.WebSocketFrame webSocketFrame) {
        this.process.send(webSocketFrame.getTextPayload());
    }

    protected void onPong(NanoWSD.WebSocketFrame webSocketFrame) {}

    protected void onException(IOException e) {}
}
