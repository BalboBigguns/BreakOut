package com.breakoutgame.GameHelpers;

import com.breakoutgame.GameObjects.Map;

import java.security.InvalidParameterException;

class Edge {
    protected enum EdgeType {
        HORIZONTAL, VERTICAL, DIAGONAL;
    }
    protected Vector2D start;
    protected Vector2D end;

    protected EdgeType type;
    private double a;
    private double b;

    private double xLeftBound;
    private double xRightBound;
    private double yTopBound;
    private double yBotBound;

    public Edge(Vector2D start, Vector2D end) {
        this.start = new Vector2D(start.x, Map.MAP_HEIGHT - start.y);
        this.end = new Vector2D(end.x, Map.MAP_HEIGHT - end.y);

        if (start.equals(end)) {
            throw new InvalidParameterException("Making edge out of two equal vectors!");
        }

        type = type();

        if (type == EdgeType.DIAGONAL) {
            a = (this.end.y - this.start.y) / (this.end.x - this.start.x);
            b = this.start.y - a * this.start.x;
        }

        if (this.start.x < this.end.x) {
            xLeftBound = this.start.x;
            xRightBound = this.end.x;
        }
        else {
            xLeftBound = this.end.x;
            xRightBound = this.start.x;
        }

        if (this.start.y < this.end.y) {
            yBotBound = this.start.y;
            yTopBound = this.end.y;
        }
        else {            
            yTopBound = this.end.y;
            yBotBound = this.start.y;
        }
    }

    public EdgeType type() {
        if (start.x - end.x == 0) {
            return EdgeType.VERTICAL;
        }
        else if (start.y - end.y == 0) {
            return EdgeType.HORIZONTAL;
        }
        else {
            return EdgeType.DIAGONAL;
        }
    }

    public Vector2D isCrossing(Edge edg) {
        if (edg.type == EdgeType.HORIZONTAL) {
            double crossingX = (edg.yTopBound - b) / a;

            if (edg.xLeftBound <= crossingX && crossingX <= edg.xRightBound) {
                return new Vector2D(crossingX, Map.MAP_HEIGHT - edg.yTopBound);
            }

            return null;
        }
        else if (edg.type == EdgeType.VERTICAL) {
            double crossingY = a * edg.xLeftBound + b;
            
            if (edg.yBotBound <= crossingY && crossingY <= edg.yTopBound){
                return new Vector2D(edg.xLeftBound, Map.MAP_HEIGHT - crossingY);
            }

            return null;
        }
        else {
            throw new InvalidParameterException("Checking 2 diagonal edges!");
        }
    }

    @Override
    public String toString() {
        return "Start: " + start + '\n' +
                "End: " + end + '\n' +
                "Type: " + type + '\n' +
                "A, b: " + a + '\t' + b + '\n' +
                "Bounds x: " + xLeftBound + '\t' + xRightBound + '\n' +
                "Bounds y: " + yBotBound + '\t' + yTopBound + '\n';
    }

}