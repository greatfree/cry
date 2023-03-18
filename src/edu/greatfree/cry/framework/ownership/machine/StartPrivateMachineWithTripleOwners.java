package edu.greatfree.cry.framework.ownership.machine;

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
 * 03/23/2022
 *
 */
class StartPrivateMachineWithTripleOwners
{

	public static void main(String[] args) throws ClassNotFoundException, IOException, RemoteReadException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		System.out.println("What is the name of the machine?");
		String machineName = Tools.INPUT.nextLine();
		System.out.println("Machine name, " + machineName + ", is starting up ...");

		/*
		 * The machine is private, and its owners' size is set to be 3. 03/22/2022, Bing Li
		 */
		Machine.RSC().start(machineName, OwnerConfig.MACHINE_PORT, OwnerConfig.REGISTRY_IP, OwnerConfig.REGISTRY_PORT, true, 3);
		System.out.println("Machine, " + machineName + ", is started ...");
		TerminateSignal.SIGNAL().waitTermination();
		System.out.println("Machine, " + machineName + ", is stopped ...");
	}

}
