package edu.greatfree.cry.messege;

/**
 * 
 * @author Bing Li
 * 
 * 02/03/2022, Bing Li
 *
 */
public class OwnershipRequest extends AsymmetricEncryptedRequest
{
	private static final long serialVersionUID = 4370213360841764940L;
	
	private String signature;
	private byte[] encryptedSignature;

	public OwnershipRequest(String sessionKey, byte[] encryptedOwner, byte[] encryptedSymCrypto, String signature, byte[] encryptedSignature)
	{
		super(CryAppID.OWNERSHIP_REQUEST, sessionKey, encryptedOwner, encryptedSymCrypto);
		this.signature = signature;
		this.encryptedSignature = encryptedSignature;
	}

	public String getSignature()
	{
		return this.signature;
	}

	public byte[] getEncryptedSignature()
	{
		return this.encryptedSignature;
	}
}
