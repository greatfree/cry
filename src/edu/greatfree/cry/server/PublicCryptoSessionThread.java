package edu.greatfree.cry.server;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.util.ServerStatus;

import edu.greatfree.cry.messege.PublicCryptoSessionRequest;
import edu.greatfree.cry.messege.PublicCryptoSessionResponse;
import edu.greatfree.cry.messege.PublicCryptoSessionStream;

/**
 * 
 * @author libing
 * 
 * 04/19/2022
 *
 */
class PublicCryptoSessionThread extends RequestQueue<PublicCryptoSessionRequest, PublicCryptoSessionStream, PublicCryptoSessionResponse>
{

	public PublicCryptoSessionThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		PublicCryptoSessionStream request;
		PublicCryptoSessionResponse response;
		while (!super.isShutdown())
		{
			while (!super.isEmpty())
			{
				request = super.dequeue();
				try
				{
					response = ServiceProvider.CRY().retainPublicCrypto(request.getMessage());
					super.respond(request.getOutStream(), request.getLock(), response);
					super.disposeMessage(request, response);
				}
				catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException | IOException e)
				{
					ServerStatus.FREE().printException(e);
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
