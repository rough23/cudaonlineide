package cz.utb.fai.cudaonlineide.client.service.coi;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import cz.utb.fai.cudaonlineide.shared.dto.COIFile;
import cz.utb.fai.cudaonlineide.shared.dto.COIFolder;
import cz.utb.fai.cudaonlineide.shared.dto.COIObject;
import cz.utb.fai.cudaonlineide.shared.dto.COIProject;
import cz.utb.fai.cudaonlineide.shared.dto.COIWorkspace;
import cz.utb.fai.cudaonlineide.shared.dto.COIFileData;

/**
 * The client-side stub for the RPC COI service.
 * 
 * @author Belanec
 */
@RemoteServiceRelativePath("coi")
public interface COIService extends RemoteService {

	/**
	 * Method retrieving workspace by workspace path.
	 * 
	 * @param workspacePath Workspace path.
	 * @return Workspace file.
	 */
	COIWorkspace getCOIWorkspaceServiceMethod(String workspacePath);

	/**
	 * Method retrieving text from given file.
	 * 
	 * @param coiObject File without text.
	 * @return File with text
	 */
	COIObject addTextToCOIFileServiceMethod(COIObject coiObject);

	/**
	 * Method creating new file.
	 * 
	 * @param coiWorkspace Active workspace.
	 * @param coiFile File to create.
	 * @return True if file was created.
	 */
	boolean createNewFile(COIWorkspace coiWorkspace, COIFile coiFile);

	/**
	 * Method creating new folder.
	 * 
	 * @param coiWorkspace Active workspace.
	 * @param coiFolder Folder to create.
	 * @return True if folder was created.
	 */
	boolean createNewFolder(COIWorkspace coiWorkspace, COIFolder coiFolder);

	/**
	 * Method creating new project.
	 * 
	 * @param coiWorkspace Active workspace.
	 * @param coiProject Project to create.
	 * @return True if project was created.
	 */
	boolean createNewProject(COIWorkspace coiWorkspace, COIProject coiProject);

	/**
	 * Method creating new workspace.
	 * 
	 * @param coiWorkspace Workspace to create.
	 * @return True if workspace was created.
	 */
	boolean createNewWorkspace(COIWorkspace coiWorkspace);

	/**
	 * Method deleting object from workspace.
	 * 
	 * @param coiWorkspace Active workspace.
	 * @param coiObject Object to delete.
	 * @return True if object was deleted.
	 */
	boolean deleteFromWorkspace(COIWorkspace coiWorkspace, COIObject coiObject);

	/**
	 * Method deleting workspace.
	 * 
	 * @param workspacePath Path of workspace to delete.
	 * @return True if workspace was deleted.
	 */
	boolean deleteWorkspace(String workspacePath);

	/**
	 * Method gets text of given log file.
	 * 
	 * @param logFile Log file name.
	 * @return Text of log file.
	 */
	String getConnectionStatus(String logFile);

	/**
	 * Method updates workspace file.
	 * 
	 * @param coiWorkspace Active workspace to update.
	 */
	void updateWorkspace(COIWorkspace coiWorkspace);

	/**
	 * Method gets all user workspace files.
	 * 
	 * @param userWorkspacePath User work directory.
	 * @return Names of user workspace files.
	 */
	List<String> getUserWorkspaceFiles(String userWorkspacePath);

	/**
	 * Method updates workspace after upload folder.
	 * 
	 * @param coiWorkspace Active workspace.
	 * @param projectPath Active project path.
	 * @param serializedCoiFolder Serialized folder to upload.
	 * @return Array of upload objects.
	 */
	COIObject[] updateWorkspaceUploadFolder(COIWorkspace coiWorkspace,
			String projectPath, String serializedCoiFolder);

	/**
	 * Method updates workspace after upload project.
	 * 
	 * @param coiWorkspace Active workspace.
	 * @param workspacePath Active workspace path.
	 * @param serializedCoiProject Serialized project to upload.
	 * @return Array of upload objects.
	 */
	COIObject[] updateWorkspaceUploadProject(COIWorkspace coiWorkspace,
			String workspacePath, String serializedCoiProject);

	/**
	 * Method updates workspace after upload workspace.
	 * 
	 * @param serializedCoiWorkspace Serialized workspace to upload.
	 * @return Array of upload objects.
	 */
	COIWorkspace updateWorkspaceUploadWorkspace(String serializedCoiWorkspace);

	/**
	 * Method gets tags from given file.
	 * 
	 * @param filePath File path.
	 * @return List of tags.
	 */
	List<COIFileData> getTagsFromFile(String filePath);

	/**
	 * Method creates makefile.
	 * 
	 * @param coiWorkspace Active workspace.
	 * @param coiProject Active project.
	 * @return Creating progress.
	 */
	String createMakefile(COIWorkspace coiWorkspace, COIProject coiProject);

	/**
	 * Method builds active project.
	 * 
	 * @param coiWorkspace Active workspace.
	 * @param coiProject Active project.
	 * @return Building progress.
	 */
	String[] buildProject(COIWorkspace coiWorkspace, COIProject coiProject);

	/**
	 * Method executes active project.
	 * 
	 * @param coiProject Active project.
	 * @return Executing output.
	 */
	String runProject(COIProject coiProject);

	/**
	 * Method opens CMakeLists.txt file.
	 * 
	 * @param coiProject Active project.
	 * @return Text of CMakeLists.txt file.
	 */
	String openCMakeLists(COIProject coiProject);

	/**
	 * Method saves text to CMakeLists.txt file.
	 * 
	 * @param textToSave Text to save.
	 * @param coiProject Active project.
	 * @return True if CMakeLists.txt was saved.
	 */
	boolean saveCMakeLists(String textToSave, COIProject coiProject);

	/**
	 * Method generates CMakeLists.txt file.
	 * 
	 * @param typeOfGenerate Type of generate (Executable, Static, Shared).
	 * @param coiProject Active project.
	 * @return Generating progress.
	 */
	String generateCMakeLists(String typeOfGenerate, COIProject coiProject);
	
	/**
	 * Method tests if CMakeLists.txt and Makefile exists. Result is important for generating these files before project build.
	 * 
	 * @param coiProject Active project.
	 * @return Number type of necessary generating.
	 */
	int prebuildGenerating(COIProject coiProject);
}
