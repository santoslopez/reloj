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
import java.util.Arrays;

public class Servidor{

    //private static int cantidadPaquetesEnviar = 8092;
    
    private static ContentType tipos = new ContentType();
    private static ExecutorService executor = Executors.newFixedThreadPool(10); // Número máximo de hilos

    public void iniciar() {
        
        // recuperando el valor del archivo de la configuracion del puerto 
        int puertoAlmacenado = ValidarArchivoTexto.getInstancia().getPrimeraFilaValorArchivo("puerto.txt");
        

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

            byte[] buffer = new byte[8092];
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
            

            /**
             * Detectar si el usuario utiliza el movil o computadora
             */
            String userAgent="";
            for(String line:lines){
                if (line.startsWith("User-Agent: ")) {
                    userAgent = line.substring("User-Agent: ".length());
                    break;
                }
            }
            
            // determinar si se usa movil o computadora
            boolean esDispositivoMovil = userAgent.contains("Mobile");

            // Leer el archivo y enviarlo en paquetes
            Path filePath = Paths.get(ruta);
             if (Files.exists(filePath) && Files.isRegularFile(filePath)) {
                byte[] fileData = Files.readAllBytes(filePath);

                String contentType = ContentType.getInstancia().recuperarContentType(ruta);
                
                long fileLengthArchivo = fileData.length;
                double megasArchivo = (double)fileLengthArchivo/(1024*1024);

                System.out.println("Archivo solicitado: "+ megasArchivo + " MB)");
                // Establecer los encabezados para visualizar el archivo en el navegador
                String responseHeaders = "HTTP/1.1 200 OK\r\n" +
                                        "Content-Type: " + contentType + "\r\n" +
                                        "Cache-Control: max-age=30, public\r\n" + // 1 minuto
                                        "Content-Disposition: inline\r\n" + // Visualización en el navegador
                                        "Content-Length: " + fileData.length + "\r\n\r\n";
                                        

                output.write(responseHeaders.getBytes());                

                if(esDispositivoMovil){
                    System.out.println("Es un dispositivo movil");

                    // si el paquete es menor o igual a 6 MB
                    if(megasArchivo <= 5){
                        for (int i = 0; i < fileData.length; i++) {
                            output.write(fileData[i]);
                            output.flush();
                        }
                    }else if (megasArchivo >= 5 && megasArchivo <= 19) {
                        int megasTrasferir = 2 * 1024 * 1024; // Tamaño del paquete en bytes (2 MB)
                    
                        TransferenciaPaquetes.getInstancia().envio(megasTrasferir,fileData,output,clientSocket);
                    }else if (megasArchivo >= 20 && megasArchivo <= 50) {
                        int megasTrasferir = 4 * 1024 * 1024; // Tamaño del paquete en bytes (2 MB)
                    
                        TransferenciaPaquetes.getInstancia().envio(megasTrasferir,fileData,output,clientSocket);

                    }else if (megasArchivo >= 51 && megasArchivo <= 100) {
                        int megasTrasferir = 5 * 1024 * 1024; // Tamaño del paquete en bytes (2 MB)
                    
                        TransferenciaPaquetes.getInstancia().envio(megasTrasferir,fileData,output,clientSocket);
                    }else if (megasArchivo >= 101 && megasArchivo <= 189) {
                        int megasTrasferir = 6 * 1024 * 1024; // Tamaño del paquete en bytes (2 MB)
                    
                        TransferenciaPaquetes.getInstancia().envio(megasTrasferir,fileData,output,clientSocket);

                    }else if (megasArchivo >= 190 && megasArchivo <= 220) {
                        int megasTrasferir = 8 * 1024 * 1024; // Tamaño del paquete en bytes (2 MB)
                    
                        TransferenciaPaquetes.getInstancia().envio(megasTrasferir,fileData,output,clientSocket);

                    }else if (megasArchivo >= 201 && megasArchivo <= 340) {
                        int megasTrasferir = 9 * 1024 * 1024; // Tamaño del paquete en bytes (2 MB)
                    
                        TransferenciaPaquetes.getInstancia().envio(megasTrasferir,fileData,output,clientSocket);

                    }else if (megasArchivo >= 341 && megasArchivo <= 450) {
                        int megasTrasferir = 7 * 1024 * 1024; // Tamaño del paquete en bytes (2 MB)
                    
                        TransferenciaPaquetes.getInstancia().envio(megasTrasferir,fileData,output,clientSocket);
                    }else if (megasArchivo >= 451 && megasArchivo <= 949) {
                        int megasTrasferir = 11 * 1024 * 1024; // Tamaño del paquete en bytes (2 MB)
                    
                        TransferenciaPaquetes.getInstancia().envio(megasTrasferir,fileData,output,clientSocket);

                    }else if (megasArchivo >= 950 && megasArchivo <= 1020) {
                        int megasTrasferir = 6 * 1024 * 1024; // Tamaño del paquete en bytes (2 MB)
                    
                        TransferenciaPaquetes.getInstancia().envio(megasTrasferir,fileData,output,clientSocket);

                    }else{

                        /*for (int i = 0; i < fileData.length; i += packetSize) {

                            int endIndex = Math.min(i + packetSize, fileData.length);
                            byte[] packetData = Arrays.copyOfRange(fileData,i, endIndex);
                            output.write(packetData);
                            output.flush();
                        }*/
                    }                
                
                
                }else{
                    System.out.println("Es una computadora");

                    // si el paquete es menor o igual a 6 MB
                    if(megasArchivo <= 10){
                        for (int i = 0; i < fileData.length; i++) {
                            output.write(fileData[i]);
                            output.flush();
                        }
                    }else if (megasArchivo >= 11 && megasArchivo <= 50) {
                        int megasTrasferir = 5 * 1024 * 1024; // Tamaño del paquete en bytes (2 MB)
                    
                        TransferenciaPaquetes.getInstancia().envio(megasTrasferir,fileData,output,clientSocket);

                    }else if (megasArchivo >= 51 && megasArchivo <= 189) {
                        int megasTrasferir = 8 * 1024 * 1024; // Tamaño del paquete en bytes (2 MB)
                    
                        TransferenciaPaquetes.getInstancia().envio(megasTrasferir,fileData,output,clientSocket);

                    }else if (megasArchivo >= 190 && megasArchivo <= 219) {
                        int megasTrasferir = 22 * 1024 * 1024; // Tamaño del paquete en bytes (2 MB)
                    
                        TransferenciaPaquetes.getInstancia().envio(megasTrasferir,fileData,output,clientSocket);

                    }else if (megasArchivo >= 200 && megasArchivo <= 340) {
                        int megasTrasferir = 15 * 1024 * 1024; // Tamaño del paquete en bytes (2 MB)
                    
                        TransferenciaPaquetes.getInstancia().envio(megasTrasferir,fileData,output,clientSocket);

                    }else if (megasArchivo >= 341 && megasArchivo <= 450) {
                        int megasTrasferir = 20 * 1024 * 1024; // Tamaño del paquete en bytes (2 MB)
                    
                        TransferenciaPaquetes.getInstancia().envio(megasTrasferir,fileData,output,clientSocket);

                    }else if (megasArchivo >= 451 && megasArchivo <= 949) {
                        int megasTrasferir = 23 * 1024 * 1024; // Tamaño del paquete en bytes (2 MB)
                    
                        TransferenciaPaquetes.getInstancia().envio(megasTrasferir,fileData,output,clientSocket);

                    }else if (megasArchivo >= 950 && megasArchivo <= 1020) {
                        int megasTrasferir = 27 * 1024 * 1024; // Tamaño del paquete en bytes (2 MB)
                    
                        TransferenciaPaquetes.getInstancia().envio(megasTrasferir,fileData,output,clientSocket);

                    }else{

                        /*for (int i = 0; i < fileData.length; i += packetSize) {

                            int endIndex = Math.min(i + packetSize, fileData.length);
                            byte[] packetData = Arrays.copyOfRange(fileData,i, endIndex);
                            output.write(packetData);
                            output.flush();
                        }*/
                    }
                
                }


            } else {
                // Archivo no encontrado, enviar respuesta 404
                String respuesta404 = "HTTP/1.1 404 Not Found\r\n\r\n";
                output.write(respuesta404.getBytes());
                output.flush();
            }
        } catch (OutOfMemoryError e) {
            // Maneja la excepción aquí
            e.printStackTrace(); // o cualquier otro manejo adecuado
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
