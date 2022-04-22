package org.greatfree.cry.framework.bitcoin.message;

import org.greatfree.message.container.Notification;

/**
 * 
 * @author libing
 * 
 * 02/26/2022
 *
 */
public class ValidationResultNotification extends Notification
{
	private static final long serialVersionUID = 180124511302777397L;
	
	private String sessionKey;
	private boolean isValid;

	public ValidationResultNotification(String sessionKey, boolean isValid)
	{
		super(CoinAppID.VALIDATION_RESULT_NOTIFICATION);
		this.sessionKey = sessionKey;
		this.isValid = isValid;
	}

	public String getSessionKey()
	{
		return this.sessionKey;
	}
	
	public boolean isValid()
	{
		return this.isValid;
	}
}
