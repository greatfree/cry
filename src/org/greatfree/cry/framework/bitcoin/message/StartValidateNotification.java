package org.greatfree.cry.framework.bitcoin.message;

import org.greatfree.message.container.Notification;

/**
 * 
 * @author Bing Li
 * 
 * 02/15/2022
 *
 */
public class StartValidateNotification extends Notification
{
	private static final long serialVersionUID = -7965367230387935255L;
	
	private String sessionKey;
	private int chainLength;

	public StartValidateNotification(String sessionKey, int chainLength)
	{
		super(CoinAppID.START_VALIDATE_NOTIFICATION);
		this.sessionKey = sessionKey;
		this.chainLength = chainLength;
	}

	public String getSessionKey()
	{
		return this.sessionKey;
	}
	
	public int getChainLength()
	{
		return this.chainLength;
	}
}
