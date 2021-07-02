package awm.controller;

import awm.controller.subcommand.Run;
import jmvc.server.MtHttpServer;

import java.io.IOException;

import static awm.Util.throwException;

/**
 * Central controller for AWM.
 */
public class Server {
    public static void start(String host, int port) {
        final Server server = new Server(host, port);
    }

    public static void start() {
        start(Config.HOST, Config.PORT);
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
            __server.addRoute("/subcmd/run", new Run() {
            });
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
