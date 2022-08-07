package com.bol.lirong.mancala.exception;

import java.util.Arrays;

/**
 * @author linlirong
 * @created 26/02/2022
 * @project mancala
 */
public class InvalidGameStateException extends IllegalStateException {

    public InvalidGameStateException(String... messages) {
        super(String.join("", Arrays.asList(messages)));
    }
}
