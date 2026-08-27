/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnEncryptionUtility.java
 * Subsystem:  PoC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Upananda Singha      1/6/12      7.2
 * <p/>
 * <p/>
 * #401, 4th Floor, 'Prestige Sigma'
 * No.3, Vittal Mallya Road
 * Bangalore - 560 001
 * www.kodiaknetworks.com
 * All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Kodiak
 * Networks, Inc. You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Kodiak Networks.
 * ************************************************************************
 */
package com.kodiak.common.resources;

import java.io.File;
import java.io.FileInputStream;

import com.kodiak.common.exception.KnException;
import com.kodiak.logger.KnLogger;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.net.URLCodec;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


public class KnEncryptionUtility {
	private static final KnLogger knLogger = KnLogger.getLogger(KnEncryptionUtility.class);

    private static final String CLASS = KnEncryptionUtility.class.getName();

    //Instance variable
    private static KnEncryptionUtility instance = new KnEncryptionUtility();
    public static final String AES = "AES";
    //URLCodec instance variable
    private static URLCodec urlCodec = new URLCodec();
    private final String ENCRYPTION_DECRYPTION_ERROR = "1001";

    //Private constructor
    private KnEncryptionUtility() {

    }

    //returning the instance of a class
    public static KnEncryptionUtility getInstance() {
        return instance;
    }

    //This Method wont allow to clone the instance
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }


    /**
     * Decodes a URL safe string into its original form. Escaped
     * characters are converted back to their original representation.
     *
     * @param url URL safe string to convert into its original form
     * @return original string
     * @throws RuntimeException Thrown if URL decoding is unsuccessful.
     *                          This only happens in the case of a UnsupportedEncodingException
     *                          which should never occur in reality.
     */
    public String decodeURL(String url) {
        String methodName = "decodeURL(url)";
        String decodedUrl;
        try {
            decodedUrl = urlCodec.decode(url);
        } catch (DecoderException exp) {
            knLogger.error( methodName, "EncoderException occured - ", exp);
            decodedUrl = url;
        }
        return decodedUrl;
    }

    /**
     * @param url the url to encode
     * @return the encoded url
     * @throws RuntimeException Thrown if URL encoding is unsuccessful.
     *                          This only happens in the case of a UnsupportedEncodingException
     *                          which should never occurs in reality.
     */
    public String encodeURL(String url) {
        String methodName = "encodeURL(url)";
        String encodedUrl;
        try {
            encodedUrl = urlCodec.encode(url);
        } catch (EncoderException exp) {
            knLogger.error(methodName, "Encoder Exception occurred - ", exp);
            encodedUrl = url;
        }
        return encodedUrl;
    }

    /**
     * encodeBase64 a safe string into its original form. Escaped
     * characters are converted back to their original representation.
     *
     * @param bytes byte[] safe string to convert into its original form
     * @return original string
     */
    public String encodeBase64(byte[] bytes) {
        return new String(Base64.encodeBase64(bytes));
    }

    /**
     * decodeBase64 a safe string into its original form. Escaped
     * characters are converted back to their original representation.
     *
     * @param bytes byte[] safe string to convert into its original form
     * @return original string
     */
    public byte[] decodeBase64(byte[] bytes) {
        return Base64.decodeBase64(bytes);
    }

    /**
     * Decrypts data using AES encryption algorithm
     *
     * @param encryptedData
     * @param key
     * @return
     */
    public String decryptAES(String encryptedData, byte[] key, boolean urlDecoding) throws KnException {
        String methodName = "decryptData(String, byte[])";
        String decrypted;

        SecretKeySpec keySpec = new SecretKeySpec(key, AES);
        try {
            Cipher cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            if (urlDecoding) {
                URLCodec urlCodec = new URLCodec();
                encryptedData = urlCodec.decode(encryptedData);
            }
            decrypted = new String(cipher.doFinal(decodeBase64(encryptedData.getBytes())));

        } catch (Exception e) {
            knLogger.error( methodName, "AES Decryption Exception occurred - " , e);
            throw new KnException(ENCRYPTION_DECRYPTION_ERROR, "AES Decryption Exception", e);
        }
        return decrypted;
    }

    /**
     * Decrypts data using AES encryption algorithm
     *
     * @param encryptedData
     * @param key
     * @return
     */
    public byte[] decryptAES(byte[] encryptedData, byte[] key, boolean urlDecoding) throws KnException {
        String methodName = "decryptData(String, byte[])";
        knLogger.info( methodName, "urlDecoding " , urlDecoding);
        byte[] decryptedData;
        try {
            SecretKeySpec keySpec = new SecretKeySpec(key, AES);
            Cipher cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            if (urlDecoding) {
                URLCodec urlCodec = new URLCodec();
                encryptedData = urlCodec.decode(encryptedData);
            }
            decryptedData = cipher.doFinal(decodeBase64(encryptedData));

        } catch (Exception e) {
            knLogger.error( methodName, "AES Decryption Exception occurred - " , e);
            throw new KnException(ENCRYPTION_DECRYPTION_ERROR, "AES Decryption Exception", e);
        }
        return decryptedData;
    }

    public String encryptAES(String data, byte[] key) throws KnException {
        String methodName = "encryptAES(Sring, byte[])";
        byte[] encrypted;
        try {
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            encrypted = cipher.doFinal(data.getBytes());
        } catch (Exception e) {
            knLogger.error( methodName, "Exception while reading AES key from file - " , e);
            throw new KnException(ENCRYPTION_DECRYPTION_ERROR, "AES Decryption Exception", e);
        }
        return new String(Base64.encodeBase64(encrypted));
    }

    public byte[] getAESKeyFromFile(File file) throws KnException {
        String methodName = "getDecodedKeyFromFile(File)";
        byte[] decodedKey;
        try {
            FileInputStream inputStream = new FileInputStream(file);
            byte[] keyfromFile = new byte[inputStream.available()];
            inputStream.read(keyfromFile);
            //Base 64 decode
            inputStream.close();
            decodedKey = Base64.decodeBase64(keyfromFile);
        } catch (Exception e) {
            knLogger.error( methodName, "AES Decryption Exception occurred - " , e);
            throw new KnException(ENCRYPTION_DECRYPTION_ERROR, "AES Decryption Exception", e);
        }
        return decodedKey;
    }
}
