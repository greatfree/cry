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
class StartPublicMachine
{

	public static void main(String[] args) throws ClassNotFoundException, IOException, RemoteReadException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		System.out.println("What is the name of the machine?");
		String machineName = Tools.INPUT.nextLine();
		System.out.println("Machine name, " + machineName + ", is starting up ...");

		Machine.RSC().start(machineName, OwnerConfig.MACHINE_PORT, OwnerConfig.REGISTRY_IP, OwnerConfig.REGISTRY_PORT, false, 0);
		System.out.println("Machine, " + machineName + ", is started ...");
		TerminateSignal.SIGNAL().waitTermination();
		System.out.println("Machine, " + machineName + ", is stopped ...");
	}

}
