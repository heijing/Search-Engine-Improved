package edu.pitt.sis.infsci2140.search;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class that stores topic information.
 * Using pattern match to process the topic.txt.
 * Extracting topicID, title, description and narrative from topic.txt.
 * Just modify the value of query in the end of the while loop to change the content of query.
 * Method parse will return a list of object topics and output a text file.
 */
public class Topic {
	protected String topicID;
	protected String query;
	protected String title;
	protected String desc;
	protected String narr;

	public String topicId() {
		return topicID;
	}

	public String query() {
		return query;
	}

	public String title() {
		return title;
	}

	public String desc() {
		return desc;
	}

	public String narr() {
		return narr;
	}

	/**
	 * Parse a list of TREC topics from the provided f.
	 * 
	 * @param f
	 * @return
	 */
	public static List<Topic> parse(File f) {
		// you should implement this method
		ArrayList<Topic> tl = new ArrayList<Topic>();
		BufferedReader reader = null;
		BufferedWriter writer = null;
		StringBuilder buffer = null;
		String line = null;
		try {
			FileWriter output = new FileWriter("query.txt");
			writer = new BufferedWriter(output);
			reader = new BufferedReader(new FileReader(f));
			line = reader.readLine();
			
			while (!line.equals(null)) {
				while (!line.contains("<top>")) {
					line = reader.readLine();
				}
				buffer = new StringBuilder();

				while (!line.contains("</top>")) {
					line = reader.readLine();
					
					buffer.append(line);
				}
				Topic top = new Topic();
				String topic = buffer.toString();
				
				Pattern pid = Pattern.compile("(.+?Number:\\s)(.+?)(<title>.+?)",
						Pattern.DOTALL + Pattern.MULTILINE
								+ Pattern.CASE_INSENSITIVE);
				Matcher m0 = pid.matcher(topic);
				m0.find();
				top.topicID = m0.group(2);
				
				Pattern ptitle = Pattern.compile("(.+?<title>\\s)(.+?)(<desc>.+?)",
						Pattern.DOTALL + Pattern.MULTILINE
								+ Pattern.CASE_INSENSITIVE);
				Matcher m1 = ptitle.matcher(topic);
				m1.find();
				top.title = m1.group(2);
				
				Pattern pdesc = Pattern.compile("(.+?Description:\\s)(.+?)(<narr>.+?)",
						Pattern.DOTALL + Pattern.MULTILINE
								+ Pattern.CASE_INSENSITIVE);
				Matcher m2 = pdesc.matcher(topic);
				m2.find();
				top.desc = m2.group(2);
				
				Pattern pnarr = Pattern.compile("(.+?Narrative:\\s)(.+?)(</top>)",
						Pattern.DOTALL + Pattern.MULTILINE
								+ Pattern.CASE_INSENSITIVE);
				Matcher m3 = pnarr.matcher(topic);
				m3.find();
				top.narr = m3.group(2);

				top.query = top.title;
				tl.add(top);
				writer.write(top.topicID + "," + top.query + "\n");
				
				if(reader.readLine()==null){
					break;
				}
			}
			reader.close();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tl;
	}

}
