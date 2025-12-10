package harmonization;

import model.HarmonizedData;
import model.RawDataRecord;

/**
 * Implementación de limpieza basada en reglas.
 * Simula la eliminación de formatos inconsistentes y validación de calidad.
 */
public class RuleBasedHarmonizer implements IHarmonizer {

    @Override
    public HarmonizedData harmonize(RawDataRecord rawRecord) {
        // 1. Obtener el contenido sucio
        String rawContent = rawRecord.getRawContent();

        // 2. Validación rápida (Fail Fast)
        if (rawContent == null || rawContent.trim().isEmpty()) {
            System.out.println("[Harmonizer] Alerta: Dato vacío recibido de " + rawRecord.getSourceId());
            return new HarmonizedData(0.0, "Unknown", false);
        }

        // 3. Limpieza (Simulando "Data Cleansing")
        // Quitamos símbolos de moneda y espacios que puedan romper el parseo
        String cleanedContent = rawContent.trim()
                .replace("€", "")
                .replace("$", "")
                .replace("EUR", "")
                .replace(",", ".") // Normalizar decimales a punto
                .replaceAll("\\s+", ""); // Quitar todos los espacios

        // 4. Transformación y Categorización
        try {
            double value = Double.parseDouble(cleanedContent);

            // Simulamos que la IA detecta la categoría según el ID de la fuente
            String category = detectCategory(rawRecord.getSourceId());

            // Retornamos el dato limpio y marcado como válido (true)
            return new HarmonizedData(value, category, true);

        } catch (NumberFormatException e) {
            // Si falla al convertir a número, no rompemos el programa.
            // Retornamos un dato inválido para que el sistema lo ignore o reporte.
            return new HarmonizedData(0.0, "Format_Error", false);
        }
    }

    /**
     * Método auxiliar pequeño para determinar la categoría.
     * (Clean Code: Single Responsibility Principle - extraer lógica auxiliar).
     */
    private String detectCategory(String sourceId) {
        String sourceLower = sourceId.toLowerCase();

        if (sourceLower.contains("crm")) {
            return "Sales_Revenue";
        } else if (sourceLower.contains("sensor") || sourceLower.contains("iot")) {
            return "Operational_Metric";
        } else {
            return "General_Data";
        }
    }
}
