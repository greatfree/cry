package edu.greatfree.cry.exceptions;

/**
 * 
 * @author libing
 * 
 * 12/31/2022
 *
 */
public class NotSignedInException extends Exception
{
	private static final long serialVersionUID = 1166010088055497669L;
	
	private String userName;
	private String password;
	
	public NotSignedInException(String un, String pw)
	{
		this.userName = un;
		this.password = pw;
	}

	public String getUserName()
	{
		return this.userName;
	}
	
	public String getPassword()
	{
		return this.password;
	}
	
	public String toString()
	{
		return "You have not signed in as " + userName + " or any other user ...";
	}
}
