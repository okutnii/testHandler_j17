package blockchain;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.List;

public class VerifyMessage {
    private final List<byte[]> list;
    private boolean verified;

    //The constructor of VerifyMessage class retrieves the byte arrays from the Message
    public VerifyMessage(Message message, PublicKey publicKey) throws Exception {
        this.list = message.getList();

        this.verified = verifySignature(message.getMessageText(), message.getSign(), publicKey);
    }

    //Method for signature verification that initializes with the Public Key,
    //updates the data to be verified and then verifies them using the signature
    private boolean verifySignature(byte[] data, byte[] signature, PublicKey publicKey) throws Exception {
        Signature sig = Signature.getInstance("SHA1withRSA");
        sig.initVerify(publicKey);
        sig.update(data);

        return sig.verify(signature);
    }


    public static Message verifyMessageAndGet(Message message) throws Exception{
        VerifyMessage vm = new VerifyMessage(message, message.getPublicKey());

        if(vm.verified)
            return message;

        return null;
    }
}