package model;

import java.time.LocalDateTime;

public class RawDataRecord {
    private final String sourceId; // Ej: "CRM_001", "IoT_Sensor_X"
    private final String rawContent; // El dato sin procesar (puede ser texto sucio)
    private final LocalDateTime ingestionTimestamp;

    public RawDataRecord(String sourceId, String rawContent) {
        this.sourceId = sourceId;
        this.rawContent = rawContent;
        this.ingestionTimestamp = LocalDateTime.now();
    }

    public String getSourceId() {
        return sourceId;
    }

    public String getRawContent() {
        return rawContent;
    }

    public LocalDateTime getIngestionTimestamp() {
        return ingestionTimestamp;
    }

    @Override
    public String toString() {
        return "RawDataRecord{source='" + sourceId + "', content='" + rawContent + "'}";
    }
}
