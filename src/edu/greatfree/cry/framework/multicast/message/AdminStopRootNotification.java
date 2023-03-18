package edu.greatfree.cry.framework.multicast.message;

import org.greatfree.message.container.Notification;

/**
 * 
 * @author libing
 * 
 * 04/12/2022
 *
 */
public class AdminStopRootNotification extends Notification
{
	private static final long serialVersionUID = 5188429250170142045L;

	public AdminStopRootNotification()
	{
		super(MultiAppID.ADMIN_STOP_ROOT_NOTIFICATION);
	}

}
