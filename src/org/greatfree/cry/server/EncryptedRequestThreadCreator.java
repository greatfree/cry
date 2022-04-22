package org.greatfree.cry.server;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.cry.messege.EncryptedRequestStream;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Request;

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
