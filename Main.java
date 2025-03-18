import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Main {

    // A method to test speed, for example, using one of the algorithms
    public static void measureSpeed(String filePath, EncryptionAlgorithm algorithm) throws Exception {
        byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
        long start = System.nanoTime();
        byte[] ciphertext = algorithm.encrypt(fileBytes);
        long end = System.nanoTime();
        double timeMs = (end - start) / 1_000_000.0;
        double throughput = (fileBytes.length / (1024.0 * 1024.0)) / ((end - start) / 1e9);
        
        System.out.println(algorithm.getName() + " encryption time (ms): " + timeMs);
        System.out.println(algorithm.getName() + " throughput (MB/s): " + throughput);
    }

    public static void main(String[] args) throws Exception {
        // Example file to test
        String filePath = "testfile.dat";

        // Create instances for each encryption algorithm
        List<EncryptionAlgorithm> algorithms = Arrays.asList(
            new AES()
            // new DESEncryption(),
            // new TDSEncryption(),
            // new RSAEncryption(),  // Remember: RSA is used differently for bulk encryption.
            // new ECCEncryption()   // Similarly for ECC.
        );

        // Loop through each algorithm and run tests
        for (EncryptionAlgorithm algo : algorithms) {
            System.out.println("\n--- Testing " + algo.getName() + " ---");
            measureSpeed(filePath, algo);
            // You can call additional test methods here (e.g., resource usage, avalanche effect, entropy)
        }
    }
}
