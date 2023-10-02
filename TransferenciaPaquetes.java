import java.io.OutputStream;
import java.util.Arrays;
import java.net.Socket;
import java.net.ServerSocket;
import java.io.IOException;


public class TransferenciaPaquetes{

    private static TransferenciaPaquetes instancia;
    
    public static TransferenciaPaquetes getInstancia(){
        if(instancia==null){
            instancia=new TransferenciaPaquetes();
        }
        return instancia;
    }

  
    public void envio(int cantidadMegasTransferir,byte[] fileData,OutputStream output,Socket clientSocket){
        try{
            //cantidadMegasTransferir = 11 * 1024 * 1024; // Tamaño del paquete en bytes (2 MB)
            /*for (int i = 0; i < fileData.length; i += cantidadMegasTransferir) {
                int endIndex = Math.min(i + cantidadMegasTransferir, fileData.length);
                byte[] packetData = Arrays.copyOfRange(fileData,i,endIndex);
                output.write(packetData);
                output.flush();

            }*/
             // Enviar el archivo en bloques más pequeños
            //int packetSize = 8192; // Tamaño del paquete en bytes (8 KB)
            int offset = 0;

            while (offset < fileData.length) {
                int remainingBytes = fileData.length - offset;
                int bytesToSend = Math.min(cantidadMegasTransferir,remainingBytes);
                output.write(fileData, offset, bytesToSend);
                output.flush();
                offset += bytesToSend;
            }
        } catch (OutOfMemoryError e) {
        // Maneja la excepción aquí
        e.printStackTrace(); // o cualquier otro manejo adecuado

        }catch(Exception ex){
            System.out.println("Se produjo el siguiente error: "+ex.getMessage());
        } finally {
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    }
}