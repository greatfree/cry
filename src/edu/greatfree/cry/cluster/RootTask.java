package edu.greatfree.cry.cluster;

import org.greatfree.message.multicast.container.ChildRootResponse;

import edu.greatfree.cry.messege.multicast.ChildRootRequest;
import edu.greatfree.cry.messege.multicast.ClusterNotification;
import edu.greatfree.cry.messege.multicast.ClusterRequest;
import edu.greatfree.cry.messege.multicast.CollectedClusterResponse;

/**
 * 
 * @author libing
 *
 */
public interface RootTask
{
	public void processNotification(ClusterNotification notification);

	/*
	 * The method is rarely used. But it is reasonable to keep it since it is possible that a client sends a request to the root only. 09/29/2022, Bing Li
	 */
	public CollectedClusterResponse processRequest(ClusterRequest request);
	public ChildRootResponse processChildRequest(ChildRootRequest request);
}
