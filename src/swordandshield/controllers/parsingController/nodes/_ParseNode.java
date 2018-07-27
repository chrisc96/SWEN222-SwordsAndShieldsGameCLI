package swordandshield.controllers.parsingController.nodes;

import swordandshield.models.Round;

public interface _ParseNode {

    /**
     * Execute will execute the relevant game methods
     * for the types of nodes, create node will create a node, etc
     *
     * @param round
     */
    @SuppressWarnings("JavaDoc")
    void execute(Round round);
}