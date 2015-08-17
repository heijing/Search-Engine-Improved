package edu.pitt.sis.infsci2140.index;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import edu.pitt.sis.infsci2140.analysis.StopwordsRemover;
import edu.pitt.sis.infsci2140.analysis.TextNormalizer;
import edu.pitt.sis.infsci2140.analysis.TextTokenizer;

/**
 * @DataStrucutre HashMap, ArrayList
 * 
 * @NewforMe Serialization, RandomAccessFile
 * 
 * @Design I modify the postings list to a randomaccessfile in the end, which
 *         will reduce the usage of memory and improve the speed. While in the
 *         processing, I still use the HashMap to store the postings, because it
 *         is hard to insert data into disk.
 */
public class MyIndexWriter {

	protected File dir;
	protected Integer initial = 1;

	// HashMap<Docid, Entry> for storing docinfo
	protected HashMap<Long, Integer> doc = new HashMap<Long, Integer>();
	// HashMap<Term, TermInfo> for storing dictionary
	protected HashMap<String, TermInfo> dictionary = new HashMap<String, TermInfo>();
	// HashMap<TermID, Postings> for storing Postings list
	protected HashMap<String, ArrayList<long[]>> postings = new HashMap<String, ArrayList<long[]>>();

	// Several Object for serializing later
	protected FileOutputStream docid_nos;
	protected ObjectOutputStream docid_noo;
	protected FileOutputStream dictionarys;
	protected ObjectOutputStream dictionaryo;
	protected RandomAccessFile postingss;

	public MyIndexWriter(File dir) throws IOException {
		this.dir = dir;
	}

	public MyIndexWriter(String path_dir) throws IOException {
		this.dir = new File(path_dir);
		if (!this.dir.exists()) {
			this.dir.mkdir();
		}

		docid_nos = new FileOutputStream(dir + "/Doc_id_no.ser");
		docid_noo = new ObjectOutputStream(docid_nos);

		postingss = new RandomAccessFile(dir + "/Postings_List.txt", "rw");

		dictionarys = new FileOutputStream(dir + "/Dictionary.ser");
		dictionaryo = new ObjectOutputStream(dictionarys);
	}

	/**
	 * This method build index for each document. NOTE THAT: in your
	 * implementation of the index, you should transform your string docnos into
	 * non-negative integer docids !!! In MyIndexReader, you should be able to
	 * request the integer docid for docnos.
	 * 
	 * @param docno
	 *            Docno
	 * @param tokenizer
	 *            A tokenizer that iteratively gives out each token in the
	 *            document.
	 * @throws IOException
	 */
	public void index(String docno, TextTokenizer tokenizer,
			StopwordsRemover stoprmv) throws IOException {

		// HashMap<Term, Term frequency> A temporary map for each doc to store
		HashMap<String, Integer> temp = new HashMap<String, Integer>();

		// Getting token, normalizing it and kick out stop words, then mapping
		// into temp map.
		String word = null;
		int doclength = 0;
		while ((word = tokenizer.nextWord()) != null) {
			doclength++;
			word = TextNormalizer.normalize(word);
			if (!stoprmv.isStopword(word)) {
				if (!temp.containsKey(word)) {
					temp.put(word, initial);
				} else {
					temp.put(word, temp.get(word) + 1);
				}
			}
		}

		long docid = getDocid(docno);
		doc.put(docid, doclength);

		// Using a enhanced for to traverse the temp map and add to dictionary
		// and postings
		for (Map.Entry<String, Integer> entry : temp.entrySet()) {
			String term = entry.getKey();
			int tf = entry.getValue();
			// Using a one dimensional array instead of two dimensions to
			// compress sizes
			long p[] = new long[2];
			p[0] = docid;
			p[1] = tf;
			if (!dictionary.containsKey(term)) {
				TermInfo terminfo = new TermInfo();
				terminfo.setdf(1);
				terminfo.setcf(tf);
				dictionary.put(term, terminfo);

				ArrayList<long[]> array = new ArrayList<long[]>();
				array.add(p);
				postings.put(term, array);
			} else {
				postings.get(term).add(p);
				TermInfo terminfo = dictionary.get(term);
				int df = terminfo.getdf() + 1;
				terminfo.setdf(df);
				int cf = terminfo.getcf() + tf;
				terminfo.setcf(cf);
			}
		}
	}

	/**
	 * Close the index writer, and you should output all the buffered content
	 * (if any).
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {

		// Serializing two maps and a list to disk file
		docid_noo.writeObject(doc);
		docid_noo.close();
		docid_nos.close();

		long pointer = 0;
		for (Entry<String, ArrayList<long[]>> entry : postings.entrySet()) {
			String term = entry.getKey();
			ArrayList<long[]> array = entry.getValue();
			String post = "";

			for (long[] p : array) {
				String docid_tf = String.valueOf(p[0]) + String.valueOf(p[1]);
				if (post == "") {
					post = docid_tf;
				} else {
					post = post + "," + docid_tf;
				}
			}

			postingss.write(post.getBytes());
			int length = post.length();
			TermInfo terminfo = dictionary.get(term);
			terminfo.setpointer(pointer);
			terminfo.setlength(length);
			pointer = pointer + length;
		}
		postingss.close();

		dictionaryo.writeObject(dictionary);
		dictionaryo.close();
		dictionarys.close();

	}

	public long getDocid(String docno) {
		String str = docno.replaceAll("[^0-9]", "");
		long docid = Long.parseLong(str);
		return docid;
	}

}
