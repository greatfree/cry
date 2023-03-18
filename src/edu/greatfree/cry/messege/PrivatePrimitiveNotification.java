package edu.greatfree.cry.messege;

/**
 * 
 * The term, primitive, represents that the message inherits the original parent, i.e., ServerMessage, rather than Notification. The reason to do that is due to the fact to process those messages with ServerDispatcher, not the container-based approach. 06/19/2022, Bing Li
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
