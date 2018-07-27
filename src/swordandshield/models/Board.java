package swordandshield.models;

import java.io.Serializable;


public class Board implements Serializable {

    Piece[][] grid = new Piece[10][10];
    public Round round;

    public Board(Round round) {
        this.round = round;
    }

    /**
     * Checks if the coordinates are outside of the 10 x 10 grid or in the corners and if
     * there if not, returns if there is a piece at the location
     *
     * @param boardRow
     * @param boardCol
     * @return  returns true if theres a piece at the location. Returns false if the position
     *          is out of bounds or there isn't a piece at the location
     */
    @SuppressWarnings("JavaDoc")
    public boolean isPieceAtPos(int boardRow, int boardCol) {
        return !outOfBounds(boardCol, boardRow) && (grid[boardRow][boardCol] != null);
    }
    public boolean isPieceAtPos(Coord pos) {
        return isPieceAtPos(pos.getY(), pos.getX());
    }

    boolean isPieceAtOffset(int boardX, int boardY, String dir, int offset) {
        Coord pos = round.determineOffset(new Coord(boardX, boardY), dir, offset);
        return isPieceAtPos(pos);
    }
    boolean isPieceAtOffset(Coord origPos, String dir, int offset) {
        return isPieceAtOffset(origPos.getX(), origPos.getY(), dir, offset);
    }

    /**
     * Gets the piece at the specified position if the position isn't outside the bounds of
     * the 10 x 10 grid or in the corner 3 pieces
     *
     * @param boardRow
     * @param boardCol
     * @return Piece or null (null if pos is out of bounds)
     */
    @SuppressWarnings("JavaDoc")
    public Piece getPieceAtPos(int boardRow, int boardCol) {
        if (!outOfBounds(boardCol, boardRow)) {
            return grid[boardRow][boardCol];
        }
        return null;
    }
    Piece getPieceAtPos(Coord pos) {
        return getPieceAtPos(pos.getY(), pos.getX());
    }

    /**
     * Returns the piece if there is a piece at a parameterised offset away from
     * an original position that is given
     *
     * @param boardX original x position to calculate offset from
     * @param boardY original y position to calculate offset from
     * @param dir up/down/left/right
     * @param offset int value of how far from original position you want to see whether
     *               there's a piece
     * @return returns the piece or null (if there isn't a piece) at the offset position
     */
    Piece getPieceAtOffset(int boardX, int boardY, String dir, int offset) {
        Coord pos = round.determineOffset(new Coord(boardX, boardY), dir, offset);
        return getPieceAtPos(pos);
    }
    Piece getPieceAtOffset(Coord origPos, String dir, int offset) {
        return getPieceAtOffset(origPos.getX(), origPos.getY(), dir, offset);
    }

    /**
     * Depending on the position you pass in, it can do multiple things.
     * If you are trying to place the piece on the Face position, it will
     * put it in the cemetery for you.
     * If the position is valid, i.e it's inside the grid, it will set the
     * piece to the position that was passed in.
     *
     * @param piece Piece to set to the position
     * @param pos The position you want the piece to be set to
     */
    void setPieceToPos(Piece piece, Coord pos) {
        if (isNextToFace(pos.getX(), pos.getY())) {
            //FIXME: If you do reactions, make check if sword facing and win/lose if so.
            if (isGameOver(piece, pos.getX(), pos.getY())) {
                Player p = getPlayerWhoLost(pos.getX(), pos.getY());
                round.gameOver(p);
            }
        }
        if (isFacePosition(pos.getX(), pos.getY())) {
            round.currPlayer.moveToCemetery(piece);
        }
        if (!outOfBounds(pos.getX(), pos.getY())) {
            round.currPlayer.getPiecesOnBoard().remove(getPieceAtPos(pos));
            grid[pos.getY()][pos.getX()] = piece;
        }
        else {
            round.currPlayer.moveToCemetery(piece);
        }
    }

    private Player getPlayerWhoLost(int x, int y) {
        if (x == 2 && y == 1 || x == 1 && y == 2) {
            return round.getPlayerbyColor(Player.ColorIdentity.GREEN);
        }
        else if (x == 7 && y == 8 || x == 8 && y == 7) {
            return round.getPlayerbyColor(Player.ColorIdentity.YELLOW);
        }
        return null;
    }

    /**
     * Only to be used when creating a piece, not to move. Removes piece from
     * available pieces and puts it at the spawn point of the current player
     *
     * @param player
     */
    @SuppressWarnings("JavaDoc")
    public void putPieceOnGrid(Piece player) {
        round.currPlayer.getPiecesAvailable().remove(player); // Can only be added from available pieces
        setPieceToPos(player, round.currPlayer.spawnPoint);
    }

    /**
     * Sets a piece at a specific position on the grid to null.
     * Used when moving pieces (we destroy current position and assign new pos)
     *
     * @param pos Piece at position to destroy
     */
    void destroy(Coord pos) {
        setPieceToPos(null, pos);
    }

    /**
     * Returns whether there is a piece at a parameterised offset away from an original
     * position that is given
     *
     * @param boardX original x position to calculate offset from
     * @param boardY original y position to calculate offset from
     * @param dir up/down/left/right
     * @param offset int value of how far from original position you want to see whether
     *               there's a piece
     * @return returns if there's a piece at the offset position
     */

    /**
     * Returns a boolean if the given position is out of bounds of the grid.
     * This includes both outside the grid and the corner positions on the
     * grid that are out of bounds. Doesn't include the face. see isFacePosition()
     *
     * @param x
     * @param y
     * @return if out of bounds
     */
    @SuppressWarnings("JavaDoc")
    boolean outOfBounds(int x, int y) {
        return (y < 0 || x < 0 || x > 9 || y > 9 ||
                (y == 0 && (x == 0 || x == 1)) ||
                (y == 1 && (x == 0)) ||
                (y == 9 && (x == 9 || x == 8)) ||
                (y == 8) && (x == 9)
        );
    }

    /**
     * Returns whether the given x,y position on the grid is
     * out of bounds. This does not include outside of the grid.
     * These are the three positions in each corner of the 10x10
     * grid
     * @param boardRow
     * @param boardCol
     * @return
     */
    @SuppressWarnings("JavaDoc")
    public boolean isOutOfBoundsOnBoard(int boardRow, int boardCol) {
        return ((boardRow == 0 && (boardCol == 0 || boardCol == 1)) ||
                (boardRow == 1 && (boardCol == 0)) ||
                (boardRow == 9 && (boardCol == 9 || boardCol == 8)) ||
                (boardRow == 8) && (boardCol == 9));
    }

    /**
     * Returns a boolean if the given position is on one of the face positions.
     *
     * @param x
     * @param y
     * @return boolean if one of face positions
     */
    @SuppressWarnings("JavaDoc")
    public boolean isFacePosition(int x, int y) {
        return ((y == round.getPlayerbyColor(Player.ColorIdentity.GREEN).homeFace.getY() &&
                x == round.getPlayerbyColor(Player.ColorIdentity.GREEN).homeFace.getX()) ||

                (y == round.getPlayerbyColor(Player.ColorIdentity.YELLOW).homeFace.getY() &&
                        x == round.getPlayerbyColor(Player.ColorIdentity.YELLOW).homeFace.getX()));
    }

    /**
     * Returns true if the x/y position passed in is one of the three normal grid spots
     * adjacent to the face position (not including the diagonal)
     *
     * @param x
     * @param y
     * @return boolean if one of adjacent face squares
     */
    @SuppressWarnings("JavaDoc")
    boolean isNextToFace(int x, int y) {
        return ((y == round.getPlayerbyColor(Player.ColorIdentity.GREEN).homeFace.getY() &&
                x == round.getPlayerbyColor(Player.ColorIdentity.GREEN).homeFace.getX() + 1) ||

                (y == round.getPlayerbyColor(Player.ColorIdentity.GREEN).homeFace.getY() + 1 &&
                        x == round.getPlayerbyColor(Player.ColorIdentity.GREEN).homeFace.getX()) ||


                (y == round.getPlayerbyColor(Player.ColorIdentity.YELLOW).homeFace.getY() - 1 &&
                        x == round.getPlayerbyColor(Player.ColorIdentity.YELLOW).homeFace.getX()) ||

                (y == round.getPlayerbyColor(Player.ColorIdentity.YELLOW).homeFace.getY() &&
                        x == round.getPlayerbyColor(Player.ColorIdentity.YELLOW).homeFace.getX() - 1));
    }

    boolean isGameOver(Piece piece, int x, int y) {
        return  (x == 2 && y == 1 && piece.layout.get(Piece.Direction.WEST) == Piece.Abilities.SWORD ||
                 x == 1 && y == 2 && piece.layout.get(Piece.Direction.NORTH) == Piece.Abilities.SWORD ||
                 x == 7 && y == 8 && piece.layout.get(Piece.Direction.EAST) == Piece.Abilities.SWORD ||
                 x == 8 && y == 7 && piece.layout.get(Piece.Direction.SOUTH) == Piece.Abilities.SWORD
        );
    }




    /**
     * Returns true if the x/y position passed is one of the two players spawn positions
     *
     * @param x
     * @param y
     * @return
     */
    @SuppressWarnings("JavaDoc")
    public boolean isSpawnPosition(int x, int y) {
        return ((y == round.getPlayerbyColor(Player.ColorIdentity.GREEN).spawnPoint.getY() &&
                x == round.getPlayerbyColor(Player.ColorIdentity.GREEN).spawnPoint.getX()) ||

                (y == round.getPlayerbyColor(Player.ColorIdentity.YELLOW).spawnPoint.getY() &&
                        x == round.getPlayerbyColor(Player.ColorIdentity.YELLOW).spawnPoint.getX()));
    }
}
