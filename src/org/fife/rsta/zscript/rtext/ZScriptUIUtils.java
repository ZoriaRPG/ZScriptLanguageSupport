/*
 * 03/16/2014
 *
 * ZScriptUIUtils - Utility methods for the UI.
 * This library is distributed under a modified BSD license.  See the included
 * ZScriptLanguageSupport.License.txt file for details.
 */
package org.fife.rsta.zscript.rtext;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Utility methods for the plugin.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class ZScriptUIUtils {

	/**
	 * A mapping from HTML entity to the character it represents.
	 */
	private static final Map<String, String> replacementMap;

	// Regexes for manipulating HTML, argh!!
	private static final Pattern HTML_ESCAPE = Pattern.compile("&([^;]+);");

	static {
		replacementMap = new HashMap<String, String>();
		replacementMap.put("nbsp", " ");
		replacementMap.put("amp",  "&");
		replacementMap.put("lt",   "<");
		replacementMap.put("gt",   ">");
		replacementMap.put("quot", "\"");
		replacementMap.put("euro", "\u20ac");
		replacementMap.put("copy", "\u00a9");
	}


	/**
	 * Private constructor to prevent instantiation.
	 */
	private ZScriptUIUtils() {
	}


	/**
	 * Iterates over a collection of objects and joins them together with
	 * <code>", "</code>, for display purposes.
	 *
	 * @param c The collection, which may be <code>null</code>.
	 * @return A string representation of the collection's contents.
	 */
	public static final String prettyPrint(Collection<?> c) {

		if (c==null || c.isEmpty()) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		int size = c.size();

		int i=0;
		for (Object obj : c) {
			sb.append(obj);
			i++;
			if (i<size) {
				sb.append(", ");
			}
		}

		return sb.toString();

	}


	/**
	 * Replaces HTML entities in a string with the characters they represent.
	 *
	 * @param html The HTML.
	 * @return The string, with characters substituted.
	 */
	public static final String replaceEntities(String html) {

		StringBuffer sb = new StringBuffer();

		Matcher m = HTML_ESCAPE.matcher(html);
		while (m.find()) {
			String escape = m.group(1);
			String replacement = null;
			if (escape.charAt(0)=='#') {
				try {
					int value = Integer.parseInt(escape.substring(1));
					replacement = Character.toString((char)value);
				} catch (NumberFormatException nfe) {
					replacement = m.group(0);
				}
			}
			else {
				replacement = replacementMap.get(escape);
				if (replacement==null) { // ???
					replacement = m.group(0);
				}
			}
			// PureZC escapes backslashes as &#092;
			replacement = replacement.replaceAll("\\\\", "\\\\\\\\");
			m.appendReplacement(sb, replacement);
		}

		m.appendTail(sb);

		return sb.toString();

	}


}