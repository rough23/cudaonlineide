package cz.utb.fai.cudaonlineide.client.predefinedfiles;

import cz.utb.fai.cudaonlineide.shared.constants.COIConstants;

/**
 * This class provides method to generate text for new CPP files.
 * 
 * @author Belanec
 *
 */
public class PredefinedCppFile {

	/**
	 * Generate blank CPP file.
	 * 
	 * @param filename File name.
	 * 
	 * @return Predefined text for blank CPP file.
	 */
	public static String getCppBlank(String filename) {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("//============================================================================").append(COIConstants.LINE_SEPARATOR);
		sb.append("// Name        : ").append(filename).append(COIConstants.COMMA + COIConstants.EXTENSION_CPP).append(COIConstants.LINE_SEPARATOR);
		sb.append("// Author      : ").append(COIConstants.LINE_SEPARATOR);
		sb.append("// Version     : ").append(COIConstants.LINE_SEPARATOR);
		sb.append("// Copyright   : ").append(COIConstants.LINE_SEPARATOR);
		sb.append("// Description : ").append(COIConstants.LINE_SEPARATOR);
		sb.append("//============================================================================").append(COIConstants.LINE_SEPARATOR);
		sb.append(COIConstants.LINE_SEPARATOR);

		return sb.toString();
	}
	
	/**
	 * Generate CPP file with main method.
	 * 
	 * @param filename File name.
	 * 
	 * @return Predefined text for CPP file with main method.
	 */
	public static String getCppMain(String filename) {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("//============================================================================").append(COIConstants.LINE_SEPARATOR);
		sb.append("// Name        : ").append(filename).append(COIConstants.COMMA + COIConstants.EXTENSION_CPP).append(COIConstants.LINE_SEPARATOR);
		sb.append("// Author      : ").append(COIConstants.LINE_SEPARATOR);
		sb.append("// Version     : ").append(COIConstants.LINE_SEPARATOR);
		sb.append("// Copyright   : ").append(COIConstants.LINE_SEPARATOR);
		sb.append("// Description : ").append(COIConstants.LINE_SEPARATOR);
		sb.append("//============================================================================").append(COIConstants.LINE_SEPARATOR);
		sb.append(COIConstants.LINE_SEPARATOR);
		
		sb.append("#include <stdio.h>").append(COIConstants.LINE_SEPARATOR);
		sb.append("#include <stdlib.h>").append(COIConstants.LINE_SEPARATOR);
		sb.append(COIConstants.LINE_SEPARATOR);
		
		sb.append("int main() {").append(COIConstants.LINE_SEPARATOR);
		sb.append(COIConstants.LINE_SEPARATOR);
		sb.append("\t// Please, insert your main code here.").append(COIConstants.LINE_SEPARATOR);
		sb.append(COIConstants.LINE_SEPARATOR);
		sb.append("\treturn 0;").append(COIConstants.LINE_SEPARATOR);
		sb.append("}").append(COIConstants.LINE_SEPARATOR);
		sb.append(COIConstants.LINE_SEPARATOR);

		return sb.toString();
	}
}
