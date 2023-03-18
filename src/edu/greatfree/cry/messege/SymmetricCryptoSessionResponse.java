package edu.greatfree.cry.messege;

/**
 * 
 * @author libing
 * 
 * 01/05/2022, Bing Li
 *
 */
// public class CryptoSessionResponse extends ServerMessage
public class SymmetricCryptoSessionResponse extends EncryptedResponse
{
	private static final long serialVersionUID = 3457411124436476711L;
	
	private boolean isDone;

	public SymmetricCryptoSessionResponse(boolean isDone)
	{
		super(CryAppID.SYMMETRIC_CRYPTO_SESSION_RESPONSE);
		this.isDone = isDone;
	}

	public boolean isDone()
	{
		return this.isDone;
	}
}
