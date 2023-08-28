import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.IOException;;
import java.net.ServerSocket;
import java.util.Scanner;
import java.util.InputMismatchException;
import java.io.BufferedReader;
import java.io.FileReader;

// para almacenar valores en el archivo
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.io.PrintWriter;

public class MenuPrincipal{

    // declarar el numero de puerto por defecto
    private static final int numeroPuerto = 1850;
    public int getNumeroPuerto(){
        return numeroPuerto;
    }

    //private static final ExecutorService pool;
    private static ServerSocket serverSocket;
    
    // Scanner para pedir datos
    private static Scanner scanner = new Scanner(System.in);

   public static void menu() {
        System.out.println("Bienvenido al servidor de archivos");
        System.out.println("Seleccione una opcion");
        System.out.println("1. Configurar puerto");
        System.out.println("2. Configurar numero threads");
        System.out.println("3. Revisar configuración de puertos y hilos");
        System.out.println("4. Iniciar servidor");
        System.out.println("5. Salir");

        int numeroOpcion = 0;
        boolean entradaValida = false;

        do {
            try {
                numeroOpcion = scanner.nextInt();
                if (numeroOpcion >= 1 && numeroOpcion <= 5) {
                    entradaValida = true;
                } else {
                    System.out.println("Opción inválida. Ingrese un número del 1 al 5.");
                }
            } catch (InputMismatchException ime) {
                System.out.println("El argumento no es un número. Ingrese un número del 1 al 5.");
                scanner.nextLine(); // Limpiar el búfer de entrada
            }
        } while (!entradaValida);

        // Realizar acciones según la opción seleccionada...
        if(numeroOpcion==1){
            System.out.println("Ingrese el numero de puerto");
            int numeroPuertoAlmacenar = scanner.nextInt();
            System.out.println("El numero de puerto es: "+numeroPuerto);
            ValidarArchivoTexto.getInstancia().validarArchivo("puerto",numeroPuertoAlmacenar,"puerto.txt","Número de puerto guardado en el archivo.","escritura");
            menu();
        }else  if(numeroOpcion==2){
            System.out.println("Ingrese el numero de hilos");
            int numeroHilosIngresado = scanner.nextInt();

            // averiguar numero de hilosd de la computadora
            int hilosDisponibles = Runtime.getRuntime().availableProcessors();
            if((numeroHilosIngresado>0) && (numeroHilosIngresado<=hilosDisponibles)){
            
                System.out.println("El numero de hilos ingresado es: "+numeroHilosIngresado);
            
                ValidarArchivoTexto.getInstancia().validarArchivo("hilos",numeroHilosIngresado,"hilos.txt","Número de hilos almacenado en el archivo.","escritura");
                menu();
            }else{
                System.out.println("Número de hilos de la computadora: "+hilosDisponibles); 
                System.out.println("No se guardo la cantidad de hilos. Hilos invalido");
                menu();
            }
        }else  if(numeroOpcion==3){
            System.out.println("Revisando configuración de puertos y threads");
            int puertoAlmacenado = ValidarArchivoTexto.getInstancia().getPrimeraFilaValorArchivo("puerto.txt"); 
            int hiloAlmacenado = ValidarArchivoTexto.getInstancia().getPrimeraFilaValorArchivo("hilos.txt");
            System.out.println("Número de puerto: "+puertoAlmacenado);
            System.out.println("Número de hilos: "+hiloAlmacenado);
            //ValidarArchivoTexto.().mostrarContenidoArchivo("hilos.txt");
            menu();
        }else  if(numeroOpcion==4){
            System.out.println("Iniciando servidor");
            Servidor server = new Servidor();
            server.iniciar();
        }else  if(numeroOpcion==5){
            System.out.println("Saliendo del programa.");
        }
    }

    public static void main(String[] args){
        menu();
    }
}