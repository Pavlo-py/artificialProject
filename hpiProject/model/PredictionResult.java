package model;

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

    // Método formateado para mostrar en consola (CLI)
    public void printReport() {
        System.out.println("====== REPORTE DE PREDICCIÓN ======");
        System.out.println("Variable Objetivo: " + targetVariable);
        System.out.println("Modelo Utilizado : " + modelUsed);
        System.out.printf("Valor Predicho   : %.2f\n", predictedValue);
        System.out.printf("Confianza        : %.1f%%\n", confidenceScore * 100);
        System.out.println("===================================");
    }

    // Getters estándar
    public double getPredictedValue() {
        return predictedValue;
    }

    public double getConfidenceScore() {
        return confidenceScore;
    }
}
