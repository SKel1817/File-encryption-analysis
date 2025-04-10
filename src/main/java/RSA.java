import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;

public class RSA implements EncryptionAlgorithm {
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private final String transformation = "RSA/ECB/PKCS1Padding"; // RSA transformation

    public RSA() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048); // RSA key size (2048 bits is recommended)
        KeyPair keyPair = keyGen.generateKeyPair();
        this.publicKey = keyPair.getPublic();
        this.privateKey = keyPair.getPrivate();
    }

    @Override
    public byte[] encrypt(byte[] plaintext) throws Exception {
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        // Maximum block size for RSA with PKCS1Padding on a 2048-bit key is 245 bytes
        int inputBlockSize = 245;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        for (int i = 0; i < plaintext.length; i += inputBlockSize) {
            int blockLength = Math.min(inputBlockSize, plaintext.length - i);
            byte[] chunk = Arrays.copyOfRange(plaintext, i, i + blockLength);
            byte[] encryptedChunk = cipher.doFinal(chunk);
            outputStream.write(encryptedChunk);
        }
        return outputStream.toByteArray();
    }

    @Override
    public byte[] decrypt(byte[] ciphertext) throws Exception {
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        // The output block size of RSA encryption for a 2048-bit key is 256 bytes
        int outputBlockSize = 256;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        for (int i = 0; i < ciphertext.length; i += outputBlockSize) {
            int blockLength = Math.min(outputBlockSize, ciphertext.length - i);
            byte[] chunk = Arrays.copyOfRange(ciphertext, i, i + blockLength);
            byte[] decryptedChunk = cipher.doFinal(chunk);
            outputStream.write(decryptedChunk);
        }
        return outputStream.toByteArray();
    }

    @Override
    public int getKeyLength() {
        return publicKey.getEncoded().length * 8; // Key length in bits
    }

    @Override
    public String getName() {
        return "RSA";
    }
}
