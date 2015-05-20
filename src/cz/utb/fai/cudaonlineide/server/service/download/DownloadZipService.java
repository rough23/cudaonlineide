package cz.utb.fai.cudaonlineide.server.service.download;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.zeroturnaround.zip.ZipUtil;

import cz.utb.fai.cudaonlineide.server.service.coi.COIServiceImpl;
import cz.utb.fai.cudaonlineide.server.verifier.RPCVerifier;
import cz.utb.fai.cudaonlineide.shared.constants.COIConstants;
import cz.utb.fai.cudaonlineide.shared.constants.WorkspaceConstants;

/**
 * Download ZIP service servlet class.
 *
 * @author Belanec
 *
 */
public class DownloadZipService extends HttpServlet {

    /**
     * Download zip service serial version UID.
     */
    private static final long serialVersionUID = -9204261960967611829L;

    /**
     * Handler for get request.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String folderPath = req.getParameter("folder");
        String folderName = req.getParameter("folderName");
        String activeUser = req.getParameter("activeUser");

        if (!RPCVerifier.isValidFolderPath(folderPath)) {
            throw new IllegalArgumentException("Downladed folder has not correct path. Server info: " + getServletContext().getServerInfo());
        }

        System.out.println("DownloadZipService LOG [Downloading folder " + folderPath + "]");

        try {
            String zipName = folderName + COIConstants.EXTENSION_COMMA_ZIP;
            String zipPath = WorkspaceConstants.CUDA_WORK_FOLDER + activeUser + COIConstants.FWD_SLASH + zipName;

            File projectToZip = new File(folderPath);

            File toBuildFolder = new File(projectToZip.getParentFile().getAbsolutePath() + "/ToBuildAndZipFolder");
            toBuildFolder.mkdir();

            File projectCopy = new File(toBuildFolder.getAbsolutePath() + "/" + folderName);
            projectCopy.mkdir();

            FileUtils.copyDirectory(projectToZip, projectCopy);

            ZipUtil.pack(toBuildFolder, new File(zipPath));

            COIServiceImpl.removeAll(toBuildFolder);

            File zipToDownload = new File(zipPath);

            if (zipToDownload.exists()) {

                int length = 0;
                ServletOutputStream op = resp.getOutputStream();
                resp.setContentType("application/octet-stream");
                resp.setContentLength((int) zipToDownload.length());
                String browser = req.getHeader("user-agent");

                if (browser.contains("Trident") || browser.contains("IE") || browser.contains("MSIE")) {
                    resp.setHeader("Content-Disposition:", "attachment;filename=" + "\"" + zipName + "\"");
                } else {
                    resp.setHeader("Content-Disposition", "attachment; filename*= \"utf-8''" + zipName + "");
                }

                byte[] bbuf = new byte[1024];
                DataInputStream in = new DataInputStream(new FileInputStream(zipToDownload));

                while ((in != null) && ((length = in.read(bbuf)) != -1)) {
                    op.write(bbuf, 0, length);
                }

                in.close();
                op.flush();
                op.close();

                System.out.println("DownloadZipService LOG [Folder " + folderPath + " was downloaded]");

                zipToDownload.delete();
            }

        } catch (IOException e) {
            Logger.getLogger(DownloadZipService.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}
