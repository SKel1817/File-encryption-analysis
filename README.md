# File Encryption Analysis

A comprehensive tool for analyzing and evaluating different encryption algorithms to establish benchmarks and standards for secure file storage.

![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)
![Java](https://img.shields.io/badge/Java-11+-orange.svg)
![License](https://img.shields.io/badge/license-MIT-green.svg)

## Overview

This project provides a robust framework for analyzing various encryption algorithms for file storage, highlighting their strengths and weaknesses. It evaluates key performance metrics like speed, security features, and resource consumption to help establish operational standards for file encryption.

The analysis includes popular encryption algorithms:
- AES (Advanced Encryption Standard)
- DES (Data Encryption Standard)
- TDES (Triple DES)
- RSA (Rivest–Shamir–Adleman)
- ChaCha20
- Blowfish
- PBE (Password-Based Encryption)

## Features

- **Intuitive GUI Interface**: User-friendly graphical interface for selecting files and viewing analysis results
- **Comprehensive Algorithm Evaluation**: Tests and compares multiple cryptographic algorithms
- **Performance Metrics**:
  - **Encryption Speed**: Measures time taken to encrypt data
  - **Throughput Analysis**: Calculates data processing speed in MB/s
  - **Avalanche Effect**: Evaluates how small changes in plaintext affect ciphertext
  - **Entropy Analysis**: Measures randomness in encrypted output using Shannon entropy
  - **Key Strength**: Evaluates key length and security implications
- **Visual Data Representation**: Charts and graphs for easy comparison
- **Detailed Reports**: Generates comprehensive reports saved to the results.txt file
- **System Resource Monitoring**: Tracks CPU and memory usage during encryption operations
- **Extensible Architecture**: Easily add new encryption algorithms by implementing the `EncryptionAlgorithm` interface

## Project Structure

```
File-encryption-anaylsis/
├── src/main/java/                  # Source code
│   ├── EncryptionAlgorithm.java    # Interface for all encryption algorithms
│   ├── EncryptionAnalysisGUI.java  # GUI implementation
│   ├── AlgorithmEvaluator.java     # Performance evaluation utilities
│   ├── FileImporter.java           # File selection utilities
│   ├── Main.java                   # Application entry point
│   ├── AES.java                    # AES implementation
│   ├── DES.java                    # DES implementation
│   ├── TDES.java                   # Triple DES implementation
│   ├── RSA.java                    # RSA implementation
│   ├── ChaCha20.java               # ChaCha20 implementation
│   ├── Blowfish.java               # Blowfish implementation
│   └── PBEEncryption.java          # Password-based encryption
├── build/                          # Build outputs
├── previous tests/                 # Archive of previous test results
├── build.gradle                    # Gradle build configuration
├── results.txt                     # Latest test results
└── README.md                       # Project documentation
```

## How It Works

1. **File Selection**: The GUI allows users to select a file for encryption analysis using a file chooser.
2. **Algorithm Testing**: Each encryption algorithm is tested for:
   - Speed and throughput
   - Avalanche effect (bit change sensitivity)
   - Entropy and randomness
   - Key length security
3. **Results Visualization**: 
   - Tabular data displays algorithm metrics
   - Charts compare algorithm performance
   - Algorithm recommendations based on different use cases
4. **Report Generation**: Detailed results are saved to a `results.txt` file for later analysis.

## Getting Started

### Prerequisites

- **Java Development Kit (JDK)**: Version 11 or higher
- **Gradle**: For building the project (or use the included Gradle wrapper)
- **IDE**: Any Java IDE like IntelliJ IDEA, Eclipse, or VS Code (optional)

### Building the Project

#### Using Gradle

```bash
# Clone the repository
git clone https://github.com/SKel1817/File-encryption-anaylsis.git
cd File-encryption-anaylsis

# Build with Gradle
./gradlew build

# Run the application
./gradlew run
```

#### Using JAR file

```bash
# Build a distributable JAR
./gradlew jar

# Run the JAR file
java -jar build/libs/File-encryption-anaylsis.jar
```

### Running the Application

1. Launch the application using one of the methods above
2. The GUI will open automatically
3. Click the "Browse" button to select a file for encryption analysis
4. Click "Analyze" to start the encryption analysis process
5. View the results in the tabbed interface:
   - "Results Table" tab shows numerical metrics
   - "Charts" tab provides graphical comparisons
   - "Recommendations" tab suggests optimal algorithms for different use cases
   - "Log" tab displays detailed operation logs

## GUI Features

The application features a modern graphical user interface with:

- File selection dialog
- Progress indication during analysis
- Tabbed results display:
  - Tabular data with sortable columns
  - Bar charts comparing key metrics
  - Algorithm recommendations
  - Detailed logs

## Adding New Encryption Algorithms

1. Create a new class that implements the `EncryptionAlgorithm` interface:
   ```java
   public class YourAlgorithm implements EncryptionAlgorithm {
       @Override
       public byte[] encrypt(byte[] plaintext) throws Exception {
           // Your encryption implementation
       }
       
       @Override
       public byte[] decrypt(byte[] ciphertext) throws Exception {
           // Your decryption implementation
       }
       
       @Override
       public int getKeyLength() {
           // Return key length in bits
       }
       
       @Override
       public String getName() {
           // Return algorithm name
       }
   }
   ```

2. Add your algorithm to the list in `Main.java`:
   ```java
   List<EncryptionAlgorithm> algorithms = Arrays.asList(
       new AES(),
       new DES(),
       // ... other algorithms
       new YourAlgorithm() // Add your algorithm here
   );
   ```

## Results Interpretation

The application evaluates algorithms on a scale of 0-10 across multiple metrics:

- **Speed**: Higher score = faster encryption time
- **Throughput**: Higher score = better data processing rate
- **Avalanche Effect**: Higher score = better sensitivity to input changes
- **Entropy**: Higher score = more randomness in output
- **Key Strength**: Higher score = longer/more secure key

The total score is a weighted average of these metrics to provide an overall ranking.

## Dependencies

- **JFreeChart**: For chart generation and visualization
- **Java Crypto Extensions**: For cryptographic operations

## License

This project is licensed under the MIT License - see the [LICENSE](./LICENSE) file for details.



