package main.java.breakoutgame.GameHelpers;

import main.java.breakoutgame.GameObjects.Map;
import java.security.InvalidParameterException;

class Edge {
    private enum EdgeType {
        HORIZONTAL, VERTICAL, DIAGONAL;
    }
    private Vector2D start;
    private Vector2D end;

    private EdgeType type;
    private double a;
    private double b;

    private double xLeftBound;
    private double xRightBound;
    private double yTopBound;
    private double yBotBound;

    public Edge(Vector2D start, Vector2D end) {
        this.start = new Vector2D(start.getX(), Map.MAP_HEIGHT - start.getY());
        this.end = new Vector2D(end.getX(), Map.MAP_HEIGHT - end.getY());

        if (start.equals(end)) {
            throw new InvalidParameterException("Making edge out of two equal vectors!");
        }

        type = type();

        if (type == EdgeType.DIAGONAL) {
            a = (this.end.getY() - this.start.getY()) / (this.end.getX() - this.start.getX());
            b = this.start.getY() - a * this.start.getX();
        }

        if (this.start.getX() < this.end.getX()) {
            xLeftBound = this.start.getX();
            xRightBound = this.end.getX();
        }
        else {
            xLeftBound = this.end.getX();
            xRightBound = this.start.getX();
        }

        if (this.start.getY() < this.end.getY()) {
            yBotBound = this.start.getY();
            yTopBound = this.end.getY();
        }
        else {            
            yTopBound = this.end.getY();
            yBotBound = this.start.getY();
        }
    }

    public EdgeType type() {
        if (start.getX() - end.getX() == 0) {
            return EdgeType.VERTICAL;
        }
        else if (start.getY() - end.getY() == 0) {
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