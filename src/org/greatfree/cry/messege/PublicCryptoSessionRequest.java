package org.greatfree.cry.messege;

import org.greatfree.cry.PublicCrypto;

/**
 * 
 * @author libing
 * 
 * 01/11/2022, Bing Li
 *
 */
public class PublicCryptoSessionRequest extends EncryptedRequest
{
	private static final long serialVersionUID = 7351021420204867161L;

	private String hostPeerName;
	private PublicCrypto pc;
//	private SymmetricCrypto sc;
//	private byte[] asymEncryptedSymCrypto;
	private byte[] encryptedSignature;

//	public PublicCryptoSessionRequest(PublicCrypto pc, SymmetricCrypto sc)
//	public PublicCryptoSessionRequest(PublicCrypto pc, byte[] asymEncryptedSymCrypto)
	public PublicCryptoSessionRequest(String hostPeerName, PublicCrypto pc)
	{
//		super(CryAppID.PUBLIC_CRYPTO_SESSION_REQUEST, pc.getSessionKey(), asymEncryptedSymCrypto);
		super(CryAppID.PUBLIC_CRYPTO_SESSION_REQUEST);
		this.hostPeerName = hostPeerName;
		this.pc = pc;
//		this.sc = sc;
//		this.asymEncryptedSymCrypto = asymEncryptedSymCrypto;
	}

	public PublicCryptoSessionRequest(String hostPeerName, PublicCrypto pc, byte[] encryptedSignature)
	{
//		super(CryAppID.PUBLIC_CRYPTO_SESSION_REQUEST, pc.getSessionKey(), asymEncryptedSymCrypto);
		super(CryAppID.PUBLIC_CRYPTO_SESSION_REQUEST);
		this.hostPeerName = hostPeerName;
		this.pc = pc;
//		this.sc = sc;
//		this.asymEncryptedSymCrypto = asymEncryptedSymCrypto;
		this.encryptedSignature = encryptedSignature;
	}
	
	public String getHostPeerName()
	{
		return this.hostPeerName;
	}

	public PublicCrypto getPublicCrypto()
	{
		return this.pc;
	}
	
	public byte[] getEncryptedSignature()
	{
		return this.encryptedSignature;
	}

	/*
	public SymmetricCrypto getSymCrypto()
	{
		return this.sc;
	}
	*/

	/*
	public byte[] getAsymEncryptedSymCrypto()
	{
		return this.asymEncryptedSymCrypto;
	}
	*/
}
