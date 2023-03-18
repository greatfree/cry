package edu.greatfree.cry.messege;

/**
 * 
 * @author libing
 * 
 * 01/14/2022, Bing Li
 *
 */
public class SignedAsymmetricEncryptedResponse extends AsymmetricEncryptedResponse
{
	private static final long serialVersionUID = 1158505168982341181L;

	private String signature;
	private byte[] encryptedSignature;
//	private boolean isOwner;
//	private String ownerName;

	public SignedAsymmetricEncryptedResponse(byte[] encryptedData, byte[] encryptedSymCrypto, String signature, byte[] encryptedSignature)
	{
		super(CryAppID.SIGNED_ASYMMETRIC_ENCRYPTED_RESPONSE, encryptedData, encryptedSymCrypto);
		this.signature = signature;
		this.encryptedSignature = encryptedSignature;
//		this.isOwner = true;
	}

	public SignedAsymmetricEncryptedResponse(int encryptionID, byte[] encryptedData, byte[] encryptedSymCrypto, String signature, byte[] encryptedSignature)
	{
		super(encryptionID, encryptedData, encryptedSymCrypto);
		this.signature = signature;
		this.encryptedSignature = encryptedSignature;
//		this.isOwner = true;
	}

	/*
	public SignedAsymmetricEncryptedResponse(byte[] encryptedData, byte[] encryptedSymCrypto, String signature, byte[] encryptedSignature, boolean isOwner)
	{
		super(CryAppID.SIGNED_ASYMMETRIC_ENCRYPTED_RESPONSE, encryptedData, encryptedSymCrypto);
		this.signature = signature;
		this.encryptedSignature = encryptedSignature;
		this.isOwner = isOwner;
	}
	*/

	/*
	public SignedAsymmetricEncryptedResponse(String ownerName)
	{
		super(CryAppID.SIGNED_ASYMMETRIC_ENCRYPTED_RESPONSE, null, null);
		this.signature = null;
		this.encryptedSignature = null;
		this.isOwner = false;
		this.ownerName = ownerName;
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
	public boolean isOwner()
	{
		return this.isOwner;
	}
	
	public String getCorrectOwnerName()
	{
		return this.ownerName;
	}
	*/
}
