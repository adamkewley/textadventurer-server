package com.adamkewley.textadventurer;

import com.zaxxer.nuprocess.NuAbstractProcessHandler;
import com.zaxxer.nuprocess.NuProcess;
import com.zaxxer.nuprocess.NuProcessBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.subjects.ReplaySubject;
import rx.subjects.Subject;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public final class ObservableProcess {

    private static final Logger log = LoggerFactory.getLogger(ObservableProcess.class);



    private final Subject<String, String> stdoutSubject = ReplaySubject.create();
    private final Subject<String, String> stderrSubject = ReplaySubject.create();
    private final List<String> command;
    private final Path workingDir;

    private Optional<NuProcess> maybeRunningProcess = Optional.empty();



    public ObservableProcess(String application, List<String> args, Path workingDir) {
        final List<String> appCall = new ArrayList<>();
        appCall.add(application);
        appCall.addAll(args);

        this.command = appCall;
        this.workingDir = workingDir;
    }



    public void start() throws IOException {
        if (maybeRunningProcess.isPresent()) {
            log.error("Tried to run an already running process (command: " + this.command + ")");
            return;
        }

        log.info("Building process for: " + this.command);

        final NuProcessBuilder processBuilder = new NuProcessBuilder(command);
        processBuilder.setCwd(workingDir);
        processBuilder.setProcessListener(new NuAbstractProcessHandler() {
            @Override
            public void onPreStart(NuProcess nuProcess) {
                super.onPreStart(nuProcess);
            }

            @Override
            public void onStart(NuProcess nuProcess) {
                super.onStart(nuProcess);
            }

            @Override
            public void onExit(int statusCode) {
                super.onExit(statusCode);
                log.info(ObservableProcess.this.command + ": stopped");
            }

            @Override
            public void onStdout(ByteBuffer buffer, boolean closed) {
                if (closed) {
                    ObservableProcess.this.stdoutSubject.onCompleted();
                } else {
                    byte[] bytes = new byte[buffer.remaining()];
                    buffer.get(bytes);
                    ObservableProcess.this.stdoutSubject.onNext(new String(bytes));
                }

                super.onStdout(buffer, closed);
            }

            @Override
            public void onStderr(ByteBuffer buffer, boolean closed) {
                if (closed) {
                    ObservableProcess.this.stderrSubject.onCompleted();
                } else {
                    byte[] bytes = new byte[buffer.remaining()];
                    buffer.get(bytes);
                    ObservableProcess.this.stderrSubject.onNext(new String(bytes));
                }

                super.onStderr(buffer, closed);
            }

            @Override
            public boolean onStdinReady(ByteBuffer buffer) {
                return super.onStdinReady(buffer);
            }
        });

        this.maybeRunningProcess = Optional.of(processBuilder.start());

        log.info(this.command + ": started");
    }

    public Observable<String> getAllOutputsObservable() {
        return this.stderrSubject.mergeWith(stdoutSubject);
    }

    public void destroy() {
        log.info("Destroying " + command);
        if (this.maybeRunningProcess.isPresent()) {
            final NuProcess runningProcess = this.maybeRunningProcess.get();

            runningProcess.destroy(false);

            try {
                runningProcess.waitFor(3, TimeUnit.SECONDS);

                if (runningProcess.isRunning())
                    runningProcess.destroy(true);
            } catch (InterruptedException e) {
                runningProcess.destroy(true);
            }
        }
    }

    public void send(String stdin) {
        if (this.maybeRunningProcess.isPresent()) {
            final NuProcess runningProcess = this.maybeRunningProcess.get();

            final ByteBuffer buffer = ByteBuffer.wrap((stdin + "\n").getBytes());

            runningProcess.writeStdin(buffer);
        }
    }
}
