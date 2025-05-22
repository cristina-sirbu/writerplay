package com.cristina.writerplay.exception;

public class FileNotFoundInBucketException extends RuntimeException {
    public FileNotFoundInBucketException(String message) {
        super(message);
    }
}
