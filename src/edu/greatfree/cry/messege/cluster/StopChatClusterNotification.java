package edu.greatfree.cry.messege.cluster;

import org.greatfree.cluster.message.ClusterApplicationID;
import org.greatfree.message.multicast.MulticastMessageType;

import edu.greatfree.cry.messege.multicast.ClusterNotification;

/**
 * 
 * @author libing
 * 
 * 02/04/2023
 *
 */
public class StopChatClusterNotification extends ClusterNotification
{
	private static final long serialVersionUID = -8842083121573762214L;

	public StopChatClusterNotification()
	{
		super(MulticastMessageType.BROADCAST_NOTIFICATION, ClusterApplicationID.STOP_CHAT_CLUSTER_NOTIFICATION);
	}
}
