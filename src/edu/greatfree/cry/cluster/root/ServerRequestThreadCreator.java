package edu.greatfree.cry.cluster.root;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.message.ServerMessage;

import edu.greatfree.cry.messege.ServerMessageStream;

/**
 * 
 * @author libing
 * 
 * 05/04/2022
 *
 */
final class ServerRequestThreadCreator implements RequestQueueCreator<ServerMessage, ServerMessageStream, ServerMessage, ServerRequestThread>
{

	@Override
	public ServerRequestThread createInstance(int taskSize)
	{
		return new ServerRequestThread(taskSize);
	}

}
