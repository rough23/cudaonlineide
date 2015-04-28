package cz.utb.fai.cudaonlineide.shared.dto.project;

import java.io.Serializable;

import cz.utb.fai.cudaonlineide.shared.constants.COIConstants;

/**
 * BuildConfiguration serializable class.
 * 
 * @author Belanec
 *
 */
public class COIBuildConfiguration implements Serializable {

	/**
	 * Serial version of COIBuildConfiguration.
	 */
	private static final long serialVersionUID = 8103399056295618683L;
	
	private String active;
	private COIConfiguration debug;
	private COIConfiguration release;
	private COIConfiguration custom;
	
	/**
	 * Default constructor.
	 */
	public COIBuildConfiguration() {
		active = COIConstants.BUILD_CONFIGURATION_DEBUG;
		debug = new COIConfiguration(COIConstants.BUILD_CONFIGURATION_DEBUG);
		release = new COIConfiguration(COIConstants.BUILD_CONFIGURATION_RELEASE);
		custom = new COIConfiguration(COIConstants.BUILD_CONFIGURATION_CUSTOM);
	}
	
	/**
	 * Constructor with setting default build configuration.
	 * 
	 * @param defaultConfiguration Build configuration.
	 */
	public COIBuildConfiguration(String defaultConfiguration) {
		active = defaultConfiguration;
		debug = new COIConfiguration(COIConstants.BUILD_CONFIGURATION_DEBUG);
		release = new COIConfiguration(COIConstants.BUILD_CONFIGURATION_RELEASE);
		custom = new COIConfiguration(COIConstants.BUILD_CONFIGURATION_CUSTOM);
	}
	
	public String getActive() {
		return active;
	}
	public void setActive(String active) {
		this.active = active;
	}
	public COIConfiguration getDebug() {
		return debug;
	}
	public void setDebug(COIConfiguration debug) {
		this.debug = debug;
	}
	public COIConfiguration getRelease() {
		return release;
	}
	public void setRelease(COIConfiguration release) {
		this.release = release;
	}
	public COIConfiguration getCustom() {
		return custom;
	}
	public void setCustom(COIConfiguration custom) {
		this.custom = custom;
	}
}