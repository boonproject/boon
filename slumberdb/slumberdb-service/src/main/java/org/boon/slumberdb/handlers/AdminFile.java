package org.boon.slumberdb.handlers;

import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;

import java.nio.file.Path;

public class AdminFile implements Handler<HttpServerRequest> {
    private String absoluteFileName;
    private String fileName;
    private String contentType;

    public AdminFile(Path path, String contentType) {
        this.absoluteFileName = path.toAbsolutePath().toString();
        this.fileName = fileName;
        this.contentType = contentType;
    }

    @Override
    public void handle(HttpServerRequest request) {
        request.response().putHeader("Content-Type", contentType);
        request.response().sendFile(absoluteFileName);
    }

    public String getAbsoluteFileName() {
        return absoluteFileName;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContentType() {
        return contentType;
    }
}
