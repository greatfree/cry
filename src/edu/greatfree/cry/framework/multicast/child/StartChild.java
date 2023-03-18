package edu.greatfree.cry.framework.multicast.child;

import java.io.IOException;

import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.util.TerminateSignal;

import edu.greatfree.cry.framework.multicast.MultiAppConfig;
import edu.greatfree.cry.multicast.MulticastConfig;

/**
 * 
 * @author libing
 * 
 * 04/10/2022
 *
 */
final class StartChild
{

	public static void main(String[] args) throws ClassNotFoundException, IOException, RemoteReadException, DistributedNodeFailedException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		System.out.println("Multicasting child is starting ...");
//		ChildPeer.CHILD().start(MultiAppConfig.ROOT_KEY, MultiAppConfig.REGISTRY_SERVER_IP, MultiAppConfig.REGISTRY_SERVER_PORT, MulticastConfig.ASYM);
		ChildPeer.CHILD().start(MultiAppConfig.ROOT_KEY, MultiAppConfig.REGISTRY_SERVER_IP, MultiAppConfig.REGISTRY_SERVER_PORT, MulticastConfig.PLAIN);
		System.out.println("Multicasting child is started ...");
		TerminateSignal.SIGNAL().waitTermination();
	}

}
