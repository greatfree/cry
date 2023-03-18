package edu.greatfree.cry.messege;

/**
 * 
 * @author libing
 * 
 * 05/16/2022
 *
 */
public class AbandonOwnershipResponse extends AsymmetricEncryptedResponse
{
	private static final long serialVersionUID = -5749047849831393085L;

	private String signature;
	private byte[] encryptedSignature;

	public AbandonOwnershipResponse(byte[] allOwners, byte[] encryptedSymCrypto, String signature, byte[] encryptedSignature)
	{
		super(CryAppID.ABANDON_OWNERSHIP_RESPONSE, allOwners, encryptedSymCrypto);
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
