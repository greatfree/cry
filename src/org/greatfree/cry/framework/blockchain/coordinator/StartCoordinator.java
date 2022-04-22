package org.greatfree.cry.framework.blockchain.coordinator;

import java.io.IOException;

import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.TerminateSignal;

/**
 * 
 * @author libing
 * 
 * 01/26/2022, Bing Li
 *
 */
class StartCoordinator
{

	public static void main(String[] args) throws ClassNotFoundException, IOException, RemoteReadException
	{
		System.out.println("Coordinator is starting up ...");
		Coordinator.CHAIN().start();
		System.out.println("Coordinator is started ...");
		TerminateSignal.SIGNAL().waitTermination();
		System.out.println("Coordinator is stopped ...");
	}

}
