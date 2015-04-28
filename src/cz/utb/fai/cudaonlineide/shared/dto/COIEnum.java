package cz.utb.fai.cudaonlineide.shared.dto;

/**
 * Workspace object enum types.
 * 
 * @author Belanec
 *
 */
public enum COIEnum {

	FILE, FOLDER, PROJECT, WORKSPACE;
	
	/**
	 * Method get enum from string.
	 * 
	 * @param enumString String to enum.
	 * @return Enum from string.
	 */
	public COIEnum getEnumFromString(String enumString) {
		
		if(enumString == null || enumString.isEmpty()) {
			return null;
		}
		
		switch(enumString) {
			case "FILE" : return FILE;
			case "FOLDER" : return FOLDER;
			case "PROJECT" : return PROJECT;
			case "WORKSPACE" : return WORKSPACE;
		}
		
		return null;
	}
	
	/**
	 * Method get string from enum.
	 * 
	 * @param enumWorkspace Enum to string.
	 * @return String from enum.
	 */
	public String getStringFromEnum(COIEnum enumWorkspace) {
		
		if(enumWorkspace == null) {
			return null;
		}
		
		switch(enumWorkspace) {
			case FILE : return "FILE";
			case FOLDER : return "FOLDER";
			case PROJECT : return "PROJECT";
			case WORKSPACE : return "WORKSPACE";
		}
		
		return null;
	}
}
