package main;

import java.util.List;
import java.util.Scanner;

// Importamos nuestras interfaces y clases (Clean Code: Dependencias claras)
import ingestion.IDataLoader;
import ingestion.CsvDataLoader;
import harmonization.IHarmonizer;
import harmonization.RuleBasedHarmonizer;
import analytics.IPredictiveModel;
import analytics.SalesForecaster;
import model.RawDataRecord;
import model.HarmonizedData;
import model.PredictionResult;

public class Main {

    public static void main(String[] args) {
        printHeader();
        Scanner scanner = new Scanner(System.in);

        // Dependencias
        String csvFilePath = "mock_data.csv";
        IDataLoader dataLoader = new CsvDataLoader();
        IHarmonizer harmonizer = new RuleBasedHarmonizer();
        IPredictiveModel aiModel = new SalesForecaster();

        // Estado del sistema
        List<RawDataRecord> loadedKnowledgeBase = null;
        boolean running = true;

        while (running) {
            boolean hasData = (loadedKnowledgeBase != null && !loadedKnowledgeBase.isEmpty());

            printDynamicMenu(hasData);
            System.out.print("\n>> Seleccione opción: ");
            String input = scanner.nextLine().trim();

            if (!hasData) {
                // --- ESTADO: SIN DATOS ---
                switch (input) {
                    case "1":
                        System.out.println("\n[...] Conectando con fuente de datos...");
                        loadedKnowledgeBase = dataLoader.loadData(csvFilePath);
                        if (!loadedKnowledgeBase.isEmpty()) {
                            System.out
                                    .println(" [OK] Ingesta completada: " + loadedKnowledgeBase.size() + " registros.");
                        } else {
                            System.out.println(" [!] Advertencia: La fuente de datos está vacía.");
                        }
                        break;
                    case "2":
                        running = false;
                        break;
                    default:
                        System.out.println(" [X] Opción inválida.");
                }
            } else {
                // --- ESTADO: DATOS CARGADOS ---
                switch (input) {
                    case "1":
                        inspectData(loadedKnowledgeBase);
                        break;
                    case "2":
                        System.out.println("\n[...] Recargando datos...");
                        loadedKnowledgeBase = dataLoader.loadData(csvFilePath);
                        System.out.println(" [OK] Base de datos actualizada.");
                        break;
                    case "3":
                        runAnalysisPipeline(loadedKnowledgeBase, harmonizer, aiModel);
                        break;
                    case "4":
                        running = false;
                        break;
                    default:
                        System.out.println(" [X] Opción inválida.");
                }
            }

            if (running) {
                waitForEnter(scanner);
            }
        }

        scanner.close();
        System.out.println("\n=== SESIÓN FINALIZADA ===");
    }

    private static void printDynamicMenu(boolean hasData) {
        System.out.println("\n┌──────────────────────────────────────────┐");
        if (!hasData) {
            System.out.println("|  ESTADO: ESPERANDO DATOS                 |");
            System.out.println("├──────────────────────────────────────────┤");
            System.out.println("|  [1] Cargar Datos (Ingestion)            |");
            System.out.println("|  [2] Salir                               |");
        } else {
            System.out.println("|  ESTADO: DATOS EN MEMORIA                |");
            System.out.println("├──────────────────────────────────────────┤");
            System.out.println("|  [1] Inspeccionar Datos (Raw View)       |");
            System.out.println("|  [2] Re-Cargar Datos (Refresh)           |");
            System.out.println("|  [3] EJECUTAR MOTOR IA (Predict)         |");
            System.out.println("|  [4] Salir                               |");
        }
        System.out.println("└──────────────────────────────────────────┘");
    }

    private static void inspectData(List<RawDataRecord> records) {
        System.out.println("\n>>> INSPECTOR DE DATOS CRUDOS (Top 5)");
        System.out.println("----------------------------------------");
        int count = 0;
        for (RawDataRecord r : records) {
            if (count >= 5)
                break;
            System.out.printf("#%d | ID: %-10s | RAW: %s\n", (count + 1), r.getSourceId(), r.getRawContent());
            count++;
        }
        System.out.println("----------------------------------------");
        System.out.println("Total registros: " + records.size());
    }

    private static void waitForEnter(Scanner s) {
        System.out.println("\n(Presione ENTER para continuar)");
        s.nextLine();
    }

    private static void runAnalysisPipeline(List<RawDataRecord> rawRecords, IHarmonizer harmonizer,
            IPredictiveModel aiModel) {
        System.out.println("\n>>> INICIANDO PIPELINE DE ANÁLISIS...");

        int processed = 0;
        int errors = 0;

        for (RawDataRecord rawRecord : rawRecords) {
            // STEP 1: Harmonize
            HarmonizedData cleanData = harmonizer.harmonize(rawRecord);

            // STEP 2: Predict
            // Nota: Ahora SIEMPRE procesamos, porque el harmonizer recupera todo.
            // Pero podríamos filtrar si la categoría es "Unreadable_Data" si quisiéramos
            // ser estrictos.
            if (cleanData.isValid()) {
                PredictionResult prediction = aiModel.predict(cleanData);
                printResultInBox(rawRecord.getSourceId(), cleanData, prediction);
                processed++;
            } else {
                // Este bloque teóricamente ya no se alcanza con la nueva lógica,
                // pero lo dejamos por seguridad defensiva.
                System.out.println(" [SKIP] " + rawRecord.getSourceId() + ": " + cleanData.getCategory());
                errors++;
            }
        }
        System.out.println("\n>>> REPORTE: " + processed + " procesados | " + errors + " errores.");
    }

    // --- MÉTODOS AUXILIARES PARA LA CLI (Clean Code: Separar la lógica de la
    // vista) ---

    private static void printHeader() {
        System.out.println("##################################################");
        System.out.println("#                                                #");
        System.out.println("#          ARTIFICIAL SOCIETY PLATFORM           #");
        System.out.println("#          v1.0 - Predictive CLI Engine          #");
        System.out.println("#                                                #");
        System.out.println("##################################################");
    }

    private static void printResultInBox(String source, HarmonizedData data, PredictionResult result) {
        System.out.println("--------------------------------------------------");
        System.out.println("    ANÁLISIS GENERADO PARA: " + source);
        System.out.println("   > Dato Original Limpio: " + data.getValue());
        System.out.println("   > Categoría Detectada : " + data.getCategory());
        System.out.println("   -----------------------------------------------");
        System.out.println("   PREDICCIÓN IA (" + result.getModelName() + ")");
        System.out.printf("   > Proyección Futura   : %.2f\n", result.getPredictedValue());
        System.out.printf("   > Nivel de Confianza  : %.1f%%\n", result.getConfidenceScore() * 100);
        System.out.println("--------------------------------------------------\n");
    }
}