/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.common.ggcache;

import com.kodiak.common.resources.KnConstants;
import com.kodiak.logger.KnLogger;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import static com.kodiak.common.resources.KnConstants.JAVA_TRUST_STORE_PASSWORD;

public class KnTruststoreMerger {
    private static final KnLogger knLogger = KnLogger.getLogger(KnTruststoreMerger.class);
    /**
     * Merges a custom truststore with the default Java CA certificates
     *
     * @param customTruststorePath Path to the custom truststore file
     * @param customTruststorePassword Password for the custom truststore
     * @param outputTruststorePath Path where the merged truststore will be saved
     * @param outputPassword Password for the output truststore
     * @throws Exception if any error occurs during the merge process
     */
    public static void mergeTruststore(String customTruststorePath,
                                       String customTruststorePassword,
                                       String outputTruststorePath,
                                       String outputPassword) throws Exception {

        String methodName = "mergeTruststore()";
        knLogger.info(methodName, "Starting truststore merge process...");

        // Load the default Java CA certificates
        KeyStore javaCaKeystore = loadJavaCaCertificates();
        knLogger.info(methodName, "Loaded Java CA certificates: " , javaCaKeystore.size() , " certificates");

        // Load the custom truststore
        KeyStore customKeystore = loadCustomTruststore(customTruststorePath, customTruststorePassword);
        knLogger.info(methodName, "Loaded custom truststore: " , customKeystore.size() , " certificates");

        // Track aliases to avoid duplicates
        Set<String> usedAliases = new HashSet<>();

        // Add all Java CA certificates to the merged keystore
//        addCertificatesToKeystore(javaCaKeystore, mergedKeystore, usedAliases);

        // Add all custom certificates to the merged keystore
        addCertificatesToKeystore(customKeystore, javaCaKeystore, usedAliases);

        // Save the merged keystore
        try (FileOutputStream fos = new FileOutputStream(outputTruststorePath)) {
            javaCaKeystore.store(fos, outputPassword.toCharArray());
        }
        knLogger.info(methodName, "Successfully created merged truststore at: " , outputTruststorePath);
        knLogger.info(methodName, "Total certificates in merged truststore: " , javaCaKeystore.size());
    }

    /**
     * Loads the default Java CA certificates
     */
    private static KeyStore loadJavaCaCertificates() throws Exception {
        String methodName = "loadJavaCaCertificates()";
        String cacertsPath = System.getProperty(KnConstants.JAVA_HOME)
                + KnConstants.JAVA_TRUST_STORE_PATH;

        knLogger.info(methodName, "Loading Java CA certificates from: " , cacertsPath);

        KeyStore keystore = KeyStore.getInstance("JKS");
        try (FileInputStream fis = new FileInputStream(cacertsPath)) {
            // Default password for Java cacerts is "changeit"
            keystore.load(fis, JAVA_TRUST_STORE_PASSWORD.toCharArray());
        }
        return keystore;
    }

    /**
     * Loads a custom truststore - handles both JKS and PKCS12 formats
     */
    private static KeyStore loadCustomTruststore(String truststorePath, String password) throws Exception {
        String methodName = "loadCustomTruststore()";
        knLogger.info(methodName, "Loading custom truststore from: " , truststorePath);

        // Try PKCS12 first, then JKS
        KeyStore keystore;
        Exception lastException;

        // Try PKCS12 format first
        try {
            keystore = KeyStore.getInstance("PKCS12");
            try (FileInputStream fis = new FileInputStream(truststorePath)) {
                keystore.load(fis, password.toCharArray());
            }
            knLogger.info(methodName, "Detected PKCS12 format for custom truststore");
            return keystore;
        } catch (Exception e) {
            lastException = e;
            knLogger.info(methodName, "PKCS12 format failed, trying JKS format...");
        }

        // Try JKS format
        try {
            keystore = KeyStore.getInstance("JKS");
            try (FileInputStream fis = new FileInputStream(truststorePath)) {
                keystore.load(fis, password.toCharArray());
            }
            knLogger.info(methodName, "Detected JKS format for custom truststore");
            return keystore;
        } catch (Exception e) {
            knLogger.error(methodName, "Failed to load truststore in both PKCS12 and JKS formats");
            throw new Exception("Unable to load custom truststore. Last error: " + e.getMessage(), lastException);
        }
    }

    /**
     * Adds certificates from source keystore to target keystore
     */
    private static void addCertificatesToKeystore(KeyStore sourceKeystore,
                                                  KeyStore targetKeystore,
                                                  Set<String> usedAliases) throws Exception {

        String methodName = "addCertificatesToKeystore()";
        Enumeration<String> aliases = sourceKeystore.aliases();
        int count = 0;

        while (aliases.hasMoreElements()) {
            String originalAlias = aliases.nextElement();

            if (sourceKeystore.isCertificateEntry(originalAlias)) {
                Certificate cert = sourceKeystore.getCertificate(originalAlias);

                // Create a unique alias
                String newAlias = originalAlias;
                int counter = 1;
                while (usedAliases.contains(newAlias)) {
                    newAlias = originalAlias + "_" + counter++;
                }

                usedAliases.add(newAlias);
                targetKeystore.setCertificateEntry(newAlias, cert);
                count++;
            }
        }

        knLogger.info(methodName, "Added " , count , " certificates");
    }

//    /**
//     * Main method for testing the merger
//     */
//    public static void main(String[] args) {
//        try {
//            String customTruststorePath = "src/main/resources/truststore.jks";
//            String customPassword = "msikodiak"; // From application.properties
//            String outputPath = "src/main/resources/merged-truststore-java.jks";
//            String outputPassword = "changeit";
//
//            System.out.println("Starting Java-based truststore merger...");
//            System.out.println("Custom truststore: " + customTruststorePath);
//            System.out.println("Output truststore: " + outputPath);
//
//            mergeTruststores(customTruststorePath, customPassword, outputPath, outputPassword);
//
//            System.out.println("\nTruststore merge completed successfully!");
//            System.out.println("Merged truststore created at: " + outputPath);
//            System.out.println("Output truststore password: " + outputPassword);
//
//        } catch (Exception e) {
//            System.err.println("Error during truststore merge: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
}
