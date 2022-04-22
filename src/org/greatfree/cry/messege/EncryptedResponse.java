package org.greatfree.cry.messege;

import org.greatfree.message.ServerMessage;

/**
 * 
 * @author libing
 * 
 *         01/04/2022, Bing Li
 *
 */
public class EncryptedResponse extends ServerMessage
{
	private static final long serialVersionUID = -1906365126833655546L;

	// It assumes that the response data is encrypted in the same way as the request. So the below line can be removed. 01/06/2022, Bing Li
//	private String sessionKey;
	private byte[] encryptedData;

//	public EncryptedResponse(String sessionKey, byte[] encryptedData)
//	public EncryptedResponse(byte[] encryptedData)
//	public EncryptedResponse(byte[] encryptedData)
	public EncryptedResponse(int encryptionID, byte[] encryptedData)
	{
		super(encryptionID);
//		this.sessionKey = sessionKey;
		this.encryptedData = encryptedData;
	}
	
//	public EncryptedResponse()
	public EncryptedResponse(int applicationID)
	{
		super(applicationID);
	}

	/*
	public String getSessionKey()
	{
		return this.sessionKey;
	}
	*/
	
	public byte[] getEncryptedData()
	{
		return this.encryptedData;
	}
}
