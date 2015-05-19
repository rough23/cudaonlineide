package cz.utb.fai.cudaonlineide.server.service.download;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cz.utb.fai.cudaonlineide.server.verifier.RPCVerifier;

/**
 * Download file service servlet class.
 *
 * @author Belanec
 *
 */
public class DownloadFileService extends HttpServlet {

    /**
     * Download file service serial version UID.
     */
    private static final long serialVersionUID = 3770112776648853109L;

    /**
     * Handler for get request.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String filePath = req.getParameter("file");
        String fileName = req.getParameter("fileName");

        if (!RPCVerifier.isValidFilePath(filePath)) {
            throw new IllegalArgumentException("Downladed file has not correct file path. Server info: " + getServletContext().getServerInfo());
        }

        System.out.println("DownloadFileService LOG [Downloading file " + filePath + "]");

        File fileToDownload = new File(filePath);

        int length = 0;
        ServletOutputStream op = resp.getOutputStream();
        resp.setContentType("application/octet-stream");
        resp.setContentLength((int) fileToDownload.length());
        String browser = req.getHeader("user-agent");

        if (browser.contains("Trident") || browser.contains("IE") || browser.contains("MSIE")) {
            resp.setHeader("Content-Disposition:", "attachment;filename=" + "\"" + fileName + "\"");
        } else {
            resp.setHeader("Content-Disposition", "attachment; filename*= \"utf-8''" + fileName + "");
        }

        byte[] bbuf = new byte[1024];
        DataInputStream in = new DataInputStream(new FileInputStream(fileToDownload));

        while ((in != null) && ((length = in.read(bbuf)) != -1)) {
            op.write(bbuf, 0, length);
        }

        in.close();
        op.flush();
        op.close();

        System.out.println("DownloadFileService LOG [File " + filePath + " was downloaded]");
    }
}
