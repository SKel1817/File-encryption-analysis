import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.ChaCha20ParameterSpec;
import java.security.SecureRandom;

public class ChaCha20 implements EncryptionAlgorithm {
    private SecretKey key;
    private final String transformation = "ChaCha20";
    private static final int NONCE_LENGTH = 12; // ChaCha20 requires a 12-byte nonce

    public ChaCha20() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("ChaCha20");
        keyGen.init(256); // ChaCha20 uses a 256-bit key
        this.key = keyGen.generateKey();
    }

    @Override
    public byte[] encrypt(byte[] plaintext) throws Exception {
        Cipher cipher = Cipher.getInstance(transformation);
        // Generate a random nonce
        byte[] nonce = new byte[NONCE_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(nonce);

        // Initialize cipher with the nonce and an initial counter (typically 1)
        ChaCha20ParameterSpec paramSpec = new ChaCha20ParameterSpec(nonce, 1);
        cipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
        byte[] encrypted = cipher.doFinal(plaintext);

        // Prepend nonce to the ciphertext for use in decryption
        byte[] output = new byte[nonce.length + encrypted.length];
        System.arraycopy(nonce, 0, output, 0, nonce.length);
        System.arraycopy(encrypted, 0, output, nonce.length, encrypted.length);
        return output;
    }

    @Override
    public byte[] decrypt(byte[] ciphertext) throws Exception {
        if (ciphertext.length < NONCE_LENGTH) {
            throw new IllegalArgumentException("Ciphertext too short");
        }
        // Extract the nonce from the beginning of the ciphertext
        byte[] nonce = new byte[NONCE_LENGTH];
        System.arraycopy(ciphertext, 0, nonce, 0, NONCE_LENGTH);
        byte[] actualCiphertext = new byte[ciphertext.length - NONCE_LENGTH];
        System.arraycopy(ciphertext, NONCE_LENGTH, actualCiphertext, 0, actualCiphertext.length);

        Cipher cipher = Cipher.getInstance(transformation);
        ChaCha20ParameterSpec paramSpec = new ChaCha20ParameterSpec(nonce, 1);
        cipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
        return cipher.doFinal(actualCiphertext);
    }

    @Override
    public int getKeyLength() {
        return key.getEncoded().length * 8;
    }

    @Override
    public String getName() {
        return "ChaCha20";
    }
}
