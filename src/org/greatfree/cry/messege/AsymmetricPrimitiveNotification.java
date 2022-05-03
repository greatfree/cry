package org.greatfree.cry.messege;

/**
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
