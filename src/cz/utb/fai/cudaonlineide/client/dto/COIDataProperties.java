package cz.utb.fai.cudaonlineide.client.dto;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

import cz.utb.fai.cudaonlineide.shared.constants.COIConstants;
import cz.utb.fai.cudaonlineide.shared.dto.COIObject;

/**
 * Data properties interface for project explorer tree.
 * 
 * @author Belanec
 *
 */
public interface COIDataProperties extends PropertyAccess<COIData> {

	@Path(COIConstants.KEY_COI_DATA_PROPERTIES)
	ModelKeyProvider<COIData> key();
	
	ValueProvider<COIData, String> name();
	ValueProvider<COIData, String> path();
	ValueProvider<COIData, String> text();
	ValueProvider<COIData, COIObject> coiObject();
}
