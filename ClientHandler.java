import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
    private Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            OutputStream out = clientSocket.getOutputStream()
        ) {
            // Leer la solicitud HTTP del cliente
            String request = in.readLine();
            if (request != null) {
                String[] parts = request.split(" ");
                if (parts.length >= 2) {
                    String method = parts[0];
                    String requestedFile = parts[1].substring(1); // Quita el primer "/"

                    // Establece el tipo de contenido en función de la extensión del archivo
                    String contentType = "";
                    if (requestedFile.endsWith(".css")) {
                        contentType = "text/css";
                    } else if (requestedFile.endsWith(".js")) {
                        contentType = "application/javascript";
                    }else{
                        contentType = "text/html";
                    }

                    // Leer el archivo y enviarlo como respuesta
                    try {
                        
                        InputStream fileStream = new FileInputStream("www/" + requestedFile);

                        // Respuesta HTTP con encabezados
                        String responseHeaders = "HTTP/1.1 200 OK\r\n"
                                + "Content-Type: " + contentType + "\r\n"
                                + "Connection: close\r\n\r\n";

                        // Enviar los encabezados seguidos del contenido del archivo
                        out.write(responseHeaders.getBytes());

                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = fileStream.read(buffer)) != -1) {
                            out.write(buffer, 0, bytesRead);
                        }
                        out.flush();
                        fileStream.close();
                    } catch (IOException e) {
                        // El archivo no existe, responder con 404
                        String notFoundResponse = "HTTP/1.1 404 Not Found\r\n\r\n";
                        out.write(notFoundResponse.getBytes());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
