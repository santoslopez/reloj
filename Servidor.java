import java.net.Socket;
import java.net.ServerSocket;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.BindException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;

public class Servidor{

    private static int valorFixedThreadPool = 5;
    
    private static ContentType tipos = new ContentType();
    private static ExecutorService executor = Executors.newFixedThreadPool(valorFixedThreadPool); // Número máximo de hilos
    private int PUERTO = 8080;
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
        } catch (BindException be) {
            
            System.out.println("El puerto " + PUERTO + " ya está en uso.");
            //menu();
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

            String[] requestLine = lines[0].split(" ");
            // Elimina el primer '/' en la ruta
            String ruta = requestLine[1].substring(1); 

            System.out.println("estoy en: "+ruta); 
                    
            String valorRuta = "";
            
            // indicar el archivo index por defecto
            if (ruta.isEmpty() || ruta.equals("/") || ruta.equals("/index")) {
                ruta = "www/index.html";
            }else{
                ruta = "www/"+ruta;
            }

            //Detectar si el usuario utiliza el movil o computadora
            boolean esDispositivoMovil = TipoDispositivo.userAgent(lines);

            double megasArchivo = TransferenciaPaquetes.getInstancia().obtenerTamanioArchivoEnMB(ruta);

            String contentType = ContentType.getInstancia().recuperarContentType(ruta);
            //System.out.println("contenttype: "+contentType);
               
            String responseHeaders=Headers.getHeader200(contentType);                                         

            output.write(responseHeaders.getBytes());                
            //output.flush();
            TipoDispositivo.envioPaquetesPorDispositivo(esDispositivoMovil,megasArchivo,ruta,output,clientSocket);
           
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
