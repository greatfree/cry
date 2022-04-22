package org.greatfree.cry.exceptions;

/**
 * 
 * @author libing
 * 
 * 02/27/2022
 *
 */
public class JoinChainFailedException extends Exception
{
	private static final long serialVersionUID = 1146353670296271433L;

	private String peerName;
	private String sessionKey;

	public JoinChainFailedException(String peerName, String sessionKey)
	{
		this.peerName = peerName;
		this.sessionKey = sessionKey;
	}

	public String getPeerName()
	{
		return this.peerName;
	}
	
	public String getSessionKey()
	{
		return this.sessionKey;
	}
}
