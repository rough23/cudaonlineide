package cz.utb.fai.cudaonlineide.shared.dto;

import java.io.Serializable;

import cz.utb.fai.cudaonlineide.shared.constants.COIConstants;

/**
 * Parent class to File, Folder, Project, Workspace class.
 * 
 * @author Belanec
 *
 */
public class COIObject implements Serializable {

	/**
	 * Serial version of COIObject.
	 */
	private static final long serialVersionUID = 8897681489126363469L;
	/**
	 * Name of project.
	 */
	private String name;
	/**
	 * Path of project.
	 */
	private String path;
	/**
	 * Type of data (file, folder, project, workspace).
	 */
	private COIEnum typeOfCOI;
	
	public COIObject() {
		name = COIConstants.EMPTY;
		path = COIConstants.EMPTY;
		typeOfCOI = null;
	}
 	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public COIEnum getTypeOfCOI() {
		return typeOfCOI;
	}
	public void setTypeOfCOI(COIEnum typeOfCOI) {
		this.typeOfCOI = typeOfCOI;
	}
}
