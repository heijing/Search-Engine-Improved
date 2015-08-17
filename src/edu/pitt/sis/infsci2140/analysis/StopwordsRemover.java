package edu.pitt.sis.infsci2140.analysis;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

/**
 * HashMap is a great structure for searching, which is very fast. I set the
 * stop words as key, and "true" as their value. Once the key can be found,
 * means that word is a stop word.
 * 
 * HashMap actually is not the best choice, because it will contain a redundant boolean value.
 * HashSet is the best choice with just String in it.
 * 
 */
public class StopwordsRemover {

//	Map<String, Object> sw = new HashMap<String, Object>();
	Set<String> sw = new HashSet<String>();
	BufferedReader reader_sw = null;
	String line_sw = null;

	// YOU MUST IMPLEMENT THIS METHOD
	public StopwordsRemover(FileInputStream instream) throws IOException {
		// load and store the stop words from the fileinputstream with
		// appropriate data structure
		// that you believe is suitable for matching stop words.
		reader_sw = new BufferedReader(new InputStreamReader(instream));
		line_sw = reader_sw.readLine();
		while (line_sw != null) {
			// put all the stop words in a map, and the value as "true".
			// if there is no mapping for that key, showing that it is not a
			// stop word.
			sw.add(line_sw);
			line_sw = reader_sw.readLine();
		}
	}

	// YOU MUST IMPLEMENT THIS METHOD
	public boolean isStopword(String word) {
		// return true if the input word is a stopword, or false if not
		if (sw.contains(word))
			return true;
		return false;
	}

}