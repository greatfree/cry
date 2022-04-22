package org.greatfree.cry.exceptions;

/**
 * 
 * @author Bing Li
 * 
 * 02/04/2022
 * 
 * 
 *
 */
public class CryptographyMismatchException extends Exception
{
	private static final long serialVersionUID = 7643873827942104236L;

	private String currentCryptoDescription;
	
	public CryptographyMismatchException(String currentCryptoDescription)
	{
		this.currentCryptoDescription = currentCryptoDescription;
	}
	
	public String getCurrentCryptoDescription()
	{
		return this.currentCryptoDescription;
	}
}
