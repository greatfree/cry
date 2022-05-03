package org.greatfree.cry.messege;

/**
 * 
 * @author libing
 * 
 * 05/01/2022
 *
 */
public class PrivatePrimitiveNotification extends SignedPrimitiveNotification
{
	private static final long serialVersionUID = 8312557735548395429L;
	
	private String ownerName;

	public PrivatePrimitiveNotification(String sessionKey, byte[] encryptedData, byte[] encryptedSymCrypto, String signature, byte[] encryptedSignature, String ownerName)
	{
		super(CryAppID.PRIVATE_PRIMITIVE_NOTIFICATION, sessionKey, encryptedData, encryptedSymCrypto, signature, encryptedSignature);
		this.ownerName = ownerName;
	}

	public String getOwnerName()
	{
		return this.ownerName;
	}
}
