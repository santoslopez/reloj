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

public class TransferenciaPaquetes {
    // patron de diseno singleton para crear una sola instancia de la clase
    private static TransferenciaPaquetes instancia;
    public static TransferenciaPaquetes getInstancia() {
        if (instancia == null) {
            instancia = new TransferenciaPaquetes();
        }
        return instancia;
    }
    
    public double obtenerTamanioArchivoEnMB(String rutaArchivo) {
        try {
            Path filePath = Paths.get(rutaArchivo);
            long fileSizeInBytes = Files.size(filePath);
            double fileSizeInMB = (double) fileSizeInBytes / (1024 * 1024);
            return fileSizeInMB;
        } catch (Exception e) {
            e.printStackTrace();
            return -1.0; // Manejo de errores, puedes devolver un valor negativo si hay un problema
        }
    }
    
    public void envio(int megasPorPaquete, String rutaArchivo, OutputStream output, Socket clientSocket) {
        int numHilos = 4;
        ExecutorService executor = Executors.newFixedThreadPool(numHilos);
        int offset = 0;
        int bytesPorMega = 1024 * 1024; // 1 megabyte en bytes
        //double megasArchivo = (double)fileLengthArchivo/(1024*1024);
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
        } finally {
            try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
}

}
