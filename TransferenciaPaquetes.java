import java.io.OutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.NoSuchFileException;


public class TransferenciaPaquetes {
    // patron de diseno singleton para crear una sola instancia de la clase
    private static TransferenciaPaquetes instancia;
    public static TransferenciaPaquetes getInstancia() {
        if (instancia == null) {
            instancia = new TransferenciaPaquetes();
        }
        return instancia;
    }
        
    public void envio(int megasPorPaquete, String rutaArchivo, OutputStream output, Socket clientSocket) {
        int numHilos = 5;
        ExecutorService executor = Executors.newFixedThreadPool(numHilos);
        int offset = 0;
        int bytesPorMega = 1024 * 1024; // 1 megabyte en bytes
        int bytesPorPaquete = megasPorPaquete * bytesPorMega;

        try {
            Path filePath = Paths.get(rutaArchivo);
            // validar que el archivo existe
            if (Files.exists(filePath) && Files.isReadable(filePath)) {
            
            byte[] fileData = Files.readAllBytes(filePath);

            while (offset < fileData.length) {
                final int inicio = offset;
                int fin = Math.min(inicio + bytesPorPaquete, fileData.length);
                offset = fin;

                byte[] packetData = Arrays.copyOfRange(fileData, inicio, fin);

                executor.execute(() -> {
                    try {
                        output.write(packetData);
                        output.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
            executor.shutdown();
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            }else{
                
                //String respuesta404 = "HTTP/1.1 404 Not Found\r\n\r\n";
                String respuesta404 = Headers.getHeader404();
                output.write(respuesta404.getBytes());
                output.flush();
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            e.printStackTrace();
        //}catch (NoSuchFileException nsfex) {
            // Maneja la excepción aquí
            //nsfex.printStackTrace(); // o cualquier otro manejo adecuado
          //  System.out.println("Se produjo el siguiente error de NoSuchFileException: "+nsfex.getMessage());
            //String respuesta404 = Headers.getHeader404();
            
        } finally {
            try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
