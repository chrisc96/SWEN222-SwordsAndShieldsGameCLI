package swordandshield.controllers.parsingController.nodes;

import swordandshield.models.Piece;
import swordandshield.models.Round;

import java.io.Serializable;

public class RotateNode implements _ParseNode, Serializable {

    private final String pieceName;
    private final int numRots;

    public RotateNode(String pieceName, int degrees) {
        this.pieceName = pieceName;
        this.numRots = num90Rotations(degrees);
    }

    /**
     * Execute will execute the relevant game methods
     * for the types of nodes, create node will create a node, etc
     * @param round
     */
    @SuppressWarnings("JavaDoc")
    @Override
    public void execute(Round round) {
        round.prevMoves.push(round.deepClone());
        Piece p = round.currPlayer.getBoardPieceByName(pieceName);
        if (p == null) return;
        p.rotatePieceCW(numRots);
        p.setAlteredThisTurn(true);
    }

    /**
     * Returns the number of 90 degree rotations a multiple
     * of 90 represents
     * @param degrees 0/90/180/270
     * @return 0, 1, 2, 3
     */
    public static int num90Rotations(int degrees) {
        return degrees/90;
    }
}