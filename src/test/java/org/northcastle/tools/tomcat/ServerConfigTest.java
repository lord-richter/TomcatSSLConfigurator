/**
 * ServerConfigTest
 *
 * Version v1.0
 *
 * Copyright (c) Rob
 */
package org.northcastle.tools.tomcat;

import static org.junit.Assert.assertThrows;
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
class ServerConfigTest {

	static ServerConfig serverConfig;
	static Path configFile;
	static String testNoConnection = "src/test/resources/testDefault.xml";
	static Path testNoConnectionFile;
	static String testUpdateConnection = "src/test/resources/testUpdate.xml";
	static Path testUpdateConnectionFile;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		serverConfig = new ServerConfig();
		configFile = Paths.get(serverConfig.getServerConfigFile());
		testNoConnectionFile = Paths.get(testNoConnection);
		testUpdateConnectionFile = Paths.get(testUpdateConnection);
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
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterEach
	void tearDown() throws Exception {
	}

	/**
	 * Test basic class operations
	 */
	@Test
	void testBasics() {
		assertAll(new Executable() {

			@Override
			public void execute() throws Throwable {
				assertTrue(serverConfig.validateConfiguration());
			}
		});
	}

	/**
	 * Update config when file does not exist
	 */
	@Test
	void testConfigDefault() {
		assertAll(new Executable() {

			@Override
			public void execute() throws Throwable {
				new Tomcat().install();
				FileUtils.deleteQuietly(configFile.toFile());
				Files.copy(testNoConnectionFile, configFile);
				serverConfig.configureSSLConnection();
				String expected = serverConfig.config.getProperty(Configuration.CONFIGURATOR_CERTIFICATE_SSL_KEYSTORE);
				String content = Files.readString(configFile);
				assertTrue(content.contains(expected));
				assertTrue(content.contains(Configuration.CONFIGURATOR_CERTIFICATE_SSL_PASSWORD));
			}
		});
	}

	/**
	 * Update config when file does not exist
	 */
	@Test
	void testConfigFailure() {
		assertThrows(Exception.class, () -> {
			new Tomcat().remove();
			FileUtils.deleteQuietly(configFile.toFile());
			serverConfig.configureSSLConnection();
		});
	}

}
