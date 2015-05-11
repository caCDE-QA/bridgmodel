package edu.mayo.cdisc.tree.client.datasources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.XMLTools;
import com.smartgwt.client.data.fields.DataSourceTextField;

import edu.mayo.cdisc.tree.client.CdiscService;
import edu.mayo.cdisc.tree.client.CdiscServiceAsync;
import edu.mayo.cdisc.tree.client.NodeTemplate;

public class BridgDatasource extends DataSource {

	private static final String RECORD_X_PATH = "/sparql/results/result";
	public static final String DATASOURCE_ID = "BridgeDatasource";
	
	public static final String ROOT = "root";
	public static final String TREE_LEVEL = "treeLevel";
	public static final String ICON = "icon";
	public static final String PARENT = "parent";
	public static final String PARENT_NODE = "parentNode";
	public static final String DISPLAY_NAME = "displayName";
	public static final String NAME = "name";
	public static final String ASSOCIATION = "association";
	public static final String SUBCOMPONENT = "subComponent";
	
	public static final String PARENT_ID = "parentId";
	public static final String CDISC_ID = "cdiscId";
	
	public static final String DATA_TYPE = "datatype";
	public static final String COMPONENT = "component";
	public static final String DATA_TYPE_OF_COMPONENT = "datatypeOfComponent";
	
	public static final String ASSOCIATION_LABEL = "associationLabel";
	public static final String TARGET_CLASS_LABEL = "targetClassLabel";
	
	public static final String DIRECT_ATTRIBUTE_DESCENDANT = "directAttributeDescendant";
		
	public static final String PREFIX = "http://www.bridgmodel.org/owl#";
	public static final String COMPONENT_PREFIX = "http://www.hl7.org/owl/iso-dt-2.0#";
	public static final String COMPONENT_PREFIX_XMLSCHEMA = "http://www.w3.org/2001/XMLSchema#";
	
	public static final String CONCEPT = "concept";
	public static final String SELECTED_VARIABLE = "selectedVariable";	

	// this xpath is for the queries from the triplestore
	private static final String X_PATH_PARENT = "binding[@name='child']/uri";
	private static final String X_PATH_LABEL = "binding[@name='childLabel']/literal";

	private static final String X_PATH_ASSOCIATION = "binding[@name='association']/uri";
	private static final String X_PATH_DEFINITION = "binding[@name='definition']/literal";
	private static final String X_PATH_DATATYPE = "binding[@name='datatype']/uri";
	
	private static final String X_PATH_COMPONENT = "binding[@name='component']/uri";
	private static final String X_PATH_DATA_TYPE_OF_COMPONENT = "binding[@name='datatypeOfComponent']/uri";
	
	private static final String X_PATH_TARGET_CLASS_LABEL = "binding[@name='targetClassLabel']/literal";
	private static final String X_PATH_ASSOCIATION_LABEL= "binding[@name='associationLabel']/literal";
	
	private String i_name = null;
	private String i_parentId = null;
	private String i_parentName = null;
	private String i_parentLongName = null;
	private String i_displayName = null;
	private String i_treeLevel = null;
	private String i_association = null;
	private boolean i_subComponent = false;
	private String i_dataTypeOfComponent = null;
	private String i_concept = null;
	private String i_selectedVariable = null;
	
	private static BridgDatasource instance = null;

	public static BridgDatasource getInstance() {
		if (instance == null) {
			instance = new BridgDatasource(DATASOURCE_ID);
		}
		return instance;
	}

	private BridgDatasource(String id) {

		setID(id);
		setRecordXPath(RECORD_X_PATH);
		
		// Primary Key
		DataSourceTextField pkField = new DataSourceTextField("cdiscId");
		pkField.setPrimaryKey(true);
		pkField.setRequired(true);
		
		// Foreign Key
		DataSourceTextField parentIdField = new DataSourceTextField(PARENT_ID, "BRIDG Parent ID");
		parentIdField.setForeignKey(DATASOURCE_ID + ".cdiscId");
		parentIdField.setRootValue(ROOT);
		parentIdField.setRequired(true);
		
		DataSourceTextField parentField = new DataSourceTextField(PARENT, "Parent ID", 128, false);
		parentField.setValueXPath(X_PATH_PARENT);
		parentField.setHidden(true);

		DataSourceTextField nameField = new DataSourceTextField(NAME, "name",128, false);
		nameField.setValueXPath(X_PATH_LABEL);

		DataSourceTextField displayNameField = new DataSourceTextField(DISPLAY_NAME, "display name", 128, true);
		displayNameField.setValueXPath(X_PATH_LABEL);

		DataSourceTextField parentNodeField = new DataSourceTextField(PARENT_NODE, "BRIDG Categories", 32, true);
		parentNodeField.setHidden(false);

		// Fields from the attributes node
		DataSourceTextField associationField = new DataSourceTextField(ASSOCIATION);
		associationField.setValueXPath(X_PATH_ASSOCIATION);

		DataSourceTextField definitionField = new DataSourceTextField("definition");
		definitionField.setValueXPath(X_PATH_DEFINITION);

		DataSourceTextField datatypeField = new DataSourceTextField("datatype");
		datatypeField.setValueXPath(X_PATH_DATATYPE);
		
		// Fields for component types
		DataSourceTextField componentField = new DataSourceTextField(COMPONENT);
		componentField.setValueXPath(X_PATH_COMPONENT);
		
		DataSourceTextField dataTypeOfComponentField = new DataSourceTextField(DATA_TYPE_OF_COMPONENT);
		dataTypeOfComponentField.setValueXPath(X_PATH_DATA_TYPE_OF_COMPONENT);
		
		// Fields for association types
		DataSourceTextField associationLabelField = new DataSourceTextField(ASSOCIATION_LABEL);
		associationLabelField.setValueXPath(X_PATH_ASSOCIATION_LABEL);
		
		DataSourceTextField targetClassLabelField = new DataSourceTextField(TARGET_CLASS_LABEL);
		targetClassLabelField.setValueXPath(X_PATH_TARGET_CLASS_LABEL);
		
		setTitleField(DISPLAY_NAME);
		
		setFields(pkField, parentIdField,
				parentField, parentNodeField, nameField, displayNameField,
				associationField, definitionField, datatypeField,  
				componentField, dataTypeOfComponentField,
				associationLabelField, targetClassLabelField);
		
		setClientOnly(true);
	}

	@Override
	protected void transformResponse(DSResponse response, DSRequest request,
			Object data) {

		if (request.getOperationType() != null) {
			switch (request.getOperationType()) {

			case ADD:
				break;
			case FETCH: {
				executeFetch(request);
			}
				break;
			case REMOVE:
				break;
			case UPDATE:
				break;

			default:
				break;
			}
		}
		super.transformResponse(response, request, data);
	}

	/**
	 * Fetch the categories by requesting them from the RPC call.
	 */
	private void executeFetch(DSRequest request) {

		System.out.println("executeFetch -- parentId = " + i_parentId);
		System.out.println("executeFetch -- i_parentName = " + i_parentName);
		System.out.println("executeFetch -- i_displayName = " + i_displayName);
		System.out.println("executeFetch -- i_treeLevel = " + i_treeLevel);
		System.out.println("executeFetch -- i_association = " + i_association);
		System.out.println("executeFetch -- i_name = " + i_name);
		

		if (i_name == null && i_treeLevel == null) {
			fetchChildrenNodes(i_name, i_displayName, i_parentName);
		}

		else if (i_name.startsWith(NodeTemplate.NODE_CHILDREN + "_")) {
			// need to get the parent name that will be used in the query
			String displayName = i_name
					.substring((NodeTemplate.NODE_CHILDREN + "_").length());

			fetchChildrenNodes(i_name, displayName, i_parentId);
		}

		else if (i_name.startsWith(NodeTemplate.NODE_ATTRIBUTES + "_")) {
			// need to get the attributes for a given node
			String displayName = i_name.substring((NodeTemplate.NODE_ATTRIBUTES + "_").length());

			System.out.println("attributes = " + displayName);

			fetchAttributeNodes(i_name, displayName, i_parentId, i_concept, i_selectedVariable);
		}
		else if (i_name.startsWith(NodeTemplate.NODE_ASSOCIATIONS + "_") || 
				 i_treeLevel.startsWith("levelAssociations") ||
				 i_treeLevel.startsWith("levelInheritedAssociations")) {
			
			// need to get the associations for a given node
			String displayName = "";
			
			if (i_name.startsWith(NodeTemplate.NODE_ASSOCIATIONS + "_")) {
				displayName = i_name.substring((NodeTemplate.NODE_ASSOCIATIONS + "_").length());
			}
			else {
				// this is a subnode under the main association node
				displayName = i_name;
			}
				
			System.out.println("associations = " + displayName);

			fetchAssocationNodes(i_name, displayName, i_parentId, i_concept, i_selectedVariable);
		}
		
		else if (i_treeLevel != null && 
				(i_treeLevel.equals("levelAttributes") || i_treeLevel.equals("levelInheritedAttributes") ) && 
				!i_subComponent) {
			
			System.out.println("Need to fetch sub attributes...");
			fetchComponentTypes(i_parentName, i_treeLevel, i_association, i_parentId, i_concept, i_selectedVariable);
		}
		else if (i_subComponent) {
			System.out.println("Need to fetch sub components...");
			fetchSubComponents(i_parentName, i_treeLevel, i_association, i_dataTypeOfComponent, 
					i_parentId, i_concept, i_selectedVariable);
		}
	}
	
	private void fetchChildrenNodes(String parentId, String displayName,
			String parentNodeName) {

		CdiscServiceAsync cdiscService = GWT.create(CdiscService.class);
		cdiscService.getCategories(parentId, displayName, parentNodeName,
				new AsyncCallback<String>() {

					public void onFailure(Throwable caught) {
						System.out.println("Server Error " + caught.toString());
					}

					public void onSuccess(String elements) {

						// System.out.println("executeFetch for Category Tree called \n"
						// + elements);

						if (elements == null || elements.trim().length() == 0) {
							return;
						}

						elements = removeNamespace(elements);

						Object results = XMLTools.selectNodes(elements,
								RECORD_X_PATH);
						Record[] fetchRecords = recordsFromXML(results);

						if (fetchRecords != null) {
							// add each record
							for (Record record : fetchRecords) {

								// don't allow the bridg class to be selectable.
								record.setAttribute("canSelect", false);
								
								record.setAttribute(TREE_LEVEL, "levelMain");

								// set the display name to <strong>
								String displayName = record.getAttribute(DISPLAY_NAME);
								record.setAttribute(DISPLAY_NAME, "<strong>" + displayName + "</strong>");
								
								// always set the concept.
								record.setAttribute(CONCEPT, displayName);
								
								String[] testAttributes = record
										.getAttributes();
								for (String attr : testAttributes) {
									System.out.println(attr + " = "
											+ record.getAttribute(attr));
								}
								
								addData(record);

								// get the children nodes
								Record[] childrenNodes = NodeTemplate
										.addChildNodes(record);

								// add the children nodes
								for (Record childRecord : childrenNodes) {
									addData(childRecord);
								}
							}
						}
					}
				});

	}

	private void fetchAttributeNodes(final String parentName, String displayName, 
			final String parentId, final String concept, final String selectedVariable) {
		
		CdiscServiceAsync cdiscService = GWT.create(CdiscService.class);
		cdiscService.getAttributes(parentName, displayName,  parentId,
				new AsyncCallback<String>() {

					@Override
					public void onSuccess(String elements) {
						System.out.println(elements);

						if (elements == null || elements.trim().length() == 0) {
							return;
						}

						elements = removeNamespace(elements);

						Object results = XMLTools.selectNodes(elements,
								RECORD_X_PATH);
						Record[] fetchRecords = recordsFromXML(results);

						if (fetchRecords != null) {
														
							// add each record
							for (Record record : fetchRecords) {
								
								// debug: display all attributes of the record
								String[] testAttributes = record
										.getAttributes();
								for (String attr : testAttributes) {
									System.out.println(attr + " = "
											+ record.getAttribute(attr));
								}
								
								String displayName = record.getAttribute(DISPLAY_NAME);
							
								record.setAttribute("canSelect", true);	
								
								
								// If the attribute is a direct object of the selected attribute,
								// then it goes under the "Attributes" node.  
								// If not, it goes under the "Inherited Attributes"
								
								String association = record.getAttribute(ASSOCIATION);
								String actualParentName = parentName.substring(NodeTemplate.NODE_ATTRIBUTES.length()+1);
								String childLabel = record.getAttribute("childLabel");
								String objectType = PREFIX + actualParentName + ".";
								
								// set the concept that was passed in.
								record.setAttribute(CONCEPT, concept);
								
								String updatedSelectedVariable;
								// update the selected variable - adding on the current attribute.
								if (selectedVariable != null && selectedVariable.length() > 0) {
									updatedSelectedVariable = selectedVariable + "." + displayName;
								}
								else {
									updatedSelectedVariable = concept + "." + displayName;
								}
								record.setAttribute(SELECTED_VARIABLE, updatedSelectedVariable);
								
								if (!(association.startsWith(objectType))) {
								
									// set all components with this icon
									record.setAttribute(ICON, "inheritedAttribute.png");
									
									// this is a inherited attribute... change parent node
									//String newParentNode = NodeTemplate.NODE_ASSOCIATIONS + "_" + actualParentName;
									//record.setAttribute(PARENT_NODE, newParentNode);
									
									// The node template generates ids in sequence.  The inherited attr. node
									// id is one greater than the attribute node id.
									//String inheritedParentId = Integer.parseInt(parentId) + 1 +"";
									//record.setAttribute(PARENT_ID, inheritedParentId);
																		
									//if (!(uniqueInheritedAttributes.contains(displayName))) {
										// add only if name is unique
										//uniqueInheritedAttributes.add(displayName);
									record.setAttribute(TREE_LEVEL, "levelInheritedAttributes");
										//record.setAttribute(ICON, "inheritedAttribute.png");
										//addData(record);
									//}																		
								}
								else {
								//else if (!(uniqueAttributes.contains(displayName))) {
									// add only if name is unique
								//	uniqueAttributes.add(displayName);
									record.setAttribute(TREE_LEVEL, "levelAttributes");
									record.setAttribute(ICON, "attribute.png");
								}
								addData(record);																
							}
						}

					}

					@Override
					public void onFailure(Throwable caught) {
						System.out.println("Server Error " + caught.toString());
					}

				});
	}
	
	private void fetchAssocationNodes(final String parentName, String displayName, 
			final String parentId, final String concept, final String selectedVariable) {

		CdiscServiceAsync cdiscService = GWT.create(CdiscService.class);
		cdiscService.getAssociations(parentName, displayName,  parentId,
				new AsyncCallback<String>() {

					@Override
					public void onSuccess(String elements) {
						System.out.println(elements);

						if (elements == null || elements.trim().length() == 0) {
							return;
						}

						elements = removeNamespace(elements);

						Object results = XMLTools.selectNodes(elements,
								RECORD_X_PATH);
						Record[] fetchRecords = recordsFromXML(results);

						if (fetchRecords != null) {
														
							// add each record
							for (Record record : fetchRecords) {
								
								// debug: display all attributes of the record
								String[] testAttributes = record
										.getAttributes();
								for (String attr : testAttributes) {
									System.out.println(attr + " = "
											+ record.getAttribute(attr));
								}
								
								// don't allow the bridg class to be selectable.
								record.setAttribute("canSelect", false);								
								
								String displayName = record.getAttribute(DISPLAY_NAME);
								String associationLabel = record.getAttribute(ASSOCIATION_LABEL);
								String targetClassLabel = record.getAttribute(TARGET_CLASS_LABEL);
								
								
								// combine the displayLabel with the association Label.
								record.setAttribute(DISPLAY_NAME, "(" +associationLabel + ") " + displayName);
								
								// If the association is a direct object of the selected Association,
								// then it gets an "Associations" node icon.  
								// If not, it goes gets the "Inherited Associations" node icon.
								
								String association = record.getAttribute(ASSOCIATION);
								String actualParentName = parentName.substring(NodeTemplate.NODE_ASSOCIATIONS.length()+1);
								String objectType = PREFIX + actualParentName + ".";
								
								// set the concept that was passed in.
								record.setAttribute(CONCEPT, concept);
								
								String updatedSelectedVariable;
								// update the selected variable - adding on the current attribute.
								if (selectedVariable != null && selectedVariable.length() > 0) {
									updatedSelectedVariable = selectedVariable + "." + displayName + "." +  targetClassLabel;
								}
								else {
									updatedSelectedVariable = concept + "." + displayName + "." +  targetClassLabel;
								}
								record.setAttribute(SELECTED_VARIABLE, updatedSelectedVariable);
								
								
								if (association != null &&  !(association.startsWith(objectType))) {
									// set all components with this icon
									record.setAttribute(ICON, "inheritedAssociation.png");								
									record.setAttribute(TREE_LEVEL, "levelInheritedAssociations");																	
								}
								else {
									record.setAttribute(TREE_LEVEL, "levelAssociations");
									record.setAttribute(ICON, "association.png");
								}
								addData(record);
								
								// add a child node base on the name from targetClassLabel
								Record targetClass = new Record();
								
								// don't allow the bridg class to be selectable.
								targetClass.setAttribute("canSelect", false);
								
								// make sure we set this is set to false so we will know later
								targetClass.setAttribute(DIRECT_ATTRIBUTE_DESCENDANT, false);
								
								targetClass.setAttribute(NAME, targetClassLabel);
								targetClass.setAttribute(DISPLAY_NAME, targetClassLabel);
								targetClass.setAttribute(PARENT, PREFIX + targetClassLabel);
								targetClass.setAttribute(CDISC_ID, NodeTemplate.nextId());
								
								// set the concept that was passed in.
								//targetClass.setAttribute(CONCEPT, concept);
								targetClass.setAttribute(CONCEPT, targetClassLabel);
																
								// parent is the record above we are working with
								targetClass.setAttribute(PARENT_ID, record.getAttribute(CDISC_ID));
								
								// set the display name to <strong>
								targetClass.setAttribute(DISPLAY_NAME, "<strong>" + targetClassLabel + "</strong>");
								//targetClass.setAttribute(ICON, "associationNode.png");
								//targetClass.setAttribute(TREE_LEVEL, "levelAssociations");
								
								// *** Make this the end of the tree branch... ***
								//targetClass.setAttribute("isFolder", false);
								
								//String updatedSelectedVariable2 = "";
								
								// update the selected variable - adding on the current attribute.
//								if (selectedVariable != null && selectedVariable.length() > 0) {
//									updatedSelectedVariable = selectedVariable + "." + displayName + "." +  targetClassLabel;
//								}
//								else {
//									updatedSelectedVariable = concept + "." + displayName + "." +  targetClassLabel;
//								}
								
								// for now, set the updatedSelectedVariable2 same as it's parent updatedSelectedVariable
								// until I understand this more.
								targetClass.setAttribute(SELECTED_VARIABLE, updatedSelectedVariable);
								
								
								addData(targetClass);
								
								// get the children nodes
								Record[] childrenNodes = NodeTemplate
										.addChildNodes(targetClass);

								// add the children nodes
								for (Record childRecord : childrenNodes) {
									addData(childRecord);
								}
								
							}
						}

					}

					@Override
					public void onFailure(Throwable caught) {
						System.out.println("Server Error retrieving assocations " + caught.toString());
					}

				});
	}

	
	/**
	 * Get the set of component nodes that are directly under the nodes that are 
	 * under the "Attributes" node.
	 * 
	 * @param parentNodeName
	 * @param treeLevel
	 * @param assocation
	 */
	private void fetchComponentTypes(String parentNodeName, final String treeLevel, String assocation, 
			final String parentId, final String concept, final String selectedVariable) {

		String tempParentComponent = null;
		if (assocation != null) {
			
			// need to strip off the prefix
			// http://www.bridgmodel.org/owl#BiologicEntity.actualIndicator
			// would be BiologicEntity.actualIndicator
			tempParentComponent = assocation.substring(PREFIX.length());
		}
		else {
			tempParentComponent = parentNodeName.substring(COMPONENT_PREFIX.length());;
		}
		
		final String parentComponent = tempParentComponent;
		
		CdiscServiceAsync cdiscService = GWT.create(CdiscService.class);
		cdiscService.getDataTypesForComponent(parentComponent, parentId,
				new AsyncCallback<String>() {

					public void onFailure(Throwable caught) {
						System.out.println("Server Error " + caught.toString());
					}

					public void onSuccess(String elements) {

						System.out.println("executeFetch fetchComponentTypes() called \n"+ elements);

						if (elements == null || elements.trim().length() == 0) {
							return;
						}

						elements = removeNamespace(elements);

						Object results = XMLTools.selectNodes(elements,
								RECORD_X_PATH);
						Record[] fetchRecords = recordsFromXML(results);

						if (fetchRecords != null && fetchRecords.length > 0) {
							
							// add each record
							for (Record record : fetchRecords) {

								record.setAttribute("canSelect", true);	
								
								// Set the tree level
								record.setAttribute(TREE_LEVEL, treeLevel);

								// set the display name to be the same as the component
								final String name = record.getAttribute(COMPONENT);
								final String displayName = name.substring(COMPONENT_PREFIX.length());
															
								record.setAttribute(DISPLAY_NAME, displayName);
								record.setAttribute(ICON, "attribute.png");
								
								
								// set the concept that was passed in.
								record.setAttribute(CONCEPT, concept);
								
								// update the selected variable - adding on the current attribute.
								String updatedSelectedVariable;
								
								if (selectedVariable != null && selectedVariable.length() > 0) {
									updatedSelectedVariable = selectedVariable + "." + displayName;
								}
								else {
									updatedSelectedVariable = concept + "." + displayName;
								}
								record.setAttribute(SELECTED_VARIABLE, updatedSelectedVariable);
								
								
								// if this of type String, then show different node
								String dataTypeOfComponent = record.getAttribute(DATA_TYPE_OF_COMPONENT);
								if (dataTypeOfComponent != null && dataTypeOfComponent.endsWith("#string")) {
									record.setAttribute(ICON, "leafNode.png");
									record.setAttribute("isFolder", false);
								}
								
								// indicate that this is a subcomponent so we can set the query correctly
								// when clicked on.
								record.setAttribute(SUBCOMPONENT, true);
																
								String[] testAttributes = record
										.getAttributes();
								for (String attr : testAttributes) {
									System.out.println(attr + " = "
											+ record.getAttribute(attr));
								}
								
								addData(record);
							}
						}
						
						else {
														
							fetchSingleDatatype(parentComponent, parentId, concept, selectedVariable);
						}
					}
				});

	}
	
	
	private void fetchSingleDatatype(String parentComponent, String parentId, 
			final String concept, final String selectedVariable) {
		
		CdiscServiceAsync cdiscService = GWT.create(CdiscService.class);
		cdiscService.getSingleDataType(parentComponent, parentId, new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String result) {
				System.out.println(result);
				
				if (result == null || result.trim().length() == 0) {
					return;
				}

				result = removeNamespace(result);

				Object results = XMLTools.selectNodes(result,
						RECORD_X_PATH);
				Record[] fetchRecords = recordsFromXML(results);

				if (fetchRecords != null && fetchRecords.length > 0) {
					
					// add each record
					for (Record record : fetchRecords) {
						
						
						String[] testAttributes = record
								.getAttributes();
						for (String attr : testAttributes) {
							System.out.println(attr + " = "
									+ record.getAttribute(attr));
						}
						
						record.setAttribute("canSelect", true);
						
						String displayName = record.getAttribute(DISPLAY_NAME);
						displayName = displayName.substring(COMPONENT_PREFIX.length());
						
						record.setAttribute(DISPLAY_NAME, displayName);
						record.setAttribute(ICON, "leafNode.png");
						record.setAttribute("isFolder", false);
						
						// set the concept that was passed in.
						record.setAttribute(CONCEPT, concept);	
						
						// update the selected variable - adding on the current attribute.
						String updatedSelectedVariable;
						
						if (selectedVariable != null && selectedVariable.length() > 0) {
							updatedSelectedVariable = selectedVariable + "." + displayName;
						}
						else {
							updatedSelectedVariable = concept + "." + displayName;
						}
						record.setAttribute(SELECTED_VARIABLE, updatedSelectedVariable);
						
						
						addData(record);
					}
			
				} 
			}
				
			@Override
			public void onFailure(Throwable caught) {
				System.out.println(caught);
			}
		} );
		
		
	}
	
	
	private void fetchSubComponents(String parentNodeName, final String treeLevel, 
			String assocation, String dataTypeOfComponent, final String parentId, 
			final String concept, final String selectedVariable) {

		String parentComponent = null;
		if (assocation != null) {
			
			// need to strip off the prefix
			// http://www.bridgmodel.org/owl#BiologicEntity.actualIndicator
			// would be BiologicEntity.actualIndicator
			parentComponent = assocation.substring(PREFIX.length());
		}
		else {
			parentComponent = parentNodeName.substring(COMPONENT_PREFIX.length());;
		}
		
		String tempDataTypeOfComonentTrimmed = "";
		if (dataTypeOfComponent.contains(COMPONENT_PREFIX)){
			tempDataTypeOfComonentTrimmed = dataTypeOfComponent.substring(COMPONENT_PREFIX.length());
		}
		else if (dataTypeOfComponent.contains(COMPONENT_PREFIX_XMLSCHEMA)){
			tempDataTypeOfComonentTrimmed = dataTypeOfComponent.substring(COMPONENT_PREFIX_XMLSCHEMA.length());
		}
		
		final String dataTypeOfComponentTrimmed = tempDataTypeOfComonentTrimmed; // dataTypeOfComponent.substring(COMPONENT_PREFIX.length());
		final String parentNodeNameTrimmed = parentNodeName.substring(COMPONENT_PREFIX.length());
		
		CdiscServiceAsync cdiscService = GWT.create(CdiscService.class);
		cdiscService.getSubComponents(dataTypeOfComponentTrimmed, parentId,
				new AsyncCallback<String>() {

					public void onFailure(Throwable caught) {
						System.out.println("Server Error " + caught.toString());
					}

					public void onSuccess(String elements) {

						System.out.println("executeFetch getSubComponents() called \n"+ elements);

						if (elements == null || elements.trim().length() == 0) {
							return;
						}

						elements = removeNamespace(elements);

						Object results = XMLTools.selectNodes(elements,
								RECORD_X_PATH);
						Record[] fetchRecords = recordsFromXML(results);

						if (fetchRecords != null && fetchRecords.length > 0) {
							
							// add each record
							for (Record record : fetchRecords) {
								
								record.setAttribute("canSelect", true);	
								
								// Set the tree level
								record.setAttribute(TREE_LEVEL, treeLevel);

								// set the display name to be the same as the component
								final String name = record.getAttribute(COMPONENT);
								final String displayName = name.substring(COMPONENT_PREFIX.length());
																
								record.setAttribute(DISPLAY_NAME, displayName);
								record.setAttribute(NAME, name);
								record.setAttribute(ICON, "attribute.png");
								
								// indicate that this is a subcomponent so we can set the query correctly
								// when clicked on.
								record.setAttribute(SUBCOMPONENT, true);
									
								// set the concept that was passed in.
								record.setAttribute(CONCEPT, concept);	
								
								// update the selected variable - adding on the current attribute.
								String updatedSelectedVariable;
								
								if (selectedVariable != null && selectedVariable.length() > 0) {
									updatedSelectedVariable = selectedVariable + "." + displayName;
								}
								else {
									updatedSelectedVariable = concept + "." + displayName;
								}
								record.setAttribute(SELECTED_VARIABLE, updatedSelectedVariable);
								
								
								
								// if this of type String, then show different node
								String dataTypeOfComponent = record.getAttribute(DATA_TYPE_OF_COMPONENT);
								if (dataTypeOfComponent != null && dataTypeOfComponent.endsWith("#string")) {
									record.setAttribute(ICON, "leafNode.png");
									record.setAttribute("isFolder", false);
								}
								
								addData(record);
								
								String[] testAttributes = record
										.getAttributes();
								for (String attr : testAttributes) {
									System.out.println(attr + " = "
											+ record.getAttribute(attr));
								}
							}
						}
						
						else {
							Record record = new Record();
							
							record.setAttribute("canSelect", true);
							
							//record.setAttribute(PARENT_NODE, parentNodeNameTrimmed);
							record.setAttribute(DISPLAY_NAME, dataTypeOfComponentTrimmed);
							record.setAttribute(NAME, dataTypeOfComponentTrimmed);
							
							record.setAttribute(CDISC_ID, NodeTemplate.nextId());
							record.setAttribute(PARENT_ID, parentId);
							
							record.setAttribute(ICON, "leafNode.png");
							record.setAttribute("isFolder", false);
							
							// set the concept that was passed in.
							record.setAttribute(CONCEPT, concept);	
							
							// update the selected variable - adding on the current attribute.
							String updatedSelectedVariable;
							
							if (selectedVariable != null && selectedVariable.length() > 0) {
								updatedSelectedVariable = selectedVariable + "." + dataTypeOfComponentTrimmed;
							}
							else {
								updatedSelectedVariable = concept + "." + dataTypeOfComponentTrimmed;
							}
							record.setAttribute(SELECTED_VARIABLE, updatedSelectedVariable);
							
									
							
							addData(record);
						}
					}
				});

	}
	public void setParentId(String id) {
		i_parentId = id;
	}

	public void setName(String name) {
		i_name = name;
	}
	
	public void setParentNode(String node) {
		i_parentName = node;
	}
	
	public void setParentLongName(String longName) {
		i_parentLongName = longName;
	}

	public void setDisplayNode(String displayNode) {
		i_displayName = displayNode;
	}
	
	public void setTreeLevel(String treeLevel) {
		i_treeLevel = treeLevel;
	}
	
	public void setAssociation(String association) {
		i_association = association;
	}
	
	public void setSubComponent(boolean subComponent) {
		i_subComponent = subComponent;
	}

	public void setDataTypeOfComponent(String dataTypeOfComponent) {
		i_dataTypeOfComponent = dataTypeOfComponent;
	}
	
	public void setConcept(String concept) {
		i_concept = concept;
	}
	
	public void setSelectedVariable(String selectedVariable) {
		i_selectedVariable = selectedVariable;
	}
	

	/**
	 * Remove the sparql namespace. smartgwt doesn't convert the records
	 * correctly if it is there.
	 * 
	 * @param categoriesXml
	 * @returnL
	 */
	private String removeNamespace(String categoriesXml) {

		String namespace = "<sparql xmlns=\"http://www.w3.org/2005/sparql-results#\">";
		String sparqlElement = "<sparql>";

		String result = categoriesXml.replace(namespace, sparqlElement);

		return result;
	}
}
