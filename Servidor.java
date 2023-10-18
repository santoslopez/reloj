import java.net.Socket;
import java.net.ServerSocket;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.BindException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.nio.file.NoSuchFileException;
import java.io.File;


public class Servidor{

    static int valorFixedThreadPool = ValidarArchivoTexto.getInstancia().getPrimeraFilaValorArchivo("hilos.txt");
    // Número máximo de hilos
    private static ExecutorService executor = Executors.newFixedThreadPool(valorFixedThreadPool); 
    private int PUERTO = ValidarArchivoTexto.getInstancia().getPrimeraFilaValorArchivo("puerto.txt");
    private ServerSocket serverSocket;
    private static byte[] fileData;

    public void iniciar() {
        // recuperando el valor del archivo de la configuracion del puerto 
        int puertoAlmacenado = ValidarArchivoTexto.getInstancia().getPrimeraFilaValorArchivo("puerto.txt");
        
        try {
            serverSocket = new ServerSocket(puertoAlmacenado);
            System.out.println("Servidor HTTP multihilo iniciado en http://localhost:"+puertoAlmacenado+"/");
            
            // Agregar un hook de apagado para manejar la interrupción de Control+C
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    detener(); // Llama al método detener para cerrar el servidor socket
                }
            });

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nueva conexión entrante: " + clientSocket.getInetAddress().getHostAddress());
                Runnable clientTask = () -> manejadorCliente(clientSocket);
                executor.execute(clientTask);
            }
        }catch (NoSuchFileException nsfex) {
            // Maneja la excepción aquí
            System.out.println("Se produjo el siguiente error de NoSuchFileException: "+nsfex.getMessage());            
        } catch (BindException be) {
            System.out.println("El puerto " + PUERTO + " ya está en uso.");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }

    public void detener() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("Servidor detenido.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void manejadorCliente(Socket clientSocket) {

        if(!clientSocket.isConnected()){
            System.out.println("La conexión no está establecidada. No se puede procesar.");
            return;
        }

        try (
            InputStream input = clientSocket.getInputStream();
            OutputStream output = clientSocket.getOutputStream()
        ) {

            byte[] buffer = new byte[8192];
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
            //System.out.println("Lineas: "+lines.length);
            System.out.println("Solicitud del cliente:");
            System.out.println(request);

            String ruta="";
            String valorRuta = "";
            if(lines.length>1){
                String[] requestLine = lines[0].split(" ");
                // Elimina el primer '/' en la ruta
                ruta = requestLine[1].substring(1); 
                String archivoRuta = "www/"+ruta;
                File archivo = new File(archivoRuta);
                
                if(archivo.exists()){
                     // indicar el archivo index por defecto
                    if (ruta.isEmpty() || ruta.equals("/")) {
                        ruta = "www/index.html";
                    }else{
                        ruta = "www/"+ruta;
                    }
               }else{
                    ruta = "www/404.html";
               }
            
            }

            //Detectar si el usuario utiliza el movil o computadora
            boolean esDispositivoMovil = TipoDispositivo.userAgent(lines);
        
            String contentType = ContentType.getInstancia().recuperarContentType(ruta);
               
            String responseHeaders=Headers.getHeader200(contentType);
            
            output.write(responseHeaders.getBytes()); 
            output.flush();

            TipoDispositivo.envioPaquetesPorDispositivo(esDispositivoMovil,ruta,output,clientSocket);
           
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
                // para liberar memoria, pero falta ver si funciona bien
                
                //fileData = null;
                System.gc();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
