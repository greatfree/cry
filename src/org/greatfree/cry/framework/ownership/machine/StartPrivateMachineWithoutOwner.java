package org.greatfree.cry.framework.ownership.machine;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.cry.framework.ownership.OwnerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.TerminateSignal;

/**
 * 
 * The test case starts up one private machine. A client that is not the owner attempts to access it. 03/22/2022, Bing Li 
 * 
 * @author libing
 * 
 * 03/19/2022
 *
 */
class StartPrivateMachineWithoutOwner
{

	public static void main(String[] args) throws ClassNotFoundException, IOException, RemoteReadException, InterruptedException
	{
		Scanner in = new Scanner(System.in);
		System.out.println("What is the name of the machine?");
		String machineName = in.nextLine();
		System.out.println("Machine name, " + machineName + ", is starting up ...");

		/*
		 * The machine is private, but its owners' size is set to be zero. 03/22/2022, Bing Li
		 */
		Machine.RSC().start(machineName, OwnerConfig.MACHINE_PORT, OwnerConfig.REGISTRY_IP, OwnerConfig.REGISTRY_PORT, true, 0);
		System.out.println("Machine, " + machineName + ", is started ...");
		TerminateSignal.SIGNAL().waitTermination();
//		Machine.RSC().stop();
		in.close();
		System.out.println("Machine, " + machineName + ", is stopped ...");
	}

}
