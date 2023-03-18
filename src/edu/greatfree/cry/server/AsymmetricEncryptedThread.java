package edu.greatfree.cry.server;

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
import org.greatfree.data.ServerConfig;
import org.greatfree.util.ServerStatus;

import edu.greatfree.cry.exceptions.NonPublicMachineException;
import edu.greatfree.cry.exceptions.PublicKeyUnavailableException;
import edu.greatfree.cry.exceptions.SessionMismatchedException;
import edu.greatfree.cry.messege.AsymmetricEncryptedRequest;
import edu.greatfree.cry.messege.AsymmetricEncryptedResponse;
import edu.greatfree.cry.messege.AsymmetricEncryptedStream;

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
		AsymmetricEncryptedResponse response = null;
		while (!super.isShutdown())
		{
			while (!super.isEmpty())
			{
				request = super.dequeue();
				try
				{
					response = ServiceProvider.CRY().processAsymmetricRequest(super.getServerKey(), request.getMessage());
				}
				catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
						| InvalidAlgorithmParameterException | ShortBufferException | IllegalBlockSizeException
						| BadPaddingException | ClassNotFoundException | IOException | PublicKeyUnavailableException
						| NonPublicMachineException | SessionMismatchedException e)
				{
					log.info("Exception: The machine is private such that the request cannot be processed!");
					response = null;
				}
				try
				{
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
