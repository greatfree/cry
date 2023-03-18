package edu.greatfree.cry.cluster.root;

import org.greatfree.concurrency.reactive.RequestQueueCreator;

import edu.greatfree.cry.messege.AsymmetricEncryptedResponse;
import edu.greatfree.cry.messege.AsymmetricPrimitiveRequest;
import edu.greatfree.cry.messege.AsymmetricPrimitiveStream;

/**
 * 
 * @author libing
 * 
 * 05/04/2022
 *
 */
final class AsymmetricPrimitiveRequestThreadCreator implements RequestQueueCreator<AsymmetricPrimitiveRequest, AsymmetricPrimitiveStream, AsymmetricEncryptedResponse, AsymmetricPrimitiveRequestThread>
{

	@Override
	public AsymmetricPrimitiveRequestThread createInstance(int taskSize)
	{
		return new AsymmetricPrimitiveRequestThread(taskSize);
	}

}
