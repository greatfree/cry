package edu.greatfree.cry.messege;

import org.greatfree.message.ServerMessage;

/**
 * 
 * The term, primitive, represents that the message inherits the original parent, i.e., ServerMessage, rather than Request. The reason to do that is due to the fact to process those messages with ServerDispatcher, not the container-based approach. 06/19/2022, Bing Li
 * 
 * @author libing
 * 
 * 05/04/2022
 *
 */
public class SymmetricPrimitiveRequest extends ServerMessage
{
	private static final long serialVersionUID = -6557854926603007495L;
	
	private String sessionKey;
	private byte[] encryptedData;

	public SymmetricPrimitiveRequest(String sessionKey, byte[] encryptedData)
	{
		super(CryAppID.SYMMETRIC_PRIMITIVE_REQUEST);
		this.sessionKey = sessionKey;
		this.encryptedData = encryptedData;
	}

	public SymmetricPrimitiveRequest(int encryptionID, String sessionKey, byte[] encryptedData)
	{
		super(encryptionID);
		this.sessionKey = sessionKey;
		this.encryptedData = encryptedData;
	}

	public String getSessionKey()
	{
		return this.sessionKey;
	}
	
	public byte[] getEncryptedData()
	{
		return this.encryptedData;
	}
}
