package edu.greatfree.cry.exceptions;

/**
 * 
 * @author libing
 * 
 * 01/05/2023
 *
 */
public class CommunityNotExistedException extends Exception
{
	private static final long serialVersionUID = -6412191069410113110L;
	
	private String community;
	
	public CommunityNotExistedException(String community)
	{
		this.community = community;
	}

	public String getCommunity()
	{
		return this.community;
	}
	
	public String toString()
	{
		return this.community + " does not exist!";
	}
}
