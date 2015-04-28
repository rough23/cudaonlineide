package cz.utb.fai.cudaonlineide.shared.constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * On-line IDE constants.
 * 
 * @author Belanec
 *
 */
public class COIConstants {

	public static final String PANEL_PROJECT_EXPLORER = "Project explorer";
	public static final String PANEL_OUTLINE = "Outline";
	public static final String PANEL_CONSOLE = "Console";
	public static final String PANEL_SERVER_LOGS = "Server logs";
	public static final String PANEL_CONNECTION_LOGS = "Connection logs";
	
	public static final String MENU_OUTLINE = "Outline";
	public static final String MENU_CONSOLE = "Console";
	public static final String MENU_UPLOAD_WORKSPACE = "Upload workspace";
	public static final String MENU_DOWNLOAD_WORKSPACE = "Download workspace";
	public static final String MENU_DELETE_WORKSPACE = "Delete workspace";
	public static final String MENU_SAVE_FILE = "Save file";
	public static final String MENU_NEW_WORKSPACE = "New workspace";
	public static final String MENU_CLOSE_WORKSPACE = "Close workspace";
	public static final String MENU_OPEN_WORKSPACE = "Open workspace";
	public static final String MENU_CMAKE = "Cmake";
	public static final String MENU_CREATE_MAKEFILE = "Create Makefile";
	public static final String MENU_SET_ACTIVE_PROJECT = "Set active project";
	public static final String MENU_CLOSE_FILE = "Close file";
	public static final String MENU_OPEN_CMAKELISTS = "Open CMakeLists";
	public static final String MENU_GENERATE_CMAKELISTS = "Generate CMakeLists";
	public static final String MENU_EXECUTABLE = "Executable";
	public static final String MENU_STATIC_LIB = "Static library";
	public static final String MENU_SHARED_LIB = "Shared library";
	public static final String MENU_SOFT_TABS = "Soft tabs";
	public static final String MENU_H_SCROLLBAR = "Horizontal scroll";
	public static final String MENU_SHOW_GUTTER = "Line numbers";
	public static final String MENU_HIGHLIGHT = "Highlight selected word";
	public static final String MENU_READ_ONLY = "Read only";
	public static final String MENU_AUTOCOMPLETE = "Autocomplete";
	public static final String MENU_SET_TAB_SIZE = "Set tab size";
	public static final String MENU_GO_TO_LINE = "Go to line";
	public static final String MENU_INSERT_AT_LINE = "Insert at line";
	public static final String MENU_SERVER_LOGS = "Server logs";
	public static final String MENU_CONNECTION_LOGS = "Connection logs";
	public static final String MENU_LOGOUT = "Logout";
	public static final String MENU_BUILD = "Build";
	public static final String MENU_RUN = "Run";
	public static final String MENU_PROPERTIES = "Properties";
	public static final String MENU_ABOUT = "About";
	public static final String MENU_HELP = "Help";
	
	public static final String MENUTOOLBAR_LOG = "Log";
	public static final String MENUTOOLBAR_USER = "User";
	public static final String MENUTOOLBAR_WORKSPACE = "Workspace";
	public static final String MENUTOOLBAR_EDITOR = "Editor";
	public static final String MENUTOOLBAR_PROJECT = "Project";
	public static final String MENUTOOLBAR_FILE = "File";
	public static final String MENUTOOLBAR_WINDOW = "Window";
	public static final String MENUTOOLBAR_HELP = "Help";	
	public static final String MENUTOOLBAR_BUILD = "Build";
	public static final String MENUTOOLBAR_CONFIGURATION = "Configuration";
	
	public static final String BUTTON_CREATE = "Create";
	public static final String BUTTON_CANCEL = "Cancel";
	public static final String BUTTON_SAVE = "Save";
	public static final String BUTTON_EDIT = "Edit";
	public static final String BUTTON_OK = "OK";
	public static final String BUTTON_LOGIN = "Login";
	
	public static final String KEY_COI_DATA_PROPERTIES = "path";
	public static final String KEY_FILE_DATA_PROPERTIES = "output";
	
	public static final String INFO_OPEN = "Open";
	public static final String INFO_DELETE = "Delete";
	public static final String INFO_CLOSE = "Close";
	public static final String INFO_SWITCH = "Switch";

	public static final String CWS_WORKSPACE = "workspace";
	public static final String CWS_PROJECT = "project";
	public static final String CWS_FOLDER = "folder";	
	public static final String CWS_FILE = "file";
	public static final String CWS_BUILD_CONFIGURATION = "buildConfiguration";
	public static final String CWS_ACTIVE = "active";
	public static final String CWS_OPTIONS = "options";
	public static final String CWS_OPTION = "option";
	public static final String CWS_INCLUDE_DIRECTORIES = "include_directories";
	public static final String CWS_PREPROCESSORS = "preprocessors";
	public static final String CWS_LIBRARY_PATHS = "library_paths";
	public static final String CWS_LIBRARY_NAMES = "library_names";
	public static final String CWS_COMPILER = "compiler";
	public static final String CWS_LINKER = "linker";
	public static final String CWS_VALUE = "value";
	public static final String CWS_NAME = "name";
	public static final String CWS_PATH = "path";
	public static final String CWS_VERSION = "cws_ver";
	public static final String CWS_VERSION_NUMBER = "2.0";
	public static final String CWS_EXTENSION = "_workspace.cws2";

	public static final String CUF_USERS = "users";
	public static final String CUF_USER = "user";
	public static final String CUF_NAME = "name";
	public static final String CUF_PASSWORD = "password";
	public static final String CUF_SID = "sid";
	
	public static final String BUILD_CONFIGURATION_DEBUG = "Debug";
	public static final String BUILD_CONFIGURATION_RELEASE = "Release";
	public static final String BUILD_CONFIGURATION_CUSTOM = "Custom";
	
	public static final List<String> BUILD_CONFIGURATION_COMPILER_OPTIONS_DEBUG = Arrays.asList("-g", "-O0", "-Wall");
	public static final List<String> BUILD_CONFIGURATION_COMPILER_OPTIONS_RELEASE = Arrays.asList("-O2", "-Wall");
	public static final List<String> BUILD_CONFIGURATION_LINKER_OPTIONS = new ArrayList<String>();
	
	public static final String ZIP_EXTENSION = ".zip";

	public static final String SIZE_100_PERCENTAGE = "100%";
	
	public static final String COMMA = ".";
	public static final String FWD_SLASH = "/";
	public static final String EMPTY = "";	
	public static final String SPACE = " ";
	public static final String LINE_SEPARATOR = "\n";
}
