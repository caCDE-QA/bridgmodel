package edu.mayo.cdisc.tree.server;

import java.net.MalformedURLException;
import java.util.Random;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.mayo.cdisc.tree.client.CdiscService;
import edu.mayo.cdisc.tree.shared.ConnectionInfo;
import edu.mayo.cdisc.tree.shared.IdGenerator;
import edu.mayo.cdisc.tree.shared.SelectedComponents;

/**
 * The server side implementation of the RPC service.
 */
public class CdiscServiceImpl extends RemoteServiceServlet implements
		CdiscService {
	
	private static final long serialVersionUID = 1L;
	private static final String ROOT = "root";
	private static Store s_store = null;
	

	@Override
	public String getCategories(String parentId, String parentName, String parentNode)
			throws IllegalArgumentException {
		
		String xml = "";
		Store store = getStore();

		try {

			if (parentNode != null && !parentNode.equals("")) {
				xml = store.query(
						CdiscQueries.getCategoryQueryString(parentName),
						Store.OutputFormat.SPARQL_XML);
			} else {
				xml = store.query(
						CdiscQueries.getRootCategoryQueryString(),
						Store.OutputFormat.SPARQL_XML);
			}
			
			// if xml <results> element is empty, then try a different query
			// to get the children
			if (!xml.contains("<result>") && (parentNode != null && !parentNode.equals("")) ) {
				xml = store.query(
						CdiscQueries.getCategoryQueryString2(parentName),
						Store.OutputFormat.SPARQL_XML);
			}
					

			// add the parent node here
			xml = addParentNode(xml, parentNode);
			// add unique id to each entry
			xml = addUniqueId(xml);

		} catch (Exception e) {
			xml = "";
			System.out.println("Error retrieving categories from 4store. "
					+ e);
		}
		
		System.out.println(xml);
		return xml;
	}

	
	@Override
	public String getAttributes(String parentId, String parentName, String parentNode)
			throws IllegalArgumentException {
		String attributesXml = "";
		Store store = getStore();

		try {

			if (parentName != null && !parentName.equals("")) {
				attributesXml = store.query(
						CdiscQueries.getAttributesQueryString(parentName),
						Store.OutputFormat.SPARQL_XML);
			} 
			// add the parent node here
			attributesXml = addParentNode(attributesXml, parentNode);
			// add unique id to each entry
			attributesXml = addUniqueId(attributesXml);

		} catch (Exception e) {
			attributesXml = "";
			System.out.println("Error retrieving categories from 4store. "
					+ e);
		}
		
		return attributesXml;
	}
	
	@Override
	public String getAssociations(String parentId, String parentName,
			String parentNode) throws IllegalArgumentException {
		String xml = "";
		Store store = getStore();

		try {

			if (parentName != null && !parentName.equals("")) {
				xml = store.query(
						CdiscQueries.getAssociationsQueryString(parentName),
						Store.OutputFormat.SPARQL_XML);
			} 
			
			// add the parent node here
			xml = addParentNode(xml, parentNode);
			// add unique id to each entry
			xml = addUniqueId(xml);

		} catch (Exception e) {
			xml = "";
			System.out.println("Error retrieving associations from 4store. "
					+ e);
		}
		
		return xml;
	}
	
	
	@Override
	public String getMetadata(String object) throws IllegalArgumentException {
		String metadataXml = "";
		Store store = getStore();

		try {

			if (object != null && !object.equals("")) {
				metadataXml = store.query(
						CdiscQueries.getMetadataQueryString(object),
						Store.OutputFormat.SPARQL_XML);
			}			

		} catch (Exception e) {
			metadataXml = "";
			System.out.println("Error retrieving metadate for " + object + " from 4store. "
					+ e);
		}

		return metadataXml;
	}
	
	@Override
	public String getDataTypesForComponent(String parent, String parentNode)
			throws IllegalArgumentException {
		String componentXml = "";
		Store store = getStore();

		try {

			if (parent != null && !parent.equals("")) {
				componentXml = store.query(
						CdiscQueries.getDataTypesQueryString(parent),
						Store.OutputFormat.SPARQL_XML);
			}			

			// add the parent node here
			componentXml = addParentNode(componentXml, parentNode);
			// add unique id to each entry
			componentXml = addUniqueId(componentXml);
			// add the "childLabel" node
			componentXml = addChildLabelNode(componentXml, "component");
						
		} catch (Exception e) {
			componentXml = "";
			System.out.println("Error retrieving sub component for " + parent + " from 4store. "
					+ e);
		}

		return componentXml;
	}
	
	
	@Override
	public String getSingleDataType(String parent, String parentNode)
			throws IllegalArgumentException {
		String xml = "";
		Store store = getStore();

		try {

			if (parent != null && !parent.equals("")) {
				xml = store.query(
						CdiscQueries.getSingleDataTypeQueryString(parent),
						Store.OutputFormat.SPARQL_XML);
			}			

			// add the parent node here
			xml = addParentNode(xml, parentNode);
			// add unique id to each entry
			xml = addUniqueId(xml);
			// add the "childLabel" node
			xml = addChildLabelNode(xml, "datatype");
						
		} catch (Exception e) {
			xml = "";
			System.out.println("Error retrieving sub component for " + parent + " from 4store. "
					+ e);
		}

		return xml;
	}
	
	@Override
	public String getSubComponents(String parent, String parentNode)
			throws IllegalArgumentException {
		
		String xml = "";
		Store store = getStore();

		try {

			if (parent != null && !parent.equals("")) {
				xml = store.query(
						CdiscQueries.getSubComponentsQueryString(parent),
						Store.OutputFormat.SPARQL_XML);
			}			

			// add the parent node here
			xml = addParentNode(xml, parentNode);
			// add unique id to each entry
			xml = addUniqueId(xml);
			// add the "childLabel" node
			xml = addChildLabelNode(xml, "component");
						
		} catch (Exception e) {
			xml = "";
			System.out.println("Error retrieving sub component for " + parent + " from 4store. "
					+ e);
		}

		return xml;
	}
	
	/**
	 * Add a unique id xml element to each entry.
	 * 
	 * @param xml
	 * @return
	 */
	private String addUniqueId(String xml) {

		String beginNode = "<cdiscId>";
		String endNode = "</cdiscId>";
		
		int fromIndex = 0;
		int insertIndex;
				
		String node = "</parentId>";
		StringBuilder sb = new StringBuilder(xml);
		
		while (sb.indexOf(node, fromIndex) > 0) {
			insertIndex = sb.indexOf(node, fromIndex)  + node.length() + 1;
			sb.insert(insertIndex, beginNode + IdGenerator.nextId() + endNode);
			
			fromIndex = insertIndex + node.length();
		}
		
		return sb.toString();
	}
	
	private String addChildLabelNode(String xml, String searchString) {

		String node = "<binding name=\"" + searchString + "\">";
		String componentIndex = "<binding name=\"" + searchString + "\"><uri>";
		String nodeEnd = "</uri></binding>";
		
		String childNodeBegin = "<binding name=\"childLabel\"><literal datatype=\"xs:string\">";
		String childNodeEnd = "</literal></binding>";
		
		int componentStart = 0;
		int componentEnd = 0;
		
		int fromIndex = 0;
		int insertIndex;
		
		System.out.println(xml);
		
		
		StringBuilder sb = new StringBuilder(xml);
		
		while (sb.indexOf(node, fromIndex) > 0) {						
			componentStart = sb.indexOf(componentIndex, fromIndex) + componentIndex.length();
			componentEnd =  sb.indexOf(nodeEnd, componentStart);
			
			String component = sb.substring(componentStart, componentEnd);
			
			insertIndex = sb.indexOf(node, fromIndex);
			if (insertIndex > 0) {
				sb.insert(insertIndex, childNodeBegin + component + childNodeEnd);
			}
			fromIndex = insertIndex + childNodeBegin.length() + component.length() + childNodeEnd.length() + componentIndex.length();
		}
		
		//System.out.println(sb);
		return sb.toString();

	}
	
	
	/**
	 * For the category data returned, we need a way to map a parent/child
	 * relationship for the tree view. This method will add a node that maps
	 * back to the parent node.
	 * 
	 * @param categoriesXml
	 * @param parentNode
	 * @return
	 */
	private String addParentNode(String categoriesXml, String parentNode) {

		if (parentNode == null || parentNode.length() == 0) {
			parentNode = ROOT;
		}

		String replaceNode = "<result>";
		String parentElement = "<result>\n <parentId>" + parentNode
				+ "</parentId>";

		return categoriesXml.replaceAll(replaceNode, parentElement);
	}
	
	public static Store getStore() {
		if (s_store == null) {
			try {
				s_store = new Store(ConnectionInfo.STORE_URL);
			} catch (MalformedURLException e) {
				// TODO log error
				System.out.println("Error creating new 4store. " + e.toString() );
			}
		}
		return s_store;
	}


	@Override
	public String generateMindMap(SelectedComponents[] data, String templateName) throws IllegalArgumentException {
		
		System.out.println("generateMindMap called on server");
		
		MapGenerator mapGenerator = new MapGenerator();
			
		// create the name for the .mm file
		String templateFileName = templateName + "_" + getNextId();
		
		// remove spaces
		templateFileName = templateFileName.replaceAll(" ", "");
		
		// name of the html file
		String htmlFileName = templateFileName + ".html";
		
		// create the .mm file and return the name of it.
		String mmFileName = mapGenerator.generateMap(data, templateName, templateFileName);
			
		System.out.println("generated map file " + mmFileName);
		
		// create the html file 
		HtmlGenerator htmlGenerator = new HtmlGenerator(htmlFileName, mmFileName);
		htmlGenerator.generateHtmlFile();
		
		return htmlFileName;
	}
	
	public static int getNextId() {
		Random r = new Random();
		int id = r.nextInt(1000000);
		
		return id;
	}



}
