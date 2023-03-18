package edu.greatfree.cry.framework.peer.server;

import java.io.IOException;

import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.util.TerminateSignal;
import org.greatfree.util.Tools;

import edu.greatfree.cry.framework.ownership.OwnerConfig;

/**
 * 
 * @author libing
 * 
 * 03/02/2023
 *
 */
final class StartServer
{
	public static void main(String[] args) throws IOException, ClassNotFoundException, RemoteReadException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		System.out.println("Tell me your name: ");
		String yourName = Tools.INPUT.nextLine();

		System.out.println("Starting server, " + yourName);
		PeerServer.PEER().start(yourName, OwnerConfig.MACHINE_PORT, OwnerConfig.REGISTRY_IP, OwnerConfig.REGISTRY_PORT, false, 0);
		System.out.println("Server, " + yourName + ", is started ...");
		TerminateSignal.SIGNAL().waitTermination();
		System.out.println("Server, " + yourName + ", is stopped ...");
	}
}
