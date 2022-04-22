package org.greatfree.cry.framework.bitcoin.message;

import org.greatfree.message.container.Notification;

/**
 * 
 * @author libing
 * 
 * 02/27/2022
 *
 */
public class JoinStateNotification extends Notification
{
	private static final long serialVersionUID = -167217255799612500L;

	private String peerName;
	private String sessionKey;
	private boolean isDone;

	public JoinStateNotification(String peerName, String sessionKey, boolean isDone)
	{
		super(CoinAppID.JOIN_STATE_NOTIFICATION);
		this.peerName = peerName;
		this.sessionKey = sessionKey;
		this.isDone = isDone;
	}
	
	public String getPeerName()
	{
		return this.peerName;
	}

	public String getSessionKey()
	{
		return this.sessionKey;
	}
	
	public boolean isDone()
	{
		return this.isDone;
	}
}
