package swordandshield.models;

import java.io.Serializable;
import java.util.*;

/**
 * Represents a unique player of Sword and Shields
 */
public class Player implements Serializable {

    public final String name;
    public final ColorIdentity token;
    public final Coord spawnPoint;
    public final Coord homeFace;

    public boolean lost = false;

    public boolean pieceCreated;
    private boolean pieceDestroyed;

    public enum ColorIdentity {
        YELLOW,
        GREEN
    }

    public final List<Piece> piecesCemetery = new ArrayList<>();
    public List<Piece> piecesAvailable = new ArrayList<>();
    public final List<Piece> piecesOnBoard = new ArrayList<>();

    public Player(String name, ColorIdentity token, Coord spawnPoint, Coord homeFace) {
        this.name = name; // Either P1 or P2
        this.token = token;
        this.spawnPoint = spawnPoint;
        this.homeFace = homeFace;
    }

    /**
     * Given a piece, it will move this from the player's pieces that are
     * on the board to the players cemetery
     * @param piece
     */
    @SuppressWarnings("JavaDoc")
    public void moveToCemetery(Piece piece) {
        setPieceDestroyed(true);
        getPiecesOnBoard().remove(piece);
        piece.setPos(-1, -1);
        getPiecesCemetery().add(piece);
    }


    /**
     * Gets a piece by it's name (A-Z) in the available
     * group. I.E not on the board or in the cemetery
     * @param name A-Z letter of the piece to find
     * @return Piece or null dependending if piece was found
     */
    public Piece getAvailablePieceByName(String name) {
        for (Piece p: piecesAvailable) {
            if (p.getName().equalsIgnoreCase(name)) {
                return p;
            }
        }
        System.out.println("No piece available with that name");
        return null;
    }

    /**
     * Gets a piece by it's name (A-Z) on the board pieces.
     * I.E not in the available pieces or in the cemetery.
     * @param name A-Z letter of the piece to find
     * @return Piece or null dependending if piece was found
     */
    public Piece getBoardPieceByName(String name) {
        for (Piece p: piecesOnBoard) {
            if (p.getName().equalsIgnoreCase(name)) {
                return p;
            }
        }
        System.out.println("No piece on board with that name");
        return null;
    }

    /**
     * Gets a piece by it's name (A-Z) on the board pieces.
     * I.E not in the available pieces or in the cemetery.
     * @param name A-Z letter of the piece to find
     * @return Piece or null dependending if piece was found
     */
    public Piece getCemeteryPieceByName(String name) {
        for (Piece p: piecesCemetery) {
            if (p.getName().equalsIgnoreCase(name)) {
                return p;
            }
        }
        System.out.println("No piece in cemetery with that name");
        return null;
    }

    /**
     * Returns the number of alterations to pieces this player
     * can make before his turn is over. Alterations to pieces
     * are moves and rotations. Creations do not alter pieces.
     * @return alterations left
     */
    int numMovesInTurnLeft() {
        int count = 0;
        for (Piece p : getPiecesOnBoard()) {
            if (!p.beenAlteredThisTurn()) {
                count++;
            }
        }
        return count;
    }

    /**
     * If there are no moves left in the turn it must be the end of the turn
     * @return
     */
    @SuppressWarnings("JavaDoc")
    public boolean isEndOfTurn() {
        return numMovesInTurnLeft() == 0;
    }

    /**
     * If some piece is moved/rotated or a piece is created, it physically cannot
     * be the start on the turn.
     * @return
     */
    @SuppressWarnings("JavaDoc")
    public boolean isStartOfTurn() {
        return numMovesInTurnLeft() == piecesOnBoard.size() && !pieceCreated && !pieceDestroyed;
    }

    /**
     * If there's nothing on the board then it must be our very first turn
     * @return
     */
    @SuppressWarnings("JavaDoc")
    public boolean isFirstTurn() {
        return piecesOnBoard.size() == 0;
    }

    /**
     * Once the turn is finished, we set each piece on the board
     * back to not being altered so when the player plays again,
     * he can play the correct amount of pieces
     */
    public void reset() {
        for (Piece p : piecesOnBoard) {
            p.setAlteredThisTurn(false);
        }
        pieceCreated = false;
        pieceDestroyed = false;
    }

    public List<Piece> getPiecesCemetery() {return piecesCemetery;}
    public List<Piece> getPiecesAvailable() {
        return piecesAvailable;
    }
    public List<Piece> getPiecesOnBoard() {
        return piecesOnBoard;
    }
    public void setPieceDestroyed(boolean pieceDestroyed) {
        this.pieceDestroyed = pieceDestroyed;
    }
}