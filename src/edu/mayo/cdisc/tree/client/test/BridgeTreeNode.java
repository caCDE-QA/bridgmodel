package edu.mayo.cdisc.tree.client.test;

import com.smartgwt.client.widgets.tree.TreeNode;

public class BridgeTreeNode extends TreeNode {

	public BridgeTreeNode(String name, String nodeID, String parentNodeID,
			String icon, boolean enabled, String idSuffix) {
		super();

		if (enabled) {
			setName(name);
		} else {
			setName("<span style='color:red'>" + name + "</span>");
		}

		setNodeID(nodeID.replace("-", "_") + idSuffix);
		//setThumbnail("thumbnails/" + nodeID.replace("-", "_") + ".gif");
		setParentNodeID(parentNodeID.replace("-", "_") + idSuffix);
		
		setIcon(icon);

		// if(nodeID.equals("featured-category") ||
		// nodeID.equals("new-category")) {
		// setIsOpen(true);
		// }
	}

	public void setNodeID(String value) {
		setAttribute("nodeID", value);
	}

	public void setThumbnail(String thumbnail) {
		setAttribute("thumbnail", thumbnail);
	}

	public void setParentNodeID(String value) {
		setAttribute("parentNodeID", value);
	}
}
