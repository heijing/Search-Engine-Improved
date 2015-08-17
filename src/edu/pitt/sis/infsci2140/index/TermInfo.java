package edu.pitt.sis.infsci2140.index;

import java.io.Serializable;

public class TermInfo implements Serializable{
	/**
	 * This is a new class I create to record more information about term.
	 * And it also store the pointer and length of a term in the randomaccessfile.
	 * cf: collection frequency
	 * df: document frequency
	 * pointer: begin byte position of the term in randomaccessfile
	 * length: length of byte of the term in randomaccessfile
	 */
	private static final long serialVersionUID = 1L;
	protected int df;
	protected int cf;
	protected long pointer;
	protected int length;
	
	public void setdf(int new_df){
		df = new_df;
	}
	
	public void setcf(int new_cf){
		cf = new_cf;
	}
	
	public void setpointer(long new_pointer){
		pointer = new_pointer;
	}
	
	public void setlength(int new_length){
		length = new_length;
	}
	
	public int getdf(){
		return df;
	}
	
	public int getcf(){
		return cf;
	}
	public long getpointer(){
		return pointer;
	}
	public int getlength(){
		return length;
	}
}
