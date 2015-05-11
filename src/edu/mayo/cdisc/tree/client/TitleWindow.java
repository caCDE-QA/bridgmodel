package edu.mayo.cdisc.tree.client;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.smartgwt.client.widgets.layout.HLayout;

public class TitleWindow extends Window {

private TextItem i_textItem;
private Button i_saveButton; 
	
	public TitleWindow() {
		super();
		
		setWidth(400);  
        setHeight(150);  
        setTitle("Template Name Window");  
        setShowMinimizeButton(false);  
        setIsModal(true);  
        setShowModalMask(true);  
        centerInPage();  
        
        DynamicForm form = new DynamicForm();  
        form.setHeight100();  
        form.setWidth100();  
        form.setPadding(5);  
        form.setLayoutAlign(VerticalAlignment.BOTTOM);  
        i_textItem = new TextItem();  
        i_textItem.setTitle("Template Name");  
        i_textItem.setWidth(250);
        
        form.setFields(i_textItem);  
        
        HLayout buttonPanel = new HLayout(25);
		buttonPanel.setLayoutMargin(10);
		buttonPanel.setAlign(Alignment.CENTER);
		buttonPanel.setWidth100();
		buttonPanel.setHeight(30);
		
		i_saveButton = new Button("Generate FreeMind Map");
		i_saveButton.setWidth(150);
		
		buttonPanel.addMember(i_saveButton);
		
//		i_textItem.addKeyPressHandler(new KeyPressHandler() {
//			
//			@Override
//			public void onKeyPress(KeyPressEvent event) {
//				if (i_textItem.getValueAsString().length() > 0) {
//					saveButton.setDisabled(false);
//				}
//				else {
//					saveButton.setDisabled(false);
//				}
//			}
//		});

		
		addItem(form);
		addItem(buttonPanel);
        
	}
	
	public String getTemplateName() {
		return i_textItem.getValueAsString();
	}

	public Button getSaveButton(){
		return i_saveButton;
	}
	
}
