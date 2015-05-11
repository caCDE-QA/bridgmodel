package edu.mayo.cdisc.tree.client;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;

public class MapWindow extends Window {

	public MapWindow(String templateName, String htmlFileName) {
		super();
		
		setWidth("90%");  
        setHeight("90%");  
        setTitle("FreeMind Map for " + templateName);  
        setShowMinimizeButton(false);  
        setIsModal(true);  
        setShowModalMask(true);  
        centerInPage();  
        
        final HTMLPane htmlPane = new HTMLPane();
        htmlPane.setWidth100();
        htmlPane.setHeight100();

        String contextPath = GWT.getHostPageBaseURL();
        
        //String fileLocation = contextPath + "data/maps/freemindbrowser.html";
        String fileLocation = contextPath + "data/maps/" + htmlFileName;
        
        htmlPane.setContentsURL(fileLocation);
        
        System.out.println("contextPath = " + contextPath);
        
        addItem(htmlPane);
        
        addCloseClickHandler(new CloseClickHandler() {
			
			@Override
			public void onCloseClick(CloseClickEvent event) {
				htmlPane.destroy();
				destroy();
			}
		});
	}

}
