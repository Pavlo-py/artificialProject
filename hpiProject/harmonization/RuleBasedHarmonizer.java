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

        // 2. Validación rápida con recuperación por defecto
        if (rawContent == null || rawContent.trim().isEmpty()) {
            return new HarmonizedData(0.0, "Missing_Value", true); // No lo invalidamos, lo marcamos como 0
        }

        // 3. Limpieza preliminar
        String cleanedContent = rawContent.trim()
                .replace("€", "")
                .replace("$", "")
                .replace("EUR", "")
                .replace(",", ".")
                .replaceAll("\\s+", "");

        // 4. Transformación Inteligente
        double value;
        String category = detectCategory(rawRecord.getSourceId());

        try {
            // Intento A: Parseo directo
            value = Double.parseDouble(cleanedContent);

        } catch (NumberFormatException e) {
            // Intento B: Recuperación por Regex (Buscar cualquier patrón numérico)
            try {
                java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("(\\d+(\\.\\d+)?)")
                        .matcher(rawContent);
                if (matcher.find()) {
                    value = Double.parseDouble(matcher.group(1));
                    category = category + "_Recovered"; // Marcamos que fue recuperado
                } else {
                    // Fallo total: Asignar 0 por defecto
                    value = 0.0;
                    category = "Unreadable_Data";
                }
            } catch (Exception ex) {
                value = 0.0;
                category = "Error_Recovery_Failed";
            }
        }

        // Retornamos SIEMPRE un dato válido (isValid = true) para que entre al flujo,
        // confiando en que la categoría alertará si es un dato raro.
        return new HarmonizedData(value, category, true);
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
