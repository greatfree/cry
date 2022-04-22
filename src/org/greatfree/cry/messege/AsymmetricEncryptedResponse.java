package org.greatfree.cry.messege;

/**
 * 
 * @author libing
 * 
 * 01/12/2022, Bing Li
 *
 */
public class AsymmetricEncryptedResponse extends EncryptedResponse
{
	private static final long serialVersionUID = 4275427675190367454L;

	private byte[] encryptedSymCrypto;

	public AsymmetricEncryptedResponse(byte[] encryptedData, byte[] encryptedSymCrypto)
	{
		super(CryAppID.ASYMMETRIC_ENCRYPTED_RESPONSE, encryptedData);
		this.encryptedSymCrypto = encryptedSymCrypto;
	}

	public AsymmetricEncryptedResponse(int encryptionID, byte[] encryptedData, byte[] encryptedSymCrypto)
	{
		super(encryptionID, encryptedData);
		this.encryptedSymCrypto = encryptedSymCrypto;
	}

	public byte[] getEncryptedSymCrypto()
	{
		return this.encryptedSymCrypto;
	}
}
