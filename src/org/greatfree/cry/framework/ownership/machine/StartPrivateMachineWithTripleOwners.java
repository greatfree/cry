package org.greatfree.cry.framework.ownership.machine;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.cry.framework.ownership.OwnerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.TerminateSignal;

/**
 * 
 * @author libing
 * 
 * 03/23/2022
 *
 */
class StartPrivateMachineWithTripleOwners
{

	public static void main(String[] args) throws ClassNotFoundException, IOException, RemoteReadException
	{
		Scanner in = new Scanner(System.in);
		System.out.println("What is the name of the machine?");
		String machineName = in.nextLine();
		System.out.println("Machine name, " + machineName + ", is starting up ...");

		/*
		 * The machine is private, and its owners' size is set to be 3. 03/22/2022, Bing Li
		 */
		Machine.RSC().start(machineName, OwnerConfig.MACHINE_PORT, OwnerConfig.REGISTRY_IP, OwnerConfig.REGISTRY_PORT, true, 3);
		System.out.println("Machine, " + machineName + ", is started ...");
		TerminateSignal.SIGNAL().waitTermination();
		in.close();
		System.out.println("Machine, " + machineName + ", is stopped ...");
	}

}
