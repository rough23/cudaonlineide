package cz.utb.fai.cudaonlineide.client.service.login;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import cz.utb.fai.cudaonlineide.shared.dto.COIUser;

/**
 * The client-side stub for the RPC login service.
 * 
 * @author Belanec
 */
@RemoteServiceRelativePath("loginService")
public interface LoginService extends RemoteService
{ 
	/**
	 * Method provides login user to server.
	 * 
	 * @param name User name.
	 * @param password User password.
	 * @return Active user.
	 */
    COIUser loginServer(String name, String password);
 
    /**
     * Method provides login user from session.
     * 
     * @return Active user.
     */
    COIUser loginFromSessionServer();
 
    /**
     * Method logout active user.
     */
    void logout();
}