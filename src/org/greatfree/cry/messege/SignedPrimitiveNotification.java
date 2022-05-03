package org.greatfree.cry.messege;

/**
 * 
 * @author libing
 * 
 * 05/01/2022
 *
 */
public class SignedPrimitiveNotification extends AsymmetricPrimitiveNotification
{
	private static final long serialVersionUID = -2650116464516747611L;

	private String signature;
	private byte[] encryptedSignature;

	public SignedPrimitiveNotification(String sessionKey, byte[] encryptedData, byte[] encryptedSymCrypto, String signature, byte[] encryptedSignature)
	{
		super(CryAppID.SIGNED_PRIMITIVE_NOTIFICATION, sessionKey, encryptedData, encryptedSymCrypto);
		this.signature = signature;
		this.encryptedSignature = encryptedSignature;
	}

	public SignedPrimitiveNotification(int encryptionID, String sessionKey, byte[] encryptedData, byte[] encryptedSymCrypto, String signature, byte[] encryptedSignature)
	{
		super(encryptionID, sessionKey, encryptedData, encryptedSymCrypto);
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
