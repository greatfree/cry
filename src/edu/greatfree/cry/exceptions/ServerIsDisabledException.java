package edu.greatfree.cry.exceptions;

/**
 * 
 * @author libing
 * 
 * 03/02/2023
 *
 */
public class ServerIsDisabledException extends Exception
{
	private static final long serialVersionUID = 7970553029185290903L;

	private String peerName;

	public ServerIsDisabledException(String peerName)
	{
		this.peerName = peerName;
	}

	public String getPeerName()
	{
		return this.peerName;
	}
}
