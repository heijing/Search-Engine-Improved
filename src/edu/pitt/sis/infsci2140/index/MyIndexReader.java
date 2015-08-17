package edu.pitt.sis.infsci2140.index;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * This class not only read the index, but also calculate the score for
 * retrieval model
 */
public class MyIndexReader {

	protected File dir;
	protected long[][] p;

	protected HashMap<Long, Integer> doc = new HashMap<Long, Integer>();
	protected HashMap<String, TermInfo> dictionary = new HashMap<String, TermInfo>();
	protected HashMap<String, ArrayList<long[]>> postings = new HashMap<String, ArrayList<long[]>>();

	protected FileInputStream docid_nos;
	protected ObjectInputStream docid_noi;
	protected FileInputStream dictionarys;
	protected ObjectInputStream dictionaryi;
	protected RandomAccessFile postingss;

	@SuppressWarnings("unchecked")
	public MyIndexReader(File dir) throws IOException {
		this.dir = dir;
		// long begin = System.nanoTime();

		docid_nos = new FileInputStream(dir + "/Doc_id_no.ser");
		docid_noi = new ObjectInputStream(docid_nos);
		try {
			doc = (HashMap<Long, Integer>) docid_noi.readObject();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		dictionarys = new FileInputStream(dir + "/Dictionary.ser");
		dictionaryi = new ObjectInputStream(dictionarys);
		try {
			dictionary = (HashMap<String, TermInfo>) dictionaryi.readObject();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		postingss = new RandomAccessFile(dir + "/Postings_List.txt", "r");
		/*
		 * long end = System.nanoTime(); System.out.println("Loading time: " +
		 * (end - begin) + " ns");
		 */
	}

	public MyIndexReader(String path_dir) throws IOException {
		this(new File(path_dir));
	}

	/**
	 * Get the (non-negative) integer docid for the requested docno. If -1
	 * returned, it indicates the requested docno does not exist in the index.
	 * 
	 * @param docno
	 * @return
	 */
	public long getDocid(String docno) {
		// you should implement this method.
		String str = docno.replaceAll("[^0-9]", "");
		long docid = Long.parseLong(str);
		return docid;
	}

	/**
	 * Retrive the docno for the integer docid.
	 * 
	 * @param docid
	 * @return
	 */
	public String getDocno(long docid) {
		// you should implement this method.
		String id = String.valueOf(docid);
		String docno = "WSJ" + id.substring(0, 6) + "-" + id.substring(6);
		return docno;
	}

	/**
	 * Get the posting list for the requested token.
	 * 
	 * @param token
	 * @return
	 */
	public long[][] getPostingList(String token) throws IOException {
		// you should implement this method.
		if (dictionary.containsKey(token)) {
			TreeMap<Long, Integer> sortid = new TreeMap<Long, Integer>();
			TermInfo terminfo = dictionary.get(token);
			long pointer = terminfo.getpointer();
			int length = terminfo.getlength();
			byte[] read = new byte[length];
			postingss.seek(pointer);
			postingss.read(read);
			String post = new String(read, "UTF-8");
			String[] docid_tf = post.split(",");
			for (String str : docid_tf) {
				String docid_s = str.substring(0, 10);
				long docid = Long.parseLong(docid_s);
				String tf_s = str.substring(10);
				int tf = Integer.parseInt(tf_s);
				sortid.put(docid, tf);
			}
			p = new long[DocFreq(token)][2];
			int i = 0;
			for (Map.Entry<Long, Integer> entry : sortid.entrySet()) {
				p[i][0] = entry.getKey();
				p[i][1] = entry.getValue();
				i++;
			}
			return p;
		}
		return null;
	}

	/**
	 * Return the number of documents that contains the token.
	 * 
	 * @param token
	 * @return
	 */
	public int DocFreq(String token) throws IOException {
		// you should implement this method.
		int df = 0;
		if (dictionary.containsKey(token)) {
			TermInfo terminfo = dictionary.get(token);
			return df = terminfo.getdf();
		}
		return df;
	}

	/**
	 * Return the total number of times the token appears in the collection.
	 * 
	 * @param token
	 * @return
	 */
	public long CollectionFreq(String token) throws IOException {
		// you should implement this method.
		long cf = 0;
		if (dictionary.containsKey(token)) {
			TermInfo terminfo = dictionary.get(token);
			return cf = terminfo.getcf();
		}
		return cf;
	}

	/**
	 * Return the length of collection
	 * 
	 * @return
	 */
	public long CollctionLen() {
		long clen = 0;
		for (Entry<Long, Integer> entry : doc.entrySet()) {
			clen = clen + entry.getValue();
		}
		return clen;
	}

	/**
	 * Return whether a term is in the dictionary
	 * 
	 * @param term
	 * @return
	 */
	public boolean termIn(String term) {
		if (dictionary.containsKey(term))
			return true;
		return false;
	}

	/**
	 * Calculate the score of given term and return a HashMap with docid and
	 * score
	 * 
	 * @param token
	 * @return
	 * @throws IOException
	 */
	public HashMap<Long, Double> scoreMap(String token) throws IOException {

		HashMap<Long, Double> scoreMap = new HashMap<Long, Double>();
		long len = CollctionLen();
		TermInfo terminfo = dictionary.get(token);
		long pointer = terminfo.getpointer();
		int length = terminfo.getlength();
		byte[] read = new byte[length];
		postingss.seek(pointer);
		postingss.read(read);
		String post = new String(read, "UTF-8");
		String[] docid_tf = post.split(",");
		for (String str : docid_tf) {
			String docid_s = str.substring(0, 10);
			long docid = Long.parseLong(docid_s);
			String tf_s = str.substring(10);
			int tf = Integer.parseInt(tf_s);

			double score = ((double) tf / (double) doc.get(docid) + (double) 2000
					* (double) terminfo.cf / (double) len)
					/ ((double) doc.get(docid) + (double) 2000);
			scoreMap.put(docid, score);
		}
		return scoreMap;
	}

	/**
	 * Calculate the score of the terms that term frequency is zero
	 * 
	 * @param token
	 * @param docid
	 * @return
	 */
	public double scoreREF(String token, long docid) {
		TermInfo terminfo = dictionary.get(token);
		int cf = terminfo.cf;
		int doclen = doc.get(docid);
		double scoreREF = (double) 2000 * (double) cf / (double) CollctionLen()
				/ ((double) doclen + (double) 2000);
		return scoreREF;
	}

	/**
	 * Calculate those terms which are not in the collection, such as "ninja"
	 * @return
	 */
	public double scoreOut() {
		double score = (double) 1 / (double) CollctionLen();
		return score;
	}

	public HashMap<Long, Integer> doc() {
		return doc;
	}

	public void close() throws IOException {
		// you should implement this method when necessary
		docid_noi.close();
		docid_nos.close();

		dictionaryi.close();
		dictionarys.close();

		postingss.close();
	}

}
