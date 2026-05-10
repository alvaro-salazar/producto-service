package com.denkitronik.productoservice.infrastructure.minio;

public class MinioUploadException extends RuntimeException {

    public MinioUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
