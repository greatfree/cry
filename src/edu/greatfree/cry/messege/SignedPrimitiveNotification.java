package edu.greatfree.cry.messege;

/**
 * 
 * 
 * The term, primitive, represents that the message inherits the original parent, i.e., ServerMessage, rather than Notification. The reason to do that is due to the fact to process those messages with ServerDispatcher, not the container-based approach. 06/19/2022, Bing Li
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
