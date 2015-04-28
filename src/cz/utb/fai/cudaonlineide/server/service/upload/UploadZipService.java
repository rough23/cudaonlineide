package cz.utb.fai.cudaonlineide.server.service.upload;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.lingala.zip4j.core.ZipFile;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.google.gson.Gson;

import cz.utb.fai.cudaonlineide.server.service.coi.COIServiceImpl;
import cz.utb.fai.cudaonlineide.shared.constants.COIConstants;
import cz.utb.fai.cudaonlineide.shared.constants.WorkspaceConstants;
import cz.utb.fai.cudaonlineide.shared.dto.COIEnum;
import cz.utb.fai.cudaonlineide.shared.dto.COIFile;
import cz.utb.fai.cudaonlineide.shared.dto.COIFolder;
import cz.utb.fai.cudaonlineide.shared.dto.COIProject;
import cz.utb.fai.cudaonlineide.shared.dto.COIWorkspace;
import cz.utb.fai.cudaonlineide.shared.dto.project.COIBuildConfiguration;

/**
 * Upload ZIP service servlet class.
 * 
 * @author Belanec
 *
 */
public class UploadZipService extends HttpServlet {
	
	/**
	 * Upload zip service serial version UID.
	 */
	private static final long serialVersionUID = -4119605492618104035L;

	/**
	 * Handler for post request.
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	
		System.out.println("UploadZipService LOG [Upload ZIP]");
		
		boolean isMultipart = ServletFileUpload.isMultipartContent(req);

		String fileName = null;
		String uploadToFolder = null;
		String typeOfCOI = null;
		String activeUser = null;
		
		if (isMultipart) {
			
			FileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
		
			try {		
				List<FileItem> items = upload.parseRequest(req);
		
				for(FileItem item : items) {
					if (item.isFormField()) {
						String name = item.getFieldName();
						
			            if (name.equalsIgnoreCase("uploadName")) {
			            	fileName = Streams.asString(item.getInputStream(), "UTF-8");
			            } else if (name.equalsIgnoreCase("uploadTo")) {
			            	uploadToFolder = Streams.asString(item.getInputStream(), "UTF-8");
			            } else if (name.equalsIgnoreCase("typeOfCOI")) {
			            	typeOfCOI = Streams.asString(item.getInputStream(), "UTF-8");
			            } else if (name.equalsIgnoreCase("activeUser")) {
			            	activeUser = Streams.asString(item.getInputStream(), "UTF-8");
			            }
					}
				}
				
				for(FileItem item : items) {
					if (!item.isFormField()) {
						String fileNameToSave = item.getName();
						
						if (fileNameToSave != null && !fileNameToSave.isEmpty() && fileName != null && typeOfCOI != null && uploadToFolder != null && activeUser != null) {
							fileNameToSave = FilenameUtils.getName(fileNameToSave);
																
							File uploadedFile = new File(uploadToFolder + fileNameToSave);
							
							try {
								item.write(uploadedFile);
								
								ZipFile zipFile = new ZipFile(uploadToFolder + fileNameToSave);
						        zipFile.extractAll(WorkspaceConstants.CUDA_WORK_FOLDER + activeUser + COIConstants.FWD_SLASH + "extractions" + COIConstants.FWD_SLASH);
								
								File extractionDirectory = new File(WorkspaceConstants.CUDA_WORK_FOLDER + activeUser + COIConstants.FWD_SLASH + "extractions" + COIConstants.FWD_SLASH);
								
								boolean folderExists = false;
								
								for(File tmpFolder : new File(uploadToFolder).listFiles()) {
									if(tmpFolder.isDirectory() && tmpFolder.getName().equals(fileName)) {
										folderExists = true;
										break;
									}
								}
								
								if(typeOfCOI.equals("Folder") && !folderExists) {
									
									COIFolder coiFolder = new COIFolder();
									coiFolder.setName(fileName);
									coiFolder.setPath(uploadToFolder + coiFolder.getName() + COIConstants.FWD_SLASH);
									coiFolder.setTypeOfCOI(COIEnum.FOLDER);
												
									List<COIFile> listFile = new ArrayList<>();
									
									for(File tmpFolder : extractionDirectory.listFiles()) {
										
										if(!tmpFolder.isDirectory()) {
											resp.getWriter().write("Bad zip structure!");
											COIServiceImpl.removeAll(extractionDirectory);
											uploadedFile.delete();
											return;
										}
											
										for(File tmpFile : tmpFolder.listFiles()) {
											
											if(!tmpFile.isFile()) {
												resp.getWriter().write("Bad zip structure!");
												COIServiceImpl.removeAll(extractionDirectory);
												uploadedFile.delete();
												return;
											}
											
											COIFile coiFile = new COIFile();
											
											int l = tmpFile.getName().lastIndexOf(COIConstants.COMMA);
											
											if (l > 0) {
												coiFile.setName(tmpFile.getName().substring(0, l));
												coiFile.setExtension(tmpFile.getName().substring(l+1));
											}
											
											coiFile.setPath(coiFolder.getPath() + tmpFile.getName());
											coiFile.setText(COIConstants.EMPTY);
											coiFile.setTypeOfCOI(COIEnum.FILE);
											
											listFile.add(coiFile);
										}	
										
										FileUtils.copyDirectory(tmpFolder, new File(coiFolder.getPath()));
									}
									coiFolder.setItems(listFile);
																		
									Gson gson = new Gson();
									String serializedCOIFolder = gson.toJson(coiFolder);
									resp.getWriter().write(serializedCOIFolder);
									COIServiceImpl.removeAll(extractionDirectory);
									uploadedFile.delete();
									return;
								
								} else if(typeOfCOI.equals("Project") && !folderExists) {
									
									COIProject coiProject = new COIProject();
									coiProject.setName(fileName);
									coiProject.setPath(uploadToFolder + coiProject.getName() + COIConstants.FWD_SLASH);							
									coiProject.setBuildConfiguration(new COIBuildConfiguration());
									coiProject.setTypeOfCOI(COIEnum.PROJECT);
												
									List<COIFolder> listFolder = new ArrayList<>();
									
									for(File tmpProject : extractionDirectory.listFiles()) {
										
										if(!tmpProject.isDirectory()) {
											resp.getWriter().write("Bad zip structure!");
											COIServiceImpl.removeAll(extractionDirectory);
											uploadedFile.delete();
											return;
										}

										for(File tmpFolder : tmpProject.listFiles()) {
											
											if(tmpFolder.getName().equals("build") || tmpFolder.getName().equals("Build")) {
												continue;
											}
											
											if(!tmpFolder.isDirectory()) {
												resp.getWriter().write("Bad zip structure!");
												COIServiceImpl.removeAll(extractionDirectory);
												uploadedFile.delete();
												return;
											}
											
											COIFolder coiFolder = new COIFolder();
											coiFolder.setName(tmpFolder.getName());
											coiFolder.setPath(coiProject.getPath() + tmpFolder.getName() + COIConstants.FWD_SLASH);
											coiFolder.setTypeOfCOI(COIEnum.FOLDER);
											
											List<COIFile> listFile = new ArrayList<>();
											
											for(File tmpFile : tmpFolder.listFiles()) {
												
												if(!tmpFile.isFile()) {
													resp.getWriter().write("Bad zip structure!");
													COIServiceImpl.removeAll(extractionDirectory);
													uploadedFile.delete();
													return;
												}
												
												COIFile coiFile = new COIFile();
												
												int l = tmpFile.getName().lastIndexOf(COIConstants.COMMA);
												
												if (l > 0) {
													coiFile.setName(tmpFile.getName().substring(0, l));
													coiFile.setExtension(tmpFile.getName().substring(l+1));
												}
												
												coiFile.setPath(coiFolder.getPath() + tmpFile.getName());
												coiFile.setText(COIConstants.EMPTY);
												coiFile.setTypeOfCOI(COIEnum.FILE);
												
												listFile.add(coiFile);
											}
											
											coiFolder.setItems(listFile);
											listFolder.add(coiFolder);
										}
										
										FileUtils.copyDirectory(tmpProject, new File(coiProject.getPath()));
									}
									coiProject.setItems(listFolder);
									
									Gson gson = new Gson();
									String serializedCOIProject = gson.toJson(coiProject);
									resp.getWriter().write(serializedCOIProject);
									COIServiceImpl.removeAll(extractionDirectory);
									uploadedFile.delete();
									return;
									
								} else if(typeOfCOI.equals("Workspace") && !folderExists) {
									
									COIWorkspace coiWorkspace = new COIWorkspace();
									coiWorkspace.setName(fileName);
									coiWorkspace.setPath(uploadToFolder + coiWorkspace.getName() + COIConstants.FWD_SLASH);
									coiWorkspace.setCwsVersion("2.0");
									coiWorkspace.setTypeOfCOI(COIEnum.WORKSPACE);
												
									List<COIProject> listProject = new ArrayList<>();
									
									for(File tmpWorkspace : extractionDirectory.listFiles()) {
										
										if(!tmpWorkspace.isDirectory()) {
											resp.getWriter().write("Bad zip structure!");
											COIServiceImpl.removeAll(extractionDirectory);
											uploadedFile.delete();
											return;
										}

										for(File tmpProject : tmpWorkspace.listFiles()) {
										
											if(!tmpProject.isDirectory()) {
												resp.getWriter().write("Bad zip structure!");
												COIServiceImpl.removeAll(extractionDirectory);
												uploadedFile.delete();
												return;
											}
											
											COIProject coiProject = new COIProject();
											coiProject.setName(tmpProject.getName());
											coiProject.setPath(coiWorkspace.getPath() + tmpProject.getName() + COIConstants.FWD_SLASH);
											coiProject.setTypeOfCOI(COIEnum.PROJECT);
											coiProject.setBuildConfiguration(new COIBuildConfiguration());
											
											List<COIFolder> listFolder = new ArrayList<>();
											
											for(File tmpFolder : tmpProject.listFiles()) {
												
												if(tmpFolder.getName().equals("build") || tmpFolder.getName().equals("Build")) {
													continue;
												}
												
												if(!tmpFolder.isDirectory()) {
													resp.getWriter().write("Bad zip structure!");
													COIServiceImpl.removeAll(extractionDirectory);
													uploadedFile.delete();
													return;
												}
												
												COIFolder coiFolder = new COIFolder();
												coiFolder.setName(tmpFolder.getName());
												coiFolder.setPath(coiProject.getPath() + tmpFolder.getName() + COIConstants.FWD_SLASH);
												coiFolder.setTypeOfCOI(COIEnum.FOLDER);
											
												List<COIFile> listFile = new ArrayList<>();
											
												for(File tmpFile : tmpFolder.listFiles()) {
													
													if(!tmpFile.isFile()) {
														resp.getWriter().write("Bad zip structure!");
														COIServiceImpl.removeAll(extractionDirectory);
														uploadedFile.delete();
														return;
													}
													
													COIFile coiFile = new COIFile();
													
													int l = tmpFile.getName().lastIndexOf(COIConstants.COMMA);
													
													if (l > 0) {
														coiFile.setName(tmpFile.getName().substring(0, l));
														coiFile.setExtension(tmpFile.getName().substring(l+1));
													}
													
													coiFile.setPath(coiFolder.getPath() + tmpFile.getName());
													coiFile.setText(COIConstants.EMPTY);
													coiFile.setTypeOfCOI(COIEnum.FILE);
													
													listFile.add(coiFile);
												}
												coiFolder.setItems(listFile);
												listFolder.add(coiFolder);
											}
											coiProject.setItems(listFolder);
											listProject.add(coiProject);
										}
										
										FileUtils.copyDirectory(tmpWorkspace, new File(coiWorkspace.getPath()));
									}
									coiWorkspace.setItems(listProject);
									
									Gson gson = new Gson();
									String serializedCOIWorkspace = gson.toJson(coiWorkspace);
									resp.getWriter().write(serializedCOIWorkspace);
									COIServiceImpl.removeAll(extractionDirectory);
									uploadedFile.delete();
									return;
									
								} else if((typeOfCOI.equals("Folder") || typeOfCOI.equals("Project") || typeOfCOI.equals("Workspace")) && folderExists) {
									resp.getWriter().write("Item already exists!");
									COIServiceImpl.removeAll(extractionDirectory);
									uploadedFile.delete();
									return;
								} else {
									resp.getWriter().write("Bad zip structure!");
									COIServiceImpl.removeAll(extractionDirectory);
									uploadedFile.delete();
									return;
								}
							} catch (Exception e) {
								Logger.getLogger(UploadZipService.class.getName()).log(Level.SEVERE, null, e);
							}
						}
					}
				}
				
			} catch (FileUploadException e) {
				Logger.getLogger(UploadZipService.class.getName()).log(Level.SEVERE, null, e);
			}
		}
		
		System.out.println("UploadZipService LOG [ZIP was successfully uploaded]");
	}
}
