package cz.utb.fai.cudaonlineide.client.predefinedfiles;

import cz.utb.fai.cudaonlineide.shared.constants.COIConstants;

/**
 * This class provides method to generate text for new CU files.
 * 
 * @author Belanec
 *
 */
public class PredefinedCuFile {

	/**
	 * Generate blank CU file.
	 * 
	 * @param filename File name.
	 * 
	 * @return Predefined text for blank CU file.
	 */
	public static String getCuBlank(String filename) {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("/*").append(COIConstants.LINE_SEPARATOR);
		sb.append(" ============================================================================").append(COIConstants.LINE_SEPARATOR);
		sb.append(" Name        : ").append(filename).append(COIConstants.COMMA + COIConstants.EXTENSION_CU).append(COIConstants.LINE_SEPARATOR);
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
	 * Generate CU file with main method.
	 * 
	 * @param filename File name.
	 * 
	 * @return Predefined text for CU file with main method.
	 */
	public static String getCuMain(String filename) {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("/*").append(COIConstants.LINE_SEPARATOR);
		sb.append(" ============================================================================").append(COIConstants.LINE_SEPARATOR);
		sb.append(" Name        : ").append(filename).append(COIConstants.COMMA + COIConstants.EXTENSION_CU).append(COIConstants.LINE_SEPARATOR);
		sb.append(" Author      : ").append(COIConstants.LINE_SEPARATOR);
		sb.append(" Version     : ").append(COIConstants.LINE_SEPARATOR);
		sb.append(" Copyright   : ").append(COIConstants.LINE_SEPARATOR);
		sb.append(" Description : ").append(COIConstants.LINE_SEPARATOR);
		sb.append(" ============================================================================").append(COIConstants.LINE_SEPARATOR);
		sb.append("*/").append(COIConstants.LINE_SEPARATOR);
		sb.append(COIConstants.LINE_SEPARATOR);
		
		sb.append("#include <stdio.h>").append(COIConstants.LINE_SEPARATOR);
		sb.append("#include <stdlib.h>").append(COIConstants.LINE_SEPARATOR);
		sb.append("#include <cuda.h>").append(COIConstants.LINE_SEPARATOR);
		sb.append("#include <cuda_runtime.h>").append(COIConstants.LINE_SEPARATOR);
		sb.append(COIConstants.LINE_SEPARATOR);
		
		sb.append("#define CUDA_CHECK_RETURN(value) {											\\").append(COIConstants.LINE_SEPARATOR);
		sb.append("\tcudaError_t _m_cudaStat = value;										\\").append(COIConstants.LINE_SEPARATOR);
		sb.append("\tif (_m_cudaStat != cudaSuccess) {										\\").append(COIConstants.LINE_SEPARATOR);
		sb.append("\t\tfprintf(stderr, \"Error %s at line %d in file %s\\n\",					\\").append(COIConstants.LINE_SEPARATOR);
		sb.append("\t\t\t\tcudaGetErrorString(_m_cudaStat), __LINE__, __FILE__);		\\").append(COIConstants.LINE_SEPARATOR);
		sb.append("\t\texit(1);															\\").append(COIConstants.LINE_SEPARATOR);
		sb.append("\t} }").append(COIConstants.LINE_SEPARATOR);
		sb.append(COIConstants.LINE_SEPARATOR);
		
		sb.append("int main( int argc, char **argv ) {").append(COIConstants.LINE_SEPARATOR);
		sb.append(COIConstants.LINE_SEPARATOR);
		sb.append("\t// Please, insert your main code here.").append(COIConstants.LINE_SEPARATOR);
		sb.append(COIConstants.LINE_SEPARATOR);
		sb.append("\tCUDA_CHECK_RETURN( cudaDeviceReset() ) ;").append(COIConstants.LINE_SEPARATOR);
		sb.append(COIConstants.LINE_SEPARATOR);
		sb.append("\treturn 0;").append(COIConstants.LINE_SEPARATOR);
		sb.append("}").append(COIConstants.LINE_SEPARATOR);
		sb.append(COIConstants.LINE_SEPARATOR);

		return sb.toString();
	}
}
