package edu.greatfree.cry.server;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Request;

import edu.greatfree.cry.messege.EncryptedRequestStream;

/**
 * 
 * @author libing
 * 
 * 01/06/2022, Bing Li
 *
 */
// class EncryptedRequestThreadCreator implements RequestQueueCreator<EncryptedRequest, EncryptedRequestStream, EncryptedResponse, EncryptedRequestThread>
class EncryptedRequestThreadCreator implements RequestQueueCreator<Request, EncryptedRequestStream, ServerMessage, EncryptedRequestThread>
{

	@Override
	public EncryptedRequestThread createInstance(int taskSize)
	{
		return new EncryptedRequestThread(taskSize);
	}

}
