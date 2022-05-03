package org.greatfree.cry.messege;

import org.greatfree.message.ServerMessage;

/**
 * 
 * @author libing
 * 
 * 04/30/2022
 *
 */
public class SymmetricPrimitiveNotification extends ServerMessage
{
	private static final long serialVersionUID = 5955738657457036345L;
	
	private String sessionKey;
	private byte[] encryptedData;

	public SymmetricPrimitiveNotification(String sessionKey, byte[] encryptedData)
	{
		super(CryAppID.SYMMETRIC_PRIMITIVE_NOTIFICATION);
		this.sessionKey = sessionKey;
		this.encryptedData = encryptedData;
	}

	public SymmetricPrimitiveNotification(int encryptionID, String sessionKey, byte[] encryptedData)
	{
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
