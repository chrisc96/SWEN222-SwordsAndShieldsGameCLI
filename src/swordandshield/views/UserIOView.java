package swordandshield.views;

import swordandshield.models.Player;
import swordandshield.models.Round;
import swordandshield.controllers.parsingController.Parser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Class for outputting user choices in the current state of the Board Model
 * and Round state
 */
public class UserIOView implements Serializable {

    /**
     * Displays the text below the board displaying the
     * current players information.
     * @param player current player
     */
    public void outputCurrPlayerText(Player player) {
        System.out.println( player.name + ": " + player.token.toString().substring(0,1).toUpperCase() +
                player.token.toString().toLowerCase().substring(1, player.token.toString().length()) +
                "'s Turn - ");
    }

    /**
     * Displays info about who lost or won the game
     * @param round
     */
    public void gameOverText(Round round) {
        System.out.println("Game Over!\n");
        if (round.currPlayer.lost) {
            System.out.println(round.currPlayer.name + " lost.");
        }
        else {
            round.changePlayer();
            System.out.println(round.currPlayer.name + " lost.");
        }
        System.out.println("\n");
    }

    /**
     * Given a round, we ascertain given the state of the player's turn,
     * what the player can do. if he can move, if can rotate. If he cannot
     * create pieces etc.
     * @param round the round object to determine the player choices
     * @return List of actions that can be executed by the user
     */
    public List<Pattern> outputPlayerChoices(Round round) {
        List<Pattern> choices = new ArrayList<>();

        choices.add(Parser.pass); // Can always pass
        if (round.currPlayer.isStartOfTurn()) {
            if (round.currPlayer.isFirstTurn()) {
                choices.add(Parser.create);
                System.out.println("Your options: Create or Pass. If you need help with syntax, type help");
                return choices;
            }
            // If it gets to here, there must be something on the board. I.E we created.
            if (!round.board.isPieceAtPos(round.currPlayer.spawnPoint)) {
                System.out.println("Your options: Create, Pass, Move, Rotate or Undo. If you need help with syntax, type help");
                choices.add(Parser.rotate);
                choices.add(Parser.move);
                choices.add(Parser.create);
                choices.add(Parser.undo);
            }
            else {
                System.out.println("Your options: Rotate, Move, Pass or Undo. If you need help with syntax, type help");
                choices.add(Parser.rotate);
                choices.add(Parser.move);
                choices.add(Parser.undo);
            }
        }
        else if (round.currPlayer.isEndOfTurn()) {
            System.out.println("You do not have any moves left in your turn. Undo or Pass to the next player");
            choices.add(Parser.undo);
        }
        // If something is moved/rotated or a piece is created, it physically cannot be the start on the turn.
        else {
            System.out.println("Your options: Rotate, Move, Undo or Pass. If you need help with syntax, type help");
            choices.add(Parser.pass);
            choices.add(Parser.rotate);
            choices.add(Parser.move);
            choices.add(Parser.undo);
        }
        return choices;
    }

    /**
     * When 'help' is typed, this is displayed.
     */
    public void outputHelpInfo() {
        System.out.println( "Welcome to the help functionality. The syntax is quite simple.\n" +
                "If you wish to create a piece at your start position:\n" +
                "\t - create [A, B, C etc] <0/90/180/270>\n" +
                "If you wish to rotate a piece:\n" +
                "\t - rotate [A, B, C etc] <0/90/180/270>\n" +
                "If you wish to movePiece a piece:\n" +
                "\t - movePiece [A, B, C] <up/right/left/down>\n" +
                "If you wish to undo your last move: (NOTE: Can only undo to start of your turn):\n" +
                "\t - undo\n" +
                "If you wish to end your turn (when you are allowed to):\n" +
                "\t - pass\n");
    }
}
