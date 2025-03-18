import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;

public class Main {

    // Measure encryption speed (time and throughput)
    public static void measureSpeed(String filePath, EncryptionAlgorithm algorithm) throws Exception {
        byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
        long startEnc = System.nanoTime();
        byte[] ciphertext = algorithm.encrypt(fileBytes);
        long endEnc = System.nanoTime();
        double encTimeMs = (endEnc - startEnc) / 1_000_000.0;
        double fileSizeMB = fileBytes.length / (1024.0 * 1024.0);
        double throughput = fileSizeMB / ((endEnc - startEnc) / 1e9);
        System.out.println(algorithm.getName() + " Encryption Time (ms): " + encTimeMs);
        System.out.println(algorithm.getName() + " Throughput (MB/s): " + throughput);
    }
    
    // Monitor CPU and memory usage
    public static void measureResourceUsage() {
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        double processCpuLoad = osBean.getProcessCpuLoad(); // value between 0.0 and 1.0
        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();
        long usedMemory = totalMemory - freeMemory;
        System.out.println("Process CPU Load: " + processCpuLoad);
        System.out.println("Used Memory (bytes): " + usedMemory);
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
        System.out.println(algorithm.getName() + " Avalanche Effect Hamming Distance: " + distance);
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
        System.out.println(algorithm.getName() + " Ciphertext Shannon Entropy: " + entropy);
    }
    
    public static void main(String[] args) throws Exception {
        // Prepare a test file. If the file doesn't exist, create a 1MB file with random data.
        String filePath = "testfile.dat";
        java.nio.file.Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            byte[] randomData = new byte[1024 * 1024]; // 1 MB
            new Random().nextBytes(randomData);
            Files.write(path, randomData);
        }
        
        // Load plaintext from file (used for avalanche effect and entropy tests)
        byte[] plaintext = Files.readAllBytes(path);
        
        // Instantiate your encryption algorithms. Here, only AES is shown.
        List<EncryptionAlgorithm> algorithms = Arrays.asList(
            new AES(),
            new DES()//, new TDS(), new RSA(), new ECC(), new TDES()
        );
        
        // Loop through each algorithm and run the tests
        for (EncryptionAlgorithm algo : algorithms) {
            System.out.println("\n=== Testing " + algo.getName() + " ===");
            
            // Speed testing
            measureSpeed(filePath, algo);
            
            // Avalanche Effect testing
            testAvalancheEffect(algo, plaintext);
            
            // Randomness and Entropy testing
            testRandomnessAndEntropy(algo, plaintext);
            
            // Display the key length
            System.out.println(algo.getName() + " Key Length (bits): " + algo.getKeyLength());
        }
        
        // Optionally, check system resource usage after running tests
        measureResourceUsage();
    }
}
