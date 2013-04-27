package com.example.AndTest;


import android.app.Application;
import android.content.Context;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

/**
 * Created with IntelliJ IDEA.
 * User: kalle
 * Date: 24.4.2013
 * Time: 21:36
 * To change this template use File | Settings | File Templates.
 */
public class SpClTest {

    static Context CurrCtx;
    public static void Testing(Context ctx) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(1024);
        KeyPair kp = keyGen.genKeyPair();
        Key publicKey = kp.getPublic();
        Key privateKey = kp.getPrivate();

        CurrCtx = ctx;
        //byte[] publicKey = keyGen.genKeyPair().getPublic().getEncoded();
        KeyFactory fact = KeyFactory.getInstance("RSA");
        RSAPublicKeySpec pub = fact.getKeySpec(kp.getPublic(),
                RSAPublicKeySpec.class);
        RSAPrivateKeySpec priv = fact.getKeySpec(kp.getPrivate(),
                RSAPrivateKeySpec.class);
        saveToFile("public.key", ctx, pub.getModulus(),
                pub.getPublicExponent());
        saveToFile("private.key", ctx, priv.getModulus(),
                priv.getPrivateExponent());

        String testStr = "mytest12345";
        byte[] bytes = testStr.getBytes("UTF-8");
        byte[] ciphertext = rsaEncrypt(bytes);
        byte[] cleartext = rsaDecrypt(ciphertext);
        String finalResult = new String(cleartext,  "UTF-8");

    }

    public static byte[] encryptData(byte[] cleartext, byte[] key) throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        Cipher c = Cipher.getInstance("AES");
        SecretKeySpec k = new SecretKeySpec(key, "AES");
        c.init(Cipher.ENCRYPT_MODE, k);
        byte[] encryptedData = c.doFinal(cleartext);
        return encryptedData;
    }

    public static byte[] decryptData(byte[] ciphertext, byte[] key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher c = Cipher.getInstance("AES");
        SecretKeySpec k = new SecretKeySpec(key, "AES");
        c.init(Cipher.DECRYPT_MODE, k);
        byte[] cleartext = c.doFinal(ciphertext);
        return cleartext;
    }

    public static byte[] rsaEncrypt(byte[] data) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        PublicKey pubKey = readPublicKeyFromFile("public.key", CurrCtx);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        byte[] cipherData = cipher.doFinal(data);
        return cipherData;
    }

    public static byte[] rsaDecrypt(byte[] data) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        PrivateKey privateKey = readPrivateKeyFromFile("private.key", CurrCtx);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] cleartext = cipher.doFinal(data);
        return cleartext;
    }

    public static void saveToFile(String fileName, Context ctx,
                                  BigInteger mod, BigInteger exp) throws IOException {
        FileOutputStream fos = ctx.openFileOutput(fileName, Context.MODE_PRIVATE);

        ObjectOutputStream oout = new ObjectOutputStream(
                new BufferedOutputStream(fos));
        try {
            oout.writeObject(mod);
            oout.writeObject(exp);
        } catch (Exception e) {
            throw new IOException("Unexpected error");
        } finally {
            oout.close();
        }
    }

    static PrivateKey readPrivateKeyFromFile(String keyFileName, Context ctx) throws IOException {
        //InputStream in = new FileInputStream(keyFileName);
        InputStream in = ctx.openFileInput(keyFileName);
        ObjectInputStream oin =
                new ObjectInputStream(new BufferedInputStream(in));
        try {
            BigInteger m = (BigInteger) oin.readObject();
            BigInteger e = (BigInteger) oin.readObject();
            //RSAPublicKeySpec keySpec = new RSAPublicKeySpec(m, e);
            RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(m, e);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = fact.generatePrivate(keySpec);
            return privateKey;
        } catch (Exception e) {
            throw new RuntimeException("Spurious serialisation error", e);
        } finally {
            oin.close();
        }
    }

    static PublicKey readPublicKeyFromFile(String keyFileName, Context ctx) throws IOException {
        //InputStream in = new FileInputStream(keyFileName);
        InputStream in = ctx.openFileInput(keyFileName);
        ObjectInputStream oin =
                new ObjectInputStream(new BufferedInputStream(in));
        try {
            BigInteger m = (BigInteger) oin.readObject();
            BigInteger e = (BigInteger) oin.readObject();
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(m, e);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            PublicKey pubKey = fact.generatePublic(keySpec);
            return pubKey;
        } catch (Exception e) {
            throw new RuntimeException("Spurious serialisation error", e);
        } finally {
            oin.close();
        }
    }



}
