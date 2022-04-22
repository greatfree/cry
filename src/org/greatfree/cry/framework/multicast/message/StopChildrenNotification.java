package org.greatfree.cry.framework.multicast.message;

import org.greatfree.message.multicast.MulticastNotification;

/**
 * 
 * @author libing
 * 
 * 04/13/2022
 *
 */
public class StopChildrenNotification extends MulticastNotification
{
	private static final long serialVersionUID = 7027909658190948163L;

	public StopChildrenNotification()
	{
		super(MultiAppID.STOP_CHILDREN_NOTIFICATION);
	}

}
