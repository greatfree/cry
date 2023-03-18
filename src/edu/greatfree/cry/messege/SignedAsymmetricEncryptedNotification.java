package edu.greatfree.cry.messege;

/**
 * 
 * @author libing
 * 
 * 01/14/2022, Bing Li
 *
 */
public class SignedAsymmetricEncryptedNotification extends AsymmetricEncryptedNotification
{
	private static final long serialVersionUID = 1837013285140307954L;

	private String signature;
	private byte[] encryptedSignature;
//	private String ownerName;
//	private boolean isOwnerRequired;

	public SignedAsymmetricEncryptedNotification(String sessionKey, byte[] encryptedData, byte[] encryptedSymCrypto, String signature, byte[] encryptedSignature)
	{
		super(CryAppID.SIGNED_ASYMMETRIC_ENCRYPTED_NOTIFICATION, sessionKey, encryptedData, encryptedSymCrypto);
		this.signature = signature;
		this.encryptedSignature = encryptedSignature;
//		this.isOwnerRequired = false;
	}

	/*
	 * The constructor is designed for its descendants. 03/22/2022, Bing Li
	 */
	public SignedAsymmetricEncryptedNotification(int encryptionID, String sessionKey, byte[] encryptedData, byte[] encryptedSymCrypto, String signature, byte[] encryptedSignature)
	{
		super(encryptionID, sessionKey, encryptedData, encryptedSymCrypto);
		this.signature = signature;
		this.encryptedSignature = encryptedSignature;
//		this.isOwnerRequired = false;
	}

	/*
	public SignedAsymmetricEncryptedNotification(String sessionKey, byte[] encryptedData, byte[] encryptedSymCrypto, String signature, byte[] encryptedSignature, String ownerName)
	{
		super(CryAppID.SIGNED_ASYMMETRIC_ENCRYPTED_NOTIFICATION, sessionKey, encryptedData, encryptedSymCrypto);
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
