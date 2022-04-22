package org.greatfree.cry.messege;

import org.greatfree.cry.SymmetricCrypto;

/**
 * 
 * @author libing
 * 
 * 01/05/2022, Bing Li
 *
 */
// public class CryptoSessionRequest extends Request
public class SymmetricCryptoSessionRequest extends EncryptedRequest
{
	private static final long serialVersionUID = -5927113084780023709L;

	private String inviterKey;
	private SymmetricCrypto sc;

	public SymmetricCryptoSessionRequest(SymmetricCrypto sc)
	{
		super(CryAppID.SYMMETRIC_CRYPTO_SESSION_REQUEST);
		this.sc = sc;
	}

	public SymmetricCryptoSessionRequest(String inviterKey, SymmetricCrypto sc)
	{
		super(CryAppID.SYMMETRIC_CRYPTO_SESSION_REQUEST);
		this.inviterKey = inviterKey;
		this.sc = sc;
	}
	
	public String getInviterKey()
	{
		return this.inviterKey;
	}
	
	public SymmetricCrypto getCrypto()
	{
		return this.sc;
	}
}
