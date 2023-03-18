package edu.greatfree.cry.server;

import java.io.IOException;
import java.util.logging.Logger;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.util.ServerStatus;

import edu.greatfree.cry.messege.SymmetricCryptoSessionRequest;
import edu.greatfree.cry.messege.SymmetricCryptoSessionResponse;
import edu.greatfree.cry.messege.SymmetricCryptoSessionStream;

/**
 * 
 * @author libing
 * 
 * 04/19/2022
 *
 */
class SymmetricCryptoSessionThread extends RequestQueue<SymmetricCryptoSessionRequest, SymmetricCryptoSessionStream, SymmetricCryptoSessionResponse>
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.server");

	public SymmetricCryptoSessionThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		SymmetricCryptoSessionStream request;
		SymmetricCryptoSessionResponse response;
		while (!super.isShutdown())
		{
			while (!super.isEmpty())
			{
				request = super.dequeue();
				response = ServiceProvider.CRY().retainSymmetricCrypto(request.getMessage());
				try
				{
					log.info("SymmetricCryptoSessionResponse is responding to client ...");
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
