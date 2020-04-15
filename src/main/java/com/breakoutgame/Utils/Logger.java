package com.breakoutgame.Utils;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.animation.Animation.Status;

import javafx.event.EventHandler;
import javafx.event.ActionEvent;

import java.util.ArrayList;

public class Logger {
    private ArrayList<Loggable> TrackedObjects = new ArrayList<Loggable>();

    interface TimeStamp {
        public void tick(int timeInterval);
        public String print();
    }

    TimeStamp timeStamp = new TimeStamp() {
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
            return String.format("T: %02d:%02d:%03d", minutes, seconds, millis);
        }
    };

    public Logger(Timeline globalTimeline, int rate) {
        Timeline loggerTimeline = new Timeline(new KeyFrame(Duration.millis(rate), new EventHandler<ActionEvent>() {

            boolean gameRunning = true;

            @Override
            public void handle(ActionEvent t) {
                if (globalTimeline.getStatus() == Status.PAUSED && gameRunning) {
                    gameRunning = false;
                    timeStamp.tick(rate);
                    printLogs();
                } else if (globalTimeline.getStatus() == Status.RUNNING) {
                    gameRunning = true;
                    timeStamp.tick(rate);
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

    public void printLogs() {
        System.out.println(timeStamp.print());
        
        for (Loggable obj : TrackedObjects) {
            System.out.println(obj.getClass().getSimpleName() + ":\n" + obj.log());
        }

    }
}