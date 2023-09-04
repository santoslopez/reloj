/**
 * Clase ContentType.java que valida todos los tipos
 * de archivos que se validan en el header
 */
import java.util.ArrayList;

public class ContentType{
    // Estructura de datos para almacenar los tipos de archivos
    private static ArrayList<String> tiposArchivos = new ArrayList<String>();

    private static ContentType instancia;
    public static ContentType getInstancia(){
        if (instancia == null){
            instancia = new ContentType();
        }
        return instancia;
    }

    // Constructor de la clase
    public ContentType(){
        // agregamos los tipos de archivos
        tiposArchivos.add("text/html");
        tiposArchivos.add("text/css");
        tiposArchivos.add("application/javascript");
        tiposArchivos.add("image/jpeg");
        tiposArchivos.add("image/png");
        tiposArchivos.add("image/jpg");
        tiposArchivos.add("text/plain");
        tiposArchivos.add("image/gif");
        
    }

    public static String recuperarContentType(String archivo){
        if (archivo.endsWith(".html")){
            return tiposArchivos.get(0);
        }else if (archivo.endsWith(".css")){
            return tiposArchivos.get(1);
        }else if (archivo.endsWith(".js")){
            return tiposArchivos.get(2);
        }else if (archivo.endsWith(".jpeg")){
            return tiposArchivos.get(3);
        }else if (archivo.endsWith(".png")){
            return tiposArchivos.get(4);
        }else if (archivo.endsWith(".jpg")){
            return tiposArchivos.get(5);
        }else if (archivo.endsWith(".txt")){
            return tiposArchivos.get(6);
        }else if (archivo.endsWith(".gif")){
            return tiposArchivos.get(7);
        }else{
            return "No se encontró el tipo de archivo: "+archivo;
        }
    }

}