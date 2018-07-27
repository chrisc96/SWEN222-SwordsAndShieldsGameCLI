package swordandshield.views;

import swordandshield.models.Coord;
import swordandshield.models.Piece;
import swordandshield.models.Player;
import swordandshield.models.Round;

import java.io.Serializable;

/**
 * A class to draw the board. It's gross, convoluted and a disgusting mess.
 * If someone makes a library to make this easy, do tell. This was living hell
 * to get right.
 */

public class BoardView implements Serializable {

    private Round round;

    public BoardView(Round b) {
        this.round = b;
    }

    /**
     * Draws the entire game. Best rendered on
     * 2560 x 1440 screens as it's quite tall.
     */
    public void draw() {
        System.out.println();

        drawAvailAndGridHeadings();
        drawAvailAndGridContent();

        System.out.println();

        drawCemeteryHeadings();
        drawCemeteryContent();

        System.out.println();
    }


    /**
     * Draws the titles for each compartment
     * P1/P2 Available - Pieces available to play
     * Swords and Shields - Game Board Heading
     */
    private void drawAvailAndGridHeadings() {
        System.out.printf("      " + round.players.get(0).name + " Available" +
                "                                          " + "Swords and Shield" +
                "                                     " + "P2 Available"
        );
        System.out.println();
    }

    /**
     * Grid coordinate separator.
     * I.E.
     *  + - - - + - - - +  < -- This is the separator
     *  |               |
     *  |  ROW CONTENT  |
     *  |               |
     *  + - - - + - - - +
     */
    private void drawAvailAndGridSeparator() {
        // Top Row
        for (int i = 0; i < 3; i++) {
            System.out.printf("+ - - - ");
        }
        System.out.printf("+");
        System.out.printf("   ");

        for (int i = 0; i < 10; i++) {
            System.out.printf("+ - - - ");
        }
        System.out.printf("+");
        System.out.printf("   ");

        for (int i = 0; i < 3; i++) {
            System.out.printf("+ - - - ");
        }
        System.out.printf("+");
        System.out.println();
    }

    private void drawAvailAndGridContent() {
        // Draw the 10 rows of content
        for (int i = 0; i < 10; i++) {
            drawAvailAndGridSeparator();
            drawAvailAndGridRow(3*i, i);
        }
        drawAvailAndGridSeparator();
    }

    /**
     * Grid coordinate separator.
     * I.E.
     *  + - - - + - - - +
     *  |               | <--
     *  |  ROW CONTENT  | <-- Draws these rows of content
     *  |               | <--
     *  + - - - + - - - +
     *
     * @param iteration start column of the current board row
     *                  i.e row = 0 thus col = 0
     *                      row = 1 thus col = 3
     *                      etc
     *  @param boardRow the current row being drawn
     */
    private void drawAvailAndGridRow(int iteration, int boardRow) {

        for (int drawRow = 1; drawRow <= 3; drawRow++) {
            // P1 Available
            int p1AvailableCounter = iteration;
            int boardCol = 1;

            System.out.printf("|");
            while (true) {
                if (p1AvailableCounter < round.players.get(0).piecesAvailable.size()) {
                    Piece p = round.players.get(0).piecesAvailable.get(p1AvailableCounter);
                    if (drawRow == 1) p.drawTopRow();
                    if (drawRow == 2) p.drawMiddleRow();
                    if (drawRow == 3) p.drawBottomRow();
                    System.out.printf("|");
                    p1AvailableCounter++;
                } else {
                    System.out.printf("       |");
                }
                // At end of the p1 available area, break to restart at 8, 16, etc
                if (boardCol % 3 == 0 && boardCol != 0) break;
                boardCol++;
            }

            // Gap between P2 Available and Grid
            System.out.printf("   ");


            // Game Board
            boardCol = 0;
            System.out.printf("|");

            while (true) {
                // Drawing grid Spawners and Out of Bounds
                if (round.board.isOutOfBoundsOnBoard(boardRow, boardCol)) {
                    if (drawRow == 1) {
                        System.out.printf("       |");
                    }
                    if (drawRow == 2) {
                        System.out.printf("   X   |");
                    }
                    if (drawRow == 3) {
                        System.out.printf("       |");
                    }
                }
                else if (round.board.isFacePosition(boardCol, boardRow)) {
                    if (drawRow == 1) {
                        System.out.printf("PROTECT|");
                    }
                    if (drawRow == 2) {
                        System.out.printf("       |");
                    }
                    if (drawRow == 3) {
                        System.out.printf("  MEE  |");
                    }
                }
                // If there's something on the grid at the current x/y pos, draw it in steps (row 1, 2,3)
                else if (round.board.isPieceAtPos(boardRow, boardCol)) {
                    Piece p = round.board.getPieceAtPos(boardRow, boardCol);
                    if (drawRow == 1) p.drawTopRow();
                    if (drawRow == 2) p.drawMiddleRow();
                    if (drawRow == 3) p.drawBottomRow();
                    System.out.printf("|");
                }
                else if (round.board.isSpawnPosition(boardCol, boardRow)) {
                    if (!round.board.isPieceAtPos(boardRow, boardCol)) {
                        if (drawRow == 1) {
                            if (new Coord(boardCol, boardRow).equals(round.getPlayerbyColor(Player.ColorIdentity.GREEN).spawnPoint)) {
                                System.out.printf(" GREEN |");
                            }
                            else if (new Coord(boardCol, boardRow).equals(round.getPlayerbyColor(Player.ColorIdentity.YELLOW).spawnPoint)) {
                                System.out.printf(" YELLO |");
                            }
                        }
                        if (drawRow == 2) {
                            System.out.printf("       |");
                        }
                        if (drawRow == 3) {
                            System.out.printf(" SPAWN |");
                        }
                    }
                }
                else {
                    System.out.printf("       |");
                }
                // At end of the p1 available area, break to restart at 8, 16, etc
                if (boardCol % 9 == 0 && boardCol != 0) break;
                boardCol++;
            }

            // Gap between P2 Available and Grid
            System.out.printf("   ");

            // P2 Available
            int p2AvailableCounter = iteration;
            boardCol = 1;

            System.out.printf("|");
            while (true) {
                if (p2AvailableCounter < round.players.get(1).piecesAvailable.size()) {
                    Piece p = round.players.get(1).piecesAvailable.get(p2AvailableCounter);
                    if (drawRow == 1) p.drawTopRow();
                    if (drawRow == 2) p.drawMiddleRow();
                    if (drawRow == 3) p.drawBottomRow();
                    System.out.printf("|");
                    p2AvailableCounter++;
                } else {
                    System.out.printf("       |");
                }
                // At end of the p1 available area, break to restart at 8, 16, etc
                if (boardCol % 3 == 0 && boardCol != 0) break;
                boardCol++;
            }

            // End of row
            System.out.println();
        }
    }

    /**
     * Draws the titles for each compartment
     * P1/P2 Cemetery - Pieces in the cemetery, cannot be used.
     */
    private void drawCemeteryHeadings() {
        System.out.println( "                           " + "P1 Cemetery" +
                            "                                                             " +
                            "P2 Cemetery");
    }

    /**
     * Grid coordinate separator.
     * I.E.
     *  + - - - + - - - +  < -- This is the separator
     *  |               |
     *  |  ROW CONTENT  |
     *  |               |
     *  + - - - + - - - +
     */
    private void drawCemeterySeparator() {
        // Top Row
        for (int i = 0; i < 8; i++) {
            System.out.printf("+ - - - ");
        }
        System.out.printf("+");
        System.out.printf("       ");

        for (int i = 0; i < 8; i++) {
            System.out.printf("+ - - - ");
        }
        System.out.printf("+");
        System.out.println();
    }

    private void drawCemeteryContent() {
        for (int i = 0; i < 3; i++) {
            drawCemeterySeparator();
            drawCemeteryRow(3*i);
        }
        drawCemeterySeparator();
    }


    /**
     * Grid coordinate separator.
     * I.E.
     *  + - - - + - - - +
     *  |               | <--
     *  |  ROW CONTENT  | <-- Draws these rows of content
     *  |               | <--
     *  + - - - + - - - +
     *
     * @param iteration start column of the current board row
     *                  i.e row = 0 thus col = 0
     *                      row = 1 thus col = 3
     *                      etc
     */
    private void drawCemeteryRow(int iteration) {
        for (int drawRow = 1; drawRow <= 3; drawRow++) {

            // P1 Cemetery
            int p1Cemetery = iteration;
            int boardCol = 1;

            System.out.printf("|");
            while (true) {
                if (p1Cemetery < round.players.get(0).piecesCemetery.size()) {
                    Piece p = round.players.get(0).piecesCemetery.get(p1Cemetery);
                    if (drawRow == 1) p.drawTopRow();
                    if (drawRow == 2) p.drawMiddleRow();
                    if (drawRow == 3) p.drawBottomRow();
                    System.out.printf("|");
                    p1Cemetery++;
                } else {
                    System.out.printf("       |");
                }
                // At end of the p1 available area, break to restart at 8, 16, etc
                if (boardCol % 8 == 0 && boardCol != 0) break;
                boardCol++;
            }
            System.out.printf("   ");

            // Gap between P1 Cemetery and P2 Cemetery
            System.out.printf("    ");

            // P2 Cemetery
            int p2Cemetery = iteration;
            boardCol = 1;

            System.out.printf("|");
            while (true) {
                if (p2Cemetery < round.players.get(1).piecesCemetery.size()) {
                    Piece p = round.players.get(1).piecesCemetery.get(p2Cemetery);
                    if (drawRow == 1) p.drawTopRow();
                    if (drawRow == 2) p.drawMiddleRow();
                    if (drawRow == 3) p.drawBottomRow();
                    System.out.printf("|");
                    p2Cemetery++;
                } else {
                    System.out.printf("       |");
                }
                // At end of the p1 available area, break to restart at 8, 16, etc
                if (boardCol % 8 == 0 && boardCol != 0) break;
                boardCol++;
            }

            System.out.println();
        }
    }
}