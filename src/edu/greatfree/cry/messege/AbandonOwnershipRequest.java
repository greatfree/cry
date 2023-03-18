package edu.greatfree.cry.messege;

/**
 * 
 * @author libing
 * 
 * 05/17/2022
 *
 */
public class AbandonOwnershipRequest extends AsymmetricEncryptedRequest
{
	private static final long serialVersionUID = 7542030111140684811L;
	
	private String signature;
	private byte[] encryptedSignature;

	public AbandonOwnershipRequest(String sessionKey, byte[] encryptedData, byte[] encryptedSymCrypto, String signature, byte[] encryptedSignature)
	{
		super(CryAppID.ABANDON_OWNERSHIP_REQUEST, sessionKey, encryptedData, encryptedSymCrypto);
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
