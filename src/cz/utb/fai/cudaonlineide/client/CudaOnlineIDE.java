package cz.utb.fai.cudaonlineide.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cz.utb.fai.cudaonlineide.client.context.TreeContextMenu;
import cz.utb.fai.cudaonlineide.client.dto.COIData;
import cz.utb.fai.cudaonlineide.client.dto.COIDataProperties;
import cz.utb.fai.cudaonlineide.client.dto.FileDataProperties;
import cz.utb.fai.cudaonlineide.client.popup.Login;
import cz.utb.fai.cudaonlineide.client.popup.PopUpWindow;
import cz.utb.fai.cudaonlineide.client.service.coi.COIService;
import cz.utb.fai.cudaonlineide.client.service.coi.COIServiceAsync;
import cz.utb.fai.cudaonlineide.client.service.login.LoginService;
import cz.utb.fai.cudaonlineide.client.service.login.LoginServiceAsync;
import cz.utb.fai.cudaonlineide.client.utils.COIBrowserConsole;
import cz.utb.fai.cudaonlineide.client.utils.MenuToolbarIcons;
import cz.utb.fai.cudaonlineide.shared.constants.COIConstants;
import cz.utb.fai.cudaonlineide.shared.constants.WorkspaceConstants;
import cz.utb.fai.cudaonlineide.shared.dto.COIEnum;
import cz.utb.fai.cudaonlineide.shared.dto.COIFile;
import cz.utb.fai.cudaonlineide.shared.dto.COIFolder;
import cz.utb.fai.cudaonlineide.shared.dto.COIObject;
import cz.utb.fai.cudaonlineide.shared.dto.COIProject;
import cz.utb.fai.cudaonlineide.shared.dto.COIWorkspace;
import cz.utb.fai.cudaonlineide.shared.dto.COIFileData;
import cz.utb.fai.cudaonlineide.shared.dto.COIUser;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.Style.LayoutRegion;
import com.sencha.gxt.core.client.resources.ThemeStyles;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.IconProvider;
import com.sencha.gxt.data.shared.StringLabelProvider;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.box.AutoProgressMessageBox;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.button.ButtonGroup;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.Viewport;
import com.sencha.gxt.widget.core.client.event.BeforeShowContextMenuEvent;
import com.sencha.gxt.widget.core.client.event.CloseEvent;
import com.sencha.gxt.widget.core.client.event.CloseEvent.CloseHandler;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent.DialogHideHandler;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.BeforeShowContextMenuEvent.BeforeShowContextMenuHandler;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.SimpleComboBox;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.menu.CheckMenuItem;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuBar;
import com.sencha.gxt.widget.core.client.menu.MenuBarItem;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.menu.SeparatorMenuItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;
import com.sencha.gxt.widget.core.client.tree.Tree;

import edu.ycp.cs.dh.acegwt.client.ace.AceCommandDescription;
import edu.ycp.cs.dh.acegwt.client.ace.AceEditor;
import edu.ycp.cs.dh.acegwt.client.ace.AceEditorCallback;
import edu.ycp.cs.dh.acegwt.client.ace.AceEditorCursorPosition;
import edu.ycp.cs.dh.acegwt.client.ace.AceEditorMode;
import edu.ycp.cs.dh.acegwt.client.ace.AceEditorTheme;

/**
 * Client-side class for constructing On-line IDE and providing handlers and
 * methods for working with this IDE.
 *
 * @author Belanec
 *
 */
public class CudaOnlineIDE implements IsWidget, EntryPoint {

    // ACTIVE files
    public static String ACTIVE_CUDA_FOLDER = COIConstants.EMPTY;
    public static String ACTIVE_CUDA_WORKSPACE_FILE = COIConstants.EMPTY;
    public static COIProject ACTIVE_CUDA_COI_PROJECT = null;
    public static COIWorkspace ACTIVE_CUDA_COI_WORKSPACE = null;
    public static String ACTIVE_USER = COIConstants.EMPTY;

    // RPC services
    public static COIServiceAsync coiService = GWT.create(COIService.class);
    public static LoginServiceAsync loginService = GWT.create(LoginService.class);

    // Static widgets that must be available from other client-side class
    public static BorderLayoutContainer coiAppContainer;
    public static TabPanel fileTabPanel;
    public static TreeStore<COIData> coiDataTreeStore;
    public static Tree<COIData, String> coiDataTree;
    public static TreeStore<COIFileData> fileDataTreeStore;
    public static SimpleComboBox<String> buildConfigurationCombo;
    public static ContentPanel westPanel;
    public static ContentPanel eastPanel;
    public static AceEditor aceConsole;

    //CUDA On-line IDE APP container global properties
    public static boolean westContainerOpen;
    public static boolean eastContainerOpen;
    public static boolean southContainerOpen;
    public static boolean northContainerOpen;

    // Console editor global properties
    public static AceEditorTheme aceActualTheme = AceEditorTheme.ECLIPSE;
    public static boolean aceSoftTabs = true;
    public static boolean aceHorizontalScroll = true;
    public static boolean aceShowGutter = true;
    public static boolean aceHighlightSelectedWord = true;
    public static boolean aceReadOnly = false;
    public static boolean aceAutocomplete = true;
    public static int aceTabSize = 4;
    public static int aceFontSize = 14;

    // Logging to browser console.
    protected COIBrowserConsole browserConsole = new COIBrowserConsole();

    /**
     * Method is called when page is loaded and call other method for building
     * on-line IDE.
     */
    public void onModuleLoad() {

        Viewport viewport = new Viewport();
        viewport.add(asWidget());
        RootPanel.get().add(viewport);

        String sessionID = Cookies.getCookie(COIConstants.CUF_SID);

        if (sessionID == null) {
            this.displayLoginWindow();
        } else {
            this.checkWithServerIfSessionIdIsStillLegal(sessionID);
        }
    }

    /**
     * Method builds complete CUDA On-line IDE container widget to show.
     */
    public Widget asWidget() {

        SimpleContainer mainContainer = new SimpleContainer();

        CudaOnlineIDE.coiAppContainer = new BorderLayoutContainer();
        CudaOnlineIDE.coiAppContainer.setBorders(true);

        this.getNorthPanel();
        this.getWestPanel();
        this.getCenterPanel();
        this.getEastPanel();
        this.getSouthPanel();

        mainContainer.add(CudaOnlineIDE.coiAppContainer);

        return mainContainer;
    }

    /**
     * Method builds north panel of application.
     */
    private void getNorthPanel() {

        ContentPanel northPanel = new ContentPanel();
        northPanel.setHeaderVisible(false);

        FlexTable table = new FlexTable();
        table.setWidget(0, 0, this.getCOIMenu());
        table.setWidget(1, 0, this.getCOIToolbar());
        northPanel.add(table);

        BorderLayoutData northData = new BorderLayoutData(115);
        northData.setMargins(new Margins(8));
        northData.setCollapsible(true);
        northData.setCollapseMini(true);
        northData.setSplit(true);
        CudaOnlineIDE.northContainerOpen = true;

        CudaOnlineIDE.coiAppContainer.setNorthWidget(northPanel, northData);
    }

    /**
     * Method builds west panel of application.
     */
    private void getWestPanel() {

        CudaOnlineIDE.westPanel = new ContentPanel();
        CudaOnlineIDE.westPanel.setHeadingText(COIConstants.PANEL_PROJECT_EXPLORER);

        BorderLayoutData westData = new BorderLayoutData(250);
        westData.setCollapsible(true);
        westData.setSplit(true);
        westData.setCollapseMini(true);
        westData.setMargins(new Margins(0, 8, 0, 5));
        westData.setMinSize(200);
        CudaOnlineIDE.westContainerOpen = true;

        CudaOnlineIDE.coiAppContainer.setWestWidget(CudaOnlineIDE.westPanel, westData);
    }

    /**
     * Method builds center panel of application.
     */
    private void getCenterPanel() {

        ContentPanel centerPanel = new ContentPanel();
        centerPanel.setResize(true);
        centerPanel.setHeaderVisible(false);
        centerPanel.add(this.getCOIFileArea());

        MarginData centerData = new MarginData();

        CudaOnlineIDE.coiAppContainer.setCenterWidget(centerPanel, centerData);
    }

    /**
     * Method builds east panel of application.
     */
    private void getEastPanel() {

        CudaOnlineIDE.eastPanel = new ContentPanel();
        CudaOnlineIDE.eastPanel.setHeadingText(COIConstants.PANEL_OUTLINE);

        BorderLayoutData eastData = new BorderLayoutData(300);
        eastData.setMargins(new Margins(0, 5, 0, 8));
        eastData.setCollapsible(true);
        eastData.setCollapseMini(true);
        eastData.setCollapsed(true);
        eastData.setSplit(true);
        eastData.setMinSize(150);
        CudaOnlineIDE.eastContainerOpen = true;

        CudaOnlineIDE.coiAppContainer.setEastWidget(CudaOnlineIDE.eastPanel, eastData);
    }

    /**
     * Method builds south panel of application.
     */
    private void getSouthPanel() {

        ContentPanel southPanel = new ContentPanel();
        southPanel.setHeaderVisible(false);
        southPanel.add(this.getCOIConsoleArea());

        BorderLayoutData southData = new BorderLayoutData(200);
        southData.setMargins(new Margins(8));
        southData.setCollapsible(true);
        southData.setCollapseMini(true);
        southData.setMinSize(200);
        CudaOnlineIDE.southContainerOpen = true;

        CudaOnlineIDE.coiAppContainer.setSouthWidget(southPanel, southData);
    }

    /**
     * Method creates menu for CUDA On-line IDE application.
     *
     * @return Menu as widget.
     */
    private Widget getCOIMenu() {

        // BUILDING FILE MENU
        MenuItem fileItem1 = new MenuItem(COIConstants.MENU_NEW_WORKSPACE, MenuToolbarIcons.PROVIDER.menuNew());
        MenuItem fileItem2 = new MenuItem(COIConstants.MENU_OPEN_WORKSPACE, MenuToolbarIcons.PROVIDER.menuOpen());
        MenuItem fileItem3 = new MenuItem(COIConstants.MENU_CLOSE_WORKSPACE, MenuToolbarIcons.PROVIDER.menuClose());
        MenuItem fileItem4 = new MenuItem(COIConstants.MENU_DELETE_WORKSPACE, MenuToolbarIcons.PROVIDER.menuDelete());
        MenuItem fileItem5 = new MenuItem(COIConstants.MENU_UPLOAD_WORKSPACE, MenuToolbarIcons.PROVIDER.menuUpload());
        MenuItem fileItem6 = new MenuItem(COIConstants.MENU_DOWNLOAD_WORKSPACE, MenuToolbarIcons.PROVIDER.menuDownload());
        MenuItem fileItem8 = new MenuItem(COIConstants.MENU_CLOSE_FILE, MenuToolbarIcons.PROVIDER.menuClose());
        MenuItem fileItem7 = new MenuItem(COIConstants.MENU_SAVE_FILE, MenuToolbarIcons.PROVIDER.menuSave());

        Menu fileMenu = new Menu();
        fileMenu.addSelectionHandler(this.getCOIMenuSelectionHandler());
        fileMenu.add(fileItem1);
        fileMenu.add(fileItem2);
        fileMenu.add(fileItem3);
        fileMenu.add(fileItem4);
        fileMenu.add(new SeparatorMenuItem());
        fileMenu.add(fileItem5);
        fileMenu.add(fileItem6);
        fileMenu.add(new SeparatorMenuItem());
        fileMenu.add(fileItem8);
        fileMenu.add(fileItem7);

        // BUILDING EDITOR MENU
        CheckMenuItem editorItem1 = new CheckMenuItem(COIConstants.MENU_SOFT_TABS);
        editorItem1.setChecked(true);
        CheckMenuItem editorItem2 = new CheckMenuItem(COIConstants.MENU_H_SCROLLBAR);
        editorItem2.setChecked(true);
        CheckMenuItem editorItem3 = new CheckMenuItem(COIConstants.MENU_SHOW_GUTTER);
        editorItem3.setChecked(true);
        CheckMenuItem editorItem4 = new CheckMenuItem(COIConstants.MENU_HIGHLIGHT);
        editorItem4.setChecked(true);
        CheckMenuItem editorItem5 = new CheckMenuItem(COIConstants.MENU_READ_ONLY);
        editorItem5.setChecked(false);
        CheckMenuItem editorItem6 = new CheckMenuItem(COIConstants.MENU_AUTOCOMPLETE);
        editorItem6.setChecked(true);
        MenuItem editorItem7 = new MenuItem(COIConstants.MENU_SET_TAB_SIZE, MenuToolbarIcons.PROVIDER.menuTabSize());
        MenuItem editorItem8 = new MenuItem(COIConstants.MENU_GO_TO_LINE, MenuToolbarIcons.PROVIDER.menuGoToLine());
        MenuItem editorItem9 = new MenuItem(COIConstants.MENU_INSERT_AT_LINE, MenuToolbarIcons.PROVIDER.menuInsert());

        Menu editorMenu = new Menu();
        editorMenu.addSelectionHandler(this.getCOIMenuSelectionHandler());
        editorMenu.add(editorItem1);
        editorMenu.add(editorItem2);
        editorMenu.add(editorItem3);
        editorMenu.add(editorItem4);
        editorMenu.add(editorItem5);
        editorMenu.add(editorItem6);
        editorMenu.add(new SeparatorMenuItem());
        editorMenu.add(editorItem7);
        editorMenu.add(editorItem8);
        editorMenu.add(editorItem9);

        // BUILDING PROJECT MENU
        MenuItem projectItem1 = new MenuItem(COIConstants.MENU_SET_ACTIVE_PROJECT, MenuToolbarIcons.PROVIDER.projectActive());
        MenuItem projectItem2 = new MenuItem(COIConstants.MENU_PROPERTIES, MenuToolbarIcons.PROVIDER.menuProperties());

        Menu projectMenu = new Menu();
        projectMenu.addSelectionHandler(this.getCOIMenuSelectionHandler());
        projectMenu.add(projectItem1);
        projectMenu.add(new SeparatorMenuItem());
        projectMenu.add(projectItem2);

        // BUILDING BUILD MENU
        MenuItem buildItem0 = new MenuItem(COIConstants.MENU_CMAKE, MenuToolbarIcons.PROVIDER.menuCmake());
        MenuItem buildItem1 = new MenuItem(COIConstants.MENU_BUILD, MenuToolbarIcons.PROVIDER.menuBuild());
        MenuItem buildItem2 = new MenuItem(COIConstants.MENU_RUN, MenuToolbarIcons.PROVIDER.menuRun());

        Menu cmakeSubMenu = new Menu();
        MenuItem generateCMakeListsItem = new MenuItem(COIConstants.MENU_GENERATE_CMAKELISTS);

        MenuItem openCMakeListsItem = new MenuItem(COIConstants.MENU_OPEN_CMAKELISTS);
        openCMakeListsItem.addSelectionHandler(new SelectionHandler<Item>() {

            /**
             * Menu open CMakeLists.txt handler.
             *
             * @param event Selection event.
             */
            @Override
            public void onSelection(SelectionEvent<Item> event) {
                CudaOnlineIDE.this.openCMakeListsMenuToolbar();
            }
        });

        MenuItem createMakefileItem = new MenuItem(COIConstants.MENU_CREATE_MAKEFILE);
        createMakefileItem.addSelectionHandler(new SelectionHandler<Item>() {

            /**
             * Menu create makefile handler.
             *
             * @param event Selection event.
             */
            @Override
            public void onSelection(SelectionEvent<Item> event) {

                if (CudaOnlineIDE.ACTIVE_CUDA_COI_PROJECT == null) {
                    Info.display("Makefile error", "No active project.");
                    return;
                }

                CudaOnlineIDE.createMakefileMenuToolbar(CudaOnlineIDE.ACTIVE_CUDA_COI_PROJECT);
            }
        });

        Menu generateCMakeListsSubMenu = new Menu();

        MenuItem generateExecutable = new MenuItem(COIConstants.MENU_EXECUTABLE);
        generateExecutable.addSelectionHandler(new SelectionHandler<Item>() {

            /**
             * Menu generate CMakeLists.txt as executable handler.
             *
             * @param event Selection event.
             */
            @Override
            public void onSelection(SelectionEvent<Item> event) {

                if (CudaOnlineIDE.ACTIVE_CUDA_COI_PROJECT == null) {
                    Info.display("CMake error", "No active project.");
                    return;
                }

                CudaOnlineIDE.generateCMakeListsMenuToolbar(CudaOnlineIDE.ACTIVE_CUDA_COI_PROJECT, COIConstants.MENU_EXECUTABLE);
            }
        });

        MenuItem generateStatic = new MenuItem(COIConstants.MENU_STATIC_LIB);
        generateStatic.addSelectionHandler(new SelectionHandler<Item>() {

            /**
             * Menu generate CMakeLists.txt as static library handler.
             *
             * @param event Selection event.
             */
            @Override
            public void onSelection(SelectionEvent<Item> event) {

                if (CudaOnlineIDE.ACTIVE_CUDA_COI_PROJECT == null) {
                    Info.display("CMake error", "No active project.");
                    return;
                }

                CudaOnlineIDE.generateCMakeListsMenuToolbar(CudaOnlineIDE.ACTIVE_CUDA_COI_PROJECT, COIConstants.MENU_STATIC_LIB);
            }
        });
        MenuItem generateShared = new MenuItem(COIConstants.MENU_SHARED_LIB);
        generateShared.addSelectionHandler(new SelectionHandler<Item>() {

            /**
             * Menu generate CMakeLists.txt as shared library handler.
             *
             * @param event Selection event.
             */
            @Override
            public void onSelection(SelectionEvent<Item> event) {

                if (CudaOnlineIDE.ACTIVE_CUDA_COI_PROJECT == null) {
                    Info.display("CMake error", "No active project.");
                    return;
                }

                CudaOnlineIDE.generateCMakeListsMenuToolbar(CudaOnlineIDE.ACTIVE_CUDA_COI_PROJECT, COIConstants.MENU_SHARED_LIB);
            }
        });

        generateCMakeListsSubMenu.add(generateExecutable);

        generateCMakeListsItem.setSubMenu(generateCMakeListsSubMenu);

        cmakeSubMenu.add(generateCMakeListsItem);
        cmakeSubMenu.add(openCMakeListsItem);

        buildItem0.setSubMenu(cmakeSubMenu);

        Menu buildMenu = new Menu();
        buildMenu.addSelectionHandler(this.getCOIMenuSelectionHandler());
        buildMenu.add(buildItem0);
        buildMenu.add(buildItem1);
        buildMenu.add(buildItem2);

        // BUILDING USER MENU
        MenuItem userItem1 = new MenuItem(COIConstants.MENU_LOGOUT, MenuToolbarIcons.PROVIDER.menuLogout());

        Menu userMenu = new Menu();
        userMenu.addSelectionHandler(this.getCOIMenuSelectionHandler());
        userMenu.add(userItem1);

        // BUILDING WINDOW MENU
        CheckMenuItem windowItem1 = new CheckMenuItem(COIConstants.PANEL_PROJECT_EXPLORER);
        windowItem1.setChecked(true);
        CheckMenuItem windowItem2 = new CheckMenuItem(COIConstants.MENU_OUTLINE);
        windowItem2.setChecked(true);
        CheckMenuItem windowItem3 = new CheckMenuItem(COIConstants.MENU_CONSOLE);
        windowItem3.setChecked(true);

        Menu windowMenu = new Menu();
        windowMenu.addSelectionHandler(this.getCOIMenuSelectionHandler());
        windowMenu.add(windowItem1);
        windowMenu.add(windowItem2);
        windowMenu.add(windowItem3);

        // BUILDING HELP MENU
        MenuItem helpItem1 = new MenuItem(COIConstants.MENU_HELP);
        MenuItem helpItem2 = new MenuItem(COIConstants.MENU_CUDA_PROGRAMMING_GUIDE);
        MenuItem helpItem3 = new MenuItem(COIConstants.MENU_ABOUT, MenuToolbarIcons.PROVIDER.menuAbout());

        Menu helpMenu = new Menu();
        helpMenu.addSelectionHandler(this.getCOIMenuSelectionHandler());
        helpMenu.add(helpItem1);
        helpMenu.add(helpItem2);
        helpMenu.add(new SeparatorMenuItem());
        helpMenu.add(helpItem3);

        // BUILDING MENUBAR
        MenuBar bar = new MenuBar();
        bar.setLayoutData(new MarginData(new Margins(10)));
        bar.addStyleName(ThemeStyles.get().style().borderBottom());
        bar.add(new MenuBarItem(COIConstants.MENUTOOLBAR_WORKSPACE, fileMenu));
        bar.add(new MenuBarItem(COIConstants.MENUTOOLBAR_PROJECT, projectMenu));
        bar.add(new MenuBarItem(COIConstants.MENUTOOLBAR_BUILD, buildMenu));
        bar.add(new MenuBarItem(COIConstants.MENUTOOLBAR_USER, userMenu));
        bar.add(new MenuBarItem(COIConstants.MENUTOOLBAR_EDITOR, editorMenu));
        bar.add(new MenuBarItem(COIConstants.MENUTOOLBAR_WINDOW, windowMenu));
        bar.add(new MenuBarItem(COIConstants.MENUTOOLBAR_HELP, helpMenu));

        return bar;
    }

    /**
     * Method configures handlers for all menu items.
     *
     * @return Selection handler for menu items.
     */
    private SelectionHandler<Item> getCOIMenuSelectionHandler() {

        SelectionHandler<Item> coiMenuSelectionHandler = new SelectionHandler<Item>() {

            /**
             * All menu selection methods.
             *
             * @param event Menu selection event.
             */
            @Override
            public void onSelection(SelectionEvent<Item> event) {
                if (event.getSelectedItem() instanceof MenuItem) {
                    MenuItem item = (MenuItem) event.getSelectedItem();

                    if (item.getText().equals(COIConstants.MENU_OPEN_WORKSPACE)) {
                        CudaOnlineIDE.this.openWorkspaceMenuToolbar();
                    } else if (item.getText().equals(COIConstants.MENU_SET_ACTIVE_PROJECT)) {
                        CudaOnlineIDE.this.setActiveProjectMenuToolbar();
                    } else if (item.getText().equals(COIConstants.PANEL_PROJECT_EXPLORER)) {
                        CudaOnlineIDE.this.toggleProjectExplorerMenuToolbar(item);
                    } else if (item.getText().equals(COIConstants.MENU_OUTLINE)) {
                        CudaOnlineIDE.this.toggleOutlineMenuToolbar(item);
                    } else if (item.getText().equals(COIConstants.MENU_CONSOLE)) {
                        CudaOnlineIDE.this.toggleConsoleMenuToolbar(item);
                    } else if (item.getText().equals(COIConstants.MENU_UPLOAD_WORKSPACE)) {
                        CudaOnlineIDE.this.uploadWorkspaceMenuToolbar();
                    } else if (item.getText().equals(COIConstants.MENU_DOWNLOAD_WORKSPACE)) {
                        CudaOnlineIDE.this.dowloadWorkspaceMenuToolbar();
                    } else if (item.getText().equals(COIConstants.MENU_DELETE_WORKSPACE)) {
                        CudaOnlineIDE.this.deleteWorkspaceMenuToolbar();
                    } else if (item.getText().equals(COIConstants.MENU_NEW_WORKSPACE)) {
                        CudaOnlineIDE.this.newWorkspaceMenuToolbar();
                    } else if (item.getText().equals(COIConstants.MENU_CLOSE_WORKSPACE)) {
                        CudaOnlineIDE.this.closeWorkspaceMenuToolbar();
                    } else if (item.getText().equals(COIConstants.MENU_CLOSE_FILE)) {
                        CudaOnlineIDE.this.closeActualFileMenuToolbar();
                    } else if (item.getText().equals(COIConstants.MENU_SAVE_FILE)) {
                        CudaOnlineIDE.saveActualFileMenuToolbar();
                    } else if (item.getText().equals(COIConstants.MENU_LOGOUT)) {
                        CudaOnlineIDE.this.logoutUser();
                    } else if (item.getText().equals(COIConstants.MENU_PROPERTIES)) {

                        if (CudaOnlineIDE.ACTIVE_CUDA_COI_PROJECT == null) {
                            Info.display("Properties error", "No active project.");
                            return;
                        }

                        CudaOnlineIDE.openProjectPropertiesMenuToolbar(CudaOnlineIDE.ACTIVE_CUDA_COI_PROJECT);
                    } else if (item.getText().equals(COIConstants.MENU_BUILD)) {

                        if (CudaOnlineIDE.ACTIVE_CUDA_COI_PROJECT == null) {
                            Info.display("Build error", "No active project.");
                            return;
                        }

                        CudaOnlineIDE.buildProjectMenuToolbar(CudaOnlineIDE.ACTIVE_CUDA_COI_PROJECT);
                    } else if (item.getText().equals(COIConstants.MENU_RUN)) {

                        if (CudaOnlineIDE.ACTIVE_CUDA_COI_PROJECT == null) {
                            Info.display("Run error", "No active project.");
                            return;
                        }

                        CudaOnlineIDE.runProjectMenuToolbar(CudaOnlineIDE.ACTIVE_CUDA_COI_PROJECT);
                    } else if (item.getText().equals(COIConstants.MENU_SOFT_TABS)) {
                        CudaOnlineIDE.this.editorSoftTabsMenuToolbar(item);
                    } else if (item.getText().equals(COIConstants.MENU_H_SCROLLBAR)) {
                        CudaOnlineIDE.this.editorHScrollBarMenuToolbar(item);
                    } else if (item.getText().equals(COIConstants.MENU_SHOW_GUTTER)) {
                        CudaOnlineIDE.this.editorShowGutterMenuToolbar(item);
                    } else if (item.getText().equals(COIConstants.MENU_HIGHLIGHT)) {
                        CudaOnlineIDE.this.editorHighlightSelectedWordMenuToolbar(item);
                    } else if (item.getText().equals(COIConstants.MENU_READ_ONLY)) {
                        CudaOnlineIDE.this.editorSetReadOnlyMenuToolbar(item);
                    } else if (item.getText().equals(COIConstants.MENU_AUTOCOMPLETE)) {
                        CudaOnlineIDE.this.editorAutocompleteMenuToolbar(item);
                    } else if (item.getText().equals(COIConstants.MENU_SET_TAB_SIZE)) {
                        CudaOnlineIDE.this.editorSetTabSizeMenuToolbar();
                    } else if (item.getText().equals(COIConstants.MENU_GO_TO_LINE)) {
                        CudaOnlineIDE.this.editorGoToLineMenuToolbar();
                    } else if (item.getText().equals(COIConstants.MENU_INSERT_AT_LINE)) {
                        CudaOnlineIDE.this.editorInserTextMenuToolbar();
                    } else if (item.getText().equals(COIConstants.MENU_HELP)) {
                        CudaOnlineIDE.this.helpMenuToolbar();
                    } else if (item.getText().equals(COIConstants.MENU_CUDA_PROGRAMMING_GUIDE)) {
                        CudaOnlineIDE.this.programmingGuideMenuToolbar();
                    } else if (item.getText().equals(COIConstants.MENU_ABOUT)) {
                        CudaOnlineIDE.this.aboutMenuToolbar();
                    }
                }
            }
        };

        return coiMenuSelectionHandler;
    }

    /**
     * Method creates toolbar for CUDA On-line IDE application.
     *
     * @return Toolbar as widget.
     */
    private Widget getCOIToolbar() {

        ToolBar toolBar = new ToolBar();

		  // WORKSPACE TOOLBAR GROUP
        TextButton workspaceNew = new TextButton();
        workspaceNew.setIcon(MenuToolbarIcons.PROVIDER.menuNew());
        workspaceNew.setToolTip(COIConstants.MENU_NEW_WORKSPACE);
        workspaceNew.addSelectHandler(new SelectHandler() {

            /**
             * ToolBar new workspace handler.
             */
            @Override
            public void onSelect(SelectEvent event) {
                CudaOnlineIDE.this.newWorkspaceMenuToolbar();
            }
        });
        TextButton workspaceOpen = new TextButton();
        workspaceOpen.setIcon(MenuToolbarIcons.PROVIDER.menuOpen());
        workspaceOpen.setToolTip(COIConstants.MENU_OPEN_WORKSPACE);
        workspaceOpen.addSelectHandler(new SelectHandler() {

            /**
             * ToolBar open workspace handler.
             */
            @Override
            public void onSelect(SelectEvent event) {
                CudaOnlineIDE.this.openWorkspaceMenuToolbar();
            }
        });
        TextButton workspaceClose = new TextButton();
        workspaceClose.setIcon(MenuToolbarIcons.PROVIDER.menuClose());
        workspaceClose.setToolTip(COIConstants.MENU_CLOSE_WORKSPACE);
        workspaceClose.addSelectHandler(new SelectHandler() {

            /**
             * ToolBar close workspace handler.
             */
            @Override
            public void onSelect(SelectEvent event) {
                CudaOnlineIDE.this.closeWorkspaceMenuToolbar();
            }
        });
        TextButton workspaceDelete = new TextButton();
        workspaceDelete.setIcon(MenuToolbarIcons.PROVIDER.menuDelete());
        workspaceDelete.setToolTip(COIConstants.MENU_DELETE_WORKSPACE);
        workspaceDelete.addSelectHandler(new SelectHandler() {

            /**
             * ToolBar delete workspace handler.
             */
            @Override
            public void onSelect(SelectEvent event) {
                CudaOnlineIDE.this.deleteWorkspaceMenuToolbar();
            }
        });
        TextButton workspaceUpload = new TextButton();
        workspaceUpload.setIcon(MenuToolbarIcons.PROVIDER.menuUpload());
        workspaceUpload.setToolTip(COIConstants.MENU_UPLOAD_WORKSPACE);
        workspaceUpload.addSelectHandler(new SelectHandler() {

            /**
             * ToolBar upload workspace handler.
             */
            @Override
            public void onSelect(SelectEvent event) {
                CudaOnlineIDE.this.uploadWorkspaceMenuToolbar();
            }
        });
        TextButton workspaceDownload = new TextButton();
        workspaceDownload.setIcon(MenuToolbarIcons.PROVIDER.menuDownload());
        workspaceDownload.setToolTip(COIConstants.MENU_DOWNLOAD_WORKSPACE);
        workspaceDownload.addSelectHandler(new SelectHandler() {

            /**
             * ToolBar download workspace handler.
             */
            @Override
            public void onSelect(SelectEvent event) {
                CudaOnlineIDE.this.dowloadWorkspaceMenuToolbar();
            }
        });

        FlexTable tableWorkspace = new FlexTable();
        tableWorkspace.setWidget(0, 0, workspaceNew);
        tableWorkspace.setWidget(0, 1, workspaceOpen);
        tableWorkspace.setWidget(0, 2, workspaceClose);
        tableWorkspace.setWidget(0, 3, workspaceDelete);
        tableWorkspace.setWidget(0, 4, workspaceUpload);
        tableWorkspace.setWidget(0, 5, workspaceDownload);
        ButtonGroup workspaceGroup = new ButtonGroup();
        workspaceGroup.setHeadingText(COIConstants.MENUTOOLBAR_WORKSPACE);
        workspaceGroup.add(tableWorkspace);

	      // PROJECT TOOLBAR GROUP
        TextButton projectProperties = new TextButton();
        projectProperties.setIcon(MenuToolbarIcons.PROVIDER.menuProperties());
        projectProperties.setToolTip(COIConstants.MENU_PROPERTIES);
        projectProperties.addSelectHandler(new SelectHandler() {

            /**
             * ToolBar open project properties handler.
             */
            @Override
            public void onSelect(SelectEvent event) {

                if (CudaOnlineIDE.ACTIVE_CUDA_COI_PROJECT == null) {
                    Info.display("Properties error", "No active project.");
                    return;
                }

                CudaOnlineIDE.openProjectPropertiesMenuToolbar(CudaOnlineIDE.ACTIVE_CUDA_COI_PROJECT);
            }
        });
        TextButton activeProjectName = new TextButton();
        activeProjectName.setIcon(MenuToolbarIcons.PROVIDER.projectActive());
        activeProjectName.setToolTip(COIConstants.MENU_SET_ACTIVE_PROJECT);
        activeProjectName.addSelectHandler(new SelectHandler() {

            /**
             * ToolBar set active project handler.
             */
            @Override
            public void onSelect(SelectEvent event) {
                CudaOnlineIDE.this.setActiveProjectMenuToolbar();
            }
        });

        FlexTable tableProject = new FlexTable();
        tableProject.setWidget(0, 1, projectProperties);
        tableProject.setWidget(0, 2, activeProjectName);
        ButtonGroup projectGroup = new ButtonGroup();
        projectGroup.setHeadingText(COIConstants.MENUTOOLBAR_PROJECT);
        projectGroup.add(tableProject);

	      // FILE TOOLBAR GROUP
        TextButton fileClose = new TextButton();
        fileClose.setIcon(MenuToolbarIcons.PROVIDER.menuClose());
        fileClose.setToolTip(COIConstants.MENU_CLOSE_FILE);
        fileClose.addSelectHandler(new SelectHandler() {

            /**
             * ToolBar close active file handler.
             */
            @Override
            public void onSelect(SelectEvent event) {
                CudaOnlineIDE.this.closeActualFileMenuToolbar();
            }
        });
        TextButton fileSave = new TextButton();
        fileSave.setIcon(MenuToolbarIcons.PROVIDER.menuSave());
        fileSave.setToolTip(COIConstants.MENU_SAVE_FILE);
        fileSave.addSelectHandler(new SelectHandler() {

            /**
             * ToolBar save active file handler.
             */
            @Override
            public void onSelect(SelectEvent event) {
                CudaOnlineIDE.saveActualFileMenuToolbar();
            }
        });

        FlexTable tableFile = new FlexTable();
        tableFile.setWidget(0, 0, fileClose);
        tableFile.setWidget(0, 1, fileSave);
        ButtonGroup fileGroup = new ButtonGroup();
        fileGroup.setHeadingText(COIConstants.MENUTOOLBAR_FILE);
        fileGroup.add(tableFile);

	      // BUILD TOOLBAR GROUP
        TextButton buildCmakeLists = new TextButton();
        buildCmakeLists.setIcon(MenuToolbarIcons.PROVIDER.menuCmakeLists());
        buildCmakeLists.setToolTip(COIConstants.MENU_GENERATE_CMAKELISTS + " as executable");
        buildCmakeLists.addSelectHandler(new SelectHandler() {

            /**
             * ToolBar generate CMakeLists.txt as executable handler.
             */
            @Override
            public void onSelect(SelectEvent event) {

                if (CudaOnlineIDE.ACTIVE_CUDA_COI_PROJECT == null) {
                    Info.display("CMake error", "No active project.");
                    return;
                }

                CudaOnlineIDE.generateCMakeListsMenuToolbar(CudaOnlineIDE.ACTIVE_CUDA_COI_PROJECT, COIConstants.MENU_EXECUTABLE);
            }
        });
        TextButton buildBuild = new TextButton();
        buildBuild.setIcon(MenuToolbarIcons.PROVIDER.menuBuild());
        buildBuild.setToolTip(COIConstants.MENU_BUILD);
        buildBuild.addSelectHandler(new SelectHandler() {

            /**
             * ToolBar build project handler.
             */
            @Override
            public void onSelect(SelectEvent event) {

                if (CudaOnlineIDE.ACTIVE_CUDA_COI_PROJECT == null) {
                    Info.display("Build error", "No active project.");
                    return;
                }

                CudaOnlineIDE.buildProjectMenuToolbar(CudaOnlineIDE.ACTIVE_CUDA_COI_PROJECT);
            }
        });
        TextButton buildRun = new TextButton();
        buildRun.setIcon(MenuToolbarIcons.PROVIDER.menuRun());
        buildRun.setToolTip(COIConstants.MENU_RUN);
        buildRun.addSelectHandler(new SelectHandler() {

            /**
             * ToolBar execute project handler.
             */
            @Override
            public void onSelect(SelectEvent event) {

                if (CudaOnlineIDE.ACTIVE_CUDA_COI_PROJECT == null) {
                    Info.display("Run error", "No active project.");
                    return;
                }

                CudaOnlineIDE.runProjectMenuToolbar(CudaOnlineIDE.ACTIVE_CUDA_COI_PROJECT);
            }
        });

        FlexTable tableBuild = new FlexTable();
        tableBuild.setWidget(0, 0, buildCmakeLists);
        tableBuild.setWidget(0, 1, buildBuild);
        tableBuild.setWidget(0, 2, buildRun);
        ButtonGroup buildGroup = new ButtonGroup();
        buildGroup.setHeadingText(COIConstants.MENUTOOLBAR_BUILD);
        buildGroup.add(tableBuild);

	      // USER TOOLBAR GROUP
        TextButton userLogout = new TextButton();
        userLogout.setIcon(MenuToolbarIcons.PROVIDER.menuLogout());
        userLogout.setToolTip(COIConstants.MENU_LOGOUT);
        userLogout.addSelectHandler(new SelectHandler() {

            /**
             * ToolBar logout active user handler.
             */
            @Override
            public void onSelect(SelectEvent event) {
                CudaOnlineIDE.this.logoutUser();
            }
        });

        FlexTable tableUser = new FlexTable();
        tableUser.setWidget(0, 0, userLogout);
        ButtonGroup userGroup = new ButtonGroup();
        userGroup.setHeadingText(COIConstants.MENUTOOLBAR_USER);
        userGroup.add(tableUser);

        // CONFIGURATION TOOLBAR GROUP
        buildConfigurationCombo = new SimpleComboBox<String>(new StringLabelProvider<String>());
        buildConfigurationCombo.setTriggerAction(TriggerAction.ALL);
        buildConfigurationCombo.setEditable(false);
        buildConfigurationCombo.setWidth(100);
        buildConfigurationCombo.add(COIConstants.BUILD_CONFIGURATION_DEBUG);
        buildConfigurationCombo.add(COIConstants.BUILD_CONFIGURATION_RELEASE);
        buildConfigurationCombo.add(COIConstants.BUILD_CONFIGURATION_CUSTOM);
        buildConfigurationCombo.setValue(COIConstants.BUILD_CONFIGURATION_DEBUG);
        buildConfigurationCombo.addSelectionHandler(new SelectionHandler<String>() {

            /**
             * Handler for selection active project configuration.
             *
             * @param event Selection event.
             */
            @Override
            public void onSelection(SelectionEvent<String> event) {

                if (CudaOnlineIDE.ACTIVE_CUDA_COI_PROJECT == null) {
                    return;
                }

                final String selectionMode = event.getSelectedItem();

                COIWorkspace coiWorkspace = CudaOnlineIDE.ACTIVE_CUDA_COI_WORKSPACE;

                for (COIProject coiProject : coiWorkspace.getItems()) {
                    if (coiProject.getPath() == CudaOnlineIDE.ACTIVE_CUDA_COI_PROJECT.getPath()) {
                        coiProject.getBuildConfiguration().setActive(event.getSelectedItem());
                        break;
                    }
                }

                CudaOnlineIDE.coiService.updateWorkspace(coiWorkspace, new AsyncCallback<Void>() {

                    /**
                     * Method is called if failed writing active configuration
                     * to workspace file.
                     */
                    @Override
                    public void onFailure(Throwable caught) {
                        GWT.log(caught.getMessage());
                        Info.display("Build error", "Error in selecting build configuration.");
                    }

                    /**
                     * Method is called if writing active configuration to
                     * workspace file is successful.
                     *
                     * @param voidResult Empty result.
                     */
                    @Override
                    public void onSuccess(Void voidResult) {
                        Info.display("Build", selectionMode + " build configuration was selected.");
                    }
                });
            }
        });

        FlexTable tableConfguration = new FlexTable();
        tableConfguration.setWidget(0, 0, buildConfigurationCombo);
        ButtonGroup configurationGroup = new ButtonGroup();
        configurationGroup.setHeadingText(COIConstants.MENUTOOLBAR_CONFIGURATION);
        configurationGroup.add(tableConfguration);

	      // EDITOR TOOLBAR GROUP
        TextButton editorSettabsize = new TextButton();
        editorSettabsize.setIcon(MenuToolbarIcons.PROVIDER.menuTabSize());
        editorSettabsize.setToolTip(COIConstants.MENU_SET_TAB_SIZE);
        editorSettabsize.addSelectHandler(new SelectHandler() {

            /**
             * ToolBar set tab size in code editor handler.
             */
            @Override
            public void onSelect(SelectEvent event) {
                CudaOnlineIDE.this.editorSetTabSizeMenuToolbar();
            }
        });
        TextButton editorGotoline = new TextButton();
        editorGotoline.setIcon(MenuToolbarIcons.PROVIDER.menuGoToLine());
        editorGotoline.setToolTip(COIConstants.MENU_GO_TO_LINE);
        editorGotoline.addSelectHandler(new SelectHandler() {

            /**
             * ToolBar go to line in code editor handler.
             */
            @Override
            public void onSelect(SelectEvent event) {
                CudaOnlineIDE.this.editorGoToLineMenuToolbar();
            }
        });
        TextButton editorInsertat = new TextButton();
        editorInsertat.setIcon(MenuToolbarIcons.PROVIDER.menuInsert());
        editorInsertat.setToolTip(COIConstants.MENU_INSERT_AT_LINE);
        editorInsertat.addSelectHandler(new SelectHandler() {

            /**
             * ToolBar insert text at cursor in code editor handler.
             */
            @Override
            public void onSelect(SelectEvent event) {
                CudaOnlineIDE.this.editorInserTextMenuToolbar();
            }
        });

        FlexTable tableEditor = new FlexTable();
        tableEditor.setWidget(0, 0, editorSettabsize);
        tableEditor.setWidget(0, 1, editorGotoline);
        tableEditor.setWidget(0, 2, editorInsertat);
        ButtonGroup editorGroup = new ButtonGroup();
        editorGroup.setHeadingText(COIConstants.MENUTOOLBAR_EDITOR);
        editorGroup.add(tableEditor);

	      // ADD GROUPS TO TOOLBAR
        toolBar.add(workspaceGroup);
        toolBar.add(projectGroup);
        toolBar.add(fileGroup);
        toolBar.add(buildGroup);
        toolBar.add(configurationGroup);
        toolBar.add(userGroup);
        toolBar.add(editorGroup);

        return toolBar;
    }

    /**
     * Method creates console area for CUDA On-line IDE application.
     *
     * @return Console area as widget.
     */
    private Widget getCOIConsoleArea() {

        final TabPanel consoleTabPanel = new TabPanel();
        consoleTabPanel.setSize(COIConstants.SIZE_100_PERCENTAGE, COIConstants.SIZE_100_PERCENTAGE);
        consoleTabPanel.setAnimScroll(true);
        consoleTabPanel.setTabScroll(true);
        consoleTabPanel.setCloseContextMenu(true);

        CudaOnlineIDE.aceConsole = new AceEditor();
        CudaOnlineIDE.aceConsole.setSize(COIConstants.SIZE_100_PERCENTAGE, COIConstants.SIZE_100_PERCENTAGE);

        consoleTabPanel.add(CudaOnlineIDE.aceConsole, new TabItemConfig(COIConstants.PANEL_CONSOLE, false));

        CudaOnlineIDE.aceConsole.startEditor();
        CudaOnlineIDE.aceConsole.setMode(AceEditorMode.PLAIN_TEXT);
        CudaOnlineIDE.aceConsole.setTheme(AceEditorTheme.ECLIPSE);
        CudaOnlineIDE.aceConsole.setFontSize(14);
        CudaOnlineIDE.aceConsole.setReadOnly(true);
        CudaOnlineIDE.aceConsole.setShowGutter(false);
        CudaOnlineIDE.aceConsole.setText("Console output ...");

        consoleTabPanel.setActiveWidget(CudaOnlineIDE.aceConsole);

        consoleTabPanel.addSelectionHandler(new SelectionHandler<Widget>() {

            /**
             * Handler for event of change active tab at console tab panel.
             *
             * @param event Selection event.
             */
            @Override
            public void onSelection(SelectionEvent<Widget> event) {
                AceEditor editor = (AceEditor) consoleTabPanel.getActiveWidget();
                editor.gotoLine(editor.getLineCount());
            }
        });

        return consoleTabPanel;
    }

    /**
     * Method creates open file area for CUDA On-line IDE application.
     *
     * @return Open file area as widget.
     */
    private Widget getCOIFileArea() {

        COIDataProperties dp = GWT.create(COIDataProperties.class);
        CudaOnlineIDE.coiDataTreeStore = new TreeStore<COIData>(dp.key());
        CudaOnlineIDE.westPanel.add(CudaOnlineIDE.coiDataTreeActionProvider(dp));

        CudaOnlineIDE.fileTabPanel = new TabPanel();
        CudaOnlineIDE.fileTabPanel.setSize(COIConstants.SIZE_100_PERCENTAGE, COIConstants.SIZE_100_PERCENTAGE);
        CudaOnlineIDE.fileTabPanel.setAnimScroll(true);
        CudaOnlineIDE.fileTabPanel.setTabScroll(true);
        CudaOnlineIDE.fileTabPanel.setCloseContextMenu(true);

        CudaOnlineIDE.fileTabPanel.addSelectionHandler(new SelectionHandler<Widget>() {

            /**
             * Handler for event of change active tab at main file tab panel.
             *
             * @param event Selection event.
             */
            @Override
            public void onSelection(SelectionEvent<Widget> event) {

                if (CudaOnlineIDE.fileTabPanel.getActiveWidget() == null) {
                    FileDataProperties fdp = GWT.create(FileDataProperties.class);
                    CudaOnlineIDE.fileDataTreeStore = new TreeStore<COIFileData>(fdp.key());
                    CudaOnlineIDE.eastPanel.add(CudaOnlineIDE.fileDataTreeActionProvider(fdp));
                } else {
                    String activeWidgetText = CudaOnlineIDE.fileTabPanel.getConfig(CudaOnlineIDE.fileTabPanel.getActiveWidget()).getText();
                    boolean escaped = false;

                    for (COIProject coiProject : ACTIVE_CUDA_COI_WORKSPACE.getItems()) {
                        for (COIFolder coiFolder : coiProject.getItems()) {
                            for (final COIFile coiFile : coiFolder.getItems()) {
                                if (CudaOnlineIDE.getExclusivePath(coiFile.getPath()) == activeWidgetText || (CudaOnlineIDE.getExclusivePath(coiFile.getPath()) + " *") == activeWidgetText) {

                                    CudaOnlineIDE.getTagsFromFileToEastPanel(coiFile.getPath());

                                    escaped = true;
                                    break;
                                }
                            }
                            if (escaped) {
                                break;
                            }
                        }
                        if (escaped) {
                            break;
                        }
                    }
                }
            }
        });

        CudaOnlineIDE.fileTabPanel.addCloseHandler(new CloseHandler<Widget>() {

            /**
             * Handler for event of close tab at main file tab panel.
             *
             * @param event Close file tab panel event.
             */
            @Override
            public void onClose(CloseEvent<Widget> event) {

                if (CudaOnlineIDE.fileTabPanel.getActiveWidget() == null) {
                    FileDataProperties fdp = GWT.create(FileDataProperties.class);
                    CudaOnlineIDE.fileDataTreeStore = new TreeStore<COIFileData>(fdp.key());
                    CudaOnlineIDE.eastPanel.add(CudaOnlineIDE.fileDataTreeActionProvider(fdp));
                } else {
                    String activeWidgetText = CudaOnlineIDE.fileTabPanel.getConfig(CudaOnlineIDE.fileTabPanel.getActiveWidget()).getText();
                    boolean escaped = false;

                    for (COIProject coiProject : ACTIVE_CUDA_COI_WORKSPACE.getItems()) {
                        for (COIFolder coiFolder : coiProject.getItems()) {
                            for (final COIFile coiFile : coiFolder.getItems()) {
                                if (CudaOnlineIDE.getExclusivePath(coiFile.getPath()) == activeWidgetText || (CudaOnlineIDE.getExclusivePath(coiFile.getPath()) + " *") == activeWidgetText) {

                                    CudaOnlineIDE.getTagsFromFileToEastPanel(coiFile.getPath());

                                    escaped = true;
                                    break;
                                }
                            }
                            if (escaped) {
                                break;
                            }
                        }
                        if (escaped) {
                            break;
                        }
                    }
                }
            }
        });

        return CudaOnlineIDE.fileTabPanel;
    }

    /**
     * Method opens dialog for opening workspace.
     */
    private void openWorkspaceMenuToolbar() {

        CudaOnlineIDE.coiService.getUserWorkspaceFiles(CudaOnlineIDE.ACTIVE_CUDA_FOLDER, new AsyncCallback<List<String>>() {
            public void onFailure(Throwable caught) {
                GWT.log(caught.getMessage());
                Info.display("Error", "Error in getting workspace files.");
            }

            public void onSuccess(List<String> workspaceFiles) {

                if (workspaceFiles == null) {
                    return;
                }

                if (workspaceFiles.isEmpty()) {
                    Info.display(COIConstants.INFO_OPEN, "No workspace in your user path.");
                    return;
                }

                Window openWorkspacePanel = PopUpWindow.openWorkspacePanel(workspaceFiles);
                openWorkspacePanel.setResizable(true);
                openWorkspacePanel.setResize(true);
                openWorkspacePanel.show();

                openWorkspacePanel.addHideHandler(new HideHandler() {

                    /**
                     * Method is called when open workspace panel is closed.
                     */
                    @Override
                    public void onHide(HideEvent event) {

                        Map<String, Object> popUpData = PopUpWindow.data;

                        if (popUpData.containsKey(PopUpWindow.WORKSPACEFILE)) {

                            CudaOnlineIDE.this.closeWorkspaceMenuToolbar();

                            CudaOnlineIDE.coiService.getCOIWorkspaceServiceMethod(ACTIVE_CUDA_FOLDER + popUpData.get(PopUpWindow.WORKSPACEFILE), new AsyncCallback<COIWorkspace>() {

                                /**
                                 * Method is called when retrieving workspace is
                                 * failed.
                                 */
                                public void onFailure(Throwable caught) {
                                    GWT.log(caught.getMessage());
                                    Info.display("Error", "Error in opening workspace.");
                                }

                                /**
                                 * Method is called when retrieving of workspace
                                 * is successful.
                                 *
                                 * @param coiWorkspace Retrieved workspace.
                                 */
                                public void onSuccess(COIWorkspace coiWorkspace) {
                                    COIDataProperties dp = GWT.create(COIDataProperties.class);
                                    CudaOnlineIDE.coiDataTreeConstructor(coiWorkspace, dp);
                                    CudaOnlineIDE.westPanel.add(CudaOnlineIDE.coiDataTreeActionProvider(dp));

                                    if (coiWorkspace.getCwsVersion() == "2.0") {
                                        CudaOnlineIDE.ACTIVE_CUDA_WORKSPACE_FILE = coiWorkspace.getName() + "_workspace.cws2";
                                    } else {
                                        CudaOnlineIDE.ACTIVE_CUDA_WORKSPACE_FILE = coiWorkspace.getName() + "_workspace.cws";
                                    }

                                    CudaOnlineIDE.ACTIVE_CUDA_COI_WORKSPACE = coiWorkspace;
                                    CudaOnlineIDE.ACTIVE_CUDA_COI_PROJECT = null;
                                    CudaOnlineIDE.coiAppContainer.collapse(LayoutRegion.EAST);

                                    Info.display(COIConstants.INFO_OPEN, "Open " + coiWorkspace.getName() + " workspace.");
                                }
                            });
                        }
                    }
                });
            }
        });

    }

    /**
     * Method opens dialog for creating new workspace.
     */
    private void newWorkspaceMenuToolbar() {

        Window newWorkspacePanel = PopUpWindow.newWorkspacePanel(CudaOnlineIDE.ACTIVE_CUDA_FOLDER);
        newWorkspacePanel.show();

        newWorkspacePanel.addHideHandler(new HideHandler() {

            /**
             * Method is called when new workspace panel is closed.
             */
            @Override
            public void onHide(HideEvent event) {

                Map<String, Object> popUpData = PopUpWindow.data;

                if (popUpData.containsKey(PopUpWindow.WORKSPACENAME)) {

                    CudaOnlineIDE.this.closeWorkspaceMenuToolbar();

                    final COIWorkspace coiWorkspace = new COIWorkspace();
                    coiWorkspace.setName((String) popUpData.get(PopUpWindow.WORKSPACENAME));
                    coiWorkspace.setPath(CudaOnlineIDE.ACTIVE_CUDA_FOLDER + coiWorkspace.getName() + COIConstants.FWD_SLASH);
                    coiWorkspace.setCwsVersion(COIConstants.CWS_VERSION_NUMBER);
                    coiWorkspace.setTypeOfCOI(COIEnum.WORKSPACE);

                    CudaOnlineIDE.coiService.createNewWorkspace(coiWorkspace, new AsyncCallback<Boolean>() {

                        /**
                         * Method is called when creating of workspace failed.
                         */
                        @Override
                        public void onFailure(Throwable caught) {
                            GWT.log(caught.getMessage());
                            Info.display("Create error", "Error creating new workspace.");
                        }

                        /**
                         * Method is called when creating of workspace is
                         * successful.
                         *
                         * @param result True if workspace was created.
                         */
                        @Override
                        public void onSuccess(Boolean result) {

                            if (result == true) {

                                COIDataProperties dp = GWT.create(COIDataProperties.class);
                                CudaOnlineIDE.coiDataTreeConstructor(coiWorkspace, dp);
                                CudaOnlineIDE.westPanel.add(CudaOnlineIDE.coiDataTreeActionProvider(dp));

                                if (coiWorkspace.getCwsVersion() == "2.0") {
                                    CudaOnlineIDE.ACTIVE_CUDA_WORKSPACE_FILE = coiWorkspace.getName() + "_workspace.cws2";
                                } else {
                                    CudaOnlineIDE.ACTIVE_CUDA_WORKSPACE_FILE = coiWorkspace.getName() + "_workspace.cws";
                                }

                                CudaOnlineIDE.ACTIVE_CUDA_COI_WORKSPACE = coiWorkspace;
                                CudaOnlineIDE.ACTIVE_CUDA_COI_PROJECT = null;
                                CudaOnlineIDE.coiAppContainer.collapse(LayoutRegion.EAST);

                                Info.display("Create", "Workspace " + coiWorkspace.getName() + " was created.");
                            } else {
                                Info.display("Create error", "Error creating new workspace.");
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * Method deletes actual workspace.
     */
    private void deleteWorkspaceMenuToolbar() {

        CudaOnlineIDE.coiService.deleteWorkspace(ACTIVE_CUDA_FOLDER + ACTIVE_CUDA_WORKSPACE_FILE, new AsyncCallback<Boolean>() {

            /**
             * Method is called if deleting of workspace failed.
             */
            @Override
            public void onFailure(Throwable caught) {
                GWT.log(caught.getMessage());
                Info.display("Delete error", "Error in deleting workspace.");
            }

            /**
             * Method is called if deleting of workspace is successful.
             *
             * @param result True if workspace was deleted.
             */
            @Override
            public void onSuccess(Boolean result) {

                if (result == true) {

                    for (int i = (CudaOnlineIDE.fileTabPanel.getWidgetCount() - 1); i >= 0; i--) {
                        CudaOnlineIDE.fileTabPanel.remove(i);
                    }

                    COIDataProperties dp = GWT.create(COIDataProperties.class);
                    CudaOnlineIDE.coiDataTreeStore = new TreeStore<COIData>(dp.key());
                    CudaOnlineIDE.westPanel.add(CudaOnlineIDE.coiDataTreeActionProvider(dp));

                    FileDataProperties fdp = GWT.create(FileDataProperties.class);
                    CudaOnlineIDE.fileDataTreeStore = new TreeStore<COIFileData>(fdp.key());
                    CudaOnlineIDE.eastPanel.add(CudaOnlineIDE.fileDataTreeActionProvider(fdp));

                    CudaOnlineIDE.aceConsole.setValue(COIConstants.EMPTY);

                    CudaOnlineIDE.buildConfigurationCombo.setValue(COIConstants.BUILD_CONFIGURATION_DEBUG);

                    CudaOnlineIDE.ACTIVE_CUDA_WORKSPACE_FILE = COIConstants.EMPTY;
                    CudaOnlineIDE.ACTIVE_CUDA_COI_WORKSPACE = null;
                    CudaOnlineIDE.ACTIVE_CUDA_COI_PROJECT = null;
                    CudaOnlineIDE.coiAppContainer.collapse(LayoutRegion.EAST);

                    Info.display(COIConstants.INFO_DELETE, "Workspace deleted.");
                } else {
                    Info.display("Delete error", "Error in deleting workspace.");
                }
            }
        });
    }

    /**
     * Method closes actual workspace.
     */
    private void closeWorkspaceMenuToolbar() {

        for (int i = (CudaOnlineIDE.fileTabPanel.getWidgetCount() - 1); i >= 0; i--) {
            CudaOnlineIDE.fileTabPanel.remove(i);
        }

        COIDataProperties dp = GWT.create(COIDataProperties.class);
        CudaOnlineIDE.coiDataTreeStore = new TreeStore<COIData>(dp.key());
        CudaOnlineIDE.westPanel.add(CudaOnlineIDE.coiDataTreeActionProvider(dp));

        FileDataProperties fdp = GWT.create(FileDataProperties.class);
        CudaOnlineIDE.fileDataTreeStore = new TreeStore<COIFileData>(fdp.key());
        CudaOnlineIDE.eastPanel.add(CudaOnlineIDE.fileDataTreeActionProvider(fdp));

        CudaOnlineIDE.aceConsole.setValue(COIConstants.EMPTY);

        CudaOnlineIDE.buildConfigurationCombo.setValue(COIConstants.BUILD_CONFIGURATION_DEBUG);

        CudaOnlineIDE.ACTIVE_CUDA_WORKSPACE_FILE = COIConstants.EMPTY;
        CudaOnlineIDE.ACTIVE_CUDA_COI_WORKSPACE = null;
        CudaOnlineIDE.ACTIVE_CUDA_COI_PROJECT = null;
        CudaOnlineIDE.coiAppContainer.collapse(LayoutRegion.EAST);
        Info.display(COIConstants.INFO_CLOSE, "Closing actual workspace.");
    }

    /**
     * Method opens dialog for uploading workspace.
     */
    private void uploadWorkspaceMenuToolbar() {

        Window zipUploadPanel = PopUpWindow.zipUploadPanel(null);
        zipUploadPanel.show();

        zipUploadPanel.addHideHandler(new HideHandler() {

            /**
             * Method is called when upload workspace panel is closed.
             */
            @Override
            public void onHide(HideEvent event) {

                Map<String, Object> popUpData = PopUpWindow.data;

                if (!popUpData.containsKey(PopUpWindow.COIWORKSPACE) || popUpData.get(PopUpWindow.COIWORKSPACE) == null || ((String) popUpData.get(PopUpWindow.COIWORKSPACE)).isEmpty()) {
                    return;
                }

                if (popUpData.get(PopUpWindow.COIWORKSPACE).equals("Item already exists!")) {
                    Info.display("Upload error", "Workspace with this name already exists.");
                    return;
                } else if (popUpData.get(PopUpWindow.COIWORKSPACE).equals("Bad zip structure!")) {
                    Info.display("Upload error", "Bad zip format.");
                    return;
                }

                CudaOnlineIDE.this.closeWorkspaceMenuToolbar();

                CudaOnlineIDE.coiService.updateWorkspaceUploadWorkspace((String) popUpData.get(PopUpWindow.COIWORKSPACE), new AsyncCallback<COIWorkspace>() {

                    /**
                     * Method is called if updating of workspace failed.
                     */
                    @Override
                    public void onFailure(Throwable caught) {
                        GWT.log(caught.getMessage());
                        Info.display("Upload error", "Error in uploading workspace.");
                    }

                    /**
                     * Method is called if updating of workspace is successful.
                     *
                     * @param coiWorkspace Updating workspace.
                     */
                    @Override
                    public void onSuccess(COIWorkspace coiWorkspace) {

                        if (coiWorkspace == null) {
                            Info.display("Upload error", "Error in uploading workspace.");
                        } else {

                            COIDataProperties dp = GWT.create(COIDataProperties.class);
                            CudaOnlineIDE.coiDataTreeConstructor(coiWorkspace, dp);
                            CudaOnlineIDE.westPanel.add(CudaOnlineIDE.coiDataTreeActionProvider(dp));

                            if (coiWorkspace.getCwsVersion() == "2.0") {
                                CudaOnlineIDE.ACTIVE_CUDA_WORKSPACE_FILE = coiWorkspace.getName() + "_workspace.cws2";
                            } else {
                                CudaOnlineIDE.ACTIVE_CUDA_WORKSPACE_FILE = coiWorkspace.getName() + "_workspace.cws";
                            }

                            CudaOnlineIDE.ACTIVE_CUDA_COI_WORKSPACE = coiWorkspace;
                            CudaOnlineIDE.ACTIVE_CUDA_COI_PROJECT = null;
                            CudaOnlineIDE.coiAppContainer.collapse(LayoutRegion.EAST);

                            Info.display("Upload success", "Workspace is successfully uploaded.");
                        }
                    }
                });
            }
        });
    }

    /**
     * Method downloads actual workspace.
     */
    private void dowloadWorkspaceMenuToolbar() {

        String url = GWT.getModuleBaseURL() + "downloadZipService?folder=" + CudaOnlineIDE.ACTIVE_CUDA_COI_WORKSPACE.getPath() + "&folderName=" + CudaOnlineIDE.ACTIVE_CUDA_COI_WORKSPACE.getName()
                + "&activeUser=" + CudaOnlineIDE.ACTIVE_USER;
        com.google.gwt.user.client.Window.open(url, "_blank", "status=0,toolbar=0,menubar=0,location=0");
    }

    /**
     * Method saves active file.
     */
    private static void saveActualFileMenuToolbar() {

        if (CudaOnlineIDE.fileTabPanel.getActiveWidget() == null) {
            return;
        }

        String activeWidgetText = CudaOnlineIDE.fileTabPanel.getConfig(CudaOnlineIDE.fileTabPanel.getActiveWidget()).getText();
        boolean escaped = false;

        for (COIProject coiProject : CudaOnlineIDE.ACTIVE_CUDA_COI_WORKSPACE.getItems()) {
            for (COIFolder coiFolder : coiProject.getItems()) {
                for (final COIFile coiFile : coiFolder.getItems()) {
                    if (CudaOnlineIDE.getExclusivePath(coiFile.getPath()) == activeWidgetText || (CudaOnlineIDE.getExclusivePath(coiFile.getPath()) + " *") == activeWidgetText) {

                        AceEditor editor = (AceEditor) CudaOnlineIDE.fileTabPanel.getActiveWidget();
                        coiFile.setText(editor.getText());

                        CudaOnlineIDE.coiService.createNewFile(CudaOnlineIDE.ACTIVE_CUDA_COI_WORKSPACE, coiFile, new AsyncCallback<Boolean>() {

                            /**
                             * Method is called if saving of file failed.
                             */
                            @Override
                            public void onFailure(Throwable caught) {
                                GWT.log(caught.getMessage());
                                Info.display("Error", "Error saving new file.");
                            }

                            /**
                             * Method is called if saving of file is successful.
                             *
                             * @param result True if file was saved.
                             */
                            @Override
                            public void onSuccess(Boolean result) {

                                TabItemConfig config = new TabItemConfig(CudaOnlineIDE.getExclusivePath(coiFile.getPath()), true);
                                CudaOnlineIDE.fileTabPanel.update(CudaOnlineIDE.fileTabPanel.getActiveWidget(), config);

                                CudaOnlineIDE.getTagsFromFileToEastPanel(coiFile.getPath());

                                Info.display("Save", "File " + coiFile.getName() + COIConstants.COMMA + coiFile.getExtension() + " was saved.");
                            }
                        });

                        escaped = true;
                        break;
                    }
                }
                if (escaped) {
                    break;
                }
            }
            if (escaped) {
                break;
            }
        }
    }

    /**
     * Method closes active file.
     */
    private void closeActualFileMenuToolbar() {

        if (CudaOnlineIDE.fileTabPanel.getActiveWidget() != null) {
            CudaOnlineIDE.fileTabPanel.remove(CudaOnlineIDE.fileTabPanel.getActiveWidget());
            Info.display(COIConstants.INFO_CLOSE, "Closing actual file.");
        }

        if (CudaOnlineIDE.fileTabPanel.getActiveWidget() == null) {
            FileDataProperties fdp = GWT.create(FileDataProperties.class);
            CudaOnlineIDE.fileDataTreeStore = new TreeStore<COIFileData>(fdp.key());
            CudaOnlineIDE.eastPanel.add(CudaOnlineIDE.fileDataTreeActionProvider(fdp));
        }
    }

    /**
     * Method opens dialog for setting active project.
     */
    private void setActiveProjectMenuToolbar() {

        Window setActiveProjectPanel = PopUpWindow.setActiveProjectPanel(CudaOnlineIDE.ACTIVE_CUDA_COI_WORKSPACE);
        setActiveProjectPanel.setResizable(true);
        setActiveProjectPanel.setResize(true);
        setActiveProjectPanel.show();

        setActiveProjectPanel.addHideHandler(new HideHandler() {

            /**
             * Method is called when set active project panel is closed.
             */
            @Override
            public void onHide(HideEvent event) {
                List<COIData> coiDataList = CudaOnlineIDE.coiDataTreeStore.getAll();

                for (COIData model : coiDataList) {
                    CudaOnlineIDE.coiDataTree.refresh(model);
                }
            }
        });
    }

    /**
     * Method creates makefile for active project.
     */
    public static void createMakefileMenuToolbar(final COIProject coiProject) {

        CudaOnlineIDE.aceConsole.setValue(COIConstants.EMPTY);
        CudaOnlineIDE.aceConsole.gotoLine(CudaOnlineIDE.aceConsole.getLineCount());

        final AutoProgressMessageBox messageBox = new AutoProgressMessageBox("Crate", "Creating makefile, please wait...");
        messageBox.setProgressText("Creating...");
        messageBox.auto();
        messageBox.show();

        CudaOnlineIDE.coiService.createMakefile(CudaOnlineIDE.ACTIVE_CUDA_COI_WORKSPACE, coiProject, new AsyncCallback<String>() {

            /**
             * Method is called if creating makefile failed.
             */
            @Override
            public void onFailure(Throwable caught) {
                messageBox.hide();
                GWT.log(caught.getMessage());
                Info.display("Makefile error", "Error in creating Makefile.");
            }

            /**
             * Method is called if creating makefile is successful.
             *
             * @param result Making progress.
             */
            @Override
            public void onSuccess(String result) {
                messageBox.hide();
                CudaOnlineIDE.aceConsole.setValue(result);
                CudaOnlineIDE.aceConsole.gotoLine(CudaOnlineIDE.aceConsole.getLineCount());

                Info.display("Makefile", "Makefile creating.");
            }
        });
    }

    /**
     * Method generate CMakeLists.txt for active project.
     */
    public static void generateCMakeListsMenuToolbar(final COIProject coiProject, final String typeOfGenerate) {

        final DialogHideHandler hideHandler = new DialogHideHandler() {

            @Override
            public void onDialogHide(DialogHideEvent event) {
                if (event.getHideButton().compareTo(PredefinedButton.YES) == 0) {

                    CudaOnlineIDE.aceConsole.setValue(COIConstants.EMPTY);
                    CudaOnlineIDE.aceConsole.gotoLine(CudaOnlineIDE.aceConsole.getLineCount());

                    final AutoProgressMessageBox messageBox = new AutoProgressMessageBox("Generate", "Generating CMakeLists.txt, please wait...");
                    messageBox.setProgressText("Generating...");
                    messageBox.auto();
                    messageBox.show();

                    CudaOnlineIDE.coiService.generateCMakeLists(typeOfGenerate, coiProject, new AsyncCallback<String>() {

                        /**
                         * Method is called if generating of CMakeLists.txt
                         * failed.
                         */
                        @Override
                        public void onFailure(Throwable caught) {
                            messageBox.hide();
                            GWT.log(caught.getMessage());
                            Info.display("CMake error", "Error in creating CMakeLists.");
                        }

                        /**
                         * Method is called if generating of CMakeLists.txt is
                         * successful.
                         *
                         * @param result Generating progress.
                         */
                        @Override
                        public void onSuccess(String result) {
                            messageBox.hide();
                            CudaOnlineIDE.aceConsole.setValue(result);
                            CudaOnlineIDE.aceConsole.gotoLine(CudaOnlineIDE.aceConsole.getLineCount());

                            Info.display("CMake", "CMakeLists generating.");
                        }
                    });
                }
            }
        };

        CudaOnlineIDE.coiService.prebuildGenerating(coiProject, new AsyncCallback<Integer>() {

            @Override
            public void onFailure(Throwable caught) {
                GWT.log(caught.getMessage());
                Info.display("CMake error", "Error in testing CMakeLists files.");
            }

            @Override
            public void onSuccess(Integer result) {

                if (result == COIConstants.GENERATING_WITH_QUESTION) {
                    ConfirmMessageBox confirmnBox = new ConfirmMessageBox("Generate CMakeLists.txt", "Are you sure you want to create new CMakeLists.txt and delete your changes?");
                    confirmnBox.addDialogHideHandler(hideHandler);
                    confirmnBox.show();
                } else {
                    hideHandler.onDialogHide(new DialogHideEvent(PredefinedButton.YES));
                }
            }

        });
    }

    /**
     * Method opens makefile for active project.
     */
    private void openCMakeListsMenuToolbar() {

        if (CudaOnlineIDE.ACTIVE_CUDA_COI_PROJECT == null) {
            Info.display("Makefile error", "No active project.");
            return;
        }

        CudaOnlineIDE.coiService.openCMakeLists(CudaOnlineIDE.ACTIVE_CUDA_COI_PROJECT, new AsyncCallback<String>() {

            /**
             * Method is called if opening CMakeLists.txt failed.
             */
            @Override
            public void onFailure(Throwable caught) {
                GWT.log(caught.getMessage());
                Info.display("Makefile error", "Error in opening makefile.");
            }

            /**
             * Method is called if opening CMakeLists.txt is successful.
             *
             * @param result Text of CMakeLists.txt
             */
            @Override
            public void onSuccess(String result) {

                if (result == null) {
                    Info.display("CMakeLists.txt error", "CMakeLists.txt must be created first.");
                } else {
                    Window openMakefilePanel = PopUpWindow.openMakeFileProjectPanel(result);
                    openMakefilePanel.show();
                }
            }
        });
    }

    /**
     * Method builds active project.
     */
    public static void buildProjectMenuToolbar(final COIProject coiProject) {

        final DialogHideHandler hideHandler = new DialogHideHandler() {

            @Override
            public void onDialogHide(DialogHideEvent event) {
                if (event.getHideButton().compareTo(PredefinedButton.YES) == 0) {

                    CudaOnlineIDE.aceConsole.setValue(COIConstants.EMPTY);
                    CudaOnlineIDE.aceConsole.gotoLine(CudaOnlineIDE.aceConsole.getLineCount());

                    final AutoProgressMessageBox messageBox = new AutoProgressMessageBox("Generate", "Generating CMakeLists.txt, please wait...");
                    messageBox.setProgressText("Generating...");
                    messageBox.auto();
                    messageBox.show();

                    CudaOnlineIDE.coiService.generateCMakeLists(COIConstants.MENU_EXECUTABLE, coiProject, new AsyncCallback<String>() {

                        /**
                         * Method is called if generating of CMakeLists.txt
                         * failed.
                         */
                        @Override
                        public void onFailure(Throwable caught) {
                            messageBox.hide();
                            GWT.log(caught.getMessage());
                            Info.display("Build error", "Error in creating CMakeLists.");
                        }

                        /**
                         * Method is called if generating of CMakeLists.txt is
                         * successful.
                         *
                         * @param result Generating progress.
                         */
                        @Override
                        public void onSuccess(String result) {
                            messageBox.hide();
                            CudaOnlineIDE.aceConsole.setValue(result);
                            CudaOnlineIDE.aceConsole.gotoLine(CudaOnlineIDE.aceConsole.getLineCount());
                            Info.display("Build", "CMakeLists generating.");

                            if (result.isEmpty() || result.startsWith("ERROR")) {
                                return;
                            }

                            final AutoProgressMessageBox messageBox = new AutoProgressMessageBox("Create", "Creating makefile, please wait...");
                            messageBox.setProgressText("Creating...");
                            messageBox.auto();
                            messageBox.show();

                            CudaOnlineIDE.coiService.createMakefile(CudaOnlineIDE.ACTIVE_CUDA_COI_WORKSPACE, coiProject, new AsyncCallback<String>() {

                                /**
                                 * Method is called if creating makefile failed.
                                 */
                                @Override
                                public void onFailure(Throwable caught) {
                                    messageBox.hide();
                                    GWT.log(caught.getMessage());
                                    Info.display("Build error", "Error in creating Makefile.");
                                }

                                /**
                                 * Method is called if creating makefile is
                                 * successful.
                                 *
                                 * @param result Making progress.
                                 */
                                @Override
                                public void onSuccess(String result) {
                                    messageBox.hide();
                                    CudaOnlineIDE.aceConsole.setValue(CudaOnlineIDE.aceConsole.getValue() + COIConstants.LINE_SEPARATOR + result);
                                    CudaOnlineIDE.aceConsole.gotoLine(CudaOnlineIDE.aceConsole.getLineCount());
                                    Info.display("Build", "Makefile creating.");

                                    if (result.isEmpty() || result.startsWith("ERROR")) {
                                        return;
                                    }

                                    final AutoProgressMessageBox messageBox = new AutoProgressMessageBox("Build", "Building project, please wait...");
                                    messageBox.setProgressText("Building...");
                                    messageBox.auto();
                                    messageBox.show();

                                    CudaOnlineIDE.coiService.buildProject(CudaOnlineIDE.ACTIVE_CUDA_COI_WORKSPACE, coiProject, new AsyncCallback<String[]>() {

                                        /**
                                         * Method is called if building of
                                         * project failed.
                                         */
                                        @Override
                                        public void onFailure(Throwable caught) {
                                            GWT.log(caught.getMessage());
                                            messageBox.hide();
                                            Info.display("Build error", "Error in building project.");
                                        }

                                        /**
                                         * Method is called if building of
                                         * project is successful.
                                         *
                                         * @param result Build progress.
                                         */
                                        @Override
                                        public void onSuccess(String[] result) {

                                            messageBox.hide();

                                            if (result == null || result.length == 0) {
                                                CudaOnlineIDE.aceConsole.setValue("ERROR: Unexpected problem.");
                                                CudaOnlineIDE.aceConsole.gotoLine(CudaOnlineIDE.aceConsole.getLineCount());
                                                return;
                                            }

                                            if (result.length == 1) {
                                                CudaOnlineIDE.aceConsole.setValue(result[0]);
                                                CudaOnlineIDE.aceConsole.gotoLine(CudaOnlineIDE.aceConsole.getLineCount());
                                                return;
                                            }

                                            CudaOnlineIDE.aceConsole.setValue(CudaOnlineIDE.aceConsole.getValue() + COIConstants.LINE_SEPARATOR + result[1]);
                                            CudaOnlineIDE.aceConsole.gotoLine(CudaOnlineIDE.aceConsole.getLineCount());
                                            Info.display("Build", "Project building.");

                                            if (result[1].isEmpty() || result[1].startsWith("ERROR")) {
                                                return;
                                            }

                                            coiProject.setUuid(result[0]);

                                            for (COIProject coiProjectTmp : CudaOnlineIDE.ACTIVE_CUDA_COI_WORKSPACE.getItems()) {
                                                if (coiProject.getPath().equals(coiProjectTmp.getPath())) {
                                                    coiProjectTmp.setUuid(result[0]);
                                                    coiProjectTmp.setBuildedOnce(true);
                                                }
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    });
                } else {

                    CudaOnlineIDE.aceConsole.setValue(COIConstants.EMPTY);
                    CudaOnlineIDE.aceConsole.gotoLine(CudaOnlineIDE.aceConsole.getLineCount());

                    final AutoProgressMessageBox messageBox = new AutoProgressMessageBox("Create", "Creating makefile, please wait...");
                    messageBox.setProgressText("Creating...");
                    messageBox.auto();
                    messageBox.show();

                    CudaOnlineIDE.coiService.createMakefile(CudaOnlineIDE.ACTIVE_CUDA_COI_WORKSPACE, coiProject, new AsyncCallback<String>() {

                        /**
                         * Method is called if creating makefile failed.
                         */
                        @Override
                        public void onFailure(Throwable caught) {
                            messageBox.hide();
                            GWT.log(caught.getMessage());
                            Info.display("Build error", "Error in creating Makefile.");
                        }

                        /**
                         * Method is called if creating makefile is successful.
                         *
                         * @param result Making progress.
                         */
                        @Override
                        public void onSuccess(String result) {
                            messageBox.hide();
                            CudaOnlineIDE.aceConsole.setValue(result);
                            CudaOnlineIDE.aceConsole.gotoLine(CudaOnlineIDE.aceConsole.getLineCount());
                            Info.display("Build", "Makefile creating.");

                            final AutoProgressMessageBox messageBox = new AutoProgressMessageBox("Build", "Building project, please wait...");
                            messageBox.setProgressText("Building...");
                            messageBox.auto();
                            messageBox.show();

                            CudaOnlineIDE.coiService.buildProject(CudaOnlineIDE.ACTIVE_CUDA_COI_WORKSPACE, coiProject, new AsyncCallback<String[]>() {

                                /**
                                 * Method is called if building of project
                                 * failed.
                                 */
                                @Override
                                public void onFailure(Throwable caught) {
                                    GWT.log(caught.getMessage());
                                    messageBox.hide();
                                    Info.display("Build error", "Error in building project.");
                                }

                                /**
                                 * Method is called if building of project is
                                 * successful.
                                 *
                                 * @param result Build progress.
                                 */
                                @Override
                                public void onSuccess(String[] result) {

                                    messageBox.hide();

                                    if (result == null || result.length == 0) {
                                        CudaOnlineIDE.aceConsole.setValue("ERROR: Unexpected problem.");
                                        CudaOnlineIDE.aceConsole.gotoLine(CudaOnlineIDE.aceConsole.getLineCount());
                                        return;
                                    }

                                    if (result.length == 1) {
                                        CudaOnlineIDE.aceConsole.setValue(result[0]);
                                        CudaOnlineIDE.aceConsole.gotoLine(CudaOnlineIDE.aceConsole.getLineCount());
                                        return;
                                    }

                                    coiProject.setUuid(result[0]);

                                    for (COIProject coiProjectTmp : CudaOnlineIDE.ACTIVE_CUDA_COI_WORKSPACE.getItems()) {
                                        if (coiProject.getPath().equals(coiProjectTmp.getPath())) {
                                            coiProjectTmp.setUuid(result[0]);
                                            coiProjectTmp.setBuildedOnce(true);
                                        }
                                    }

                                    CudaOnlineIDE.aceConsole.setValue(CudaOnlineIDE.aceConsole.getValue() + COIConstants.LINE_SEPARATOR + result[1]);
                                    CudaOnlineIDE.aceConsole.gotoLine(CudaOnlineIDE.aceConsole.getLineCount());

                                    Info.display("Build", "Project building.");
                                }
                            });
                        }
                    });
                }
            }
        };

        CudaOnlineIDE.coiService.prebuildGenerating(coiProject, new AsyncCallback<Integer>() {

            @Override
            public void onFailure(Throwable caught) {
                GWT.log(caught.getMessage());
                Info.display("Build error", "Error in testing CMakeLists files.");
            }

            @Override
            public void onSuccess(Integer result) {

                if (result == COIConstants.GENERATING_WITH_QUESTION) {
                    ConfirmMessageBox confirmnBox = new ConfirmMessageBox("Generate CMakeLists.txt", "Are you sure you want to create new CMakeLists.txt and delete your changes?");
                    confirmnBox.addDialogHideHandler(hideHandler);
                    confirmnBox.show();
                } else {
                    hideHandler.onDialogHide(new DialogHideEvent(PredefinedButton.YES));
                }
            }
        });
    }

    /**
     * Method runs active project.
     */
    public static void runProjectMenuToolbar(final COIProject coiProject) {

        CudaOnlineIDE.aceConsole.setValue(COIConstants.EMPTY);
        CudaOnlineIDE.aceConsole.gotoLine(CudaOnlineIDE.aceConsole.getLineCount());

        final AutoProgressMessageBox messageBox = new AutoProgressMessageBox("Execute", "Executing project, please wait...");
        messageBox.setProgressText("Executing...");
        messageBox.auto();
        messageBox.show();

        CudaOnlineIDE.coiService.runProject(coiProject, new AsyncCallback<String>() {

            /**
             * Method is called if executing of project failed.
             */
            @Override
            public void onFailure(Throwable caught) {
                GWT.log(caught.getMessage());
                messageBox.hide();
                Info.display("Run error", "Error in running project.");
            }

            /**
             * Method is called if executing of project is successful.
             *
             * @param result Output of execution.
             */
            @Override
            public void onSuccess(String result) {

                messageBox.hide();

                if (result == null || result.isEmpty()) {
                    CudaOnlineIDE.aceConsole.setValue("ERROR: Unexpected problem.");
                    CudaOnlineIDE.aceConsole.gotoLine(CudaOnlineIDE.aceConsole.getLineCount());
                    return;
                }

                if (result.startsWith("ERROR")) {
                    CudaOnlineIDE.aceConsole.setValue(result);
                    CudaOnlineIDE.aceConsole.gotoLine(CudaOnlineIDE.aceConsole.getLineCount());
                    return;
                }

                CudaOnlineIDE.aceConsole.setValue(result);
                CudaOnlineIDE.aceConsole.gotoLine(CudaOnlineIDE.aceConsole.getLineCount());

                Info.display("Execute", "Project executing.");
            }
        });
    }

    /**
     * Method opens dialog with active project properties.
     */
    public static void openProjectPropertiesMenuToolbar(final COIProject coiProject) {

        Window propertiesPanel = PopUpWindow.propertiesPanel(coiProject);
        propertiesPanel.show();
    }

    /**
     * Method sets soft tabs in code editor.
     *
     * @param item Object determines if item is checked or not.
     */
    private void editorSoftTabsMenuToolbar(MenuItem item) {

        if (CudaOnlineIDE.fileTabPanel.getActiveWidget() == null) {
            return;
        }

        CudaOnlineIDE.aceSoftTabs = ((CheckMenuItem) item).isChecked();

        for (int i = 0; i < CudaOnlineIDE.fileTabPanel.getWidgetCount(); i++) {
            AceEditor aceEditor = (AceEditor) CudaOnlineIDE.fileTabPanel.getWidget(i);
            aceEditor.setUseSoftTabs(((CheckMenuItem) item).isChecked());
        }
    }

    /**
     * Method sets horizontal scrollbar is enabled or disabled in code editor.
     *
     * @param item Object determines if item is checked or not.
     */
    private void editorHScrollBarMenuToolbar(MenuItem item) {

        if (CudaOnlineIDE.fileTabPanel.getActiveWidget() == null) {
            return;
        }

        CudaOnlineIDE.aceHorizontalScroll = ((CheckMenuItem) item).isChecked();

        for (int i = 0; i < CudaOnlineIDE.fileTabPanel.getWidgetCount(); i++) {
            AceEditor aceEditor = (AceEditor) CudaOnlineIDE.fileTabPanel.getWidget(i);
            aceEditor.setHScrollBarAlwaysVisible(((CheckMenuItem) item).isChecked());
        }
    }

    /**
     * Method sets gutter is enabled or disabled in code editor.
     *
     * @param item Object determines if item is checked or not.
     */
    private void editorShowGutterMenuToolbar(MenuItem item) {

        if (CudaOnlineIDE.fileTabPanel.getActiveWidget() == null) {
            return;
        }

        CudaOnlineIDE.aceShowGutter = ((CheckMenuItem) item).isChecked();

        for (int i = 0; i < CudaOnlineIDE.fileTabPanel.getWidgetCount(); i++) {
            AceEditor aceEditor = (AceEditor) CudaOnlineIDE.fileTabPanel.getWidget(i);
            aceEditor.setShowGutter(((CheckMenuItem) item).isChecked());
        }
    }

    /**
     * Method sets highlighting is enabled or disabled in code editor.
     *
     * @param item Object determines if item is checked or not.
     */
    private void editorHighlightSelectedWordMenuToolbar(MenuItem item) {

        if (CudaOnlineIDE.fileTabPanel.getActiveWidget() == null) {
            return;
        }

        CudaOnlineIDE.aceHighlightSelectedWord = ((CheckMenuItem) item).isChecked();

        for (int i = 0; i < CudaOnlineIDE.fileTabPanel.getWidgetCount(); i++) {
            AceEditor aceEditor = (AceEditor) CudaOnlineIDE.fileTabPanel.getWidget(i);
            aceEditor.setHighlightSelectedWord(((CheckMenuItem) item).isChecked());
        }
    }

    /**
     * Method sets code editor is read only or not.
     *
     * @param item Object determines if item is checked or not.
     */
    private void editorSetReadOnlyMenuToolbar(MenuItem item) {

        if (CudaOnlineIDE.fileTabPanel.getActiveWidget() == null) {
            return;
        }

        CudaOnlineIDE.aceReadOnly = ((CheckMenuItem) item).isChecked();

        for (int i = 0; i < CudaOnlineIDE.fileTabPanel.getWidgetCount(); i++) {
            AceEditor aceEditor = (AceEditor) CudaOnlineIDE.fileTabPanel.getWidget(i);
            aceEditor.setReadOnly(((CheckMenuItem) item).isChecked());
        }
    }

    /**
     * Method sets autocomplete is enabled or disabled in code editor.
     *
     * @param item Object determines if item is checked or not.
     */
    private void editorAutocompleteMenuToolbar(MenuItem item) {

        if (CudaOnlineIDE.fileTabPanel.getActiveWidget() == null) {
            return;
        }

        CudaOnlineIDE.aceAutocomplete = ((CheckMenuItem) item).isChecked();

        for (int i = 0; i < CudaOnlineIDE.fileTabPanel.getWidgetCount(); i++) {
            AceEditor aceEditor = (AceEditor) CudaOnlineIDE.fileTabPanel.getWidget(i);
            aceEditor.setAutocompleteEnabled(((CheckMenuItem) item).isChecked());
        }
    }

    /**
     * Method sets tab size in code editor.
     */
    private void editorSetTabSizeMenuToolbar() {

        if (CudaOnlineIDE.fileTabPanel.getActiveWidget() == null) {
            return;
        }

        Window tabSizePanel = PopUpWindow.tabSizePanel();
        tabSizePanel.show();

        tabSizePanel.addHideHandler(new HideHandler() {

            /**
             * Method is called when tab size panel is closed.
             */
            @Override
            public void onHide(HideEvent event) {

                Map<String, Object> popUpData = PopUpWindow.data;

                if (popUpData.containsKey(PopUpWindow.TABSIZE)) {

                    CudaOnlineIDE.aceTabSize = ((Double) popUpData.get(PopUpWindow.TABSIZE)).intValue();

                    for (int i = 0; i < CudaOnlineIDE.fileTabPanel.getWidgetCount(); i++) {
                        AceEditor aceEditor = (AceEditor) CudaOnlineIDE.fileTabPanel.getWidget(i);
                        aceEditor.setTabSize(CudaOnlineIDE.aceTabSize);
                    }
                }
            }
        });
    }

    /**
     * Method navigates to selected line in code editor.
     */
    private void editorGoToLineMenuToolbar() {

        if (CudaOnlineIDE.fileTabPanel.getActiveWidget() == null) {
            return;
        }

        final AceEditor editor = (AceEditor) CudaOnlineIDE.fileTabPanel.getActiveWidget();
        AceEditorCursorPosition aecp = editor.getCursorPosition();
        int cursorPosition;

        if (aecp == null || aecp.getRow() == -1) {
            cursorPosition = 1;
        } else {
            cursorPosition = aecp.getRow() + 1;
        }

        Window goToLinePanel = PopUpWindow.goToLinePanel(editor.getLineCount(), cursorPosition);
        goToLinePanel.show();

        goToLinePanel.addHideHandler(new HideHandler() {

            /**
             * Method is called when go to line panel is closed.
             */
            @Override
            public void onHide(HideEvent event) {

                Map<String, Object> popUpData = PopUpWindow.data;

                if (popUpData.containsKey(PopUpWindow.GOTOLINE)) {
                    editor.gotoLine(((Double) popUpData.get(PopUpWindow.GOTOLINE)).intValue());
                }
            }
        });
    }

    /**
     * Method inserts text at cursor in code editor.
     */
    private void editorInserTextMenuToolbar() {

        if (CudaOnlineIDE.fileTabPanel.getActiveWidget() == null) {
            return;
        }

        final AceEditor editor = (AceEditor) CudaOnlineIDE.fileTabPanel.getActiveWidget();

        Window insertAtCursorPanel = PopUpWindow.insertAtCursorPanel();
        insertAtCursorPanel.show();

        insertAtCursorPanel.addHideHandler(new HideHandler() {

            /**
             * Method is called when insert text at cursor panel is closed.
             */
            @Override
            public void onHide(HideEvent event) {

                Map<String, Object> popUpData = PopUpWindow.data;

                if (popUpData.containsKey(PopUpWindow.INSERTATCURSOR)) {
                    editor.insertAtCursor((String) popUpData.get(PopUpWindow.INSERTATCURSOR));
                }
            }
        });
    }

    /**
     * Method logging out logged user.
     */
    private void logoutUser() {

        CudaOnlineIDE.loginService.logout(new AsyncCallback<Void>() {

            /**
             * Method is called if logged out of user failed.
             */
            @Override
            public void onFailure(Throwable caught) {
                GWT.log(caught.getMessage());
                Info.display("Logout error", "User cannot be logged out.");
            }

            /**
             * Method is called if logged out of user is successful.
             *
             * @param result Empty result.
             */
            @Override
            public void onSuccess(Void result) {

                CudaOnlineIDE.this.closeWorkspaceMenuToolbar();
                CudaOnlineIDE.ACTIVE_CUDA_FOLDER = COIConstants.EMPTY;
                CudaOnlineIDE.ACTIVE_USER = COIConstants.EMPTY;

                Info.display("Logout", "User was logged out.");
                displayLoginWindow();
            }
        });
    }

    /**
     * Method opens user manual.
     */
    private void helpMenuToolbar() {

        String url = "https://dl.dropboxusercontent.com/u/93282530/CudaIDE_manual.pdf";
        com.google.gwt.user.client.Window.open(url, "_blank", "menubar=yes,location=yes,resizable=yes,scrollbars=yes,status=yes");
    }

    /**
     * Method opens CUDA programming guide.
     */
    private void programmingGuideMenuToolbar() {

        String url = "http://docs.nvidia.com/cuda/cuda-c-programming-guide/";
        com.google.gwt.user.client.Window.open(url, "_blank", "menubar=yes,location=yes,resizable=yes,scrollbars=yes,status=yes");
    }

    /**
     * Method opens message box with information about this project.
     */
    private void aboutMenuToolbar() {

        Window aboutPanel = PopUpWindow.aboutPanel();
        aboutPanel.show();
    }

    /**
     * Method toggles project explorer panel.
     *
     * @param item Object determines if item is checked or not.
     */
    private void toggleProjectExplorerMenuToolbar(MenuItem item) {

        if (((CheckMenuItem) item).isChecked()) {
            CudaOnlineIDE.coiAppContainer.show(LayoutRegion.WEST);
        } else {
            CudaOnlineIDE.coiAppContainer.hide(LayoutRegion.WEST);
        }
        CudaOnlineIDE.westContainerOpen = ((CheckMenuItem) item).isChecked();
    }

    /**
     * Method toggles outline panel.
     *
     * @param item Object determines if item is checked or not.
     */
    private void toggleOutlineMenuToolbar(MenuItem item) {

        if (((CheckMenuItem) item).isChecked()) {
            CudaOnlineIDE.coiAppContainer.show(LayoutRegion.EAST);
        } else {
            CudaOnlineIDE.coiAppContainer.hide(LayoutRegion.EAST);
        }
        CudaOnlineIDE.eastContainerOpen = ((CheckMenuItem) item).isChecked();
    }

    /**
     * Method toggles console panel.
     *
     * @param item Object determines if item is checked or not.
     */
    private void toggleConsoleMenuToolbar(MenuItem item) {

        if (((CheckMenuItem) item).isChecked()) {
            CudaOnlineIDE.coiAppContainer.show(LayoutRegion.SOUTH);
        } else {
            CudaOnlineIDE.coiAppContainer.hide(LayoutRegion.SOUTH);
        }
        CudaOnlineIDE.southContainerOpen = ((CheckMenuItem) item).isChecked();
    }

    /**
     * Method updates build configuration combo box by selected active project.
     */
    public static void updateBuildConfigurationComboBoxByActiveProject() {

        CudaOnlineIDE.buildConfigurationCombo.setValue(CudaOnlineIDE.ACTIVE_CUDA_COI_PROJECT.getBuildConfiguration().getActive());
    }

    /**
     * Method check session information if contains active user.
     *
     * @param sessionID Session ID.
     */
    private void checkWithServerIfSessionIdIsStillLegal(String sessionID) {
        CudaOnlineIDE.loginService.loginFromSessionServer(new AsyncCallback<COIUser>() {
            /**
             * Method is called when check of session failed.
             */
            @Override
            public void onFailure(Throwable caught) {
                GWT.log(caught.getMessage());
                displayLoginWindow();
            }

            /**
             * Method is called when check of session is successful.
             *
             * @param user Active user.
             */
            @Override
            public void onSuccess(COIUser user) {
                if (user == null) {
                    displayLoginWindow();
                } else {
                    if (!user.isLoggedIn()) {
                        displayLoginWindow();
                    } else {
                        CudaOnlineIDE.ACTIVE_CUDA_FOLDER = WorkspaceConstants.CUDA_WORK_FOLDER
                                + user.getName()
                                + COIConstants.FWD_SLASH;
                        CudaOnlineIDE.ACTIVE_USER = user.getName();
                    }
                }
            }
        });
    }

    /**
     * Method show login panel.
     */
    private void displayLoginWindow() {
        Window loginPanel = Login.loginPanel();
        loginPanel.show();
    }

    ;
	
	/**
	 * Tree builder of chosen workspace.
	 * 
	 * @param coiWorkspace Workspace for building tree.
	 * @param dp Data properties of tree.
	 */
	public static void coiDataTreeConstructor(COIWorkspace coiWorkspace, COIDataProperties dp) {

        CudaOnlineIDE.coiDataTreeStore = new TreeStore<COIData>(dp.key());

        COIData tWorkspace = new COIData(coiWorkspace.getName(), coiWorkspace.getPath(), null, coiWorkspace);
        CudaOnlineIDE.coiDataTreeStore.add(tWorkspace);

        if (coiWorkspace.getItems() != null && !coiWorkspace.getItems().isEmpty()) {

            for (COIProject coiProject : coiWorkspace.getItems()) {
                COIData tProject = new COIData(coiProject.getName(), coiProject.getPath(), null, coiProject);
                CudaOnlineIDE.coiDataTreeStore.add(tWorkspace, tProject);

                if (coiProject.getItems() != null && !coiProject.getItems().isEmpty()) {

                    for (COIFolder coiFolder : coiProject.getItems()) {

                        if (coiFolder.getName().isEmpty() && coiFolder.getPath().equals(coiProject.getPath())) {

                            if (coiFolder.getItems() != null && !coiFolder.getItems().isEmpty()) {

                                for (COIFile coiFile : coiFolder.getItems()) {
                                    COIData tFile = new COIData(coiFile.getName() + COIConstants.COMMA + coiFile.getExtension(), coiFile.getPath(), null, coiFile);
                                    CudaOnlineIDE.coiDataTreeStore.add(tProject, tFile);
                                }
                            }

                        } else {

                            COIData tFolder = new COIData(coiFolder.getName(), coiFolder.getPath(), null, coiFolder);
                            CudaOnlineIDE.coiDataTreeStore.add(tProject, tFolder);

                            if (coiFolder.getItems() != null && !coiFolder.getItems().isEmpty()) {

                                for (COIFile coiFile : coiFolder.getItems()) {
                                    COIData tFile = new COIData(coiFile.getName() + COIConstants.COMMA + coiFile.getExtension(), coiFile.getPath(), null, coiFile);
                                    CudaOnlineIDE.coiDataTreeStore.add(tFolder, tFile);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Method implements actions to project explorer tree.
     *
     * @param dp Data properties of tree.
     *
     * @return Tree widget which is placed in project explorer.
     */
    public static Widget coiDataTreeActionProvider(COIDataProperties dp) {

        CudaOnlineIDE.coiDataTree = new Tree<COIData, String>(CudaOnlineIDE.coiDataTreeStore, dp.name()) {

            /**
             * Handler for double click to project explorer tree object.
             */
            @Override
            protected void onDoubleClick(Event event) {
                TreeNode<COIData> node = findNode(event.getEventTarget().<Element>cast());

                if (node == null) {
                    return;
                }

                COIData data = node.getModel();

                if (data.getCoiObject().getTypeOfCOI() == COIEnum.FILE) {
                    if (CudaOnlineIDE.openFileTab(data)) {
                        Info.display(COIConstants.INFO_OPEN, "Open " + data.getName());
                    } else {
                        Info.display(COIConstants.INFO_SWITCH, "Switch to " + data.getName());
                    }
                }
                super.onDoubleClick(event);
            }
        };

        CudaOnlineIDE.coiDataTree.setIconProvider(new IconProvider<COIData>() {

            /**
             * Icon provider for project explore tree.
             *
             * @param model Tree object.
             *
             * @return Icon for chosen tree object.
             */
            @Override
            public ImageResource getIcon(COIData model) {

                if (model.getCoiObject().getTypeOfCOI() == COIEnum.WORKSPACE) {
                    return MenuToolbarIcons.PROVIDER.workspace();
                } else if (model.getCoiObject().getTypeOfCOI() == COIEnum.PROJECT) {

                    if (CudaOnlineIDE.ACTIVE_CUDA_COI_PROJECT != null) {

                        COIProject coiProject = (COIProject) model.getCoiObject();

                        if (CudaOnlineIDE.ACTIVE_CUDA_COI_PROJECT.getPath() == coiProject.getPath()) {
                            return MenuToolbarIcons.PROVIDER.projectActive();
                        }
                    }

                    return MenuToolbarIcons.PROVIDER.project();
                } else if (model.getCoiObject().getTypeOfCOI() == COIEnum.FOLDER) {
                    return MenuToolbarIcons.PROVIDER.folder();
                } else if (model.getCoiObject().getTypeOfCOI() == COIEnum.FILE) {

                    COIFile coiFile = (COIFile) model.getCoiObject();

                    if (coiFile.getExtension().equals("c")) {
                        return MenuToolbarIcons.PROVIDER.c();
                    } else if (coiFile.getExtension().equals("cpp")) {
                        return MenuToolbarIcons.PROVIDER.cpp();
                    } else if (coiFile.getExtension().equals("cu")) {
                        return MenuToolbarIcons.PROVIDER.cu();
                    } else if (coiFile.getExtension().equals("cuh")) {
                        return MenuToolbarIcons.PROVIDER.cuh();
                    } else if (coiFile.getExtension().equals("h")) {
                        return MenuToolbarIcons.PROVIDER.h();
                    } else if (coiFile.getExtension().equals("res")) {
                        return MenuToolbarIcons.PROVIDER.res();
                    } else if (coiFile.getExtension().equals("txt")) {
                        return MenuToolbarIcons.PROVIDER.txt();
                    } else {
                        return MenuToolbarIcons.PROVIDER.other();
                    }

                } else {
                    return null;
                }
            }
        });

        CudaOnlineIDE.coiDataTree.setContextMenu(new Menu());
        CudaOnlineIDE.coiDataTree.addBeforeShowContextMenuHandler(new BeforeShowContextMenuHandler() {

            /**
             * Method builds context menu for choose tree object.
             */
            @Override
            public void onBeforeShowContextMenu(BeforeShowContextMenuEvent event) {
                CudaOnlineIDE.buildContextMenu(event);
            }
        });

        return CudaOnlineIDE.coiDataTree;
    }

    /**
     * Method determines if is necessary to open new file tab, or if tab with
     * this file is opened.
     *
     * @param data File to open.
     *
     * @return True if is necessary open new file tab.
     */
    private static boolean openFileTab(COIData data) {

        int widgetCount;

        if ((widgetCount = CudaOnlineIDE.fileTabPanel.getWidgetCount()) != 0) {

            for (int i = 0; i < widgetCount; i++) {
                Widget widget = CudaOnlineIDE.fileTabPanel.getWidget(i);
                TabItemConfig config = CudaOnlineIDE.fileTabPanel.getConfig(widget);
                String choosenTab = CudaOnlineIDE.getExclusivePath(data.getPath());

                if (choosenTab == config.getText() || (choosenTab + " *") == config.getText()) {
                    CudaOnlineIDE.fileTabPanel.setActiveWidget(widget);
                    return false;
                }
            }
        }

        CudaOnlineIDE.coiService.addTextToCOIFileServiceMethod(data.getCoiObject(), new AsyncCallback<COIObject>() {

            /**
             * Method is called if retrieving of text of file failed.
             */
            public void onFailure(Throwable caught) {
                GWT.log(caught.getMessage());
                Info.display("Error", "Error in retrieving text from file.");
            }

            /**
             * Method is called if retrieving of text of file is successful.
             *
             * @param coiObject File to retrieving text.
             */
            public void onSuccess(COIObject coiObject) {
                CudaOnlineIDE.openNewFileAceEditor(coiObject);
            }
        });

        return true;
    }

    /**
     * Method open chosen file and create new ACE.
     *
     * @param coiObject File to open in COIObject.
     */
    private static void openNewFileAceEditor(COIObject coiObject) {

        COIFile coiFile = (COIFile) coiObject;

        AceEditor editor = new AceEditor();
        editor.setWidth(COIConstants.SIZE_100_PERCENTAGE);
        editor.setHeight(COIConstants.SIZE_100_PERCENTAGE);

        CudaOnlineIDE.fileTabPanel.add(editor, new TabItemConfig(CudaOnlineIDE.getExclusivePath(coiFile.getPath()), true));
        CudaOnlineIDE.fileTabPanel.setActiveWidget(editor);

        editor.startEditor();
        editor.setText(coiFile.getText());

        if (coiFile.getExtension().equals("c") || coiFile.getExtension().equals("cpp") || coiFile.getExtension().equals("cu") || coiFile.getExtension().equals("h") || coiFile.getExtension().equals("cuh")) {
            editor.setMode(AceEditorMode.C_CPP);
        } else {
            editor.setMode(AceEditorMode.PLAIN_TEXT);
        }
        editor.setTheme(CudaOnlineIDE.aceActualTheme);
        editor.setFontSize(CudaOnlineIDE.aceFontSize);

        editor.setUseSoftTabs(CudaOnlineIDE.aceSoftTabs);
        editor.setHScrollBarAlwaysVisible(CudaOnlineIDE.aceHorizontalScroll);
        editor.setShowGutter(CudaOnlineIDE.aceShowGutter);
        editor.setHighlightSelectedWord(CudaOnlineIDE.aceHighlightSelectedWord);
        editor.setReadOnly(CudaOnlineIDE.aceReadOnly);
        editor.setAutocompleteEnabled(CudaOnlineIDE.aceAutocomplete);
        editor.setTabSize(CudaOnlineIDE.aceTabSize);

        CudaOnlineIDE.addCCPHandler(editor.getElement());

        editor.addOnChangeHandler(new AceEditorCallback() {

            /**
             * Handler catches change of text in active tab.
             */
            @Override
            public void invokeAceCallback(JavaScriptObject obj) {
                String activeTab = CudaOnlineIDE.fileTabPanel.getConfig(CudaOnlineIDE.fileTabPanel.getActiveWidget()).getText();

                if (!activeTab.endsWith(" *")) {
                    TabItemConfig config = new TabItemConfig(activeTab + " *", true);
                    CudaOnlineIDE.fileTabPanel.update(CudaOnlineIDE.fileTabPanel.getActiveWidget(), config);
                }
            }
        });

        editor.addCommand(new AceCommandDescription("increaseFontSize",
                new AceCommandDescription.ExecAction() {

                    /**
                     * Handler catches request for increase font size.
                     */
                    @Override
                    public Object exec(AceEditor editor) {
                        int fontSize = editor.getFontSize();

                        CudaOnlineIDE.aceFontSize = fontSize + 1;

                        for (int i = 0; i < CudaOnlineIDE.fileTabPanel.getWidgetCount(); i++) {
                            AceEditor aceEditor = (AceEditor) CudaOnlineIDE.fileTabPanel.getWidget(i);
                            aceEditor.setFontSize(fontSize + 1);
                        }
                        return null;
                    }
                }).withBindKey("Ctrl-=|Ctrl-+"));

        editor.addCommand(new AceCommandDescription("decreaseFontSize",
                new AceCommandDescription.ExecAction() {

                    /**
                     * Handler catches request for decrease font size.
                     */
                    @Override
                    public Object exec(AceEditor editor) {
                        int fontSize = editor.getFontSize();
                        fontSize = Math.max(fontSize - 1, 1);

                        CudaOnlineIDE.aceFontSize = fontSize;

                        for (int i = 0; i < CudaOnlineIDE.fileTabPanel.getWidgetCount(); i++) {
                            AceEditor aceEditor = (AceEditor) CudaOnlineIDE.fileTabPanel.getWidget(i);
                            aceEditor.setFontSize(fontSize);
                        }
                        return null;
                    }
                }).withBindKey("Ctrl+-|Ctrl-_"));

        editor.addCommand(new AceCommandDescription("resetFontSize",
                new AceCommandDescription.ExecAction() {

                    /**
                     * Handler catches request to reset font size to default
                     * value.
                     */
                    @Override
                    public Object exec(AceEditor editor) {

                        CudaOnlineIDE.aceFontSize = 14;

                        for (int i = 0; i < CudaOnlineIDE.fileTabPanel.getWidgetCount(); i++) {
                            AceEditor aceEditor = (AceEditor) CudaOnlineIDE.fileTabPanel.getWidget(i);
                            aceEditor.setFontSize(14);
                        }
                        return null;
                    }
                }).withBindKey("Ctrl+0|Ctrl-Numpad0"));

        editor.addCommand(new AceCommandDescription("eclipseTheme",
                new AceCommandDescription.ExecAction() {

                    /**
                     * Handler catches request to change theme to Eclipse.
                     */
                    @Override
                    public Object exec(AceEditor editor) {

                        Info.display("Theme change", "Default theme was chosen.");

                        CudaOnlineIDE.aceActualTheme = AceEditorTheme.ECLIPSE;

                        for (int i = 0; i < CudaOnlineIDE.fileTabPanel.getWidgetCount(); i++) {
                            AceEditor aceEditor = (AceEditor) CudaOnlineIDE.fileTabPanel.getWidget(i);
                            aceEditor.setTheme(AceEditorTheme.ECLIPSE);
                        }
                        return null;
                    }
                }).withBindKey("Ctrl-Q"));

        editor.addCommand(new AceCommandDescription("xcodeTheme",
                new AceCommandDescription.ExecAction() {

                    /**
                     * Handler catches request to change theme to XCode.
                     */
                    @Override
                    public Object exec(AceEditor editor) {

                        Info.display("Theme change", "XCode theme was chosen.");

                        CudaOnlineIDE.aceActualTheme = AceEditorTheme.XCODE;

                        for (int i = 0; i < CudaOnlineIDE.fileTabPanel.getWidgetCount(); i++) {
                            AceEditor aceEditor = (AceEditor) CudaOnlineIDE.fileTabPanel.getWidget(i);
                            aceEditor.setTheme(AceEditorTheme.XCODE);
                        }
                        return null;
                    }
                }).withBindKey("Ctrl-E"));

        editor.addCommand(new AceCommandDescription("twilightTheme",
                new AceCommandDescription.ExecAction() {

                    /**
                     * Handler catches request to change theme to Night.
                     */
                    @Override
                    public Object exec(AceEditor editor) {

                        Info.display("Theme change", "Night view theme was chosen.");

                        CudaOnlineIDE.aceActualTheme = AceEditorTheme.TWILIGHT;

                        for (int i = 0; i < CudaOnlineIDE.fileTabPanel.getWidgetCount(); i++) {
                            AceEditor aceEditor = (AceEditor) CudaOnlineIDE.fileTabPanel.getWidget(i);
                            aceEditor.setTheme(AceEditorTheme.TWILIGHT);
                        }
                        return null;
                    }
                }).withBindKey("Ctrl-W"));

        editor.addCommand(new AceCommandDescription("fullscreenEnabled",
                new AceCommandDescription.ExecAction() {

                    /**
                     * Handler catches request to enable full screen view.
                     */
                    @Override
                    public Object exec(AceEditor editor) {

                        Info.display("Extended view open", "To close extended view press Ctrl-Shift-I.");

                        for (int i = 0; i < CudaOnlineIDE.fileTabPanel.getWidgetCount(); i++) {
                            Widget widget = CudaOnlineIDE.fileTabPanel.getWidget(i);
                            TabItemConfig tiConfig = CudaOnlineIDE.fileTabPanel.getConfig(widget);
                            tiConfig.setClosable(false);
                            CudaOnlineIDE.fileTabPanel.update(widget, tiConfig);
                        }

                        CudaOnlineIDE.coiAppContainer.hide(LayoutRegion.NORTH);
                        CudaOnlineIDE.coiAppContainer.hide(LayoutRegion.WEST);
                        CudaOnlineIDE.coiAppContainer.hide(LayoutRegion.EAST);
                        CudaOnlineIDE.coiAppContainer.hide(LayoutRegion.SOUTH);
                        return null;
                    }
                }).withBindKey("Ctrl-I"));

        editor.addCommand(new AceCommandDescription("fullscreenDisabled",
                new AceCommandDescription.ExecAction() {

                    /**
                     * Handlers catches request to disable full screen view.
                     */
                    @Override
                    public Object exec(AceEditor editor) {

                        Info.display("Extended view close", "To open extended view press Ctrl-I.");

                        for (int i = 0; i < CudaOnlineIDE.fileTabPanel.getWidgetCount(); i++) {
                            Widget widget = CudaOnlineIDE.fileTabPanel.getWidget(i);
                            TabItemConfig tiConfig = CudaOnlineIDE.fileTabPanel.getConfig(widget);
                            tiConfig.setClosable(true);
                            CudaOnlineIDE.fileTabPanel.update(widget, tiConfig);
                        }

                        CudaOnlineIDE.coiAppContainer.show(LayoutRegion.NORTH);

                        if (CudaOnlineIDE.westContainerOpen) {
                            CudaOnlineIDE.coiAppContainer.show(LayoutRegion.WEST);
                        }

                        if (CudaOnlineIDE.eastContainerOpen) {
                            CudaOnlineIDE.coiAppContainer.show(LayoutRegion.EAST);
                        }

                        if (CudaOnlineIDE.southContainerOpen) {
                            CudaOnlineIDE.coiAppContainer.show(LayoutRegion.SOUTH);
                        }

                        return null;
                    }
                }).withBindKey("Ctrl-Shift-I"));

        editor.addCommand(new AceCommandDescription("saveActualFile",
                new AceCommandDescription.ExecAction() {

                    /**
                     * Handler catches request to save active file.
                     */
                    @Override
                    public Object exec(AceEditor editor) {
                        CudaOnlineIDE.saveActualFileMenuToolbar();
                        return null;
                    }
                }).withBindKey("Ctrl-S"));

        CudaOnlineIDE.getTagsFromFileToEastPanel(coiFile.getPath());
    }

    private static void buildContextMenu(BeforeShowContextMenuEvent event) {

        COIData coiData = CudaOnlineIDE.coiDataTree.getSelectionModel().getSelectedItem();

        if (coiData != null && coiData.getCoiObject() != null && coiData.getCoiObject().getTypeOfCOI() != null) {

            Menu contextMenu = event.getMenu();
            contextMenu.setWidth(170);
            contextMenu.clear();

            switch (coiData.getCoiObject().getTypeOfCOI()) {

                case FILE:
                    contextMenu.add(TreeContextMenu.getUniversalDownloadContextMenu());
                    contextMenu.add(TreeContextMenu.getDeleteInContextMenu());
                    break;

                case FOLDER:
                    contextMenu.add(TreeContextMenu.getNewFileContextMenu());
                    contextMenu.add(TreeContextMenu.getUploadFileContextMenu());
                    contextMenu.add(TreeContextMenu.getUniversalDownloadContextMenu());
                    contextMenu.add(TreeContextMenu.getDeleteInContextMenu());
                    break;

                case PROJECT:
                    contextMenu.add(TreeContextMenu.getNewFolderContextMenu());
                    contextMenu.add(TreeContextMenu.getUploadFolderContextMenu());
                    contextMenu.add(TreeContextMenu.getUniversalDownloadContextMenu());
                    contextMenu.add(TreeContextMenu.getDeleteInContextMenu());
                    contextMenu.add(TreeContextMenu.getSetAsActiveContextMenu());
                    contextMenu.add(TreeContextMenu.getPropertiesContextMenu());
                    contextMenu.add(TreeContextMenu.getGenerateCMakeListsContextMenu());
                    contextMenu.add(TreeContextMenu.getBuildContextMenu());
                    contextMenu.add(TreeContextMenu.getRunContextMenu());
                    break;

                case WORKSPACE:
                    contextMenu.add(TreeContextMenu.getNewProjectContextMenu());
                    contextMenu.add(TreeContextMenu.getUploadProjectContextMenu());
                    contextMenu.add(TreeContextMenu.getUniversalDownloadContextMenu());
                    break;
            }
        }

        CudaOnlineIDE.coiDataTree.setSize(COIConstants.SIZE_100_PERCENTAGE, COIConstants.SIZE_100_PERCENTAGE);
        CudaOnlineIDE.coiDataTree.focus();
    }

    /**
     * Tree builder of file tags.
     *
     * @param fileDataList File tags list.
     * @param dp Data properties of tags output.
     */
    public static void fileDataTreeConstructor(List<COIFileData> fileDataList, FileDataProperties dp) {

        CudaOnlineIDE.fileDataTreeStore = new TreeStore<COIFileData>(dp.key());

        while (!fileDataList.isEmpty()) {

            for (COIFileData fileData : fileDataList) {
                CudaOnlineIDE.fileDataTreeStore.add(fileData);
                fileDataList.remove(fileData);
                fileDataList = CudaOnlineIDE.recursiveAddToActualFileData(fileData, fileDataList);
                break;
            }
        }
    }

    /**
     * Method implements actions to output tree.
     *
     * @param dp Data properties of output tree.
     *
     * @return Tree widget which is placed in output.
     */
    public static Widget fileDataTreeActionProvider(FileDataProperties dp) {

        final Tree<COIFileData, String> tree = new Tree<COIFileData, String>(CudaOnlineIDE.fileDataTreeStore, dp.output()) {

            /**
             * Double click on output object handler.
             */
            @Override
            protected void onDoubleClick(Event event) {
                TreeNode<COIFileData> node = findNode(event.getEventTarget().<Element>cast());

                if (node == null) {
                    return;
                }

                COIFileData data = node.getModel();
                AceEditor editor = (AceEditor) CudaOnlineIDE.fileTabPanel.getActiveWidget();
                editor.gotoLine(data.getLine());
            }
        };

        tree.setIconProvider(new IconProvider<COIFileData>() {

            /**
             * Icon provider for output tree.
             *
             * @param model Tree object.
             *
             * @return Icon for chosen tree object.
             */
            @Override
            public ImageResource getIcon(COIFileData model) {

                if (model.getKind().equals("class")) {
                    return MenuToolbarIcons.PROVIDER.outClass();
                } else if (model.getKind().equals("keyword")) {
                    return MenuToolbarIcons.PROVIDER.outKeyword();
                } else if (model.getKind().equals("enum")) {
                    return MenuToolbarIcons.PROVIDER.outEnum();
                } else if (model.getKind().equals("enumerator")) {
                    return MenuToolbarIcons.PROVIDER.outEnumerator();
                } else if (model.getKind().equals("function") && model.getAccess().equals("private")) {
                    return MenuToolbarIcons.PROVIDER.outFunctionPrivate();
                } else if (model.getKind().equals("function") && model.getAccess().equals("protected")) {
                    return MenuToolbarIcons.PROVIDER.outFunctionProtected();
                } else if (model.getKind().equals("function") && (model.getAccess().equals("public") || model.getAccess().equals(COIConstants.EMPTY))) {
                    return MenuToolbarIcons.PROVIDER.outFunctionPublic();
                } else if (model.getKind().equals("macro")) {
                    return MenuToolbarIcons.PROVIDER.outMacro();
                } else if (model.getKind().equals("member") && model.getAccess().equals("private")) {
                    return MenuToolbarIcons.PROVIDER.outMemberPrivate();
                } else if (model.getKind().equals("member") && model.getAccess().equals("protected")) {
                    return MenuToolbarIcons.PROVIDER.outMemberProtected();
                } else if (model.getKind().equals("member") && (model.getAccess().equals("public") || model.getAccess().equals(COIConstants.EMPTY))) {
                    return MenuToolbarIcons.PROVIDER.outMemberPublic();
                } else if (model.getKind().equals("struct")) {
                    return MenuToolbarIcons.PROVIDER.outStruct();
                } else if (model.getKind().equals("typedef")) {
                    return MenuToolbarIcons.PROVIDER.outTypedef();
                } else if (model.getKind().equals("union")) {
                    return MenuToolbarIcons.PROVIDER.outUnion();
                } else if (model.getKind().equals("word")) {
                    return MenuToolbarIcons.PROVIDER.outWord();
                } else {
                    return null;
                }
            }
        });

        return tree;
    }

    /**
     * Method gets tag from chosen file and sets result to east panel.
     *
     * @param filePath File path of file to tag.
     */
    private static void getTagsFromFileToEastPanel(String filePath) {

        CudaOnlineIDE.coiService.getTagsFromFile(filePath, new AsyncCallback<List<COIFileData>>() {

            /**
             * Method is call if tagging of file failed.
             */
            public void onFailure(Throwable caught) {
                GWT.log(caught.getMessage());
                Info.display("Output error", "File cannot be tagged to output.");
            }

            /**
             * Method is call if tagging of file is successful.
             *
             * @param fileDataList List of tags from chosen file.
             */
            public void onSuccess(List<COIFileData> fileDataList) {
                FileDataProperties fdp = GWT.create(FileDataProperties.class);
                CudaOnlineIDE.fileDataTreeConstructor(fileDataList, fdp);
                CudaOnlineIDE.eastPanel.add(CudaOnlineIDE.fileDataTreeActionProvider(fdp));
            }
        });

    }

    /**
     * Implements control function of CCP (copy/cut/paste) events to widget.
     *
     * @param elementID Id of object to control CCP events.
     */
    private static native void addCCPHandler(Element elementID) /*-{
     elementID.oncut = function(e) {     	
     @cz.utb.fai.cudaonlineide.client.CudaOnlineIDE::handleCut()();
     };
	      
     elementID.oncopy = function(e) {      	
     @cz.utb.fai.cudaonlineide.client.CudaOnlineIDE::handleCopy()();
     };
	      
     elementID.onpaste = function(e) {
     @cz.utb.fai.cudaonlineide.client.CudaOnlineIDE::handlePaste()();
     };
     }-*/;

    /**
     * Handler for cut event on connected widget.
     */
    private static void handleCut() {
        Info.display("Cut", "Text was cut to clipboard.");
    }

    /**
     * Handler for copy event on connected widget.
     */
    private static void handleCopy() {
        Info.display("Copy", "Text was copy to clipboard.");
    }

    /**
     * Handler for paste event on connected widget.
     */
    private static void handlePaste() {
        Info.display("Paste", "Text was paste from clipboard.");
    }

    /**
     * Method returns exclusive file path for file in project.
     *
     * @param filePath File path.
     *
     * @return Exclusive path.
     */
    public static String getExclusivePath(String filePath) {

        if (filePath == null) {
            return COIConstants.EMPTY;
        }

        List<String> subPathList = getPathListFromPath(filePath, new ArrayList<String>());
        String exclusivePath;
        int count = subPathList.size();

        if (count > 2) {
            exclusivePath = subPathList.get(count - 3) + subPathList.get(count - 2) + subPathList.get(count - 1);
        } else if (count == 2) {
            exclusivePath = subPathList.get(0) + subPathList.get(1);
        } else if (count == 1) {
            exclusivePath = subPathList.get(0);
        } else {
            return COIConstants.EMPTY;
        }

        if (exclusivePath.startsWith(COIConstants.FWD_SLASH) && exclusivePath.length() > 1) {
            exclusivePath = exclusivePath.substring(1);
        }

        return exclusivePath;
    }

    /**
     * Method divide path to individual directory.
     *
     * @param filePath File path to divide.
     * @param subPathList List of individual directory.
     *
     * @return List of individual directory.
     */
    private static List<String> getPathListFromPath(String filePath, List<String> subPathList) {

        int index = filePath.lastIndexOf(COIConstants.FWD_SLASH);

        if (index == -1) {
            return subPathList;
        }

        subPathList.add(0, filePath.substring(index));
        String subPath = filePath.substring(0, index);

        return getPathListFromPath(subPath, subPathList);
    }

    /**
     * Method build output tree.
     *
     * @param fileData Parent output data object.
     * @param fileDataList All other output data object list.
     *
     * @return Builded output data object list.
     */
    private static List<COIFileData> recursiveAddToActualFileData(COIFileData fileData, List<COIFileData> fileDataList) {

        int compareSize = -1;
        while (compareSize != fileDataList.size()) {

            compareSize = fileDataList.size();

            for (COIFileData fData : fileDataList) {

                if (fileData.getKind().equals(fData.getParent()[0]) && fileData.getParents().equals(fData.getParent()[1])) {
                    CudaOnlineIDE.fileDataTreeStore.add(fileData, fData);
                    fileDataList.remove(fData);
                    fileDataList = CudaOnlineIDE.recursiveAddToActualFileData(fData, fileDataList);
                    break;
                }
            }
        }
        return fileDataList;
    }
}
