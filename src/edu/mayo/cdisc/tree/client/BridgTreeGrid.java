package edu.mayo.cdisc.tree.client;

import com.smartgwt.client.types.SelectionAppearance;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.SelectionType;
import com.smartgwt.client.widgets.grid.events.CellClickEvent;
import com.smartgwt.client.widgets.grid.events.CellClickHandler;
import com.smartgwt.client.widgets.grid.events.SelectionUpdatedEvent;
import com.smartgwt.client.widgets.grid.events.SelectionUpdatedHandler;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.events.FolderOpenedEvent;
import com.smartgwt.client.widgets.tree.events.FolderOpenedHandler;

import edu.mayo.cdisc.tree.client.datasources.BridgDatasource;

public class BridgTreeGrid extends TreeGrid {
	
	public BridgTreeGrid(final BridgDatasource bridgeDS) {
		super();

		 // shows the column header "Name" for the treegrid.
		setShowHeader(false);
							
		setLeaveScrollbarGap(false);
		setAnimateFolders(true);
		setCanAcceptDroppedRecords(false);
		setCanReparentNodes(false);
		setSelectionType(SelectionStyle.SINGLE);

		setDataSource(bridgeDS);
		setAutoFetchData(true);
		setShowResizeBar(true);

		// Set the folder icons for opened and closed. They are in the
		// war/images directory.
		setShowOpenIcons(true);
		setFolderIcon("folder.png");
		setOpenIconSuffix("opened");
		setClosedIconSuffix("closed");
		
		setShowConnectors(true);
		
		// CHECKBOX Settings
		setSelectionType(SelectionStyle.MULTIPLE);
		setSelectionAppearance(SelectionAppearance.CHECKBOX);  
		setShowSelectedStyle(true);  
        setShowPartialSelection(true); 

		 //setShowRoot(true);

		//getTree().getParent(node)
				
		addFolderOpenedHandler(new FolderOpenedHandler() {
						
			@Override
			public void onFolderOpened(FolderOpenedEvent event) {
				
				String id = event.getNode().getAttribute("cdiscId");
				String parentLongName = event.getNode().getAttribute("parentLongName");
				
				String name = event.getNode().getAttribute("name");
				String displayNode = event.getNode().getAttribute("displayName");
				String treeLevel = event.getNode().getAttribute("treeLevel");
				String association = event.getNode().getAttribute("association");
				boolean subComponent = event.getNode().getAttributeAsBoolean("subComponent");
				String dataTypeOfComponent = event.getNode().getAttribute(BridgDatasource.DATA_TYPE_OF_COMPONENT);
				String concept = event.getNode().getAttribute(BridgDatasource.CONCEPT);
				String selectedVariable = event.getNode().getAttribute(BridgDatasource.SELECTED_VARIABLE);
				
//				System.out.println("Folder open event -- parentId = " + id);
//				System.out.println("Folder open event -- parentNode = " + node);
//				System.out.println("Folder open event -- displayName = " + displayNode);
//				System.out.println("Folder open event -- treeLevel = "+ treeLevel);

				bridgeDS.setParentId(id);
				bridgeDS.setParentNode(name);
				bridgeDS.setParentLongName(parentLongName);
				bridgeDS.setName(name);
				
				bridgeDS.setDisplayNode(displayNode);
				bridgeDS.setTreeLevel(treeLevel);
				bridgeDS.setAssociation(association);
				bridgeDS.setSubComponent(subComponent);
				bridgeDS.setDataTypeOfComponent(dataTypeOfComponent);
				bridgeDS.setConcept(concept);
				bridgeDS.setSelectedVariable(selectedVariable);
			}
		});
		
//		addSelectionUpdatedHandler(new SelectionUpdatedHandler() {
//			
//			@Override
//			public void onSelectionUpdated(SelectionUpdatedEvent event) {
//				System.out.println("selection changed");	
//			}
//		});
//		
		addCellClickHandler(new CellClickHandler() {
			
			@Override
			public void onCellClick(CellClickEvent event) {
				System.out.println("CellClickHandler");	
				
			}
		});


	}

}
