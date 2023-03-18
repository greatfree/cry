package edu.greatfree.cry.cluster;

import org.greatfree.message.ServerMessage;
import org.greatfree.message.multicast.container.ChildRootResponse;
import org.greatfree.message.multicast.container.IntercastRequest;

import edu.greatfree.cry.messege.multicast.ChildRootRequest;
import edu.greatfree.cry.messege.multicast.ClusterNotification;
import edu.greatfree.cry.messege.multicast.ClusterRequest;
import edu.greatfree.cry.messege.multicast.CollectedClusterResponse;
import edu.greatfree.cry.multicast.MulticastTask;

/**
 * 
 * @author libing
 * 
 * 05/03/2022
 *
 */
// public abstract class ClusterTask
//public interface ClusterTask
public abstract class ClusterTask extends MulticastTask
{
	public abstract void processNotification(ServerMessage notification);
	public abstract ServerMessage processRequest(ServerMessage request);
	public abstract void processNotification(ClusterNotification notification);
	public abstract CollectedClusterResponse processRootRequest(ClusterRequest request);
	public abstract void processChildRequest(ClusterRequest request);
	public abstract ChildRootResponse processRequest(ChildRootRequest request);
	public abstract ServerMessage processRequest(IntercastRequest request);
}
