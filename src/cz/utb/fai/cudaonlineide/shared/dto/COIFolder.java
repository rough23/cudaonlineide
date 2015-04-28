package cz.utb.fai.cudaonlineide.shared.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Class represents folder of workspace.
 * 
 * @author Belanec
 *
 */
public class COIFolder extends COIObject {

	/**
	 * Serial version of COIFolder.
	 */
	private static final long serialVersionUID = 8897681489126363472L;
	/**
	 * List of CudaOnlineIDE files.
	 */
	private List<COIFile> items;
	
	public COIFolder() {
		super();
		items = new ArrayList<>();
	}
	
	// GETTERS AND SETTERS 
	public List<COIFile> getItems() {
		return items;
	}
	public void setItems(List<COIFile> items) {
		this.items = items;
	}
}
