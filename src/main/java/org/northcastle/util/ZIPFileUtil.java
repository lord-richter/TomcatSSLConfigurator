/**
 * ZIPFileUtil
 *
 * Version v1.0
 *
 * Copyright (c) Rob Richter
 */
package org.northcastle.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZIPFileUtil {

	/**
	 * Unpack a ZIP file, do not overwrite, do not strip any directories
	 *
	 * @param sourceFile location of the ZIP file
	 * @param targetDir  target directory where unpack will happen
	 * @throws IOException
	 */
	public static void expandZIPFile(Path sourceFile, Path targetDir) throws IOException {
		ZIPFileUtil.expandZIPFile(sourceFile, targetDir, false, 0);
	}

	/**
	 * Unpack a ZIP file, allowing customization of overwrite and optional removal
	 * of leading directories
	 *
	 * @param sourceFile       location of the ZIP file
	 * @param targetDir        target directory where unpack will happen
	 * @param overwrite        when true, file from the ZIP will replace files in
	 *                         the file system
	 * @param trimPathElements number of leading directories stored in the zip file
	 *                         to remove, 0 to not remove any
	 * @throws IOException
	 */
	public static void expandZIPFile(Path sourceFile, Path targetDir, boolean overwrite, int trimPathElements)
			throws IOException {
		byte[] buffer = new byte[1024];

		// source file must exist
		if (!Files.exists(sourceFile)) {
			throw new IOException();
		}

		// create target directory if it does not exist
		if (!Files.exists(targetDir)) {
			Files.createDirectories(targetDir);
		}

		ZipInputStream zipInput = new ZipInputStream(new FileInputStream(sourceFile.toFile()));
		ZipEntry zipEntry = zipInput.getNextEntry();
		while (zipEntry != null) {
			Path zipFileEntry = Paths.get(zipEntry.getName());

			// strip off leading path elements
			if (trimPathElements < zipFileEntry.getNameCount()) {
				zipFileEntry = zipFileEntry.subpath(trimPathElements, zipFileEntry.getNameCount());

				// place the file in the proper output location
				Path nextFile = targetDir.resolve(zipFileEntry);

				// if the file does not exist, or overwrite, then handle
				if (!Files.exists(nextFile) || overwrite) {
					// handle zip file directory entries
					if (zipEntry.isDirectory()) {
						Files.createDirectories(nextFile);
						System.out.println("MKDIR: " + nextFile);
					} else {

						// handle zip files without directory entries
						// these directories need to be created if they don't exist
						Path parentDir = nextFile.getParent();
						if (parentDir != null && !Files.exists(parentDir)) {
							Files.createDirectories(parentDir);
							System.out.println("MKDIR: " + parentDir);
						}

						// unpack the file into the directory
						System.out.println("EXPANDING: " + nextFile);
						FileOutputStream fileOut = new FileOutputStream(nextFile.toFile());
						int len;
						while ((len = zipInput.read(buffer)) > 0) {
							fileOut.write(buffer, 0, len);
						}
						fileOut.close();
					}
				} else {
					System.out.println("SKIPPING: " + nextFile);
				}
			} else {
				System.out.println("SKIPPING: " + zipFileEntry);
			}
			zipEntry = zipInput.getNextEntry();
		}

		zipInput.close();
	}

	/**
	 * This class is never instantiated
	 */
	private ZIPFileUtil() {
	}
}
