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
public class TrecwebCollection implements DocumentCollection {


	BufferedReader reader = null;
	String line = null;
	Map<String, Object> map = new HashMap<String, Object>();

	public TrecwebCollection(FileInputStream instream) throws IOException {
		// This constructor should take an inputstream of the collection file as
		// the input parameter.
		reader = new BufferedReader(new InputStreamReader(instream));
	}

	public Map<String, Object> nextDocument() throws IOException {
		// Read the definition of this method from
		// edu.pitt.sis.infsci2140.index.DocumentCollection interface
		// and follow the assignment instructions to implement this method.
		line = reader.readLine();

		if (line == null)
			return null;

		while (!line.contains("<DOC>")) {
			line = reader.readLine();
		}
		// System.out.println("New doc Start!");

		while (!line.contains("<DOCNO>")) {
			line = reader.readLine();
		}
		// System.out.println("Find doc ID!");

		String docno = line.replace("<DOCNO>", "");
		docno = docno.replace("</DOCNO>", "");
		map.put("DOCNO", docno);

		while (!line.contains("</DOCHDR>")) {
			line = reader.readLine();
		}
		// System.out.println("Find doc content!");

		line = reader.readLine();
		StringBuilder contentbuilder = new StringBuilder();
		while (!line.contains("</DOC>")) {
			contentbuilder = contentbuilder.append(line + " ");
			line = reader.readLine();
		}
		String content_s = contentbuilder.toString();
		char[] content = content_s.toCharArray();
		map.put("CONTENT", content);
		// System.out.println("Mapping complete!");
		return map;
	}

}