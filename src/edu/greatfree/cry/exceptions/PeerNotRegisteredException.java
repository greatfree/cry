package edu.greatfree.cry.exceptions;

/**
 * 
 * @author libing
 * 
 * 12/31/2022
 *
 */
public class PeerNotRegisteredException extends Exception
{
	private static final long serialVersionUID = -292757492485698769L;
	
	private String peerName;
	
	public PeerNotRegisteredException(String pn)
	{
		this.peerName = pn;
	}

	public String getPeerName()
	{
		return this.peerName;
	}
	
	public String toString()
	{
		return this.peerName + " is not registered";
	}
}
