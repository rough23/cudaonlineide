package cz.utb.fai.cudaonlineide.client.dto;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

import cz.utb.fai.cudaonlineide.shared.constants.COIConstants;
import cz.utb.fai.cudaonlineide.shared.dto.COIFileData;

/**
 * Data properties interface for output tree.
 * 
 * @author Belanec
 *
 */
public interface FileDataProperties extends PropertyAccess<COIFileData> {

	@Path(COIConstants.KEY_FILE_DATA_PROPERTIES)
	ModelKeyProvider<COIFileData> key();
	
	ValueProvider<COIFileData, String> name();
	ValueProvider<COIFileData, String> file();
	ValueProvider<COIFileData, String> kind();
	ValueProvider<COIFileData, Integer> line();
	ValueProvider<COIFileData, String> access();
	ValueProvider<COIFileData, String> implementation();
	ValueProvider<COIFileData, String> inherits();
	ValueProvider<COIFileData, String> signature();
	ValueProvider<COIFileData, String> output();
}
