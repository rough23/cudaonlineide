package cz.utb.fai.cudaonlineide.shared.dto.project;

import java.io.Serializable;

import cz.utb.fai.cudaonlineide.shared.constants.COIConstants;

/**
 * Project configuration serializable class.
 * 
 * @author Belanec
 *
 */
public class COIConfiguration implements Serializable {

	/**
	 * Serial version of COIConfiguration.
	 */
	private static final long serialVersionUID = -1751728408498120587L;

	private String type;
	private COICompiler compiler;
	private COILinker linker;
	
	/**
	 * Default constructor.
	 */
	public COIConfiguration() {
		type = COIConstants.EMPTY;
		compiler = new COICompiler();
		linker = new COILinker();
	}
	
	/**
	 * Constructor with setting default type of configuration.
	 * 
	 * @param typeOfConfiguration Type of configuration.
	 */
	public COIConfiguration(String typeOfConfiguration) {
		
		compiler = new COICompiler();
		linker = new COILinker();
		
		if(typeOfConfiguration.equals(COIConstants.BUILD_CONFIGURATION_DEBUG)) {
			type = COIConstants.BUILD_CONFIGURATION_DEBUG;
			compiler.setOptions(COIConstants.BUILD_CONFIGURATION_COMPILER_OPTIONS_DEBUG);
			linker.setOptions(COIConstants.BUILD_CONFIGURATION_LINKER_OPTIONS);
			
		} else if(typeOfConfiguration.equals(COIConstants.BUILD_CONFIGURATION_RELEASE)) {
			type = COIConstants.BUILD_CONFIGURATION_RELEASE;
			compiler.setOptions(COIConstants.BUILD_CONFIGURATION_COMPILER_OPTIONS_RELEASE);
			linker.setOptions(COIConstants.BUILD_CONFIGURATION_LINKER_OPTIONS);
		} else {
			type = COIConstants.BUILD_CONFIGURATION_CUSTOM;
		}
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}	
	public COICompiler getCompiler() {
		return compiler;
	}
	public void setCompiler(COICompiler compiler) {
		this.compiler = compiler;
	}
	public COILinker getLinker() {
		return linker;
	}
	public void setLinker(COILinker linker) {
		this.linker = linker;
	}
}
