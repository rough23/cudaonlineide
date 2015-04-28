package cz.utb.fai.cudaonlineide.shared.dto.project;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cz.utb.fai.cudaonlineide.shared.constants.COIConstants;

/**
 * Compiler settings serializable class.
 * 
 * @author Belanec
 *
 */
public class COICompiler implements Serializable {

	/**
	 *  Serial version of COICompiler.
	 */
	private static final long serialVersionUID = -4841552785425816532L;

	private List<String> options;
	private List<String> includeDirectories;
	private List<String> preprocessors;
	
	/**
	 * Default constructor.
	 */
	public COICompiler() {
		options = new ArrayList<>();
		includeDirectories = new ArrayList<>();
		preprocessors = new ArrayList<>();
	}

	public List<String> getOptions() {
		return options;
	}
	public void setOptions(List<String> options) {
		this.options = options;
	}
	public List<String> getIncludeDirectories() {
		return includeDirectories;
	}
	public void setIncludeDirectories(List<String> includeDirectories) {
		this.includeDirectories = includeDirectories;
	}
	public List<String> getPreprocessors() {
		return preprocessors;
	}
	public void setPreprocessors(List<String> preprocessors) {
		this.preprocessors = preprocessors;
	}
	
	public String getOptionsText() {
		
		if(options == null || options.isEmpty()) {
			return COIConstants.EMPTY;
		}
		
		StringBuilder optionsTextBuilder = new StringBuilder();
		
		for(int i = 0; i < options.size(); i++) {
			
			optionsTextBuilder.append(options.get(i));
			
			if(i != (options.size() - 1)) {
				optionsTextBuilder.append(COIConstants.SPACE);
			}
		}
		
		return optionsTextBuilder.toString();
	}
	
	public String getIncludeDirectoriesText() {
		
		if(includeDirectories == null || includeDirectories.isEmpty()) {
			return COIConstants.EMPTY;
		}
		
		StringBuilder includeDirectoriesTextBuilder = new StringBuilder();
		
		for(int i = 0; i < includeDirectories.size(); i++) {
			
			includeDirectoriesTextBuilder.append(includeDirectories.get(i));
			
			if(i != (includeDirectories.size() - 1)) {
				includeDirectoriesTextBuilder.append(COIConstants.LINE_SEPARATOR);
			}
		}
		
		return includeDirectoriesTextBuilder.toString();
	}
	
	public String getPreprocessorsText() {
		
		if(preprocessors == null || preprocessors.isEmpty()) {
			return COIConstants.EMPTY;
		}
		
		StringBuilder preprocessorsTextBuilder = new StringBuilder();
		
		for(int i = 0; i < preprocessors.size(); i++) {
			
			preprocessorsTextBuilder.append(preprocessors.get(i));
			
			if(i != (preprocessors.size() - 1)) {
				preprocessorsTextBuilder.append(COIConstants.LINE_SEPARATOR);
			}
		}
		
		return preprocessorsTextBuilder.toString();
	}
}
