package edu.greatfree.cry.messege.multicast;

import org.greatfree.cluster.message.ClusterMessageType;
import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.util.IPAddress;

/**
 * 
 * @author libing
 * 
 * 05/11/2022
 *
 */
public class SelectedChildNotification extends ClusterNotification
{
	private static final long serialVersionUID = 7287038200743826102L;

	private String rootKey;
	private IPAddress clusterRootIP;
	private boolean isBusy;

	public SelectedChildNotification(String rootKey, IPAddress ip, boolean isBusy)
	{
		super(MulticastMessageType.BROADCAST_NOTIFICATION, ClusterMessageType.SELECTED_CHILD_NOTIFICATION);
		this.rootKey = rootKey;
		this.clusterRootIP = ip;
		this.isBusy = isBusy;
	}
	
	public String getRootKey()
	{
		return this.rootKey;
	}
	
	public IPAddress getClusterRootIP()
	{
		return this.clusterRootIP;
	}
	
	public boolean isBusy()
	{
		return this.isBusy;
	}

}
