package edu.mayo.cdisc.tree.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.mayo.cdisc.tree.shared.SelectedComponents;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface CdiscService extends RemoteService {
	String getCategories(String parentId, String parentName, String parentNode) throws IllegalArgumentException;
	
	String getAttributes(String parentId, String parentName, String parentNode) throws IllegalArgumentException;
	
	String getAssociations(String parentId, String parentName, String parentNode) throws IllegalArgumentException;
	
	String getMetadata(String object) throws IllegalArgumentException;
	
	String getDataTypesForComponent(String parent, String parentNode) throws IllegalArgumentException;
	
	String getSingleDataType(String parent, String parentNode) throws IllegalArgumentException;
	
	String getSubComponents(String parent, String parentNode) throws IllegalArgumentException;
	
	String generateMindMap(SelectedComponents[] data, String templateName) throws IllegalArgumentException;
	
	
}
