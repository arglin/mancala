package com.bol.lirong.mancala.exception;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author linlirong
 * @created 26/02/2022
 * @project mancala
 */
public class InvalidGameInputException extends IllegalArgumentException {

    public InvalidGameInputException(String... messages) {
        super(String.join("", Arrays.asList(messages)));
    }
}
