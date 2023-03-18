package edu.greatfree.cry.cluster.child;

import org.greatfree.concurrency.reactive.RequestQueueCreator;

import edu.greatfree.cry.messege.EncryptedResponse;
import edu.greatfree.cry.messege.SymmetricPrimitiveRequest;
import edu.greatfree.cry.messege.SymmetricPrimitiveStream;

/**
 * 
 * @author libing
 * 
 * 05/12/2022
 *
 */
final class SymmetricPrimitiveRequestThreadCreator implements RequestQueueCreator<SymmetricPrimitiveRequest, SymmetricPrimitiveStream, EncryptedResponse, SymmetricPrimitiveRequestThread>
{

	@Override
	public SymmetricPrimitiveRequestThread createInstance(int taskSize)
	{
		return new SymmetricPrimitiveRequestThread(taskSize);
	}

}
