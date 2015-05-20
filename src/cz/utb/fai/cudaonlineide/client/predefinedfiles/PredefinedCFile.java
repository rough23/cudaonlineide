package cz.utb.fai.cudaonlineide.client.predefinedfiles;

import cz.utb.fai.cudaonlineide.shared.constants.COIConstants;

/**
 * This class provides method to generate text for new C files.
 * 
 * @author Belanec
 *
 */
public class PredefinedCFile {

	/**
	 * Generate blank C file.
	 * 
	 * @param filename File name.
	 * 
	 * @return Predefined text for blank C file.
	 */
	public static String getCBlank(String filename) {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("/*").append(COIConstants.LINE_SEPARATOR);
		sb.append(" ============================================================================").append(COIConstants.LINE_SEPARATOR);
		sb.append(" Name        : ").append(filename).append(COIConstants.COMMA + COIConstants.EXTENSION_C).append(COIConstants.LINE_SEPARATOR);
		sb.append(" Author      : ").append(COIConstants.LINE_SEPARATOR);
		sb.append(" Version     : ").append(COIConstants.LINE_SEPARATOR);
		sb.append(" Copyright   : ").append(COIConstants.LINE_SEPARATOR);
		sb.append(" Description : ").append(COIConstants.LINE_SEPARATOR);
		sb.append(" ============================================================================").append(COIConstants.LINE_SEPARATOR);
		sb.append("*/").append(COIConstants.LINE_SEPARATOR);
		sb.append(COIConstants.LINE_SEPARATOR);

		return sb.toString();
	}
	
	/**
	 * Generate C file with main method.
	 * 
	 * @param filename File name.
	 * 
	 * @return Predefined text for C file with main method.
	 */
	public static String getCMain(String filename) {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("/*").append(COIConstants.LINE_SEPARATOR);
		sb.append(" ============================================================================").append(COIConstants.LINE_SEPARATOR);
		sb.append(" Name        : ").append(filename).append(COIConstants.COMMA + COIConstants.EXTENSION_C).append(COIConstants.LINE_SEPARATOR);
		sb.append(" Author      : ").append(COIConstants.LINE_SEPARATOR);
		sb.append(" Version     : ").append(COIConstants.LINE_SEPARATOR);
		sb.append(" Copyright   : ").append(COIConstants.LINE_SEPARATOR);
		sb.append(" Description : ").append(COIConstants.LINE_SEPARATOR);
		sb.append(" ============================================================================").append(COIConstants.LINE_SEPARATOR);
		sb.append("*/").append(COIConstants.LINE_SEPARATOR);
		sb.append(COIConstants.LINE_SEPARATOR);
		
		sb.append("#include <stdio.h>").append(COIConstants.LINE_SEPARATOR);
		sb.append("#include <stdlib.h>").append(COIConstants.LINE_SEPARATOR);
		sb.append(COIConstants.LINE_SEPARATOR);
		
		sb.append("int main(void) {").append(COIConstants.LINE_SEPARATOR);
		sb.append(COIConstants.LINE_SEPARATOR);
		sb.append("\t// Please, insert your main code here.").append(COIConstants.LINE_SEPARATOR);
		sb.append(COIConstants.LINE_SEPARATOR);
		sb.append("\treturn 0;").append(COIConstants.LINE_SEPARATOR);
		sb.append("}").append(COIConstants.LINE_SEPARATOR);
		sb.append(COIConstants.LINE_SEPARATOR);

		return sb.toString();
	}
}
