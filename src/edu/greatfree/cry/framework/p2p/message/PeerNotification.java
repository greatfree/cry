package edu.greatfree.cry.framework.p2p.message;

import org.greatfree.message.container.Notification;

/**
 * 
 * @author libing
 * 
 * 01/11/2022, Bing Li
 *
 */
public class PeerNotification extends Notification
{
	private static final long serialVersionUID = -2148090072088828410L;
	
	private Greetings gts;

	public PeerNotification(Greetings gts)
	{
		super(P2PAppID.PEER_NOTIFICATION);
		this.gts = gts;
	}

	public Greetings getGreetings()
	{
		return this.gts;
	}
}
