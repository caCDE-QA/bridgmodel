package edu.mayo.cdisc.tree.client.datasources;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceTextField;

/**
 * DataSource for the selected components in tree.
 */
public class SelectedComponentDataSource extends DataSource {

public static final String DATASOURCE_ID = "SelectedComponentDataSource";
	
	public static final String CONCEPT = "concept";
	public static final String NAME = "name";
	
	public static final String TITLE_CONCEPT = "Concept";
	public static final String TITLE_NAME = "Selected Variable";
	
	private static SelectedComponentDataSource instance = null;

	public static SelectedComponentDataSource getInstance() {
		if (instance == null) {
			instance = new SelectedComponentDataSource(DATASOURCE_ID);
		}
		return instance;
	}

	private SelectedComponentDataSource(String id) {

		setID(id);

		DataSourceTextField conceptField = new DataSourceTextField(CONCEPT,TITLE_CONCEPT);
		conceptField.setRequired(false);
		
		DataSourceTextField nameField = new DataSourceTextField(NAME,TITLE_NAME);
		nameField.setRequired(true);
		
		setFields(conceptField, nameField);
		setClientOnly(true);
	}
	
}

