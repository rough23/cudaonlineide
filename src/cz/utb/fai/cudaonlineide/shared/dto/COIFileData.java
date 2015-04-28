package cz.utb.fai.cudaonlineide.shared.dto;

import java.io.Serializable;

/**
 * Class represents output tags from file.
 * 
 * @author Belanec
 *
 */
public class COIFileData implements Serializable {

	private static final long serialVersionUID = 2662990091306010335L;
	private String name;
	private String file;
	private String kind;
	private Integer line;
	private String[] parent;
	private String parents;
	private String access;
	private String implementation;
	private String inherits;
	private String signature;
	private String output;

	public COIFileData() {
		this.name = "";
		this.file = "";
		this.kind = "";
		this.line = -1;
		this.parent = new String[2];
		this.parents = "";
		this.access = "";
		this.implementation = "";
		this.inherits = "";
		this.signature = "";
		this.output = "";
	}

	public COIFileData(String name, String file, String kind, Integer line,
			String[] parent, String parents, String access,
			String implementation, String inherits, String signature,
			String output) {
		this.name = name;
		this.file = file;
		this.kind = kind;
		this.line = line;
		this.parent = parent;
		this.parents = parents;
		this.access = access;
		this.implementation = implementation;
		this.inherits = inherits;
		this.signature = signature;
		this.output = output;
	}

	public String getName() {
		return name;
	}

	public String getFile() {
		return file;
	}

	public String getKind() {
		return kind;
	}

	public Integer getLine() {
		return line;
	}

	public String[] getParent() {
		return parent;
	}

	public String getParents() {
		return parents;
	}

	public String getAccess() {
		return access;
	}

	public String getImplementation() {
		return implementation;
	}

	public String getInherits() {
		return inherits;
	}

	public String getSignature() {
		return signature;
	}

	public String getOutput() {
		return output;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public void setLine(Integer line) {
		this.line = line;
	}

	public void setParent(String[] parent) {
		this.parent = parent;
	}

	public void setParents(String parents) {
		this.parents = parents;
	}

	public void setAccess(String access) {
		this.access = access;
	}

	public void setImplementation(String implementation) {
		this.implementation = implementation;
	}

	public void setInherits(String inherits) {
		this.inherits = inherits;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public void setOutput(String output) {
		this.output = output;
	}
}
