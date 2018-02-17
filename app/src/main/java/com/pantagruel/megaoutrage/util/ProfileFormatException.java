package com.pantagruel.megaoutrage.util;

/**
 * Custom exception
 * Nothing special with the code
 */

public class ProfileFormatException extends Exception {
    // Parameterless Constructor
    public ProfileFormatException() {}

    // Constructor that accepts a message
    public ProfileFormatException(String message)
    {
        super(message);
    }
}
