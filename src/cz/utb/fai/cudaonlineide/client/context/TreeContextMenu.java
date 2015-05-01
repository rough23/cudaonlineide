package cz.utb.fai.cudaonlineide.client.context;

import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.tree.Tree;

import cz.utb.fai.cudaonlineide.client.CudaOnlineIDE;
import cz.utb.fai.cudaonlineide.client.dto.COIData;
import cz.utb.fai.cudaonlineide.client.popup.PopUpWindow;
import cz.utb.fai.cudaonlineide.client.service.coi.COIService;
import cz.utb.fai.cudaonlineide.client.service.coi.COIServiceAsync;
import cz.utb.fai.cudaonlineide.client.utils.MenuToolbarIcons;
import cz.utb.fai.cudaonlineide.shared.constants.COIConstants;
import cz.utb.fai.cudaonlineide.shared.dto.COIEnum;
import cz.utb.fai.cudaonlineide.shared.dto.COIFile;
import cz.utb.fai.cudaonlineide.shared.dto.COIFolder;
import cz.utb.fai.cudaonlineide.shared.dto.COIObject;
import cz.utb.fai.cudaonlineide.shared.dto.COIProject;
import cz.utb.fai.cudaonlineide.shared.dto.COIWorkspace;
import cz.utb.fai.cudaonlineide.shared.dto.project.COIBuildConfiguration;

/**
 * Class contains all items of project explorer tree context menu.
 * 
 * @author Belenec
 * 
 */
public class TreeContextMenu {

	private final static COIServiceAsync coiService = GWT
			.create(COIService.class);

	/**
	 * New file context menu builder.
	 * 
	 * @param s
	 *            Active tree store.
	 * @param t
	 *            Active project explorer tree.
	 * @return Return new file context item.
	 */
	public static MenuItem getNewFileContextMenu(final TreeStore<COIData> s,
			final Tree<COIData, String> t) {

		MenuItem newFile = new MenuItem();
		newFile.setIcon(MenuToolbarIcons.PROVIDER.menuNew());
		newFile.setText("New file");
		newFile.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				final COIData coiData = t.getSelectionModel().getSelectedItem();
				if (coiData != null && coiData.getPath() != null
						&& coiData.getCoiObject() != null) {

					Window newFilePanel = PopUpWindow
							.newFilePanel((COIFolder) coiData.getCoiObject());
					newFilePanel.show();

					newFilePanel.addHideHandler(new HideHandler() {

						@Override
						public void onHide(HideEvent event) {

							Map<String, Object> popUpData = PopUpWindow.data;

							if (popUpData.containsKey(PopUpWindow.FILENAME)
									&& popUpData
											.containsKey(PopUpWindow.EXTENSION)) {

								final COIFile coiFile = new COIFile();
								coiFile.setName((String) popUpData
										.get(PopUpWindow.FILENAME));
								coiFile.setExtension((String) popUpData
										.get(PopUpWindow.EXTENSION));
								coiFile.setPath(coiData.getPath()
										+ coiFile.getName()
										+ COIConstants.COMMA
										+ coiFile.getExtension());
								coiFile.setText(COIConstants.EMPTY);
								coiFile.setTypeOfCOI(COIEnum.FILE);

								COIWorkspace coiWorkspace = CudaOnlineIDE.ACTIVE_CUDA_COI_WORKSPACE;

								boolean escapeFor = false;

								for (COIProject coiProject : coiWorkspace
										.getItems()) {
									for (COIFolder coiFolder : coiProject
											.getItems()) {
										if (coiFolder.getPath() == coiData
												.getPath()) {
											coiFolder.getItems().add(coiFile);
											escapeFor = true;
											break;
										}
									}
									if (escapeFor) {
										break;
									}
								}

								TreeContextMenu.coiService.createNewFile(
										coiWorkspace, coiFile,
										new AsyncCallback<Boolean>() {

											@Override
											public void onFailure(
													Throwable caught) {
												GWT.log(caught.getMessage());
												Info.display("Create error",
														"Error creating new file.");
											}

											@Override
											public void onSuccess(Boolean result) {

												if (result == true) {
													COIData childCoiData = new COIData(
															coiFile.getName()
																	+ "."
																	+ coiFile
																			.getExtension(),
															coiFile.getPath(),
															coiFile.getText(),
															coiFile);
													s.add(coiData, childCoiData);
													t.setExpanded(coiData, true);
													t.setSize(
															COIConstants.SIZE_100_PERCENTAGE,
															COIConstants.SIZE_100_PERCENTAGE);
													t.focus();
													Info.display(
															"Create",
															"File "
																	+ childCoiData
																			.getName()
																	+ " was created.");
												} else {
													Info.display(
															"Create error",
															"Error creating new file.");
												}
											}
										});
							}
						}
					});
				}
			}
		});

		return newFile;
	}

	/**
	 * New folder context menu builder.
	 * 
	 * @param s
	 *            Active tree store.
	 * @param t
	 *            Active project explorer tree.
	 * @return Return new folder context item.
	 */
	public static MenuItem getNewFolderContextMenu(final TreeStore<COIData> s,
			final Tree<COIData, String> t) {

		MenuItem newFolder = new MenuItem();
		newFolder.setIcon(MenuToolbarIcons.PROVIDER.menuAdd());
		newFolder.setText("New folder");
		newFolder.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				final COIData coiData = t.getSelectionModel().getSelectedItem();
				if (coiData != null && coiData.getPath() != null
						&& coiData.getCoiObject() != null) {

					Window newFolderPanel = PopUpWindow
							.newFolderPanel((COIProject) coiData.getCoiObject());
					newFolderPanel.show();

					newFolderPanel.addHideHandler(new HideHandler() {

						@Override
						public void onHide(HideEvent event) {

							Map<String, Object> popUpData = PopUpWindow.data;

							if (popUpData.containsKey(PopUpWindow.FOLDERNAME)) {

								final COIFolder coiFolder = new COIFolder();
								coiFolder.setName((String) popUpData
										.get(PopUpWindow.FOLDERNAME));
								coiFolder.setPath(coiData.getPath()
										+ coiFolder.getName()
										+ COIConstants.FWD_SLASH);
								coiFolder.setTypeOfCOI(COIEnum.FOLDER);

								COIWorkspace coiWorkspace = CudaOnlineIDE.ACTIVE_CUDA_COI_WORKSPACE;

								for (COIProject coiProject : coiWorkspace
										.getItems()) {
									if (coiProject.getPath() == coiData
											.getPath()) {
										coiProject.getItems().add(coiFolder);
										break;
									}
								}

								TreeContextMenu.coiService.createNewFolder(
										coiWorkspace, coiFolder,
										new AsyncCallback<Boolean>() {

											@Override
											public void onFailure(
													Throwable caught) {
												GWT.log(caught.getMessage());
												Info.display("Create error",
														"Error creating new folder.");
											}

											@Override
											public void onSuccess(Boolean result) {

												if (result == true) {
													COIData childCoiData = new COIData(
															coiFolder.getName(),
															coiFolder.getPath(),
															null, coiFolder);
													s.add(coiData, childCoiData);
													t.setExpanded(coiData, true);
													t.setSize(
															COIConstants.SIZE_100_PERCENTAGE,
															COIConstants.SIZE_100_PERCENTAGE);
													t.focus();
													Info.display(
															"Create",
															"Folder "
																	+ childCoiData
																			.getName()
																	+ " was created.");
												} else {
													Info.display(
															"Create error",
															"Error creating new folder.");
												}
											}
										});
							}
						}
					});
				}
			}
		});

		return newFolder;
	}

	/**
	 * New project context menu builder.
	 * 
	 * @param s
	 *            Active tree store.
	 * @param t
	 *            Active project explorer tree.
	 * @return Return new project context item.
	 */
	public static MenuItem getNewProjectContextMenu(final TreeStore<COIData> s,
			final Tree<COIData, String> t) {

		MenuItem newProject = new MenuItem();
		newProject.setIcon(MenuToolbarIcons.PROVIDER.menuAdd());
		newProject.setText("New project");
		newProject.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				final COIData coiData = t.getSelectionModel().getSelectedItem();
				if (coiData != null && coiData.getPath() != null
						&& coiData.getCoiObject() != null) {

					Window newProjectPanel = PopUpWindow
							.newProjectPanel((COIWorkspace) coiData
									.getCoiObject());
					newProjectPanel.show();

					newProjectPanel.addHideHandler(new HideHandler() {

						@Override
						public void onHide(HideEvent event) {

							Map<String, Object> popUpData = PopUpWindow.data;

							if (popUpData.containsKey(PopUpWindow.PROJECTNAME)
									&& popUpData
											.containsKey(PopUpWindow.BUILD_CONFIGURATION)) {

								final COIProject coiProject = new COIProject();
								coiProject.setName((String) popUpData
										.get(PopUpWindow.PROJECTNAME));
								coiProject.setPath(coiData.getPath()
										+ coiProject.getName()
										+ COIConstants.FWD_SLASH);
								coiProject
										.setBuildConfiguration(new COIBuildConfiguration(
												(String) popUpData
														.get(PopUpWindow.BUILD_CONFIGURATION)));
								coiProject.setTypeOfCOI(COIEnum.PROJECT);

								COIWorkspace coiWorkspace = CudaOnlineIDE.ACTIVE_CUDA_COI_WORKSPACE;
								coiWorkspace.getItems().add(coiProject);

								TreeContextMenu.coiService.createNewProject(
										coiWorkspace, coiProject,
										new AsyncCallback<Boolean>() {

											@Override
											public void onFailure(
													Throwable caught) {
												GWT.log(caught.getMessage());
												Info.display("Create error",
														"Error creating new project.");
											}

											@Override
											public void onSuccess(Boolean result) {

												if (result == true) {
													COIData childCoiData = new COIData(
															coiProject
																	.getName(),
															coiProject
																	.getPath(),
															null, coiProject);
													s.add(coiData, childCoiData);
													t.setExpanded(coiData, true);
													t.setSize(
															COIConstants.SIZE_100_PERCENTAGE,
															COIConstants.SIZE_100_PERCENTAGE);
													t.focus();
													Info.display(
															"Create",
															"Project "
																	+ childCoiData
																			.getName()
																	+ " was created.");
												} else {
													Info.display(
															"Create error",
															"Error creating new project.");
												}
											}
										});
							}
						}
					});
				}
			}
		});

		return newProject;
	}

	/**
	 * Delete objects context menu builder.
	 * 
	 * @param s
	 *            Active tree store.
	 * @param t
	 *            Active project explorer tree.
	 * @return Return delete object context item.
	 */
	public static MenuItem getDeleteInContextMenu(final TreeStore<COIData> s,
			final Tree<COIData, String> t) {

		MenuItem delete = new MenuItem();
		delete.setIcon(MenuToolbarIcons.PROVIDER.menuDelete());
		delete.setText("Delete");
		delete.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {

				final COIData coiData = t.getSelectionModel().getSelectedItem();
				if (coiData != null && coiData.getPath() != null
						&& coiData.getCoiObject() != null) {

					s.remove(coiData);
					t.setSize(COIConstants.SIZE_100_PERCENTAGE,
							COIConstants.SIZE_100_PERCENTAGE);
					t.focus();

					COIObject coiObject = coiData.getCoiObject();
					COIWorkspace coiWorkspace = CudaOnlineIDE.ACTIVE_CUDA_COI_WORKSPACE;
					COIEnum typeOfCOI = coiObject.getTypeOfCOI();

					boolean escapeFor = false;

					switch (typeOfCOI) {
					case PROJECT:

						COIProject coiProjectToDelete = (COIProject) coiObject;

						if (CudaOnlineIDE.ACTIVE_CUDA_COI_PROJECT != null
								&& CudaOnlineIDE.ACTIVE_CUDA_COI_PROJECT
										.getName() == coiProjectToDelete
										.getName()) {
							CudaOnlineIDE.ACTIVE_CUDA_COI_PROJECT = null;
						}
						coiWorkspace.getItems().remove(coiProjectToDelete);
						break;
					case FOLDER:
						for (COIProject coiProject : coiWorkspace.getItems()) {
							if (coiProject.getItems().contains(
									(COIFolder) coiObject)) {
								coiProject.getItems().remove(
										(COIFolder) coiObject);
								break;
							}
						}
						break;
					case FILE:
						for (COIProject coiProject : coiWorkspace.getItems()) {
							for (COIFolder coiFolder : coiProject.getItems()) {
								if (coiFolder.getItems().contains(
										(COIFile) coiObject)) {
									coiFolder.getItems().remove(
											(COIFile) coiObject);

									for (int i = 0; i < CudaOnlineIDE.fileTabPanel
											.getWidgetCount(); i++) {

										String tabToClose = CudaOnlineIDE.fileTabPanel
												.getConfig(
														CudaOnlineIDE.fileTabPanel
																.getWidget(i))
												.getText();

										if (tabToClose == CudaOnlineIDE
												.getExclusivePath(((COIFile) coiObject)
														.getPath())
												|| tabToClose == (CudaOnlineIDE
														.getExclusivePath(((COIFile) coiObject)
																.getPath()) + " *")) {
											CudaOnlineIDE.fileTabPanel
													.remove(i);
											break;
										}
									}

									escapeFor = true;
									break;
								}
							}
							if (escapeFor) {
								break;
							}
						}
						break;
					default:
						Info.display("Delete", "Nothing to delete.");
					}

					TreeContextMenu.coiService.deleteFromWorkspace(
							coiWorkspace, coiObject,
							new AsyncCallback<Boolean>() {

								@Override
								public void onFailure(Throwable caught) {
									GWT.log(caught.getMessage());
									Info.display("Error",
											"Error in deleting object.");
								}

								@Override
								public void onSuccess(Boolean result) {
									if (result == true) {
										Info.display("Delete", "Object "
												+ coiData.getName()
												+ " was deleted.");
									} else {
										Info.display("Error",
												"Error in deleting object.");
									}
								}
							});
				}
			}
		});

		return delete;
	}

	/**
	 * Upload file context menu builder.
	 * 
	 * @param s
	 *            Active tree store.
	 * @param t
	 *            Active project explorer tree.
	 * @return Return upload file context item.
	 */
	public static MenuItem getUploadFileContextMenu(final TreeStore<COIData> s,
			final Tree<COIData, String> t) {

		MenuItem uploadFile = new MenuItem();
		uploadFile.setIcon(MenuToolbarIcons.PROVIDER.menuUpload());
		uploadFile.setText("Upload file");
		uploadFile.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				final COIData coiData = t.getSelectionModel().getSelectedItem();
				if (coiData != null && coiData.getPath() != null
						&& coiData.getCoiObject() != null) {

					COIFolder coiFolder = (COIFolder) coiData.getCoiObject();

					Window fileUploadPanel = PopUpWindow
							.fileUploadPanel(coiFolder);
					fileUploadPanel.show();

					fileUploadPanel.addHideHandler(new HideHandler() {

						@Override
						public void onHide(HideEvent event) {

							Map<String, Object> popUpData = PopUpWindow.data;

							final COIFile coiFile = new COIFile();

							if (popUpData.containsKey(PopUpWindow.FILENAME)
									&& popUpData
											.containsKey(PopUpWindow.EXTENSION)) {

								coiFile.setName((String) popUpData
										.get(PopUpWindow.FILENAME));
								coiFile.setExtension((String) popUpData
										.get(PopUpWindow.EXTENSION));
								coiFile.setPath(coiData.getPath()
										+ coiFile.getName()
										+ COIConstants.COMMA
										+ coiFile.getExtension());
								coiFile.setText(COIConstants.EMPTY);
								coiFile.setTypeOfCOI(COIEnum.FILE);

								COIWorkspace coiWorkspace = CudaOnlineIDE.ACTIVE_CUDA_COI_WORKSPACE;

								boolean escapeFor = false;

								for (COIProject coiProject : coiWorkspace
										.getItems()) {
									for (COIFolder coiFolder : coiProject
											.getItems()) {
										if (coiFolder.getPath() == coiData
												.getPath()) {
											coiFolder.getItems().add(coiFile);
											escapeFor = true;
											break;
										}
									}
									if (escapeFor) {
										break;
									}
								}

								TreeContextMenu.coiService.updateWorkspace(
										coiWorkspace,
										new AsyncCallback<Void>() {

											@Override
											public void onFailure(
													Throwable caught) {
												GWT.log(caught.getMessage());
												Info.display("Error",
														"Error in updating workspace after uploading file.");
											}

											@Override
											public void onSuccess(Void voidValue) {

												COIData childCoiData = new COIData(
														coiFile.getName()
																+ "."
																+ coiFile
																		.getExtension(),
														coiFile.getPath(),
														coiFile.getText(),
														coiFile);
												s.add(coiData, childCoiData);
												t.setExpanded(coiData, true);
												t.setSize(
														COIConstants.SIZE_100_PERCENTAGE,
														COIConstants.SIZE_100_PERCENTAGE);
												t.focus();
											}
										});
							}
						}
					});
				}
			}
		});

		return uploadFile;
	}

	/**
	 * Upload folder context menu builder.
	 * 
	 * @param s
	 *            Active tree store.
	 * @param t
	 *            Active project explorer tree.
	 * @return Return upload folder context item.
	 */
	public static MenuItem getUploadFolderContextMenu(
			final TreeStore<COIData> s, final Tree<COIData, String> t) {

		MenuItem uploadFolder = new MenuItem();
		uploadFolder.setIcon(MenuToolbarIcons.PROVIDER.menuUpload());
		uploadFolder.setText("Upload folder");
		uploadFolder.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				final COIData coiData = t.getSelectionModel().getSelectedItem();
				if (coiData != null && coiData.getPath() != null
						&& coiData.getCoiObject() != null) {

					final COIProject coiProject = (COIProject) coiData
							.getCoiObject();

					Window zipUploadPanel = PopUpWindow
							.zipUploadPanel(coiProject);
					zipUploadPanel.show();

					zipUploadPanel.addHideHandler(new HideHandler() {

						@Override
						public void onHide(HideEvent event) {

							Map<String, Object> popUpData = PopUpWindow.data;

							if (!popUpData.containsKey(PopUpWindow.COIFOLDER)
									|| popUpData.get(PopUpWindow.COIFOLDER) == null
									|| ((String) popUpData
											.get(PopUpWindow.COIFOLDER))
											.isEmpty()) {
								return;
							}

							if (popUpData.get(PopUpWindow.COIFOLDER).equals(
									"Item already exists!")) {
								Info.display("Upload error",
										"Folder with this name already exists.");
								return;
							} else if (popUpData.get(PopUpWindow.COIFOLDER)
									.equals("Bad zip structure!")) {
								Info.display("Upload error", "Bad zip format.");
								return;
							}

							TreeContextMenu.coiService
									.updateWorkspaceUploadFolder(
											CudaOnlineIDE.ACTIVE_CUDA_COI_WORKSPACE,
											coiProject.getPath(),
											(String) popUpData
													.get(PopUpWindow.COIFOLDER),
											new AsyncCallback<COIObject[]>() {

												@Override
												public void onFailure(
														Throwable caught) {
													GWT.log(caught.getMessage());
													Info.display(
															"Upload error",
															"Error in updating workspace after uploading folder.");
												}

												@Override
												public void onSuccess(
														COIObject[] recieveCOI) {

													if (recieveCOI == null
															|| recieveCOI.length != 2
															|| recieveCOI[0] == null
															|| recieveCOI[1] == null) {
														Info.display(
																"Upload error",
																"Error in updating workspace after uploading folder.");
													} else {

														CudaOnlineIDE.ACTIVE_CUDA_COI_WORKSPACE = (COIWorkspace) recieveCOI[0];
														COIFolder coiFolder = (COIFolder) recieveCOI[1];

														COIData childCoiData = new COIData(
																coiFolder
																		.getName(),
																coiFolder
																		.getPath(),
																null, coiFolder);
														s.add(coiData,
																childCoiData);

														for (COIFile coiFile : coiFolder
																.getItems()) {
															COIData childChildCoiData = new COIData(
																	coiFile.getName()
																			+ "."
																			+ coiFile
																					.getExtension(),
																	coiFile.getPath(),
																	coiFile.getText(),
																	coiFile);
															s.add(childCoiData,
																	childChildCoiData);
														}

														t.setExpanded(coiData,
																true);
														t.setSize(
																COIConstants.SIZE_100_PERCENTAGE,
																COIConstants.SIZE_100_PERCENTAGE);
														t.focus();

														Info.display(
																"Upload success",
																"Folder is successfully uploaded.");
													}
												}
											});
						}
					});
				}
			}
		});

		return uploadFolder;
	}

	/**
	 * Upload project context menu builder.
	 * 
	 * @param s
	 *            Active tree store.
	 * @param t
	 *            Active project explorer tree.
	 * @return Return upload project context item.
	 */
	public static MenuItem getUploadProjectContextMenu(
			final TreeStore<COIData> s, final Tree<COIData, String> t) {

		MenuItem uploadProject = new MenuItem();
		uploadProject.setIcon(MenuToolbarIcons.PROVIDER.menuUpload());
		uploadProject.setText("Upload project");
		uploadProject.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				final COIData coiData = t.getSelectionModel().getSelectedItem();
				if (coiData != null && coiData.getPath() != null
						&& coiData.getCoiObject() != null) {

					final COIWorkspace coiWorkspace = (COIWorkspace) coiData
							.getCoiObject();

					Window zipUploadPanel = PopUpWindow
							.zipUploadPanel(coiWorkspace);
					zipUploadPanel.show();

					zipUploadPanel.addHideHandler(new HideHandler() {

						@Override
						public void onHide(HideEvent event) {

							Map<String, Object> popUpData = PopUpWindow.data;

							if (!popUpData.containsKey(PopUpWindow.COIPROJECT)
									|| popUpData.get(PopUpWindow.COIPROJECT) == null
									|| ((String) popUpData
											.get(PopUpWindow.COIPROJECT))
											.isEmpty()) {
								return;
							}

							if (popUpData.get(PopUpWindow.COIPROJECT).equals(
									"Item already exists!")) {
								Info.display("Upload error",
										"Project with this name already exists.");
								return;
							} else if (popUpData.get(PopUpWindow.COIPROJECT)
									.equals("Bad zip structure!")) {
								Info.display("Upload error", "Bad zip format.");
								return;
							}

							TreeContextMenu.coiService
									.updateWorkspaceUploadProject(
											CudaOnlineIDE.ACTIVE_CUDA_COI_WORKSPACE,
											coiWorkspace.getPath(),
											(String) popUpData
													.get(PopUpWindow.COIPROJECT),
											new AsyncCallback<COIObject[]>() {

												@Override
												public void onFailure(
														Throwable caught) {
													GWT.log(caught.getMessage());
													Info.display(
															"Upload error",
															"Error in updating workspace after uploading project.");
												}

												@Override
												public void onSuccess(
														COIObject[] recieveCOI) {

													if (recieveCOI == null
															|| recieveCOI.length != 2
															|| recieveCOI[0] == null
															|| recieveCOI[1] == null) {
														Info.display(
																"Upload error",
																"Error in updating workspace after uploading project.");
													} else {

														CudaOnlineIDE.ACTIVE_CUDA_COI_WORKSPACE = (COIWorkspace) recieveCOI[0];
														COIProject coiProject = (COIProject) recieveCOI[1];

														COIData childCoiData = new COIData(
																coiProject
																		.getName(),
																coiProject
																		.getPath(),
																null,
																coiProject);
														s.add(coiData,
																childCoiData);

														for (COIFolder coiFolder : coiProject
																.getItems()) {

															COIData childChildCoiData = new COIData(
																	coiFolder
																			.getName(),
																	coiFolder
																			.getPath(),
																	null,
																	coiFolder);
															s.add(childCoiData,
																	childChildCoiData);

															for (COIFile coiFile : coiFolder
																	.getItems()) {
																COIData childChildChildCoiData = new COIData(
																		coiFile.getName()
																				+ "."
																				+ coiFile
																						.getExtension(),
																		coiFile.getPath(),
																		coiFile.getText(),
																		coiFile);
																s.add(childChildCoiData,
																		childChildChildCoiData);
															}
														}

														t.setExpanded(coiData,
																true);
														t.setSize(
																COIConstants.SIZE_100_PERCENTAGE,
																COIConstants.SIZE_100_PERCENTAGE);
														t.focus();

														Info.display(
																"Upload success",
																"Project is successfully uploaded.");
													}
												}
											});
						}
					});
				}
			}
		});

		return uploadProject;
	}

	/**
	 * Download context menu builder.
	 * 
	 * @param s
	 *            Active tree store.
	 * @param t
	 *            Active project explorer tree.
	 * @return Return download context item.
	 */
	public static MenuItem getUniversalDownloadContextMenu(
			final TreeStore<COIData> s, final Tree<COIData, String> t) {

		MenuItem newFile = new MenuItem();
		newFile.setIcon(MenuToolbarIcons.PROVIDER.menuDownload());
		newFile.setText("Download");
		newFile.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				COIData coiData = t.getSelectionModel().getSelectedItem();
				if (coiData != null && coiData.getPath() != null
						&& coiData.getCoiObject() != null) {

					String url;

					if (coiData.getCoiObject().getTypeOfCOI() == COIEnum.FILE) {
						url = GWT.getModuleBaseURL()
								+ "downloadFileService?file="
								+ coiData.getPath() + "&fileName="
								+ coiData.getName();
					} else {
						url = GWT.getModuleBaseURL()
								+ "downloadZipService?folder="
								+ coiData.getPath() + "&folderName="
								+ coiData.getName() + "&activeUser="
								+ CudaOnlineIDE.ACTIVE_USER;
					}

					com.google.gwt.user.client.Window.open(url, "_blank",
							"status=0,toolbar=0,menubar=0,location=0");
				}
			}
		});

		return newFile;
	}

	/**
	 * Project properties context menu builder.
	 * 
	 * @param s
	 *            Active tree store.
	 * @param t
	 *            Active project explorer tree.
	 * @return Return project properties context item.
	 */
	public static MenuItem getPropertiesContextMenu(final TreeStore<COIData> s,
			final Tree<COIData, String> t) {

		MenuItem properties = new MenuItem();
		properties.setIcon(MenuToolbarIcons.PROVIDER.menuProperties());
		properties.setText("Properties");
		properties.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				final COIData coiData = t.getSelectionModel().getSelectedItem();
				if (coiData != null && coiData.getPath() != null
						&& coiData.getCoiObject() != null) {

					CudaOnlineIDE.openProjectPropertiesMenuToolbar((COIProject) coiData.getCoiObject());
				}
			}
		});

		return properties;
	}

	/**
	 * Setting active project context menu builder.
	 * 
	 * @param s
	 *            Active tree store.
	 * @param t
	 *            Active project explorer tree.
	 * @return Return setting active project context item.
	 */
	public static MenuItem getSetAsActiveContextMenu(
			final TreeStore<COIData> s, final Tree<COIData, String> t) {

		MenuItem setAsActive = new MenuItem();
		setAsActive.setIcon(MenuToolbarIcons.PROVIDER.projectActive());
		setAsActive.setText("Set as active");
		setAsActive.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				final COIData coiData = t.getSelectionModel().getSelectedItem();
				if (coiData != null && coiData.getPath() != null
						&& coiData.getCoiObject() != null) {
					COIProject coiProject = (COIProject) coiData.getCoiObject();
					CudaOnlineIDE.ACTIVE_CUDA_COI_PROJECT = coiProject;
					CudaOnlineIDE
							.updateBuildConfigurationComboBoxByActiveProject();

					List<COIData> coiDataList = s.getAll();

					for (COIData model : coiDataList) {
						t.refresh(model);
					}
				}
			}
		});

		return setAsActive;
	}

	/**
	 * Creating makefile context menu builder.
	 * 
	 * @param s
	 *            Active tree store.
	 * @param t
	 *            Active project explorer tree.
	 * @return Return creating makefile context item.
	 */
	public static MenuItem getCreateMakefileContextMenu(
			final TreeStore<COIData> s, final Tree<COIData, String> t) {

		MenuItem build = new MenuItem();
		build.setIcon(MenuToolbarIcons.PROVIDER.menuCmake());
		build.setText("Create Makefile");
		build.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				final COIData coiData = t.getSelectionModel().getSelectedItem();
				if (coiData != null && coiData.getPath() != null
						&& coiData.getCoiObject() != null) {

					CudaOnlineIDE.createMakefileMenuToolbar((COIProject) coiData.getCoiObject());
				}
			}
		});

		return build;
	}

	/**
	 * Generating CMakeLists.txt context menu builder.
	 * 
	 * @param s
	 *            Active tree store.
	 * @param t
	 *            Active project explorer tree.
	 * @return Return generating CMakeLists.txt context item.
	 */
	public static MenuItem getGenerateCMakeListsContextMenu(
			final TreeStore<COIData> s, final Tree<COIData, String> t) {

		MenuItem generate = new MenuItem();
		generate.setIcon(MenuToolbarIcons.PROVIDER.menuCmakeLists());
		generate.setText("Generate CMakeLists");
		generate.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				final COIData coiData = t.getSelectionModel().getSelectedItem();
				if (coiData != null && coiData.getPath() != null
						&& coiData.getCoiObject() != null) {

					CudaOnlineIDE.generateCMakeListsMenuToolbar((COIProject) coiData.getCoiObject(), COIConstants.MENU_EXECUTABLE);
				}
			}
		});

		return generate;
	}

	/**
	 * Building project context menu builder.
	 * 
	 * @param s
	 *            Active tree store.
	 * @param t
	 *            Active project explorer tree.
	 * @return Return building project context item.
	 */
	public static MenuItem getBuildContextMenu(final TreeStore<COIData> s,
			final Tree<COIData, String> t) {

		MenuItem build = new MenuItem();
		build.setIcon(MenuToolbarIcons.PROVIDER.menuBuild());
		build.setText("Build");
		build.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				final COIData coiData = t.getSelectionModel().getSelectedItem();
				if (coiData != null && coiData.getPath() != null
						&& coiData.getCoiObject() != null) {

					CudaOnlineIDE.buildProjectMenuToolbar((COIProject) coiData.getCoiObject()); 
				}
			}
		});

		return build;
	}

	/**
	 * Executing project context menu builder.
	 * 
	 * @param s
	 *            Active tree store.
	 * @param t
	 *            Active project explorer tree.
	 * @return Return executing project context item.
	 */
	public static MenuItem getRunContextMenu(final TreeStore<COIData> s,
			final Tree<COIData, String> t) {

		MenuItem build = new MenuItem();
		build.setIcon(MenuToolbarIcons.PROVIDER.menuRun());
		build.setText("Run");
		build.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				final COIData coiData = t.getSelectionModel().getSelectedItem();
				if (coiData != null && coiData.getPath() != null
						&& coiData.getCoiObject() != null) {
					
					CudaOnlineIDE.runProjectMenuToolbar((COIProject) coiData.getCoiObject());
				}
			}
		});

		return build;
	}

}
