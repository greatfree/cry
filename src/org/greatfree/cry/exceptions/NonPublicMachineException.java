package org.greatfree.cry.exceptions;

/**
 * 
 * @author libing
 * 
 * 03/22/2022
 *
 */
public class NonPublicMachineException extends Exception
{
	private static final long serialVersionUID = 6529229741667189192L;

	private String message;

	public NonPublicMachineException(String message)
	{
		this.message = message;
	}
	
	public String getMessage()
	{
		return this.message;
	}
}
