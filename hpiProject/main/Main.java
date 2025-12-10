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
            System.out.print("\n>> Seleccione opción: ");
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
                        simulateLoading("Conectando con Data Lake");
                        loadedKnowledgeBase = dataLoader.loadData(csvFilePath);
                        if (!loadedKnowledgeBase.isEmpty()) {
                            System.out.println(" [OK] Ingesta exitosa: " + loadedKnowledgeBase.size() + " registros.");
                        } else {
                            System.out.println(" [!] No se encontraron datos.");
                        }
                        break;
                    case "2": // Ajustes
                        openSettingsMenu(scanner, config);
                        break;
                    case "3":
                        running = false;
                        break;
                    default:
                        System.out.println(" [X] Opción inválida.");
                }
            } else {
                switch (input) {
                    case "1":
                        inspectData(loadedKnowledgeBase);
                        break;
                    case "2": // Recargar
                        simulateLoading("Refrescando caché");
                        loadedKnowledgeBase = dataLoader.loadData(csvFilePath);
                        // Limpiamos resultados anteriores pues los datos cambiaron
                        lastResults = null;
                        break;
                    case "3":
                        simulateLoading("Ejecutando Modelos de IA");
                        lastResults = runAnalysisPipeline(loadedKnowledgeBase, harmonizer, aiModel, config);

                        // Oferta proactiva de exportación
                        System.out.print("\n>> ¿Desea guardar reporte detallado en disco? (y/n): ");
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

    // --- MENÚS DINÁMICOS ---

    private static void printDynamicMenu(boolean hasData, boolean hasResults) {
        System.out.println("\n┌──────────────────────────────────────────┐");
        if (!hasData) {
            System.out.println("|  ESTADO: ESPERANDO DATOS                 |");
            System.out.println("├──────────────────────────────────────────┤");
            System.out.println("|  [1] Cargar Datos (Ingestion)            |");
            System.out.println("|  [2] Configuración Global (IA Params)    |");
            System.out.println("|  [3] Salir                               |");
        } else {
            System.out.println("|  ESTADO: DATOS EN MEMORIA (" + (hasResults ? "ANALIZADO" : "PENDIENTE") + ")   |");
            System.out.println("├──────────────────────────────────────────┤");
            System.out.println("|  [1] Inspeccionar Datos (Raw View)       |");
            System.out.println("|  [2] Re-Cargar Datos (Refresh)           |");
            System.out.println("|  [3] EJECUTAR MOTOR IA (Predict)         |");
            System.out.println("|  [4] Configuración Global (IA Params)    |");
            System.out.println("|  [5] Salir                               |");
        }
        System.out.println("└──────────────────────────────────────────┘");
    }

    private static void openSettingsMenu(Scanner scanner, ConfigurationContext config) {
        System.out.println("\n>>> CONFIGURACIÓN DE SISTEMA <<<");
        System.out.println("Growth Factor Actual: " + config.getGrowthFactor() + "x");
        System.out.print("Ingrese nuevo factor (ej. 1.5 para 50% crecimiento), o ENTER para cancelar: ");
        String val = scanner.nextLine().trim();
        if (!val.isEmpty()) {
            try {
                double newFactor = Double.parseDouble(val);
                config.setGrowthFactor(newFactor);
                System.out.println(" [OK] Configuración actualizada.");
            } catch (NumberFormatException e) {
                System.out.println(" [!] Valor inválido.");
            }
        }
    }

    // --- LÓGICA CORE ---

    // Ahora retorna resultados para la exportación
    private static List<PredictionResult> runAnalysisPipeline(List<RawDataRecord> rawRecords, IHarmonizer harmonizer,
            IPredictiveModel aiModel, ConfigurationContext config) {
        List<PredictionResult> results = new ArrayList<>();
        int processed = 0;

        System.out.println("\n>>> Resultados en tiempo real:");
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
        System.out.println("Total procesados: " + processed);
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
        System.out.println("\n>>> INSPECTOR DE DATOS CRUDOS (Top 5)");
        int count = 0;
        for (RawDataRecord r : records) {
            if (count >= 5)
                break;
            System.out.printf("#%d | ID: %-10s | RAW: %s\n", (count + 1), r.getSourceId(), r.getRawContent());
            count++;
        }
    }

    private static void waitForEnter(Scanner s) {
        System.out.println("\n(Presione ENTER para continuar)");
        s.nextLine();
    }

    // --- LEGACY (Mantener firma del header) ---
    private static void printHeader() {
        System.out.println("##################################################");
        System.out.println("#                                                #");
        System.out.println("#          ARTIFICIAL SOCIETY PLATFORM           #");
        System.out.println("#          v2.0 - Enterprise Edition             #");
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