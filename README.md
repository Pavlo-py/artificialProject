# ARTIFICIAL SOCIETY PLATFORM

## 1. Executive Summary

**Artificial Society**  is a next-generation SaaS intelligence layer designed to bridge the "insight gap" between fragmented data repositories and strategic usability. Built upon the industry-standard CRISP-DM methodology , the platform leverages a modular HPI Architecture (Ingestion-Harmonization-Prediction) to democratize access to advanced analytics for both technical and non-technical stakeholders.

The system is engineered to function in high-friction environments, automating the transformation of heterogeneous and "dirty" data into high-fidelity forecasts and operational insights.By integrating AI-driven harmonization and advanced forecasting empowers organizations to transition from reactive reporting to proactive, AI-augmented decision-making

## 2. Technical Architecture

The solution follows strict **SOLID principles**, utilizing interface-driven development to ensure extensibility. The codebase is organized into distinct functional layers:

### 2.1 Ingestion Layer
Managed by the `ingestion` package. This module abstracts the data source origin.

* **Abstraction:** The `IDataLoader` interface defines the contract for data acquisition, allowing seamless integration with varying formats (CSV, SQL, API) without altering the core logic.
* **Implementation:** The current release includes `CsvDataLoader`, a robust implementation for flat file processing. It handles file I/O operations, header skipping, and raw record instantiation, providing fault tolerance against I/O exceptions.

### 2.2 Harmonization Layer (ETL)
Managed by the `harmonization` package. Unlike standard ETL processes, this layer applies semantic rules to sanitize input.

* **Rule-Based Logic:** The `RuleBasedHarmonizer` implements sanitization algorithms that automatically strip currency symbols (`€`, `$`, `EUR`) and normalize decimal formats.
* **Heuristic Recovery:** The system utilizes Regex patterns to attempt recovery of numerical values from corrupted strings, categorizing data into *Sales Revenue*, *Operational Metrics*, or *General Data* based on source identifiers.

### 2.3 Analytics & Prediction Engine
Managed by the `analytics` package. This layer operates exclusively on harmonized data.

* **Strategy Pattern:** The `IPredictiveModel` interface allows for hot-swapping of algorithms.
* **Forecasting Logic:** The `SalesForecaster` implementation projects future values based on a configurable growth factor. It calculates a *Confidence Score* derived from data magnitude and stochastic variance simulation.

### 2.4 Reporting & Persistence
Managed by the `reporting` package.

* **Report Generation:** The `ReportExporter` class handles the serialization of analysis results to the local file system. Reports are timestamped (`Analysis_Report_yyyyMMdd_HHmmss.txt`) and include detailed breakdowns of source IDs, projected values, and confidence percentages.

## 3. System Configuration

The platform utilizes a **Singleton Pattern** via the `ConfigurationContext` class to manage global application state. This ensures a single source of truth for runtime parameters.

| Parameter | Type | Default Value | Description |
| :--- | :--- | :--- | :--- |
| `growthFactor` | Double | `1.15` | Multiplicador de proyección de ventas. |
| `simulationDelayMs` | Integer | `200` | Tiempo de espera para efectos visuales (ms). |
| `sourcePath` | String | `mock_data.csv` | Ruta del archivo de ingesta de datos. |

## 4. Deployment & Usage

### Prerequisites
* Java Development Kit (JDK) 11 or higher.
* Standard File System access for report generation.

### Compilation
The project structure supports standard Java compilation. Ensure all packages are included in the classpath.

```bash
javac -d bin src/**/*.java
```

### Execution
Execute the main entry point:

```bash
java -cp bin main.Main
```
### Operational Workflow
The system presents a state-driven CLI menu designed to guide the operator through the HPI lifecycle:

1.  **Data Loading (Ingestion)**
    Utilize **Option 1** to ingest the dataset (e.g., `mock_data.csv`). The system validates the read operation and integrity via the `CsvDataLoader` component.

2.  **Harmonization & Analysis (Processing)**
    Utilize **Option 3** to trigger the intelligence pipeline:
    * **Cleaning:** Raw records are automatically sanitized via `RuleBasedHarmonizer`.
    * **Prediction:** Valid records are processed by the `SalesForecaster` engine.
    * **Visualization:** Results are displayed in real-time via the console interface.

3.  **Export (Persistence)**
    Post-analysis, the system proactively prompts to save a detailed report to disk using the `ReportExporter` module.

4.  **Configuration (Tuning)**
    Utilize **Option 2** (or **Option 4**, depending on state) to modify the *Global Growth Factor* in real-time.

## 5. Directory Structure
## 5. Directory Structure

```text
hpiProject/
├── analytics/          # Predictive modeling strategies (Strategy Pattern)
├── config/             # Global configuration & State (Singleton)
├── harmonization/      # Data cleaning & standardization logic
├── ingestion/          # Data loading implementations (Data Lake)
├── main/               # Entry point and UI orchestration (CLI)
├── model/              # Data Transfer Objects (DTOs)
└── reporting/          # Output generation and persistent reporting
```

