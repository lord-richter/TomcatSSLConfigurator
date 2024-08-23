/**
 * Keys
 * Common keypair and keystore utility class
 *
 * Version v1.0
 *
 * Copyright (c) Rob Richter
 */

package org.northcastle.security;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

/**
 * Convenience class for handling keys and key stores
 */
public class Keys {

	/**
	 * Create a new keystore and update it with the key pair and certificate chain
	 *
	 * @param storeType key store type, which must be a valid provider
	 * @param pair      key pair to store into the key store
	 * @param password  password for the key store
	 * @param chain     the certificate chain that will include the key pair
	 * @return updated key store
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 * @throws IOException
	 * @throws KeyStoreException
	 */
	public static KeyStore createKeyStore(String storeType, KeyPair pair, String password, Certificate[] chain)
			throws NoSuchAlgorithmException, CertificateException, IOException, KeyStoreException {
		KeyStore keyStore = KeyStore.getInstance(storeType);
		keyStore.load(null, null); // empty key store
		keyStore.setKeyEntry("main", pair.getPrivate(), password.toCharArray(), chain);
		return keyStore;
	}

	/**
	 * Write key store file as a new file. This will delete any existing key store
	 * of the same name.
	 *
	 * @param keyStore
	 * @param keyStoreFile
	 * @param password
	 * @throws IOException
	 * @throws CertificateException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 */
	public static void createKeyStoreFile(KeyStore keyStore, Path keyStoreFile, String password)
			throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		if (keyStoreFile == null) {
			throw new IOException("Invalid key store file name");
		}

		Files.deleteIfExists(keyStoreFile);
		Files.createDirectories(keyStoreFile.getParent());

		FileOutputStream outStream = new FileOutputStream(keyStoreFile.toFile());

		keyStore.store(outStream, password.toCharArray());

		outStream.close();

	}

	/**
	 * Generate an RSA key pair with the specified key size
	 *
	 * @param keysize key size of 4096 bits is recommended
	 * @return generated KeyPair
	 * @throws NoSuchAlgorithmException
	 */
	public static KeyPair generateKeyPair(int keysize) throws NoSuchAlgorithmException {
		KeyPairGenerator keyPair = KeyPairGenerator.getInstance("RSA");
		keyPair.initialize(keysize);
		return keyPair.generateKeyPair();
	}

	/**
	 * This class does not need to be instantiated
	 */
	private Keys() {
	}
}
