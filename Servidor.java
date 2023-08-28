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
        BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))
    ) {
        String request = reader.readLine();
        
        // necesario para separar el request
        String[] partes = request.split(" "); 
        
        if (request != null) {
            // comparamos el request con el archivo que queremos enviar
            String ruta = partes[1].substring(1); // Elimina la "/" al principio

            if (partes[1].equals("/index.html") || partes[1].equals("/index") || partes[1].equals("/")) {
                String pagina = "www/index.html";  
                enviarRespuesta(writer,pagina,clientSocket);
            //}else {
                //String archivo = "404.html";
                //enviarRespuesta(writer,archivo,clientSocket);
            }else{
                String archivo = ruta;
                System.out.println("Archivo..................: "+archivo);
                enviarRespuesta(writer,archivo,clientSocket);
            }
        }
    } catch (IOException e) {
       
        System.out.println("Se produjo el siguiente error en el servidor: " + e.getMessage());
    } finally {
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// Es lo que se le envia al usuario cliente
private static void enviarRespuesta(BufferedWriter writer, String archivo,Socket clientSocket) throws IOException {
    byte[] contenidoArchivo = Files.readAllBytes(Paths.get(archivo));

    String contentTipo = tipos.recuperarContentType(archivo);
    
    writer.write("HTTP/1.1 200 OK\r\n");
    // tratar de tener una conexion persistente
    writer.write("Connection: Keep-Alive\r\n");
    writer.write("Keep-Alive: timeout=15, max=3\r\n");
    writer.write("Content-Length: " + contenidoArchivo.length + "\r\n");
    writer.write("Content-Type: "+contentTipo+"\r\n");
    writer.write("Cache-Control: max-age=3600\r\n"); // Tiempo de caché en segundos
    
    //System.out.println("Content-Type: "+contentTipo+"\r\n");
    //System.out.println("Content-Length: "+contenidoArchivo.length+"\r\n");

    writer.write("\r\n");
    writer.flush();

    OutputStream salidaStre = clientSocket.getOutputStream();
    salidaStre.write(contenidoArchivo);
    salidaStre.flush();
}
    
}