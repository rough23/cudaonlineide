package cz.utb.fai.cudaonlineide.shared.dto;

import java.io.Serializable;

import cz.utb.fai.cudaonlineide.shared.constants.COIConstants;

/**
 * User to login class.
 * 
 * @author Belanec
 *
 */
public class COIUser implements Serializable {

	private static final long serialVersionUID = -8630187375329183569L;

	private String name;
	private boolean loggedIn;
	private String sessionId;

	public COIUser() {
		name = COIConstants.EMPTY;
		loggedIn = false;
		sessionId = COIConstants.EMPTY;
	}

	public COIUser(String name, String sessionId) {
		this.name = name;
		this.loggedIn = false;
		this.sessionId = sessionId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
}
