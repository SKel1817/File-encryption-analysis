import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.security.SecureRandom;

public class PBEEncryption implements EncryptionAlgorithm {
    private SecretKey key;
    private byte[] salt;
    private final int iterationCount = 1000;
    private final String transformation = "PBEWithMD5AndDES";

    public PBEEncryption() throws Exception {
        // Fixed password for demonstration purposes
        String password = "secretPassword";
        // Generate an 8-byte salt
        salt = new byte[8];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);

        PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(transformation);
        key = keyFactory.generateSecret(keySpec);
    }

    @Override
    public byte[] encrypt(byte[] plaintext) throws Exception {
        Cipher cipher = Cipher.getInstance(transformation);
        PBEParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);
        cipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
        return cipher.doFinal(plaintext);
    }

    @Override
    public byte[] decrypt(byte[] ciphertext) throws Exception {
        Cipher cipher = Cipher.getInstance(transformation);
        PBEParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);
        cipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
        return cipher.doFinal(ciphertext);
    }

    @Override
    public int getKeyLength() {
        return key.getEncoded().length * 8;
    }

    @Override
    public String getName() {
        return "PBEWithMD5AndDES";
    }
}
