package model;

/**
 * Objeto de transferencia que contiene el resultado final del análisis
 * predictivo.
 */
public class PredictionResult {
    private final String targetVariable; // Qué estamos prediciendo (ej. "Ingresos Q3")
    private final double predictedValue; // El resultado numérico
    private final double confidenceScore; // Nivel de confianza (0.0 a 1.0)
    private final String modelUsed; // Algoritmo usado (ej. "ARIMA", "LinearReg")

    public PredictionResult(String targetVariable, double predictedValue, double confidenceScore, String modelUsed) {
        this.targetVariable = targetVariable;
        this.predictedValue = predictedValue;
        this.confidenceScore = confidenceScore;
        this.modelUsed = modelUsed;
    }

    // --- GETTERS (Necesarios para que el Main pueda leer los datos) ---

    public double getPredictedValue() {
        return predictedValue;
    }

    public double getConfidenceScore() {
        return confidenceScore;
    }

    // ¡ESTE ES EL QUE FALTABA!
    public String getModelName() {
        return modelUsed;
    }

    // Método opcional para reporte rápido
    public void printReport() {
        System.out.println("Variable: " + targetVariable);
        System.out.println("Valor: " + predictedValue);
    }
}