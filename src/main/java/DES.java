import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class DES implements EncryptionAlgorithm {
    private SecretKey key;
    private final String transformation = "DES/ECB/PKCS5Padding"; // Updated for DES

    public DES() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("DES"); // Use DES
        keyGen.init(56); // DES uses a 56-bit key
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
        return "DES";
    }
}