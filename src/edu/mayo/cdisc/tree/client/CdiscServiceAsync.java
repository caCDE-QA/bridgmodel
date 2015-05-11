package edu.mayo.cdisc.tree.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.mayo.cdisc.tree.shared.SelectedComponents;

/**
 * The async counterpart of <code>CdiscService</code>.
 */
public interface CdiscServiceAsync {
	void getCategories(String parentId, String parentName, String parentNode,
			AsyncCallback<String> callback) throws IllegalArgumentException;
	
	void getAttributes(String parentId, String parentName, String parentNode,
			AsyncCallback<String> callback) throws IllegalArgumentException;
	
	void getAssociations(String parentId, String parentName, String parentNode,
			AsyncCallback<String> callback) throws IllegalArgumentException;
	
	void getMetadata(String object,
			AsyncCallback<String> callback) throws IllegalArgumentException;
	
	void getDataTypesForComponent(String parent, String parentNode,
			AsyncCallback<String> callback) throws IllegalArgumentException;
	
	void getSingleDataType(String parent, String parentNode,
			AsyncCallback<String> callback) throws IllegalArgumentException;
	
	void getSubComponents(String parent, String parentNode,
			AsyncCallback<String> callback) throws IllegalArgumentException;
	
	void generateMindMap(SelectedComponents[] data, String templateName,
			AsyncCallback<String> callback) throws IllegalArgumentException;
}
