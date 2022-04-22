package org.greatfree.cry.framework.blockchain.message;

import org.greatfree.message.container.Notification;

/**
 * 
 * @author libing
 * 
 * 01/26/2022, Bing Li
 *
 */
public class StopCoordinatorNotification extends Notification
{
	private static final long serialVersionUID = -534474182074628073L;

	public StopCoordinatorNotification()
	{
		super(ChainAppID.STOP_COORDINATOR_NOTIFICATION);
	}

}
