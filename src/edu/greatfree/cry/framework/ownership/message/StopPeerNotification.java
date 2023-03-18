package edu.greatfree.cry.framework.ownership.message;

import org.greatfree.message.container.Notification;

/**
 * 
 * @author libing
 * 
 * 03/23/2022
 *
 */
public class StopPeerNotification extends Notification
{
	private static final long serialVersionUID = 7830128450579975338L;

	public StopPeerNotification()
	{
		super(OwnerAppID.STOP_PEER_NOTIFICATION);
	}

}
