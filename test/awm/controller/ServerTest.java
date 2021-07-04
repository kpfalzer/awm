package awm.controller;

class ServerTest {

    @org.junit.jupiter.api.Test
    void start() {
        /*
        curl --header "Content-Type: application/json" --request POST --data '{"name":"mr. crazy-k"}' http://localhost:3005/subcmd/run
         */
        Server.start();
    }
}