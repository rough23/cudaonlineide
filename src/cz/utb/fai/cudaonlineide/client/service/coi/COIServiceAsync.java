package cz.utb.fai.cudaonlineide.client.service.coi;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import cz.utb.fai.cudaonlineide.shared.dto.COIFile;
import cz.utb.fai.cudaonlineide.shared.dto.COIFolder;
import cz.utb.fai.cudaonlineide.shared.dto.COIObject;
import cz.utb.fai.cudaonlineide.shared.dto.COIProject;
import cz.utb.fai.cudaonlineide.shared.dto.COIWorkspace;
import cz.utb.fai.cudaonlineide.shared.dto.COIFileData;

/**
 * The asynchronous counterpart of <code>COIService</code>.
 *
 * @author m_belanec
 */
public interface COIServiceAsync {

    /**
     * Method retrieving workspace by workspace path.
     *
     * @param workspacePath Workspace path.
     * @param callback Asynchronous callback with workspace file.
     */
    void getCOIWorkspaceServiceMethod(String workspacePath,
            AsyncCallback<COIWorkspace> callback);

    /**
     * Method retrieving text from given file.
     *
     * @param coiObject File without text.
     * @param callback Asynchronous callback with file with text.
     */
    void addTextToCOIFileServiceMethod(COIObject coiObject,
            AsyncCallback<COIObject> callback);

    /**
     * Method creating new file.
     *
     * @param coiWorkspace Active workspace.
     * @param coiFile File to create.
     * @param callback Asynchronous callback with boolean value.
     */
    void createNewFile(COIWorkspace coiWorkspace, COIFile coiFile,
            AsyncCallback<Boolean> callback);

    /**
     * Method creating new folder.
     *
     * @param coiWorkspace Active workspace.
     * @param coiFolder Folder to create.
     * @param callback Asynchronous callback with boolean value.
     */
    void createNewFolder(COIWorkspace coiWorkspace, COIFolder coiFolder,
            AsyncCallback<Boolean> callback);

    /**
     * Method creating new project.
     *
     * @param coiWorkspace Active workspace.
     * @param coiProject Project to create.
     * @param callback Asynchronous callback with boolean value.
     */
    void createNewProject(COIWorkspace coiWorkspace, COIProject coiProject,
            AsyncCallback<Boolean> callback);

    /**
     * Method creating new workspace.
     *
     * @param coiWorkspace Workspace to create.
     * @param callback Asynchronous callback with boolean value.
     */
    void createNewWorkspace(COIWorkspace coiWorkspace,
            AsyncCallback<Boolean> callback);

    /**
     * Method deleting object from workspace.
     *
     * @param coiWorkspace Active workspace.
     * @param coiObject Object to delete.
     * @param callback Asynchronous callback with boolean value.
     */
    void deleteFromWorkspace(COIWorkspace coiWorkspace, COIObject coiObject,
            AsyncCallback<Boolean> callback);

    /**
     * Method gets text of given log file.
     *
     * @param logFile Log file name.
     * @param callback Asynchronous callback with text of log file.
     */
    void getConnectionStatus(String logFile, AsyncCallback<String> callback);

    /**
     * Method updates workspace file.
     *
     * @param coiWorkspace Active workspace to update.
     * @param callback Empty asynchronous callback.
     */
    void updateWorkspace(COIWorkspace coiWorkspace, AsyncCallback<Void> callback);

    /**
     * Method updates workspace after upload folder.
     *
     * @param coiWorkspace Active workspace.
     * @param projectPath Active project path.
     * @param serializedCoiFolder Serialized folder to upload.
     * @param callback Asynchronous callback with array of upload objects.
     */
    void updateWorkspaceUploadFolder(COIWorkspace coiWorkspace,
            String projectPath, String serializedCoiFolder,
            AsyncCallback<COIObject[]> callback);

    /**
     * Method updates workspace after upload project.
     *
     * @param coiWorkspace Active workspace.
     * @param workspacePath Active workspace path.
     * @param serializedCoiProject Serialized project to upload.
     * @param callback Asynchronous callback with array of upload objects.
     */
    void updateWorkspaceUploadProject(COIWorkspace coiWorkspace,
            String workspacePath, String serializedCoiProject,
            AsyncCallback<COIObject[]> callback);

    /**
     * Method gets all user workspace files.
     *
     * @param userWorkspacePath User work directory.
     * @param callback Asynchronous callback with names of user workspace files.
     */
    void getUserWorkspaceFiles(String userWorkspacePath,
            AsyncCallback<List<String>> callback);

    /**
     * Method deleting workspace.
     *
     * @param workspacePath Path of workspace to delete.
     * @param callback Asynchronous callback with boolean value.
     */
    void deleteWorkspace(String workspacePath, AsyncCallback<Boolean> callback);

    /**
     * Method updates workspace after upload workspace.
     *
     * @param serializedCoiWorkspace Serialized workspace to upload.
     * @param callback Asynchronous callback with array of upload objects.
     */
    void updateWorkspaceUploadWorkspace(String serializedCoiWorkspace,
            AsyncCallback<COIWorkspace> callback);

    /**
     * Method gets tags from given file.
     *
     * @param filePath File path.
     * @param callback Asynchronous callback with list of tags.
     */
    void getTagsFromFile(String filePath, AsyncCallback<List<COIFileData>> callback);

    /**
     * Method creates makefile.
     *
     * @param coiWorkspace Active workspace.
     * @param coiProject Active project.
     * @param callback Asynchronous callback with creating progress.
     */
    void createMakefile(COIWorkspace coiWorkspace, COIProject coiProject, AsyncCallback<String> callback);

    /**
     * Method builds active project.
     *
     * @param coiWorkspace Active workspace.
     * @param coiProject Active project.
     * @param callback Asynchronous callback with building progress.
     */
    void buildProject(COIWorkspace coiWorkspace, COIProject coiProject, AsyncCallback<String[]> callback);

    /**
     * Method executes active project.
     *
     * @param coiProject Active project.
     * @param callback Asynchronous callback with executing progress.
     */
    void runProject(COIProject coiProject, AsyncCallback<String> callback);

    /**
     * Method opens CMakeLists.txt file.
     *
     * @param coiProject Active project.
     * @param callback Asynchronous callback with text of CMakeLists.txt file.
     */
    void openCMakeLists(COIProject coiProject,
            AsyncCallback<String> callback);

    /**
     * Method saves text to CMakeLists.txt file.
     *
     * @param textToSave Text to save.
     * @param coiProject Active project.
     * @param callback Asynchronous callback with boolean value.
     */
    void saveCMakeLists(String textToSave, COIProject coiProject,
            AsyncCallback<Boolean> callback);

    /**
     * Method generates CMakeLists.txt file.
     *
     * @param typeOfGenerate Type of generate (Executable, Static, Shared).
     * @param coiProject Active project.
     * @param callback Asynchronous callback with generating progress.
     */
    void generateCMakeLists(String typeOfGenerate, COIProject coiProject,
            AsyncCallback<String> callback);

    /**
     * Method tests if CMakeLists.txt and Makefile exists. Result is important
     * for generating these files before project build.
     *
     * @param coiProject Active project.
     * @param callback Asynchronous callback with number type of necessary
     * generating.
     */
    void prebuildGenerating(COIProject coiProject,
            AsyncCallback<Integer> callback);

}
