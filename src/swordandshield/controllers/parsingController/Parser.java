package swordandshield.controllers.parsingController;

import swordandshield.models.Round;
import swordandshield.controllers.parsingController.nodes.*;

import java.io.Serializable;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

/**
 * Recursive descent parser that returns a ParseNode to the input controller
 * which is then executed. Uses a fixed grammar.
 */
public class Parser implements Serializable {

    private static Pattern letter = Pattern.compile("[A-Xa-x]", CASE_INSENSITIVE);
    private static Pattern rotation = Pattern.compile("0|90|180|270", CASE_INSENSITIVE);
    private static Pattern direction = Pattern.compile("up|down|left|right", CASE_INSENSITIVE);

    public static Pattern move = Pattern.compile("move", CASE_INSENSITIVE);
    public static Pattern create = Pattern.compile("create", CASE_INSENSITIVE);
    public static Pattern rotate = Pattern.compile("rotate", CASE_INSENSITIVE);
    public static Pattern pass = Pattern.compile("pass", CASE_INSENSITIVE);
    public static Pattern undo = Pattern.compile("undo", CASE_INSENSITIVE);
    private static Pattern help = Pattern.compile("help", CASE_INSENSITIVE);

    /**
     * Gets user input and sends this correct input to the controller.
     * Checks user input against regex patterns and allowed actions on
     * every move depending on the state of the round.
     * @param round
     * @return Returns a _ParseNode to the input controller
     */
    @SuppressWarnings("JavaDoc")
    public _ParseNode getInput(Round round) {
        Scanner buf = new Scanner(System.in);
        round.userIOView.outputCurrPlayerText(round.currPlayer);
        List<Pattern> allowedChoices = round.userIOView.outputPlayerChoices(round);
        return parseTurn(allowedChoices, buf);
    }

    /**
     * checks if user input matches any of the predefined grammar patterns.
     * It uses this to determine which command wants to be returned and
     * eventually executed.
     * @param allowedChoices List of actions that can be executed
     *                       based on the current status of the round
     * @param buf
     * @return returns ParseNode recursively back to getInput()
     */
    @SuppressWarnings("JavaDoc")
    private _ParseNode parseTurn(List<Pattern> allowedChoices, Scanner buf) {
        while (buf.hasNextLine()) {
            int count = 0;
            String[] line = buf.nextLine().split("\\s");
            if (matches(create, line[count])) return parseCreateNode(allowedChoices, line, ++count);
            else if (matches(move, line[count])) return parseMoveNode(allowedChoices, line, ++count);
            else if (matches(rotate, line[count])) return parseRotateNode(allowedChoices, line, ++count);
            else if (matches(pass, line[count])) return parsePassNode(allowedChoices);
            else if (matches(undo, line[count])) return parseUndoNode(allowedChoices);
            else if (matches(help, line[count])) return new HelpNode();
            else {
                System.out.println("Unknown Command. Please try again");
            }
        }
        return null;
    }

    /**
     * Grammar for terminal for 'create' user input.
     * @param allowedChoices List of actions that can be executed
     *                       based on the current status of the round
     * @param line The user input string, split into an array of Strings
     * @param count the index of the line String[] we want to look at
     * @return returns CreateNode (which is by subtyping a ParseNode) recursively back to getInput()
     */
    private CreateNode parseCreateNode(List<Pattern> allowedChoices, String[] line, int count) {
        if (!requireAction(allowedChoices, create)) return null;

        String pieceName = require(letter, "Correct piece letter not found. Please try again", line, count);
        if (pieceName.equalsIgnoreCase("")) return null;

        int rot = requireInt(rotation, line, ++count);
        if (rot == -1) return null;

        return new CreateNode(pieceName, rot);
    }

    /**
     * Grammar for terminal for 'move' user input.
     * @param allowedChoices List of actions that can be executed
     *                       based on the current status of the round
     * @param line The user input string, split into an array of Strings
     * @param count the index of the line String[] we want to look at
     * @return returns MoveNode (which is by subtyping a ParseNode) recursively back to getInput()
     */
    private MoveNode parseMoveNode(List<Pattern> allowedChoices, String[] line, int count) {
        if (!requireAction(allowedChoices, move)) return null;

        String pieceName = require(letter, "Correct piece letter not found. Please try again", line, count);
        if (pieceName.equalsIgnoreCase("")) return null;

        String dir = require(direction, "Correct direction not specified. Please try again", line, ++count);
        if (dir.equalsIgnoreCase("")) return null;

        return new MoveNode(pieceName, dir);
    }

    /**
     * Grammar for terminal for 'rotate' user input.
     * @param allowedChoices List of actions that can be executed
     *                       based on the current status of the round
     * @param line The user input string, split into an array of Strings
     * @param count the index of the line String[] we want to look at
     * @return returns RotateNode (which is by subtyping a ParseNode) recursively back to getInput()
     */
    private RotateNode parseRotateNode(List<Pattern> allowedChoices, String[] line, int count) {
        if (!requireAction(allowedChoices, rotate)) return null;

        String pieceName = require(letter, "Correct piece letter not found. Please try again", line, count);
        if (pieceName.equalsIgnoreCase("")) return null;

        int rot = requireInt(rotation, line, ++count);
        if (rot == -1) return null;

        return new RotateNode(pieceName, rot);
    }

    /**
     * Grammar for terminal for 'undo' user input.
     * @param allowedChoices List of actions that can be executed
     *                       based on the current status of the round
     * @return returns UndoNode (which is by subtyping a ParseNode) recursively back to getInput()
     */
    private UndoNode parseUndoNode(List<Pattern> allowedChoices) {
        if (!requireAction(allowedChoices, undo)) return null;

        return new UndoNode();
    }

    /**
     * Grammar for terminal for 'pass' user input.
     * @param allowedChoices List of actions that can be executed
     *                       based on the current status of the round
     * @return returns PassNode (which is by subtyping a ParseNode) recursively back to getInput()
     */
    private PassNode parsePassNode(List<Pattern> allowedChoices) {
        if (!requireAction(allowedChoices, pass)) return null;
        return new PassNode();
    }


    // Helper Methods
    /**
     * Returns a string that matches the String pattern, if it does match.
     * If it doesn't match, empty string is returned.
     *
     * @param pattern The pattern we want to match against
     * @param message error message to display if it goes wrong and doesn't match
     * @param line The user input string, split into an array of Strings
     * @param count the index of the line String[] we want to look at
     * @return empty string if it didn't match. Otherwise it returns the string entered by user
     */
    private String require(Pattern pattern, String message, String[] line, int count) {
        if (count < line.length) {
            if (line[count].matches(pattern.pattern())) {
                return line[count];
            }
        }
        System.out.println(message);
        return "";
    }

    /**
     * Returns an int that matches an int pattern, if it does match.
     * If it doesn't match, -1 is returned.
     *
     * @param pattern The pattern we want to match against
     * @param line The user input string, split into an array of Strings
     * @param count the index of the line String[] we want to look at
     * @return the int entered (if it matched the pattern), otherwise -1
     */
    private int requireInt(Pattern pattern, String[] line, int count) {
        if (count < line.length) {
            if (line[count].matches(pattern.pattern())) {
                return Integer.parseInt(line[count]);
            }
        }
        System.out.println("Valid number for rotation not entered. Please try again");
        return -1;
    }

    /**
     * Checks if the action the user wants to execute is possible.
     * I.E they typed create to create a piece but if they can't
     * create a piece at this piece, it returns false
     * @param actions List of actions allowed on this turn in the round
     * @param pat pattern the user wants to execute.
     * @return boolean. Returns false if the pattern is not in the List
     */
    private boolean requireAction(List<Pattern> actions, Pattern pat) {
        if (!actions.contains(pat)) {
            System.out.println("Cannot do this action. Please try again");
            return false;
        }
        return true;
    }

    /**
     * Returns a boolean if a pattern matches a string based upon that string.
     * The pattern should have the CASE_INSENSITIVE flag enabled. This method
     * is completely CASE_INSENSITIVE.
     * @param pattern to match
     * @param s to match
     * @return boolean if they match or not
     */
    private boolean matches(Pattern pattern, String s) {
        return (pattern.toString().equalsIgnoreCase(s));
    }
}