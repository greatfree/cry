package org.greatfree.cry.framework.cluster.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.ClusterNotification;

/**
 * 
 * @author libing
 * 
 * 04/27/2022
 *
 */
public class StopChildrenNotification extends ClusterNotification
{
	private static final long serialVersionUID = 8205597159425147598L;

	public StopChildrenNotification()
	{
		super(MulticastMessageType.LOCAL_NOTIFICATION, ClusterAppID.STOP_CHILDREN_NOTIFICATION);
	}

}
