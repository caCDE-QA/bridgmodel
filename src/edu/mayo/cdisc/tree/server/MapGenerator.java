package edu.mayo.cdisc.tree.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedList;


import ca.quine.jcommons.freemind.maputil.MapUtils;
import ca.quine.jcommons.freemind.xml.Map;
import ca.quine.jcommons.freemind.xml.Node;
import ca.quine.jcommons.freemind.xml.types.FontBOLDType;
import ca.quine.jcommons.freemind.xml.types.NodeItemFOLDEDType;
import ca.quine.jcommons.freemind.xml.types.NodeItemPOSITIONType;
import ca.quine.jcommons.marshaller.Marshaller;
import edu.mayo.cdisc.tree.shared.SelectedComponents;

public class MapGenerator {

	// for dev
	//private static final String PATH = "";
	
	// for deployment
	private static final String PATH = "../webapps/cdisc/data/maps/";
	
	public MapGenerator() {
		super();
		
	}

	/**
	 * Generate the .mm and return the location of it.
	 * @return
	 */
	public String generateMap(SelectedComponents[] data, String templateName, String templateFileName) {
		
		System.out.println("MapGenerator.generateMap called");
		
		String mmFileName = templateFileName + ".mm";
		
		
		ArrayList<String> used = new ArrayList<String>();
		
		try {
			
			
			// create the initial file
			createFile(PATH + mmFileName);
			
			Node rootNode = MapUtils.makeNode(templateName);
			rootNode.setBACKGROUND_COLOR("#cc00cc");
			rootNode.setCOLOR("#000000");
			//rootNode.setSTYLE(FontBOLDType.TRUE_TYPE);
			
			System.out.println("CREATING MM FILE... " + mmFileName);
			
			Map map = new Map();
			map.setVersion("1.0.0");
			map.setNode(rootNode);
			
			for (int i = 0; i < data.length; i++) {

				// bridg class
				String concept = data[i].getConcept();

				if (!used.contains(concept)) {
					used.add(concept);
					
					Node nodeConcept = MapUtils.makeNode(concept);
					nodeConcept.setBACKGROUND_COLOR("#3ca9d0");
					nodeConcept.setCOLOR("#000000");
					
					MapUtils.addChild(rootNode, nodeConcept);
					
					// add the variables to the parent concept/bridg class
					for (int j = 0; j < data.length; j++) {

						String testConcept = data[j].getConcept();
						
						if (testConcept.equals(concept)) {
							
							Node nodeVariable = MapUtils.makeNode(data[j].getVariable());
							nodeVariable.setBACKGROUND_COLOR("#fbff00");
							nodeVariable.setFOLDED(NodeItemFOLDEDType.FALSE);
							
							MapUtils.addChild(nodeConcept, nodeVariable);
						}
					}
				}
			}
			
			
//			Node rightChild1 = MapUtils.makeNode("right child 1");
//			Node rightChild2 = MapUtils.makeNode("right child 2");
//			Node leftChild1 = MapUtils.makeNode("left child 1",
//					NodeItemPOSITIONType.LEFT);
//			Node leftChild2 = MapUtils.makeNode("left child 2",
//					NodeItemPOSITIONType.LEFT);
//			
//			Node rightChild11 = MapUtils.makeNode("right child 11");
//			Node rightChild21 = MapUtils.makeNode("right child 21");
//			rightChild21.setFOLDED(NodeItemFOLDEDType.FALSE);
//			Node leftChild11 = MapUtils.makeNode("left child 11",
//					NodeItemPOSITIONType.LEFT);
//			Node leftChild21 = MapUtils.makeNode("left child 21",
//					NodeItemPOSITIONType.LEFT);
//
//			MapUtils.addChild(rootNode, rightChild1);
//			MapUtils.addChild(rootNode, rightChild2);
//			MapUtils.addChild(rootNode, leftChild1);
//			MapUtils.addChild(rootNode, leftChild2);
//			MapUtils.addChild(rightChild1, rightChild11);
//			MapUtils.addChild(rightChild1, rightChild21);
//			MapUtils.addChild(leftChild1, leftChild11);
//			MapUtils.addChild(leftChild1, leftChild21);

			System.out.println("Creating MM file - "+ PATH +  mmFileName);
			
			// when deployed, the base dir is the bin dir.
			// in dev mode, the base dir is wht war dir
			BufferedWriter bw = new BufferedWriter(new FileWriter(PATH + mmFileName));
			map.marshal(bw);
			bw.close();
			
		} catch (Exception e) {
			mmFileName = null;
			System.out.println("Error creating MM file -  " + e.toString());
		}
		
		return mmFileName;
	}
	
	
	private void createFile(String fileName) throws Exception {
		File file = new File(fileName);
		 
	    if (file.createNewFile()){
	        System.out.println("File is created " + file.getAbsolutePath());
	      }else
	     {
	       System.out.println("File already exists "+ file.getAbsolutePath());
	     }
	}
	
	
}
