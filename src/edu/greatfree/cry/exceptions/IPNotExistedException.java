package edu.greatfree.cry.exceptions;

/**
 * 
 * @author libing
 * 
 * 02/14/2023
 *
 */
public class IPNotExistedException extends Exception
{
	private static final long serialVersionUID = 754370855832592793L;
	
	private String ipKey;
	
	public IPNotExistedException(String ipKey)
	{
		this.ipKey = ipKey;
	}

	public String getIPKey()
	{
		return this.ipKey;
	}
}
