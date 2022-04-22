package org.greatfree.cry;

import java.io.Serializable;

import javax.crypto.SecretKey;

/**
 * 
 * @author libing
 * 
 * 01/05/2022, Bing Li
 *
 */
public final class SymmetricCrypto implements Serializable
{
	private static final long serialVersionUID = -6247789457694465191L;

//	private String sessionKey;
	// The key is used to retrieve the public key at its partner. Usually, the peer key plays the role if applicable. 01/11/2022, Bing Li
	private String sourcePeerKey;
	private String destinationPeerKey;
	private SecretKey cipherKey;
	private SecretKey ivKey;
	private String cipherSpec;

//	public SymmetricCrypto(String sessionKey, SecretKey cipherKey, SecretKey ivKey, String cipherSpec)
	public SymmetricCrypto(String sKey, String dKey, SecretKey cipherKey, SecretKey ivKey, String cipherSpec)
	{
		this.sourcePeerKey = sKey;
		this.destinationPeerKey = dKey;
		this.cipherKey = cipherKey;
		this.ivKey = ivKey;
		this.cipherSpec = cipherSpec;
	}

	/*
	public SymmetricCrypto(SecretKey cipherKey, SecretKey ivKey, String cipherSpec)
	{
//		this.sourcePeerKey = sKey;
//		this.destinationPeerKey = dKey;
		this.cipherKey = cipherKey;
		this.ivKey = ivKey;
		this.cipherSpec = cipherSpec;
	}
	*/

	/*
	public SymmetricCrypto(SecretKey cipherKey, SecretKey ivKey, String cipherSpec, String peerKey)
	{
		this.cipherKey = cipherKey;
		this.ivKey = ivKey;
		this.cipherSpec = cipherSpec;
		this.peerKey = peerKey;
	}
	*/

	/*
	public String getSessionKey()
	{
		return this.sessionKey;
	}
	*/

//	public String getPeerKey()
	public String getSourcePeerKey()
	{
		return this.sourcePeerKey;
	}
	
	public String getDestinationPeerKey()
	{
		return this.destinationPeerKey;
	}
	
	public SecretKey getCipherKey()
	{
		return this.cipherKey;
	}
	
	public SecretKey getIVKey()
	{
		return this.ivKey;
	}
	
	public String getCipherSpec()
	{
		return this.cipherSpec;
	}
}
