package org.greatfree.cry.framework.cluster.child;

import java.io.IOException;

import org.greatfree.cry.framework.cluster.ClusterConfig;
import org.greatfree.cry.multicast.MulticastConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.TerminateSignal;

/**
 * 
 * @author libing
 * 
 * 04/26/2022
 *
 */
final class StartChild
{

	public static void main(String[] args) throws ClassNotFoundException, IOException, RemoteReadException, InterruptedException, DistributedNodeFailedException
	{
		System.out.println("Cluster child starting up ...");
		ClusterChild.CRY().start(ClusterConfig.REGISTRY_IP, ClusterConfig.REGISTRY_PORT, new ClusterChildTask(), MulticastConfig.PLAIN, ClusterConfig.ROOT_KEY);
		System.out.println("Cluster child started ...");
		TerminateSignal.SIGNAL().waitTermination();
	}

}
