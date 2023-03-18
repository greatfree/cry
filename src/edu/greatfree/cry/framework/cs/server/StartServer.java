package edu.greatfree.cry.framework.cs.server;

import java.io.IOException;

import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.TerminateSignal;

import edu.greatfree.cry.framework.cs.Config;

/**
 * 
 * @author libing
 * 
 * 01/07/2022, Bing Li
 *
 */
class StartServer
{

	public static void main(String[] args) throws ClassNotFoundException, IOException, RemoteReadException
	{
		System.out.println("Crypto server starting up ...");
		CryptoServer.CRY().start(Config.CRYPTO_SERVER_PORT, new CSTask());
		System.out.println("Crypto server started ...");
		TerminateSignal.SIGNAL().waitTermination();
		System.out.println("Crypto server stopped ...");
	}

}
