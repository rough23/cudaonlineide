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
	
	/**
	 * Generate CU file with sample source code.
	 * 
	 * @param filename File name.
	 * 
	 * @return Predefined text for CU file with sample source code.
	 */
	public static String getCuSample(String filename) {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("/*").append(COIConstants.LINE_SEPARATOR);
		sb.append(" ============================================================================").append(COIConstants.LINE_SEPARATOR);
		sb.append(" Name        : ").append(filename).append(COIConstants.COMMA + COIConstants.EXTENSION_CU).append(COIConstants.LINE_SEPARATOR);
		sb.append(" Author      : ").append(COIConstants.LINE_SEPARATOR);
		sb.append(" Version     : ").append(COIConstants.LINE_SEPARATOR);
		sb.append(" Copyright   : ").append(COIConstants.LINE_SEPARATOR);
		sb.append(" Description : Sample CUDA project.").append(COIConstants.LINE_SEPARATOR);
		sb.append(" ============================================================================").append(COIConstants.LINE_SEPARATOR);
		sb.append("*/").append(COIConstants.LINE_SEPARATOR);
		sb.append(COIConstants.LINE_SEPARATOR);
		
		sb.append("#include <stdio.h>").append(COIConstants.LINE_SEPARATOR);
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
		
		sb.append("int main( void ) {").append(COIConstants.LINE_SEPARATOR);
		sb.append(COIConstants.LINE_SEPARATOR);

		sb.append("\tcudaDeviceProp prop;").append(COIConstants.LINE_SEPARATOR);
		sb.append("\tint count;").append(COIConstants.LINE_SEPARATOR);
		sb.append("\tCUDA_CHECK_RETURN( cudaGetDeviceCount( &count ) );").append(COIConstants.LINE_SEPARATOR);
		sb.append(COIConstants.LINE_SEPARATOR);
		
		sb.append("\tfor (int i = 0; i < count; i++) {").append(COIConstants.LINE_SEPARATOR);
		sb.append(COIConstants.LINE_SEPARATOR);
	        
		sb.append("\t\tCUDA_CHECK_RETURN( cudaGetDeviceProperties( &prop, i ) );").append(COIConstants.LINE_SEPARATOR);
		sb.append(COIConstants.LINE_SEPARATOR);
		
		sb.append("\t\tprintf( \"\\n\" );").append(COIConstants.LINE_SEPARATOR);
		sb.append("\t\tprintf( \"--- General Information for device %d ---\\n\", i );").append(COIConstants.LINE_SEPARATOR);
		sb.append("\t\tprintf( \" Name: %s\\n\", prop.name );").append(COIConstants.LINE_SEPARATOR);
		sb.append("\t\tprintf( \" Compute capability: %d.%d\\n\", prop.major, prop.minor );").append(COIConstants.LINE_SEPARATOR);
		sb.append("\t\tprintf( \" Clock rate: %d\\n\", prop.clockRate );").append(COIConstants.LINE_SEPARATOR);
		sb.append(COIConstants.LINE_SEPARATOR);
		
		sb.append("\t\tprintf( \" Device copy overlap: \" );").append(COIConstants.LINE_SEPARATOR);
		sb.append("\t\tif (prop.deviceOverlap) {").append(COIConstants.LINE_SEPARATOR);
		sb.append("\t\t\tprintf( \"Enabled\\n\" );").append(COIConstants.LINE_SEPARATOR);
		sb.append("\t\t} else {").append(COIConstants.LINE_SEPARATOR);
		sb.append("\t\t\tprintf( \"Disabled\\n\" );").append(COIConstants.LINE_SEPARATOR);
		sb.append("\t\t}").append(COIConstants.LINE_SEPARATOR);
		sb.append(COIConstants.LINE_SEPARATOR);
		
		sb.append("\t\tprintf( \" Kernel execition timeout: \" );").append(COIConstants.LINE_SEPARATOR);
		sb.append("\t\tif (prop.kernelExecTimeoutEnabled) {").append(COIConstants.LINE_SEPARATOR);
		sb.append("\t\t\tprintf( \"Enabled\\n\" );").append(COIConstants.LINE_SEPARATOR);
		sb.append("\t\t} else {").append(COIConstants.LINE_SEPARATOR);
		sb.append("\t\t\tprintf( \"Disabled\\n\" );").append(COIConstants.LINE_SEPARATOR);
		sb.append("\t\t}").append(COIConstants.LINE_SEPARATOR);
		sb.append(COIConstants.LINE_SEPARATOR);    
		
		sb.append("\t\tprintf( \"\\n\" );").append(COIConstants.LINE_SEPARATOR);
		sb.append("\t\tprintf( \"--- Memory Information for device %d ---\\n\", i );").append(COIConstants.LINE_SEPARATOR);
		sb.append("\t\tprintf( \" Total global memory: %ld\\n\", prop.totalGlobalMem );").append(COIConstants.LINE_SEPARATOR);
		sb.append("\t\tprintf( \" Total constant memory: %ld\\n\", prop.totalConstMem );").append(COIConstants.LINE_SEPARATOR);
		sb.append("\t\tprintf( \" Max memory pitch: %ld\\n\", prop.memPitch );").append(COIConstants.LINE_SEPARATOR);
		sb.append("\t\tprintf( \" Texture alignment: %ld\\n\", prop.textureAlignment );").append(COIConstants.LINE_SEPARATOR);
		sb.append(COIConstants.LINE_SEPARATOR);
		
		sb.append("\t\tprintf( \"\\n\" );").append(COIConstants.LINE_SEPARATOR);
		sb.append("\t\tprintf( \"--- MP Information for device %d ---\\n\", i );").append(COIConstants.LINE_SEPARATOR);
		sb.append("\t\tprintf( \" Multiprocessor count: %d\\n\", prop.multiProcessorCount );").append(COIConstants.LINE_SEPARATOR);
		sb.append("\t\tprintf( \" Shared memory per multiprocessor: %ld\\n\", prop.sharedMemPerBlock );").append(COIConstants.LINE_SEPARATOR);
		sb.append("\t\tprintf( \" Registers per multiprocessor: %d\\n\", prop.regsPerBlock );").append(COIConstants.LINE_SEPARATOR);
		sb.append("\t\tprintf( \" Threads in warp: %d\\n\", prop.warpSize );").append(COIConstants.LINE_SEPARATOR);
		sb.append("\t\tprintf( \" Max threads per block: %d\\n\", prop.maxThreadsPerBlock );").append(COIConstants.LINE_SEPARATOR);
		sb.append("\t\tprintf( \" Max thread dimensions: (%d, %d, %d)\\n\", prop.maxThreadsDim[0], prop.maxThreadsDim[1], prop.maxThreadsDim[2] );").append(COIConstants.LINE_SEPARATOR);
		sb.append("\t\tprintf( \" Max grid dimensions: (%d, %d, %d)\\n\", prop.maxGridSize[0], prop.maxGridSize[1], prop.maxGridSize[2] );").append(COIConstants.LINE_SEPARATOR);
		sb.append("\t}").append(COIConstants.LINE_SEPARATOR);
		sb.append(COIConstants.LINE_SEPARATOR);
		
		sb.append("\tCUDA_CHECK_RETURN( cudaDeviceReset() ) ;").append(COIConstants.LINE_SEPARATOR);
		sb.append(COIConstants.LINE_SEPARATOR);
		
		sb.append("\treturn 0;").append(COIConstants.LINE_SEPARATOR);
		sb.append("}").append(COIConstants.LINE_SEPARATOR);
		sb.append(COIConstants.LINE_SEPARATOR);

		return sb.toString();
	}
}
