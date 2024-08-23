/**
 * Tomcat
 *
 * Version v1.0
 *
 * Copyright (c) Rob Richter
 */
package org.northcastle.tools.tomcat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.northcastle.util.ZIPFileUtil;

import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Slf4j
public class Tomcat extends Configurator {

	public static void main(String[] args) {
		try {
			Tomcat t = new Tomcat();
			t.install();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private Path targetDir;

	/**
	 * Default constructor
	 */
	public Tomcat() throws IOException {
		if (!validateConfiguration()) {
			throw new RuntimeException("Tomcat configuration is missing required properties");
		}

		// convert the target property into a Path for later
		targetDir = Paths.get(properties.getProperty(CONFIGURATOR_TARGET_DIRECTORY));

		log.info("Tomcat is currently " + (isInstalled() ? "Installed" : "Not Installed"));
	}

	/**
	 * This returns the current absolute installation directory for Tomcat
	 *
	 * @return absolute installation directory
	 */
	public String getInstallDirectory() {
		return targetDir.toAbsolutePath().toString();
	}

	/**
	 * Install tomcat from the configured source zip file.
	 *
	 * @return absolute installation directory
	 * @throws IOException
	 */
	public String install() throws IOException {
		// check whether there is an install image
		if (!properties.containsKey(CONFIGURATOR_TOMCAT_IMAGE_DIRECTORY) || !properties.containsKey(CONFIGURATOR_TOMCAT_IMAGE_ZIP_FILE)) {
			throw new RuntimeException("Tomcat is not installed and no installation image was provided.");
		}
		
		
		Path installSource = Paths.get(properties.getProperty(CONFIGURATOR_TOMCAT_IMAGE_DIRECTORY),
				properties.getProperty(CONFIGURATOR_TOMCAT_IMAGE_ZIP_FILE));

		// overwrite default is true
		boolean overwrite = true;

		// the option to remove leading directories defaults to 0 if not specified
		int stripdir = (properties.containsKey(CONFIGURATOR_TOMCAT_IMAGE_ZIP_STRIPDIRECTORIES)
				? Integer.parseInt(properties.getProperty(CONFIGURATOR_TOMCAT_IMAGE_ZIP_STRIPDIRECTORIES))
				: 0);

		// call ZIP to unpack
		ZIPFileUtil.expandZIPFile(installSource, targetDir, overwrite, stripdir);

		return getInstallDirectory();
		 
	}

	/**
	 * Check the target directory to see if Tomcat is installed there. It looks for
	 * the conf directory, then the server.xml and the catalina.properties files.
	 * All three must exist.
	 *
	 * @return true if installed
	 */
	public boolean isInstalled() {
		boolean installed = false;

		// get for target directory + conf directory
		installed = Files.exists(Paths.get(getInstallDirectory(), "conf"))
				&& Files.exists(
						Paths.get(getInstallDirectory(), properties.getProperty(CONFIGURATOR_TOMCAT_FILE_SERVERXML)))
				&& Files.exists(Paths.get(getInstallDirectory(),
						properties.getProperty(CONFIGURATOR_TOMCAT_FILE_CATALINAPROPERTIES)));

		return installed;

	}

	/**
	 * Delete anything that is previously installed at the target location
	 *
	 * @throws IOException
	 */
	public boolean remove() throws IOException {
		log.info("Deleting anything that can be found at " + targetDir.toString());
		FileUtils.deleteQuietly(targetDir.toFile());

		return !Files.exists(targetDir);
	}

	@Override
	protected boolean validateConfiguration() {
		return properties.containsKey(CONFIGURATOR_TARGET_DIRECTORY)
				&& properties.containsKey(CONFIGURATOR_TOMCAT_FILE_CATALINAPROPERTIES)
				&& properties.containsKey(CONFIGURATOR_TOMCAT_FILE_SERVERXML);
	}
}
