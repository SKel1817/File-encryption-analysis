import java.nio.file.Files;
import java.io.File;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {

    // Save output to both console and file
    private static PrintWriter resultWriter;
    
    // Initialize the result file writer
    public static void initResultFile() {
        try {
            // Create a timestamped filename (optional)
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String timestamp = sdf.format(new Date());
            
            // Create or overwrite the results.txt file
            resultWriter = new PrintWriter(new FileWriter("results.txt"));
            resultWriter.println("Encryption Algorithm Analysis Results");
            resultWriter.println("Generated: " + new Date());
            resultWriter.println("=====================================\n");
            
            // Inform the user
            System.out.println("Results will be saved to results.txt");
        } catch (IOException e) {
            System.err.println("Error creating results file: " + e.getMessage());
        }
    }    // Write to both console and file
    public static void writeResult(String text) {
        // Simple output without colors
        System.out.println(text);
        
        // Write to file
        if (resultWriter != null) {
            resultWriter.println(text);
        }
    }
      // Write formatted output to both console and file
    public static void writeResultf(String format, Object... args) {
        String formattedText = String.format(format, args);
        
        // Simple output without colors
        System.out.print(formattedText);
        
        // Write to file
        if (resultWriter != null) {
            resultWriter.print(formattedText);
        }
    }
    
    // Close the result file
    public static void closeResultFile() {
        if (resultWriter != null) {
            resultWriter.close();
            System.out.println("\nResults have been saved to results.txt");
        }
    }

    // Measure encryption speed (time and throughput)
    public static void measureSpeed(String filePath, EncryptionAlgorithm algorithm) throws Exception {
        byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
        long startEnc = System.nanoTime();
        byte[] ciphertext = algorithm.encrypt(fileBytes);
        long endEnc = System.nanoTime();
        double encTimeMs = (endEnc - startEnc) / 1_000_000.0;
        double fileSizeMB = fileBytes.length / (1024.0 * 1024.0);
        double throughput = fileSizeMB / ((endEnc - startEnc) / 1e9);
        writeResult(algorithm.getName() + " Encryption Time (ms): " + encTimeMs);
        writeResult(algorithm.getName() + " Throughput (MB/s): " + throughput);
    }
    
    // Monitor CPU and memory usage
    public static void measureResourceUsage() {
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        double processCpuLoad = osBean.getProcessCpuLoad(); // value between 0.0 and 1.0
        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();
        long usedMemory = totalMemory - freeMemory;
        writeResult("Process CPU Load: " + processCpuLoad);
        writeResult("Used Memory (bytes): " + usedMemory);
        writeResult("Total Memory (bytes): " + totalMemory);
    }
    
    // Utility: Calculate Hamming distance between two byte arrays
    public static int hammingDistance(byte[] a, byte[] b) {
        int distance = 0;
        int len = Math.min(a.length, b.length);
        for (int i = 0; i < len; i++) {
            int xor = a[i] ^ b[i];
            distance += Integer.bitCount(xor & 0xFF);
        }
        return distance;
    }
    
    // Test Avalanche Effect: flip a bit in plaintext and compare ciphertexts
    public static void testAvalancheEffect(EncryptionAlgorithm algorithm, byte[] plaintext) throws Exception {
        byte[] originalCipher = algorithm.encrypt(plaintext);
        
        // Create a modified plaintext by flipping the least significant bit of the first byte
        byte[] modifiedPlaintext = Arrays.copyOf(plaintext, plaintext.length);
        modifiedPlaintext[0] ^= 0x01;
        
        byte[] modifiedCipher = algorithm.encrypt(modifiedPlaintext);
        int distance = hammingDistance(originalCipher, modifiedCipher);
        writeResult(algorithm.getName() + " Avalanche Effect Hamming Distance: " + distance);
    }
    
    // Utility: Calculate Shannon entropy of data
    public static double calculateEntropy(byte[] data) {
        int[] freq = new int[256];
        for (byte b : data) {
            freq[b & 0xFF]++;
        }
        double entropy = 0.0;
        int length = data.length;
        for (int count : freq) {
            if (count == 0) continue;
            double p = (double) count / length;
            entropy -= p * (Math.log(p) / Math.log(2));
        }
        return entropy;
    }
    
    // Test Randomness and Entropy on ciphertext
    public static void testRandomnessAndEntropy(EncryptionAlgorithm algorithm, byte[] plaintext) throws Exception {
        byte[] ciphertext = algorithm.encrypt(plaintext);
        double entropy = calculateEntropy(ciphertext);
        writeResult(algorithm.getName() + " Ciphertext Shannon Entropy: " + entropy);
    }
      // Display samples of original and encrypted data
    public static void displayFileSamples(byte[] original, byte[] encrypted, String algorithmName) {
        int sampleSize = Math.min(50, original.length); // Show up to 50 bytes
        
        writeResult("\n=== Provided Data for " + algorithmName + " ===");
        writeResult("Original data (first " + sampleSize + " bytes): ");
        displayHexAndText(original, sampleSize);
        
        writeResult("\nEncrypted data (first " + sampleSize + " bytes): ");
        displayHexAndText(encrypted, sampleSize);
        writeResult("===================================");
    }
    
    // Utility method to display bytes in hex and text format
    public static void displayHexAndText(byte[] data, int limit) {
        int displayLimit = Math.min(limit, data.length);
        StringBuilder hexView = new StringBuilder();
        StringBuilder textView = new StringBuilder();
        
        for (int i = 0; i < displayLimit; i++) {
            // Convert to hex
            String hex = String.format("%02X ", data[i] & 0xFF);
            hexView.append(hex);
            
            // Convert to displayable text (or . if not printable)
            char c = (char) (data[i] & 0xFF);
            if (c >= 32 && c < 127) {
                textView.append(c);
            } else {
                textView.append('.');
            }
            
            // Add spacing every 8 bytes
            if ((i + 1) % 8 == 0) {
                hexView.append(" ");
                textView.append(" ");
            }
        }
        
        writeResult("HEX: " + hexView);
        writeResult("TXT: " + textView);
    }
    
    // Compare algorithms and recommend the best one
    public static void compareAndRecommend(List<AlgorithmEvaluator.AlgorithmPerformance> performances) {
        writeResult("\n===================================================");
        writeResult("           ALGORITHM COMPARISON RESULTS           ");
        writeResult("===================================================");
        
        // Display comparison table header
        writeResultf("%-15s %-15s %-15s %-15s %-15s %-15s%n", 
                        "Algorithm", "Encrypt Time", "Throughput", "Avalanche", "Entropy", "Key Length");
        writeResult("-------------------------------------------------------------------------------------------------------------------");
        
        // Display each algorithm's metrics
        for (AlgorithmEvaluator.AlgorithmPerformance perf : performances) {
            writeResultf("%-15s %-15.2f %-15.2f %-15d %-15.4f %-15d%n", 
                            perf.getName(), 
                            perf.getEncryptionTime(), 
                            perf.getThroughput(), 
                            perf.getAvalancheEffect(), 
                            perf.getEntropy(),
                            perf.getKeyLength());
        }
        
        writeResult("\n===================================================");
        writeResult("              ALGORITHM SCORES (0-10)             ");
        writeResult("===================================================");
        
        // Display normalized scores
        writeResultf("%-15s %-15s %-15s %-15s %-15s %-15s %-15s%n", 
                        "Algorithm", "Speed", "Throughput", "Avalanche", "Entropy", "Key Strength", "Total Score");
        writeResult("-----------------------------------------------------------------------------------------------------------------------");
        
        for (AlgorithmEvaluator.AlgorithmPerformance perf : performances) {
            writeResultf("%-15s %-15.2f %-15.2f %-15.2f %-15.2f %-15.2f %-15.2f%n", 
                            perf.getName(), 
                            perf.getScore("encryptionTime"), 
                            perf.getScore("throughput"), 
                            perf.getScore("avalancheEffect"),
                            perf.getScore("entropy"),
                            perf.getScore("keyLength"),
                            perf.getTotalScore());
        }
        
        // Find the best algorithms
        AlgorithmEvaluator evaluator = new AlgorithmEvaluator();
        for (AlgorithmEvaluator.AlgorithmPerformance perf : performances) {
            evaluator.addPerformance(perf);
        }
        
        AlgorithmEvaluator.AlgorithmPerformance bestOverall = evaluator.getBestAlgorithm();
        AlgorithmEvaluator.AlgorithmPerformance bestSpeed = evaluator.getBestForSpeed();
        AlgorithmEvaluator.AlgorithmPerformance bestSecurity = evaluator.getBestForSecurity();
        AlgorithmEvaluator.AlgorithmPerformance bestSmallFiles = evaluator.getBestForSmallFiles();
        AlgorithmEvaluator.AlgorithmPerformance bestLargeFiles = evaluator.getBestForLargeFiles();
        
        // Display recommendations
        writeResult("\n===================================================");
        writeResult("               RECOMMENDATIONS                    ");
        writeResult("===================================================");
        writeResult("Best Overall Algorithm: " + bestOverall.getName() + " (Score: " + String.format("%.2f", bestOverall.getTotalScore()) + ")");
        writeResult("Best for Speed: " + bestSpeed.getName());
        writeResult("Best for Security: " + bestSecurity.getName());
        writeResult("Best for Small Files: " + bestSmallFiles.getName());
        writeResult("Best for Large Files: " + bestLargeFiles.getName());
    }
    
    public static void main(String[] args) throws Exception {
        // Initialize result file
        initResultFile();

        // Use the file explorer to let the user choose a file.
        File selectedFile = FileImporter.chooseFile();
        if (selectedFile == null) {
            writeResult("No file selected. Exiting.");
            closeResultFile();
            return;
        }
        String filePath = selectedFile.getAbsolutePath();
        writeResult("Selected file: " + filePath);
        
        // Load plaintext from the selected file (used for avalanche effect and entropy tests)
        byte[] plaintext = Files.readAllBytes(Paths.get(filePath));
        
        List<EncryptionAlgorithm> algorithms = Arrays.asList(
            new AES(),
            new DES(),
            new TDES(),
            new RSA(),
            new ChaCha20(),
            new Blowfish(),
            new PBEEncryption()
        );
        
        // Create an evaluator to collect performance data
        AlgorithmEvaluator evaluator = new AlgorithmEvaluator();
        
        // Loop through each algorithm and run the tests
        for (EncryptionAlgorithm algo : algorithms) {
            writeResult("\n=== Testing " + algo.getName() + " ===");
            
            // Create performance object for this algorithm
            AlgorithmEvaluator.AlgorithmPerformance performance = new AlgorithmEvaluator.AlgorithmPerformance(algo.getName());
              // Speed testing
            byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
            long startEnc = System.nanoTime();
            byte[] ciphertext = algo.encrypt(fileBytes);
            long endEnc = System.nanoTime();
            double encTimeMs = (endEnc - startEnc) / 1_000_000.0;
            double fileSizeMB = fileBytes.length / (1024.0 * 1024.0);
            double throughput = fileSizeMB / ((endEnc - startEnc) / 1e9);
            writeResult(algo.getName() + " Encryption Time (ms): " + encTimeMs);
            writeResult(algo.getName() + " Throughput (MB/s): " + throughput);
              // Display samples of original and encrypted data
            displayFileSamples(fileBytes, ciphertext, algo.getName());
            
            // Store the speed metrics
            performance.setEncryptionTime(encTimeMs);
            performance.setThroughput(throughput);
            
            // Avalanche Effect testing
            byte[] originalCipher = algo.encrypt(plaintext);
            byte[] modifiedPlaintext = Arrays.copyOf(plaintext, plaintext.length);
            modifiedPlaintext[0] ^= 0x01;
            byte[] modifiedCipher = algo.encrypt(modifiedPlaintext);
            int distance = hammingDistance(originalCipher, modifiedCipher);
            writeResult(algo.getName() + " Avalanche Effect Hamming Distance: " + distance);
            
            // Store avalanche effect
            performance.setAvalancheEffect(distance);
            
            // Randomness and Entropy testing
            double entropy = calculateEntropy(ciphertext);
            writeResult(algo.getName() + " Ciphertext Shannon Entropy: " + entropy);
            
            // Store entropy
            performance.setEntropy(entropy);
            
            // Display and store the key length
            int keyLength = algo.getKeyLength();
            writeResult(algo.getName() + " Key Length (bits): " + keyLength);
            performance.setKeyLength(keyLength);
            
            // Add this algorithm's performance to the evaluator
            evaluator.addPerformance(performance);
        }
        
        // Normalize scores and compare algorithms
        evaluator.normalizeScores();
        List<AlgorithmEvaluator.AlgorithmPerformance> sortedPerformances = evaluator.getSortedPerformances();
        
        // Display comparison and recommendations
        compareAndRecommend(sortedPerformances);
        
        // check system resource usage after running tests
        measureResourceUsage();

        // Close result file
        closeResultFile();
    }
}
