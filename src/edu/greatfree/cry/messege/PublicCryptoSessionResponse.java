package edu.greatfree.cry.messege;

import edu.greatfree.cry.PublicCrypto;

/**
 * 
 * @author libing
 * 
 * 01/11/2022, Bing Li
 *
 */
public class PublicCryptoSessionResponse extends EncryptedResponse
{
	private static final long serialVersionUID = 6593064644592866441L;

	private String hostPeerName;
	private PublicCrypto pc;
	private boolean isDone;

	public PublicCryptoSessionResponse(String hostPeerName, PublicCrypto pc, boolean isDone)
	{
		super(CryAppID.PUBLIC_CRYPTO_SESSION_RESPONSE);
		this.hostPeerName = hostPeerName;
		this.pc = pc;
		this.isDone = isDone;
	}
	
	public String getHostPeerName()
	{
		return this.hostPeerName;
	}
	
	public PublicCrypto getPublicCrypto()
	{
		return this.pc;
	}

	public boolean isDone()
	{
		return this.isDone;
	}
}
