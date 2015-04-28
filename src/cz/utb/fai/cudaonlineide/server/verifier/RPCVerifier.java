package cz.utb.fai.cudaonlineide.server.verifier;

import java.io.File;

import cz.utb.fai.cudaonlineide.shared.dto.COIEnum;
import cz.utb.fai.cudaonlineide.shared.dto.COIFile;
import cz.utb.fai.cudaonlineide.shared.dto.COIFolder;
import cz.utb.fai.cudaonlineide.shared.dto.COIObject;
import cz.utb.fai.cudaonlineide.shared.dto.COIProject;
import cz.utb.fai.cudaonlineide.shared.dto.COIWorkspace;

/**
 * Verifier class to test COI objects that are sent from the client to the server.
 * 
 * @author m_belanec
 */
public class RPCVerifier {
	
	/**
	 * Method tests if string value is empty or not.
	 * 
	 * @param tString String to test.
	 * @return boolean Boolean value (True/False).
	 */
	public static boolean isStringWithValue(String tString) {
		
		if(tString == null || tString.isEmpty()) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Method tests if COIObject is initialized and have all required attributes.
	 * 
	 * @param coiObject Object to test.
	 * @return boolean Boolean value (True/False).
	 */
	public static boolean isInitialized(COIObject coiObject) {
		
		if(coiObject == null) {
			return false;
		}
		
		return (coiObject.getName() != null && coiObject.getPath() != null);
	}
	
	/**
	 * Method test if file path is correct and verified.
	 * 
	 * @param filePath Path to file.
	 * @return boolean Boolean value (True/False).
	 */
	public static boolean isValidFilePath(String filePath) {
		
		if(filePath == null || filePath.isEmpty()) {
			return false;
		}
		
		File testFile = new File(filePath);	
		return (testFile != null && testFile.isFile());
	}
	
	/**
	 * Method test if folder path is correct and verified.
	 * 
	 * @param folderPath Path to folder.
	 * @return boolean Boolean value (True/False).
	 */
	public static boolean isValidFolderPath(String folderPath) {
		
		if(folderPath == null || folderPath.isEmpty()) {
			return false;
		}
		
		File testFolder = new File(folderPath);	
		return (testFolder != null && testFolder.isDirectory());
	}
	
	/**
	 * Method test if new file has correct parameters.
	 * 
	 * @param coiFile File to save.
	 * @return boolean Boolean value (True/False).
	 */
	public static boolean isCOIFileCorrect(COIFile coiFile) {
		
		if(coiFile == null || coiFile.getPath() == null || coiFile.getText() == null || coiFile.getTypeOfCOI() == null) {
			return false;
		}
		
		return (coiFile.getTypeOfCOI() == COIEnum.FILE);
	}
	
	/**
	 * Method test if new folder has correct parameters.
	 * 
	 * @param coiFolder Folder to save.
	 * @return boolean Boolean value (True/False).
	 */
	public static boolean isCOIFolderCorrect(COIFolder coiFolder) {
		
		if(coiFolder == null || coiFolder.getPath() == null || coiFolder.getTypeOfCOI() == null) {
			return false;
		}
		
		return (coiFolder.getTypeOfCOI() == COIEnum.FOLDER);
	}
	
	/**
	 * Method test if new project has correct parameters.
	 * 
	 * @param coiProject Project to save.
	 * @return boolean Boolean value (True/False).
	 */
	public static boolean isCOIProjectCorrect(COIProject coiProject) {
		
		if(coiProject == null || coiProject.getPath() == null || coiProject.getTypeOfCOI() == null) {
			return false;
		}
		
		return (coiProject.getTypeOfCOI() == COIEnum.PROJECT);
	}
	
	/**
	 * Method test if new file has correct parameters.
	 * 
	 * @param coiWorkspace Workspace file.
	 * @return boolean Boolean value (True/False).
	 */
	public static boolean isCOIWorkspaceCorrect(COIWorkspace coiWorkspace) {
		
		if(coiWorkspace == null || coiWorkspace.getPath() == null || coiWorkspace.getName() == null || coiWorkspace.getTypeOfCOI() == null) {
			return false;
		}
		
		return (coiWorkspace.getTypeOfCOI() == COIEnum.WORKSPACE);
	}
}
