# File-encryption-anaylsis
 Analyze current available encryption algorithms for file storage, to find the faults and advantages to each type of algorithm. In order to help establish an operation standard for file encryption. 
## Overview
This project analyzes various encryption algorithms for file storage, highlighting their strengths and weaknesses to help establish an operational standard for file encryption. It includes implementations of popular encryption algorithms such as AES, DES, RSA, and placeholders for others like ECC and TDES.

## Features
- **Encryption Speed Testing**: Measures the time and throughput of encryption algorithms.
- **Avalanche Effect Testing**: Evaluates the sensitivity of encryption algorithms to small changes in plaintext.
- **Randomness and Entropy Analysis**: Calculates the Shannon entropy of ciphertext to assess randomness.
- **Resource Usage Monitoring**: Tracks CPU and memory usage during encryption operations.
- **Extensible Design**: Easily add new encryption algorithms by implementing the `EncryptionAlgorithm` interface.

## How It Works
1. **File Selection**: The program allows users to select a file for encryption analysis using a file chooser.
2. **Algorithm Testing**: Each encryption algorithm is tested for speed, avalanche effect, and randomness.
3. **Results Display**: Outputs metrics such as encryption time, throughput, Hamming distance, entropy, and key length.
4. **System Monitoring**: Optionally displays CPU and memory usage after tests.

## Setup Instructions

### Prerequisites
- **Java Development Kit (JDK)**: Ensure JDK 8 or later is installed.
- **IDE or Text Editor**: Use an IDE like IntelliJ IDEA, Eclipse, or a text editor like VS Code.
- **Build Tool (Optional)**: Maven or Gradle for dependency management.

### Steps for Windows
1. **Clone the Repository**:
    ```bash
    git clone https://github.com/your-username/File-encryption-analysis.git
    cd File-encryption-analysis
    ```


### Steps for macOS
1. **Clone the Repository**:
    ```bash
    git clone https://github.com/your-username/File-encryption-analysis.git
    cd File-encryption-analysis
    ```


## Adding New Encryption Algorithms
1. Create a new class that implements the `EncryptionAlgorithm` interface.
2. Implement the required methods: `encrypt`, `decrypt`, `getKeyLength`, and `getName`.
3. Add the new class to the list of algorithms in the `Main` class.

## License
This project is licensed under the MIT License. See the [LICENSE](./LICENSE) file for details.

## Contributing
Contributions are welcome! Feel free to submit issues or pull requests to improve the project.

## Contact
For questions or feedback, please contact Valerie Nielson at [your-email@example.com].
