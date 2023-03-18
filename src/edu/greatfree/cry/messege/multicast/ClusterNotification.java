package edu.greatfree.cry.messege.multicast;

import java.util.Set;

import org.greatfree.cluster.ClusterConfig;
import org.greatfree.message.multicast.MulticastMessageType;

/**
 * 
 * @author libing
 * 
 * 05/11/2022
 *
 */
public class ClusterNotification extends PrimitiveMulticastNotification
{
	private static final long serialVersionUID = -1437305017456601918L;
	
	private String clientKey;
	private int notificationType;
	private int clusterAppID;
	private int partitionIndex;
	private Set<String> childrenKeys;

	/*
	 * The notification type, MulticastMessageType.UNICAST_NOTIFICATION, is not necessary to be specified since the constructor is unique to represent the case. 10/18/2020, Bing Li
	 * 
	 * The constructor is usually used for the nearest unicasting. So the client key is required for nearest measurement. 10/28/2018, Bing Li 
	 */
	public ClusterNotification(String clientKey, int clusterAppID)
	{
		super(MulticastMessageType.CLUSTER_NOTIFICATION);
		this.clientKey = clientKey;
		// MulticastMessageType.UNICAST_NOTIFICATION
		this.notificationType = MulticastMessageType.UNICAST_NOTIFICATION;
		this.clusterAppID = clusterAppID;
		this.partitionIndex = ClusterConfig.NO_PARTITION_INDEX;
		this.childrenKeys = null;
	}

	/*
	 * The constructor is usually used for the random unicasting. 09/21/2021, Bing Li 
	 */
	public ClusterNotification(int clusterAppID)
	{
		super(MulticastMessageType.CLUSTER_NOTIFICATION);
		this.notificationType = MulticastMessageType.UNICAST_NOTIFICATION;
		this.clusterAppID = clusterAppID;
		this.partitionIndex = ClusterConfig.NO_PARTITION_INDEX;
		this.childrenKeys = null;
	}
	

	/*
	 * The below two constructors are used together for the root and the children, respectively. 10/28/2018, Bing Li
	 */

	/*
	 * This constructor is usually used for normal broadcasting. 10/28/2018, Bing Li
	 */
	public ClusterNotification(int notificationType, int clusterAppID)
	{
		super(MulticastMessageType.CLUSTER_NOTIFICATION);
		this.notificationType = notificationType;
		this.clusterAppID = clusterAppID;
		this.partitionIndex = ClusterConfig.NO_PARTITION_INDEX;
		this.childrenKeys = null;
	}

	/*
	 * Some messages need to be replicated among one partition. If so, the constructor is employed and the partition index should be specified. 09/07/2020, Bing Li
	 */
	public ClusterNotification(int notificationType, int clusterAppID, int partitionIndex)
	{
		super(MulticastMessageType.CLUSTER_NOTIFICATION);
		this.notificationType = notificationType;
		this.clusterAppID = clusterAppID;
		this.partitionIndex = partitionIndex;
		this.childrenKeys = null;
	}
	
	/*
	 * When broadcasting is performed within specified children, the constructor is employed. 09/13/2020, Bing Li
	 */
	public ClusterNotification(int notificationType, int clusterAppID, Set<String> childrenKeys)
	{
		super(MulticastMessageType.CLUSTER_NOTIFICATION);
		this.notificationType = notificationType;
		this.clusterAppID = clusterAppID;
		this.partitionIndex = ClusterConfig.NO_PARTITION_INDEX;
		this.childrenKeys = childrenKeys;
	}

	public String getClientKey()
	{
		return this.clientKey;
	}

	public int getNotificationType()
	{
		return this.notificationType;
	}

	public int getClusterAppID()
	{
		return this.clusterAppID;
	}

	public int getPartitionIndex()
	{
		return this.partitionIndex;
	}
	
	public Set<String> getChildrenKeys()
	{
		return this.childrenKeys;
	}

}
