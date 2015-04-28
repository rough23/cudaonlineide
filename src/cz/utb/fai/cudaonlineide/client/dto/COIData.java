package cz.utb.fai.cudaonlineide.client.dto;

import java.io.Serializable;

import cz.utb.fai.cudaonlineide.shared.dto.COIObject;

/**
 * Class represents project explorer tree object.
 * 
 * @author Belanec
 *
 */
public class COIData implements Serializable {

	private static final long serialVersionUID = -2283352568786016004L;
	private String name;
	private String path;
	private String text;
	private COIObject coiObject;

	public COIData(String name, String path, String text, COIObject coiObject) {
		super();
		this.setName(name);
		this.setPath(path);
		this.setText(text);
		this.setCoiObject(coiObject);
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

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public COIObject getCoiObject() {
		return coiObject;
	}

	public void setCoiObject(COIObject coiObject) {
		this.coiObject = coiObject;
	}

}
