package ingestion;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import model.RawDataRecord;

/**
 * Implementación concreta para cargar datos desde archivos CSV.
 * Simula los "Automated connectors" descritos en la arquitectura del sistema.
 */
public class CsvDataLoader implements IDataLoader {

    @Override
    public List<RawDataRecord> loadData(String filePath) {
        List<RawDataRecord> records = new ArrayList<>();

        System.out.println("[Ingestion] Iniciando lectura de archivo: " + filePath);

        // Try-with-resources para asegurar que el archivo se cierra automáticamente
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isHeader = true;

            while ((line = br.readLine()) != null) {
                // Omitir la cabecera del CSV
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                // Parsear la línea (Separar por comas)
                // Nota: En un sistema real usaríamos una librería robusta,
                // para P3 un split simple es suficiente y demuestra lógica básica.
                String[] values = line.split(",");

                if (values.length >= 3) {
                    String sourceId = values[0].trim();
                    // El valor crudo está en la posición 2 (índice 2)
                    // Nota: Si el CSV tiene comas dentro del valor (ej: "1,200"), este split simple
                    // fallaría,
                    // pero asumimos el formato del mock_data.csv para este ejercicio.
                    String rawValue = values[2].trim();

                    // Crear el registro inmutable
                    records.add(new RawDataRecord(sourceId, rawValue));
                }
            }
            System.out.println("[Ingestion] Lectura completada. Registros cargados: " + records.size());

        } catch (IOException e) {
            System.err.println("[Ingestion Error] No se pudo leer el archivo: " + e.getMessage());
        }

        return records;
    }
}