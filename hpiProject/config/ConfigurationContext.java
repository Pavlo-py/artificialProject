package config;

/**
 * Singleton para gestionar la configuración global de la aplicación.
 * Permite ajustar parámetros del modelo en tiempo real.
 */
public class ConfigurationContext {
    private static ConfigurationContext instance;

    // Parámetros de configuración (con valores por defecto)
    private double growthFactor = 1.15; // +15% por defecto
    private int simulationDelayMs = 200; // Velocidad de "efecto visual"

    // Constructor privado para Singleton
    private ConfigurationContext() {
    }

    public static synchronized ConfigurationContext getInstance() {
        if (instance == null) {
            instance = new ConfigurationContext();
        }
        return instance;
    }

    // --- Getters & Setters ---

    public double getGrowthFactor() {
        return growthFactor;
    }

    public void setGrowthFactor(double growthFactor) {
        // Validación básica
        if (growthFactor < 0.0) {
            System.out.println("[Config] Error: El factor no puede ser negativo.");
            return;
        }
        this.growthFactor = growthFactor;
        System.out.println("[Config] Nuevo factor de crecimiento establecido: " + growthFactor + "x");
    }

    public int getSimulationDelayMs() {
        return simulationDelayMs;
    }

    public void setSimulationDelayMs(int delay) {
        this.simulationDelayMs = delay;
    }
}
