package edu.pitt.sis.infsci2140.analysis;

/**
 * This class transforms the char[] into String, then uses the method
 * String.toLowerCase to normalize the word. And return the corresponding char[]
 * of the String.
 * 
 */
public class TextNormalizer {

	// YOU MUST IMPLEMENT THIS METHOD
	public static String normalize(String str) {
		// return the normalized version of the word characters (replacing all
		// uppercase characters into the corresponding lowercase characters)
		String str_u = str.toLowerCase();
		return str_u;
	}

}
