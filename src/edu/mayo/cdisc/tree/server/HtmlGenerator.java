package edu.mayo.cdisc.tree.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

//import org.apache.commons.io.FileUtils;

public class HtmlGenerator {

	// dev environment
	//private static final String FILE_PATH = "/Applications/Tomcat7/apache-tomcat-7.0.27/webapps/cdisc/data/maps/";
	
	// production environment
	private static final String FILE_PATH = "../webapps/cdisc/data/maps/";
	
	// file to copy and use as a base html file
	private static final String FILE_NAME = "freemindBrowserTemplate.html";
	
	private static final String REPLACE_TEXT = "<FREEMIND_FILE>";
	
	private String i_mmFileName;
	private String i_htmlFileName;
	
	public HtmlGenerator(String htmlFileName, String mmFileName) {
		super();
		
		i_mmFileName = mmFileName;
		i_htmlFileName = htmlFileName;
	}

	public boolean generateHtmlFile(){
		
		System.out.println("updating html file with mm file name of " + i_mmFileName);
		
		File src = new File(FILE_PATH + FILE_NAME);
		File dst = new File(FILE_PATH + i_htmlFileName);
		
		System.out.println("Creating copy of html file SRC = " + src.getAbsolutePath());
		System.out.println("Creating copy of html file DST = " + dst.getAbsolutePath());

		try {
			
			System.out.println("Copy start");
			FileUtils.copyFile(src, dst);	
			System.out.println("Copy complete");
			
			replaceSelected(dst, REPLACE_TEXT, i_mmFileName);
			System.out.println("New file created and updated - " + dst.getAbsolutePath());
			
		}catch (Exception e) {
			System.out.println("Error creating html file - " + e.toString());
			return false;
		}
		
		return true;
		
	}
	
	public static void replaceSelected(File file, String textToReplace, String newText) throws IOException {

	    // we need to store all the lines
	    List<String> lines = new ArrayList<String>();

	    // first, read the file and store the changes
	    BufferedReader in = new BufferedReader(new FileReader(file));
	    String line = in.readLine();
	    while (line != null) {
	    	
	    	// replace the name of the .mm file to point to
	    	if (line.contains(textToReplace)) {
	    		line = line.replaceAll(textToReplace, newText);
	    	}
	    	
	    	
	        lines.add(line);
	        line = in.readLine();
	    }
	    
	    in.close();

	    // now, write the file again with the changes
	    PrintWriter out = new PrintWriter(file);
	    for (String l : lines)
	        out.println(l);
	    
	    out.flush();
	    out.close();

	}
	

	
}
