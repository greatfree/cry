package org.greatfree.cry.messege;

/**
 * 
 * @author libing
 * 
 * 03/23/2022
 *
 */
public class SayAsymmetricByeNotification extends AsymmetricEncryptedNotification
{
	private static final long serialVersionUID = -424859713589623519L;
	
	public SayAsymmetricByeNotification(String sessionKey, byte[] encryptedData, byte[] encryptedSymCrypto)
	{
		super(CryAppID.SAY_ASYMMETRIC_BYE_NOTIFICATION, sessionKey, encryptedData, encryptedSymCrypto);
	}

}
