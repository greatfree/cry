package edu.greatfree.cry.framework.cluster.message;

import org.greatfree.cluster.message.ClusterApplicationID;
import org.greatfree.message.multicast.MulticastMessageType;

import edu.greatfree.cry.messege.multicast.ClusterNotification;

/**
 * 
 * @author libing
 * 
 * 05/12/2022
 *
 */
public class StopChatClusterNotification extends ClusterNotification
{
	private static final long serialVersionUID = 1855813194991878136L;

	public StopChatClusterNotification()
	{
		super(MulticastMessageType.BROADCAST_NOTIFICATION, ClusterApplicationID.STOP_CHAT_CLUSTER_NOTIFICATION);
	}

}
