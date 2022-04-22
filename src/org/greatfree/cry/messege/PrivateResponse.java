package org.greatfree.cry.messege;

/**
 * 
 * @author libing
 * 
 * 03/22/2022
 *
 */
public class PrivateResponse extends SignedAsymmetricEncryptedResponse
{
	private static final long serialVersionUID = -9070120888832649306L;

	private boolean isOwner;
	private String ownerName;

	public PrivateResponse(byte[] encryptedData, byte[] encryptedSymCrypto, String signature, byte[] encryptedSignature)
	{
		super(CryAppID.PRIVATE_RESPONSE, encryptedData, encryptedSymCrypto, signature, encryptedSignature);
		this.isOwner = true;
	}

	public PrivateResponse(String ownerName)
	{
		super(CryAppID.PRIVATE_RESPONSE, null, null, null, null);
		this.isOwner = false;
		this.ownerName = ownerName;
	}
	
	public boolean isOwner()
	{
		return this.isOwner;
	}
	
	public String getOwnerName()
	{
		return this.ownerName;
	}
}
