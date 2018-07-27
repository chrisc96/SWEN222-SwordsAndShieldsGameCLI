package swordandshield.controllers.parsingController.nodes;

import swordandshield.models.Piece;
import swordandshield.models.Round;

import java.io.Serializable;

public class MoveNode implements _ParseNode, Serializable {

    private String pieceName;
    private String dir;

    public MoveNode(String pieceName, String dir) {
        this.pieceName = pieceName;
        this.dir = dir;
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
        int adjacentLength = p.detAdjacentPieceLength(round, p.getPos(), dir, 0);
        if (p.beenAlteredThisTurn()) {
            System.out.println("This piece has already been moved/rotated this turn");
            return;
        }
        round.board.round.moveAdjacentPieces(adjacentLength, p.getPos(), dir, round.board);
        p.setAlteredThisTurn(true);
    }
}