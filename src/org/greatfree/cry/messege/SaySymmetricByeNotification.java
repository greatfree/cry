package org.greatfree.cry.messege;

/**
 * 
 * @author libing
 * 
 * 03/23/2022
 *
 */
public class SaySymmetricByeNotification extends EncryptedNotification
{
	private static final long serialVersionUID = 4062285292999152602L;

	public SaySymmetricByeNotification(String sessionKey, byte[] encryptedData)
	{
		super(CryAppID.SAY_SYMMETRIC_BYE_NOTIFICATION, sessionKey, encryptedData);
	}
}
