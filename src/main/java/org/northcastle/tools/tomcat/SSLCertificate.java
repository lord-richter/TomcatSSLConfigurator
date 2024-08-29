/**
 * Certificate
 *
 * Version v1.0
 *
 * Copyright (c) Rob
 */
package org.northcastle.tools.tomcat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.cert.Certificate;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.northcastle.security.Keys;
import org.northcastle.security.certificate.X509;

import lombok.extern.slf4j.Slf4j;

/**
 * This class generates a self-signed TLS/SSL certificate compatible with Tomcat.
 */
@Slf4j
public class SSLCertificate extends Configurator {

	Path targetFile;

	/**
	 * @param properties
	 *
	 */
	public SSLCertificate() throws IOException {
		if (!validateConfiguration()) {
			throw new RuntimeException("The certificate keystore was not provided in the configuration");
		}

		targetFile = Paths.get(config.getProperty(Configuration.CONFIGURATOR_TARGET_DIRECTORY), "conf",
				config.getProperty(Configuration.CONFIGURATOR_CERTIFICATE_SSL_KEYSTORE)).normalize();
		log.info("Expected location for the keystore file will be: " + targetFile);
	}

	/**
	 * Verify that the target certificate file exists and that there is a password
	 * on file. This does not verify that the password actually works.
	 *
	 * @return true if certificate and password can be found
	 */
	public boolean checkCertificate() {
		boolean ok = false;

		// if the file exists at the target location, make sure we have a password on
		// file
		if (Files.exists(targetFile)) {
			ok = config.containsKey(Configuration.CONFIGURATOR_CERTIFICATE_SSL_PASSWORD);
		}

		return ok;
	}

	/**
	 * Generate a new self-signed certificate. The certificate will be stored in the
	 * key store specified by the configuration. If no password is configured, one
	 * will be provided, and properties will be updated.
	 *
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	private void generateNewCertificate() throws GeneralSecurityException, IOException {

		// make sure we have certificate information
		String certificateSubject = config.getProperty(Configuration.CONFIGURATOR_CERTIFICATE_SSL_OWNER);
		if (certificateSubject == null) {
			throw new RuntimeException("Certificate owner/subject needs to be configured.");
		}

		// handle passwords
		String password = config.getProperty(Configuration.CONFIGURATOR_CERTIFICATE_SSL_PASSWORD);
		if (password == null) {
			// random 15 letters
			password = RandomStringUtils.randomAlphabetic(15);
			// save new password for later
			config.setProperty(Configuration.CONFIGURATOR_CERTIFICATE_SSL_PASSWORD, password);
			log.info("Randomly generated password for certificate");

		}

		// generate key pair
		KeyPair keyPair = Keys.generateKeyPair(4096);

		// build certificate chain, default to SHA256+RSA and valid 365 days
		Certificate[] chain = { X509.generateSelfSignedCertificate(certificateSubject, keyPair, 365) };

		// make the keystore to put it in
		KeyStore store = Keys.createKeyStore("PKCS12", keyPair, password, chain);

		// save the keystore to the conf directory
		Keys.createKeyStoreFile(store, targetFile, password);

		log.info("Generated key store: " + targetFile);
	}

	/**
	 * This returns the current installation directory for the keystore file
	 *
	 * @return absolute installation directory
	 */
	public String getKeyStoreDirectory() {
		return targetFile.toAbsolutePath().toString();
	}

	/**
	 * Install or Generate certificate If one is already installed, this just
	 * returns If one is not installed, but there is a pre-staged one, then copy
	 * that and return Otherwise, generate the new certificate.
	 *
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public void installCertificate() throws IOException, GeneralSecurityException {
		// is the certificate already there?
		if (checkCertificate()) {
			log.info("Certificate installed, password available");
			return;
		}

		// is there a pre-staged source certificate specified?
		if (config.containsKey(Configuration.CONFIGURATOR_CERTIFICATE_SSL_SOURCE_KEYSTORE)
				&& config.containsKey(Configuration.CONFIGURATOR_CERTIFICATE_SSL_PASSWORD)) {
			Path sourceCertificate = Paths.get(config.getProperty(Configuration.CONFIGURATOR_CERTIFICATE_SSL_SOURCE_KEYSTORE));
			log.info("Using pre-staged certificate " + sourceCertificate);
			if (Files.exists(sourceCertificate)) {
				FileUtils.deleteQuietly(targetFile.toFile());
				Files.copy(sourceCertificate, targetFile);
			} else {
				throw new RuntimeException("Source certificate was not found");
			}
		}

		// is the certificate there?
		if (checkCertificate()) {
			log.info("Certificate installed, password available");
			return;
		}

		// make a new one
		generateNewCertificate();

		// log results
		log.info("Certificate installed: " + checkCertificate());

	}

	@Override
	protected boolean validateConfiguration() {
		return config.containsKey(Configuration.CONFIGURATOR_CERTIFICATE_SSL_KEYSTORE);
	}

}
