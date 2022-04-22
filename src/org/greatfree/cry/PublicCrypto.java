package org.greatfree.cry;

import java.io.Serializable;
import java.security.PublicKey;

/**
 * 
 * @author libing
 * 
 * 01/11/2022, Bing Li
 * 
 * The crypto, including the public key, is sent to the partner. 01/11/2022, Bing Li
 *
 */
public class PublicCrypto implements Serializable
{
	private static final long serialVersionUID = -6750257825507378967L;

	private String sessionKey;
	private String hostPeerKey;
	private String asymAlgorithm;
	private PublicKey publicKey;
	private String signatureAlgorithm;
	private String signature;

//	public PublicCrypto(String peerKey, String algorithm, PublicKey publicKey)
	public PublicCrypto(String sessionKey, String hostPeerKey, String asymAlgorithm, PublicKey publicKey)
	{
		this.sessionKey = sessionKey;
		this.hostPeerKey = hostPeerKey;
		this.asymAlgorithm = asymAlgorithm;
		this.publicKey = publicKey;
	}

	public PublicCrypto(String sessionKey, String hostPeerKey, String asymAlgorithm, PublicKey publicKey, String signatureAlgorithm, String signature)
	{
		this.sessionKey = sessionKey;
		this.hostPeerKey = hostPeerKey;
		this.asymAlgorithm = asymAlgorithm;
		this.publicKey = publicKey;
		this.signatureAlgorithm = signatureAlgorithm;
		this.signature = signature;
	}

	public String getSessionKey()
	{
		return this.sessionKey;
	}

	public String getHostPeerKey()
	{
		return this.hostPeerKey;
	}
	
	public String getAsymAlgorithm()
	{
		return this.asymAlgorithm;
	}
	
	public PublicKey getPublicKey()
	{
		return this.publicKey;
	}
	
	public String getSignatureAlgorithm()
	{
		return this.signatureAlgorithm;
	}
	
	public String getSignature()
	{
		return this.signature;
	}
}
