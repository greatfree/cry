package edu.greatfree.cry.framework.multicast.message;

import edu.greatfree.cry.messege.multicast.PrimitiveMulticastNotification;

/**
 * 
 * @author libing
 * 
 * 04/13/2022
 *
 */
// public class StopChildrenNotification extends MulticastNotification
public class StopChildrenNotification extends PrimitiveMulticastNotification
{
	private static final long serialVersionUID = 7027909658190948163L;

	public StopChildrenNotification()
	{
		super(MultiAppID.STOP_CHILDREN_NOTIFICATION);
	}

}
