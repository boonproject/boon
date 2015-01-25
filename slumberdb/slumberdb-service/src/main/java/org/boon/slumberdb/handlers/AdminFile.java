package org.boon.slumberdb.handlers;

import org.boon.IO;
import org.boon.slumberdb.service.config.DataStoreServerConfig;
import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.HttpServerResponse;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class AdminFile implements Handler<HttpServerRequest> {

    public static List<AdminFile> handlers(DataStoreServerConfig dataStoreServerConfig) {
        List<AdminFile> list = new ArrayList<>();
        Path base = IO.path(IO.CLASSPATH_SCHEMA + ":" + "/web/admin");
        List<Path> children = IO.listPath(base);
        for (Path path : children) {
            if (path.getFileName().toString().endsWith(".html")) {
                list.add( new AdminFile(dataStoreServerConfig, path) );
            }
        }
        return list;
    }

    private String serverport;
    private String adminPort;
    private String adminName;
    private String absoluteFileName;
    private String file;

    public AdminFile(DataStoreServerConfig dataStoreServerConfig, Path path) {
        serverport = "" + dataStoreServerConfig.port();
        adminPort = "" + dataStoreServerConfig.adminPort();
        adminName = dataStoreServerConfig.adminName();
        this.absoluteFileName = path.toAbsolutePath().toString();

        String fileName = path.getFileName().toString();
        if (fileName.contains("index.html")) {
            file = "/admin";
        }
        else {
            int at = fileName.lastIndexOf(".html"); // finding the extension
            file = "/admin/" + fileName.substring(0, at);
        }
    }

    @Override
    public void handle(HttpServerRequest request) {
        HttpServerResponse response = request.response();
        response.putHeader("Content-Type", "text/html");
        response.setChunked(true);

        Templater templater = new Templater(request, response);
        IO.eachLine(absoluteFileName, templater);
        response.end();
    }

    class Templater implements IO.EachLine {
        HttpServerRequest request;
        HttpServerResponse response;

        public Templater(HttpServerRequest request, HttpServerResponse response) {
            this.request = request;
            this.response = response;
        }

        // :replace-adminport:
        // :replace-serverhost:
        // :replace-serverport:

        @Override
        public boolean line(String line, int index) {
            if (line.contains(":replace-")) {
                line = line .replace(":replace-serverhost:", request.absoluteURI().getHost())
                        .replace(":replace-serverport:", serverport)
                        .replace(":replace-adminport:", adminPort)
                        .replace(":replace-adminname:", adminName)
                ;
            }
            response.write(line);
            response.write("\n");
            return true;
        }
    }

    public String file() {
        return file;
    }
}
