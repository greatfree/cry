package edu.greatfree.cry.cluster;

import org.greatfree.message.multicast.container.IntercastNotification;
import org.greatfree.message.multicast.container.IntercastRequest;

import edu.greatfree.cry.messege.multicast.ClusterNotification;
import edu.greatfree.cry.messege.multicast.ClusterRequest;
import edu.greatfree.cry.messege.multicast.CollectedClusterResponse;
import edu.greatfree.cry.messege.multicast.InterChildrenNotification;
import edu.greatfree.cry.messege.multicast.InterChildrenRequest;
import edu.greatfree.cry.messege.multicast.PrimitiveMulticastResponse;

/**
 * 
 * @author libing
 * 
 * 05/11/2022
 *
 */
public interface ChildTask
{
	public void processNotification(ClusterNotification notification);
	public PrimitiveMulticastResponse processRequest(ClusterRequest request);

	public InterChildrenNotification prepareNotification(IntercastNotification notification);
	public InterChildrenRequest prepareRequest(IntercastRequest request);
	
	public PrimitiveMulticastResponse processRequest(InterChildrenRequest request);
	public void processResponse(CollectedClusterResponse response);

}
