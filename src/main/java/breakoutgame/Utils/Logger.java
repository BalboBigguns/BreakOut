package main.java.breakoutgame.Utils;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.animation.Animation.Status;

import javafx.event.EventHandler;
import javafx.event.ActionEvent;

import java.io.PrintStream;
import java.util.ArrayList;

public class Logger {
    public enum LogType {
        INFO, ERROR;
    }

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";

    private static final int LOG_RATE = 500;    // new log from traced objects each 500ms

    static private Logger GlobalInstance = null;
    static private ArrayList<Loggable> TrackedObjects = new ArrayList<Loggable>();
    static private PrintStream logOutput;


    interface TimeStamp {
        void tick(int timeInterval);
        String print();
    }

    static TimeStamp timeStamp = new TimeStamp() {
        int millis = 0;
        int seconds = 0;
        int minutes = 0;

        @Override
        public void tick(int timeInterval) {
            millis += timeInterval;

            int quotient = millis / 1000;
            if (quotient >= 1) {
                seconds += quotient;
                millis %= 1000;

                quotient = seconds / 60;
                if (quotient >= 1) {
                    minutes += quotient;
                    seconds %= 60;
                }
            }            
        }

        @Override
        public String print(){
            return String.format("#%02d:%02d:%03d", minutes, seconds, millis);
        }
    };

    static public void createGlobalInstance(Timeline globalTimeline) {
        GlobalInstance = new Logger(globalTimeline);
    }

    static public Logger getGlobalInstance() {
        if (GlobalInstance == null) {
            throw new NullPointerException();
        }
        else {
            return GlobalInstance;
        }
    }

    private Logger(Timeline globalTimeline) {

        logOutput = System.out;

        Timeline loggerTimeline = new Timeline(new KeyFrame(Duration.millis(LOG_RATE), new EventHandler<ActionEvent>() {
            boolean gameRunning = true;

            @Override
            public void handle(ActionEvent t) {
                if (globalTimeline.getStatus() == Status.PAUSED && gameRunning) {
                    gameRunning = false;
                    timeStamp.tick(LOG_RATE);
                    printLogs();
                } else if (globalTimeline.getStatus() == Status.RUNNING) {
                    gameRunning = true;
                    timeStamp.tick(LOG_RATE);
                    printLogs();
                }
            }
        }));

        loggerTimeline.setCycleCount(Timeline.INDEFINITE);
        loggerTimeline.play();
    }

    public void addObjectToTrack(Loggable obj) {
        TrackedObjects.add(obj);
    }

    public void printEvent(String msg, LogType type) {
        switch (type) {
            case INFO:
                printInfo(msg);
                break;
            case ERROR:
                printError(msg);
                break;
        }
    }

    private void printInfo(String msg) {
        logOutput.print(ANSI_GREEN + timeStamp.print() + "_INFO: " + ANSI_RESET);
        logOutput.println(msg);
    }

    private void printError(String msg) {
        logOutput.print(ANSI_RED + timeStamp.print() + "_ERROR: " + ANSI_RESET);
        logOutput.println(msg);
    }

    private void printLogs() {
        for (Loggable obj : TrackedObjects) {
            printInfo(obj.getClass().getSimpleName() + ":\n" + obj.log());
        }

    }
}