package org.greatfree.cry.messege;

/**
 * 
 * Only the client that owns the remote nodes can send the notification. 03/22/2022, Bing Li
 * 
 * @author libing
 * 
 * 03/22/2022
 *
 */
public class PrivateNotification extends SignedAsymmetricEncryptedNotification
{
	private static final long serialVersionUID = -6144479039618994399L;

	private String ownerName;

	public PrivateNotification(String sessionKey, byte[] encryptedData, byte[] encryptedSymCrypto, String signature, byte[] encryptedSignature, String ownerName)
	{
		super(CryAppID.PRIVATE_NOTIFICATION, sessionKey, encryptedData, encryptedSymCrypto, signature, encryptedSignature);
		this.ownerName = ownerName;
	}

	public String getOwnerName()
	{
		return this.ownerName;
	}
}
