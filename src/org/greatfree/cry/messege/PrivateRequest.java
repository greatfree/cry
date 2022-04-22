package org.greatfree.cry.messege;

/**
 * 
 * @author libing
 * 
 * 03/22/2022
 *
 */
public class PrivateRequest extends SignedAsymmetricEncryptedRequest
{
	private static final long serialVersionUID = -4698160275296896066L;

	private String ownerName;

	public PrivateRequest(String sessionKey, byte[] encryptedData, byte[] encryptedSymCrypto, String signature, byte[] encryptedSignature, String ownerName)
	{
		super(CryAppID.PRIVATE_REQUEST, sessionKey, encryptedData, encryptedSymCrypto, signature, encryptedSignature);
		this.ownerName = ownerName;
	}
	
	public String getOwnerName()
	{
		return this.ownerName;
	}
}
