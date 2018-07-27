package swordandshield.models;

import org.junit.Before;
import org.junit.Test;
import swordandshield.controllers.InputController;
import swordandshield.controllers.parsingController.Parser;
import swordandshield.controllers.parsingController.nodes.*;

import java.util.Map;

import static junit.framework.TestCase.*;

/**
 * Testing method doesn't utilise the game method.
 * However due to the fact the program was designed
 * with MVC design pattern, I can pass input into my
 * controller to affect and query the state of my model
 * without having to display the view.
 */
public class Tests {

    Round r;
    InputController ic;

    @Before
    public void setup() {
        r = new Round(Round.STATE.TESTING);
        ic = r.inputController;
    }

    @Test
    public void testNothingOnBoard() {
        assertEquals(0, r.currPlayer.getPiecesOnBoard().size());
        r.changePlayer();
        assertEquals(0, r.currPlayer.getPiecesOnBoard().size());
    }

    @Test
    public void checkPassWorks() {
        assertEquals(Player.ColorIdentity.YELLOW, r.currPlayer.token);
        r.changePlayer();
        assertEquals(Player.ColorIdentity.GREEN, r.currPlayer.token);
        r.changePlayer();
        assertEquals(Player.ColorIdentity.YELLOW, r.currPlayer.token);
    }

    /**
     * Tests whether we can create a piece when there is already a piece at the spawnpoint
     * Expected value : false - cannot create a piece
     */
    @Test
    public void invalidCreate_1() {
        ic.testCommandsToExecute.add(new CreateNode("B", 0));

        ic.runTestCommands(r);
        assertNotNull(r.board.getPieceAtPos(r.currPlayer.spawnPoint));

        assertFalse(r.userIOView.outputPlayerChoices(r).contains(Parser.create));
    }

    /**
     * Tests whether we can create a piece when there is already a piece at the spawnpoint.
     * Checks if we can do this for both players
     * Expected value : false - cannot create a piece
     */
    @Test
    public void invalidCreate_2() {
        ic.testCommandsToExecute.add(new CreateNode("B", 0));
        ic.runTestCommands(r);
        assertNotNull(r.board.getPieceAtPos(r.currPlayer.spawnPoint));
        assertFalse(r.userIOView.outputPlayerChoices(r).contains(Parser.create));

        r.changePlayer();
        ic.testCommandsToExecute.add(new CreateNode("B", 0));
        ic.runTestCommands(r);
        assertNotNull(r.board.getPieceAtPos(r.currPlayer.spawnPoint));
        assertFalse(r.userIOView.outputPlayerChoices(r).contains(Parser.create));
    }

    /**
     * On the very first turn of the game, the player should only be able to create or
     * pass. Shouldn't be able to undo.
     */
    @Test
    public void checkCreateNoUndo() {
        assertTrue(r.currPlayer.isStartOfTurn());
        assertTrue(r.currPlayer.isFirstTurn());
        assertFalse(r.userIOView.outputPlayerChoices(r).contains(Parser.undo));
        assertTrue(r.userIOView.outputPlayerChoices(r).contains(Parser.create));
        assertTrue(r.userIOView.outputPlayerChoices(r).contains(Parser.pass));
    }

    /**
     * Checks basic move of a piece in up direction
     */
    @Test
    public void checkMoveDir_1() {
        ic.testCommandsToExecute.add(new CreateNode("B", 0));
        ic.testCommandsToExecute.add(new MoveNode("B", "up"));
        ic.runTestCommands(r);

        assertTrue(r.board.getPieceAtOffset(r.currPlayer.spawnPoint, "up", 1) != null);
        assertEquals("B", r.board.getPieceAtOffset(r.currPlayer.spawnPoint, "up", 1).getName());
    }

    /**
     * Checks basic move of a piece in down direction
     */
    @Test
    public void checkMoveDir_2() {
        ic.testCommandsToExecute.add(new CreateNode("B", 0));
        ic.testCommandsToExecute.add(new MoveNode("B", "down"));
        ic.runTestCommands(r);

        assertTrue(r.board.getPieceAtOffset(r.currPlayer.spawnPoint, "down", 1) != null);
        assertEquals("B", r.board.getPieceAtOffset(r.currPlayer.spawnPoint, "down", 1).getName());
    }

    /**
     * Checks basic move of a piece in left direction
     */
    @Test
    public void checkMoveDir_3() {
        ic.testCommandsToExecute.add(new CreateNode("B", 0));
        ic.testCommandsToExecute.add(new MoveNode("B", "left"));
        ic.runTestCommands(r);

        assertTrue(r.board.getPieceAtOffset(r.currPlayer.spawnPoint, "left", 1) != null);
        assertEquals("B", r.board.getPieceAtOffset(r.currPlayer.spawnPoint, "left", 1).getName());
    }

    /**
     * Checks basic move of a piece in right direction
     */
    @Test
    public void checkMoveDir_4() {
        ic.testCommandsToExecute.add(new CreateNode("B", 0));
        ic.testCommandsToExecute.add(new MoveNode("B", "right"));
        ic.runTestCommands(r);

        assertTrue(r.board.getPieceAtOffset(r.currPlayer.spawnPoint, "right", 1) != null);
        assertEquals("B", r.board.getPieceAtOffset(r.currPlayer.spawnPoint, "right", 1).getName());
    }

    /**
     * Checks if moving a piece left with a piece on its left moves them correctly.
     * Should work for up/down/right if it works for left
     */
    @Test
    public void checkMultipleMove() {
        ic.testCommandsToExecute.add(new CreateNode("B", 0));
        ic.testCommandsToExecute.add(new MoveNode("B", "left"));
        ic.runTestCommands(r);

        r.changePlayer();
        r.changePlayer();

        ic.testCommandsToExecute.add(new CreateNode("C", 0));
        ic.testCommandsToExecute.add(new MoveNode("C", "left"));
        ic.runTestCommands(r);

        assertNotNull(r.board.getPieceAtOffset(r.currPlayer.spawnPoint, "left", 2));
        assertNotNull(r.board.getPieceAtOffset(r.currPlayer.spawnPoint, "left", 1));
        assertNull(r.board.getPieceAtPos(r.currPlayer.spawnPoint));

        Piece first = r.currPlayer.getBoardPieceByName("B");
        Piece second = r.currPlayer.getBoardPieceByName("C");
        assertEquals(new Coord(5, 7), first.getPos());
        assertEquals(new Coord(6, 7), second.getPos());
    }

    /**
     * Creates and Moves a piece to the left. Checks if the previous
     * position on the board was deleted and new position contains
     * the moved piece. Checks also if the piece's position has
     * been updated correctly
     */
    @Test
    public void checkCreateAndMovePiece() {
        ic.testCommandsToExecute.add(new CreateNode("B", 0));
        ic.testCommandsToExecute.add(new MoveNode("B", "left"));
        ic.runTestCommands(r);

        assertTrue(r.board.getPieceAtPos(r.currPlayer.spawnPoint) == null);
        assertTrue(r.board.getPieceAtOffset(r.currPlayer.spawnPoint, "left", 1) != null);
        assertEquals(r.currPlayer.getBoardPieceByName("B").getPos(),
                    (new Coord(r.currPlayer.spawnPoint.getX() - 1, r.currPlayer.spawnPoint.getY())));
    }

    /**
     * Checks if rotate works when creating a node with no rotation specified and then
     * rotating that piece by 90 as a separate command. Uses north and west to check against.
     * North and West must be different tokens (Sword/Shield or Sword/Nothing etc)
     */
    @Test
    public void checkRotateFromCreate() {
        Map<Piece.Direction, Piece.Abilities> layoutBefore = r.currPlayer.getAvailablePieceByName("D").layout;
        ic.testCommandsToExecute.add(new CreateNode("D", 0));
        ic.testCommandsToExecute.add(new RotateNode("D", 90));
        ic.runTestCommands(r);

        Map<Piece.Direction, Piece.Abilities> layoutAfter = r.currPlayer.getBoardPieceByName("D").layout;

        assertEquals(layoutBefore.get(Piece.Direction.WEST), layoutAfter.get(Piece.Direction.NORTH));
    }

    /**
     * Checks if rotate works when creating a node with a rotation specified and then
     * rotating that piece by 360 as a separate command. Uses north and west to check against.
     * North and West must be different tokens (Sword/Shield or Sword/Nothing etc)
     */
    @Test
    public void checkCreateThenRotate_1() {
        Map<Piece.Direction, Piece.Abilities> layoutBefore = r.currPlayer.getAvailablePieceByName("D").layout;
        ic.testCommandsToExecute.add(new CreateNode("D", 90));
        ic.testCommandsToExecute.add(new RotateNode("D", 360));
        ic.runTestCommands(r);

        Map<Piece.Direction, Piece.Abilities> layoutAfter = r.currPlayer.getBoardPieceByName("D").layout;

        assertEquals(layoutBefore.get(Piece.Direction.WEST), layoutAfter.get(Piece.Direction.NORTH));
    }

    /**
     * Checks help command doesn't to the next player
     */
    @Test
    public void checkHelpActionNoAction() {
        Player p = r.currPlayer;
        ic.testCommandsToExecute.add(new HelpNode());
        ic.runTestCommands(r);
        assertEquals(p, r.currPlayer);
    }

    /**
     * Checks properties of piece when it dies. If a piece dies of it's own accord (not being pushed)
     * This will pass if the position gets set to -1 -1, it is in the cemetery and no long on the board
     * and the previous position it was at before it died is null - i.e deleted.
     */
    @Test
    public void checkDeathProperties() {
        ic.testCommandsToExecute.add(new CreateNode("B", 0));
        ic.testCommandsToExecute.add(new MoveNode("B", "right"));
        ic.runTestCommands(r);

        r.changePlayer(); // End of turn, pass
        r.changePlayer(); // Swap back to original player

        ic.testCommandsToExecute.add(new MoveNode("B", "right"));
        ic.runTestCommands(r);

        r.changePlayer();
        r.changePlayer();

        Coord posBeforeDeath = r.currPlayer.getBoardPieceByName("B").getPos();
        ic.testCommandsToExecute.add(new MoveNode("B", "right")); // Should put it outside the board
        ic.runTestCommands(r);

        // Piece should be off the board at this point
        assertNotNull(r.currPlayer.getCemeteryPieceByName("B")); // Is in cemetery
        assertNull(r.board.getPieceAtPos(posBeforeDeath));

        Piece pieceDead = r.currPlayer.getCemeteryPieceByName("B");
        System.out.println("Pos after death: " + pieceDead.getPos().toString());
        assertTrue(pieceDead.getPos().equals(new Coord(-1, -1)));
        System.out.println(pieceDead.beenAlteredThisTurn());
        assertTrue(pieceDead.beenAlteredThisTurn());
    }

    /**
     * Checks if the only piece on the board dies, it's the end of the turn
     * we cannot now create or do anything other than undo or pass
     */
    @Test
    public void testOnlyPieceDies_1() {
        ic.testCommandsToExecute.add(new CreateNode("B", 0));
        ic.testCommandsToExecute.add(new MoveNode("B", "right"));
        ic.runTestCommands(r);

        r.changePlayer(); // End of turn, pass
        r.changePlayer(); // Swap back to original player

        ic.testCommandsToExecute.add(new MoveNode("B", "right"));
        ic.runTestCommands(r);

        r.changePlayer();
        r.changePlayer();

        ic.testCommandsToExecute.add(new MoveNode("B", "right")); // Should put it outside the board
        ic.runTestCommands(r);

        assertNotNull(r.currPlayer.getCemeteryPieceByName("B")); // Is in cemetery
        assertTrue(r.currPlayer.isEndOfTurn());
        assertTrue(!r.currPlayer.isStartOfTurn()); // Was having a bug here
    }

    /**
     * There was a bug where if a piece died and it was the only piece on the board it
     * gave the correct option 'pass or undo' - no more turns. However the next turn of that
     * player it was still saying that it was the end of the turn and there were no pieces
     * left to play. The fix was that to set pieceDestroyed to false at end of turn. It wasn't
     * recognising it being the start of the turn as a result.
     */
    @Test
    public void testOnlyPieceDies_2() {
        testOnlyPieceDies_1();

        r.changePlayer();
        r.changePlayer();

        assertTrue(r.currPlayer.isStartOfTurn());
    }

    /**
     * Checks basic undo functionality
     */
    @Test
    public void undoTest_1() {
        ic.testCommandsToExecute.add(new CreateNode("B", 0));
        ic.testCommandsToExecute.add(new UndoNode());
        ic.runTestCommands(r);

        assertFalse(r.board.isPieceAtPos(r.currPlayer.spawnPoint));
    }

    /**
     * Checks basic undo functionality with longer movements
     * and also checks if we cannot undo further than the start of our turn
     */
    @Test
    public void undoTest_2() {
        ic.testCommandsToExecute.add(new CreateNode("B", 0));
        ic.testCommandsToExecute.add(new MoveNode("B", "left"));
        ic.testCommandsToExecute.add(new UndoNode());
        ic.testCommandsToExecute.add(new UndoNode());
        ic.runTestCommands(r);

        assertFalse(r.board.isPieceAtPos(r.currPlayer.spawnPoint));
        assertTrue(r.currPlayer.isStartOfTurn());
        assertFalse(r.userIOView.outputPlayerChoices(r).contains(Parser.undo));
    }

    @Test
    public void undoTest_3() {
        ic.testCommandsToExecute.add(new CreateNode("B", 0));
        ic.testCommandsToExecute.add(new MoveNode("B", "right"));
        ic.runTestCommands(r);

        r.changePlayer();
        r.changePlayer();

        ic.testCommandsToExecute.add(new MoveNode("B", "right"));
        ic.runTestCommands(r);

        r.changePlayer();
        r.changePlayer();

        ic.testCommandsToExecute.add(new MoveNode("B", "right")); // Should be dead
        ic.testCommandsToExecute.add(new UndoNode());
        ic.runTestCommands(r);

        assertTrue(r.board.isPieceAtOffset(r.currPlayer.spawnPoint, "right", 2));
    }
}