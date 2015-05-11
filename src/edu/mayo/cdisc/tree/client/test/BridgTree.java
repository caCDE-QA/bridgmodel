package edu.mayo.cdisc.tree.client.test;

import com.smartgwt.client.widgets.tree.TreeGrid;  
import com.smartgwt.client.widgets.tree.TreeGridField;  
import com.smartgwt.client.widgets.tree.events.DataArrivedEvent;  
import com.smartgwt.client.widgets.tree.events.DataArrivedHandler;  
public class BridgTree {

	private String idSuffix = "";
	
	public BridgTree() {
		super();

		
        
        final TreeGrid employeeTree = new TreeGrid();  
        employeeTree.setLoadDataOnDemand(false);          
        employeeTree.setWidth(500);  
        employeeTree.setHeight100();  
        employeeTree.setDataSource(EmployeeXmlDS.getInstance());  
        employeeTree.setAutoFetchData(true);  
        employeeTree.setShowConnectors(true);         
        //employeeTree.setNodeIcon("icons/16/person.png");  
        //employeeTree.setFolderIcon("icons/16/person.png");  
        //employeeTree.setShowOpenIcons(false);  
        //employeeTree.setShowDropIcons(false);  
        //employeeTree.setClosedIconSuffix("");       
        employeeTree.setBaseStyle("noBorderCell");  
        employeeTree.setFields(new TreeGridField("Name"));  
  
        employeeTree.addDataArrivedHandler(new DataArrivedHandler() {  
            public void onDataArrived(DataArrivedEvent event) {  
                employeeTree.getData().openAll();  
            }  
        });  
          
        employeeTree.draw();  
    }  

}
