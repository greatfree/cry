package edu.greatfree.cry.framework.cluster.message;

import org.greatfree.message.multicast.MulticastMessageType;

import edu.greatfree.cry.messege.multicast.ClusterNotification;

/**
 * 
 * @author libing
 * 
 * 04/27/2022
 *
 */
public class StopRootNotification extends ClusterNotification
{
	private static final long serialVersionUID = 4488048858643901375L;

	public StopRootNotification()
	{
		super(MulticastMessageType.LOCAL_NOTIFICATION, ClusterAppID.STOP_ROOT_NOTIFICATION);
	}

}
