package edu.pitt.sis.infsci2140.analysis;

/**
 * TextTokenizer can split a sequence of text into individual word tokens.
 * First, sign the texts to a member variable. Second, using a index to control
 * the reading and return of the nextWord. Taking white space as the divider of
 * each word, and kick out some general punctuation from the word. And all the
 * HTML tags will be deleted.("<" and ">" and all the chars between them)
 * 
 */
public class TextTokenizer {

	int index;// current index of char[]
	int size;// size of char[]
	char[] c;// save the char[] in TextTokenizer

	// YOU MUST IMPLEMENT THIS METHOD
	public TextTokenizer(char[] texts) {
		// this constructor will tokenize the input texts (usually it is a char
		// array for a whole document)
		index = 0;
		size = texts.length;
		c = texts;
	}

	// YOU MUST IMPLEMENT THIS METHOD
	public String nextWord() {
		// read and return the next word of the document; or return null if it
		// is the end of the document

		StringBuilder wordbuilder = new StringBuilder();
		String word_s = null;

		if (index >= size) {
			// document ends
			return null;
		} else {
			// throw out white space and HTML tags
			while (c[index] == ' ' || c[index] == '<') {
				// throw out the "<" and ">" and the content between them
				if (c[index] == '<') {
					index++;
					while (c[index] != '>') {
						index++;
					}
					index++;
					if (index >= size)
						return null;
				} else {
					index++;
					if (index >= size)
						return null;
				}
			}

			// get the raw token
			while (c[index] != ' ') {
				wordbuilder = wordbuilder.append(c[index]);
				index++;
				if (index >= size)
					break;
				while (c[index] == '<') {
					index++;
					while (c[index] != '>') {
						index++;
					}
					index++;
					if (index >= size)
						break;
				}
			}
			word_s = wordbuilder.toString();

			// eliminate the "." as full stop, but keep the decimal point or
			// others
			if (word_s.length() >= 1
					&& word_s.charAt(word_s.length() - 1) == '.') {
				word_s = word_s.replace(".", "");
			}

			// delete other general punctuation in the token
			String[] punctuation = { ",", ":", "?", "!", ";", "\"", "(", ")",
					"%", "$", "{", "}", "[", "]" };
			for (String p : punctuation) {
				word_s = word_s.replace(p, "");
			}
			return word_s;
		}
	}

}
