package cz.utb.fai.cudaonlineide.client.service.login;

import com.google.gwt.user.client.rpc.AsyncCallback;
import cz.utb.fai.cudaonlineide.shared.dto.COIUser;

/**
 * The asynchronous counterpart of <code>LoginService</code>.
 *
 * @author Belanec
 */
public interface LoginServiceAsync {

    /**
     * Method provides login user to server.
     *
     * @param name User name.
     * @param password User password.
     * @param callback Asynchronous callback with active user.
     */
    void loginServer(String name, String password, AsyncCallback<COIUser> callback);

    /**
     * Method provides login user from session.
     *
     * @param callback Asynchronous callback with active user.
     */
    void loginFromSessionServer(AsyncCallback<COIUser> callback);

    /**
     * Method logout active user.
     *
     * @param callback Empty asynchronous callback.
     */
    void logout(AsyncCallback<Void> callback);
}
