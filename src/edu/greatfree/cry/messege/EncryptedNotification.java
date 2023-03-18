package edu.greatfree.cry.messege;

import org.greatfree.message.container.Notification;

/**
 * 
 * @author libing
 * 
 *         01/04/2022, Bing Li
 *
 */
public class EncryptedNotification extends Notification
{
	private static final long serialVersionUID = -8161872907730516482L;

	private String sessionKey;
	private byte[] encryptedData;

//	public EncryptedNotification(String sessionKey, byte[] encryptedData)
//	public EncryptedNotification(String sessionKey, byte[] encryptedData)
	public EncryptedNotification(int encryptionID, String sessionKey, byte[] encryptedData)
	{
//		super(CryAppID.SYMMETRIC_ENCRYPTED_NOTIFICATION);
		super(encryptionID);
		this.sessionKey = sessionKey;
		this.encryptedData = encryptedData;
	}
	
	public String getSessionKey()
	{
		return this.sessionKey;
	}
	
	public byte[] getEncryptedData()
	{
		return this.encryptedData;
	}
}
