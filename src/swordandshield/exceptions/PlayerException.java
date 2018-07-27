package swordandshield.exceptions;

import java.io.Serializable;

/**
 * Created by Chris on 10/08/2017.
 * Exception for when issues arise in game state that cannot be
 * recovered. Throw this when the Player does something unrecoverable.
 */
public class PlayerException extends RuntimeException {

    public PlayerException(String message) {
        super(message);
    }
}