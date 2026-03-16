import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {
    public static void main(String[] args) {
        System.out.println("Servidor iniciado en http://localhost:8080");
        
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    handleRequest(socket);
                } catch (IOException e) {
                    System.err.println("Error procesando petición: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleRequest(Socket socket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             OutputStream out = socket.getOutputStream()) {
            
            String requestLine = in.readLine();
            if (requestLine == null || requestLine.isEmpty()) return;
            
            System.out.println("Petición recibida: " + requestLine);
            
            String[] parts = requestLine.split(" ");
            if (parts.length < 2) return;
            
            String method = parts[0];
            String path = parts[1];

            if (method.equals("GET") && path.equals("/books")) {
                String body = BookController.getAllBooks();
                sendResponse(out, "200 OK", body);
            } else {
                sendResponse(out, "404 Not Found", "{\"error\":\"Ruta no encontrada\"}");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendResponse(OutputStream out, String status, String body) throws IOException {
        String response = "HTTP/1.1 " + status + "\r\n" +
                          "Content-Type: application/json\r\n" +
                          "Content-Length: " + body.getBytes().length + "\r\n" +
                          "\r\n" +
                          body;
        out.write(response.getBytes());
        out.flush();
    }
}