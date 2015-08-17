package edu.pitt.sis.infsci2140.index;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
/**
 * The logic of this class is to read the input steam line by line, and
 * judging whether the line contains some key words, which indicate the
 * beginning and end of DOCNO and CONTENT. Then put them into HashMap.
 */
public class TrectextCollection implements DocumentCollection {


	BufferedReader reader = null;
	String line = null;
	Map<String, Object> map = new HashMap<String, Object>();

	public TrectextCollection(FileInputStream instream) throws IOException {
		// This constructor should take an inputstream of the collection file as
		// the input parameter.
		reader = new BufferedReader(new InputStreamReader(instream));
	}

	public Map<String, Object> nextDocument() throws IOException {
		// Read the definition of this method from
		// edu.pitt.sis.infsci2140.index.DocumentCollection interface
		// and follow the assignment instructions to implement this method.

		line = reader.readLine();

		// Using String.contains() to judge the beginning of a doc
		while (!line.contains("<DOC>")) {
			line = reader.readLine();
			// Condition to return "null" and exit this method
			// This is different with TrecwebCollection, because this content
			// end with </TEXT>, not </DOC>.
			if (line == null) {
				return null;
			}
		}
		// System.out.println("New doc Start!");

		while (!line.contains("<DOCNO>")) {
			line = reader.readLine();
		}
		// System.out.println("Find doc ID!");

		// According to the format of DOCNO in Trectext, using " " to split the
		// line and get DOCNO in the middle
		String[] line_docno = line.split(" ");
		String docno = line_docno[1];
		// put key and value into map
		map.put("DOCNO", docno);

		while (!line.contains("<TEXT>")) {
			line = reader.readLine();
		}
		// System.out.println("Find doc content!");

		line = reader.readLine();
		// Use StringBuilder to combine the line of content
		StringBuilder contentbuilder = new StringBuilder();
		while (!line.contains("</TEXT>")) {
			contentbuilder = contentbuilder.append(line);
			line = reader.readLine();
		}
		String content_s = contentbuilder.toString();
		char[] content = content_s.toCharArray();
		map.put("CONTENT", content);
		// System.out.println("Mapping complete!");
		return map;

	}

}
