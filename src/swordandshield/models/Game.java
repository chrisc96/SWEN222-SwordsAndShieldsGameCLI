package swordandshield.models;

import java.io.*;

public class Game {

    public Game() {
        new Round(Round.STATE.RUNNING);
    }

    public static void main(String[] args) {
        new Game();
    }
}
