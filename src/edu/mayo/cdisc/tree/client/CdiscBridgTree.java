package edu.mayo.cdisc.tree.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.data.XMLTools;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Autofit;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellClickEvent;
import com.smartgwt.client.widgets.grid.events.CellClickHandler;
import com.smartgwt.client.widgets.grid.events.SelectionUpdatedEvent;
import com.smartgwt.client.widgets.grid.events.SelectionUpdatedHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.events.NodeClickEvent;
import com.smartgwt.client.widgets.tree.events.NodeClickHandler;

import edu.mayo.cdisc.tree.client.datasources.BridgDatasource;
import edu.mayo.cdisc.tree.client.datasources.SelectedComponentDataSource;
import edu.mayo.cdisc.tree.client.test.BridgTree;
import edu.mayo.cdisc.tree.shared.SelectedComponents;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class CdiscBridgTree implements EntryPoint {
	
	private static final String BASE = "http://www.bridgmodel.org/owl#";
	
	private BridgTreeGrid i_treeGrid;
	private MetadataListGrid i_metadataListGrid;
	private SelectedComponentListGrid i_selectedComponentListGrid;
	private VLayout i_rightPanel;
	private VLayout i_detailsPanel;
	private Label i_detailsLabel;
	private VLayout i_selectedPanel;
	private Label i_selectedLabel;
	private Button i_saveButton;
	private Button i_mapButton;
	
	private TabSet i_tabSet;
	private Tab i_tabDetails;
	private Tab i_tabComponents;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		
		boolean debug = false;
				
		if (debug) {
			BridgTree tree = new BridgTree();
		}
		else {
		
		HLayout mainLayout = new HLayout();
		mainLayout.setWidth100();
		mainLayout.setHeight100();
		mainLayout.setLayoutMargin(10);
		mainLayout.setMembersMargin(5);
		
		// add the tree
		VLayout treeLayout = new VLayout(3);
		treeLayout.setWidth("50%");
		treeLayout.setHeight100();
		treeLayout.setOverflow(Overflow.VISIBLE);

		
		Label treeLabel = createLabel("Class Hiearchy");  
		//treeLabel.setWidth100();
		//treeLabel.setHeight(40);
		treeLayout.addMember(treeLabel);
		
//		HLayout buttonLayout = new HLayout();
//		buttonLayout.setWidth100();
//		buttonLayout.setHeight(25);
//		
//		Button clearButton = new Button("Clear Selection");
//		buttonLayout.addMember(clearButton);
//		
//		clearButton.addClickHandler(new ClickHandler() {
//			
//			@Override
//			public void onClick(ClickEvent event) {
//				// get the nodes that are selected
//				ListGridRecord[] selectedRecords = i_treeGrid.getSelectedRecords();
//				System.out.println("---- SELECTED RECORDS -------");
//				for(ListGridRecord record : selectedRecords ){
//
//					//record.setAttribute("isSelected", false);
//					System.out.println("Display Name = " + record.getAttribute(BridgDatasource.DISPLAY_NAME));
//					System.out.println("isSelected = " + record.getAttribute("isSelected"));
//				}
//			}
//		});
		
		
		i_treeGrid = new BridgTreeGrid(BridgDatasource.getInstance());
		i_treeGrid.setWidth100();
		i_treeGrid.setHeight100();
		//i_treeGrid.setAutoFitData(Autofit.BOTH);
		
		//i_treeGrid.getTree().setAttribute("reportCollisions", false, false);
		
		
//		i_treeGrid.addCellClickHandler(new CellClickHandler() {
//			
//			@Override
//			public void onCellClick(CellClickEvent event) {
//				System.out.println("onCellClick event = " + event.getSource().toString());
//				
//			}
//		});
//		
//		i_treeGrid.addClickHandler(new ClickHandler() {
//			
//			@Override
//			public void onClick(ClickEvent event) {
//				System.out.println("event = " + event.getSource().toString());
//				
//				Record record = i_treeGrid.getSelectedRecord();
//				
//				//event.getSource().
//				
//				RecordList recordList = i_treeGrid.getSelectedCellData();
//				System.out.println("recordList = " + recordList);
//				
//				System.out.println("user clicked on tree");
//			}
//		});
		
		i_treeGrid.addNodeClickHandler(new NodeClickHandler() {

			@Override
			public void onNodeClick(NodeClickEvent event) {
				String categoryParentId = event.getNode()
						.getAttribute("parent");

				Record record = i_treeGrid.getSelectedRecord();
				
				if (record == null) {
					return;
				}
				
				// DEBUG
				String[] testAttributes = record
						.getAttributes();
				for (String attr : testAttributes) {
					System.out.println(attr + " = "
							+ record.getAttribute(attr));
				}
				// DEBUG				
				
				String categoryName = record.getAttribute(BridgDatasource.NAME);
	
				// if association attribute not null, parse it
				// else use the display name
				String name = record.getAttribute(BridgDatasource.ASSOCIATION);
				if (name != null) {
					name = name.substring(BASE.length());
				}
				else {
					name = categoryName;
				}
					
				updateDetailsPanel(categoryParentId, name);
				
			
			}
		});
		
		
		//i_treeGrid.addSelectionChangedHandler(handler)
		
		i_treeGrid.addSelectionUpdatedHandler(new SelectionUpdatedHandler() {
			
			@Override
			public void onSelectionUpdated(SelectionUpdatedEvent event) {
				
				// get the nodes that are selected
				ListGridRecord[] selectedRecords = i_treeGrid.getSelectedRecords();
		
//				System.out.println("---- SELECTED RECORDS -------");
//				for(ListGridRecord record : selectedRecords ){
//					System.out.println("Display Name = " + record.getAttribute(BridgDatasource.DISPLAY_NAME));
//					System.out.println("Name =         " + record.getAttribute(BridgDatasource.NAME));
//					System.out.println("Association =  " + record.getAttribute(BridgDatasource.ASSOCIATION));
//					System.out.println();
//				}
//				System.out.println("--------------------------");
				
				i_selectedComponentListGrid.populateData(selectedRecords);

			}
		});
		
//		treeLayout.addMember(buttonLayout);
		treeLayout.addMember(i_treeGrid);
		
		
//		BridgTree tree = new BridgTree();
//		treeLayout.addMember(tree);
		
		
		mainLayout.addMember(treeLayout);

		// add the right panel
		i_rightPanel = new VLayout(5);
		i_rightPanel.setWidth("50%");
		i_rightPanel.setHeight100();
		
		i_detailsLabel = createLabel("Details");  
		//i_rightPanel.addMember(i_detailsLabel);
		
		i_detailsPanel = new VLayout();
		i_detailsPanel.setWidth100();
		i_detailsPanel.setHeight100();
		
		i_detailsPanel.addMember(i_detailsLabel);
		
		i_metadataListGrid = new MetadataListGrid();
		i_detailsPanel.addMember(i_metadataListGrid);
				
		// panel for showing selected components
		i_selectedPanel = new VLayout();
		i_selectedPanel.setWidth100();
		i_selectedPanel.setHeight100();
		i_selectedPanel.setBackgroundColor("#ccd2e4");
		
		i_selectedLabel = createLabel("Selected Components");  
		i_selectedPanel.addMember(i_selectedLabel);
		
		i_selectedComponentListGrid = new SelectedComponentListGrid();
		i_selectedPanel.addMember(i_selectedComponentListGrid);
		
		i_selectedPanel.addMember(getSelectedButtonPanel());
		
		// create tabs for the right side
		i_tabSet = new TabSet();  
		i_tabSet.setTabBarPosition(Side.TOP);  
		i_tabSet.setWidth100();
		i_tabSet.setHeight100();  
		 
		i_tabDetails = new Tab("Selected Details", "details.png");  
		i_tabDetails.setPane(i_detailsPanel); 
				 
		i_tabComponents = new Tab("Selected Components", "tree.png");  
		i_tabComponents.setPane(i_selectedPanel);   
		
		i_tabSet.addTab(i_tabComponents);
		i_tabSet.addTab(i_tabDetails);
		
		
		i_rightPanel.addMember(i_tabSet);
		mainLayout.addMember(i_rightPanel);
				
		RootPanel.get().add(mainLayout);
		
		GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
			  @Override 
			  public void onUncaughtException(Throwable e) {
			    System.out.println(e.getMessage()); 
			  }
			});

		}
		
	}
	
	private Canvas getSelectedButtonPanel() {
		
		HLayout buttonPanel = new HLayout(25);
		buttonPanel.setLayoutMargin(25);
		buttonPanel.setAlign(Alignment.CENTER);
		buttonPanel.setWidth100();
		buttonPanel.setHeight(50);
		
		i_saveButton = new Button("Save Selection");
		
		i_mapButton =  new Button("Generate Freemind Map");
		i_mapButton.setWidth(180);
		i_mapButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				final TitleWindow mapTitleWindow = new TitleWindow();
				
				
				mapTitleWindow.getSaveButton().addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						System.out.println("Generate MM and show window --  " + mapTitleWindow.getTemplateName());
						mapTitleWindow.hide();
						
						String templateName = mapTitleWindow.getTemplateName();
						generateMindMap(templateName);
					}

				});
				
				mapTitleWindow.show();
				
			}
		});

		buttonPanel.addMember(i_saveButton);
		buttonPanel.addMember(i_mapButton);
		
		return buttonPanel;
	}

	private void generateMindMap(final String templateName) {
				
		// get the selected components.
		ListGridRecord[] records = i_selectedComponentListGrid.getRecords();
		SelectedComponents[] selectedComponents = new SelectedComponents[records.length];
				
		for (int i = 0; i < selectedComponents.length; i++) {
			
			SelectedComponents selected = new SelectedComponents();
			selected.setConcept(records[i].getAttribute(SelectedComponentDataSource.CONCEPT) );
			selected.setVariable(records[i].getAttribute(SelectedComponentDataSource.NAME) );
			selected.setDescription("some description");
			
			selectedComponents[i] = selected;
		}
		
		//SC.say("Generating Map " + templateName);
				
		CdiscServiceAsync cdiscService = GWT.create(CdiscService.class);
		cdiscService.generateMindMap(selectedComponents, templateName, new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String htmlFileName) {
				
				//SC.say("Created html file " + htmlFileName);
				
				//System.out.println(htmlFileName);
				
				createMapWindow(templateName, htmlFileName);
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				SC.warn("Failed to create FreeMind Map " + caught.toString());
			}
		} );
		
	}
	
	private void updateDetailsPanel(String categoryParentId, final String categoryName){
		
		// if this is a Children/Attributes/InheritedAttributes node,
		// then don't query, just clear the table.
		if (categoryName.startsWith(NodeTemplate.NODE_CHILDREN + "_") || 
			categoryName.startsWith(NodeTemplate.NODE_ATTRIBUTES + "_") ||
			categoryName.startsWith(NodeTemplate.NODE_ASSOCIATIONS + "_"))
		{
			i_metadataListGrid.clearData();
			i_detailsLabel.setContents("<h3>Details</h3>");
		}
		
		else {
			CdiscServiceAsync cdiscService = GWT.create(CdiscService.class);
			cdiscService.getMetadata(categoryName,
					new AsyncCallback<String>() {
	
						public void onFailure(Throwable caught) {
							System.out.println("Server Error " + caught.toString());
						}
	
						public void onSuccess(String xmlResult) {
							System.out.println(xmlResult);
							
							i_metadataListGrid.populateData(xmlResult);
							i_detailsLabel.setContents("<h3>Details for <em>" + categoryName + "</em></h3>");
							
						}
					});
		}
	}

	private void createMapWindow(String templateName, String htmlFileName) {
		MapWindow mapWindow = new MapWindow(templateName, htmlFileName);
		mapWindow.show();
		
	}
	
	
	
	private Label createLabel(String title) {
		Label label = new Label("<h3>" + title + "</h3>");
		label.setBackgroundColor("#ccd2e4");
		label.setAlign(Alignment.CENTER);
		label.setWidth100();
		label.setHeight(20);
		
		return label;
	}
	
}
