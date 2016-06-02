package org.benetech.authenticon.api.encoders.iconmap;

public class IconMap10GroupsHandler extends AbstractIconMapHandler {

	private static final int ICON_MAPPING_TYPE_GROUP_COUNT = 10;
	
	public IconMap10GroupsHandler() {
	}

	@Override
	protected int getMappingCount() {
		return ICON_MAPPING_TYPE_GROUP_COUNT;
	}
}
