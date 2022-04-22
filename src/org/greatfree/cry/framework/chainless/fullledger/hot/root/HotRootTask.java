package org.greatfree.cry.framework.chainless.fullledger.hot.root;

import org.greatfree.cluster.RootTask;
import org.greatfree.message.multicast.container.ChildRootRequest;
import org.greatfree.message.multicast.container.ChildRootResponse;
import org.greatfree.message.multicast.container.ClusterNotification;
import org.greatfree.message.multicast.container.ClusterRequest;
import org.greatfree.message.multicast.container.CollectedClusterResponse;

/**
 * 
 * @author libing
 * 
 * 04/06/2022
 *
 */
class HotRootTask implements RootTask
{

	@Override
	public ChildRootResponse processChildRequest(ChildRootRequest request)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void processNotification(ClusterNotification notification)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public CollectedClusterResponse processRequest(ClusterRequest request)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
