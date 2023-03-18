package edu.greatfree.cry.cluster.child;

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
 * 05/09/2022
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
		ServerMessage response;
		while (!super.isShutdown())
		{
			while (!super.isEmpty())
			{
				request = super.dequeue();
				try
				{
//					response = ServiceProvider.CRY().processClusterRootRequest(super.getServerKey(), request.getMessage());
					response = ServiceProvider.CRY().processClusterChildRequest(super.getServerKey(), request.getMessage());
					super.respond(request.getOutStream(), request.getLock(), response);
					super.disposeMessage(request, response);
				}
				catch (NonPublicMachineException | IOException e)
				{
					e.printStackTrace();
				}
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
