package edu.mayo.cdisc.tree.client;

import java.util.ArrayList;

import com.smartgwt.client.data.SortSpecifier;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SortDirection;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

import edu.mayo.cdisc.tree.client.datasources.BridgDatasource;
import edu.mayo.cdisc.tree.client.datasources.SelectedComponentDataSource;

/**
 * ListGrid to display the selected components of the tree
 */
public class SelectedComponentListGrid extends ListGrid {
	
	public static final String COLUMN_RDF = "RDF";
	
	public static final String RDF_LINK_BASE = "http://informatics.mayo.edu/rest/project/bridg/template/cimi?name=";
	
	public static final String EMPTY_MESSAGE = "No selections.";
	public static final String HTML_TAG_START = "<strong>";
	public static final String HTML_TAG_END = "</strong>";
	
	private final SelectedComponentDataSource i_selectedComponentDataSource;
	
	
	public SelectedComponentListGrid() {
		super();

		i_selectedComponentDataSource = SelectedComponentDataSource.getInstance();

		setWidth100();
		setHeight("80%");
		setShowAllRecords(true);
		setWrapCells(false);
		setDataSource(i_selectedComponentDataSource);
		setEmptyMessage(EMPTY_MESSAGE);

		// hover attributes
		// setCanHover(true);
		// setShowHover(true);
		// setShowHoverComponents(true);
		setHoverMoveWithMouse(true);
		setHoverWidth(500);
		setHoverWrap(false);

		// crate a link field.
		ListGridField rdfField = new ListGridField(COLUMN_RDF, 100);
		rdfField.setType(ListGridFieldType.LINK);
		rdfField.setWrap(false);
		
		ListGridField conceptField = new ListGridField(SelectedComponentDataSource.CONCEPT, SelectedComponentDataSource.TITLE_CONCEPT);
		conceptField.setWidth("40%");
		conceptField.setWrap(false);
		conceptField.setShowHover(true);
		conceptField.setCanEdit(false);
		
		ListGridField nameField = new ListGridField(SelectedComponentDataSource.NAME, SelectedComponentDataSource.TITLE_NAME);
		nameField.setWidth("*");
		nameField.setWrap(false);
		nameField.setShowHover(true);
		nameField.setCanEdit(false);
				
		setFields(rdfField, conceptField, nameField);
		
		// set sorting to the first column - "Concept" 
		SortSpecifier sortSpecifier = new SortSpecifier(SelectedComponentDataSource.CONCEPT, SortDirection.ASCENDING);
		SortSpecifier[] sortSpecifiers = { sortSpecifier };
		setSort(sortSpecifiers);

	}
	
//	@Override
//	protected Canvas createRecordComponent(final ListGridRecord record, Integer colNum) {
//		String fieldName = this.getFieldName(colNum);  
//		System.out.println("field name = " + fieldName);
//		
//		if (fieldName.equals(COLUMN_RDF)) {  
//            IButton button = new IButton();  
//            button.setHeight(18);  
//            button.setWidth(65);                      
//            button.setTitle(COLUMN_RDF);  
//            button.addClickHandler(new ClickHandler() {
//				
//				@Override
//				public void onClick(ClickEvent event) {
//					 SC.say(record.getAttribute(SelectedComponentDataSource.NAME) + " info button clicked.");  
//				}
//			});  
//            return button;  
//        } else {  
//            return null;  
//        }  
//	}
	
	public void populateData(ListGridRecord[] selectedRecords){
		
		clearData();
		
		ArrayList<ListGridRecord> newRecords = new ArrayList<ListGridRecord>();
		
		for (ListGridRecord selected : selectedRecords) {
			String displayName = selected.getAttribute(BridgDatasource.DISPLAY_NAME);
			
			if (!displayName.startsWith(NodeTemplate.NODE_ASSOCIATIONS) &&
				!displayName.startsWith(NodeTemplate.NODE_ATTRIBUTES) &&
				!displayName.startsWith(NodeTemplate.NODE_CHILDREN)) {
				
				ListGridRecord newRecord = new ListGridRecord();
				
				String concept = selected.getAttribute(BridgDatasource.CONCEPT);
				String name = selected.getAttribute(BridgDatasource.SELECTED_VARIABLE);
				
				// create a new record with the values
				newRecord.setLinkText(RDF_LINK_BASE + name);
				newRecord.setAttribute(SelectedComponentDataSource.NAME, name);
				newRecord.setAttribute(SelectedComponentDataSource.CONCEPT, concept);
				
				newRecords.add(newRecord);
			}
		}
		
		i_selectedComponentDataSource.setTestData(newRecords.toArray(new ListGridRecord[newRecords.size()]));
		
		fetchData();
		redraw();
	}
	
	public void clearData() {
		setData(new ListGridRecord[0]);
	}
}
