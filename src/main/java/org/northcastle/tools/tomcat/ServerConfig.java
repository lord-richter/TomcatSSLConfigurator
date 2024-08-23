/**
 * ServerConfig
 *
 * Version v1.0
 *
 * Copyright (c) Rob
 */
package org.northcastle.tools.tomcat;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.northcastle.xml.XMLUtil;
import org.xml.sax.SAXException;

import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Slf4j
public class ServerConfig extends Configurator {

	private static final String ELEMENT_SERVICE = "Service";
	private static final String ELEMENT_CONNECTOR = "Connector";
	private static final String ELEMENT_SSLHOSTCONFIG = "SSLHostConfig";
	private static final String ELEMENT_CERTIFICATE = "Certificate";
	private static final String ELEMENT_UPGRADEPROTOCOL = "UpgradeProtocol";
	private static final String ATTRIBUTE_PORT = "port";
	private static final String ATTRIBUTE_PROTOCOL = "protocol";
	private static final String ATTRIBUTE_REDIRECTPORT = "redirectPort";
	private static final String ATTRIBUTE_SSLENABLED = "SSLEnabled";
	private static final String ATTRIBUTE_MAXTHREADS = "maxThreads";
	private static final String ATTRIBUTE_CLASSNAME = "className";

	private static final String ATTRIBUTE_MAXPARMCOUNT = "maxParameterCount";
	private static final String ATTRIBUTE_KEYSTORE = "certificateKeystoreFile";
	private static final String ATTRIBUTE_KEYPASS = "certificateKeystorePassword";
	private static final String ATTRIBUTE_KEYTYPE = "type";

	private Path serverFile = null;
	private Path backupServerFile = null;

	private Document serverDocument = null;
	private List<Element> serviceList = null;
	private List<Element> connectorList = null;

	/**
	 * Constructor for handling the server configuration file
	 *
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	public ServerConfig() throws IOException {
		validateConfiguration();

		serverFile = Paths.get(properties.getProperty(CONFIGURATOR_TARGET_DIRECTORY),
				properties.getProperty(CONFIGURATOR_TOMCAT_FILE_SERVERXML));
		backupServerFile = Paths.get(properties.getProperty(CONFIGURATOR_TARGET_DIRECTORY),
				properties.getProperty(CONFIGURATOR_TOMCAT_FILE_SERVERXML) + ".backup");
		log.info("Server configuration file located at: " + serverFile);
	}

	/**
	 * This handles the update to server.xml. It backs up the XML file, reads the
	 * file, updates it, and writes it out in a single method. This is done in a
	 * single method to avoid mistakes if someone tries to have two of these classes
	 * open, or tries to call things multiple times.
	 *
	 * @throws IOException
	 * @throws JDOMException
	 *
	 */
	public void configureSSLConnection() throws IOException, JDOMException {
		// copy the server.xml file to a backup copy that will be used for reading
		Files.deleteIfExists(backupServerFile);
		Files.copy(serverFile, backupServerFile);

		readFile(backupServerFile);

		// iterate through connections looking for nodes we can configure SSL
		boolean foundSSLNode = false;
		for (Element element : connectorList) {
			// if this is an SSL port using the port number configured
			if (element.hasAttributes() && element.getAttribute(ATTRIBUTE_PORT) != null
					&& element.getAttributeValue(ATTRIBUTE_PORT)
							.equalsIgnoreCase(properties.getProperty(CONFIGURATOR_TOMCAT_CONNECTOR_PORT_SSL))) {
				// this is an SSL connector, so get all certificates element buried in it
				// if there are more than one, we will take the first one for our purposes
				List<Element> certificateNodes = XMLUtil.getElementsByType(element, ELEMENT_CERTIFICATE);
				if (certificateNodes.size() > 0) {
					Element certificateNode = certificateNodes.get(0);

					// configure the SSL
					certificateNode.setAttribute(ATTRIBUTE_KEYTYPE,
							properties.getProperty(CONFIGURATOR_CERTIFICATE_SSL_ALGORITHM));
					certificateNode.setAttribute(ATTRIBUTE_KEYSTORE,
							"conf/" + properties.getProperty(CONFIGURATOR_CERTIFICATE_SSL_KEYSTORE));
					certificateNode.setAttribute(ATTRIBUTE_KEYPASS, "${" + CONFIGURATOR_CERTIFICATE_SSL_PASSWORD + "}");
				} else {
					// we arrive here when there is no certificate, which is an error we do not
					// handle
					throw new RuntimeException("Malformed XML detected.  Connector does not have Certificate");
				}

				// update the connector attributes that we handle
				element.setAttribute(ATTRIBUTE_SSLENABLED, "true");
				element.setAttribute(ATTRIBUTE_PROTOCOL, properties.getProperty(CONFIGURATOR_CERTIFICATE_SSL_PROTOCOL));
				// remember that we found a node
				foundSSLNode = true;
				// this is not an SSL port, but it is the expected non-SSL port, set redirect
			} else if (element.hasAttributes() && element.getAttribute(ATTRIBUTE_PORT) != null
					&& element.getAttributeValue(ATTRIBUTE_PORT)
							.equalsIgnoreCase(properties.getProperty(CONFIGURATOR_TOMCAT_CONNECTOR_PORT))) {

				// even if this is already set, go ahead and set it again
				element.setAttribute(ATTRIBUTE_REDIRECTPORT,
						properties.getProperty(CONFIGURATOR_TOMCAT_CONNECTOR_PORT_SSL));
			}
		}

		// handle case where the SSL was not already configured and we need to do that
		if (!foundSSLNode) {
			// the out of the box config for Tomcat has the SSL port commented out
			serviceList.get(0).addContent(createSSLConnectorNode());
		}

		// write the XML file, but delete old one first
		Files.deleteIfExists(serverFile);

		FileOutputStream outputStream = new FileOutputStream(serverFile.toFile());
		XMLOutputter output = new XMLOutputter();
		output.setFormat(Format.getPrettyFormat());
		output.output(serverDocument, outputStream);
	}

	/**
	 * This is necessary, as the default config SSL node is not present in the
	 * document. Create a new tree, set the attributes, and return it so that it can
	 * be added to the Service element.
	 *
	 * @return new Connector node
	 */
	private Element createSSLConnectorNode() {
		// these are the elements that are part of the Connector
		Element certificate = new Element(ELEMENT_CERTIFICATE);
		Element hostconfig = new Element(ELEMENT_SSLHOSTCONFIG);
		Element upgrade = new Element(ELEMENT_UPGRADEPROTOCOL);
		Element connector = new Element(ELEMENT_CONNECTOR);

		// define the certificate. All of the attributes are configurable
		certificate.setAttribute(ATTRIBUTE_KEYTYPE, properties.getProperty(CONFIGURATOR_CERTIFICATE_SSL_ALGORITHM));
		certificate.setAttribute(ATTRIBUTE_KEYSTORE,
				"conf/" + properties.getProperty(CONFIGURATOR_CERTIFICATE_SSL_KEYSTORE));
		certificate.setAttribute(ATTRIBUTE_KEYPASS, "${" + CONFIGURATOR_CERTIFICATE_SSL_PASSWORD + "}");

		// hard coded until such time as it becomes configurable
		upgrade.setAttribute(ATTRIBUTE_CLASSNAME, "org.apache.coyote.http2.Http2Protocol");

		// many values hard coded until configurable
		connector.setAttribute(ATTRIBUTE_PORT, properties.getProperty(CONFIGURATOR_TOMCAT_CONNECTOR_PORT_SSL));
		connector.setAttribute(ATTRIBUTE_PROTOCOL, properties.getProperty(CONFIGURATOR_CERTIFICATE_SSL_PROTOCOL));
		connector.setAttribute(ATTRIBUTE_MAXTHREADS, "150");
		connector.setAttribute(ATTRIBUTE_MAXPARMCOUNT, "1000");
		connector.setAttribute(ATTRIBUTE_SSLENABLED, "true");

		// chain everything together
		hostconfig.setContent(certificate);
		connector.setContent(upgrade);
		connector.setContent(hostconfig);

		return connector;

	}

	/**
	 * This returns the target directory and name of the server config file
	 *
	 * @return absolute directory
	 */
	public String getServerConfigFile() {
		return serverFile.toAbsolutePath().toString();
	}

	/**
	 * Standard method to read an XML file into a DOM. This will also find and
	 * remember Service and Connector nodes. If there are multiple Service nodes, an
	 * exception will be thrown at run time
	 *
	 * @param backupServerFile
	 * @throws IOException
	 * @throws JDOMException
	 */
	private void readFile(Path backupServerFile) throws JDOMException, IOException {

		// read in XML file
		SAXBuilder builder = new SAXBuilder();
		serverDocument = builder.build(backupServerFile.toFile());

		Element root = serverDocument.getRootElement();

		// get the service nodes. Configuring multiple services is something that could
		// be added in the future, if desired. We will be using the first one.
		serviceList = XMLUtil.getElementsByType(root, ELEMENT_SERVICE);
		if (serviceList.size() != 1) {
			throw new RuntimeException("This utility requires one, and only one, Service element");
		}

		// get all of the connector nodes.
		connectorList = XMLUtil.getElementsByType(root, ELEMENT_CONNECTOR);
	}

	@Override
	protected boolean validateConfiguration() {
		return properties.containsKey(CONFIGURATOR_CERTIFICATE_SSL_ALGORITHM)
				&& properties.containsKey(CONFIGURATOR_CERTIFICATE_SSL_KEYSTORE)
				&& properties.containsKey(CONFIGURATOR_CERTIFICATE_SSL_PROTOCOL)
				&& properties.containsKey(CONFIGURATOR_TOMCAT_CONNECTOR_PORT_SSL);
	}

}
