package analytics;

import model.HarmonizedData;
import model.PredictionResult;

/**
 * Modelo de predicción de ventas.
 * Simula un algoritmo de proyección de crecimiento (Forecasting).
 */
public class SalesForecaster implements IPredictiveModel {

    // Simulación de parámetros del modelo
    // Simulación de parámetros del modelo
    // La configuración ahora viene de ConfigurationContext
    // private static final double GROWTH_FACTOR = 1.15;

    @Override
    public PredictionResult predict(HarmonizedData data) {
        // 1. Validar que el modelo soporta este tipo de dato
        if (!data.isValid()) {
            return new PredictionResult("Unknown", 0.0, 0.0, "Error: Invalid Data");
        }

        // Obtener configuración global
        double growthFactor = config.ConfigurationContext.getInstance().getGrowthFactor();

        // 2. Lógica de Predicción (Simulación de IA)
        // Tomamos el valor actual y proyectamos el futuro
        double currentVal = data.getValue();
        double projectedVal = currentVal * growthFactor;

        // 3. Cálculo de confianza (Simulación con mayor variabilidad)
        // Simulamos que el modelo tiene variaciones basadas en "factores externos"
        // aleatorios
        // Genera una confianza base entre 0.70 y 0.95
        double randomFactor = 0.70 + (Math.random() * 0.25);

        // Ajuste por magnitud: valores extremos suelen tener menor confianza
        double magnitudeAdjustment = (currentVal > 10000) ? -0.05 : 0.02;

        double confidence = randomFactor + magnitudeAdjustment;

        // Clampear para asegurar que esté entre 0 y 1
        confidence = Math.max(0.0, Math.min(1.0, confidence));

        // 4. Retornar el resultado encapsulado
        return new PredictionResult(
                "Future_" + data.getCategory(), // Ej: Future_Sales_Revenue
                projectedVal,
                confidence,
                "Standard_Growth_Algorithm_v1");
    }
}