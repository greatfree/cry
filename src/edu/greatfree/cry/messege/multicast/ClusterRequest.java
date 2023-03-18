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
public abstract class ClusterRequest extends PrimitiveMulticastRequest
{
	private static final long serialVersionUID = 8242169872889093702L;

	
	private String clientKey;
	private int requestType;
	private int clusterAppID;
	private int partitionIndex;
	private Set<String> childrenKeys;

	/*
	 * This constructor is used to initialize a unicast which selecting a child randomly. 04/29/2022, Bing Li
	 */
	public ClusterRequest(int applicationID)
	{
		super(MulticastMessageType.CLUSTER_REQUEST);
		this.clientKey = null;
//		this.requestType = requestType;
		this.requestType = MulticastMessageType.UNICAST_REQUEST;
		this.clusterAppID = applicationID;
		this.partitionIndex = ClusterConfig.NO_PARTITION_INDEX;
		this.childrenKeys = null;
	}

	/*
	 * 
	 * This key is important. Developers can set the value. So they can decide how to balance the load. For example, in the case of S3, all of the encoded data slices for the same encoding block can be sent to a unique child for merging. The client key can be the ID of the encoding block. 07/11/2020, Bing Li
	 * 
	 * The constructor is usually used for the nearest unicasting. So the client key is required for nearest measure. 10/28/2018, Bing Li 
	 */
	public ClusterRequest(String clientKey, int applicationID)
	{
		super(MulticastMessageType.CLUSTER_REQUEST);
		this.clientKey = clientKey;
		this.requestType = MulticastMessageType.UNICAST_REQUEST;
		this.clusterAppID = applicationID;
		this.partitionIndex = ClusterConfig.NO_PARTITION_INDEX;
		this.childrenKeys = null;
	}

	/*
	 * The below two constructors are used together for the root and the children, respectively. 10/28/2018, Bing Li
	 */

	/*
	 * This constructor is usually used for normal broadcasting. 10/28/2018, Bing Li
	 */
	public ClusterRequest(int requestType, int applicationID)
	{
		super(MulticastMessageType.CLUSTER_REQUEST);
		this.requestType = requestType;
		this.clusterAppID = applicationID;
		this.partitionIndex = ClusterConfig.NO_PARTITION_INDEX;
		this.childrenKeys = null;
	}

	/*
	 * This constructor is usually used for broadcasting upon replication. The partition index is required to be specified. 10/28/2018, Bing Li
	 */
	public ClusterRequest(int requestType, int applicationID, int partitionIndex)
	{
		super(MulticastMessageType.CLUSTER_REQUEST);
		this.requestType = requestType;
		this.clusterAppID = applicationID;
		this.partitionIndex = partitionIndex;
		this.childrenKeys = null;
	}

	/*
	 * When broadcasting is performed within specified children, the constructor is employed. 09/13/2020, Bing Li
	 */
	public ClusterRequest(int requestType, int applicationID, Set<String> childrenKeys)
	{
		super(MulticastMessageType.CLUSTER_REQUEST);
		this.requestType = requestType;
		this.clusterAppID = applicationID;
		this.partitionIndex = ClusterConfig.NO_PARTITION_INDEX;
		this.childrenKeys = childrenKeys;
	}

	public String getClientKey()
	{
		return this.clientKey;
	}

	public int getRequestType()
	{
		return this.requestType;
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
