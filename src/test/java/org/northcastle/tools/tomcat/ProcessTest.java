/**
 * ProcessTest
 *
 * Version v1.0
 *
 * Copyright (c) Rob
 */
package org.northcastle.tools.tomcat;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

/**
 *
 */
class ProcessTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
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
	 *
	 */
	@Test
	void testCatalina() {
		assertAll(new Executable() {

			@Override
			public void execute() throws Throwable {
				Tomcat tomcat = new Tomcat();
				tomcat.config.setProperty("configurator.tomcat.image.directory","src/main/resources/10.1.28-Windows-x64");
				tomcat.config.setProperty("configurator.tomcat.image.zip.file","apache-tomcat-10.1.28-windows-x64.zip");
				tomcat.config.setProperty("configurator.tomcat.image.zip.stripdirectories","1");
				
				tomcat.install();
				
				Process process = new Process();
				process.config.setProperty("configurator.certificate.ssl.password", "defaultPassword");

				Path catalinaFile = process.updateCatalinaProperties();
				String content = Files.readString(catalinaFile);
				assertTrue(content.contains("defaultPassword"));
			}
		});
	}

	/**
	 *
	 */
	@Test
	void testCatalinaFileNotFound() {
		assertThrows(FileNotFoundException.class, () -> {
			new Tomcat().remove();
			Process process = new Process();
			process.updateCatalinaProperties();
		});
	}

}
