package hpi_project;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=== BIENVENIDO A ARTIFICIAL SOCIETY (v1.0) ===");
        System.out.println("Transformando datos en inteligencia predictiva... [cite: 199]");

        while (true) {
            System.out.println("\nSeleccione una acción:");
            System.out.println("1. Ingestar Datos (Simular CSV)");
            System.out.println("2. Armonizar Datos (Limpieza AI)");
            System.out.println("3. Ejecutar Predicción");
            System.out.println("4. Salir");

            int option = scanner.nextInt();

            switch (option) {
                case 1:
                    System.out.println("[Ingestion] Conectando a fuente de datos...");
                    break;
                case 2:
                    System.out.println("[Harmonization] Detectando anomalías y nulos...");
                    break;
                case 3:
                    System.out.println("[Intelligence] Entrenando modelo en GPU virtual...");
                    System.out.println(">> PREDICCIÓN: Las ventas aumentarán un 15% (Confianza: 85%)");
                    break;
                case 4:
                    System.out.println("Cerrando sesión...");
                    return;
                default:
                    System.out.println("Opción no válida.");
            }
        }
    }
}
