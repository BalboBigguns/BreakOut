package main.java.breakoutgame.GameHelpers;

import java.lang.Math;

public class Vector2D {
    public double x;
    public double y;

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2D(Vector2D toCopy) {
        this.x = toCopy.x;
        this.y = toCopy.y;
    }

    public Vector2D add(Vector2D vec) {
       x += vec.x; 
       y += vec.y;
       return this;
    }

    public Vector2D add(double x, double y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public Vector2D sub(Vector2D vec) {
        x -= vec.x;
        y -= vec.y;
        return this;
    }

    public void mult(double m) {
        x *= m; 
        y *= m;
    }

    public double length() {
        return Math.sqrt(x * x + y * y);
    }

    public double distance(Vector2D vec) {
        return sub(vec).length();
    }

    public double dot(Vector2D vec) {
        return x * vec.x + y * vec.y;
    }
    public double angle(Vector2D vec) {
        return Math.acos(dot(vec) / (length() * vec.length()));
    }

    public Vector2D normalize() {
        x /= length();
        y /= length();
        return this;
    }

    public boolean equals(Vector2D vec) {
        return (x == vec.x) && (y == vec.y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}

