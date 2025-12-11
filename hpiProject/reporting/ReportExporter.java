package reporting;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import model.PredictionResult;
import model.RawDataRecord;

public class ReportExporter {

    public void exportReport(List<RawDataRecord> rawData, List<PredictionResult> predictions) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = "Analysis_Report_" + timestamp + ".txt";

        System.out.println("\n>>> Generating report on disk: " + filename + "...");

        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Cabecera del reporte
            writer.println("==================================================");
            writer.println("          ARTIFICIAL SOCIETY - REPORT");
            writer.println("          Generated: " + LocalDateTime.now());
            writer.println("==================================================");
            writer.println("");

            int count = Math.min(rawData.size(), predictions.size());

            for (int i = 0; i < count; i++) {
                RawDataRecord raw = rawData.get(i);
                PredictionResult pred = predictions.get(i);

                writer.println("RECORD ID: " + raw.getSourceId());
                writer.println(" - Target: " + pred.getModelName()); // O el campo que convenga
                writer.println(" - Projected Value: " + String.format("%.2f", pred.getPredictedValue()));
                writer.println(" - Confidence: " + String.format("%.1f%%", pred.getConfidenceScore() * 100));
                writer.println("--------------------------------------------------");
            }

            writer.println("\n[END OF REPORT]");
            System.out.println(">>> [OK] Report saved successfully.");

        } catch (IOException e) {
            System.err.println(">>> [ERROR] Failed to write report: " + e.getMessage());
        }
    }
}
