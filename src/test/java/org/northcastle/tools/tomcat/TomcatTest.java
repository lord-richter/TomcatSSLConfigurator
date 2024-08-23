/**
 * TomcatTest
 *
 * Version v1.0
 *
 * Copyright (c) Rob
 */
package org.northcastle.tools.tomcat;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 */
class TomcatTest {

	static Tomcat tomcatClass;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		tomcatClass = new Tomcat();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterAll
	static void tearDownAfterClass() throws Exception {
		tomcatClass.remove();
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
	 * Test method for
	 * {@link org.northcastle.tools.tomcat.Tomcat#getInstallDirectory()}.
	 */
	@Test
	void testGetInstallDirectory() {
		String testdir = tomcatClass.getInstallDirectory();
		System.out.println(testdir);
	}

	/**
	 * @throws Exception
	 *
	 */
	@Test
	void testNewInstall() throws Exception {
		Tomcat.properties.setProperty("configurator.tomcat.image.directory","src/main/resources/10.1.28-Windows-x64");
		Tomcat.properties.setProperty("configurator.tomcat.image.zip.file","apache-tomcat-10.1.28-windows-x64.zip");
		Tomcat.properties.setProperty("configurator.tomcat.image.zip.stripdirectories","1");		
		tomcatClass.remove();
		tomcatClass.install();
		assertTrue(tomcatClass.isInstalled());
	}
	
	/**
	 * @throws Exception
	 *
	 */
	@Test
	void testNoImageDirectory() throws Exception {
		Tomcat.properties.remove("configurator.tomcat.image.directory");
		Tomcat.properties.setProperty("configurator.tomcat.image.zip.file","apache-tomcat-10.1.28-windows-x64.zip");
		tomcatClass.remove();
		assertThrows(RuntimeException.class,() -> {
			tomcatClass.install();	
		});
	}
	
	/**
	 * @throws Exception
	 *
	 */
	@Test
	void testNoImageFile() throws Exception {
		Tomcat.properties.setProperty("configurator.tomcat.image.directory","src/main/resources/10.1.28-Windows-x64");
		Tomcat.properties.remove("configurator.tomcat.image.zip.file");
		tomcatClass.remove();
		assertThrows(RuntimeException.class,() -> {
			tomcatClass.install();	
		});
	}	

	/**
	 * @throws Exception
	 *
	 */
	@Test
	void testSuccessiveInstall() throws Exception {
		// make sure we are configured to install
		Tomcat.properties.setProperty("configurator.tomcat.image.directory","src/main/resources/10.1.28-Windows-x64");
		Tomcat.properties.setProperty("configurator.tomcat.image.zip.file","apache-tomcat-10.1.28-windows-x64.zip");
		Tomcat.properties.setProperty("configurator.tomcat.image.zip.stripdirectories","1");
		tomcatClass.install();
		// note that this deletes the previous install
		tomcatClass.install();
		assertTrue(tomcatClass.isInstalled());
	}

}
