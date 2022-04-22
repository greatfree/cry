package org.greatfree.cry.messege;

/**
 * 
 * @author libing
 * 
 * 01/12/2022, Bing Li
 *
 */
public class AsymmetricEncryptedNotification extends EncryptedNotification
{
	private static final long serialVersionUID = 8923447670806100825L;
	
	private byte[] encryptedSymCrypto;
	private boolean isServerMessage;

	public AsymmetricEncryptedNotification(String sessionKey, byte[] encryptedData, byte[] encryptedSymCrypto)
	{
		super(CryAppID.ASYMMETRIC_ENCRYPTED_NOTIFICATION, sessionKey, encryptedData);
		this.encryptedSymCrypto = encryptedSymCrypto;
		this.isServerMessage = false;
	}

	public AsymmetricEncryptedNotification(String sessionKey, byte[] encryptedData, byte[] encryptedSymCrypto, boolean isServerMessage)
	{
		super(CryAppID.ASYMMETRIC_ENCRYPTED_NOTIFICATION, sessionKey, encryptedData);
		this.encryptedSymCrypto = encryptedSymCrypto;
		this.isServerMessage = isServerMessage;
	}

	public AsymmetricEncryptedNotification(int encryptionID, String sessionKey, byte[] encryptedData, byte[] encryptedSymCrypto)
	{
		super(encryptionID, sessionKey, encryptedData);
		this.encryptedSymCrypto = encryptedSymCrypto;
	}

	public byte[] getEncryptedSymCrypto()
	{
		return this.encryptedSymCrypto;
	}
	
	public boolean isServerMessage()
	{
		return this.isServerMessage;
	}
}
