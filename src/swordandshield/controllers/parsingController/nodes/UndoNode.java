package swordandshield.controllers.parsingController.nodes;

import swordandshield.models.Round;

import java.io.Serializable;

public class UndoNode implements _ParseNode, Serializable {

    /**
     * Execute will execute the relevant game methods
     * for the types of nodes, create node will create a node, etc
     * @param round
     */
    @SuppressWarnings("JavaDoc")
    @Override
    public void execute(Round round) {
        if (round.currPlayer.isStartOfTurn()) {
            System.out.println("Cannot undo. This is the start of your turn.");
            return;
        }
        if (round.prevMoves.size() == 0) {
            System.out.println("Can't undo. Nothing left to undo.");
        }
        else {
            Round r = round.prevMoves.pop();
            round.setCloneFields(r);
        }
    }
}