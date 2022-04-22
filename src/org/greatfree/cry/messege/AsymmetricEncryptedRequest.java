package org.greatfree.cry.messege;

/**
 * 
 * @author libing
 * 
 * 01/12/2022, Bing Li
 *
 */
public class AsymmetricEncryptedRequest extends EncryptedRequest
{
	private static final long serialVersionUID = -8529181565802171597L;
	
	private byte[] encryptedSymCrypto;

	public AsymmetricEncryptedRequest(String sessionKey, byte[] encryptedData, byte[] encryptedSymCrypto)
	{
		super(CryAppID.ASYMMETRIC_ENCRYPTED_REQUEST, sessionKey, encryptedData);
		this.encryptedSymCrypto = encryptedSymCrypto;
	}

	public AsymmetricEncryptedRequest(int encryptionID, String sessionKey, byte[] encryptedData, byte[] encryptedSymCrypto)
	{
		super(encryptionID, sessionKey, encryptedData);
		this.encryptedSymCrypto = encryptedSymCrypto;
	}

	public byte[] getEncryptedSymCrypto()
	{
		return this.encryptedSymCrypto;
	}
}
