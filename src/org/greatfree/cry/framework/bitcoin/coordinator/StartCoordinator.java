package org.greatfree.cry.framework.bitcoin.coordinator;

import java.io.IOException;

import org.greatfree.cry.framework.bitcoin.CoinConfig;
import org.greatfree.cry.framework.blockchain.BlockConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.TerminateSignal;

/**
 * 
 * @author Bing Li
 * 
 * 02/14/2022
 *
 */
class StartCoordinator
{

	public static void main(String[] args) throws ClassNotFoundException, IOException, RemoteReadException
	{
		System.out.println("Coordinator is starting up ...");
		Coordinator.COIN().start(CoinConfig.COIN_COORDINATOR_PORT, BlockConfig.BC_REGISTRY_IP, BlockConfig.BC_REGISTRY_PORT);
		System.out.println("Coordinator is started ...");
		TerminateSignal.SIGNAL().waitTermination();
		System.out.println("Coordinator is stopped ...");
	}

}
