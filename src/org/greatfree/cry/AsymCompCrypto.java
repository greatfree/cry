package org.greatfree.cry;

import java.io.Serializable;

import javax.crypto.SecretKey;

/**
 * 
 * The asymmetric cryptography algorithm needs a companion, i.e., a symmetric crypto, to encrypt long-length data since the asymmetric algorithm cannot encrypt long-length data. To differentiate the symmetric crypto from the one for the normal symmetric cryptography data, the class is created. 04/18/2022, Bing Li
 * 
 * @author libing
 * 
 * 04/18/2022
 *
 */
public final class AsymCompCrypto implements Serializable
{
	private static final long serialVersionUID = 3163194231693818986L;

	private SecretKey cipherKey;
	private SecretKey ivKey;
	private String cipherSpec;
	// The peerKey is used to retrieve the public key at its partner. 01/11/2022, Bing Li
	private String peerKey;

	public AsymCompCrypto(SecretKey cipherKey, SecretKey ivKey, String cipherSpec, String peerKey)
	{
		this.cipherKey = cipherKey;
		this.ivKey = ivKey;
		this.cipherSpec = cipherSpec;
		this.peerKey = peerKey;
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

	public String getPeerKey()
	{
		return this.peerKey;
	}
}
