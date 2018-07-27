package swordandshield.models;

import java.io.Serializable;

/**
 * Coordinate class to represent positions on the grid
 */

public class Coord implements Serializable{
    private final int x;
    private final int y;

    public Coord(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        else if (this.getClass() != obj.getClass()) return false;
        else {
            Coord coord = (Coord) obj;
            return coord.x == this.x && coord.y == this.y;
        }
    }

    int getX() {
        return x;
    }

    int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Coord{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}