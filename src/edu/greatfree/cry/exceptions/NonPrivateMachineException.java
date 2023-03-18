package edu.greatfree.cry.exceptions;

/**
 * 
 * @author libing
 * 
 * 03/22/2022
 *
 */
public class NonPrivateMachineException extends Exception
{
	private static final long serialVersionUID = -5684824022873332828L;
	
	private String message;

	public NonPrivateMachineException(String message)
	{
		this.message = message;
	}
	
	public String getMessage()
	{
		return this.message;
	}
}
