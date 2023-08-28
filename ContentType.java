/**
 * Clase ContentType.java que valida todos los tipos
 * de archivos que se validan en el header
 */
import java.util.ArrayList;

public class ContentType{
    // Estructura de datos para almacenar los tipos de archivos
    private static ArrayList<String> tiposArchivos = new ArrayList<String>();

    // metodo para recuperar el ArrayList
    /*private ArrayList<String> getTiposArchivos(){
        return tiposArchivos;
    }

    public String getContentTypePorIndice(int indice){
        if ((indice>=1) && (indice<=tiposArchivos.size())){
            return tiposArchivos.get(indice);
        }else{
            return "El indice está fuera de rango";
        }
    }*/

    // Constructor de la clase
    public ContentType(){
        // agregamos los tipos de archivos
        tiposArchivos.add("text/html");
        tiposArchivos.add("text/css");
        tiposArchivos.add("application/javascript");
        tiposArchivos.add("image/jpeg");
        tiposArchivos.add("image/png");
        tiposArchivos.add("image/jpg");
        tiposArchivos.add("image/gif");
    }

    public static String recuperarContentType(String archivo){
        if (archivo.endsWith(".html")){
            return tiposArchivos.get(0);
        }else if (archivo.endsWith(".css")){
            return tiposArchivos.get(1);
        }else if (archivo.endsWith(".javascript")){
            return tiposArchivos.get(2);
        }else if (archivo.endsWith(".jpeg")){
            return tiposArchivos.get(3);
        }else if (archivo.endsWith(".png")){
            return tiposArchivos.get(4);
        }else if (archivo.endsWith(".jpg")){
            return tiposArchivos.get(5);
        }else{
            return "No se encontró el tipo de archivo: "+archivo;
        }
    }

}