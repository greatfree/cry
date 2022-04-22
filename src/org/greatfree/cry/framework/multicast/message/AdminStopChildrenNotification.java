package org.greatfree.cry.framework.multicast.message;

import org.greatfree.message.container.Notification;

/**
 * 
 * @author libing
 * 
 * 04/12/2022
 *
 */
public class AdminStopChildrenNotification extends Notification
{
	private static final long serialVersionUID = 7027909658190948163L;

	public AdminStopChildrenNotification()
	{
		super(MultiAppID.ADMIN_STOP_CHILDREN_NOTIFICATION);
	}

}
