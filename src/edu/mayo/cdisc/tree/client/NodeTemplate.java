package edu.mayo.cdisc.tree.client;

import com.smartgwt.client.data.Record;

import edu.mayo.cdisc.tree.client.datasources.BridgDatasource;

/**
 * Class to create common child tree node structure for a selected node.
 * We need to create the following child nodes under the given node: Children, 
 * Attributes, Inherited Attributes.
 */
public class NodeTemplate {

	public static final String NODE_CHILDREN = "Children";
	public static final String NODE_ATTRIBUTES = "Attributes";
	public static final String NODE_ASSOCIATIONS = "Associations";
	
	
	private static int s_id = 1000000;
	
	public NodeTemplate() {
		super();
	}
	
	/**
	 * Create the child nodes.
	 * 
	 * @param record
	 * @return
	 */
	public static Record[] addChildNodes (Record record) {
				
		String parentId = record.getAttribute(BridgDatasource.CDISC_ID);
		String node = record.getAttribute(BridgDatasource.DISPLAY_NAME);
		String parentName = record.getAttribute(BridgDatasource.NAME);
		String parentLongName = record.getAttribute(BridgDatasource.PARENT);
		String concept =  record.getAttribute(BridgDatasource.CONCEPT);
		
		System.out.println("NodeTemplate -- id = " + parentId);
		System.out.println("NodeTemplate -- displayName = " + node);
		System.out.println("NodeTemplate -- parentName = " + parentName);
		System.out.println("NodeTemplate -- parentLongName = " + parentLongName);
		System.out.println("NodeTemplate -- concept = " + concept);
				
		// create the "Children" node
		Record children = new Record();
		children.setAttribute("canSelect", false);
		children.setAttribute(BridgDatasource.NAME, NODE_CHILDREN + "_" + parentName);
		children.setAttribute(BridgDatasource.PARENT, parentLongName);
		
		children.setAttribute(BridgDatasource.DISPLAY_NAME, NODE_CHILDREN);
		children.setAttribute(BridgDatasource.CDISC_ID, nextId());
		children.setAttribute(BridgDatasource.PARENT_ID, parentId);
		children.setAttribute(BridgDatasource.ICON, "ball.png");
		children.setAttribute(BridgDatasource.CONCEPT, concept);
		
		// create the "Attributes" node
		Record attributes = new Record();
		attributes.setAttribute("canSelect", false);
		attributes.setAttribute(BridgDatasource.NAME, NODE_ATTRIBUTES+ "_" + parentName);
		attributes.setAttribute(BridgDatasource.PARENT, parentLongName);
		
		attributes.setAttribute(BridgDatasource.DISPLAY_NAME, NODE_ATTRIBUTES);
		attributes.setAttribute(BridgDatasource.CDISC_ID, nextId());
		attributes.setAttribute(BridgDatasource.PARENT_ID, parentId);
		attributes.setAttribute(BridgDatasource.ICON, "attributes.png");
		attributes.setAttribute(BridgDatasource.CONCEPT, concept);
		
		// create the "Associations" node
		Record associations = new Record();
		associations.setAttribute("canSelect", false);
		associations.setAttribute(BridgDatasource.NAME, NODE_ASSOCIATIONS + "_" + parentName);
		associations.setAttribute(BridgDatasource.PARENT, parentLongName);
		
		associations.setAttribute(BridgDatasource.DISPLAY_NAME, NODE_ASSOCIATIONS);
		associations.setAttribute(BridgDatasource.CDISC_ID, nextId());
		associations.setAttribute(BridgDatasource.PARENT_ID, parentId);
		associations.setAttribute(BridgDatasource.ICON, "associations.png");
		associations.setAttribute(BridgDatasource.CONCEPT, concept);
		
		Record[] childRecords =  new Record[] {children, attributes , associations};
		return childRecords;
		
	}

	public static int nextId() {
		 return s_id++;
	 }
	
}
