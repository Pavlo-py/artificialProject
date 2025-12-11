ARTIFICIAL SOCIETY PLATFORM
  Version: 2.0 Enterprise Edition Build: Stable Architecture: HPI (Harmonization-Prediction-Interaction)

1. Executive Summary
  The Artificial Society Platform (ASP) is a modular predictive analytics system designed to process heterogeneous data streams. The platform implements a complete data pipeline ranging from raw ingestion to the generation of persistent business reports. Its core value lies in the HPI Architecture, which decouples data ingestion mechanisms from business logic and predictive modeling, ensuring high cohesion and low coupling across modules.
  
  The system is engineered to handle "dirty data" environments through heuristic harmonization layers, transforming unstructured inputs into actionable financial and operational forecasts.

2. Technical Architecture
  The solution follows strict SOLID principles, utilizing interface-driven development to ensure extensibility. The codebase is organized into distinct functional layers:

2.1 Ingestion Layer
  Managed by the ingestion package. This module abstracts the data source origin.
  
  Abstraction: The IDataLoader interface defines the contract for data acquisition, allowing seamless integration with varying formats (CSV, SQL, API) without altering the core logic.
  
  Implementation: The current release includes CsvDataLoader, a robust implementation for flat file processing. It handles file I/O operations, header skipping, and raw record instantiation, providing fault tolerance against I/O exceptions.

2.2 Harmonization Layer (ETL)
  Managed by the harmonization package. Unlike standard ETL processes, this layer applies semantic rules to sanitize input.
  
  Rule-Based Logic: The RuleBasedHarmonizer implements sanitization algorithms that automatically strip currency symbols (€, $, EUR) and normalize decimal formats.
  
  Heuristic Recovery: The system utilizes Regex patterns to attempt recovery of numerical values from corrupted strings, categorizing data into Sales Revenue, Operational Metrics, or General Data based on source identifiers.

2.3 Analytics & Prediction Engine
  Managed by the analytics package. This layer operates exclusively on harmonized data.
  
  Strategy Pattern: The IPredictiveModel interface allows for hot-swapping of algorithms.
  
  Forecasting Logic: The SalesForecaster implementation projects future values based on a configurable growth factor. It calculates a Confidence Score derived from data magnitude and stochastic variance simulation.

2.4 Reporting & Persistence
  Managed by the reporting package.
  
  Report Generation: The ReportExporter class handles the serialization of analysis results to the local file system. Reports are timestamped (Analysis_Report_yyyyMMdd_HHmmss.txt) and include detailed breakdowns of source IDs, projected values, and confidence percentages.

3. System Configuration
  The platform utilizes a Singleton Pattern via the ConfigurationContext class to manage global application state. This ensures a single source of truth for runtime parameters.
  
  =====================================================================================================================
  Parameter             Type        Default      Description
  growthFactor          double      1.15         Determines the projection multiplier for the forecasting model.
  simulationDelayMs     int         200          Controls the latency simulation for real-time processing visualization.
  ======================================================================================================================

4. Deployment & Usage
  Prerequisites
  Java Development Kit (JDK) 11 or higher.
  
  Standard File System access for report generation.
  
  Compilation
  The project structure supports standard Java compilation. Ensure all packages are included in the classpath.
  
  ```bash
javac -d bin src/**/*.java
```
  
  Execution
  Execute the main entry point:

  ```bash
  java -cp bin main.Main
  ```
  
  Operational Workflow
  The system presents a state-driven CLI menu:
  
  Data Loading: Utilize Option 1 to ingest the dataset (e.g., mock_data.csv). The system validates the read operation via CsvDataLoader.
  
  Harmonization & Analysis: Utilize Option 3 to trigger the pipeline.
  
  Raw records are cleaned via RuleBasedHarmonizer.
  
  Valid records are processed by SalesForecaster.
  
  Results are displayed in real-time.
  
  Export: Post-analysis, the system prompts to save a detailed report to disk via ReportExporter.
  
  Configuration: Utilize Option 2 (or Option 4 depending on state) to modify the Global Growth Factor.

5. Directory Structure
   hpiProject/
├── analytics/         # Predictive modeling strategies
├── config/            # Global configuration (Singleton)
├── harmonization/     # Data cleaning and standardization logic
├── ingestion/         # Data loading implementations
├── main/              # Entry point and UI orchestration
├── model/             # Data Transfer Objects (DTOs)
└── reporting/         # Output generation and file writing
   
