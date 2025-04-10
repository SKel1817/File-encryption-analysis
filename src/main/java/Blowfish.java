import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class Blowfish implements EncryptionAlgorithm {
    private SecretKey key;
    private final String transformation = "Blowfish/ECB/PKCS5Padding";

    public Blowfish() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("Blowfish");
        keyGen.init(128); // Blowfish key size can vary (32-448 bits)
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
        return "Blowfish";
    }
}
