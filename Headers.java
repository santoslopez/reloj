/**
 * Clase Headers.java:
 * Aqui se encuentran los headers que se utilizarán en el servidor.
 * Aqui se estable el cache-control, el content-type, el content-disposition
 */

public class Headers{
    
    public static String getHeader200(String contentType){
        String header200 = "HTTP/1.1 200 OK\r\n" +
            "Content-Type: " + contentType + "\r\n" +
            // tratar de tener una conexion persistente
            "Connection: Keep-Alive\r\n"+
            "Keep-Alive: timeout=60, max=20\r\n"+
            "Cache-Control: max-age=60, public\r\n" + // 1 minuto
            "Content-Disposition: inline\r\n\r\n"; // Visualización en el navegador
            //"Content-Length: " + fileData.length + "\r\n\r\n";               
        return header200;
    }
    
    public static String getHeader404(){
        String header404 = "HTTP/1.1 404 Not Found\r\n\r\n";
        return header404;
    }
}