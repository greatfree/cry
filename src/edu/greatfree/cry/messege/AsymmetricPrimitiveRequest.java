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
public class AsymmetricPrimitiveRequest extends SymmetricPrimitiveRequest
{
	private static final long serialVersionUID = -8878451348460167766L;

	private byte[] encryptedSymCrypto;

	public AsymmetricPrimitiveRequest(String sessionKey, byte[] encryptedData, byte[] encryptedSymCrypto)
	{
		super(CryAppID.ASYMMETRIC_PRIMITIVE_REQUEST, sessionKey, encryptedData);
		this.encryptedSymCrypto = encryptedSymCrypto;
	}

	public AsymmetricPrimitiveRequest(int encryptionID, String sessionKey, byte[] encryptedData, byte[] encryptedSymCrypto)
	{
		super(encryptionID, sessionKey, encryptedData);
		this.encryptedSymCrypto = encryptedSymCrypto;
	}

	public byte[] getEncryptedSymCrypto()
	{
		return this.encryptedSymCrypto;
	}
}