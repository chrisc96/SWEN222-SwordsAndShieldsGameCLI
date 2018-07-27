package swordandshield.controllers.parsingController.nodes;

import swordandshield.models.Round;

import java.io.Serializable;

public class HelpNode implements _ParseNode, Serializable {

    /**
     * Execute will execute the relevant game methods
     * for the types of nodes, create node will create a node, etc
     * @param round
     */
    @SuppressWarnings("JavaDoc")
    @Override
    public void execute(Round round) {
        round.userIOView.outputHelpInfo();
    }
}
