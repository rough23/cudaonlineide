package cz.utb.fai.cudaonlineide.client.popup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.StringLabelProvider;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.event.SubmitCompleteEvent;
import com.sencha.gxt.widget.core.client.event.SubmitCompleteEvent.SubmitCompleteHandler;
import com.sencha.gxt.widget.core.client.event.SubmitEvent;
import com.sencha.gxt.widget.core.client.event.SubmitEvent.SubmitHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.DoubleSpinnerField;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FieldSet;
import com.sencha.gxt.widget.core.client.form.FileUploadField;
import com.sencha.gxt.widget.core.client.form.FormPanel;
import com.sencha.gxt.widget.core.client.form.SimpleComboBox;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.form.FormPanel.Encoding;
import com.sencha.gxt.widget.core.client.form.FormPanel.Method;
import com.sencha.gxt.widget.core.client.info.Info;

import cz.utb.fai.cudaonlineide.client.CudaOnlineIDE;
import cz.utb.fai.cudaonlineide.client.utils.MenuToolbarIcons;
import cz.utb.fai.cudaonlineide.shared.constants.COIConstants;
import cz.utb.fai.cudaonlineide.shared.dto.COIEnum;
import cz.utb.fai.cudaonlineide.shared.dto.COIFile;
import cz.utb.fai.cudaonlineide.shared.dto.COIFolder;
import cz.utb.fai.cudaonlineide.shared.dto.COIObject;
import cz.utb.fai.cudaonlineide.shared.dto.COIProject;
import cz.utb.fai.cudaonlineide.shared.dto.COIWorkspace;
import cz.utb.fai.cudaonlineide.shared.dto.project.COICompiler;
import cz.utb.fai.cudaonlineide.shared.dto.project.COIConfiguration;
import cz.utb.fai.cudaonlineide.shared.dto.project.COILinker;
import edu.ycp.cs.dh.acegwt.client.ace.AceCommandDescription;
import edu.ycp.cs.dh.acegwt.client.ace.AceEditor;
import edu.ycp.cs.dh.acegwt.client.ace.AceEditorMode;

/**
 * Class contains all pop up windows in project.
 * 
 * @author Belanec
 * 
 */
public class PopUpWindow {

	public static final Map<String, Object> data = new HashMap<>();

	public static final String FILENAME = "filename";
	public static final String EXTENSION = "extension";
	public static final String FOLDERNAME = "foldername";
	public static final String PROJECTNAME = "projectname";
	public static final String WORKSPACENAME = "workspacename";
	public static final String WORKSPACEFILE = "workspacefile";
	public static final String BUILD_CONFIGURATION = "buildconfiguration";
	public static final String BUILD_CONFIGURATION_OBJECT = "buildconfigurationobject";
	public static final String COIFOLDER = "coifolder";
	public static final String COIPROJECT = "coiproject";
	public static final String COIWORKSPACE = "coiworkspace";
	public static final String GOTOLINE = "gotoline";
	public static final String INSERTATCURSOR = "insertatcursor";
	public static final String TABSIZE = "tabsize";

	/**
	 * Creating of new file pop up window.
	 * 
	 * @param coiFolder
	 *            Folder to add file.
	 * @return New file pop up window.
	 */
	public static Window newFilePanel(final COIFolder coiFolder) {

		PopUpWindow.data.clear();

		final Window panel = new Window();
		panel.setBodyStyle("background: none; padding: 10px");
		panel.setHeadingText("New file");
		panel.setButtonAlign(BoxLayoutPack.CENTER);
		panel.setWidth(500);
		panel.setLayoutData(new MarginData(10));
		panel.setModal(true);
		panel.setResizable(false);

		VerticalLayoutContainer p = new VerticalLayoutContainer();
		panel.add(p);

		final TextButton createButton = new TextButton(
				COIConstants.BUTTON_CREATE);
		createButton.setEnabled(false);
		final TextButton cancelButton = new TextButton(
				COIConstants.BUTTON_CANCEL);

		final TextField fileName = new TextField();
		fileName.setAllowBlank(false);
		fileName.setEmptyText("Enter file name...");

		final TextField extension = new TextField();
		extension.setAllowBlank(false);
		extension.setEmptyText("Enter file extension...");

		fileName.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				if (fileName.getValue() != null
						&& !fileName.getValue().isEmpty()
						&& extension.getValue() != null
						&& !extension.getValue().isEmpty()) {
					createButton.setEnabled(true);
				} else {
					createButton.setEnabled(false);
				}
			}
		});

		extension.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				if (fileName.getValue() != null
						&& !fileName.getValue().isEmpty()
						&& extension.getValue() != null
						&& !extension.getValue().isEmpty()) {
					createButton.setEnabled(true);
				} else {
					createButton.setEnabled(false);
				}
			}
		});

		createButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {

				boolean fileExists = false;

				for (COIFile coiFile : coiFolder.getItems()) {
					if (coiFile.getName().equals(fileName.getValue())
							&& coiFile.getExtension().equals(
									extension.getValue())) {
						fileExists = true;
						break;
					}
				}

				if (fileExists) {
					Info.display("Create error",
							"File with this name already exists.");
				} else {
					PopUpWindow.data.put(PopUpWindow.FILENAME,
							fileName.getValue());
					PopUpWindow.data.put(PopUpWindow.EXTENSION,
							extension.getValue());
				}

				panel.hide();
			}
		});

		cancelButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				panel.hide();
			}
		});

		p.add(new FieldLabel(fileName, "File name"), new VerticalLayoutData(1,
				-1));
		p.add(new FieldLabel(extension, "File extension"),
				new VerticalLayoutData(1, -1));
		panel.addButton(createButton);
		panel.addButton(cancelButton);

		return panel;
	}

	/**
	 * Creating of new folder pop up window.
	 * 
	 * @param coiProject
	 *            Project to add folder.
	 * @return New folder pop up window.
	 */
	public static Window newFolderPanel(final COIProject coiProject) {

		PopUpWindow.data.clear();

		final Window panel = new Window();
		panel.setBodyStyle("background: none; padding: 10px");
		panel.setHeadingText("New folder");
		panel.setButtonAlign(BoxLayoutPack.CENTER);
		panel.setWidth(500);
		panel.setLayoutData(new MarginData(10));
		panel.setModal(true);
		panel.setResizable(false);

		VerticalLayoutContainer p = new VerticalLayoutContainer();
		panel.add(p);

		final TextButton createButton = new TextButton(
				COIConstants.BUTTON_CREATE);
		createButton.setEnabled(false);
		final TextButton cancelButton = new TextButton(
				COIConstants.BUTTON_CANCEL);

		final TextField folderName = new TextField();
		folderName.setAllowBlank(false);
		folderName.setEmptyText("Enter folder name...");

		folderName.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				if (folderName.getValue() != null
						&& !folderName.getValue().isEmpty()) {
					createButton.setEnabled(true);
				} else {
					createButton.setEnabled(false);
				}
			}
		});

		createButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {

				if (folderName.getValue().isEmpty()) {
					Info.display("Create error", "Folder must have some name.");
					return;
				}

				if (folderName.getValue().equals("build")
						|| folderName.getValue().equals("Build")) {
					Info.display("Create error",
							"Folder with name \"build\" cannot be created. Please type different name.");
					return;
				}

				for (COIFolder coiFolder : coiProject.getItems()) {
					if (coiFolder.getName().equals(folderName.getValue())) {
						Info.display("Create error",
								"Folder with this name already exists.");
						return;
					}
				}

				PopUpWindow.data.put(PopUpWindow.FOLDERNAME,
						folderName.getValue());

				panel.hide();
			}
		});

		cancelButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				panel.hide();
			}
		});

		p.add(new FieldLabel(folderName, "Folder name"),
				new VerticalLayoutData(1, -1));
		panel.addButton(createButton);
		panel.addButton(cancelButton);

		return panel;
	}

	/**
	 * Creating of new project pop up window.
	 * 
	 * @param coiWorkspace
	 *            Workspace to add project.
	 * @return New project pop up window.
	 */
	public static Window newProjectPanel(final COIWorkspace coiWorkspace) {

		PopUpWindow.data.clear();

		final Window panel = new Window();
		panel.setBodyStyle("background: none; padding: 10px");
		panel.setHeadingText("New project");
		panel.setButtonAlign(BoxLayoutPack.CENTER);
		panel.setWidth(500);
		panel.setLayoutData(new MarginData(10));
		panel.setModal(true);
		panel.setResizable(false);

		VerticalLayoutContainer p = new VerticalLayoutContainer();
		panel.add(p);

		final TextButton createButton = new TextButton(
				COIConstants.BUTTON_CREATE);
		createButton.setEnabled(false);
		final TextButton cancelButton = new TextButton(
				COIConstants.BUTTON_CANCEL);

		final TextField projectName = new TextField();
		projectName.setAllowBlank(false);
		projectName.setEmptyText("Enter project name...");

		final SimpleComboBox<String> buildConfiguration = new SimpleComboBox<String>(
				new StringLabelProvider<String>());
		buildConfiguration.setTriggerAction(TriggerAction.ALL);
		buildConfiguration.setEditable(false);
		buildConfiguration.setWidth(100);
		buildConfiguration.add(COIConstants.BUILD_CONFIGURATION_DEBUG);
		buildConfiguration.add(COIConstants.BUILD_CONFIGURATION_RELEASE);
		buildConfiguration.setValue(COIConstants.BUILD_CONFIGURATION_DEBUG);

		projectName.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				if (projectName.getValue() != null
						&& !projectName.getValue().isEmpty()) {
					createButton.setEnabled(true);
				} else {
					createButton.setEnabled(false);
				}
			}
		});

		createButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {

				boolean projectExists = false;

				for (COIProject coiProject : coiWorkspace.getItems()) {
					if (coiProject.getName().equals(projectName.getValue())) {
						projectExists = true;
						break;
					}
				}

				if (projectExists) {
					Info.display("Create error",
							"Project with this name already exists.");
				} else {
					PopUpWindow.data.put(PopUpWindow.PROJECTNAME,
							projectName.getValue());
					PopUpWindow.data.put(PopUpWindow.BUILD_CONFIGURATION,
							buildConfiguration.getValue());
				}

				panel.hide();
			}
		});

		cancelButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				panel.hide();
			}
		});

		p.add(new FieldLabel(projectName, "Project name"),
				new VerticalLayoutData(1, -1));
		p.add(new FieldLabel(buildConfiguration, "Build configuration"),
				new VerticalLayoutData(1, -1));
		panel.addButton(createButton);
		panel.addButton(cancelButton);

		return panel;
	}

	/**
	 * Creating of new workspace pop up window.
	 * 
	 * @param userWorkspacePath
	 *            User work path for new workspace.
	 * @return New workspace pop up window.
	 */
	public static Window newWorkspacePanel(final String userWorkspacePath) {

		PopUpWindow.data.clear();

		final Window panel = new Window();
		panel.setBodyStyle("background: none; padding: 10px");
		panel.setHeadingText("New workspace");
		panel.setButtonAlign(BoxLayoutPack.CENTER);
		panel.setWidth(500);
		panel.setLayoutData(new MarginData(10));
		panel.setModal(true);
		panel.setResizable(false);

		VerticalLayoutContainer p = new VerticalLayoutContainer();
		panel.add(p);

		final TextButton createButton = new TextButton(
				COIConstants.BUTTON_CREATE);
		createButton.setEnabled(false);
		final TextButton cancelButton = new TextButton(
				COIConstants.BUTTON_CANCEL);

		final TextField workspaceName = new TextField();
		workspaceName.setAllowBlank(false);
		workspaceName.setEmptyText("Enter workspace name...");

		workspaceName.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				if (workspaceName.getValue() != null
						&& !workspaceName.getValue().isEmpty()) {
					createButton.setEnabled(true);
				} else {
					createButton.setEnabled(false);
				}
			}
		});

		createButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {

				CudaOnlineIDE.coiService.getUserWorkspaceFiles(
						CudaOnlineIDE.ACTIVE_CUDA_FOLDER,
						new AsyncCallback<List<String>>() {
							public void onFailure(Throwable caught) {
								GWT.log(caught.getMessage());
								Info.display("Error",
										"Error in getting workspace files.");
								panel.hide();
							}

							public void onSuccess(List<String> workspaceFiles) {

								if (workspaceFiles == null) {
									panel.hide();
								}

								boolean workspaceExists = false;

								for (String workspaceFile : workspaceFiles) {
									if (workspaceFile.equals(workspaceName
											.getValue() + "_workspace.cws2")) {
										workspaceExists = true;
										break;
									}
								}

								if (workspaceExists) {
									Info.display("Create error",
											"Workspace with this name already exists.");
								} else {
									PopUpWindow.data.put(
											PopUpWindow.WORKSPACENAME,
											workspaceName.getValue());
								}

								panel.hide();
							}
						});
			}
		});

		cancelButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				panel.hide();
			}
		});

		p.add(new FieldLabel(workspaceName, "Workspace name"),
				new VerticalLayoutData(1, -1));
		panel.addButton(createButton);
		panel.addButton(cancelButton);

		return panel;
	}

	/**
	 * Pop up window for choosing active workspace.
	 * 
	 * @param workspaceFiles
	 *            All user workspaces.
	 * @return Pop up window with workspaces.
	 */
	public static Window openWorkspacePanel(final List<String> workspaceFiles) {

		PopUpWindow.data.clear();

		final Window panel = new Window();
		panel.setBodyStyle("background: none; padding: 10px");
		panel.setHeadingText("Open workspace");
		panel.setButtonAlign(BoxLayoutPack.CENTER);
		panel.setWidth(500);
		panel.setLayoutData(new MarginData(10));
		panel.setModal(true);
		panel.setResizable(false);

		VerticalLayoutContainer p = new VerticalLayoutContainer();
		panel.add(p);

		for (final String workspaceFile : workspaceFiles) {

			String workspaceFolder = workspaceFile.replace("_workspace.cws2",
					COIConstants.EMPTY);

			TextButton openButton = new TextButton(workspaceFolder);
			openButton.setId(workspaceFolder + "_ID");
			openButton.addSelectHandler(new SelectHandler() {
				@Override
				public void onSelect(SelectEvent event) {

					PopUpWindow.data.put(PopUpWindow.WORKSPACEFILE,
							workspaceFile);
					panel.hide();
				}
			});

			p.add(openButton, new VerticalLayoutData(1.0, -0.9, new Margins(0,
					0, 1, 0)));
		}

		return panel;
	}

	/**
	 * Pop up window for setting active project.
	 * 
	 * @param coiWorkspace
	 *            Active workspace.
	 * @return Pop up window with projects.
	 */
	public static Window setActiveProjectPanel(final COIWorkspace coiWorkspace) {

		final Window panel = new Window();
		panel.setBodyStyle("background: none; padding: 10px");
		panel.setHeadingText("Open workspace");
		panel.setButtonAlign(BoxLayoutPack.CENTER);
		panel.setWidth(500);
		panel.setLayoutData(new MarginData(10));
		panel.setModal(true);
		panel.setResizable(false);

		VerticalLayoutContainer p = new VerticalLayoutContainer();
		panel.add(p);

		for (final COIProject coiProject : coiWorkspace.getItems()) {

			TextButton openButton = new TextButton(coiProject.getName());
			openButton.setId(coiProject.getName() + "_ID");
			openButton.addSelectHandler(new SelectHandler() {
				@Override
				public void onSelect(SelectEvent event) {

					CudaOnlineIDE.ACTIVE_CUDA_COI_PROJECT = coiProject;
					CudaOnlineIDE
							.updateBuildConfigurationComboBoxByActiveProject();
					panel.hide();
				}
			});

			p.add(openButton, new VerticalLayoutData(1.0, -0.9, new Margins(0,
					0, 1, 0)));
		}

		return panel;
	}

	/**
	 * File upload pop up window.
	 * 
	 * @param coiFolder
	 *            Folder to add uploaded file.
	 * @return File upload pop up window.
	 */
	public static Window fileUploadPanel(final COIFolder coiFolder) {

		PopUpWindow.data.clear();

		final Window windowPanel = new Window();
		windowPanel.setHeadingText("File upload");
		windowPanel.setButtonAlign(BoxLayoutPack.CENTER);
		windowPanel.setWidth(350);
		windowPanel.setLayoutData(new MarginData(10));
		windowPanel.setModal(true);

		final FormPanel form = new FormPanel();
		form.setAction(GWT.getModuleBaseURL() + "uploadFileService");
		form.setEncoding(Encoding.MULTIPART);
		form.setMethod(Method.POST);

		VerticalLayoutContainer p = new VerticalLayoutContainer();

		final TextField fileName = new TextField();
		fileName.setAllowBlank(false);
		fileName.setName("uploadFileName");

		final TextField extension = new TextField();
		extension.setAllowBlank(false);
		extension.setName("uploadFileExtension");

		final TextField uploadToFolder = new TextField();
		uploadToFolder.setName("uploadToFolder");
		uploadToFolder.setValue(coiFolder.getPath());
		uploadToFolder.setReadOnly(true);

		final FileUploadField file = new FileUploadField();
		file.setAllowBlank(false);
		file.setName("uploadFileData");

		TextButton btnReset = new TextButton("Reset");
		TextButton btnSubmit = new TextButton("Submit");

		btnReset.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				form.reset();
				file.reset();
				uploadToFolder.setValue(coiFolder.getPath());
			}
		});

		btnSubmit.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				form.submit();
			}
		});

		form.addSubmitHandler(new SubmitHandler() {
			public void onSubmit(SubmitEvent event) {
				if (fileName.getValue() == null
						|| fileName.getValue().isEmpty()) {
					Info.display("Upload error", "File name must not be empty.");
					event.cancel();
				}

				if (extension.getValue() == null
						|| extension.getValue().isEmpty()) {
					Info.display("Upload error",
							"File extension must not be empty");
					event.cancel();
				}
			}
		});

		form.addSubmitCompleteHandler(new SubmitCompleteHandler() {
			public void onSubmitComplete(SubmitCompleteEvent event) {

				String result = event
						.getResults()
						.replace(
								"<pre style=\"word-wrap: break-word; white-space: pre-wrap;\">",
								"").replace("</pre>", "");
				result = result.replace("&lt;", "<").replace("&gt;", ">")
						.replace("&amp;", "").replace("&nbsp;", "");

				if (result == null || result.isEmpty()) {
					Info.display("Upload error",
							"File with this name already exists.");
				} else {
					Info.display("Upload success", result);
					PopUpWindow.data.put(PopUpWindow.FILENAME,
							fileName.getValue());
					PopUpWindow.data.put(PopUpWindow.EXTENSION,
							extension.getValue());
				}
				windowPanel.hide();
			}
		});

		p.add(new FieldLabel(fileName, "File name"), new VerticalLayoutData(
				-18, -1));
		p.add(new FieldLabel(extension, "Extension"), new VerticalLayoutData(
				-18, -1));
		p.add(new FieldLabel(uploadToFolder, "Upload to folder"),
				new VerticalLayoutData(-18, -1));
		p.add(new FieldLabel(file, "File"), new VerticalLayoutData(-18, -1));

		form.add(p, new MarginData(10));

		windowPanel.add(form);
		windowPanel.addButton(btnReset);
		windowPanel.addButton(btnSubmit);

		return windowPanel;
	}

	/**
	 * ZIP upload pop up window.
	 * 
	 * @param coiObject
	 *            Directory to extract ZIP.
	 * @return ZIP upload panel.
	 */
	public static Window zipUploadPanel(final COIObject coiObject) {

		PopUpWindow.data.clear();

		final Window windowPanel = new Window();
		windowPanel.setHeadingText("Zip upload");
		windowPanel.setButtonAlign(BoxLayoutPack.CENTER);
		windowPanel.setWidth(350);
		windowPanel.setLayoutData(new MarginData(10));
		windowPanel.setModal(true);

		final FormPanel form = new FormPanel();
		form.setAction(GWT.getModuleBaseURL() + "uploadZipService");
		form.setEncoding(Encoding.MULTIPART);
		form.setMethod(Method.POST);

		VerticalLayoutContainer p = new VerticalLayoutContainer();

		final TextField fileName = new TextField();
		fileName.setAllowBlank(false);
		fileName.setName("uploadName");

		final TextField uploadToFolder = new TextField();
		uploadToFolder.setName("uploadTo");
		if (coiObject == null) {
			uploadToFolder.setValue(CudaOnlineIDE.ACTIVE_CUDA_FOLDER);
		} else {
			uploadToFolder.setValue(coiObject.getPath());
		}
		uploadToFolder.setReadOnly(true);

		final TextField typeOfCOI = new TextField();
		typeOfCOI.setAllowBlank(false);
		typeOfCOI.setVisible(false);
		typeOfCOI.setName("typeOfCOI");

		if (coiObject != null && coiObject.getTypeOfCOI() == COIEnum.PROJECT) {
			typeOfCOI.setValue("Folder");
		} else if (coiObject != null
				&& coiObject.getTypeOfCOI() == COIEnum.WORKSPACE) {
			typeOfCOI.setValue("Project");
		} else {
			typeOfCOI.setValue("Workspace");
		}

		FieldLabel typeOfCOIFL = new FieldLabel(typeOfCOI, "Type of COI");
		typeOfCOIFL.setVisible(false);

		final TextField activeUser = new TextField();
		activeUser.setAllowBlank(false);
		activeUser.setVisible(false);
		activeUser.setName("activeUser");
		activeUser.setValue(CudaOnlineIDE.ACTIVE_USER);

		FieldLabel activeUserFL = new FieldLabel(activeUser, "Active user");
		activeUserFL.setVisible(false);

		final FileUploadField file = new FileUploadField();
		file.setAllowBlank(false);
		file.setName("uploadFileData");

		TextButton btnReset = new TextButton("Reset");
		TextButton btnSubmit = new TextButton("Submit");

		btnReset.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				form.reset();
				file.reset();

				if (coiObject == null) {
					uploadToFolder.setValue(CudaOnlineIDE.ACTIVE_CUDA_FOLDER);
				} else {
					uploadToFolder.setValue(coiObject.getPath());
				}
			}
		});

		btnSubmit.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {

				if (typeOfCOI.getValue().equals("Folder")
						&& (fileName.getValue().equals("build") || fileName
								.getValue().equals("Build"))) {
					Info.display("Upload error",
							"Folder with name \"build\" cannot be created. Please type different name.");
					return;
				}

				form.submit();
			}
		});

		form.addSubmitHandler(new SubmitHandler() {
			public void onSubmit(SubmitEvent event) {
				if (fileName.getValue() == null
						|| fileName.getValue().isEmpty()) {
					Info.display("Upload error", "Name must not be empty.");
					event.cancel();
				}
			}
		});

		form.addSubmitCompleteHandler(new SubmitCompleteHandler() {
			public void onSubmitComplete(SubmitCompleteEvent event) {

				String result = event
						.getResults()
						.replace(
								"<pre style=\"word-wrap: break-word; white-space: pre-wrap;\">",
								"").replace("</pre>", "");
				result = result.replace("&lt;", "<").replace("&gt;", ">")
						.replace("&amp;", "").replace("&nbsp;", "");

				if (typeOfCOI.getValue().equals("Folder")) {
					PopUpWindow.data.put(PopUpWindow.COIFOLDER, result);
				} else if (typeOfCOI.getValue().equals("Project")) {
					PopUpWindow.data.put(PopUpWindow.COIPROJECT, result);
				} else {
					PopUpWindow.data.put(PopUpWindow.COIWORKSPACE, result);
				}
				windowPanel.hide();
			}
		});

		p.add(new FieldLabel(fileName, "Name"), new VerticalLayoutData(-18, -1));
		p.add(new FieldLabel(uploadToFolder, "Upload to"),
				new VerticalLayoutData(-18, -1));
		p.add(typeOfCOIFL, new VerticalLayoutData(-18, -1));
		p.add(activeUserFL, new VerticalLayoutData(-18, -1));
		p.add(new FieldLabel(file, "Zip"), new VerticalLayoutData(-18, -1));

		form.add(p, new MarginData(10));

		windowPanel.add(form);
		windowPanel.addButton(btnReset);
		windowPanel.addButton(btnSubmit);

		return windowPanel;
	}

	/**
	 * Pop up window with project properties.
	 * 
	 * @param coiProject
	 *            Active project.
	 * @return Properties panel.
	 */
	public static Window propertiesPanel(final COIProject coiProject) {

		PopUpWindow.data.clear();

		final Window panel = new Window();
		panel.setBodyStyle("background: none; padding: 10px");
		panel.setHeadingText("Project properties");
		panel.setButtonAlign(BoxLayoutPack.CENTER);
		panel.setWidth(500);
		panel.setLayoutData(new MarginData(10));
		panel.setModal(true);
		panel.setResizable(false);

		VerticalLayoutContainer p = new VerticalLayoutContainer();
		panel.add(p);

		final TextButton okButton = new TextButton(COIConstants.BUTTON_OK);
		final TextButton compilerButton = new TextButton(
				COIConstants.BUTTON_EDIT);
		final TextButton linkerButton = new TextButton(COIConstants.BUTTON_EDIT);

		final COIConfiguration configurationToEdit;

		switch (CudaOnlineIDE.buildConfigurationCombo.getValue()) {

		case COIConstants.BUILD_CONFIGURATION_DEBUG:
			configurationToEdit = coiProject.getBuildConfiguration().getDebug();
			break;
		case COIConstants.BUILD_CONFIGURATION_RELEASE:
			configurationToEdit = coiProject.getBuildConfiguration()
					.getRelease();
			break;
		case COIConstants.BUILD_CONFIGURATION_CUSTOM:
			configurationToEdit = coiProject.getBuildConfiguration()
					.getCustom();
			break;
		default:
			configurationToEdit = new COIConfiguration();
		}

		final TextField compilerOptions = new TextField();
		compilerOptions.setReadOnly(true);
		compilerOptions.setValue(configurationToEdit.getCompiler()
				.getOptionsText());
		compilerOptions.setEnabled(false);

		final TextField linkerOptions = new TextField();
		linkerOptions.setReadOnly(true);
		linkerOptions
				.setValue(configurationToEdit.getLinker().getOptionsText());
		linkerOptions.setEnabled(false);

		compilerButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {

				Window compilerPanel = PopUpWindow.compilerPanel(coiProject,
						configurationToEdit);
				compilerPanel.show();
				compilerPanel.addHideHandler(new HideHandler() {
					@Override
					public void onHide(HideEvent event) {
						compilerOptions.setValue(configurationToEdit
								.getCompiler().getOptionsText());
					}
				});
			}
		});

		linkerButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {

				Window linkerPanel = PopUpWindow.linkerPanel(coiProject,
						configurationToEdit);
				linkerPanel.show();
				linkerPanel.addHideHandler(new HideHandler() {
					@Override
					public void onHide(HideEvent event) {
						linkerOptions.setValue(configurationToEdit.getLinker()
								.getOptionsText());
					}
				});
			}
		});

		okButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				panel.hide();
			}
		});

		p.add(new FieldLabel(compilerOptions, "Compiler options"),
				new VerticalLayoutData(1, -1));
		p.add(new FieldLabel(compilerButton, "Compiler settings"),
				new VerticalLayoutData(1, -1));
		p.add(new FieldLabel(linkerOptions, "Linker options"),
				new VerticalLayoutData(1, -1));
		p.add(new FieldLabel(linkerButton, "Linker settings"),
				new VerticalLayoutData(1, -1));
		panel.addButton(okButton);

		return panel;
	}

	/**
	 * Pop up window for active project compiler settings.
	 * 
	 * @param coiProject
	 *            Active project.
	 * @param coiConfiguration
	 *            Active configuration.
	 * @return Compiler options panel.
	 */
	public static Window compilerPanel(final COIProject coiProject,
			final COIConfiguration coiConfiguration) {

		final Window panel = new Window();
		panel.setBodyStyle("background: none; padding: 10px");
		panel.setHeadingText("Compiler settings");
		panel.setButtonAlign(BoxLayoutPack.CENTER);
		panel.setWidth(500);
		panel.setLayoutData(new MarginData(10));
		panel.setModal(true);
		panel.setResizable(true);
		panel.setMaximizable(true);
		panel.setDraggable(true);

		VerticalLayoutContainer optionsVLC = new VerticalLayoutContainer();
		FieldSet optionsFieldSet = new FieldSet();
		optionsFieldSet.setHeadingText("Options");
		optionsFieldSet.setCollapsible(true);
		optionsFieldSet.add(optionsVLC);
		optionsFieldSet.getElement().getStyle().setMarginBottom(10, Unit.PX);

		VerticalLayoutContainer includeDirectoriesVLC = new VerticalLayoutContainer();
		FieldSet includeDirectoriesFieldSet = new FieldSet();
		includeDirectoriesFieldSet.setHeadingText("Include directories");
		includeDirectoriesFieldSet.setCollapsible(true);
		includeDirectoriesFieldSet.add(includeDirectoriesVLC);
		includeDirectoriesFieldSet.getElement().getStyle()
				.setMarginBottom(10, Unit.PX);

		VerticalLayoutContainer preprocessorsVLC = new VerticalLayoutContainer();
		FieldSet preprocessorsFieldSet = new FieldSet();
		preprocessorsFieldSet.setHeadingText("Preprocessors");
		preprocessorsFieldSet.setCollapsible(true);
		preprocessorsFieldSet.add(preprocessorsVLC);

		VerticalLayoutContainer globalVLC = new VerticalLayoutContainer();
		globalVLC.add(optionsFieldSet, new VerticalLayoutData(1, -1));
		globalVLC
				.add(includeDirectoriesFieldSet, new VerticalLayoutData(1, -1));
		globalVLC.add(preprocessorsFieldSet, new VerticalLayoutData(1, -1));

		panel.add(globalVLC);

		final COICompiler coiCompiler = coiConfiguration.getCompiler();

		List<String> choosedOptions = new ArrayList<>(coiCompiler.getOptions());

		final TextButton saveButton = new TextButton(COIConstants.BUTTON_SAVE);
		final TextButton cancelButton = new TextButton(
				COIConstants.BUTTON_CANCEL);

		final CheckBox compilerWall = new CheckBox();
		compilerWall.setBoxLabel("-Wall [Enable all compiler warnings]");
		compilerWall.setValue(choosedOptions.contains(compilerWall
				.getBoxLabel().split(" ")[0]));
		choosedOptions.remove(compilerWall.getBoxLabel().split(" ")[0]);

		final CheckBox compilerfopenmp = new CheckBox();
		compilerfopenmp.setBoxLabel("-fopenmp [Enable OpenMP (compilation)]");
		compilerfopenmp.setValue(choosedOptions.contains(compilerfopenmp
				.getBoxLabel().split(" ")[0]));
		choosedOptions.remove(compilerfopenmp.getBoxLabel().split(" ")[0]);

		final CheckBox compilerW = new CheckBox();
		compilerW.setBoxLabel("-W [Enable standard compiler warnings]");
		compilerW.setValue(choosedOptions.contains(compilerW.getBoxLabel()
				.split(" ")[0]));
		choosedOptions.remove(compilerW.getBoxLabel().split(" ")[0]);

		final CheckBox compilerpedantic = new CheckBox();
		compilerpedantic
				.setBoxLabel("-pedantic [Enable warnings demanded by strict ISO C and ISO C++]");
		compilerpedantic.setValue(choosedOptions.contains(compilerpedantic
				.getBoxLabel().split(" ")[0]));
		choosedOptions.remove(compilerpedantic.getBoxLabel().split(" ")[0]);

		final CheckBox compilerfexpensive_optimizations = new CheckBox();
		compilerfexpensive_optimizations
				.setBoxLabel("-fexpensive-optimizations [Expensive optimizations]");
		compilerfexpensive_optimizations.setValue(choosedOptions
				.contains(compilerfexpensive_optimizations.getBoxLabel().split(
						" ")[0]));
		choosedOptions.remove(compilerfexpensive_optimizations.getBoxLabel()
				.split(" ")[0]);

		final CheckBox compileransi = new CheckBox();
		compileransi
				.setBoxLabel("-ansi [In C mode, support all ISO C90 programs. In C++ mode, remove GNU extensions that conflict with ISO C++]");
		compileransi.setValue(choosedOptions.contains(compileransi
				.getBoxLabel().split(" ")[0]));
		choosedOptions.remove(compileransi.getBoxLabel().split(" ")[0]);

		final CheckBox compilerw = new CheckBox();
		compilerw.setBoxLabel("-w [Inhibit all warning messages]");
		compilerw.setValue(choosedOptions.contains(compilerw.getBoxLabel()
				.split(" ")[0]));
		choosedOptions.remove(compilerw.getBoxLabel().split(" ")[0]);

		final CheckBox compilerO2 = new CheckBox();
		compilerO2.setBoxLabel("-O2 [Optimize even more (for speed)]");
		compilerO2.setValue(choosedOptions.contains(compilerO2.getBoxLabel()
				.split(" ")[0]));
		choosedOptions.remove(compilerO2.getBoxLabel().split(" ")[0]);

		final CheckBox compilerO3 = new CheckBox();
		compilerO3.setBoxLabel("-O3 [Optimize fully (for speed)]");
		compilerO3.setValue(choosedOptions.contains(compilerO3.getBoxLabel()
				.split(" ")[0]));
		choosedOptions.remove(compilerO3.getBoxLabel().split(" ")[0]);

		final CheckBox compilerOs = new CheckBox();
		compilerOs.setBoxLabel("-Os [Optimize generated code (for size)]");
		compilerOs.setValue(choosedOptions.contains(compilerOs.getBoxLabel()
				.split(" ")[0]));
		choosedOptions.remove(compilerOs.getBoxLabel().split(" ")[0]);

		final CheckBox compilerO = new CheckBox();
		compilerO.setBoxLabel("-O [Optimize generated code (for speed)]");
		compilerO.setValue(choosedOptions.contains(compilerO.getBoxLabel()
				.split(" ")[0]));
		choosedOptions.remove(compilerO.getBoxLabel().split(" ")[0]);

		final CheckBox compilerO1 = new CheckBox();
		compilerO1.setBoxLabel("-O1 [Optimize more (for speed)]");
		compilerO1.setValue(choosedOptions.contains(compilerO1.getBoxLabel()
				.split(" ")[0]));
		choosedOptions.remove(compilerO1.getBoxLabel().split(" ")[0]);

		final CheckBox compilerg = new CheckBox();
		compilerg.setBoxLabel("-g [Produce debugging information]");
		compilerg.setValue(choosedOptions.contains(compilerg.getBoxLabel()
				.split(" ")[0]));
		choosedOptions.remove(compilerg.getBoxLabel().split(" ")[0]);

		final CheckBox compilerpg = new CheckBox();
		compilerpg.setBoxLabel("-pg [Profile code when executed]");
		compilerpg.setValue(choosedOptions.contains(compilerpg.getBoxLabel()
				.split(" ")[0]));
		choosedOptions.remove(compilerpg.getBoxLabel().split(" ")[0]);

		final CheckBox compilerWfatal_errors = new CheckBox();
		compilerWfatal_errors
				.setBoxLabel("-Wfatal-errors [Stop compiling after first error]");
		compilerWfatal_errors.setValue(choosedOptions
				.contains(compilerWfatal_errors.getBoxLabel().split(" ")[0]));
		choosedOptions
				.remove(compilerWfatal_errors.getBoxLabel().split(" ")[0]);

		final CheckBox compilerpedantic_errors = new CheckBox();
		compilerpedantic_errors
				.setBoxLabel("-pedantic-errors [Treat as errors the warnings demanded by strict ISO C and ISO C++]");
		compilerpedantic_errors.setValue(choosedOptions
				.contains(compilerpedantic_errors.getBoxLabel().split(" ")[0]));
		choosedOptions
				.remove(compilerpedantic_errors.getBoxLabel().split(" ")[0]);
		compilerpedantic_errors.getElement().getStyle()
				.setMarginBottom(10, Unit.PX);

		final CheckBox compilerWmain = new CheckBox();
		compilerWmain.setBoxLabel("-Wmain [Warn if main() is not conformant]");
		compilerWmain.setValue(choosedOptions.contains(compilerWmain
				.getBoxLabel().split(" ")[0]));
		choosedOptions.remove(compilerWmain.getBoxLabel().split(" ")[0]);

		final TextField compilerOther = new TextField();

		if (choosedOptions != null && (choosedOptions.size() == 1)) {
			compilerOther.setValue(choosedOptions.get(0));
		}

		final TextArea compilerIncludeDirectoriesText = new TextArea();
		compilerIncludeDirectoriesText.setText(coiCompiler
				.getIncludeDirectoriesText());

		final TextArea compilerPreprocessorsText = new TextArea();
		compilerPreprocessorsText.setText(coiCompiler.getPreprocessorsText());

		saveButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {

				List<String> choosedOptionsToSave = new ArrayList<>();
				List<String> choosedIncludeDirectories = new ArrayList<>();
				List<String> choosedPreprocessors = new ArrayList<>();

				if (compilerw.getValue())
					choosedOptionsToSave.add("-w");
				if (compilerW.getValue())
					choosedOptionsToSave.add("-W");
				if (compilerWmain.getValue())
					choosedOptionsToSave.add("-Wmain");
				if (compilerWall.getValue())
					choosedOptionsToSave.add("-Wall");
				if (compilerpedantic.getValue())
					choosedOptionsToSave.add("-pedantic");
				if (compileransi.getValue())
					choosedOptionsToSave.add("-ansi");
				if (compilerg.getValue())
					choosedOptionsToSave.add("-g");
				if (compilerpg.getValue())
					choosedOptionsToSave.add("-pg");
				if (compilerO.getValue())
					choosedOptionsToSave.add("-O");
				if (compilerO1.getValue())
					choosedOptionsToSave.add("-O1");
				if (compilerO2.getValue())
					choosedOptionsToSave.add("-O2");
				if (compilerO3.getValue())
					choosedOptionsToSave.add("-O3");
				if (compilerOs.getValue())
					choosedOptionsToSave.add("-Os");
				if (compilerfopenmp.getValue())
					choosedOptionsToSave.add("-fopenmp");
				if (compilerfexpensive_optimizations.getValue())
					choosedOptionsToSave.add("-fexpensive-optimizations");
				if (compilerWfatal_errors.getValue())
					choosedOptionsToSave.add("-Wfatal-errors");
				if (compilerpedantic_errors.getValue())
					choosedOptionsToSave.add("-pedantic-errors");
				if (!compilerOther.getText().isEmpty())
					choosedOptionsToSave.add(compilerOther.getText());

				coiCompiler.setOptions(choosedOptionsToSave);

				String[] includeDirectoriesSplits = compilerIncludeDirectoriesText
						.getText().split(COIConstants.LINE_SEPARATOR);

				for (String includeDirectoriesSplit : includeDirectoriesSplits) {
					if (!includeDirectoriesSplit.isEmpty()) {
						choosedIncludeDirectories.add(includeDirectoriesSplit);
					}
				}

				coiCompiler.setIncludeDirectories(choosedIncludeDirectories);

				String[] preprocessorsSplits = compilerPreprocessorsText
						.getText().split(COIConstants.LINE_SEPARATOR);

				for (String preprocessorsSplit : preprocessorsSplits) {
					if (!preprocessorsSplit.isEmpty()) {
						choosedPreprocessors.add(preprocessorsSplit);
					}
				}

				coiCompiler.setPreprocessors(choosedPreprocessors);

				COIWorkspace coiWorkspace = CudaOnlineIDE.ACTIVE_CUDA_COI_WORKSPACE;

				for (COIProject coiProjectTmp : coiWorkspace.getItems()) {
					if (coiProjectTmp.getPath() == coiProject.getPath()) {
						coiProjectTmp.setBuildConfiguration(coiProject
								.getBuildConfiguration());
						break;
					}
				}

				CudaOnlineIDE.coiService.updateWorkspace(coiWorkspace,
						new AsyncCallback<Void>() {

							@Override
							public void onFailure(Throwable caught) {
								GWT.log(caught.getMessage());
								Info.display("Update error",
										"Error in updating project.");
							}

							@Override
							public void onSuccess(Void voidResult) {
								CudaOnlineIDE
										.updateBuildConfigurationComboBoxByActiveProject();
								Info.display("Update",
										"Project compiler properties was successfully updated.");
							}
						});

				panel.hide();
			}
		});

		cancelButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				panel.hide();
			}
		});

		optionsVLC.add(compilerw, new VerticalLayoutData(1, -1));
		optionsVLC.add(compilerW, new VerticalLayoutData(1, -1));
		optionsVLC.add(compilerWmain, new VerticalLayoutData(1, -1));
		optionsVLC.add(compilerWall, new VerticalLayoutData(1, -1));
		optionsVLC.add(compilerpedantic, new VerticalLayoutData(1, -1));
		optionsVLC.add(compileransi, new VerticalLayoutData(1, -1));
		optionsVLC.add(compilerg, new VerticalLayoutData(1, -1));
		optionsVLC.add(compilerpg, new VerticalLayoutData(1, -1));
		optionsVLC.add(compilerO, new VerticalLayoutData(1, -1));
		optionsVLC.add(compilerO1, new VerticalLayoutData(1, -1));
		optionsVLC.add(compilerO2, new VerticalLayoutData(1, -1));
		optionsVLC.add(compilerO3, new VerticalLayoutData(1, -1));
		optionsVLC.add(compilerOs, new VerticalLayoutData(1, -1));
		optionsVLC.add(compilerfopenmp, new VerticalLayoutData(1, -1));
		optionsVLC.add(compilerfexpensive_optimizations,
				new VerticalLayoutData(1, -1));
		optionsVLC.add(compilerWfatal_errors, new VerticalLayoutData(1, -1));
		optionsVLC.add(compilerpedantic_errors, new VerticalLayoutData(1, -1));
		optionsVLC.add(new FieldLabel(compilerOther, "Other options:"),
				new VerticalLayoutData(1, -1));

		includeDirectoriesVLC.add(new FieldLabel(
				compilerIncludeDirectoriesText, "Include directories:"),
				new VerticalLayoutData(1, -1));

		preprocessorsVLC.add(new FieldLabel(compilerPreprocessorsText,
				"Preprocessors:"), new VerticalLayoutData(1, -1));

		panel.addButton(saveButton);
		panel.addButton(cancelButton);

		return panel;
	}

	/**
	 * Pop up window for active project linker settings.
	 * 
	 * @param coiProject
	 *            Active project.
	 * @param coiConfiguration
	 *            Active configuration.
	 * @return Linker options panel.
	 */
	public static Window linkerPanel(final COIProject coiProject,
			final COIConfiguration coiConfiguration) {

		final Window panel = new Window();
		panel.setBodyStyle("background: none; padding: 10px");
		panel.setHeadingText("Linker settings");
		panel.setButtonAlign(BoxLayoutPack.CENTER);
		panel.setWidth(500);
		panel.setLayoutData(new MarginData(10));
		panel.setModal(true);
		panel.setResizable(true);
		panel.setMaximizable(true);
		panel.setDraggable(true);

		VerticalLayoutContainer optionsVLC = new VerticalLayoutContainer();
		FieldSet optionsFieldSet = new FieldSet();
		optionsFieldSet.setHeadingText("Options");
		optionsFieldSet.setCollapsible(true);
		optionsFieldSet.add(optionsVLC);
		optionsFieldSet.getElement().getStyle().setMarginBottom(10, Unit.PX);

		VerticalLayoutContainer libraryPathsVLC = new VerticalLayoutContainer();
		FieldSet libraryPathsFieldSet = new FieldSet();
		libraryPathsFieldSet.setHeadingText("Library paths");
		libraryPathsFieldSet.setCollapsible(true);
		libraryPathsFieldSet.add(libraryPathsVLC);
		libraryPathsFieldSet.getElement().getStyle()
				.setMarginBottom(10, Unit.PX);

		VerticalLayoutContainer libraryNamesVLC = new VerticalLayoutContainer();
		FieldSet libraryNamesFieldSet = new FieldSet();
		libraryNamesFieldSet.setHeadingText("Library names");
		libraryNamesFieldSet.setCollapsible(true);
		libraryNamesFieldSet.add(libraryNamesVLC);

		VerticalLayoutContainer globalVLC = new VerticalLayoutContainer();
		globalVLC.add(optionsFieldSet, new VerticalLayoutData(1, -1));
		globalVLC.add(libraryPathsFieldSet, new VerticalLayoutData(1, -1));
		globalVLC.add(libraryNamesFieldSet, new VerticalLayoutData(1, -1));

		panel.add(globalVLC);

		final COILinker coiLinker = coiConfiguration.getLinker();

		List<String> choosedOptions = new ArrayList<>(coiLinker.getOptions());

		final TextButton saveButton = new TextButton(COIConstants.BUTTON_SAVE);
		final TextButton cancelButton = new TextButton(
				COIConstants.BUTTON_CANCEL);

		final CheckBox linkerfopenmp = new CheckBox();
		linkerfopenmp.setBoxLabel("-fopenmp [Enable OpenMP (linkage)]");
		linkerfopenmp.setValue(choosedOptions.contains(linkerfopenmp
				.getBoxLabel().split(" ")[0]));
		choosedOptions.remove(linkerfopenmp.getBoxLabel().split(" ")[0]);
		linkerfopenmp.getElement().getStyle().setMarginBottom(10, Unit.PX);

		final CheckBox linkers = new CheckBox();
		linkers.setBoxLabel("-s [Remove all symbol table and relocation information from the executable]");
		linkers.setValue(choosedOptions.contains(linkers.getBoxLabel().split(
				" ")[0]));
		choosedOptions.remove(linkers.getBoxLabel().split(" ")[0]);

		final TextField linkerOther = new TextField();

		if (choosedOptions != null && (choosedOptions.size() == 1)) {
			linkerOther.setValue(choosedOptions.get(0));
		}

		final TextArea linkerLibraryPathsText = new TextArea();
		linkerLibraryPathsText.setText(coiLinker.getLibraryPathsText());

		final TextArea linkerLibraryNamesText = new TextArea();
		linkerLibraryNamesText.setText(coiLinker.getLibraryNamesText());

		saveButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {

				List<String> choosedOptionsToSave = new ArrayList<>();
				List<String> choosedLibraryPaths = new ArrayList<>();
				List<String> choosedLibraryNames = new ArrayList<>();

				if (linkers.getValue())
					choosedOptionsToSave.add("-s");
				if (linkerfopenmp.getValue())
					choosedOptionsToSave.add("-fopenmp");
				if (!linkerOther.getText().isEmpty())
					choosedOptionsToSave.add(linkerOther.getText());

				coiLinker.setOptions(choosedOptionsToSave);

				String[] libraryPathsSplits = linkerLibraryPathsText.getText()
						.split(COIConstants.LINE_SEPARATOR);

				for (String libraryPathsSplit : libraryPathsSplits) {
					if (!libraryPathsSplit.isEmpty()) {
						choosedLibraryPaths.add(libraryPathsSplit);
					}
				}

				coiLinker.setLibraryPaths(choosedLibraryPaths);

				String[] libraryNamesSplits = linkerLibraryNamesText.getText()
						.split(COIConstants.LINE_SEPARATOR);

				for (String libraryNamesSplit : libraryNamesSplits) {
					if (!libraryNamesSplit.isEmpty()) {
						choosedLibraryNames.add(libraryNamesSplit);
					}
				}

				coiLinker.setLibraryNames(choosedLibraryNames);

				COIWorkspace coiWorkspace = CudaOnlineIDE.ACTIVE_CUDA_COI_WORKSPACE;

				for (COIProject coiProjectTmp : coiWorkspace.getItems()) {
					if (coiProjectTmp.getPath() == coiProject.getPath()) {
						coiProjectTmp.setBuildConfiguration(coiProject
								.getBuildConfiguration());
						break;
					}
				}

				CudaOnlineIDE.coiService.updateWorkspace(coiWorkspace,
						new AsyncCallback<Void>() {

							@Override
							public void onFailure(Throwable caught) {
								GWT.log(caught.getMessage());
								Info.display("Update error",
										"Error in updating project.");
							}

							@Override
							public void onSuccess(Void voidResult) {
								CudaOnlineIDE
										.updateBuildConfigurationComboBoxByActiveProject();
								Info.display("Update",
										"Project linker properties was successfully updated.");
							}
						});

				panel.hide();
			}
		});

		cancelButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				panel.hide();
			}
		});

		optionsVLC.add(linkers, new VerticalLayoutData(1, -1));
		optionsVLC.add(linkerfopenmp, new VerticalLayoutData(1, -1));
		optionsVLC.add(new FieldLabel(linkerOther, "Other options:"),
				new VerticalLayoutData(1, -1));

		libraryPathsVLC.add(new FieldLabel(linkerLibraryPathsText,
				"Library paths:"), new VerticalLayoutData(1, -1));

		libraryNamesVLC.add(new FieldLabel(linkerLibraryNamesText,
				"Library names:"), new VerticalLayoutData(1, -1));

		panel.addButton(saveButton);
		panel.addButton(cancelButton);

		return panel;
	}

	/**
	 * Pop up window for going to chosen line.
	 * 
	 * @param lineCount
	 *            Line count of text.
	 * @param actualLine
	 *            Actual line.
	 * @return Go to line panel.
	 */
	public static Window goToLinePanel(final int lineCount, final int actualLine) {

		PopUpWindow.data.clear();

		final Window panel = new Window();
		panel.setBodyStyle("background: none; padding: 10px");
		panel.setHeadingText("Go to line");
		panel.setButtonAlign(BoxLayoutPack.CENTER);
		panel.setWidth(500);
		panel.setLayoutData(new MarginData(10));
		panel.setModal(true);
		panel.setResizable(false);

		VerticalLayoutContainer p = new VerticalLayoutContainer();
		panel.add(p);

		final TextButton okButton = new TextButton(COIConstants.BUTTON_OK);
		final TextButton cancelButton = new TextButton(
				COIConstants.BUTTON_CANCEL);

		final DoubleSpinnerField spinnerField = new DoubleSpinnerField();
		spinnerField.setIncrement(1d);
		spinnerField.setMinValue(1d);
		spinnerField.setMaxValue((double) lineCount);
		spinnerField.setAllowNegative(false);
		spinnerField.setOriginalValue((double) actualLine);
		spinnerField.setValue((double) actualLine);
		spinnerField.getPropertyEditor().setFormat(NumberFormat.getFormat("0"));

		okButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				PopUpWindow.data.put(PopUpWindow.GOTOLINE,
						spinnerField.getCurrentValue());
				panel.hide();
			}
		});

		cancelButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				panel.hide();
			}
		});

		p.add(new FieldLabel(spinnerField, "Line"), new VerticalLayoutData(1,
				-1));
		panel.addButton(okButton);
		panel.addButton(cancelButton);

		return panel;
	}

	/**
	 * Pop up window for inserting text at cursor.
	 * 
	 * @return Insert at cursor panel.
	 */
	public static Window insertAtCursorPanel() {

		PopUpWindow.data.clear();

		final Window panel = new Window();
		panel.setBodyStyle("background: none; padding: 10px");
		panel.setHeadingText("Insert text");
		panel.setButtonAlign(BoxLayoutPack.CENTER);
		panel.setWidth(500);
		panel.setLayoutData(new MarginData(10));
		panel.setModal(true);
		panel.setResizable(false);

		VerticalLayoutContainer p = new VerticalLayoutContainer();
		panel.add(p);

		final TextButton okButton = new TextButton("OK");
		final TextButton cancelButton = new TextButton(
				COIConstants.BUTTON_CANCEL);

		final TextField insert = new TextField();
		insert.setAllowBlank(false);
		insert.setEmptyText("Insert text at cursor...");

		okButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				PopUpWindow.data.put(PopUpWindow.INSERTATCURSOR,
						insert.getValue());
				panel.hide();
			}
		});

		cancelButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				panel.hide();
			}
		});

		p.add(new FieldLabel(insert, "Insert"), new VerticalLayoutData(1, -1));
		panel.addButton(okButton);
		panel.addButton(cancelButton);

		return panel;
	}

	/**
	 * Pop up window for setting tab size in code editor.
	 * 
	 * @return Tab size panel.
	 */
	public static Window tabSizePanel() {

		PopUpWindow.data.clear();

		final Window panel = new Window();
		panel.setBodyStyle("background: none; padding: 10px");
		panel.setHeadingText("Tab size");
		panel.setButtonAlign(BoxLayoutPack.CENTER);
		panel.setWidth(500);
		panel.setLayoutData(new MarginData(10));
		panel.setModal(true);
		panel.setResizable(false);

		VerticalLayoutContainer p = new VerticalLayoutContainer();
		panel.add(p);

		final TextButton okButton = new TextButton("OK");
		final TextButton cancelButton = new TextButton(
				COIConstants.BUTTON_CANCEL);

		final DoubleSpinnerField spinnerField = new DoubleSpinnerField();
		spinnerField.setIncrement(1d);
		spinnerField.setMinValue(1d);
		spinnerField.setMaxValue(15d);
		spinnerField.setAllowNegative(false);
		spinnerField.setOriginalValue(4d);
		spinnerField.setValue(4d);
		spinnerField.getPropertyEditor().setFormat(NumberFormat.getFormat("0"));

		okButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				PopUpWindow.data.put(PopUpWindow.TABSIZE,
						spinnerField.getCurrentValue());
				panel.hide();
			}
		});

		cancelButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				panel.hide();
			}
		});

		p.add(new FieldLabel(spinnerField, "Tab size"), new VerticalLayoutData(
				1, -1));
		panel.addButton(okButton);
		panel.addButton(cancelButton);

		return panel;
	}

	/**
	 * Pop up window for opening CMakeLists.txt file and editing it.
	 * 
	 * @param result
	 *            Text of CMakeLists.txt.
	 * @return CMakeLists.txt panel.
	 */
	public static Window openMakeFileProjectPanel(final String result) {

		final Window panel = new Window();
		panel.setBodyStyle("background: none; padding: 10px");
		panel.setHeadingText("Edit CMakeLists");
		panel.setButtonAlign(BoxLayoutPack.CENTER);
		panel.setWidth(800);
		panel.setHeight(800);
		panel.setLayoutData(new MarginData(10));
		panel.setModal(true);
		panel.setResizable(false);

		final TextButton saveButton = new TextButton(COIConstants.BUTTON_SAVE);
		final TextButton cancelButton = new TextButton(
				COIConstants.BUTTON_CANCEL);

		final AceEditor editor = new AceEditor();
		editor.setWidth(COIConstants.SIZE_100_PERCENTAGE);
		editor.setHeight(COIConstants.SIZE_100_PERCENTAGE);

		panel.add(editor);

		editor.startEditor();
		editor.setText(result);
		editor.setMode(AceEditorMode.PLAIN_TEXT);
		editor.setTheme(CudaOnlineIDE.aceActualTheme);
		editor.setFontSize(CudaOnlineIDE.aceFontSize);

		editor.setUseSoftTabs(CudaOnlineIDE.aceSoftTabs);
		editor.setHScrollBarAlwaysVisible(CudaOnlineIDE.aceHorizontalScroll);
		editor.setShowGutter(CudaOnlineIDE.aceShowGutter);
		editor.setHighlightSelectedWord(CudaOnlineIDE.aceHighlightSelectedWord);
		editor.setReadOnly(CudaOnlineIDE.aceReadOnly);
		editor.setAutocompleteEnabled(CudaOnlineIDE.aceAutocomplete);
		editor.setTabSize(CudaOnlineIDE.aceTabSize);

		editor.addCommand(new AceCommandDescription("saveActualFile",
				new AceCommandDescription.ExecAction() {
					@Override
					public Object exec(AceEditor aceEditor) {
						PopUpWindow.saveMakefile(aceEditor.getText());
						return null;
					}
				}).withBindKey("Ctrl-S"));

		saveButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				PopUpWindow.saveMakefile(editor.getText());
				panel.hide();
			}
		});

		cancelButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				panel.hide();
			}
		});

		panel.addButton(saveButton);
		panel.addButton(cancelButton);

		return panel;
	}

	/**
	 * Pop up window with information about project.
	 * 
	 * @return About panel.
	 */
	public static Window aboutPanel() {

		final Window panel = new Window();
		panel.setBodyStyle("background: none; padding: 10px");
		panel.setHeadingText("About");
		panel.setButtonAlign(BoxLayoutPack.CENTER);
		panel.setWidth(500);
		panel.setLayoutData(new MarginData(10));
		panel.setModal(true);
		panel.setResizable(false);

		VerticalLayoutContainer p = new VerticalLayoutContainer();
		panel.add(p);

		final TextButton okButton = new TextButton(COIConstants.BUTTON_OK);

		okButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				panel.hide();
			}
		});

		HTML nameHTML = new HTML(
				"<center><br /><span style=\"font-family: Arial, Helvetica, sans-serif; font-size: larger; font-weight: bold;\">CUDA On-line IDE</span></center>");
		p.add(nameHTML, new VerticalLayoutData(1, -1));

		Image image = new Image(MenuToolbarIcons.PROVIDER.logoCuda());
		image.getElement().setId("cudaIDEImage");
		p.add(image, new VerticalLayoutData(1, -1));

		HTML tableHTML = new HTML(
				"<center>"
						+ "<table style=\"font-family: Arial, Helvetica, sans-serif; font-size: 85%;\">"
						+ "<tr>"
						+ "<td style=\"font-weight: bold;\">Version:</td>"
						+ "<td>1.0</td>"
						+ "</tr>"
						+ "<tr>"
						+ "<td style=\"font-weight: bold;\">License:</td>"
						+ "<td><a href=\"https://www.gnu.org/licenses/gpl-2.0.html\" target=\"_blank\">GNU/GPL v2</a></td>"
						+ "</tr>"
						+ "<tr>"
						+ "<td style=\"font-weight: bold;\">Address:</td>"
						+ "<td><a href=\"http://10.51.11.99:8080/CudaOnlineIDE\" target=\"_blank\">http://10.51.11.99:8080/CudaOnlineIDE</a></td>"
						+ "</tr>"
						+ "<tr>"
						+ "<td>&nbsp;</td>"
						+ "<td>&nbsp;</td>"
						+ "</tr>"
						+ "<tr>"
						+ "<td style=\"font-weight: bold;\">Author:</td>"
						+ "<td>Martin Belanec</td>"
						+ "</tr>"
						+ "<tr>"
						+ "<td style=\"font-weight: bold;\">Email:</td>"
						+ "<td><a href=\"mailto:m_belanec@fai.utb.cz\">m_belanec@fai.utb.cz</a></td>"
						+ "</tr>" + "</table>" + "</center>");
		p.add(tableHTML, new VerticalLayoutData(1, -1));

		panel.addButton(okButton);

		return panel;
	}

	/**
	 * Method saves text to active project CMakeLists.txt.
	 * 
	 * @param textToSave
	 *            Text to save.
	 */
	private static void saveMakefile(String textToSave) {

		CudaOnlineIDE.coiService.saveCMakeLists(textToSave,
				CudaOnlineIDE.ACTIVE_CUDA_COI_PROJECT,
				new AsyncCallback<Boolean>() {

					@Override
					public void onFailure(Throwable caught) {
						GWT.log(caught.getMessage());
						Info.display("Save error", "Error in saving CMakeLists.");
					}

					@Override
					public void onSuccess(Boolean result) {

						if (result.booleanValue()) {
							Info.display("Save CMakeLists",
									"CMakeLists was successfully updated.");
						} else {
							Info.display("Save error",
									"Error in saving CMakeLists.");
						}
					}
				});
	}
}
