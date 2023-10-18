import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
//import java.io.PrintWriter;

public class ValidarArchivoTexto{
    private static ValidarArchivoTexto instancia;
    public static ValidarArchivoTexto getInstancia(){
        if(instancia==null){
            instancia = new ValidarArchivoTexto();
        }
        return instancia;
    }

    public static void validarArchivo(String identificador,int valor,String nombreArchivo,String mensaje,String tipoAccion){
        try{
            if(tipoAccion=="lectura"){
                System.out.println("Mostrando el contenido del archivo");

            }else if(tipoAccion=="escritura"){
                File archivo = new File(nombreArchivo);
                
                try (FileWriter writer = new FileWriter(nombreArchivo,false)) {
                    if (!archivo.exists()) {
                        System.out.println("El archivo no existe. Se ha creado automaticamente");
                        archivo.createNewFile();
                    }
                    System.out.println("El archivo se ha sobreescrito correctamente.");
                    //writer.write(identificador+":"+valor);
                    writer.write(String.valueOf(valor));
                    System.out.println(mensaje);
                } catch (IOException e) {
                    System.out.println("Error al escribir en el archivo: "+e.getMessage());
                }
            }else{
                System.out.println("No se reconoce el tipo de acción");
            }


        }catch(SecurityException exc){

        }
    }
    
    public int getPrimeraFilaValorArchivo(String nombreArchivo){
        String primeraLineaHilos="";
        try{
            // Se recupera el numero de hilos del archivo de configuración
            BufferedReader br = new BufferedReader(new FileReader(nombreArchivo));
            primeraLineaHilos = br.readLine();     
        }catch(Exception e){
            System.out.println("Error al iniciar el servidor: "+e.getMessage());
        }
        // convertimos el numero recuperado en entero
        int numeroHilosArchivoTexto = Integer.parseInt(primeraLineaHilos);
        return numeroHilosArchivoTexto;
    }

}