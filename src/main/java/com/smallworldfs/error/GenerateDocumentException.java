package com.smallworldfs.error;

public class GenerateDocumentException extends RuntimeException {

    public static Integer STATUS = 1;

    public GenerateDocumentException(String message) {
        super(message);
    }

    public GenerateDocumentException(String message, Throwable exception) {
        super(message, exception);
    }
}
