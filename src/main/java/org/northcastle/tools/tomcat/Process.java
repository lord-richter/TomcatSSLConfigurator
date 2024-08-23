/**
 * Configurator
 *
 * Version v1.0
 *
 * Copyright (c) Rob
 */
package org.northcastle.tools.tomcat;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.jdom2.JDOMException;
import org.xml.sax.SAXException;

import lombok.extern.slf4j.Slf4j;

/**
 * This is the main entry point into the configuration process. The process()
 * method controls the workflow
 */
@Slf4j
public class Process extends Configurator {

	/**
	 * Default constructor, which reads in the configuration from the properties
	 * file
	 *
	 * @throws IOException
	 *
	 */
	public Process() throws IOException {
		if (!validateConfiguration()) {
			throw new RuntimeException("Missing one or more processing properties.");
		}
		log.info("Starting processing");
	}

	/**
	 * This is the main process script
	 *
	 * @throws IOException
	 * @throws JDOMException
	 * @throws GeneralSecurityException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 */
	public void process() throws IOException, JDOMException, GeneralSecurityException {

		// is tomcat installed?
		// if it is not, try to install it. Get actual install location for later.
		Tomcat tomcat = new Tomcat();
		if (!tomcat.isInstalled()) {
			properties.put(CONFIGURATOR_TARGET_DIRECTORY, tomcat.install());
		} else {
			properties.put(CONFIGURATOR_TARGET_DIRECTORY, tomcat.getInstallDirectory());
		}

		// do we need a certificate?
		SSLCertificate certificate = new SSLCertificate();
		// if the certificate is not specified, cannot be found, or is missing
		// configuration
		// information, but we can still make a certificate, then do that.
		certificate.installCertificate();

		// update server.xml
		ServerConfig serverxml = new ServerConfig();
		serverxml.configureSSLConnection();

		// update catalina.properties
		updateCatalinaProperties();

	}

	/**
	 * Read the Tomcat catalina.properties file, add or update the secret, write
	 * updated properties file. This is expected to be done after the certificate
	 * and certificate password are dealt with
	 *
	 * @throws IOException
	 */
	protected Path updateCatalinaProperties() throws IOException {
		// locate the catalina properties file
		Path catalinaPath = Paths.get(properties.getProperty(CONFIGURATOR_TARGET_DIRECTORY),
				properties.getProperty(CONFIGURATOR_TOMCAT_FILE_CATALINAPROPERTIES));
		InputStream resource = new FileInputStream(catalinaPath.toFile());

		// read properties and close file
		Properties catalina = new Properties();
		catalina.load(resource);
		resource.close();

		// store password property
		catalina.setProperty(CONFIGURATOR_CERTIFICATE_SSL_PASSWORD,
				properties.getProperty(CONFIGURATOR_CERTIFICATE_SSL_PASSWORD));

		// write the catalina properties file
		catalina.store(new FileOutputStream(catalinaPath.toFile()), null);

		return catalinaPath;
	}

	/**
	 * Make sure that certain properties are set
	 *
	 * @return
	 */
	@Override
	protected boolean validateConfiguration() {
		return properties.containsKey(CONFIGURATOR_TARGET_DIRECTORY);
	}

}
