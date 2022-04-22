package org.greatfree.cry.framework.chainless.fullledger.hot.root;

import java.io.IOException;

import org.greatfree.cluster.RootTask;
import org.greatfree.cluster.root.container.ClusterServerContainer;
import org.greatfree.cry.framework.chainless.fullledger.hot.HotConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.TerminateSignal;

/**
 * 
 * @author libing
 * 
 * 04/06/2022
 *
 */
class HotServer
{
	private ClusterServerContainer server;

	private HotServer()
	{
	}
	
	private static HotServer instance = new HotServer();
	
	public static HotServer FULL()
	{
		if (instance == null)
		{
			instance = new HotServer();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void stopCluster() throws IOException, DistributedNodeFailedException
	{
		this.server.stopCluster();
	}

	public void stopServer(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException
	{
		TerminateSignal.SIGNAL().notifyAllTermination();
		this.server.stop(timeout);
	}
	
	public void start(int port, RootTask task) throws IOException, ClassNotFoundException, RemoteReadException, DistributedNodeFailedException
	{
		this.server = new ClusterServerContainer(port, HotConfig.FULL_HOT_SERVER_NAME, task);
		this.server.start();
	}
}
