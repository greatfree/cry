package org.greatfree.cry.framework.bitcoin.message;

import org.greatfree.message.container.Notification;

/**
 * 
 * @author libing
 * 
 * 02/27/2022
 *
 */
public class JoinChainNotification extends Notification
{
	private static final long serialVersionUID = -629430340755100080L;
	
	private String sessionKey;

	public JoinChainNotification(String sessionKey)
	{
		super(CoinAppID.JOIN_CHAIN_NOTIFICATION);
		this.sessionKey = sessionKey;
	}

	public String getSessionKey()
	{
		return this.sessionKey;
	}
}
