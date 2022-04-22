package org.greatfree.cry.framework.multisigned.server;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.cry.framework.ownership.OwnerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.TerminateSignal;

/**
 * 
 * @author libing
 * 
 * 03/26/2022
 *
 */
class StartServerWithTripleOwners
{

	public static void main(String[] args) throws ClassNotFoundException, IOException, RemoteReadException
	{
		Scanner in = new Scanner(System.in);
		System.out.println("What is the name of the machine?");
		String serverName = in.nextLine();
		System.out.println("Server name, " + serverName + ", is starting up ...");
		
		Server.RSC().start(serverName, OwnerConfig.MACHINE_PORT, OwnerConfig.REGISTRY_IP, OwnerConfig.REGISTRY_PORT, true, 3);
		System.out.println("Machine, " + serverName + ", is started ...");
		TerminateSignal.SIGNAL().waitTermination();
		in.close();
		System.out.println("Machine, " + serverName + ", is stopped ...");
	}

}
