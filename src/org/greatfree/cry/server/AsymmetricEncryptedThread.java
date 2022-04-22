package org.greatfree.cry.server;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.cry.exceptions.NonPublicMachineException;
import org.greatfree.cry.exceptions.PublicKeyUnavailableException;
import org.greatfree.cry.exceptions.SessionMismatchedException;
import org.greatfree.cry.messege.AsymmetricEncryptedRequest;
import org.greatfree.cry.messege.AsymmetricEncryptedResponse;
import org.greatfree.cry.messege.AsymmetricEncryptedStream;
import org.greatfree.data.ServerConfig;
import org.greatfree.util.ServerStatus;

/**
 * 
 * @author libing
 * 
 * 04/19/2022
 *
 */
class AsymmetricEncryptedThread extends RequestQueue<AsymmetricEncryptedRequest, AsymmetricEncryptedStream, AsymmetricEncryptedResponse>
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.server");

	public AsymmetricEncryptedThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		AsymmetricEncryptedStream request;
		AsymmetricEncryptedResponse response;
		while (!super.isShutdown())
		{
			while (!super.isEmpty())
			{
				request = super.dequeue();
				try
				{
					response = ServiceProvider.CRY().processAsymmetricRequest(super.getServerKey(), request.getMessage());
					super.respond(request.getOutStream(), request.getLock(), response);
					super.disposeMessage(request, response);
				}
				catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
						| InvalidAlgorithmParameterException | ShortBufferException | IllegalBlockSizeException
						| BadPaddingException | ClassNotFoundException | IOException | PublicKeyUnavailableException
						| NonPublicMachineException | SessionMismatchedException e)
				{
					log.info("Exception: The machine is private such that the request cannot be processed!");
					response = null;
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
