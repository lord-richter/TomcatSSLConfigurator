/**
 * Configurator
 *
 * Version v1.0
 *
 * Copyright (c) Rob Richter
 */
package org.northcastle.tools.tomcat;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

/**
 * This serves as a base and common class for the entire
 */
@Slf4j
public abstract class Configurator {
	
	protected Configuration config;

	/**
	 * This is the main that gets used when running this application
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Process c = new Process();
			c.process();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Construct class using default configuration file
	 *
	 * @throws IOException
	 */
	public Configurator() throws IOException {
		// each instance gets a reference to the configuration
		config = Configuration.getInstance();
	}

	// everyone needs to do this for the properties they care about
	protected abstract boolean validateConfiguration();
}
