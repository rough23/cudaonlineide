package cz.utb.fai.cudaonlineide.server.service.coi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.zeroturnaround.zip.ZipUtil;

import cz.utb.fai.cudaonlineide.buildserver.BuildServer;
import cz.utb.fai.cudaonlineide.buildserver.BuildServerConstants;
import cz.utb.fai.cudaonlineide.client.service.coi.COIService;
import cz.utb.fai.cudaonlineide.server.verifier.RPCVerifier;
import cz.utb.fai.cudaonlineide.shared.constants.COIConstants;
import cz.utb.fai.cudaonlineide.shared.constants.WorkspaceConstants;
import cz.utb.fai.cudaonlineide.shared.dto.COIEnum;
import cz.utb.fai.cudaonlineide.shared.dto.COIFile;
import cz.utb.fai.cudaonlineide.shared.dto.COIFolder;
import cz.utb.fai.cudaonlineide.shared.dto.COIObject;
import cz.utb.fai.cudaonlineide.shared.dto.COIProject;
import cz.utb.fai.cudaonlineide.shared.dto.COIWorkspace;
import cz.utb.fai.cudaonlineide.shared.dto.COIFileData;
import cz.utb.fai.cudaonlineide.shared.dto.project.COIBuildConfiguration;
import cz.utb.fai.cudaonlineide.shared.dto.project.COICompiler;
import cz.utb.fai.cudaonlineide.shared.dto.project.COIConfiguration;
import cz.utb.fai.cudaonlineide.shared.dto.project.COIGlobal;
import cz.utb.fai.cudaonlineide.shared.dto.project.COILinker;

import com.google.gson.Gson;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server-side implementation of the RPC COI file service.
 *
 * @author Belanec
 */
public class COIServiceImpl extends RemoteServiceServlet implements COIService {

    /**
     * Serial version UID for COIService implementation.
     */
    private static final long serialVersionUID = -856567840522833339L;

    /**
     * Chosen configuration for CUDA build.
     */
    public static final String CUDA_BUILD_CONFIGURATION = WorkspaceConstants.REMOTE_CONFIGURATION;

    /**
     * Method retrieving workspace by workspace path.
     *
     * @param workspacePath Workspace path.
     * @return Workspace file.
     */
    @Override
    public COIWorkspace getCOIWorkspaceServiceMethod(String workspacePath) {

        if (!RPCVerifier.isValidFilePath(workspacePath)) {
            throw new IllegalArgumentException("Workspace path is not correct. Server info: " + getServletContext().getServerInfo());
        }

        System.out.println("COIServiceImpl LOG [Retrieving workspace " + workspacePath + "]");

        return this.getCOIWorkspace(workspacePath);
    }

    /**
     * Method retrieving text from given file.
     *
     * @param coiObject File without text.
     * @return File with text
     */
    @Override
    public COIObject addTextToCOIFileServiceMethod(COIObject coiObject) {

        if (!RPCVerifier.isInitialized(coiObject)) {
            throw new IllegalArgumentException("COIObject has not initialized. Server info: " + getServletContext().getServerInfo());
        }

        if (!RPCVerifier.isValidFilePath(coiObject.getPath())) {
            throw new IllegalArgumentException("COIObject has not correct file path. Server info: " + getServletContext().getServerInfo());
        }

        System.out.println("COIServiceImpl LOG [Retrieving text]");

        return this.addTextToCOIFile(coiObject);
    }

    /**
     * Method creating new file.
     *
     * @param coiWorkspace Active workspace.
     * @param coiFile File to create.
     * @return True if file was created.
     */
    @Override
    public boolean createNewFile(COIWorkspace coiWorkspace, COIFile coiFile) {

        if (!RPCVerifier.isCOIWorkspaceCorrect(coiWorkspace)) {
            throw new IllegalArgumentException("COIWorkspace has not correct form to store. Server info: " + getServletContext().getServerInfo());
        }

        if (!RPCVerifier.isCOIFileCorrect(coiFile)) {
            throw new IllegalArgumentException("COIFile has not correct form to store. Server info: " + getServletContext().getServerInfo());
        }

        System.out.println("COIServiceImpl LOG [Creating file " + coiFile.getName() + "]");

        String text = coiFile.getText();

        try {
            File file = new File(coiFile.getPath());
            BufferedWriter output = new BufferedWriter(new FileWriter(file));
            output.write(text);
            output.close();
            this.localUpdateWorkspace(coiWorkspace);
            return true;
        } catch (IOException e) {
            Logger.getLogger(COIServiceImpl.class.getName()).log(Level.SEVERE, null, e);
            throw new IllegalArgumentException("Error in creating new file. Server info: " + getServletContext().getServerInfo());
        }
    }

    /**
     * Method creating new folder.
     *
     * @param coiWorkspace Active workspace.
     * @param coiFolder Folder to create.
     * @return True if folder was created.
     */
    @Override
    public boolean createNewFolder(COIWorkspace coiWorkspace, COIFolder coiFolder) {

        if (!RPCVerifier.isCOIWorkspaceCorrect(coiWorkspace)) {
            throw new IllegalArgumentException("COIWorkspace has not correct form to store. Server info: " + getServletContext().getServerInfo());
        }

        if (!RPCVerifier.isCOIFolderCorrect(coiFolder)) {
            throw new IllegalArgumentException("COIFolder has not correct form to store. Server info: " + getServletContext().getServerInfo());
        }

        System.out.println("COIServiceImpl LOG [Creating folder " + coiFolder.getName() + "]");

        File file = new File(coiFolder.getPath());
        this.localUpdateWorkspace(coiWorkspace);
        return file.mkdir();
    }

    /**
     * Method creating new project.
     *
     * @param coiWorkspace Active workspace.
     * @param coiProject Project to create.
     * @return True if project was created.
     */
    @Override
    public boolean createNewProject(COIWorkspace coiWorkspace, COIProject coiProject) {

        if (!RPCVerifier.isCOIWorkspaceCorrect(coiWorkspace)) {
            throw new IllegalArgumentException("COIWorkspace has not correct form to store. Server info: " + getServletContext().getServerInfo());
        }

        if (!RPCVerifier.isCOIProjectCorrect(coiProject)) {
            throw new IllegalArgumentException("COIProject has not correct form to store. Server info: " + getServletContext().getServerInfo());
        }

        System.out.println("COIServiceImpl LOG [Creating project " + coiProject.getName() + "]");

        File file = new File(coiProject.getPath());
        this.localUpdateWorkspace(coiWorkspace);
        return file.mkdir();
    }

    /**
     * Method creating new workspace.
     *
     * @param coiWorkspace Workspace to create.
     * @return True if workspace was created.
     */
    @Override
    public boolean createNewWorkspace(COIWorkspace coiWorkspace) {

        if (!RPCVerifier.isCOIWorkspaceCorrect(coiWorkspace)) {
            throw new IllegalArgumentException("COIWorkspace has not correct form to store. Server info: " + getServletContext().getServerInfo());
        }

        System.out.println("COIServiceImpl LOG [Creating workspace " + coiWorkspace.getName() + "]");

        File file = new File(coiWorkspace.getPath());
        this.localUpdateWorkspace(coiWorkspace);
        return file.mkdir();
    }

    /**
     * Method deleting object from workspace.
     *
     * @param coiWorkspace Active workspace.
     * @param coiObject Object to delete.
     * @return True if object was deleted.
     */
    @Override
    public boolean deleteFromWorkspace(COIWorkspace coiWorkspace, COIObject coiObject) {

        if (!RPCVerifier.isCOIWorkspaceCorrect(coiWorkspace)) {
            throw new IllegalArgumentException("COIWorkspace has not correct form to store. Server info: " + getServletContext().getServerInfo());
        }

        if (!RPCVerifier.isInitialized(coiObject)) {
            throw new IllegalArgumentException("COIObject has not initialized. Server info: " + getServletContext().getServerInfo());
        }

        System.out.println("COIServiceImpl LOG [Deleting object from workspace " + coiWorkspace.getName() + "]");

        File file = new File(coiObject.getPath());
        COIServiceImpl.removeAll(file);
        this.localUpdateWorkspace(coiWorkspace);
        return true;
    }

    /**
     * Method gets text of given log file.
     *
     * @param logFile Log file name.
     * @return Text of log file.
     */
    @Override
    public String getConnectionStatus(String logFile) {

        if (!RPCVerifier.isStringWithValue(logFile)) {
            throw new IllegalArgumentException("Name of log file is not correct. Server info: " + getServletContext().getServerInfo());
        }

        String command = "cat";
        String logDirectory = "/var/lib/tomcat7/logs/";
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();

        String logPath;

        if (logFile.equals("localhost")) {
            logPath = logDirectory + logFile + COIConstants.COMMA + dateFormat.format(cal.getTime()) + ".log";
        } else {
            logPath = logDirectory + logFile + ".out";
        }

        try {
            Process proc = Runtime.getRuntime().exec(command + " " + logPath);

            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));

            String line = COIConstants.EMPTY;
            StringBuilder sb = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                sb.append(line).append(System.lineSeparator());
            }

            proc.waitFor();

            return sb.toString();

        } catch (IOException | InterruptedException e) {
            Logger.getLogger(COIServiceImpl.class.getName()).log(Level.SEVERE, null, e);
            throw new IllegalArgumentException("Connection status cannot be recognized. Server info: " + getServletContext().getServerInfo());
        }
    }

    /**
     * Method creates makefile.
     *
     * @param coiWorkspace Active workspace.
     * @param coiProject Active project.
     * @return Creating progress.
     */
    @Override
    public String createMakefile(final COIWorkspace coiWorkspace, final COIProject coiProject) {

        if (!RPCVerifier.isCOIProjectCorrect(coiProject)) {
            throw new IllegalArgumentException("COIProject has not correct form. Server info: " + getServletContext().getServerInfo());
        }

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Logger.getLogger(COIServiceImpl.class.getName()).log(Level.SEVERE, null, e);
        }

        System.out.println("COIServiceImpl LOG [Creating makefile for project " + coiProject.getName() + "]");

        File cMakeLists = new File(coiProject.getPath() + "build/CMakeLists.txt");
        File cMakeListsBckp = new File(coiProject.getPath() + "build/CMakeLists.txt.backup");

        if (!cMakeLists.exists()) {

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Logger.getLogger(COIServiceImpl.class.getName()).log(Level.SEVERE, null, e);
            }

            if (!cMakeLists.exists()) {
                return "ERROR: CMakeLists.txt must be created first.";
            }
        }

        try {

            File cMakeListsTmp = new File(coiProject.getPath() + "CMakeLists.txt");
            File cMakeListsBckpTmp = new File(coiProject.getPath() + "CMakeLists.txt.backup");

            FileUtils.copyFile(cMakeLists, cMakeListsTmp);
            FileUtils.copyFile(cMakeListsBckp, cMakeListsBckpTmp);

            File buildFolder = new File(coiProject.getPath() + "build/");

            if (buildFolder.exists()) {
                COIServiceImpl.removeAll(buildFolder);
            }

            buildFolder.mkdir();

            FileUtils.copyFile(cMakeListsTmp, cMakeLists);
            FileUtils.copyFile(cMakeListsBckpTmp, cMakeListsBckp);

            cMakeListsTmp.delete();
            cMakeListsBckpTmp.delete();

            final StringBuilder sb = new StringBuilder();

            try {
                ProcessBuilder builder = new ProcessBuilder("/bin/sh");
                final Process proc = builder.start();

                final Thread inputThread = new Thread() {
                    @Override
                    public void run() {
                        Scanner s = new Scanner(proc.getInputStream());
                        while (s.hasNextLine()) {
                            synchronized (sb) {
                                sb.append(s.nextLine()).append(System.lineSeparator());
                            }
                        }
                        s.close();
                    }
                };

                final Thread errorThread = new Thread() {
                    @Override
                    public void run() {
                        Scanner e = new Scanner(proc.getErrorStream());
                        while (e.hasNextLine()) {
                            synchronized (sb) {
                                sb.append(e.nextLine()).append(System.lineSeparator());
                            }
                        }
                        e.close();
                    }
                };

                Thread cmakeThread = new Thread() {
                    @Override
                    public void run() {

                        try {
                            BufferedWriter p_stdin = new BufferedWriter(new OutputStreamWriter(proc.getOutputStream()));

                            p_stdin.write("cd " + coiProject.getPath() + "build/");
                            p_stdin.newLine();
                            p_stdin.flush();

                            p_stdin.write("cmake -G \"Unix Makefiles\"");
                            p_stdin.newLine();
                            p_stdin.flush();

                            inputThread.start();
                            errorThread.start();

                            p_stdin.write("exit");
                            p_stdin.newLine();
                            p_stdin.flush();

                        } catch (IOException e) {
                            Logger.getLogger(COIServiceImpl.class.getName()).log(Level.SEVERE, null, e);
                        }
                    }
                };

                cmakeThread.start();

                int i = 10;

                while (i > 0) {

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Logger.getLogger(COIServiceImpl.class.getName()).log(Level.SEVERE, null, e);
                    }

                    i--;

                    if (!cmakeThread.isAlive()) {
                        synchronized (sb) {
                            if (sb.toString().isEmpty()) {
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    Logger.getLogger(COIServiceImpl.class.getName()).log(Level.SEVERE, null, e);
                                }
                            }
                        }
                        break;
                    }
                }

                if (inputThread.isAlive()) {
                    inputThread.interrupt();
                }

                if (errorThread.isAlive()) {
                    errorThread.interrupt();
                }

                if (cmakeThread.isAlive()) {
                    cmakeThread.interrupt();
                }

                if (sb.toString().isEmpty()) {
                    return "ERROR: Problem with creating makefile.";
                }

                this.editCMakeFileToRelativePaths(coiProject.getPath(), "cmake_install.cmake");
                this.editCMakeFileToRelativePaths(coiProject.getPath(), "CMakeCache.txt");
                this.editCMakeFileToRelativePaths(coiProject.getPath(), "Makefile");

                ProcessBuilder builderForPermissions = new ProcessBuilder("/bin/sh");
                Process procForPermissions = builderForPermissions.start();
                BufferedWriter p_stdinForPermissions = new BufferedWriter(new OutputStreamWriter(procForPermissions.getOutputStream()));

                p_stdinForPermissions.write("chmod -R 777 " + coiProject.getPath() + "build/ && exit");
                p_stdinForPermissions.newLine();
                p_stdinForPermissions.flush();

                //COIServiceImpl.this.buildProject(coiWorkspace, coiProject);

                return "----- CREATING MAKEFILE -----" + System.lineSeparator() + sb.toString().replace("-- ", "");

            } catch (IOException e2) {
                Logger.getLogger(COIServiceImpl.class.getName()).log(Level.SEVERE, null, e2);
                return "ERROR: Problem with process builder.";
            }

        } catch (IOException e) {
            Logger.getLogger(COIServiceImpl.class.getName()).log(Level.SEVERE, null, e);
            return "ERROR: Cannot copy CMakeLists.txt for creating makefile.";
        }

    }

    /**
     * Method generates CMakeLists.txt file.
     *
     * @param typeOfGenerate Type of generate (Executable, Static, Shared).
     * @param coiProject Active project.
     * @return Generating progress.
     */
    @Override
    public String generateCMakeLists(String typeOfGenerate, COIProject coiProject) {

        if (!RPCVerifier.isCOIProjectCorrect(coiProject)) {
            throw new IllegalArgumentException("COIProject has not correct form. Server info: " + getServletContext().getServerInfo());
        }

        if (!RPCVerifier.isStringWithValue(typeOfGenerate)) {
            throw new IllegalArgumentException("Type of generate has not correct form. Server info: " + getServletContext().getServerInfo());
        }

        System.out.println("COIServiceImpl LOG [Generate CMakeLists.txt for project " + coiProject.getName() + "]");

        List<String> executablesForBuild = new ArrayList<>();
        List<String> foldersForBuild = new ArrayList<>();

        for (COIFolder coiFolder : coiProject.getItems()) {
            for (COIFile coiFile : coiFolder.getItems()) {
                if (coiFile.getExtension().equals("c") || coiFile.getExtension().equals("cpp") || coiFile.getExtension().equals("cu")) {

                    executablesForBuild.add(coiFile.getPath());

                    if (!foldersForBuild.contains(coiFolder.getName())) {
                        foldersForBuild.add(coiFolder.getName());
                    }
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("----- GENERATING CMAKELISTS -----").append(System.lineSeparator());

        if (executablesForBuild.isEmpty()) {
            return "ERROR: No source files to create CMakeLists.txt.";
        }

        try {

            File buildFolder = new File(coiProject.getPath() + "build/");

            if (buildFolder.exists()) {
                COIServiceImpl.removeAll(buildFolder);
            }

            buildFolder.mkdir();
            File cMakeLists = new File(coiProject.getPath() + "build/CMakeLists.txt");
            cMakeLists.createNewFile();

            COIConfiguration coiConfiguration;

            switch (coiProject.getBuildConfiguration().getActive()) {
                case COIConstants.BUILD_CONFIGURATION_DEBUG:
                    coiConfiguration = coiProject.getBuildConfiguration().getDebug();
                    break;

                case COIConstants.BUILD_CONFIGURATION_RELEASE:
                    coiConfiguration = coiProject.getBuildConfiguration().getRelease();
                    break;

                case COIConstants.BUILD_CONFIGURATION_CUSTOM:
                    coiConfiguration = coiProject.getBuildConfiguration().getCustom();
                    break;
                default:
                    coiConfiguration = new COIConfiguration();
            }

            String ccPtx = coiConfiguration.getGlobal().getCcPtx();
            String ccGpu = coiConfiguration.getGlobal().getCcGpu();
            
            FileWriter fw = new FileWriter(cMakeLists.getAbsoluteFile());

            Path buildPath = Paths.get(buildFolder.getPath());

            // NVCC BUILDER
            BufferedWriter bw = new BufferedWriter(fw);

            // CMAKE REQUIRED
            bw.write("cmake_minimum_required(VERSION 2.8.11)" + System.lineSeparator() + System.lineSeparator());

            // PROJECT
            bw.write("project(" + coiProject.getName() + ")" + System.lineSeparator() + System.lineSeparator());

            // CUDA REQUIRED FILES
            bw.write("find_package(CUDA QUIET REQUIRED)" + System.lineSeparator() + System.lineSeparator());

            // NVCC OPTIONS
            bw.write("set(CUDA_NVCC_FLAGS ${CUDA_NVCC_FLAGS}; -gencode arch=compute_" + ccPtx.replace(".", "") + ",code=sm_" + ccGpu.replace(".", "") + ")" + System.lineSeparator() + System.lineSeparator());

            // INCLUDE DIRECTORIES
            bw.write("include_directories(" + System.lineSeparator());
            for (String folderForBuild : foldersForBuild) {
                String relativePath = buildPath.relativize(Paths.get(coiProject.getPath() + folderForBuild + "/")).toString();
                bw.write("\t" + relativePath + System.lineSeparator());
            }
            for (String includeDirectory : coiConfiguration.getCompiler().getIncludeDirectories()) {
                bw.write("\t" + includeDirectory + System.lineSeparator());
            }
            bw.write(")" + System.lineSeparator() + System.lineSeparator());

            // ADD DEFINITIONS
            if (!coiConfiguration.getCompiler().getPreprocessors().isEmpty()) {
                bw.write("add_definitions(" + System.lineSeparator());
                for (String preprocessor : coiConfiguration.getCompiler().getPreprocessors()) {
                    bw.write("\t-D" + preprocessor + System.lineSeparator());
                }
                bw.write(")" + System.lineSeparator() + System.lineSeparator());
            }

            // LINKER OPTIONS
            bw.write("set(CMAKE_LDFLAGS \"${CMAKE_LDFLAGS}  ");
            for (String linkerOption : coiConfiguration.getLinker().getOptions()) {
                bw.write(linkerOption + " ");
            }
            bw.write("\")" + System.lineSeparator() + System.lineSeparator());

            // LIBRARY PATHS
            bw.write("set(CMAKE_LDFLAGS \"${CMAKE_LDFLAGS} ");
            bw.write("-L\\\".\\\" ");
            for (String libraryPath : coiConfiguration.getLinker().getLibraryPaths()) {
                bw.write("-L\\\"" + libraryPath + "\\\" ");
            }
            bw.write("\")" + System.lineSeparator() + System.lineSeparator());

            // DEFINE THE NVCC SOURCES
            bw.write("set ( CUDA_SRCS" + System.lineSeparator());
            for (String cppForBuild : executablesForBuild) {
                String relativePath = buildPath.relativize(Paths.get(cppForBuild)).toString();
                bw.write("\t" + relativePath + System.lineSeparator());
            }
            bw.write(")" + System.lineSeparator() + System.lineSeparator());

            // SET SOURCE FILES PROPERTIES
            bw.write("set_source_files_properties(" + System.lineSeparator());
            bw.write("\t${CUDA_SRCS} PROPERTIES COMPILE_FLAGS " + System.lineSeparator());
            bw.write("\t\" ");
            for (String compilerOption : coiConfiguration.getCompiler().getOptions()) {
                bw.write(compilerOption + " ");
            }
            bw.write("\"" + System.lineSeparator());
            bw.write(")" + System.lineSeparator() + System.lineSeparator());

            switch (typeOfGenerate) {

                case COIConstants.MENU_EXECUTABLE:
                    // ADD EXECUTABLE
                    bw.write("cuda_add_executable(" + coiProject.getName() + ".build ${CUDA_SRCS})" + System.lineSeparator() + System.lineSeparator());
                    break;

                case COIConstants.MENU_STATIC_LIB:
                    // ADD STATIC LIBRARY
                    bw.write("cuda_add_library(" + coiProject.getName() + ".build STATIC ${CUDA_SRCS})" + System.lineSeparator() + System.lineSeparator());
                    break;

                case COIConstants.MENU_SHARED_LIB:
                    // ADD SHARED LIBRARY
                    bw.write("cuda_add_library(" + coiProject.getName() + ".build SHARED ${CUDA_SRCS})" + System.lineSeparator() + System.lineSeparator());
                    break;

                default:
                    bw.close();

                    if (buildFolder.exists()) {
                        COIServiceImpl.removeAll(buildFolder);
                    }

                    return "ERROR: Type of generate has not correct form.";
            }

            // TARGET LINK LIBRARIES
            if (!coiConfiguration.getLinker().getLibraryNames().isEmpty()) {
                bw.write("target_link_libraries(" + coiProject.getName() + System.lineSeparator());
                for (String libraryName : coiConfiguration.getLinker().getLibraryNames()) {
                    bw.write("\t" + libraryName + System.lineSeparator());
                }
                bw.write(")" + System.lineSeparator() + System.lineSeparator());
            }

            bw.flush();
            bw.close();

            File cMakeListsBckp = new File(coiProject.getPath() + "build/CMakeLists.txt.backup");
            FileUtils.copyFile(cMakeLists, cMakeListsBckp);

        } catch (IOException e) {
            Logger.getLogger(COIServiceImpl.class.getName()).log(Level.SEVERE, null, e);
            return "ERROR: Problem with creating CMakeLists.txt.";
        }

        sb.append("Generating CMakeLists.txt was successfully completed.").append(System.lineSeparator());
        return sb.toString();
    }

    /**
     * Method builds active project.
     *
     * @param coiWorkspace Active workspace.
     * @param coiProject Active project.
     * @return Building progress.
     */
    @Override
    public String[] buildProject(COIWorkspace coiWorkspace, final COIProject coiProject) {

        if (!RPCVerifier.isCOIProjectCorrect(coiProject)) {
            throw new IllegalArgumentException("COIProject has not correct form to store. Server info: " + getServletContext().getServerInfo());
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Logger.getLogger(COIServiceImpl.class.getName()).log(Level.SEVERE, null, e);
        }

        System.out.println("COIServiceImpl LOG [Building project " + coiProject.getName() + "]");

        File makefile = new File(coiProject.getPath() + "build/Makefile");

        if (!makefile.exists()) {

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Logger.getLogger(COIServiceImpl.class.getName()).log(Level.SEVERE, null, e);
            }

            if (!makefile.exists()) {
                String[] output = {"ERROR: Makefile must be created first."};
                return output;
            }
        }

        if (COIServiceImpl.CUDA_BUILD_CONFIGURATION.equals(WorkspaceConstants.REMOTE_CONFIGURATION)) {
            return this.buildProjectRemote(coiWorkspace, coiProject);
        } else {
            return this.buildProjectLocal(coiProject);
        }
    }

    /**
     * Method build project on local system.
     *
     * @param coiProject Active project.
     * @return Building progress.
     */
    private String[] buildProjectLocal(final COIProject coiProject) {

        File runfile = new File(coiProject.getPath() + "build/" + coiProject.getName() + ".build");
        File staticLibfile = new File(coiProject.getPath() + "build/" + coiProject.getName() + ".build.a");
        File sharedLibfile = new File(coiProject.getPath() + "build/" + coiProject.getName() + ".build.so");

        if (runfile.exists()) {
            runfile.delete();
        }

        if (staticLibfile.exists()) {
            staticLibfile.delete();
        }

        if (sharedLibfile.exists()) {
            sharedLibfile.delete();
        }

        final StringBuilder sb = new StringBuilder();

        try {

            ProcessBuilder builder = new ProcessBuilder("/bin/sh");
            final Process proc = builder.start();

            final Thread inputThread = new Thread() {
                @Override
                public void run() {
                    Scanner s = new Scanner(proc.getInputStream());
                    while (s.hasNextLine()) {
                        synchronized (sb) {
                            sb.append(s.nextLine()).append(System.lineSeparator());
                        }
                    }
                    s.close();
                }
            };

            final Thread errorThread = new Thread() {
                @Override
                public void run() {
                    Scanner e = new Scanner(proc.getErrorStream());
                    while (e.hasNextLine()) {
                        synchronized (sb) {
                            sb.append(e.nextLine()).append(System.lineSeparator());
                        }
                    }
                    e.close();
                }
            };

            Thread buildThread = new Thread() {
                @Override
                public void run() {

                    try {
                        BufferedWriter p_stdin = new BufferedWriter(new OutputStreamWriter(proc.getOutputStream()));

                        p_stdin.write("cd " + coiProject.getPath() + "build/");
                        p_stdin.newLine();
                        p_stdin.flush();

                        p_stdin.write("make");
                        p_stdin.newLine();
                        p_stdin.flush();

                        inputThread.start();
                        errorThread.start();

                        p_stdin.write("exit");
                        p_stdin.newLine();
                        p_stdin.flush();

                    } catch (IOException e) {
                        Logger.getLogger(COIServiceImpl.class.getName()).log(Level.SEVERE, null, e);
                    }
                }
            };

            buildThread.start();

            int i = 15;

            while (i > 0) {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Logger.getLogger(COIServiceImpl.class.getName()).log(Level.SEVERE, null, e);
                }

                i--;

                if (!buildThread.isAlive()) {
                    synchronized (sb) {
                        if (sb.toString().isEmpty()) {
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                Logger.getLogger(COIServiceImpl.class.getName()).log(Level.SEVERE, null, e);
                            }
                        }
                    }
                    break;
                }
            }

            if (inputThread.isAlive()) {
                inputThread.interrupt();
            }

            if (errorThread.isAlive()) {
                errorThread.interrupt();
            }

            if (buildThread.isAlive()) {
                buildThread.interrupt();
            }

            if (sb.toString().isEmpty()) {

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Logger.getLogger(COIServiceImpl.class.getName()).log(Level.SEVERE, null, e);
                }

                if (sb.toString().isEmpty()) {
                    String[] output = {"ERROR: Problem with build project."};
                    return output;
                }
            }

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Logger.getLogger(COIServiceImpl.class.getName()).log(Level.SEVERE, null, e);
            }

            System.out.println("COIServiceImpl LOG [Project " + coiProject.getName() + " was build]");

            String[] output = new String[2];

            output[0] = "Standalone";
            output[1] = "----- BUILDING -----" + System.lineSeparator() + sb.toString();
            return output;

        } catch (IOException e1) {
            Logger.getLogger(COIServiceImpl.class.getName()).log(Level.SEVERE, null, e1);
            String[] output = {"ERROR: Problem with process builder."};
            return output;
        }
    }

    /**
     * Method build project on remote system.
     *
     * @param coiWorkspace Active workspace.
     * @param coiProject Active project.
     * @return Building progress.
     */
    private String[] buildProjectRemote(COIWorkspace coiWorkspace, final COIProject coiProject) {

        try {
            String zipName = coiProject.getName() + COIConstants.EXTENSION_COMMA_ZIP;
            String zipPath = coiWorkspace.getPath() + zipName;

            File projectToZip = new File(coiProject.getPath());

            File toBuildFolder = new File(projectToZip.getParentFile().getAbsolutePath() + "/ToBuildAndZipFolder");
            toBuildFolder.mkdir();

            File projectCopy = new File(toBuildFolder.getAbsolutePath() + "/" + coiProject.getName());
            projectCopy.mkdir();

            FileUtils.copyDirectory(projectToZip, projectCopy);

            ZipUtil.pack(toBuildFolder, new File(zipPath));

            File zipToDownload = new File(zipPath);

            String uuid = "";
            String[] result = null;

            BuildServer bs = new BuildServer();

            if (zipToDownload.exists()) {

                uuid = bs.uploadAndBuild(zipToDownload);
                zipToDownload.delete();

                int i = 30;

                while (result == null && i > 0) {

                    Thread.sleep(1000);
                    i--;

                    try {
                        result = bs.getResult(uuid, BuildServerConstants.COMPILATION_OUTPUT);
                    } catch (IOException e) {
                        result = null;
                    }
                }
            }

            if (uuid == null || uuid.isEmpty() || result == null || result.length == 0) {
                String[] output = {"ERROR: Build failed."};
                return output;
            }

            COIServiceImpl.removeAll(toBuildFolder);

            String[] output = new String[2];

            StringBuilder outputResult = new StringBuilder();

            if (result[0] != null) {
                String[] splittedResults = result[0].split(System.lineSeparator());

                for (String splittedResult : splittedResults) {

                    if (splittedResult.isEmpty()) {
                        outputResult.append(COIConstants.EMPTY).append(System.lineSeparator());
                        continue;
                    }

                    for (int i = 0; i < splittedResult.length(); i++) {
                        if (Character.isUpperCase(splittedResult.codePointAt(i))) {
                            outputResult.append(splittedResult.substring(i).replace("`", "\'")).append(System.lineSeparator());
                            break;
                        }
                    }
                }
            }

            if (result.length == 2 && result[1].equals("true")) {
                output[0] = null;
                output[1] = "----- BUILDING -----" + System.lineSeparator() + outputResult.toString() + System.lineSeparator() + "Build failed.";
            } else {
                output[0] = uuid;
                output[1] = "----- BUILDING -----" + System.lineSeparator() + outputResult.toString() + System.lineSeparator() + "Build successful.";
            }

            return output;

        } catch (IOException e) {
            Logger.getLogger(COIServiceImpl.class.getName()).log(Level.SEVERE, null, e);
            String[] output = {"ERROR: Problem with ZIP file for build server."};
            return output;
        } catch (InterruptedException e) {
            Logger.getLogger(COIServiceImpl.class.getName()).log(Level.SEVERE, null, e);
            String[] output = {"ERROR: Problem with getting response from server."};
            return output;
        }

    }

    /**
     * Method executes active project.
     *
     * @param coiProject Active project.
     * @return Executing output.
     */
    @Override
    public String runProject(final COIProject coiProject) {

        if (!RPCVerifier.isCOIProjectCorrect(coiProject)) {
            throw new IllegalArgumentException("COIProject has not correct form to store. Server info: " + getServletContext().getServerInfo());
        }

        System.out.println("COIServiceImpl LOG [Executing project " + coiProject.getName() + "]");

        if (coiProject.getUuid() == null || coiProject.getUuid().isEmpty()) {
            return "ERROR: Project must be build first.";
        }

        if (COIServiceImpl.CUDA_BUILD_CONFIGURATION.equals(WorkspaceConstants.REMOTE_CONFIGURATION)) {
            return this.runProjectRemote(coiProject);
        } else {
            return this.runProjectLocal(coiProject);
        }
    }

    /**
     * Method executes project on local system.
     *
     * @param coiProject Active project.
     * @return Executing output.
     */
    private String runProjectLocal(final COIProject coiProject) {

        File staticLibfile = new File(coiProject.getPath() + "build/" + coiProject.getName() + ".build.a");

        if (staticLibfile.exists()) {
            return "ERROR: Static library cannot be executed.";
        }

        File sharedLibfile = new File(coiProject.getPath() + "build/" + coiProject.getName() + ".build.so");

        if (sharedLibfile.exists()) {
            return "ERROR: Shared library cannot be executed.";
        }

        File runfile = new File(coiProject.getPath() + "build/" + coiProject.getName() + ".build");

        if (!runfile.exists()) {

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Logger.getLogger(COIServiceImpl.class.getName()).log(Level.SEVERE, null, e);
            }

            if (!runfile.exists()) {
                return "ERROR: Project must be builded as executable first.";
            }
        }

        final StringBuilder sb = new StringBuilder();

        try {
            ProcessBuilder builder = new ProcessBuilder("/bin/sh");
            final Process proc = builder.start();

            final Thread inputThread = new Thread() {
                @Override
                public void run() {
                    Scanner s = new Scanner(proc.getInputStream());
                    while (s.hasNextLine()) {
                        synchronized (sb) {
                            sb.append(s.nextLine()).append(System.lineSeparator());
                        }
                    }
                    s.close();
                }
            };

            final Thread errorThread = new Thread() {
                @Override
                public void run() {
                    Scanner e = new Scanner(proc.getErrorStream());
                    while (e.hasNextLine()) {
                        synchronized (sb) {
                            sb.append(e.nextLine()).append(System.lineSeparator());
                        }
                    }
                    e.close();
                }
            };

            Thread runThread = new Thread() {
                @Override
                public void run() {

                    try {
                        BufferedWriter p_stdin = new BufferedWriter(new OutputStreamWriter(proc.getOutputStream()));

                        p_stdin.write("cd " + coiProject.getPath() + "build/");
                        p_stdin.newLine();
                        p_stdin.flush();

                        p_stdin.write("./" + coiProject.getName() + ".build");
                        p_stdin.newLine();
                        p_stdin.flush();

                        inputThread.start();
                        errorThread.start();

                        p_stdin.write("exit");
                        p_stdin.newLine();
                        p_stdin.flush();

                    } catch (IOException e) {
                        Logger.getLogger(COIServiceImpl.class.getName()).log(Level.SEVERE, null, e);
                    }
                }
            };

            runThread.start();

            int i = 20;

            while (i > 0) {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Logger.getLogger(COIServiceImpl.class.getName()).log(Level.SEVERE, null, e);
                }

                i--;

                if (!runThread.isAlive()) {
                    synchronized (sb) {
                        if (sb.toString().isEmpty()) {
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                Logger.getLogger(COIServiceImpl.class.getName()).log(Level.SEVERE, null, e);
                            }
                        }
                    }
                    break;
                }
            }

            if (inputThread.isAlive()) {
                inputThread.interrupt();
            }

            if (errorThread.isAlive()) {
                errorThread.interrupt();
            }

            if (runThread.isAlive()) {
                runThread.interrupt();
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Logger.getLogger(COIServiceImpl.class.getName()).log(Level.SEVERE, null, e);
            }

            System.out.println("COIServiceImpl LOG [Project " + coiProject.getName() + " was executed]");

            if (sb.toString().isEmpty()) {
                return "----- RUNNING -----" + System.lineSeparator() + "No output.";
            }

            return "----- RUNNING -----" + System.lineSeparator() + sb.toString();

        } catch (IOException e1) {
            Logger.getLogger(COIServiceImpl.class.getName()).log(Level.SEVERE, null, e1);
            return "ERROR: Problem with process builder.";
        }
    }

    /**
     * Method executes project on remote system.
     *
     * @param coiProject Active project.
     * @return Executing output.
     */
    private String runProjectRemote(final COIProject coiProject) {

    	COIConfiguration coiConfiguration;

        switch (coiProject.getBuildConfiguration().getActive()) {
            case COIConstants.BUILD_CONFIGURATION_DEBUG:
                coiConfiguration = coiProject.getBuildConfiguration().getDebug();
                break;

            case COIConstants.BUILD_CONFIGURATION_RELEASE:
                coiConfiguration = coiProject.getBuildConfiguration().getRelease();
                break;

            case COIConstants.BUILD_CONFIGURATION_CUSTOM:
                coiConfiguration = coiProject.getBuildConfiguration().getCustom();
                break;
            default:
                coiConfiguration = new COIConfiguration();
        }
    	
        String arguments = coiConfiguration.getGlobal().getArguments();
        
        BuildServer bs = new BuildServer();

        try {
            bs.execute(coiProject.getUuid(), arguments);
        } catch (IOException e) {
            Logger.getLogger(COIServiceImpl.class.getName()).log(Level.SEVERE, null, e);
        }

        String result[] = null;

        int i = 20;

        while (result == null && i > 0) {

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
                Logger.getLogger(COIServiceImpl.class.getName()).log(Level.SEVERE, null, e1);
            }

            i--;

            try {
                result = bs.getResult(coiProject.getUuid(), BuildServerConstants.EXECUTION_OUTPUT);
            } catch (IOException e) {
                result = null;
            }
        }

        if (result == null || result.length == 0) {
            return "ERROR: Execution failed.";
        }

        return "----- RUNNING -----" + System.lineSeparator() + result[0];
    }

    /**
     * Method gets tags from given file.
     *
     * @param filePath File path.
     * @return List of tags.
     */
    @Override
    public List<COIFileData> getTagsFromFile(String filePath) {

        if (!RPCVerifier.isValidFilePath(filePath)) {
            throw new IllegalArgumentException("File path is not correct. Server info: " + getServletContext().getServerInfo());
        }

        if (!filePath.endsWith(".c") && !filePath.endsWith(".cpp") && !filePath.endsWith(".h") && !filePath.endsWith(".cu") && !filePath.endsWith(".cuh")) {
            return new ArrayList<>();
        }

        System.out.println("COIServiceImpl LOG [Tagging file " + filePath + " to output]");

        File parentFolder = new File(filePath).getParentFile();

        String command = "ctags -R -u --fields=+aKmnsSzti-klf --langmap=c++:+.h.cuh.cu";

        try {
            Process proc = Runtime.getRuntime().exec(command + " " + filePath, null, parentFolder);
            proc.waitFor();

            File tags = new File(parentFolder, "tags");

            List<COIFileData> fileDataList = new ArrayList<>();

            if (!tags.exists()) {
                return fileDataList;
            }

            FileReader inputFile = new FileReader(tags);
            BufferedReader bufferReader = new BufferedReader(inputFile);
            String line;
            String lineTextToReplace;
            String[] splits;
            COIFileData fileData;

            while ((line = bufferReader.readLine()) != null) {
                if (!line.startsWith("!_")) {

                    if (line.contains("/^")) {
                        lineTextToReplace = line.substring(line.indexOf("/^"), line.indexOf("$/;\"") + 4);
                    } else {

                        int i = 1;

                        while (line.charAt(line.indexOf(";\"") - i) != '\t') {
                            i++;
                        }

                        lineTextToReplace = line.substring(line.indexOf(";\"") - (i - 1), line.indexOf(";\"") + 2);
                    }

                    line = line.replace(lineTextToReplace, "");
                    line = line.replace("\t\t", "\t");
                    splits = line.split("\t");

                    if (splits == null || splits.length < 2) {
                        continue;
                    }

                    fileData = new COIFileData();
                    fileData.setName(splits[0]);
                    fileData.setFile(splits[1]);
                    String[] emptyParent = {"", ""};
                    fileData.setParent(emptyParent);

                    String[] typeOfSplit;

                    for (int i = 2; i < splits.length; i++) {

                        if (splits[i].contains(":") && !splits[i].contains("::")) {
                            typeOfSplit = splits[i].split(":");

                            if (typeOfSplit == null || typeOfSplit.length != 2) {
                                continue;
                            }

                            if (typeOfSplit[0].equals("kind")) {
                                fileData.setKind(typeOfSplit[1]);
                            } else if (typeOfSplit[0].equals("line")) {
                                fileData.setLine(Integer.valueOf(typeOfSplit[1]));
                            } else if (typeOfSplit[0].equals("access")) {
                                fileData.setAccess(typeOfSplit[1]);
                            } else if (typeOfSplit[0].equals("implementation")) {
                                fileData.setImplementation(typeOfSplit[1]);
                            } else if (typeOfSplit[0].equals("inherits")) {
                                fileData.setInherits(typeOfSplit[1]);
                            } else if (typeOfSplit[0].equals("signature")) {
                                fileData.setSignature(typeOfSplit[1]);
                            } else {
                                String[] parent = {typeOfSplit[0], typeOfSplit[1]};
                                fileData.setParent(parent);
                            }
                        } else if (splits[i].contains(":") && splits[i].contains("::")) {
                            String name = splits[i].substring(0, splits[i].indexOf(":"));
                            String kind = splits[i].substring(splits[i].indexOf(":") + 1);

                            String[] parent = {name, kind};
                            fileData.setParent(parent);
                        }
                    }

                    if (fileData.getParent()[1].equals("")) {
                        fileData.setParents(fileData.getName());
                    } else {
                        fileData.setParents(fileData.getParent()[1] + "::" + fileData.getName());
                    }

                    StringBuilder outputBuilder = new StringBuilder();

                    if (!fileData.getImplementation().equals("")) {
                        outputBuilder.append(fileData.getImplementation()).append(" ");
                    }

                    outputBuilder.append(fileData.getName());
                    outputBuilder.append(fileData.getSignature());
                    outputBuilder.append(" : ");
                    outputBuilder.append(fileData.getLine().toString());
                    fileData.setOutput(outputBuilder.toString());

                    fileDataList.add(fileData);
                }
            }

            bufferReader.close();
            tags.delete();

            return fileDataList;

        } catch (IOException | InterruptedException e) {
            Logger.getLogger(COIServiceImpl.class.getName()).log(Level.SEVERE, null, e);
            throw new IllegalArgumentException("File tags cannot be read. Server info: " + getServletContext().getServerInfo());
        }
    }

    /**
     * Method updates workspace file.
     *
     * @param coiWorkspace Active workspace to update.
     */
    @Override
    public void updateWorkspace(COIWorkspace coiWorkspace) {

        if (!RPCVerifier.isCOIWorkspaceCorrect(coiWorkspace)) {
            throw new IllegalArgumentException("COIWorkspace has not correct form to store. Server info: " + getServletContext().getServerInfo());
        }

        System.out.println("COIServiceImpl LOG [Updating workspace " + coiWorkspace.getName() + "]");

        this.localUpdateWorkspace(coiWorkspace);
    }

    /**
     * Method updates workspace after upload folder.
     *
     * @param coiWorkspace Active workspace.
     * @param projectPath Active project path.
     * @param serializedCoiFolder Serialized folder to upload.
     * @return Array of upload objects.
     */
    @Override
    public COIObject[] updateWorkspaceUploadFolder(COIWorkspace coiWorkspace, String projectPath, String serializedCoiFolder) {

        if (!RPCVerifier.isCOIWorkspaceCorrect(coiWorkspace)) {
            throw new IllegalArgumentException("COIWorkspace has not correct form to store. Server info: " + getServletContext().getServerInfo());
        }

        if (!RPCVerifier.isStringWithValue(projectPath)) {
            throw new IllegalArgumentException("Project path is not correct. Server info: " + getServletContext().getServerInfo());
        }

        if (!RPCVerifier.isStringWithValue(serializedCoiFolder)) {
            throw new IllegalArgumentException("Serialized COI folder is not correct. Server info: " + getServletContext().getServerInfo());
        }

        System.out.println("COIServiceImpl LOG [Updating workspace " + coiWorkspace.getName() + " after upload folder]");

        Gson gson = new Gson();
        COIFolder coiFolder = gson.fromJson(serializedCoiFolder, COIFolder.class);

        boolean exist = false;

        for (COIProject coiProject : coiWorkspace.getItems()) {
            if (coiProject.getPath() == projectPath) {
                for (COIFolder coiFolderTmp : coiProject.getItems()) {
                    if (coiFolderTmp.getName() == coiFolder.getName()) {
                        exist = true;
                        break;
                    }
                }

                if (!exist) {
                    coiProject.getItems().add(coiFolder);
                } else {
                    return null;
                }
                break;
            }
        }

        this.localUpdateWorkspace(coiWorkspace);

        COIObject[] returnCOI = new COIObject[2];
        returnCOI[0] = coiWorkspace;
        returnCOI[1] = coiFolder;

        return returnCOI;
    }

    /**
     * Method updates workspace after upload project.
     *
     * @param coiWorkspace Active workspace.
     * @param workspacePath Active workspace path.
     * @param serializedCoiProject Serialized project to upload.
     * @return Array of upload objects.
     */
    @Override
    public COIObject[] updateWorkspaceUploadProject(COIWorkspace coiWorkspace, String workspacePath, String serializedCoiProject) {

        if (!RPCVerifier.isCOIWorkspaceCorrect(coiWorkspace)) {
            throw new IllegalArgumentException("COIWorkspace has not correct form to store. Server info: " + getServletContext().getServerInfo());
        }

        if (!RPCVerifier.isStringWithValue(workspacePath)) {
            throw new IllegalArgumentException("Workspace path is not correct. Server info: " + getServletContext().getServerInfo());
        }

        if (!RPCVerifier.isStringWithValue(serializedCoiProject)) {
            throw new IllegalArgumentException("Serialized COI project is not correct. Server info: " + getServletContext().getServerInfo());
        }

        System.out.println("COIServiceImpl LOG [Updating workspace " + coiWorkspace.getName() + " after upload project]");

        Gson gson = new Gson();
        COIProject coiProject = gson.fromJson(serializedCoiProject, COIProject.class);

        boolean exist = false;

        for (COIProject coiProjectTmp : coiWorkspace.getItems()) {
            if (coiProjectTmp.getName() == coiProject.getName()) {
                exist = true;
                break;
            }
        }

        if (!exist) {
            coiWorkspace.getItems().add(coiProject);
        } else {
            return null;
        }

        this.localUpdateWorkspace(coiWorkspace);

        COIObject[] returnCOI = new COIObject[2];
        returnCOI[0] = coiWorkspace;
        returnCOI[1] = coiProject;

        return returnCOI;
    }

    /**
     * Method updates workspace after upload workspace.
     *
     * @param serializedCoiWorkspace Serialized workspace to upload.
     * @return Array of upload objects.
     */
    @Override
    public COIWorkspace updateWorkspaceUploadWorkspace(String serializedCoiWorkspace) {

        if (!RPCVerifier.isStringWithValue(serializedCoiWorkspace)) {
            throw new IllegalArgumentException("Serialized COI workspace is not correct. Server info: " + getServletContext().getServerInfo());
        }

        System.out.println("COIServiceImpl LOG [Updating workspace]");

        Gson gson = new Gson();
        COIWorkspace coiWorkspace = gson.fromJson(serializedCoiWorkspace, COIWorkspace.class);

        this.localUpdateWorkspace(coiWorkspace);

        return coiWorkspace;
    }

    /**
     * Method gets all user workspace files.
     *
     * @param userWorkspacePath User work directory.
     * @return Names of user workspace files.
     */
    @Override
    public List<String> getUserWorkspaceFiles(String userWorkspacePath) {

        if (!RPCVerifier.isStringWithValue(userWorkspacePath)) {
            throw new IllegalArgumentException("User path is not correct. Server info: " + getServletContext().getServerInfo());
        }

        System.out.println("COIServiceImpl LOG [Retrieving workspace " + userWorkspacePath + " files]");

        List<String> workspaceFiles = new ArrayList<>();

        for (File tmpWorkspace : new File(userWorkspacePath).listFiles()) {
            if (tmpWorkspace.isFile() && tmpWorkspace.getName().endsWith("_workspace.cws2")) {
                workspaceFiles.add(tmpWorkspace.getName());
            }
        }

        return workspaceFiles;
    }

    /**
     * Method deleting workspace.
     *
     * @param workspacePath Path of workspace to delete.
     * @return True if workspace was deleted.
     */
    @Override
    public boolean deleteWorkspace(String workspacePath) {

        if (!RPCVerifier.isValidFilePath(workspacePath)) {
            throw new IllegalArgumentException("Workspace path is not correct. Server info: " + getServletContext().getServerInfo());
        }

        System.out.println("COIServiceImpl LOG [Deleting workspace " + workspacePath + "]");

        File workspaceFile = new File(workspacePath);
        String workspaceFolderPath = workspaceFile.getPath().replace("_workspace.cws2", "/");
        File workspaceFolder = new File(workspaceFolderPath);

        COIServiceImpl.removeAll(workspaceFolder);

        return workspaceFile.delete();
    }

    /**
     * Method opens CMakeLists.txt file.
     *
     * @param coiProject Active project.
     * @return Text of CMakeLists.txt file.
     */
    @Override
    public String openCMakeLists(COIProject coiProject) {

        if (!RPCVerifier.isCOIProjectCorrect(coiProject)) {
            throw new IllegalArgumentException("COIProject has not correct form. Server info: " + getServletContext().getServerInfo());
        }

        System.out.println("COIServiceImpl LOG [Open CMakeLists.txt from project " + coiProject.getName() + "]");

        File cMakeLists = new File(coiProject.getPath() + "build/CMakeLists.txt");

        if (!cMakeLists.exists()) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        BufferedReader bufferReader = null;

        try {
            FileReader inputFile = new FileReader(cMakeLists);
            bufferReader = new BufferedReader(inputFile);
            String line;

            while ((line = bufferReader.readLine()) != null) {
                sb.append(line).append(System.lineSeparator());
            }

            return sb.toString();

        } catch (IOException e) {
            Logger.getLogger(COIServiceImpl.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            try {
                bufferReader.close();
            } catch (IOException e) {
                Logger.getLogger(COIServiceImpl.class.getName()).log(Level.SEVERE, null, e);
            }
        }

        return null;
    }

    /**
     * Method saves text to CMakeLists.txt file.
     *
     * @param textToSave Text to save.
     * @param coiProject Active project.
     * @return True if CMakeLists.txt was saved.
     */
    @Override
    public boolean saveCMakeLists(String textToSave, COIProject coiProject) {

        if (!RPCVerifier.isCOIProjectCorrect(coiProject)) {
            throw new IllegalArgumentException("COIProject has not correct form. Server info: " + getServletContext().getServerInfo());
        }

        if (textToSave == null) {
            throw new IllegalArgumentException("Text is null. Server info: " + getServletContext().getServerInfo());
        }

        System.out.println("COIServiceImpl LOG [Saving CMakeLists.txt from project " + coiProject.getName() + "]");

        File cMakeLists = new File(coiProject.getPath() + "build/CMakeLists.txt");

        if (!cMakeLists.exists()) {
            return false;
        }

        try {
            BufferedWriter output = new BufferedWriter(new FileWriter(cMakeLists));
            output.write(textToSave);
            output.close();
            return true;
        } catch (IOException e) {
            Logger.getLogger(COIServiceImpl.class.getName()).log(Level.SEVERE, null, e);
            throw new IllegalArgumentException("Error in saving makefile. Server info: " + getServletContext().getServerInfo());
        }
    }

    /**
     * Method tests if CMakeLists.txt and Makefile exists. Result is important
     * for generating these files before project build.
     *
     * @param coiProject Active project.
     * @return Number type of necessary generating.
     */
    public int prebuildGenerating(COIProject coiProject) {

        File cMakeLists = new File(coiProject.getPath() + "build/CMakeLists.txt");
        File cMakeListsBckp = new File(coiProject.getPath() + "build/CMakeLists.txt.backup");

        if (!cMakeLists.exists() || !cMakeListsBckp.exists()) {
            return COIConstants.GENERATING_ALL;
        }

        String cMakeListsText = "";
        String cMakeListsBckpText = "";

        try (FileReader inputFile = new FileReader(cMakeLists)) {
            try (BufferedReader bufferReader = new BufferedReader(inputFile)) {

                String line;
                StringBuilder sb = new StringBuilder();

                while ((line = bufferReader.readLine()) != null) {
                    sb.append(line).append(System.lineSeparator());
                }

                cMakeListsText = sb.toString();
            }
        } catch (IOException e) {
            Logger.getLogger(COIServiceImpl.class.getName()).log(Level.SEVERE, null, e);
        }

        try (FileReader inputFile = new FileReader(cMakeListsBckp)) {
            try (BufferedReader bufferReader = new BufferedReader(inputFile)) {

                String line;
                StringBuilder sb = new StringBuilder();

                while ((line = bufferReader.readLine()) != null) {
                    sb.append(line).append(System.lineSeparator());
                }

                cMakeListsBckpText = sb.toString();
            }
        } catch (IOException e) {
            Logger.getLogger(COIServiceImpl.class.getName()).log(Level.SEVERE, null, e);
        }

        if (cMakeListsText.equals(cMakeListsBckpText)) {
            return COIConstants.GENERATING_ALL;
        }

        return COIConstants.GENERATING_WITH_QUESTION;
    }

    /**
     * Method creates COIWorkpspace tree to show actual workspace.
     *
     * @param workspacePath Path to actual workspace settings file.
     * @return COIWorkspace Actual workspace tree.
     */
    private COIWorkspace getCOIWorkspace(String workspacePath) {

        try {

            File fXmlFile = new File(workspacePath);
            COIWorkspace coiWorkspace = new COIWorkspace();

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            doc.getDocumentElement().normalize();

            if (!doc.getDocumentElement().getNodeName().equals(COIConstants.CWS_WORKSPACE)) {
                return null;
            }

            coiWorkspace.setName(doc.getDocumentElement().getAttribute(COIConstants.CWS_NAME));
            coiWorkspace.setPath(doc.getDocumentElement().getAttribute(COIConstants.CWS_PATH));
            coiWorkspace.setCwsVersion(doc.getDocumentElement().getElementsByTagName(COIConstants.CWS_VERSION).item(0).getTextContent());
            coiWorkspace.setTypeOfCOI(COIEnum.WORKSPACE);

            List<COIProject> coiProjects = new ArrayList<>();

            NodeList nProjects = doc.getElementsByTagName(COIConstants.CWS_PROJECT);

            if (nProjects == null || nProjects.getLength() == 0) {
                return coiWorkspace;
            }

            for (int i = 0; i < nProjects.getLength(); i++) {

                Node nProject = nProjects.item(i);

                if (nProject.getNodeName().equals(COIConstants.CWS_PROJECT) && nProject.getNodeType() == Node.ELEMENT_NODE) {

                    Element eProject = (Element) nProject;

                    COIProject coiProject = new COIProject();
                    coiProject.setName(eProject.getAttribute(COIConstants.CWS_NAME));
                    coiProject.setPath(eProject.getAttribute(COIConstants.CWS_PATH));
                    coiProject.setTypeOfCOI(COIEnum.PROJECT);

                    COIBuildConfiguration coiBuildConfiguration = new COIBuildConfiguration();

                    NodeList nBuildConfigurations = eProject.getElementsByTagName(COIConstants.CWS_BUILD_CONFIGURATION);

                    for (int j = 0; j < nBuildConfigurations.getLength(); j++) {

                        Node nBuildConfiguration = nBuildConfigurations.item(j);

                        if (nBuildConfiguration.getNodeName().equals(COIConstants.CWS_BUILD_CONFIGURATION) && nBuildConfiguration.getNodeType() == Node.ELEMENT_NODE) {

                            Element eBuildConfiguration = (Element) nBuildConfiguration;

                            coiBuildConfiguration.setActive(eBuildConfiguration.getAttribute(COIConstants.CWS_ACTIVE));

                            COIConfiguration coiConfigurationDebug = this.getCOIConfiguration(eBuildConfiguration, COIConstants.BUILD_CONFIGURATION_DEBUG);
                            coiBuildConfiguration.setDebug(coiConfigurationDebug);

                            COIConfiguration coiConfigurationRelease = this.getCOIConfiguration(eBuildConfiguration, COIConstants.BUILD_CONFIGURATION_RELEASE);
                            coiBuildConfiguration.setRelease(coiConfigurationRelease);

                            COIConfiguration coiConfigurationCustom = this.getCOIConfiguration(eBuildConfiguration, COIConstants.BUILD_CONFIGURATION_CUSTOM);
                            coiBuildConfiguration.setCustom(coiConfigurationCustom);
                        }
                    }

                    coiProject.setBuildConfiguration(coiBuildConfiguration);

                    List<COIFolder> coiFolders = new ArrayList<>();

                    NodeList nFolders = eProject.getElementsByTagName(COIConstants.CWS_FOLDER);

                    for (int j = 0; j < nFolders.getLength(); j++) {

                        Node nFolder = nFolders.item(j);

                        if (nFolder.getNodeName().equals(COIConstants.CWS_FOLDER) && nFolder.getNodeType() == Node.ELEMENT_NODE) {

                            Element eFolder = (Element) nFolder;

                            COIFolder coiFolder = new COIFolder();
                            coiFolder.setName(eFolder.getAttribute(COIConstants.CWS_NAME));
                            coiFolder.setPath(eFolder.getAttribute(COIConstants.CWS_PATH));
                            coiFolder.setTypeOfCOI(COIEnum.FOLDER);

                            List<COIFile> coiFiles = new ArrayList<>();

                            NodeList nFiles = eFolder.getElementsByTagName(COIConstants.CWS_FILE);

                            for (int k = 0; k < nFiles.getLength(); k++) {

                                Node nFile = nFiles.item(k);

                                if (nFile.getNodeName().equals(COIConstants.CWS_FILE) && nFile.getNodeType() == Node.ELEMENT_NODE) {

                                    Element eFile = (Element) nFile;

                                    COIFile coiFile = new COIFile();
                                    coiFile.setName(eFile.getAttribute(COIConstants.CWS_NAME));
                                    coiFile.setPath(eFile.getAttribute(COIConstants.CWS_PATH));

                                    if (coiFile.getPath() != null) {
                                        int l = coiFile.getPath().lastIndexOf(COIConstants.COMMA);

                                        if (l > 0) {
                                            coiFile.setExtension(coiFile.getPath().substring(l + 1));
                                        }
                                    }

                                    coiFile.setTypeOfCOI(COIEnum.FILE);

                                    coiFiles.add(coiFile);
                                }
                            }
                            coiFolder.setItems(coiFiles);
                            coiFolders.add(coiFolder);
                        }
                    }
                    coiProject.setItems(coiFolders);
                    coiProjects.add(coiProject);
                }
            }
            coiWorkspace.setItems(coiProjects);
            return coiWorkspace;

        } catch (Exception e) {
            Logger.getLogger(COIServiceImpl.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    /**
     * Method return text from file situated by file path.
     *
     * @param coiObject File without text.
     * @return File with text.
     */
    private COIObject addTextToCOIFile(COIObject coiObject) {

        File file = new File(coiObject.getPath());
        COIFile coiFile = (COIFile) coiObject;

        StringBuilder sb = new StringBuilder();
        BufferedReader bufferReader = null;

        try {
            FileReader inputFile = new FileReader(file);
            bufferReader = new BufferedReader(inputFile);
            String line;

            while ((line = bufferReader.readLine()) != null) {
                sb.append(line).append(System.lineSeparator());
            }

            coiFile.setText(sb.toString());
        } catch (IOException e) {
            Logger.getLogger(COIServiceImpl.class.getName()).log(Level.SEVERE, null, e);
            coiFile.setText(COIConstants.EMPTY);
        } finally {
            try {
                bufferReader.close();
            } catch (IOException e) {
                Logger.getLogger(COIServiceImpl.class.getName()).log(Level.SEVERE, null, e);
                coiFile.setText(COIConstants.EMPTY);
            }
        }

        return coiFile;
    }

    /**
     * Method update workspace file after change in workspace.
     *
     * @param coiWorkspace Workspace file.
     */
    private void localUpdateWorkspace(COIWorkspace coiWorkspace) {

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            Element workspace = doc.createElement(COIConstants.CWS_WORKSPACE);
            doc.appendChild(workspace);
            workspace.setAttribute(COIConstants.CWS_PATH, coiWorkspace.getPath());
            workspace.setAttribute(COIConstants.CWS_NAME, coiWorkspace.getName());

            Element cwsVersion = doc.createElement(COIConstants.CWS_VERSION);
            cwsVersion.appendChild(doc.createTextNode(COIConstants.CWS_VERSION_NUMBER));
            workspace.appendChild(cwsVersion);

            for (COIProject coiProject : coiWorkspace.getItems()) {

                Element project = doc.createElement(COIConstants.CWS_PROJECT);
                workspace.appendChild(project);
                project.setAttribute(COIConstants.CWS_PATH, coiProject.getPath());
                project.setAttribute(COIConstants.CWS_NAME, coiProject.getName());

                Element buildConfiguration = doc.createElement(COIConstants.CWS_BUILD_CONFIGURATION);
                project.appendChild(buildConfiguration);
                buildConfiguration.setAttribute(COIConstants.CWS_ACTIVE, coiProject.getBuildConfiguration().getActive());

                Element debug = doc.createElement(COIConstants.BUILD_CONFIGURATION_DEBUG);
                buildConfiguration.appendChild(debug);
                this.setCOIConfiguration(doc, debug, coiProject, COIConstants.BUILD_CONFIGURATION_DEBUG);

                Element release = doc.createElement(COIConstants.BUILD_CONFIGURATION_RELEASE);
                buildConfiguration.appendChild(release);
                this.setCOIConfiguration(doc, release, coiProject, COIConstants.BUILD_CONFIGURATION_RELEASE);

                Element custom = doc.createElement(COIConstants.BUILD_CONFIGURATION_CUSTOM);
                buildConfiguration.appendChild(custom);
                this.setCOIConfiguration(doc, custom, coiProject, COIConstants.BUILD_CONFIGURATION_CUSTOM);

                for (COIFolder coiFolder : coiProject.getItems()) {

                    Element folder = doc.createElement(COIConstants.CWS_FOLDER);
                    project.appendChild(folder);
                    folder.setAttribute(COIConstants.CWS_PATH, coiFolder.getPath());
                    folder.setAttribute(COIConstants.CWS_NAME, coiFolder.getName());

                    for (COIFile coiFile : coiFolder.getItems()) {

                        Element file = doc.createElement(COIConstants.CWS_FILE);
                        folder.appendChild(file);
                        file.setAttribute(COIConstants.CWS_PATH, coiFile.getPath());
                        file.setAttribute(COIConstants.CWS_NAME, coiFile.getName());
                    }
                }
            }

            doc.getDocumentElement().normalize();

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(coiWorkspace.getPath().substring(0, coiWorkspace.getPath().length() - 1) + COIConstants.CWS_EXTENSION));
            transformer.transform(source, result);

        } catch (ParserConfigurationException | TransformerException e) {
            Logger.getLogger(COIServiceImpl.class.getName()).log(Level.SEVERE, null, e);
        }

    }

    /**
     * Method writes build configuration to workspace file.
     *
     * @param doc Document for write.
     * @param type Type of object.
     * @param coiProject Project.
     * @param configurationType Type of configuration.
     */
    private void setCOIConfiguration(Document doc, Element type, COIProject coiProject, String configurationType) {

        Element compiler = doc.createElement(COIConstants.CWS_COMPILER);
        type.appendChild(compiler);

        Element linker = doc.createElement(COIConstants.CWS_LINKER);
        type.appendChild(linker);
        
        Element global = doc.createElement(COIConstants.CWS_GLOBAL);
        type.appendChild(global);

        Element compilerOptions = doc.createElement(COIConstants.CWS_OPTIONS);
        compiler.appendChild(compilerOptions);

        Element compilerIncludeDirectories = doc.createElement(COIConstants.CWS_INCLUDE_DIRECTORIES);
        compiler.appendChild(compilerIncludeDirectories);

        Element compilerPreprocessors = doc.createElement(COIConstants.CWS_PREPROCESSORS);
        compiler.appendChild(compilerPreprocessors);

        Element linkerOptions = doc.createElement(COIConstants.CWS_OPTIONS);
        linker.appendChild(linkerOptions);

        Element linkerLibraryPaths = doc.createElement(COIConstants.CWS_LIBRARY_PATHS);
        linker.appendChild(linkerLibraryPaths);

        Element linkerLibraryNames = doc.createElement(COIConstants.CWS_LIBRARY_NAMES);
        linker.appendChild(linkerLibraryNames);

        COIConfiguration coiConfiguration;

        switch (configurationType) {
            case COIConstants.BUILD_CONFIGURATION_DEBUG:
                coiConfiguration = coiProject.getBuildConfiguration().getDebug();
                break;

            case COIConstants.BUILD_CONFIGURATION_RELEASE:
                coiConfiguration = coiProject.getBuildConfiguration().getRelease();
                break;

            case COIConstants.BUILD_CONFIGURATION_CUSTOM:
                coiConfiguration = coiProject.getBuildConfiguration().getCustom();
                break;
            default:
                coiConfiguration = new COIConfiguration();
        }
        
        this.setOptionListFromBuildConfiguration(doc, compilerOptions, coiConfiguration.getCompiler().getOptions());
        this.setOptionListFromBuildConfiguration(doc, compilerIncludeDirectories, coiConfiguration.getCompiler().getIncludeDirectories());
        this.setOptionListFromBuildConfiguration(doc, compilerPreprocessors, coiConfiguration.getCompiler().getPreprocessors());
        this.setOptionListFromBuildConfiguration(doc, linkerOptions, coiConfiguration.getLinker().getOptions());
        this.setOptionListFromBuildConfiguration(doc, linkerLibraryPaths, coiConfiguration.getLinker().getLibraryPaths());
        this.setOptionListFromBuildConfiguration(doc, linkerLibraryNames, coiConfiguration.getLinker().getLibraryNames());
        
        Element dPtx = doc.createElement(COIConstants.CWS_PTX);
        global.appendChild(dPtx);
        dPtx.setAttribute(COIConstants.CWS_VALUE, coiConfiguration.getGlobal().getCcPtx());
        
        Element dGpu = doc.createElement(COIConstants.CWS_GPU);
        global.appendChild(dGpu);
        dGpu.setAttribute(COIConstants.CWS_VALUE, coiConfiguration.getGlobal().getCcGpu());
        
        Element dArgument = doc.createElement(COIConstants.CWS_ARGUMENT);
        global.appendChild(dArgument);
        dArgument.setAttribute(COIConstants.CWS_VALUE, coiConfiguration.getGlobal().getArguments());
    }

    /**
     * Method writes option list to workspace file.
     *
     * @param doc Document for write.
     * @param type Type of object.
     * @param typeList Type of list.
     */
    private void setOptionListFromBuildConfiguration(Document doc, Element type, List<String> typeList) {

        for (String option : typeList) {
            Element dOption = doc.createElement(COIConstants.CWS_OPTION);
            type.appendChild(dOption);
            dOption.setAttribute(COIConstants.CWS_VALUE, option);
        }
    }

    /**
     * Method reads build configuration from workspace file.
     *
     * @param eBuildConfiguration Element to read.
     * @param configurationType Type of configuration.
     * @return Build configuration.
     */
    private COIConfiguration getCOIConfiguration(Element eBuildConfiguration, String configurationType) {

        COIConfiguration coiConfiguration = new COIConfiguration();
        coiConfiguration.setType(configurationType);

        NodeList nTypes = eBuildConfiguration.getElementsByTagName(configurationType);

        for (int k = 0; k < nTypes.getLength(); k++) {

            Node nType = nTypes.item(k);

            if (nType.getNodeName().equals(configurationType) && nType.getNodeType() == Node.ELEMENT_NODE) {

                Element eType = (Element) nType;

                COICompiler coiCompiler = new COICompiler();

                NodeList nCompilers = eType.getElementsByTagName(COIConstants.CWS_COMPILER);

                for (int l = 0; l < nCompilers.getLength(); l++) {

                    Node nCompiler = nCompilers.item(l);

                    if (nCompiler.getNodeName().equals(COIConstants.CWS_COMPILER) && nCompiler.getNodeType() == Node.ELEMENT_NODE) {

                        Element eCompiler = (Element) nCompiler;

                        List<String> options = this.getOptionListFromBuildConfiguration(eCompiler, COIConstants.CWS_OPTIONS);
                        coiCompiler.setOptions(options);

                        List<String> includeDirectories = this.getOptionListFromBuildConfiguration(eCompiler, COIConstants.CWS_INCLUDE_DIRECTORIES);
                        coiCompiler.setIncludeDirectories(includeDirectories);

                        List<String> preprocessors = this.getOptionListFromBuildConfiguration(eCompiler, COIConstants.CWS_PREPROCESSORS);
                        coiCompiler.setPreprocessors(preprocessors);
                    }
                }

                coiConfiguration.setCompiler(coiCompiler);

                COILinker coiLinker = new COILinker();

                NodeList nLinkers = eType.getElementsByTagName(COIConstants.CWS_LINKER);

                for (int l = 0; l < nLinkers.getLength(); l++) {

                    Node nLinker = nLinkers.item(l);

                    if (nLinker.getNodeName().equals(COIConstants.CWS_LINKER) && nLinker.getNodeType() == Node.ELEMENT_NODE) {

                        Element eLinker = (Element) nLinker;

                        List<String> options = this.getOptionListFromBuildConfiguration(eLinker, COIConstants.CWS_OPTIONS);
                        coiLinker.setOptions(options);

                        List<String> libraryPaths = this.getOptionListFromBuildConfiguration(eLinker, COIConstants.CWS_LIBRARY_PATHS);
                        coiLinker.setLibraryPaths(libraryPaths);

                        List<String> libraryNames = this.getOptionListFromBuildConfiguration(eLinker, COIConstants.CWS_LIBRARY_NAMES);
                        coiLinker.setLibraryNames(libraryNames);
                    }
                }

                coiConfiguration.setLinker(coiLinker);
                
                COIGlobal coiGlobal = new COIGlobal();

                NodeList nGlobals = eType.getElementsByTagName(COIConstants.CWS_GLOBAL);

                for (int l = 0; l < nGlobals.getLength(); l++) {

                    Node nGlobal = nGlobals.item(l);

                    if (nGlobal.getNodeName().equals(COIConstants.CWS_GLOBAL) && nGlobal.getNodeType() == Node.ELEMENT_NODE) {

                        Element eGlobal = (Element) nGlobal;

                        NodeList nPtxs = eGlobal.getElementsByTagName(COIConstants.CWS_PTX);

                        for (int n = 0; n < nPtxs.getLength(); n++) {

                            Node nPtx = nPtxs.item(n);

                            if (nPtx.getNodeName().equals(COIConstants.CWS_PTX) && nPtx.getNodeType() == Node.ELEMENT_NODE) {

                                Element ePtx = (Element) nPtx;

                                coiGlobal.setCcPtx(ePtx.getAttribute(COIConstants.CWS_VALUE));
                            }
                        }
                        
                        NodeList nGpus = eGlobal.getElementsByTagName(COIConstants.CWS_GPU);

                        for (int n = 0; n < nGpus.getLength(); n++) {

                            Node nGpu = nGpus.item(n);

                            if (nGpu.getNodeName().equals(COIConstants.CWS_GPU) && nGpu.getNodeType() == Node.ELEMENT_NODE) {

                                Element eGpu = (Element) nGpu;

                                coiGlobal.setCcGpu(eGpu.getAttribute(COIConstants.CWS_VALUE));
                            }
                        }
                        
                        NodeList nArguments = eGlobal.getElementsByTagName(COIConstants.CWS_ARGUMENT);

                        for (int n = 0; n < nArguments.getLength(); n++) {

                            Node nArgument = nArguments.item(n);

                            if (nArgument.getNodeName().equals(COIConstants.CWS_ARGUMENT) && nArgument.getNodeType() == Node.ELEMENT_NODE) {

                                Element eArgument = (Element) nArgument;

                                coiGlobal.setArguments(eArgument.getAttribute(COIConstants.CWS_VALUE));
                            }
                        }
                    }
                }

                coiConfiguration.setGlobal(coiGlobal);
            }
        }

        return coiConfiguration;
    }

    /**
     * Method reads option list from workspace file.
     *
     * @param eConfigurationType Type of configuration.
     * @param optionType Type of options.
     * @return Option list.
     */
    private List<String> getOptionListFromBuildConfiguration(Element eConfigurationType, String optionType) {

        List<String> optionList = new ArrayList<>();

        NodeList nOptionTypes = eConfigurationType.getElementsByTagName(optionType);

        for (int m = 0; m < nOptionTypes.getLength(); m++) {

            Node nOptionType = nOptionTypes.item(m);

            if (nOptionType.getNodeName().equals(optionType) && nOptionType.getNodeType() == Node.ELEMENT_NODE) {

                Element eOptionType = (Element) nOptionType;

                NodeList nOptions = eOptionType.getElementsByTagName(COIConstants.CWS_OPTION);

                for (int n = 0; n < nOptions.getLength(); n++) {

                    Node nOption = nOptions.item(n);

                    if (nOption.getNodeName().equals(COIConstants.CWS_OPTION) && nOption.getNodeType() == Node.ELEMENT_NODE) {

                        Element eOption = (Element) nOption;

                        optionList.add(eOption.getAttribute(COIConstants.CWS_VALUE));
                    }
                }

            }
        }

        return optionList;
    }

    /**
     * Method changes absolute path in CMake files to relative for building on
     * remote system.
     *
     * @param projectFolder Active project folder.
     * @param cMakeFileName Name of file to change.
     * @throws IOException If process of reading or writing file failed.
     */
    private void editCMakeFileToRelativePaths(String projectFolder, String cMakeFileName) throws IOException {

        File cmakeFile = new File(projectFolder + "build/" + cMakeFileName);

        System.out.println("COIServiceImpl LOG [Making relative path in " + cmakeFile.getPath() + "]");

        int cMakeTimer = 5;

        while (!cmakeFile.exists()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Logger.getLogger(COIServiceImpl.class.getName()).log(Level.SEVERE, null, e);
            }

            cMakeTimer--;

            if (cMakeTimer == 0) {
                return;
            }
        }

        String cmakeFileText = "";

        try (FileReader inputFile = new FileReader(cmakeFile)) {
            try (BufferedReader bufferReader = new BufferedReader(inputFile)) {

                String line;
                StringBuilder sb = new StringBuilder();

                while ((line = bufferReader.readLine()) != null) {
                    sb.append(line).append(System.lineSeparator());
                }

                cmakeFileText = sb.toString();
            }
        }

        File workspaceFile = new File(projectFolder).getParentFile();
        String workspacePath = workspaceFile.getAbsolutePath();

//		projectFolder = projectFolder.replace("\\", "/");
        workspacePath = workspacePath.replace("\\", "/");

        System.out.println("COIServiceImpl LOG [Replacing path " + workspacePath + " in file " + cmakeFile.getPath() + " to relative ../..]");

//		cmakeFileText = cmakeFileText.replace(projectFolder, "../");	
        cmakeFileText = cmakeFileText.replace(workspacePath, "../..");

        try (PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(cmakeFile)))) {
            printWriter.print(cmakeFileText);
            printWriter.flush();
        }
    }

    /**
     * Method removes file or directory. If directory contains other files and
     * directories, these files and directories are deleted too.
     *
     * @param directory File or directory to remove.
     */
    public static void removeAll(File directory) {

        if (directory.isDirectory()) {
            for (File subDirectory : directory.listFiles()) {
                removeAll(subDirectory);
            }
        }

        directory.delete();
    }

    /**
     * Escape an html string. Escaping data received from the client helps to
     * prevent cross-site script vulnerabilities.
     *
     * @param html The html string to escape.
     * @return String The escaped string.
     */
    @SuppressWarnings("unused")
    private String escapeHtml(String html) {
        if (html == null) {
            return null;
        }
        return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(
                ">", "&gt;");
    }

    /**
     * Method format outline text.
     *
     * @param text Outline text to format.
     * @return String Formatted text to outline.
     */
    @SuppressWarnings("unused")
    private String formatOutlineText(String text) {

        if (text.contains("//")) {
            text = text.substring(0, text.indexOf("//"));
        }

        if (text.contains("{")) {
            text = text.substring(0, text.indexOf("{"));
        }

        String textToReplace;

        while (text.contains("/*") && text.contains("*/")) {
            if (text.indexOf("/*") > text.indexOf("*/")) {
                text = text.substring(text.indexOf("*/") + 2);
            } else {
                textToReplace = text.substring(text.indexOf("/*"), text.indexOf("*/") + 2);
                text = text.replace(textToReplace, "");
            }
        }

        if (text.contains("/*") && !text.contains("*/")) {
            text = text.substring(0, text.indexOf("/*"));
        }

        if (text.contains("*/") && !text.contains("/*")) {
            text = text.substring(text.indexOf("*/") + 2);
        }

        text = text.replace(";", "");

        while (text.startsWith(" ")) {
            text = text.substring(1);
        }

        while (text.endsWith(" ")) {
            text = text.substring(0, text.length() - 1);
        }

        return text;
    }
}
