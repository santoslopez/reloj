import java.io.OutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.Arrays;

public class TransferenciaPaquetes {
    private static TransferenciaPaquetes instancia;

    public static TransferenciaPaquetes getInstancia() {
        if (instancia == null) {
            instancia = new TransferenciaPaquetes();
        }
        return instancia;
    }

    public void envio(int megasPorPaquete, byte[] fileData, OutputStream output, Socket clientSocket) {
        int numHilos = 3;
        ExecutorService executor = Executors.newFixedThreadPool(numHilos);
        int offset = 0;
        int bytesPorMega = 1024 * 1024; // 1 megabyte en bytes
        int bytesPorPaquete = megasPorPaquete * bytesPorMega;

        try {
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
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
