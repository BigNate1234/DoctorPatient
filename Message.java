/**
 * 
 */
package edu.miami.cis324.hw4.nfurman;

import java.io.*;

/**
 * @author nfurman
 *
 */
public class Message implements Serializable {

	protected static final long serialVersionUID = 1000;
	
	static final int CREATE_DOC = 00,
			CREATE_PAT = 01,
			CREATE_VISIT = 02,
			ADD_REMOVED_DOC = 03,
			ADD_REMOVED_PAT = 04,
			EDIT_DOC = 10,
			EDIT_PAT = 11,
			EDIT_VISIT = 12,
			REMOVE_DOC = 20,
			REMOVE_PAT = 21,
			REMOVE_VISIT = 22,
			GET_DATA = 99,
			WRITE_DATA = 100,
			LOGOUT = 101;
	private int type;
	private String message;
	
	public Message(int type, String message) {
		this.type = type;
		this.message = message;
	}
	
	public Message() {
		type = 0;
		message = null;
	}
	
	public int getType() {
		return type;
	}
	
	public String getMessage() {
		return message;
	}
}
