package edu.greatfree.cry.cluster.root;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.ServerMessage;
import org.greatfree.util.ServerStatus;

import edu.greatfree.cry.exceptions.NonPublicMachineException;
import edu.greatfree.cry.messege.ServerMessageStream;
import edu.greatfree.cry.server.ServiceProvider;

/**
 * 
 * @author libing
 * 
 * 05/04/2022
 *
 */
final class ServerRequestThread extends RequestQueue<ServerMessage, ServerMessageStream, ServerMessage>
{

	public ServerRequestThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		ServerMessageStream request;
		ServerMessage response = null;
		while (!super.isShutdown())
		{
			while (!super.isEmpty())
			{
				request = super.dequeue();
				try
				{
					response = ServiceProvider.CRY().processClusterRootRequest(super.getServerKey(), request.getMessage());
				}
				catch (NonPublicMachineException e)
				{
					response = null;
				}
				try
				{
					super.respond(request.getOutStream(), request.getLock(), response);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				super.disposeMessage(request, response);
			}
			try
			{
				super.holdOn(ServerConfig.REQUEST_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				ServerStatus.FREE().printException(e);
			}
		}
		
	}

}
