package cz.utb.fai.cudaonlineide.client.popup;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.PasswordField;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.info.Info;

import cz.utb.fai.cudaonlineide.client.CudaOnlineIDE;
import cz.utb.fai.cudaonlineide.shared.constants.COIConstants;
import cz.utb.fai.cudaonlineide.shared.constants.WorkspaceConstants;
import cz.utb.fai.cudaonlineide.shared.dto.COIUser;

/**
 * Class contains login pop up window.
 *
 * @author Belanec
 *
 */
public class Login {

    /**
     * Creating of login pop up window.
     *
     * @return Login pop up window.
     */
    public static Window loginPanel() {

        final Window panel = new Window();
        panel.setBodyStyle("background: none; padding: 10px");
        panel.setHeadingText("Login");
        panel.setButtonAlign(BoxLayoutPack.CENTER);
        panel.setWidth(500);
        panel.setLayoutData(new MarginData(10));
        panel.setModal(true);
        panel.setResizable(false);
        panel.setClosable(false);

        VerticalLayoutContainer p = new VerticalLayoutContainer();
        panel.add(p);

        final TextButton logButton = new TextButton(COIConstants.BUTTON_LOGIN);

        final TextField login = new TextField();
        login.setAllowBlank(false);
        login.setEmptyText("Enter login...");

        final PasswordField password = new PasswordField();
        password.setAllowBlank(false);
        password.setEmptyText("Enter password...");

        logButton.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {

                if (login.getText().isEmpty() || password.getText().isEmpty()) {
                    Info.display("Login error",
                            "You must type login and password.");
                    return;
                }

                String loginText = login.getText();
                String passwordText = password.getText();

                CudaOnlineIDE.loginService.loginServer(loginText, passwordText,
                        new AsyncCallback<COIUser>() {

                            @Override
                            public void onSuccess(COIUser user) {
                                if (user.isLoggedIn()) {
                                    String sessionID = user.getSessionId();
                                    final long DURATION = 1000 * 60 * 60 * 24
                                    * 1;
                                    Date expires = new Date(System
                                            .currentTimeMillis() + DURATION);
                                    Cookies.setCookie(COIConstants.CUF_SID,
                                            sessionID, expires, null,
                                            COIConstants.FWD_SLASH, false);

                                    CudaOnlineIDE.ACTIVE_CUDA_FOLDER = WorkspaceConstants.CUDA_WORK_FOLDER
                                    + user.getName()
                                    + COIConstants.FWD_SLASH;
                                    CudaOnlineIDE.ACTIVE_USER = user.getName();
                                    panel.hide();
                                } else {
                                    Info.display("Login error",
                                            "Access Denied. Check your user-name and password.");
                                }
                            }

                            @Override
                            public void onFailure(Throwable caught) {
                                GWT.log(caught.getMessage());
                                Info.display("Login error",
                                        "Access Denied. Check your user-name and password.");
                            }
                        });
            }
        });

        p.add(new FieldLabel(login, "Login"), new VerticalLayoutData(1, -1));
        p.add(new FieldLabel(password, "Password"), new VerticalLayoutData(1,
                -1));
        panel.addButton(logButton);

        return panel;
    }
}
