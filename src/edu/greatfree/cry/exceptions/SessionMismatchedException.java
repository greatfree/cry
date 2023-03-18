package edu.greatfree.cry.exceptions;

/**
 * 
 * @author libing
 * 
 * 03/22/2022
 *
 */
public class SessionMismatchedException extends Exception
{
	private static final long serialVersionUID = 6542633253034919587L;

	private String message;

	public SessionMismatchedException(String message)
	{
		this.message = message;
	}
	
	public String getMessage()
	{
		return this.message;
	}
}
