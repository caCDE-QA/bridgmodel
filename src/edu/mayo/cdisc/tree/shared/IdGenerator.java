package edu.mayo.cdisc.tree.shared;

public class IdGenerator {
	private static int s_id = 1;
	
	 public static int nextId() {
		 return s_id++;
	 }
}
