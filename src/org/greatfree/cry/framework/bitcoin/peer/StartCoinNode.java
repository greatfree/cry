package org.greatfree.cry.framework.bitcoin.peer;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Scanner;

import org.greatfree.cry.exceptions.CryptographyMismatchException;
import org.greatfree.cry.framework.blockchain.BlockConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.TerminateSignal;

/**
 * 
 * @author Bing Li
 * 
 * 02/14/2022
 *
 */
class StartCoinNode
{

	public static void main(String[] args) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, SignatureException, IOException, RemoteReadException, DistributedNodeFailedException, CryptographyMismatchException, InterruptedException
	{
		Scanner in = new Scanner(System.in);
		System.out.println("What is the name of the mining-coin machine?");
		String nodeName = in.nextLine();
		System.out.println("Coin node, " + nodeName + ", is starting up ...");
		CoinNode.COIN().start(nodeName, BlockConfig.CHAIN_NODE_PORT, BlockConfig.BC_REGISTRY_IP, BlockConfig.BC_REGISTRY_PORT);
		System.out.println("Coin node, " + nodeName + ", is started ...");
		TerminateSignal.SIGNAL().waitTermination();
		CoinNode.COIN().stop();
		in.close();
		System.out.println("Coin node, " + nodeName + ", is stopped ...");
	}

}
