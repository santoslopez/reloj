/**
 * Clase TipoDispocitivo.java:
 * Aqui se averigua si el dispositivo con el que se conecta el cliente
 * es computadora o celular. En base a esto se configura para enviar
 * el archivo en paquetes de ciertos megas para que la experiencia del usuario sea más agradable.
 */
import java.net.SocketException;
import java.net.Socket;
import java.io.OutputStream;
import java.io.IOException;

public class TipoDispositivo{

    public static boolean userAgent(String lines[]){
        String userAgent="";
        for(String line:lines){
            if (line.startsWith("User-Agent: ")) {
                userAgent = line.substring("User-Agent: ".length());
                break;
            }
        }
        boolean tipoDispositivo = userAgent.contains("Mobile");
        return tipoDispositivo;
    }

    /**
     * Metodo con el que se verifica que tipo de dispositivo está utilizando el cliente
     * y en base a esto se envia el archivo en paquetes de ciertos megas.
     * Importante que el archivo que se verifica que tiene el servidor, en base a cierta condición o tamaño del archivo
     * que el servidor pretende enviar, se envia en paquetes de ciertos megas.
     */
    public static void envioPaquetesPorDispositivo(boolean esDispositivoMovil,double megasArchivo,String ruta,OutputStream output, Socket clientSocket){
      
        try{
            //Array de 10 elementos
            int[] megasTrasferir;   

            if(esDispositivoMovil){
                System.out.println("Es un dispositivo movil");
                megasTrasferir = new int[]{2,3,5,6,7,10,11,12,13,8};
            }else{
                System.out.println("Es una computadora");
                megasTrasferir = new int[]{2,3,5,6,7,10,11,12,13,8};
            }
            if(megasArchivo <= 8){      
                TransferenciaPaquetes.getInstancia().envio(megasTrasferir[0],ruta,output,clientSocket);
            }else if (megasArchivo >= 9 && megasArchivo <= 50) {
                TransferenciaPaquetes.getInstancia().envio(megasTrasferir[1],ruta,output,clientSocket);
            }else if (megasArchivo >= 51 && megasArchivo <= 189) {                    
                TransferenciaPaquetes.getInstancia().envio(megasTrasferir[2],ruta,output,clientSocket);
            }else if (megasArchivo >= 190 && megasArchivo <= 219) {
                TransferenciaPaquetes.getInstancia().envio(megasTrasferir[3],ruta,output,clientSocket);
            }else if (megasArchivo >= 220 && megasArchivo <= 320) {                    
                TransferenciaPaquetes.getInstancia().envio(megasTrasferir[4],ruta,output,clientSocket);
            }else if (megasArchivo >= 321 && megasArchivo <= 390) {                    
                TransferenciaPaquetes.getInstancia().envio(megasTrasferir[5],ruta,output,clientSocket);
            }else if (megasArchivo >= 391 && megasArchivo <= 450) {                    
                TransferenciaPaquetes.getInstancia().envio(megasTrasferir[6],ruta,output,clientSocket);
            }else if (megasArchivo >= 451 && megasArchivo <= 949) {                    
                TransferenciaPaquetes.getInstancia().envio(megasTrasferir[7],ruta,output,clientSocket);
            }else if (megasArchivo >= 950 && megasArchivo <= 1020) {                    
                TransferenciaPaquetes.getInstancia().envio(megasTrasferir[8],ruta,output,clientSocket);
            }else{
                TransferenciaPaquetes.getInstancia().envio(megasTrasferir[9],ruta,output,clientSocket);           
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}