package edu.greatfree.cry.exceptions;

/**
 * 
 * @author Bing Li
 * 
 * 02/07/2022
 *
 */
public class CheatingException extends Exception
{
	private static final long serialVersionUID = 3109062026705377930L;
	
	private String signature;
	
	public CheatingException(String signature)
	{
		this.signature = signature;
	}

	public String getSignature()
	{
		return this.signature;
	}
}
