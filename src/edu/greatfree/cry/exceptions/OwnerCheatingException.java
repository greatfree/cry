package edu.greatfree.cry.exceptions;

/**
 * 
 * @author Bing Li
 * 
 * 02/07/2022
 *
 */
public class OwnerCheatingException extends Exception
{
	private static final long serialVersionUID = -2934985381434111202L;
	
	private String owner;
	
	public OwnerCheatingException(String owner)
	{
		this.owner = owner;
	}

	public String getOwner()
	{
		return this.owner;
	}
}
