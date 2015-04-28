package cz.utb.fai.cudaonlineide.shared.dto.project;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cz.utb.fai.cudaonlineide.shared.constants.COIConstants;

/**
 * Linker setting serializable class.
 * 
 * @author Belanec
 *
 */
public class COILinker implements Serializable {

	/**
	 * Serial version of COILinker.
	 */
	private static final long serialVersionUID = 9049037749434528987L;
	
	private List<String> options;
	private List<String> libraryPaths;
	private List<String> libraryNames;
	
	public COILinker() {
		options = new ArrayList<>();
		libraryPaths = new ArrayList<>();
		libraryNames = new ArrayList<>();
	}

	public List<String> getOptions() {
		return options;
	}
	public void setOptions(List<String> options) {
		this.options = options;
	}
	public List<String> getLibraryPaths() {
		return libraryPaths;
	}
	public void setLibraryPaths(List<String> libraryPaths) {
		this.libraryPaths = libraryPaths;
	}
	public List<String> getLibraryNames() {
		return libraryNames;
	}
	public void setLibraryNames(List<String> libraryNames) {
		this.libraryNames = libraryNames;
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
	
	public String getLibraryPathsText() {
		
		if(libraryPaths == null || libraryPaths.isEmpty()) {
			return COIConstants.EMPTY;
		}
		
		StringBuilder libraryPathsTextBuilder = new StringBuilder();
		
		for(int i = 0; i < libraryPaths.size(); i++) {
			
			libraryPathsTextBuilder.append(libraryPaths.get(i));
			
			if(i != (libraryPaths.size() - 1)) {
				libraryPathsTextBuilder.append(COIConstants.LINE_SEPARATOR);
			}
		}
		
		return libraryPathsTextBuilder.toString();
	}
	
	public String getLibraryNamesText() {
		
		if(libraryNames == null || libraryNames.isEmpty()) {
			return COIConstants.EMPTY;
		}
		
		StringBuilder libraryNamesTextBuilder = new StringBuilder();
		
		for(int i = 0; i < libraryNames.size(); i++) {
			
			libraryNamesTextBuilder.append(libraryNames.get(i));
			
			if(i != (libraryNames.size() - 1)) {
				libraryNamesTextBuilder.append(COIConstants.LINE_SEPARATOR);
			}
		}
		
		return libraryNamesTextBuilder.toString();
	}
}
