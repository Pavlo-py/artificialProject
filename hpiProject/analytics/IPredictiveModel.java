package analytics;

import model.HarmonizedData;
import model.PredictionResult;

/**
 * Interfaz que define el comportamiento de cualquier modelo predictivo en
 * Artificial Society.
 * Permite intercambiar algoritmos (Linear Regression, LSTM, Prophet) sin
 * afectar al sistema.
 */
public interface IPredictiveModel {
    /**
     * Genera una predicción basada en un dato armonizado.
     * 
     * @param data El dato limpio y validado.
     * @return Un objeto con el resultado de la predicción y la confianza.
     */
    PredictionResult predict(HarmonizedData data);
}