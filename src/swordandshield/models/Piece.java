package swordandshield.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a piece that can be located at any position on the board
 */
public class Piece implements Serializable{

    Player player;

    // Set to true once a turn for each player if it gets moved, rotated etc
    private boolean beenAltered = false;

    private String name;
    private Coord pos;

    Map<Direction, Abilities> layout = new HashMap<>();

    public enum Direction {
        NORTH,
        EAST,
        SOUTH,
        WEST
    }

    public enum Abilities {
        SWORD,
        SHIELD,
        NOTHING
    }

    public Piece(Player player, Character name, Map<Direction, Abilities> layout) {
        this.player = player;
        this.name = name.toString().toUpperCase();
        this.layout = layout;
    }

    /**
     * Handles the shifting of a piece around the board.
     * It destroys the piece by setting the position on the grid
     * equal to null.
     * It then sets it's position to 1 square offset away from the
     * previous position in the direction specified by the parameter
     * @param dir direction of offset move. move(east) moves it one
     *            square east
     */
    public void move(Round round, String dir) {
        round.board.destroy(pos);
        if (dir.equalsIgnoreCase("up")) addToPlayersBoardPieces(pos.getX(), pos.getY() - 1);
        else if (dir.equalsIgnoreCase("down")) addToPlayersBoardPieces(pos.getX(), pos.getY() + 1);
        else if (dir.equalsIgnoreCase("left")) addToPlayersBoardPieces(pos.getX() - 1, pos.getY());
        else if (dir.equalsIgnoreCase("right")) addToPlayersBoardPieces(pos.getX() + 1, pos.getY());
        round.board.setPieceToPos(this, getPos());
    }

    /**
     * Rotates a piece 90 degrees every time for the parameter times
     * @param times number of times to rotate by 90 degrees
     */
    public void rotatePieceCW(int times) {
        for (int i = 0; i < times; i++) {
            rotatePieceCW();
        }
    }

    /**
     * Changes piece layout so that placement of swords and shields
     * are shifted clockwise around the piece
     */
    private void rotatePieceCW() {
        Map<Direction, Abilities> newLayout = new HashMap<>();
        newLayout.put(Direction.NORTH, layout.get(Direction.WEST));
        newLayout.put(Direction.EAST, layout.get(Direction.NORTH));
        newLayout.put(Direction.SOUTH, layout.get(Direction.EAST));
        newLayout.put(Direction.WEST, layout.get(Direction.SOUTH));
        layout = newLayout;
    }

    /**
     * Used in reactions
     * @param currDir
     * @return
     */
    private String getOppositeDirection(String currDir) {
        switch (currDir) {
            case "up":
                return "down";
            case "down":
                return "up";
            case "left":
                return "right";
            case "right":
                return "left";
            default:
                return "";
        }
    }

    /**
     * Given a piece, a direction to look and its current position it
     * recursively finds how many pieces are adjacent to it in that
     * direction. If there is nothing to the east when dir = east, then
     * it would return 0.
     * @param round round reference
     * @param currPos position of piece we're looking at (changes every
     *                recursive call by 1 in the direction of dir)
     * @param dir direction to find adjacent pieces
     * @param num the number of adjacent pieces, this is returned
     * @return  We return num (see parameter). 0 is returned if there is
     *          no pieces next to the piece this was called on
     */
    public int detAdjacentPieceLength(Round round, Coord currPos, String dir, int num) {
        if (!round.board.isPieceAtOffset(pos, dir, num + 1)) return num;
        else return detAdjacentPieceLength(round, round.board.round.determineOffset(currPos, dir, 1), dir, ++num);
    }

    /**
     * Adds this Piece to player's list of pieces on board
     * @param x
     * @param y
     */
    @SuppressWarnings("JavaDoc")
    private void addToPlayersBoardPieces(int x, int y) {
        this.pos = new Coord(x, y);
        player.piecesOnBoard.add(this);
    }
    public void addToPlayersBoardPieces(Coord pos) {
        addToPlayersBoardPieces(pos.getX(), pos.getY());
    }


    // Helper / Accessor methods
    public Coord getPos() {
        return pos;
    }


    public void setPos(int x, int y) {
        pos = new Coord(x, y);
    }
    public void setPos(Coord pos) {
        setPos(pos.getX(), pos.getY());
    }

    public boolean beenAlteredThisTurn() {
        return beenAltered;
    }

    public void setAlteredThisTurn(boolean b) {
        beenAltered = b;
    }

    public String getName() {
        return name;
    }


    // Drawing methods - Used by BoardView
    public void drawTopRow() {
        // Top Row
        if (layout.get(Direction.NORTH) ==  Abilities.SWORD) {System.out.printf("   |   ");}
        else if (layout.get(Direction.NORTH) == Abilities.SHIELD) {System.out.printf("   #   ");}
        else if (layout.get(Direction.NORTH) ==  Abilities.NOTHING) {System.out.printf("       ");}
    }

    public void drawMiddleRow() {
        // Middle Row
        if (layout.get(Direction.WEST) ==  Abilities.SWORD) {System.out.print(" -");}
        else if (layout.get(Direction.WEST) == Abilities.SHIELD) {System.out.print(" #");}
        else if (layout.get(Direction.WEST) ==  Abilities.NOTHING) {System.out.print("  ");}

        System.out.print("" + player.name + name);

        if (layout.get(Direction.EAST) ==  Abilities.SWORD) {System.out.print("- ");}
        else if (layout.get(Direction.EAST) == Abilities.SHIELD) {System.out.print("# ");}
        else if (layout.get(Direction.EAST) ==  Abilities.NOTHING) {System.out.print("  ");}
    }

    public void drawBottomRow() {
        if (layout.get(Direction.SOUTH) ==  Abilities.SWORD) {System.out.print("   |   ");}
        else if (layout.get(Direction.SOUTH) == Abilities.SHIELD) {System.out.print("   #   ");}
        else if (layout.get(Direction.SOUTH) ==  Abilities.NOTHING) {System.out.print("       ");}
    }
}