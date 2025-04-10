import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class TDES implements EncryptionAlgorithm {
    private SecretKey key;
    private final String transformation = "DESede/ECB/PKCS5Padding"; // TDES transformation

    public TDES() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("DESede"); // TDES key generator
        keyGen.init(168); // TDES uses a 168-bit key
        this.key = keyGen.generateKey();
    }

    @Override
    public byte[] encrypt(byte[] plaintext) throws Exception {
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(plaintext);
    }

    @Override
    public byte[] decrypt(byte[] ciphertext) throws Exception {
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(ciphertext);
    }

    @Override
    public int getKeyLength() {
        return key.getEncoded().length * 8;
    }

    @Override
    public String getName() {
        return "TDES";
    }
}
