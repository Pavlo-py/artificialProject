package model;

/**
 * Representa un dato que ha pasado por el proceso de limpieza y validación.
 * Listo para ser consumido por el motor predictivo.
 */
public class HarmonizedData {
    private final double value; // El valor numérico limpio
    private final String category; // Ej: "Ventas", "Churn", "Tráfico"
    private final boolean isValid; // Flag de validación de calidad

    public HarmonizedData(double value, String category, boolean isValid) {
        this.value = value;
        this.category = category;
        this.isValid = isValid;
    }

    public double getValue() {
        return value;
    }

    public String getCategory() {
        return category;
    }

    public boolean isValid() {
        return isValid;
    }
}
