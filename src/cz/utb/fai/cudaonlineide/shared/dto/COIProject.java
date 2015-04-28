package cz.utb.fai.cudaonlineide.shared.dto;

import java.util.ArrayList;
import java.util.List;

import cz.utb.fai.cudaonlineide.shared.dto.project.COIBuildConfiguration;

/**
 * Class represents project of workspace.
 * 
 * @author Belanec
 *
 */
public class COIProject extends COIObject {

	/**
	 * Serial version of COIProject.
	 */
	private static final long serialVersionUID = 8897681489126363471L;
	/**
	 * Build configurations.
	 */
	private COIBuildConfiguration buildConfiguration;
	/**
	 * List of CudaOnlineIDE folders.
	 */
	private List<COIFolder> items;
	/**
	 * UUID of building.
	 */
	private String uuid;
	
	public COIProject() {
		super();
		buildConfiguration = new COIBuildConfiguration();
		items = new ArrayList<>();
	}
	
	// GETTERS AND SETTERS 
	public List<COIFolder> getItems() {
		return items;
	}
	public void setItems(List<COIFolder> items) {
		this.items = items;
	}
	public COIBuildConfiguration getBuildConfiguration() {
		return buildConfiguration;
	}
	public void setBuildConfiguration(COIBuildConfiguration buildConfiguration) {
		this.buildConfiguration = buildConfiguration;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

}
