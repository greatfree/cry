package org.greatfree.cry.exceptions;

/**
 * 
 * @author Bing Li
 * 
 * 02/07/2022
 *
 */
public class SymmetricKeyUnavailableException extends Exception
{
	private static final long serialVersionUID = -2291774928101186923L;

	public String partner;

	public SymmetricKeyUnavailableException(String partner)
	{
		this.partner = partner;
	}

	public String getPartner()
	{
		return this.partner;
	}
}
