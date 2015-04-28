package cz.utb.fai.cudaonlineide.shared.dto;

import cz.utb.fai.cudaonlineide.shared.constants.COIConstants;

/**
 * Class represents file of workspace.
 * 
 * @author Belanec
 *
 */
public class COIFile extends COIObject {

	/**
	 * Serial version of COIFile.
	 */
	private static final long serialVersionUID = 8897681489126363473L;
	/**
	 * Text of data file
	 */
	private String text;
	/**
	 * Extension of data file.
	 */
	private String extension;
	
	public COIFile() {
		super();
		text = COIConstants.EMPTY;
		extension = COIConstants.EMPTY;
	}
	
	// GETTERS AND SETTERS 
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getExtension() {
		return extension;
	}
	public void setExtension(String extension) {
		this.extension = extension;
	}
}
