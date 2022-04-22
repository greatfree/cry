package org.greatfree.cry.framework.bitcoin.message;

import org.greatfree.message.container.Notification;

/**
 * 
 * @author libing
 * 
 * 02/26/2022
 *
 */
public class GoAheadValidationNotification extends Notification
{
	private static final long serialVersionUID = -8002684181974256794L;
	
	private String sessionKey;

	public GoAheadValidationNotification(String sessionKey)
	{
		super(CoinAppID.GO_AHEAD_VALIDATION_NOTIFICATION);
		this.sessionKey = sessionKey;
	}

	public String getSessionKey()
	{
		return this.sessionKey;
	}
}
