package swordandshield.controllers;

import swordandshield.models.Round;
import swordandshield.controllers.parsingController.nodes._ParseNode;
import swordandshield.controllers.parsingController.Parser;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Queue;

public class InputController implements Serializable {

    private Parser parser;
    public Queue<_ParseNode> testCommandsToExecute = new ArrayDeque<>();


    public InputController() {
        this.parser = new Parser();
    }

    /**
     * Prompts user for input and then uses parser to get that input
     * this input is returned in the form of a node that can be executed
     * @return
     * @param round
     */
    @SuppressWarnings("JavaDoc")
    public _ParseNode takeTurn(Round round) {
        return parser.getInput(round);
    }

    public void runTestCommands(Round round) {
        while (testCommandsToExecute.size() > 0) {
            testCommandsToExecute.poll().execute(round);
        }
    }

}
