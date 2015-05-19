package cz.utb.fai.cudaonlineide.shared.dto.project;

import java.io.Serializable;

/**
 * Global setting serializable class.
 * 
 * @author Belanec
 *
 */
public class COIGlobal implements Serializable {
	
	/**
	 * Serial version of COIGlobal.
	 */
	private static final long serialVersionUID = 8553729581110213033L;
	
	private String ccPtx;
	private String ccGpu;
	private String arguments;
	
	public COIGlobal() {
		ccPtx = "3.5";
		ccGpu = "3.5";
		arguments = "";
	}
	
	public String getCcPtx() {
		return ccPtx;
	}
	public void setCcPtx(String ccPtx) {
		this.ccPtx = ccPtx;
	}
	public String getCcGpu() {
		return ccGpu;
	}
	public void setCcGpu(String ccGpu) {
		this.ccGpu = ccGpu;
	}
	public String getArguments() {
		return arguments;
	}
	public void setArguments(String arguments) {
		this.arguments = arguments;
	}
	
}
