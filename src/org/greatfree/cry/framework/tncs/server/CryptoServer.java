package org.greatfree.cry.framework.tncs.server;

import java.io.IOException;

import org.greatfree.cry.server.Server;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.server.container.ServerTask;

/**
 * 
 * @author libing
 * 
 * 01/07/2022, Bing Li
 *
 */
class CryptoServer
{
	private Server server;

	private CryptoServer()
	{
	}
	
	private static CryptoServer instance = new CryptoServer();
	
	public static CryptoServer CRY()
	{
		if (instance == null)
		{
			instance = new CryptoServer();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void stop(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException
	{
		this.server.stop(timeout);
	}

	public void start(int port, ServerTask task) throws IOException, ClassNotFoundException, RemoteReadException
	{
		this.server = new Server(port, task);
		this.server.start();
	}
}
