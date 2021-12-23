package blockchain;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;

public class GenerateKeys {
    private final KeyPairGenerator keyGen;
    private KeyPair pair;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    public GenerateKeys(int keyLength) throws NoSuchAlgorithmException {

        this.keyGen = KeyPairGenerator.getInstance("RSA");
        this.keyGen.initialize(keyLength);
    }

    public void createKeys() {
        this.pair = this.keyGen.generateKeyPair();
        this.privateKey = pair.getPrivate();
        this.publicKey = pair.getPublic();

    }

    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }

    public PublicKey getPublicKey() {
        return this.publicKey;
    }

    public void writeToFile(String path, byte[] key) throws IOException {

        File f = new File(path);
        f.getParentFile().mkdirs();

        FileOutputStream fos = new FileOutputStream(f);
        fos.write(key);
        fos.flush();
        fos.close();

    }

    public static KeyPair generateKeys() throws NoSuchAlgorithmException, IOException {
        GenerateKeys myKeys = new GenerateKeys(1024);
        myKeys.createKeys();

        String publicKeyPath = "MyKeys/publicKey";
        String privateKeyPath = "MyKeys/privateKey";

        myKeys.writeToFile(publicKeyPath, myKeys.getPublicKey().getEncoded());
        myKeys.writeToFile(privateKeyPath, myKeys.getPrivateKey().getEncoded());

        return myKeys.pair;
    }
}
