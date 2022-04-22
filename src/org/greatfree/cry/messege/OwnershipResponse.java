package org.greatfree.cry.messege;

/**
 * 
 * @author Bing Li
 * 
 * 02/03/2022, Bing Li
 *
 */
public class OwnershipResponse extends AsymmetricEncryptedResponse
{
	private static final long serialVersionUID = 5922750833471634638L;
	
	private String signature;
	private byte[] encryptedSignature;

	/*
	public OwnershipResponse(byte[] isSucceeded, byte[] encryptedSymCrypto, String signature, byte[] encryptedSignature)
	{
		super(CryAppID.OWNERSHIP_RESPONSE, isSucceeded, encryptedSymCrypto);
		this.signature = signature;
		this.encryptedSignature = encryptedSignature;
	}
	*/

	public OwnershipResponse(byte[] allOwners, byte[] encryptedSymCrypto, String signature, byte[] encryptedSignature)
	{
		super(CryAppID.OWNERSHIP_RESPONSE, allOwners, encryptedSymCrypto);
		this.signature = signature;
		this.encryptedSignature = encryptedSignature;
	}

	public String getSignature()
	{
		return this.signature;
	}

	public byte[] getEncryptedSignature()
	{
		return this.encryptedSignature;
	}
}
