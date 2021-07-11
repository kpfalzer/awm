package awm.controller;

import awm.controller.subcommand.Run;
import jmvc.server.MtHttpServer;

import java.io.IOException;

import static awm.Util.throwException;

/**
 * Central controller for AWM.
 */
public class Server {
    public static Server start(String host, int port) {
        return new Server(host, port);
    }

    public static Server start() {
        return start(Config.HOST, Config.PORT);
    }

    public Server(String host, int port) {
        __host = host;
        __port = port;
        __start();
    }

    public Server() {
        this(Config.HOST, Config.PORT);
    }

    private void __start() {
        try {
            __server = new MtHttpServer(__host, __port);
            __server.addRoute("/subcmd/run", Run.handlerFactory());
            __server.start();
            Thread.currentThread().join();
        } catch (IOException | InterruptedException e) {
            throwException(e);
        }
    }

    private final String __host;
    private final int __port;
    private MtHttpServer __server;

    public static void main(String[] argv) {
        Server.start();
    }
}
