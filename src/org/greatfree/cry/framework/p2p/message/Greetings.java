package org.greatfree.cry.framework.p2p.message;

import java.io.Serializable;

/**
 * 
 * @author libing
 * 
 * 01/22/2022, Bing Li
 *
 */
public class Greetings implements Serializable
{
	private static final long serialVersionUID = 5281282225599631470L;
	
	private String peerName;
	private int giftsCount;
	private String message;
	
	public Greetings(String peerName, int giftsCount, String message)
	{
		this.peerName = peerName;
		this.giftsCount = giftsCount;
		this.message = message;
	}

	public String getPeerName()
	{
		return this.peerName;
	}
	
	public int getGiftsCount()
	{
		return this.giftsCount;
	}
	
	public String getMessage()
	{
		return this.message;
	}
	
	public String toString()
	{
		return this.peerName + " gave you " + this.giftsCount + " gifts, and he said, " + this.message;
	}
}
