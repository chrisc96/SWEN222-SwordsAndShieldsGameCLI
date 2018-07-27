package swordandshield.controllers.parsingController.nodes;

import swordandshield.models.Piece;
import swordandshield.models.Round;

import java.io.Serializable;

public class CreateNode implements _ParseNode, Serializable {

    private final int numRots;
    private final String name;

    public CreateNode(String name, int degrees) {
        this.name = name;
        this.numRots = RotateNode.num90Rotations(degrees);
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
        Piece p = round.currPlayer.getAvailablePieceByName(name);
        if (p == null) return;

        if (round.board.isPieceAtPos(round.currPlayer.spawnPoint)) {
            System.out.println("Piece already on spawn point. Cannot create here");
        }
        else {
            p.addToPlayersBoardPieces(round.currPlayer.spawnPoint);
            p.rotatePieceCW(numRots);
            round.board.putPieceOnGrid(p);
            round.currPlayer.pieceCreated = true;
        }
    }
}