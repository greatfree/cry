package org.greatfree.cry.framework.blockchain.message;

import org.greatfree.message.container.Notification;

/**
 * 
 * @author libing
 * 
 * 01/26/2022, Bing Li
 *
 */
public class SucceedingPeerNotification extends Notification
{
	private static final long serialVersionUID = -2165728004191232703L;
	
//	private IPAddress ip;
	private String peerName;

//	public SucceedingPeerNotification(IPAddress ip)
	public SucceedingPeerNotification(String peerName)
	{
		super(ChainAppID.SUCCEEDING_PEER_NOTIFICATION);
//		this.ip = ip;
		this.peerName = peerName;
	}

	/*
	public IPAddress getSucceedingIP()
	{
		return this.ip;
	}
	*/
	
	public String getPeerName()
	{
		return this.peerName;
	}
}
