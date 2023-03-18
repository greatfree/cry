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
public class SignedPrimitiveRequest extends AsymmetricPrimitiveRequest
{
	private static final long serialVersionUID = -1426541570318834923L;

	private String signature;
	private byte[] encryptedSignature;

	public SignedPrimitiveRequest(String sessionKey, byte[] encryptedData, byte[] encryptedSymCrypto, String signature, byte[] encryptedSignature)
	{
		super(CryAppID.SIGNED_PRIMITIVE_REQUEST, sessionKey, encryptedData, encryptedSymCrypto);
		this.signature = signature;
		this.encryptedSignature = encryptedSignature;
	}

	public SignedPrimitiveRequest(int encryptionID, String sessionKey, byte[] encryptedData, byte[] encryptedSymCrypto, String signature, byte[] encryptedSignature)
	{
		super(encryptionID, sessionKey, encryptedData, encryptedSymCrypto);
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
