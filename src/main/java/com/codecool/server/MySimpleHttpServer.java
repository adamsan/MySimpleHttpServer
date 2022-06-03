package com.codecool.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;

public class MySimpleHttpServer {

    private final ServerSocket serverSocket;

    public MySimpleHttpServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            throw new MySimleHttpServerException(e);
        }
    }

    public static void main(String[] args) {
        MySimpleHttpServer server = new MySimpleHttpServer(8002);
        server.start();
    }

    public void start() {
        try {
            System.out.println("Starting server");
            Socket socket = serverSocket.accept();
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                if (line.matches("GET /.* HTTP/1.1")) {
                    String requestedPage = line.split(" ")[1];
                    System.out.println("Requested page:" + requestedPage);
                }

                if (line.isEmpty()) break;
            }
            System.out.println("------------------------------");
            LocalDateTime now = LocalDateTime.now();
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            writer.println("""
                    HTTP/1.1 200 OK
                    Content-Type: text/html
                                                    
                    <html>
                    <head><title>My simple server response page</title><head>
                    <body>
                    <h1>Simple webserver page</h1>
                    <div>Would you have thought, a webserver can be so simple?  </div>
                    <p>Current time is: %s</p>
                    </body>
                    </html>
                    """.formatted(now));
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            throw new MySimleHttpServerException(ex);
        }
    }

    public void stop() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new MySimleHttpServerException(e);
        }
    }
}

class MySimleHttpServerException extends RuntimeException {

    public MySimleHttpServerException(IOException e) {
        super(e);
    }
}