package org.greatfree.cry.framework.multicast.child;

import java.io.IOException;

import org.greatfree.cry.framework.multicast.MultiAppConfig;
import org.greatfree.cry.multicast.MulticastConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.TerminateSignal;

/**
 * 
 * @author libing
 * 
 * 04/10/2022
 *
 */
final class StartChild
{

	public static void main(String[] args) throws ClassNotFoundException, IOException, RemoteReadException, DistributedNodeFailedException
	{
		System.out.println("Multicasting child is starting ...");
//		ChildPeer.CHILD().start(MultiAppConfig.ROOT_KEY, MultiAppConfig.REGISTRY_SERVER_IP, MultiAppConfig.REGISTRY_SERVER_PORT, MulticastConfig.ASYM);
		ChildPeer.CHILD().start(MultiAppConfig.ROOT_KEY, MultiAppConfig.REGISTRY_SERVER_IP, MultiAppConfig.REGISTRY_SERVER_PORT, MulticastConfig.PLAIN);
		System.out.println("Multicasting child is started ...");
		TerminateSignal.SIGNAL().waitTermination();
	}

}
