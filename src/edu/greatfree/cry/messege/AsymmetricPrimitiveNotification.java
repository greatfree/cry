package edu.greatfree.cry.messege;

/**
 * 
 * The term, primitive, represents that the message inherits the original parent, i.e., ServerMessage, rather than Notification. The reason to do that is due to the fact to process those messages with ServerDispatcher, not the container-based approach. 06/19/2022, Bing Li
 *  
 * 
 * @author libing
 * 
 * 04/30/2022
 *
 */
public class AsymmetricPrimitiveNotification extends SymmetricPrimitiveNotification
{
	private static final long serialVersionUID = -3761977085931006049L;
	
	private byte[] encryptedSymCrypto;

	public AsymmetricPrimitiveNotification(String sessionKey, byte[] encryptedData, byte[] encryptedSymCrypto)
	{
		super(CryAppID.ASYMMETRIC_PRIMITIVE_NOTIFICATION, sessionKey, encryptedData);
		this.encryptedSymCrypto = encryptedSymCrypto;
	}
	
	public AsymmetricPrimitiveNotification(int encryptionID, String sessionKey, byte[] encryptedData, byte[] encryptedSymCrypto)
	{
		super(encryptionID, sessionKey, encryptedData);
		this.encryptedSymCrypto = encryptedSymCrypto;
	}

	public byte[] getEncryptedSymCrypto()
	{
		return this.encryptedSymCrypto;
	}
}
