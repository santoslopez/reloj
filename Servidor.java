import java.net.Socket;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.File;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.Path;
import java.net.BindException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.FileReader;
import java.net.SocketException;

public class Servidor{
    private static ContentType tipos = new ContentType();
    public void iniciar() {
        
        // recuperando el valor del archivo de la configuracion del puerto 
        int puertoAlmacenado = ValidarArchivoTexto.getInstancia().getPrimeraFilaValorArchivo("puerto.txt");
        
        ExecutorService executor = Executors.newFixedThreadPool(puertoAlmacenado); // Número máximo de hilos

        try (ServerSocket serverSocket = new ServerSocket(puertoAlmacenado)) {
            System.out.println("Servidor HTTP multihilo iniciado en http://localhost:"+puertoAlmacenado+"/");
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nueva conexión entrante: " + clientSocket.getInetAddress().getHostAddress());
                Runnable clientTask = () -> manejadorCliente(clientSocket);
                executor.execute(clientTask);
            }
        } catch (BindException be) {
            int PUERTO = 8080;
            System.out.println("El puerto " + PUERTO + " ya está en uso.");
            //menu();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }

    private static void manejadorCliente(Socket clientSocket) {
        try (
            InputStream input = clientSocket.getInputStream();
            OutputStream output = clientSocket.getOutputStream()
        ) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            
            // Leer la solicitud del cliente (ignorando las cabeceras por ahora)
            StringBuilder request = new StringBuilder();
            while ((bytesRead = input.read(buffer)) != -1) {
                request.append(new String(buffer, 0, bytesRead));
                if (request.toString().endsWith("\r\n\r\n")) {
                    break; // Fin de la solicitud HTTP
                }
            }

            // Analizar la solicitud para obtener la ruta del archivo solicitado
            String[] lines = request.toString().split("\r\n");
            String[] requestLine = lines[0].split(" ");
            String ruta = requestLine[1].substring(1); // Elimina el primer '/' en la ruta
            
            // Leer el archivo y enviarlo en paquetes
            Path filePath = Paths.get(ruta);
             if (Files.exists(filePath) && Files.isRegularFile(filePath)) {
                byte[] fileData = Files.readAllBytes(filePath);

                String contentType = ContentType.getInstancia().recuperarContentType(ruta);
                
                // Establecer los encabezados para visualizar el archivo en el navegador
                String responseHeaders = "HTTP/1.1 200 OK\r\n" +
                                        "Content-Type: " + contentType + "\r\n" +
                                        "Cache-Control: max-age=60, public\r\n" + // 1 minuto
                                        "Content-Disposition: inline\r\n" + // Visualización en el navegador
                                        "Content-Length: " + fileData.length + "\r\n\r\n";
                                        
                output.write(responseHeaders.getBytes());
                output.write(fileData);
                output.flush();
                
                // Enviar el archivo en paquetes
                for (int i = 0; i < fileData.length; i++) {
                    output.write(fileData[i]);
                    output.flush();
                }
            } else {
                // Archivo no encontrado, enviar respuesta 404
                String respuesta404 = "HTTP/1.1 404 Not Found\r\n\r\n";
                output.write(respuesta404.getBytes());
                output.flush();
            }

        } catch (SocketException se) {
            System.out.println("Se produjo una excepción de SocketException: " + se.getMessage());
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
