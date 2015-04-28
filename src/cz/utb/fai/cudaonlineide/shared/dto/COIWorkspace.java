package cz.utb.fai.cudaonlineide.shared.dto;

import java.util.ArrayList;
import java.util.List;

import cz.utb.fai.cudaonlineide.shared.constants.COIConstants;

/**
 * Class represents workspace.
 * 
 * @author Belanec
 *
 */
public class COIWorkspace extends COIObject {

	/**
	 * Serial version of COIWorkspace.
	 */
	private static final long serialVersionUID = 8897681489126363470L;
	/**
	 * CudaOnlineIDE workspace version.
	 */
	private String cwsVersion;
	/**
	 * List of CudaOnlineIDE folders.
	 */
	private List<COIProject> items;
	
	public COIWorkspace() {
		super();
		cwsVersion = COIConstants.EMPTY;
		items = new ArrayList<>();
	}
	
	// GETTERS AND SETTERS 
	public List<COIProject> getItems() {
		return items;
	}
	public void setItems(List<COIProject> items) {
		this.items = items;
	}
	public String getCwsVersion() {
		return cwsVersion;
	}
	public void setCwsVersion(String cwsVersion) {
		this.cwsVersion = cwsVersion;
	}
}
