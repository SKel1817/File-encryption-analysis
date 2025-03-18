public interface EncryptionAlgorithm {
    byte[] encrypt(byte[] plaintext) throws Exception;
    byte[] decrypt(byte[] ciphertext) throws Exception;
    int getKeyLength();
    String getName();
}
