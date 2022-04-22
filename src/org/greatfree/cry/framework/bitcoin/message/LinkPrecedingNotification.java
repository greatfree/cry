package org.greatfree.cry.framework.bitcoin.message;

import org.greatfree.message.container.Notification;

/**
 * 
 * @author Bing Li
 * 
 * 02/16/2022
 *
 */
public class LinkPrecedingNotification extends Notification
{
	private static final long serialVersionUID = -8227119281467667940L;
	
	private String peerName;

	public LinkPrecedingNotification(String pn)
	{
		super(CoinAppID.LINK_PRECEDING_NOTIFICATION);
		this.peerName = pn;
	}

	public String getPeerName()
	{
		return this.peerName;
	}
}
