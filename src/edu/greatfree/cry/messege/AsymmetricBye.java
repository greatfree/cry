package edu.greatfree.cry.messege;

import java.io.Serializable;

/**
 * 
 * The class encloses the data to remove the asymmetric information from the partner immediately before a peer needs to stop. 03/23/2022, Bing Li
 * 
 * @author libing
 * 
 * 03/23/2022
 *
 */
public final class AsymmetricBye implements Serializable
{
	private static final long serialVersionUID = 3312285823612229537L;

	private String hostPeerName;
	private String hostPeerKey;
	private String publicCryptoSessionKey;
	private String signature;

	public AsymmetricBye(String hostPeerName, String hostPeerKey, String pcSessionKey, String signature)
	{
		this.hostPeerName = hostPeerName;
		this.hostPeerKey = hostPeerKey;
		this.publicCryptoSessionKey = pcSessionKey;
		this.signature = signature;
	}
	
	public String getHostPeerName()
	{
		return this.hostPeerName;
	}

	public String getHostPeerKey()
	{
		return this.hostPeerKey;
	}
	
	public String getPublicCryptoSessionKey()
	{
		return this.publicCryptoSessionKey;
	}
	
	public String getSignature()
	{
		return this.signature;
	}
}
