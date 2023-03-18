package edu.greatfree.cry.messege;

/**
 * 
 * The term, primitive, represents that the message inherits the original parent, i.e., ServerMessage, rather than Request. The reason to do that is due to the fact to process those messages with ServerDispatcher, not the container-based approach. 06/19/2022, Bing Li
 * 
 * @author libing
 * 
 * 05/04/2022
 *
 */
public class PrivatePrimitiveRequest extends SignedPrimitiveRequest
{
	private static final long serialVersionUID = 5259740120442188153L;

	private String ownerName;

	public PrivatePrimitiveRequest(String sessionKey, byte[] encryptedData, byte[] encryptedSymCrypto, String signature, byte[] encryptedSignature, String ownerName)
	{
		super(CryAppID.PRIVATE_PRIMITIVE_REQUEST, sessionKey, encryptedData, encryptedSymCrypto, signature, encryptedSignature);
		this.ownerName = ownerName;
	}

	public String getOwnerName()
	{
		return this.ownerName;
	}
}
