package ingestion;

import java.util.List;
import model.RawDataRecord;

/**
 * Contrato para cualquier cargador de datos (CSV, SQL, API).
 * Permite cambiar la fuente de datos sin romper la aplicación principal.
 */
public interface IDataLoader {
    List<RawDataRecord> loadData(String sourcePath);
}