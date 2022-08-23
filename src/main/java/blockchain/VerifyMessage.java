package blockchain;

import java.security.PublicKey;
import java.security.Signature;

public class VerifyMessage {

    /**
     * @param data      byte array that represents data
     * @param signature byte array that represents signature
     * @param publicKey public key to verify message
     * @return true, if signature if valid, false otherwise
     */
    private static boolean verifySignature(byte[] data, byte[] signature, PublicKey publicKey) throws Exception {
        Signature sig = Signature.getInstance("SHA1withRSA");
        sig.initVerify(publicKey);
        sig.update(data);
        return sig.verify(signature);
    }

    /**
     * @param message message to verify
     * @return verified message
     */
    public static Message verifyMessageAndGet(Message message) throws Exception {
        boolean verified = verifySignature(message.getMessageText(),
                message.getSign(), message.getPublicKey());
        if (verified) {
            return message;
        }
        return null;
    }
}