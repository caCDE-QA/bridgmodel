package edu.mayo.cdisc.tree.client.datasources;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.XMLTools;
import com.smartgwt.client.data.fields.DataSourceTextField;

public class MetadataDatasource extends DataSource {

	private static final String RECORD_X_PATH = "/sparql/results/result";
	public static final String DATASOURCE_ID = "MetadataDatasource";
	
	// this xpath is for the queries from the triplestore
	private static final String X_PATH_PREDICATE = "binding[@name='predicate']/uri";
	private static final String X_PATH_OBJECT1 = "binding[@name='object']/uri";
	private static final String X_PATH_OBJECT2 = "binding[@name='object']/literal";
	
	private static MetadataDatasource instance = null;

	public static MetadataDatasource getInstance() {
		if (instance == null) {
			instance = new MetadataDatasource(DATASOURCE_ID);
		}
		return instance;
	}

	private MetadataDatasource(String id) {

		setID(id);
		setRecordXPath(RECORD_X_PATH);
		
		DataSourceTextField predicateField = new DataSourceTextField("predicate");
		predicateField.setValueXPath(X_PATH_PREDICATE);
		
		
		// The object can come back in one of two paths
		//
		// <binding name="object"><literal datatype="xs:string">Clinical activities such as surgical procedure</literal></binding>
		// or
		// <binding name="object"><uri>urn:CTRv1.0:Activity</uri></binding>
		
		DataSourceTextField objectField1 = new DataSourceTextField("object1");
		objectField1.setValueXPath(X_PATH_OBJECT1);
		
		DataSourceTextField objectField2 = new DataSourceTextField("object2");
		objectField2.setValueXPath(X_PATH_OBJECT2);
		
		setFields(predicateField, objectField1, objectField2);
		setClientOnly(true);
	}
	
	public void setData(String xmlData) {
		xmlData = removeNamespace(xmlData);
		
		Object results = XMLTools.selectNodes(xmlData, RECORD_X_PATH);
		Record[] fetchRecords = recordsFromXML(results);
		
		setTestData(fetchRecords);
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
