package cz.utb.fai.cudaonlineide.server.service.upload;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.FilenameUtils;

import cz.utb.fai.cudaonlineide.shared.constants.COIConstants;

/**
 * Upload file service servlet class.
 *
 * @author Belanec
 *
 */
public class UploadFileService extends HttpServlet {

    /**
     * Upload file service serial version UID.
     */
    private static final long serialVersionUID = 6136087037948848865L;

    /**
     * Handler for post request.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        System.out.println("UploadFileService LOG [Upload file]");

        boolean isMultipart = ServletFileUpload.isMultipartContent(req);

        String fileName = null;
        String fileExtension = null;
        String uploadToFolder = null;

        if (isMultipart) {

            FileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);

            try {
                List<FileItem> items = upload.parseRequest(req);

                for (FileItem item : items) {
                    if (item.isFormField()) {
                        String name = item.getFieldName();

                        if (name.equalsIgnoreCase("uploadFileName")) {
                            fileName = Streams.asString(item.getInputStream(), "UTF-8");
                        } else if (name.equalsIgnoreCase("uploadFileExtension")) {
                            fileExtension = Streams.asString(item.getInputStream(), "UTF-8");
                        } else if (name.equalsIgnoreCase("uploadToFolder")) {
                            uploadToFolder = Streams.asString(item.getInputStream(), "UTF-8");
                        }
                    }
                }

                for (FileItem item : items) {
                    if (!item.isFormField()) {
                        String fileNameToSave = item.getName();

                        if (fileNameToSave != null && !fileNameToSave.isEmpty() && fileName != null && fileExtension != null && uploadToFolder != null) {
                            fileNameToSave = FilenameUtils.getName(fileNameToSave);

                            boolean fileExists = false;

                            for (File tmpFile : new File(uploadToFolder).listFiles()) {
                                if (tmpFile.isFile() && tmpFile.getName().equals(fileName + COIConstants.COMMA + fileExtension)) {
                                    fileExists = true;
                                    break;
                                }
                            }

                            if (fileExists) {
                                return;
                            }

                            File uploadedFile = new File(uploadToFolder + fileName + COIConstants.COMMA + fileExtension);

                            try {
                                item.write(uploadedFile);
                                resp.getWriter().write("File " + fileNameToSave + " successfully uploaded as " + fileName + COIConstants.COMMA + fileExtension + ".");

                                System.out.println("UploadFileService LOG [File was successfully uploaded]");
                            } catch (Exception e) {
                                Logger.getLogger(UploadFileService.class.getName()).log(Level.SEVERE, null, e);
                            }
                        }
                    }
                }

            } catch (FileUploadException e) {
                Logger.getLogger(UploadFileService.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }

}
