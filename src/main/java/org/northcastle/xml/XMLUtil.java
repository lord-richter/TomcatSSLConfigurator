/**
 * XMLUtil
 *
 * Version v1.0
 *
 * Copyright (c) Rob Richter
 */
package org.northcastle.xml;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;

/**
 * This is an XML helper class to offload commonly used functions
 */
public class XMLUtil {

	/**
	 * Search for all elements in a document that match a type
	 *
	 * @param rootElement root element to search from
	 * @param elementType element type to find
	 * @return list of elements that match the type
	 */
	public static List<Element> getElementsByType(Element rootElement, String elementType) {
		List<Element> matchingElements = new ArrayList<>();
		searchElementsByType(rootElement, elementType, matchingElements);
		return matchingElements;
	}

	/**
	 * Traverse DOM recursively for elements that match the specified type
	 *
	 * @param element          Element to start search at
	 * @param elementType      Name of the element to search fore
	 * @param matchingElements reference to previously located matching elements
	 */
	private static void searchElementsByType(Element element, String elementType, List<Element> matchingElements) {
		if (element.getName().equals(elementType)) {
			matchingElements.add(element);
		}
		// Recursively search child elements
		for (Element child : element.getChildren()) {
			searchElementsByType(child, elementType, matchingElements);
		}
	}

	/**
	 * This class does not need to be instantiated
	 */
	private XMLUtil() {
	}

}
