package cz.utb.fai.cudaonlineide.server.service.login;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import cz.utb.fai.cudaonlineide.client.service.login.LoginService;
import cz.utb.fai.cudaonlineide.imapauthentication.ImapAuthentication;
import cz.utb.fai.cudaonlineide.shared.constants.COIConstants;
import cz.utb.fai.cudaonlineide.shared.constants.WorkspaceConstants;
import cz.utb.fai.cudaonlineide.shared.dto.COIUser;

/**
 * Implementation of LoginService servlet.
 * 
 * @author Belanec
 *
 */
public class LoginServiceImpl extends RemoteServiceServlet implements
		LoginService {
	
	private static final long serialVersionUID = 4456105400553118785L;

	/**
	 * Method for login user to IDE.
	 */
	@Override
	public COIUser loginServer(String name, String password) {
		
		System.out.println("LoginServiceImpl LOG [Login request by " + name + "]");
		
		int randomNum = (int) (Math.random() * (Integer.MAX_VALUE - 1));

		COIUser user = new COIUser(name, String.valueOf(randomNum));

		if (this.isUserToLogByIMAP(user, password)) {
			
			System.out.println("LoginServiceImpl LOG [IMAP login service]");
			
			user.setLoggedIn(true);
			this.createUserWorkFolder(user);
			storeUserInSession(user);
		} else {

			if (this.isUserToLogByUserToLogFile(user, password)) {
				
				System.out.println("LoginServiceImpl LOG [File login service]");
				
				user.setLoggedIn(true);
				this.createUserWorkFolder(user);
				storeUserInSession(user);
			} else {
				System.out.println("LoginServiceImpl LOG [User " + name + " cannot be logged in]");
			}
		}

		return user;
	}

	/**
	 * Method for login from current session.
	 */
	@Override
	public COIUser loginFromSessionServer() {
		
		System.out.println("LoginServiceImpl LOG [Login request from session]");
		
		return getUserAlreadyFromSession();
	}

	/**
	 * Method from logged out active user.
	 */
	@Override
	public void logout() {
		
		System.out.println("LoginServiceImpl LOG [Logout request]");
		
		deleteUserFromSession();
	}

	/**
	 * Method gets user from active session.
	 * 
	 * @return User object in session.
	 */
	private COIUser getUserAlreadyFromSession() {
		COIUser user = null;
		HttpServletRequest httpServletRequest = this.getThreadLocalRequest();
		HttpSession session = httpServletRequest.getSession();
		Object userObj = session.getAttribute(COIConstants.CUF_USER);
		if (userObj != null && userObj instanceof COIUser) {
			user = (COIUser) userObj;
		}
		return user;
	}

	/**
	 * Method store user to session.
	 * 
	 * @param user User object to store.
	 */
	private void storeUserInSession(COIUser user) {
		HttpServletRequest httpServletRequest = this.getThreadLocalRequest();
		HttpSession session = httpServletRequest.getSession(true);
		session.setAttribute(COIConstants.CUF_USER, user);
	}

	/**
	 * Method delete user from session.
	 */
	private void deleteUserFromSession() {
		HttpServletRequest httpServletRequest = this.getThreadLocalRequest();
		HttpSession session = httpServletRequest.getSession();
		session.removeAttribute(COIConstants.CUF_USER);
	}

	/**
	 * Method tests if user is able to login by IMAP authentication.
	 * 
	 * @param userToLog User object to login.
	 * @param userPassword User password.
	 * @return True if user is able to login.
	 */
	private boolean isUserToLogByIMAP(COIUser userToLog, String userPassword) {

		ImapAuthentication imapAuthentication = new ImapAuthentication();
		return imapAuthentication
				.isUserToLog(userToLog.getName(), userPassword);
	}

	/**
	 * Method test if user is able to login by login file.
	 * 
	 * @param userToLog User object to login.
	 * @param userPassword User password.
	 * @return True if user is able to login.
	 */
	private boolean isUserToLogByUserToLogFile(COIUser userToLog,
			String userPassword) {

		try {
			File fXmlFile = new File(WorkspaceConstants.CUDA_WORK_FOLDER
					+ WorkspaceConstants.USER_TO_LOG_FILE);

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

			Document doc = dBuilder.parse(fXmlFile);

			doc.getDocumentElement().normalize();

			if (!doc.getDocumentElement().getNodeName()
					.equals(COIConstants.CUF_USERS)) {
				return false;
			}

			NodeList nUsers = doc.getElementsByTagName(COIConstants.CUF_USER);

			if (nUsers == null || nUsers.getLength() == 0) {
				return false;
			}

			for (int i = 0; i < nUsers.getLength(); i++) {

				Node nUser = nUsers.item(i);

				if (nUser.getNodeName().equals(COIConstants.CUF_USER)
						&& nUser.getNodeType() == Node.ELEMENT_NODE) {

					Element eUser = (Element) nUser;

					if (eUser.getAttribute(COIConstants.CUF_NAME) == null
							|| eUser.getAttribute(COIConstants.CUF_PASSWORD) == null) {
						continue;
					}

					String password = "";

					try {

						MessageDigest mDigest = MessageDigest
								.getInstance("SHA1");
						byte[] result = mDigest.digest(userPassword.getBytes());

						StringBuffer sb = new StringBuffer();
						for (int q = 0; q < result.length; q++) {
							sb.append(Integer.toString(
									(result[q] & 0xff) + 0x100, 16)
									.substring(1));
						}

						password = sb.toString();

					} catch (NoSuchAlgorithmException e) {
						Logger.getLogger(LoginServiceImpl.class.getName()).log(
								Level.SEVERE, null, e);
					}

					if (userToLog.getName().equals(
							eUser.getAttribute(COIConstants.CUF_NAME))
							&& password.equals(eUser
									.getAttribute(COIConstants.CUF_PASSWORD))) {
						return true;
					}
				}
			}

		} catch (SAXException | IOException | ParserConfigurationException e) {
			Logger.getLogger(LoginServiceImpl.class.getName()).log(
					Level.SEVERE, null, e);
			return false;
		}

		return false;
	}

	/**
	 * Method create work folder for login user.
	 * 
	 * @param user Login user object.
	 */
	private void createUserWorkFolder(COIUser user) {

		File userWorkFolder = new File(WorkspaceConstants.CUDA_WORK_FOLDER
				+ user.getName());

		if (userWorkFolder.exists()) {
			if (userWorkFolder.isDirectory()) {
				return;
			} else {
				userWorkFolder.delete();
				userWorkFolder.mkdir();
			}
		} else {
			userWorkFolder.mkdir();
		}
	}
}
