package edu.greatfree.cry.cluster.root;

import org.greatfree.concurrency.reactive.RequestQueueCreator;

import edu.greatfree.cry.messege.SignedAsymmetricEncryptedResponse;
import edu.greatfree.cry.messege.SignedPrimitiveRequest;
import edu.greatfree.cry.messege.SignedPrimitiveStream;

/**
 * 
 * @author libing
 * 
 * 05/04/2022
 *
 */
final class SignedPrimitiveRequestThreadCreator implements RequestQueueCreator<SignedPrimitiveRequest, SignedPrimitiveStream, SignedAsymmetricEncryptedResponse, SignedPrimitiveRequestThread>
{

	@Override
	public SignedPrimitiveRequestThread createInstance(int taskSize)
	{
		return new SignedPrimitiveRequestThread(taskSize);
	}

}
