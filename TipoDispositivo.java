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
    public static void envioPaquetesPorDispositivo(boolean esDispositivoMovil,String ruta,OutputStream output, Socket clientSocket){
      
        try{
            //Array de 10 elementos
            //int[] megasTrasferir;   
            int megasTrasferir = ValidarArchivoTexto.getInstancia().getPrimeraFilaValorArchivo("megasTransferir.txt"); 


            if(esDispositivoMovil){
                System.out.println("Es un dispositivo movil");
                //megasTrasferir = new int[]{5};
            }else{
                //megasTrasferir = new int[]{5};
            }
            TransferenciaPaquetes.getInstancia().envio(megasTrasferir,ruta,output,clientSocket);

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}