package edu.pitt.sis.infsci2140.search;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.pitt.sis.infsci2140.analysis.StopwordsRemover;
import edu.pitt.sis.infsci2140.analysis.TextNormalizer;
import edu.pitt.sis.infsci2140.analysis.TextTokenizer;
import edu.pitt.sis.infsci2140.index.MyIndexReader;

/**
 * Just like processing content, I use tokenizer and stoprmv and nomarlizer to
 * process the query. I think it is more valuable to considering """ when I
 * stored position in the index. So I didn't care about it now. For every query
 * term, I traverse the docid to calculate score for each doc. There are many
 * different conditions, I use different score functions. The calculation is
 * done in MyIndexReader, here is just get the result or multiply the
 * probability of different words.
 * 
 *
 * 
 */
public class MyRetrievalModel {

	protected MyIndexReader ixreader;
	FileInputStream instream_stopwords = null;

	public MyRetrievalModel() {
		// you should implement this method
	}

	public MyRetrievalModel setIndex(MyIndexReader ixreader) {
		this.ixreader = ixreader;
		return this;
	}

	/**
	 * Search for the topic information. The returned results should be ranked
	 * by the score (from the most relevant to the least). max_return specifies
	 * the maximum number of results to be returned.
	 * 
	 * @param topic
	 *            The topic information to be searched for.
	 * @param max_return
	 *            The maximum number of returned document
	 * @return
	 */
	public List<SearchResult> search(Topic topic, int max_return)
			throws IOException {
		LinkedHashMap<Long, Double> totalScore = new LinkedHashMap<Long, Double>();

		char[] query = topic.query().toCharArray();
		TextTokenizer tokenizer = new TextTokenizer(query);
		StopwordsRemover stoprmv = null;
		instream_stopwords = new FileInputStream("stop_words.txt");
		stoprmv = new StopwordsRemover(instream_stopwords);
		String word = null;
		HashMap<Long, Integer> doc = ixreader.doc();
		while ((word = tokenizer.nextWord()) != null) {
			word = TextNormalizer.normalize(word);
			if (!stoprmv.isStopword(word)) {
				if (ixreader.termIn(word)) {
					HashMap<Long, Double> scoreMap = ixreader.scoreMap(word);
					for (Entry<Long, Integer> entry : doc.entrySet()) {
						long docid = entry.getKey();

						if (scoreMap.containsKey(docid)) {
							if (totalScore.containsKey(docid)) {
								double newScore = scoreMap.get(docid)
										* totalScore.get(docid);

								totalScore.put(docid, newScore);
							} else {
								totalScore.put(docid, scoreMap.get(docid));
							}
						} else {
							if (totalScore.containsKey(docid)) {
								double newScore = ixreader
										.scoreREF(word, docid)
										* totalScore.get(docid);
								totalScore.put(docid, newScore);
							} else {
								totalScore.put(docid,
										ixreader.scoreREF(word, docid));
							}
						}
					}

				} else {
					for (Entry<Long, Integer> entry : doc.entrySet()) {
						long docid = entry.getKey();
						if (totalScore.containsKey(docid)) {
							double newScore = ixreader.scoreOut()
									* totalScore.get(docid);
							totalScore.put(docid, newScore);
						} else {
							totalScore.put(docid, ixreader.scoreOut());
						}
					}
				}
			}
		}

		totalScore = (LinkedHashMap<Long, Double>) sortMapByValues(totalScore);
		List<SearchResult> search = new ArrayList<SearchResult>();
		for (int k = 0; k < max_return; k++) {
			Entry<Long, Double> e = totalScore.entrySet().iterator().next();
			long docid = e.getKey();
			totalScore.remove(docid);
			String docno = ixreader.getDocno(docid);
			double score = e.getValue();
			SearchResult sr = new SearchResult(docid, docno, score);
			search.add(sr);
		}
		return search;
	}

	// A generic method to sort a map by value, got from StactOverFlow.
	public static <K, V extends Comparable<? super V>> Map<K, V> sortMapByValues(
			final Map<K, V> mapToSort) {
		List<Map.Entry<K, V>> entries = new ArrayList<Map.Entry<K, V>>(
				mapToSort.size());

		entries.addAll(mapToSort.entrySet());

		Collections.sort(entries, new Comparator<Map.Entry<K, V>>() {
			@Override
			public int compare(final Map.Entry<K, V> entry1,
					final Map.Entry<K, V> entry2) {
				return entry2.getValue().compareTo(entry1.getValue());
			}
		});

		Map<K, V> sortedMap = new LinkedHashMap<K, V>();

		for (Map.Entry<K, V> entry : entries) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
}