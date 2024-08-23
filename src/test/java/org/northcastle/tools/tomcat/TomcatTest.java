/**
 * TomcatTest
 *
 * Version v1.0
 *
 * Copyright (c) Rob
 */
package org.northcastle.tools.tomcat;

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
		tomcatClass.remove();
		tomcatClass.install();
		assertTrue(tomcatClass.isInstalled());
	}

	/**
	 * @throws Exception
	 *
	 */
	@Test
	void testSuccessiveInstall() throws Exception {
		tomcatClass.install();
		// note that this deletes
		tomcatClass.install();
		assertTrue(tomcatClass.isInstalled());
	}

}
