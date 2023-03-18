package edu.greatfree.cry.messege.cluster;

import java.util.HashSet;
import java.util.Set;

import org.greatfree.cluster.message.ClusterMessageType;
import org.greatfree.message.multicast.MulticastMessageType;

/**
 * 
 * IntercastNotification and IntercastRequest should NOT be placed here. I guess. 06/19/2022, Bing Li
 * 
 * Since children are seen as internal nodes of a cluster. Their messages should not be refused by the root. The class retains all the children messages to the root. Thus, the root can differ the children messages from others from clients. 05/18/2022, Bing Li
 * 
 * @author libing
 * 
 * 05/18/2022
 *
 */
public final class ChildrenMessages
{
	private Set<Integer> ids = new HashSet<Integer>();

	private ChildrenMessages()
	{
		this.ids.add(ClusterMessageType.JOIN_NOTIFICATION);
		this.ids.add(ClusterMessageType.LEAVE_NOTIFICATION);
		this.ids.add(ClusterMessageType.HEAVY_WORKLOAD_NOTIFICATION);
		this.ids.add(ClusterMessageType.SUPERFLUOUS_RESOURCES_NOTIFICATION);
		this.ids.add(MulticastMessageType.INTERCAST_NOTIFICATION);
		this.ids.add(ClusterMessageType.CHILD_RESPONSE);
		this.ids.add(ClusterMessageType.PARTITION_SIZE_REQUEST);
		this.ids.add(ClusterMessageType.CLUSTER_SIZE_REQUEST);
		this.ids.add(ClusterMessageType.ADDITIONAL_CHILDREN_REQUEST);
		this.ids.add(MulticastMessageType.INTERCAST_REQUEST);
//		this.ids.add(MulticastMessageType.CLUSTER_NOTIFICATION);
//		this.ids.add(MulticastMessageType.CLUSTER_REQUEST);
		this.ids.add(MulticastMessageType.CHILD_ROOT_REQUEST);
	}
	
	private static ChildrenMessages instance = new ChildrenMessages();
	
	public static ChildrenMessages CRY()
	{
		if (instance == null)
		{
			instance = new ChildrenMessages();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public boolean isClusterMessage(int messageID)
	{
		return this.ids.contains(messageID);
	}
}
