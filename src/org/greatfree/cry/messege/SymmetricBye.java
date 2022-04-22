package org.greatfree.cry.messege;

import java.io.Serializable;

/**
 * 
 * @author libing
 * 
 * 03/23/2022
 *
 */
public final class SymmetricBye implements Serializable
{
	private static final long serialVersionUID = -2717331579093929443L;

	private String hostPeerName;
	private String hostPeerKey;

	public SymmetricBye(String hostPeerName, String hostPeerKey)
	{
		this.hostPeerName = hostPeerName;
		this.hostPeerKey = hostPeerKey;
	}
	
	public String getHostPeerName()
	{
		return this.hostPeerName;
	}

	public String getHostPeerKey()
	{
		return this.hostPeerKey;
	}
}
