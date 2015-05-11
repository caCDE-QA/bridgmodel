package edu.mayo.cdisc.tree.client;

import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

import edu.mayo.cdisc.tree.client.datasources.MetadataDatasource;

public class MetadataListGrid extends ListGrid {

	public static final String EMPTY_MESSAGE = "No data available.";

	private final MetadataDatasource i_metadataDatasource;
	private String i_xmlData;
	
	public MetadataListGrid() {
		super();

		i_metadataDatasource = MetadataDatasource.getInstance();

		setWidth100();
		setHeight100();
		setShowAllRecords(true);
		setWrapCells(false);
		setDataSource(i_metadataDatasource);
		setEmptyMessage(EMPTY_MESSAGE);

		// hover attributes
		// setCanHover(true);
		// setShowHover(true);
		// setShowHoverComponents(true);
		setHoverMoveWithMouse(true);
		setHoverWidth(500);
		setHoverWrap(false);

		//setShowRecordComponents(true);
		//setShowRecordComponentsByCell(true);

		ListGridField predicatefField = new ListGridField("predicate", "Predicate");
		predicatefField.setWidth("40%");
		predicatefField.setWrap(false);
		predicatefField.setShowHover(true);
		predicatefField.setCanEdit(false);
		
		ListGridField objectfField1 = new ListGridField("object1", "URI");
		objectfField1.setWidth("30%");
		objectfField1.setWrap(false);
		objectfField1.setShowHover(true);
		objectfField1.setCanEdit(false);
		
		ListGridField objectfField2 = new ListGridField("object2", "Literal");
		objectfField2.setWidth("*");
		objectfField2.setWrap(false);
		objectfField2.setShowHover(true);
		objectfField2.setCanEdit(false);
		
		setFields(predicatefField, objectfField1, objectfField2);
		
	}
	
	/**
	 * Call the search to get the matching data.
	 * 
	 * @param searchText
	 */
	public void populateData(String xmlData) {

		// System.out.println(xmlData);

		clearData();
		
		i_xmlData = xmlData;
		i_metadataDatasource.setData(i_xmlData);

		fetchData();
		redraw();
	}
	
	public void clearData() {
		setData(new ListGridRecord[0]);
	}
	
}
