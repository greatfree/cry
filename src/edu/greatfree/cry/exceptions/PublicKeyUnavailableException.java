package edu.greatfree.cry.exceptions;

/**
 * 
 * @author Bing Li
 * 
 * 02/07/2022
 *
 */
public class PublicKeyUnavailableException extends Exception
{
	private static final long serialVersionUID = -3287829131673206930L;
	
	public String partner;
	
	public PublicKeyUnavailableException(String partner)
	{
		this.partner = partner;
	}

	public String getPartner()
	{
		return this.partner;
	}
}
