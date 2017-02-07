package ee.ut.jf2016.hw6;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ChatServer {
    private static HashMap<Socket, String> names = new HashMap<>();

    public static void main(String[] args) throws Exception {
        runWebServer();
        runClientServer();
    }

    private static void runClientServer()  {
        try (ServerSocket server = new ServerSocket(7070)) {
            ExecutorService executor = Executors.newCachedThreadPool();
            System.out.println("Server started. Listening for connections...");
            while (true) {
                Socket client = server.accept();
                System.out.println("Someone's here!");
                BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream(), UTF_8));
                executor.execute(() -> {
                    while (true) {
                        try {
                            String msg = reader.readLine();
                            if (msg == null) {
                                notifyEveryone(client, names.get(client) + " has left\n");
                                reader.close();
                                names.remove(client);
                                break;
                            }
                            if (!names.containsKey(client)) {
                                // msg is client username in this case
                                while (names.containsValue(msg)) {
                                    msg = msg + "_" + (int)(Math.random()*1000);
                                }
                                sendMessageToClient(client, "Your username is: " + msg + "\n");
                                notifyEveryone(client, msg + " has joined\n");
                                names.put(client, msg);
                            } else notifyEveryone(client, names.get(client) + ": " + msg + "\n");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void runWebServer() throws Exception {
        Thread th = new Thread(() -> {
            class MyHandler extends AbstractHandler {
                @Override
                public void handle(String path, Request baseRequest,
                                   HttpServletRequest request, HttpServletResponse response)
                        throws IOException, ServletException {
                    baseRequest.setHandled(true);
                    response.setStatus(HttpServletResponse.SC_OK);
                    names.keySet().forEach(key -> responseToGetRequest(response, key));
                }
            }
            Server server = new Server(8080);
            server.setHandler(new MyHandler());
            try {
                server.start();
                server.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        th.start();
    }
    private static void sendMessageToClient(Socket currentClient, String message) {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(currentClient.getOutputStream(), UTF_8);
            writer.write(message);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void responseToGetRequest(HttpServletResponse response, Socket client){
        try {
            response.getWriter().println(client.toString() + " -> " + names.get(client));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void notifyEveryone(Socket sender, String message) {
        names.keySet().stream().filter(currentClient -> !currentClient.equals(sender)).forEach(currentClient -> sendMessageToClient(currentClient, message));
    }
}
