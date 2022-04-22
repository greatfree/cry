package org.greatfree.cry.messege;

/**
 * 
 * @author libing
 * 
 * 01/14/2022, Bing Li
 *
 */
public class SignedAsymmetricEncryptedRequest extends AsymmetricEncryptedRequest
{
	private static final long serialVersionUID = 1568980930639530645L;

	private String signature;
	private byte[] encryptedSignature;
//	private String ownerName;
//	private boolean isOwnerRequired;

	public SignedAsymmetricEncryptedRequest(String sessionKey, byte[] encryptedData, byte[] encryptedSymCrypto, String signature, byte[] encryptedSignature)
	{
		super(CryAppID.SIGNED_ASYMMETRIC_ENCRYPTED_REQUEST, sessionKey, encryptedData, encryptedSymCrypto);
		this.signature = signature;
		this.encryptedSignature = encryptedSignature;
//		this.isOwnerRequired = false;
	}


	public SignedAsymmetricEncryptedRequest(int encryptionID, String sessionKey, byte[] encryptedData, byte[] encryptedSymCrypto, String signature, byte[] encryptedSignature)
	{
		super(encryptionID, sessionKey, encryptedData, encryptedSymCrypto);
		this.signature = signature;
		this.encryptedSignature = encryptedSignature;
//		this.isOwnerRequired = false;
	}

	/*
	public SignedAsymmetricEncryptedRequest(String sessionKey, byte[] encryptedData, byte[] encryptedSymCrypto, String signature, byte[] encryptedSignature, String ownerName)
	{
		super(CryAppID.SIGNED_ASYMMETRIC_ENCRYPTED_REQUEST, sessionKey, encryptedData, encryptedSymCrypto);
		this.signature = signature;
		this.encryptedSignature = encryptedSignature;
		this.ownerName = ownerName;
		this.isOwnerRequired = true;
	}
	*/

	public String getSignature()
	{
		return this.signature;
	}

	public byte[] getEncryptedSignature()
	{
		return this.encryptedSignature;
	}

	/*
	public boolean isOwnerRequired()
	{
		return this.isOwnerRequired;
	}
	
	public String getOwnerName()
	{
		return this.ownerName;
	}
	*/
}
