package org.greatfree.cry.messege;

import org.greatfree.message.container.Request;

/**
 * 
 * @author libing
 * 
 *         01/04/2022, Bing Li
 *
 */
public class EncryptedRequest extends Request
{
	private static final long serialVersionUID = 7634957511261296349L;

	private String sessionKey;
	private byte[] encryptedData;

//	public EncryptedRequest(String sessionKey, byte[] encryptedData)
//	public EncryptedRequest(String sessionKey, byte[] encryptedData)
	public EncryptedRequest(int encryptionID, String sessionKey, byte[] encryptedData)
	{
		super(encryptionID);
		this.sessionKey = sessionKey;
		this.encryptedData = encryptedData;
	}
	
//	public EncryptedRequest()
	public EncryptedRequest(int applicationID)
	{
		super(applicationID);
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
