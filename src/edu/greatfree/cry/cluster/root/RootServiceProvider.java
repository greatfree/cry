package edu.greatfree.cry.cluster.root;

import org.greatfree.message.multicast.container.ChildRootResponse;

import edu.greatfree.cry.cluster.RootTask;
import edu.greatfree.cry.messege.multicast.ChildRootRequest;
import edu.greatfree.cry.messege.multicast.ClusterNotification;
import edu.greatfree.cry.messege.multicast.ClusterRequest;
import edu.greatfree.cry.messege.multicast.CollectedClusterResponse;

/**
 * 
 * @author libing
 * 
 * 04/24/2022
 *
 */
final class RootServiceProvider
{
	private RootTask task;
	
	private RootServiceProvider()
	{
	}
	
	private static RootServiceProvider instance = new RootServiceProvider();
	
	public static RootServiceProvider CRY()
	{
		if (instance == null)
		{
			instance = new RootServiceProvider();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void init(RootTask task)
	{
		this.task = task;
	}
	
	public void processNotification(ClusterNotification notification)
	{
		this.task.processNotification(notification);
	}

	public CollectedClusterResponse processRequest(ClusterRequest request)
	{
		return this.task.processRequest(request);
	}
	
	public ChildRootResponse processChildRequest(ChildRootRequest request)
	{
		return this.task.processChildRequest(request);
	}
}
