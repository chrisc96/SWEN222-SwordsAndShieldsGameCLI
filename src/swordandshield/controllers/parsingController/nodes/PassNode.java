package swordandshield.controllers.parsingController.nodes;

import swordandshield.models.Round;

import java.io.Serializable;

public class PassNode implements _ParseNode, Serializable {

    /**
     * Have to manually change playing here for a pass
     * @param round
     */
    @SuppressWarnings("JavaDoc")
    @Override
    public void execute(Round round) {}
}