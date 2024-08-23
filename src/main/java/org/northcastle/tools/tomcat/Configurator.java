/**
 * Configurator
 *
 * Version v1.0
 *
 * Copyright (c) Rob Richter
 */
package org.northcastle.tools.tomcat;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;

/**
 * This serves as a base and common class for the entire
 */
@Slf4j
public abstract class Configurator {

	private static final String propertyFile = "configurator.properties";
	protected static final Properties properties = new Properties();

	// define property keys as reusable strings to enforce consistency
	protected static final String CONFIGURATOR_TARGET_DIRECTORY = "configurator.target.directory";
	protected static final String CONFIGURATOR_TOMCAT_IMAGE_DIRECTORY = "configurator.tomcat.image.directory";
	protected static final String CONFIGURATOR_TOMCAT_IMAGE_ZIP_FILE = "configurator.tomcat.image.zip.file";
	protected static final String CONFIGURATOR_TOMCAT_IMAGE_ZIP_STRIPDIRECTORIES = "configurator.tomcat.image.zip.stripdirectories";
	protected static final String CONFIGURATOR_TOMCAT_FILE_SERVERXML = "configurator.tomcat.file.serverxml";
	protected static final String CONFIGURATOR_TOMCAT_FILE_CATALINAPROPERTIES = "configurator.tomcat.file.catalinaproperties";
	protected static final String CONFIGURATOR_TOMCAT_CONNECTOR_PORT = "configurator.tomcat.connector.port";
	protected static final String CONFIGURATOR_TOMCAT_CONNECTOR_PORT_SSL = "configurator.tomcat.connector.port.ssl";
	protected static final String CONFIGURATOR_CERTIFICATE_SSL_KEYSTORE = "configurator.certificate.ssl.keystore";
	protected static final String CONFIGURATOR_CERTIFICATE_SSL_SOURCE_KEYSTORE = "configurator.certificate.ssl.source.keystore";
	protected static final String CONFIGURATOR_CERTIFICATE_SSL_PASSWORD = "configurator.certificate.ssl.password";
	protected static final String CONFIGURATOR_CERTIFICATE_SSL_PROTOCOL = "configurator.certificate.ssl.protocol";
	protected static final String CONFIGURATOR_CERTIFICATE_SSL_ALGORITHM = "configurator.certificate.ssl.algorithm";
	protected static final String CONFIGURATOR_CERTIFICATE_SSL_OWNER = "configurator.certificate.ssl.owner";

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
		// if we have not read the properties, do it now, never do it again.
		if (properties.isEmpty()) {
			// read the configurator properties file
			log.info("Loading configuration from " + propertyFile);
			InputStream resource = Thread.currentThread().getContextClassLoader().getResourceAsStream(propertyFile);
			properties.load(resource);

			// pull in properties from the command line
			// add them to the class properties. this will override any properties
			// defined in the properties file
			// filter to address only properties we are interested in
			// we don't have to do this, but it removes debugging clutter
			String matchingString = "configurator\\..*";
			Pattern matchingPattern = Pattern.compile(matchingString);

			System.getProperties().forEach((key, value) -> {
				if (matchingPattern.matcher((String) key).matches()) {
					properties.put(key, value);
				}
			});

			// this is the time when the properties file was read.
			properties.put("time.start.milliseconds", Long.toString(System.currentTimeMillis()));
		}
	}

	// everyone needs to do this for the properties they care about
	protected abstract boolean validateConfiguration();
}
