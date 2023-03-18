package edu.greatfree.cry.exceptions;

/**
 * 
 * @author libing
 * 
 * 
 *
 */
public class CelebrateNotExistedException extends Exception
{
	private static final long serialVersionUID = 8619699697198617494L;
	
	private String celebrate;
	
	public CelebrateNotExistedException(String celebrate)
	{
		this.celebrate = celebrate;
	}

	public String getCelebrate()
	{
		return this.celebrate;
	}
	
	public String toString()
	{
		return this.celebrate + " does not exist!";
	}
}
