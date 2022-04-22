package org.greatfree.cry.framework.bitcoin.message;

import org.greatfree.message.container.Notification;

/**
 * 
 * @author Bing Li
 * 
 * 02/16/2022
 *
 */
public class JoinDoneNotification extends Notification
{
	private static final long serialVersionUID = 115234452381443810L;

	private String sessionKey;
	private String peerName;

	public JoinDoneNotification(String sessionKey, String peerName)
	{
		super(CoinAppID.JOIN_DONE_NOTIFICATION);
		this.sessionKey = sessionKey;
		this.peerName = peerName;
	}
	
	public String getSessionKey()
	{
		return this.sessionKey;
	}

	public String getPeerName()
	{
		return this.peerName;
	}
}
