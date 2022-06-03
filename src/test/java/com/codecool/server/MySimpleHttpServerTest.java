package com.codecool.server;

import org.junit.jupiter.api.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class MySimpleHttpServerTest {

    MySimpleHttpServer server;

    @BeforeEach
    void setup() {
        Runnable r = () -> {
            server = new MySimpleHttpServer(8002);
            server.start();
        };
        new Thread(r).start();
    }

    @AfterEach
    void teardown() {
        server.stop();
    }

    @Test
    void webserverRespondsToOneRequest() throws IOException {

        MyResponse resp = sendGetRequest("http://localhost:8002");
        assertEquals(200, resp.status, "Status code is 200 (OK)");
        assertEquals("text/html", resp.contentType);
        assertTrue(resp.content.contains("Simple webserver page"));
    }

    private MyResponse sendGetRequest(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setConnectTimeout(500);

        int status = con.getResponseCode();
        String contentType = con.getContentType();
        String content = null;
        try (var in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            content = in.lines().collect(Collectors.joining("\n"));
        }
        return new MyResponse(status, contentType, content);
    }
}

class MyResponse {
    int status;
    String contentType;
    String content;

    public MyResponse(int status, String contentType, String content) {
        this.status = status;
        this.contentType = contentType;
        this.content = content;
    }
}