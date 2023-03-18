package edu.greatfree.cry.server;

import java.io.IOException;
import java.util.logging.Logger;

import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.server.CSServer;
import org.greatfree.server.container.ServerProfile;
import org.greatfree.server.container.ServerTask;
import org.greatfree.util.TerminateSignal;

/**
 * 
 * @author libing
 * 
 *         01/04/2022, Bing Li
 *
 */
public final class Server
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.server");

	private CSServer<CryptoCSDispatcher> server;

	public Server(int port, ServerTask task) throws IOException
	{
		// To execute the ServiceProvider.CRY(), CSDispatcher needs to be rewrite. 01/05/2022, Bing Li
		CryptoCSDispatcher csd = new CryptoCSDispatcher(ServerConfig.SHARED_THREAD_POOL_SIZE, ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME);

		this.server = new CSServer.CSServerBuilder<CryptoCSDispatcher>().port(port).listenerCount(ServerConfig.LISTENING_THREAD_COUNT).dispatcher(csd).build();

		// Assign the server key to the message dispatchers in the server dispatcher. 03/30/2020, Bing Li
		csd.init();
		ServiceProvider.CRY().init(this.server.getID(), task);
	}

	public Server(ServerTask task, String configXML) throws IOException
	{
		CryptoCSDispatcher csd = new CryptoCSDispatcher(ServerConfig.SHARED_THREAD_POOL_SIZE, ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME);

		ServerProfile.CS().init(configXML);
		this.server = new CSServer.CSServerBuilder<CryptoCSDispatcher>().port(ServerProfile.CS().getPort()).listenerCount(ServerProfile.CS().getListeningThreadCount()).dispatcher(csd).build();

		// Assign the server key to the message dispatchers in the server dispatcher. 03/30/2020, Bing Li
		csd.init();
		ServiceProvider.CRY().init(this.server.getID(), task);
	}

	public void stop(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException
	{
		TerminateSignal.SIGNAL().setTerminated();
		TerminateSignal.SIGNAL().notifyAllTermination();
		this.server.stop(timeout);
		log.info("Server is here ...");
	}

	public void start() throws IOException, ClassNotFoundException, RemoteReadException
	{
		this.server.start();
	}
}
