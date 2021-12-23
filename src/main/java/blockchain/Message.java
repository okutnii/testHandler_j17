package blockchain;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

class Message {
    private final long id;
    private final List<byte[]> list;
    private final PublicKey publicKey;

    /**
     * The constructor of Message class builds the list that will be written to the file.
     * The list consists of the message and the signature.
     * */
    public Message(String data, KeyPair pair, long id) throws Exception {
        list = new ArrayList<>();
        list.add(data.getBytes());
        list.add(sign(data, pair));

        publicKey = pair.getPublic();

        this.id = id;
    }

    /**
     * The method that signs the data using the private key that is stored in keyFile path
     * */
    public byte[] sign(String data, KeyPair pair) throws Exception{
        Signature rsa = Signature.getInstance("SHA1withRSA");
        rsa.initSign(pair.getPrivate());
        rsa.update(data.getBytes());

        return rsa.sign();
    }

    public long getId() {
        return id;
    }

    public List<byte[]> getList() {
        return list;
    }

    public byte[] getMessageText(){
        return list.get(0);
    }

    public byte[] getSign(){
        return list.get(1);
    }

    public  PublicKey getPublicKey(){
        return this.publicKey;
    }

    @Override
    public String toString(){
        return new String(getMessageText()).concat(
                new String(getSign())
        );
    }

    public static Message getSignedDataOf(String data, KeyPair pair, long id) throws Exception{
        return new Message(data,pair, id);
    }
}
