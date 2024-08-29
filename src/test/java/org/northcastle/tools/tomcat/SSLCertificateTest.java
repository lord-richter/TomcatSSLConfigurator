/**
 * SSLCertificateTest
 *
 * Version v1.0
 *
 * Copyright (c) Rob
 */
package org.northcastle.tools.tomcat;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

/**
 *
 */
class SSLCertificateTest {

	static SSLCertificate certificate;
	static Path keystoreFile;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		certificate = new SSLCertificate();
		keystoreFile = Paths.get(certificate.getKeyStoreDirectory());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		System.out.println("-------------------------------------");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterEach
	void tearDown() throws Exception {

	}

	/**
	 * Test generate certificate happy path (password needs to be generated)
	 */
	@Test
	void testCertificateGenerate() {
		assertAll(new Executable() {

			@Override
			public void execute() throws Throwable {
				// delete target file
				FileUtils.deleteQuietly(keystoreFile.toFile());
				certificate.config.remove("configurator.certificate.ssl.password");
				certificate.config.remove("configurator.certificate.ssl.source.keystore");
				certificate.installCertificate();
				assertTrue(Files.exists(keystoreFile));
			}
		});
	}

	/**
	 * Test generate certificate happy path (password needs to be generated)
	 */
	@Test
	void testCertificateNoPasswordGenerate() {
		assertAll(new Executable() {

			@Override
			public void execute() throws Throwable {
				// delete target file
				FileUtils.deleteQuietly(keystoreFile.toFile());
				certificate.config.remove("configurator.certificate.ssl.password");
				certificate.config.setProperty("configurator.certificate.ssl.source.keystore",
						"src/main/resources/source.keystore");
				certificate.installCertificate();
				assertTrue(Files.exists(keystoreFile));
			}
		});
	}

	/**
	 * Test that we correctly detect when both password and key store are detected
	 */
	@Test
	void testExistingCertificate() {
		assertAll(new Executable() {

			@Override
			public void execute() throws Throwable {
				// make sure there is a file there
				if (!Files.exists(keystoreFile)) {
					Files.createDirectories(keystoreFile.getParent());
					Files.createFile(keystoreFile);
				}
				// make sure a password is set
				certificate.config.setProperty("configurator.certificate.ssl.password", "defaultPassword");
				assertTrue(certificate.checkCertificate());
			}
		});
	}

	/**
	 * Test generate certificate happy path (password specified)
	 */
	@Test
	void testHappyCertificate() {
		assertAll(new Executable() {

			@Override
			public void execute() throws Throwable {
				// delete target file
				FileUtils.deleteQuietly(keystoreFile.toFile());
				// make sure there is a source keystore
				certificate.config.setProperty("configurator.certificate.ssl.source.keystore",
						"src/main/resources/source.keystore");
				// make sure there is a password set
				certificate.config.setProperty("configurator.certificate.ssl.password", "defaultPassword");
				certificate.installCertificate();
				assertTrue(Files.exists(keystoreFile));
			}
		});
	}

	/**
	 * Test that we correctly detect when no password was provided
	 */
	@Test
	void testMissingKeystore() {
		assertAll(new Executable() {

			@Override
			public void execute() throws Throwable {
				// make sure there is NO file there
				FileUtils.deleteQuietly(keystoreFile.toFile());
				// make sure there is a password set (not needed)
				certificate.config.setProperty("configurator.certificate.ssl.password", "defaultPassword");
				assertFalse(certificate.checkCertificate());
			}
		});
	}

	/**
	 * Test that we correctly detect when no password was provided
	 */
	@Test
	void testMissingPassword() {
		assertAll(new Executable() {

			@Override
			public void execute() throws Throwable {
				// make sure there is a file there
				if (!Files.exists(keystoreFile)) {
					Files.createDirectories(keystoreFile.getParent());
					Files.createFile(keystoreFile);
				}
				// make sure a password is NOT set
				certificate.config.remove("configurator.certificate.ssl.password");
				assertFalse(certificate.checkCertificate());
			}
		});
	}

}
