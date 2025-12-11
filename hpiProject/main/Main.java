package main;

import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;

// Imports de la arquitectura HPI
import ingestion.IDataLoader;
import ingestion.CsvDataLoader;
import harmonization.IHarmonizer;
import harmonization.RuleBasedHarmonizer;
import analytics.IPredictiveModel;
import analytics.SalesForecaster;
import model.RawDataRecord;
import model.HarmonizedData;
import model.PredictionResult;

import config.ConfigurationContext;
import reporting.ReportExporter;

public class Main {

    public static void main(String[] args) {
        printHeader();
        Scanner scanner = new Scanner(System.in);
        ConfigurationContext config = ConfigurationContext.getInstance();
        ReportExporter reportExporter = new ReportExporter();

        // Inicialización de componentes
        String csvFilePath = "mock_data.csv";
        IDataLoader dataLoader = new CsvDataLoader();
        IHarmonizer harmonizer = new RuleBasedHarmonizer();
        IPredictiveModel aiModel = new SalesForecaster();

        List<RawDataRecord> loadedKnowledgeBase = null;
        List<PredictionResult> lastResults = null; // Para guardar reporte después

        boolean running = true;

        while (running) {
            boolean hasData = (loadedKnowledgeBase != null && !loadedKnowledgeBase.isEmpty());
            boolean hasResults = (lastResults != null && !lastResults.isEmpty());

            printDynamicMenu(hasData, hasResults);
            System.out.print("\n>> Select option: ");
            String input = scanner.nextLine().trim();

            if ("4".equals(input) || "salir".equalsIgnoreCase(input)) {
                // Opción Salir (puede variar según el estado, pero el 4 siempre es salir o
                // ajustes en este diseño simplificado)
                if (hasData) {
                    // Si hay datos, la opción 4 es Configuración, y 5 es Salir (ver
                    // printDynamicMenu)
                    // Espera... miremos printDynamicMenu abajo.
                    // Sin datos: [1] Cargar, [2] Config, [3] Salir
                    // Con datos: [1] Inspeccionar, [2] Re-Cargar, [3] Ejecutar, [4] Config, [5]
                    // Salir
                } else {
                    if (input.equals("3"))
                        running = false;
                }
            }

            // Lógica de navegación basada en estados
            if (!hasData) {
                switch (input) {
                    case "1":
                        simulateLoading("Connecting to Data Lake");
                        loadedKnowledgeBase = dataLoader.loadData(csvFilePath);
                        if (!loadedKnowledgeBase.isEmpty()) {
                            System.out
                                    .println(" [OK] Ingestion successful: " + loadedKnowledgeBase.size() + " records.");
                        } else {
                            System.out.println(" [!] No data found.");
                        }
                        break;
                    case "2": // Ajustes
                        openSettingsMenu(scanner, config);
                        break;
                    case "3":
                        running = false;
                        break;
                    default:
                        System.out.println(" [X] Invalid Option.");
                }
            } else {
                switch (input) {
                    case "1":
                        inspectData(loadedKnowledgeBase);
                        break;
                    case "2": // Reload
                        simulateLoading("Refreshing Cache");
                        loadedKnowledgeBase = dataLoader.loadData(csvFilePath);
                        // Limpiamos resultados anteriores pues los datos cambiaron
                        lastResults = null;
                        break;
                    case "3":
                        simulateLoading("Running AI Models");
                        lastResults = runAnalysisPipeline(loadedKnowledgeBase, harmonizer, aiModel, config);

                        // Proactive export offer
                        System.out.print("\n>> Do you want to save detailed report to disk? (y/n): ");
                        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
                            reportExporter.exportReport(loadedKnowledgeBase, lastResults);
                        }
                        break;
                    case "4":
                        openSettingsMenu(scanner, config);
                        break;
                    case "5":
                        running = false;
                        break;
                    default:
                        System.out.println(" [X] Invalid Option.");
                }
            }

            if (running) {
                waitForEnter(scanner);
            }
        }

        scanner.close();
        System.out.println("\n=== SESSION TERMINATED ===");
    }

    // --- MENÚS DINÁMICOS ---

    private static void printDynamicMenu(boolean hasData, boolean hasResults) {
        System.out.println("\n┌──────────────────────────────────────────┐");
        if (!hasData) {
            System.out.println("|  STATUS: WAITING FOR DATA                |");
            System.out.println("├──────────────────────────────────────────┤");
            System.out.println("|  [1] Load Data (Ingestion)               |");
            System.out.println("|  [2] Global Configuration (AI Params)    |");
            System.out.println("|  [3] Exit                                |");
        } else {
            System.out.println("|  STATUS: DATA IN MEMORY (" + (hasResults ? "ANALYZED" : "PENDING") + ")      |");
            System.out.println("├──────────────────────────────────────────┤");
            System.out.println("|  [1] Inspect Data (Raw View)             |");
            System.out.println("|  [2] Re-Load Data (Refresh)              |");
            System.out.println("|  [3] RUN AI ENGINE (Predict)             |");
            System.out.println("|  [4] Global Configuration (AI Params)    |");
            System.out.println("|  [5] Exit                                |");
        }
        System.out.println("└──────────────────────────────────────────┘");
    }

    private static void openSettingsMenu(Scanner scanner, ConfigurationContext config) {
        System.out.println("\n>>> SYSTEM CONFIGURATION <<<");
        System.out.println("Current Growth Factor: " + config.getGrowthFactor() + "x");
        System.out.print("Enter new factor (e.g., 1.5 for 50% growth), or ENTER to cancel: ");
        String val = scanner.nextLine().trim();
        if (!val.isEmpty()) {
            try {
                double newFactor = Double.parseDouble(val);
                config.setGrowthFactor(newFactor);
                System.out.println(" [OK] Configuration updated.");
            } catch (NumberFormatException e) {
                System.out.println(" [!] Invalid value.");
            }
        }
    }

    // --- LÓGICA CORE ---

    // Ahora retorna resultados para la exportación
    private static List<PredictionResult> runAnalysisPipeline(List<RawDataRecord> rawRecords, IHarmonizer harmonizer,
            IPredictiveModel aiModel, ConfigurationContext config) {
        List<PredictionResult> results = new ArrayList<>();
        int processed = 0;

        System.out.println("\n>>> Real-time Results:");
        System.out.println("----------------------------------------");

        for (RawDataRecord rawRecord : rawRecords) {
            HarmonizedData cleanData = harmonizer.harmonize(rawRecord);

            if (cleanData.isValid()) {
                PredictionResult prediction = aiModel.predict(cleanData);
                results.add(prediction);

                // Mostrar solo resumen por pantalla para no saturar
                System.out.printf(" > ID: %-10s | Predict: %8.2f | Conf: %4.1f%%\n",
                        rawRecord.getSourceId(), prediction.getPredictedValue(), prediction.getConfidenceScore() * 100);
                processed++;

                // Simular latencia de computación en modo 'Detailed'
                try {
                    Thread.sleep(50);
                } catch (Exception e) {
                }
            }
        }
        System.out.println("----------------------------------------");
        System.out.println("Total processed: " + processed);
        return results;
    }

    // --- UTILIDADES VISUALES ---

    private static void simulateLoading(String action) {
        System.out.print(action + " [");
        for (int i = 0; i < 20; i++) {
            System.out.print("=");
            try {
                Thread.sleep(60);
            } catch (InterruptedException e) {
            }
        }
        System.out.println("] 100%");
    }

    private static void inspectData(List<RawDataRecord> records) {
        System.out.println("\n>>> RAW DATA INSPECTOR (Top 5)");
        int count = 0;
        for (RawDataRecord r : records) {
            if (count >= 5)
                break;
            System.out.printf("#%d | ID: %-10s | RAW: %s\n", (count + 1), r.getSourceId(), r.getRawContent());
            count++;
        }
    }

    private static void waitForEnter(Scanner s) {
        System.out.println("\n(Press ENTER to continue)");
        s.nextLine();
    }

    // --- LEGACY (Mantener firma del header) ---
    private static void printHeader() {
        System.out.println("##################################################");
        System.out.println("#                                                #");
        System.out.println("#          ARTIFICIAL SOCIETY PLATFORM           #");
        System.out.println("#          v1.0 - Enterprise Edition             #");
        System.out.println("#                                                #");
        System.out.println("##################################################");
    }

}