package swordandshield.startup;

import swordandshield.models.Piece;
import swordandshield.models.Player;

import java.io.*;
import java.util.*;

/**
 * Loads data from a text file containing all the information
 * for the pieces used in the Sword and Shields game.
 */
public class DataLoader implements Serializable {

    public DataLoader(Player player) {
        player.piecesAvailable = parsePieces(player);
    }

    /**
     * Parses the information in the text file to return a list of pieces
     * containing the correct data
     * @param player player that these pieces that will be assigned to
     * @return List of pieces to be assigned to a player
     */
    private List<Piece> parsePieces(Player player) {
        List<Piece> piecesAvailable = new ArrayList<>();
        try {
            BufferedReader data = new BufferedReader(new FileReader(new File("PieceData.txt")));
            try {
                data.readLine(); // Get rid of first comment line
            } catch (IOException e) {
                e.printStackTrace();
            }
            String line;
            Character name = 'A';

            Map<Integer, Piece.Direction> intToDir = new HashMap<>();
            intToDir.put(1, Piece.Direction.NORTH);
            intToDir.put(2, Piece.Direction.EAST);
            intToDir.put(3, Piece.Direction.SOUTH);
            intToDir.put(4, Piece.Direction.WEST);

            while ((line = data.readLine()) != null) {
                String[] values = line.split("\t");

                HashMap<Piece.Direction, Piece.Abilities> abilities = new HashMap<>();

                int index = 0;
                for (String v : values) {
                    index++;
                    Piece.Abilities ability = null;
                    switch (v) {
                        case "SWORD":
                            ability = Piece.Abilities.SWORD;
                            break;
                        case "SHIELD":
                            ability = Piece.Abilities.SHIELD;
                            break;
                        case "NOTHING":
                            ability = Piece.Abilities.NOTHING;
                            break;
                    }
                    abilities.put(intToDir.get(index), ability);
                }
                if (index%4 == 0) {
                    Piece piece = new Piece(player, name, abilities);
                    piecesAvailable.add(piece);
                    name++;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return piecesAvailable;
    }
}
