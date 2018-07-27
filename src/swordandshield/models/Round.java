package swordandshield.models;

import swordandshield.controllers.InputController;
import swordandshield.controllers.parsingController.nodes.PassNode;
import swordandshield.exceptions.PlayerException;
import swordandshield.controllers.parsingController.nodes._ParseNode;
import swordandshield.startup.DataLoader;
import swordandshield.views.BoardView;
import swordandshield.views.UserIOView;

import javax.swing.undo.CannotUndoException;
import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Represents the game of Sword and Shield. This is instantiated
 * every time a brand new game starts. It is the main model that updates
 * the boardView
 */

public class Round implements Serializable {

    public STATE state;

    public Stack<Round> prevMoves = new Stack<>();

    public Board board;
    private BoardView boardView;
    public UserIOView userIOView;
    public InputController inputController;

    public List<Player> players = new ArrayList<>();
    public Player currPlayer;

    /**
     * Determines the coordinate of an offset away from an original position coordinate
     *
     * @param origPos the original coordinate you want to calculate the offset from
     * @param dir what direction (up, down, left, right)
     * @param offset how far away you want the offset from the original position
     * @return Coordinate for offset from an original position
     */
    Coord determineOffset(Coord origPos, String dir, int offset) {
        Coord pos;
        if (dir.equalsIgnoreCase("up")) pos = new Coord(origPos.getX(), origPos.getY() - offset);
        else if (dir.equalsIgnoreCase("down")) pos = new Coord(origPos.getX(), origPos.getY() + offset);
        else if (dir.equalsIgnoreCase("left")) pos = new Coord(origPos.getX() - offset, origPos.getY());
        else if (dir.equalsIgnoreCase("right")) pos = new Coord(origPos.getX() + offset, origPos.getY());
        else {
            System.out.println("Direction not recognised");
            return null;
        }
        return pos;
    }

    /**
     * Given the number of adjacent pieces to the original, it shifts the furthest
     * piece away from the original piece by 1, and loops until it has moved itself
     * by one.
     *  @param adjacentLength number of pieces adjacent to start piece, can be 0
     * @param origPos start piece location
     * @param dir direction to move all pieces
     * @param board
     */
    public void moveAdjacentPieces(int adjacentLength, Coord origPos, String dir, Board board) {
        while (adjacentLength >= 0) {
            Piece p = board.getPieceAtOffset(origPos, dir, adjacentLength);
            p.move(this, dir);
            adjacentLength--;
        }
    }

    public void gameOver(Player p) {
        p.lost = true;
        state = STATE.GAMEOVER;
    }

    public enum STATE {
        RUNNING,
        GAMEOVER,
        TESTING
    }

    Round(STATE startState) {
        this.state = startState;
        setupGame();
        prevMoves.push(deepClone());

        redraw();
        run();
    }

    void setupGame() {
        board = new Board(this);
        boardView = new BoardView(this);
        userIOView = new UserIOView();
        inputController = new InputController();
        createPlayers();
    }

    /**
     * Creates the players, adds them to the list of players,
     * loads all their pieces and sets the current player
     */
    void createPlayers() {
        players.add(new Player("P1", Player.ColorIdentity.YELLOW, new Coord(7, 7), new Coord(8, 8)));
        players.add(new Player("P2", Player.ColorIdentity.GREEN, new Coord(2, 2), new Coord(1, 1)));
        for (Player p: players) {
            new DataLoader(p);
        }
        currPlayer = players.get(0);
    }

    /**
     * Main game loop. Keeps running if the state is Running
     * This draws the boardView and userIOView and also communicates
     * with the inputController to alter the boardView and alter gameState
     */
    void run() {
        while (state == STATE.RUNNING) {
            _ParseNode command;
            if ((command = inputController.takeTurn(this)) != null) {
                command.execute(this);
            }

            if (command instanceof PassNode) changePlayer();
            redraw();
        }
        if (state == STATE.GAMEOVER) {
            userIOView.gameOverText(this);
        }
    }

    void redraw() {
        boardView.draw();
    }

    /**
     * Returns the next player given the current player
     * @param currPlayer the current player
     * @return returns the next Player
     */
    Player getNextPlayer(Player currPlayer) {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i) == currPlayer) return players.get((i+1)%players.size());
        }
        throw new PlayerException("Couldn't find next player in game. You must be playing alone...");
    }

    /**
     * Changes the player to the next player
     */
    public void changePlayer() {
        currPlayer.reset();
        currPlayer = getNextPlayer(currPlayer);
    }

    /**
     * Returns a player with a specified col token, throws a exception
     * and breaks execution if a player with the color is not found.
     *
     * @param col Green/Yellow
     * @return Player with that color token
     */
    public Player getPlayerbyColor(Player.ColorIdentity col) {
        for (Player p: players) {
            if (p.token == col) return p;
        }
        throw new PlayerException("Couldn't find player with that colour identity");
    }



    /**
     * Given a Round that should have been deep cloned, this will assign the fields of the
     * current round to the fields of a previous state of the round. This is used for the
     * undo functionality.
     * @param previousRound
     */
    @SuppressWarnings("JavaDoc")
    public void setCloneFields(Round previousRound) {
        Class copy1 = previousRound.getClass();
        Class copy2 = this.getClass();

        Field[] fromFields = copy1.getDeclaredFields();
        Field[] toFields = copy2.getDeclaredFields();

        Object value;

        if (fromFields.length != toFields.length) throw new CannotUndoException();
        else {
            for (Field field : fromFields){
                Field field1;
                try {
                    field1 = copy2.getDeclaredField(field.getName());
                    value = field.get(previousRound);
                    field1.set(this,value);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Used to deep clone this round object, everything that implements serializable
     * will also be cloned. These clones are statically added to a stack of Rounds in Game and
     * popped in run() upon receival of a valid user input command.
     *
     * @return Round object with all pointers separate to current object
     */
    public Round deepClone() {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(outputStream);
            out.writeObject(this);

            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            ObjectInputStream in = new ObjectInputStream(inputStream);
            return (Round) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }
}