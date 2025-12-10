package analytics;

import model.HarmonizedData;
import model.PredictionResult;

/**
 * Modelo de predicción de ventas.
 * Simula un algoritmo de proyección de crecimiento (Forecasting).
 */
public class SalesForecaster implements IPredictiveModel {

    // Simulación de parámetros del modelo
    private static final double GROWTH_FACTOR = 1.15; // Proyecta un crecimiento del 15%
    private static final double BASE_CONFIDENCE = 0.85; // 85% de confianza base

    @Override
    public PredictionResult predict(HarmonizedData data) {
        // 1. Validar que el modelo soporta este tipo de dato
        if (!data.isValid()) {
            return new PredictionResult("Unknown", 0.0, 0.0, "Error: Invalid Data");
        }

        // 2. Lógica de Predicción (Simulación de IA)
        // Tomamos el valor actual y proyectamos el futuro
        double currentVal = data.getValue();
        double projectedVal = currentVal * GROWTH_FACTOR;

        // 3. Cálculo de confianza (Simulación)
        // Si el valor es muy alto, la confianza baja ligeramente (incertidumbre de
        // mercado)
        double confidence = (currentVal > 10000) ? BASE_CONFIDENCE - 0.05 : BASE_CONFIDENCE;

        // 4. Retornar el resultado encapsulado
        return new PredictionResult(
                "Future_" + data.getCategory(), // Ej: Future_Sales_Revenue
                projectedVal,
                confidence,
                "Standard_Growth_Algorithm_v1");
    }
}