package org.greatfree.cry.server;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.cry.exceptions.CheatingException;
import org.greatfree.cry.exceptions.NonPublicMachineException;
import org.greatfree.cry.exceptions.PublicKeyUnavailableException;
import org.greatfree.cry.exceptions.SessionMismatchedException;
import org.greatfree.cry.messege.SignedAsymmetricEncryptedRequest;
import org.greatfree.cry.messege.SignedAsymmetricEncryptedResponse;
import org.greatfree.cry.messege.SignedAsymmetricEncryptedStream;
import org.greatfree.data.ServerConfig;
import org.greatfree.util.ServerStatus;

/**
 * 
 * @author libing
 * 
 * 04/19/2022
 *
 */
class SignedAsymmetricEncryptedThread extends RequestQueue<SignedAsymmetricEncryptedRequest, SignedAsymmetricEncryptedStream, SignedAsymmetricEncryptedResponse>
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.server");

	public SignedAsymmetricEncryptedThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		SignedAsymmetricEncryptedStream request;
		SignedAsymmetricEncryptedResponse response;
		while (!super.isShutdown())
		{
			while (!super.isEmpty())
			{
				request = super.dequeue();
				try
				{
					response = ServiceProvider.CRY().processAsymmetricRequest(super.getServerKey(), request.getMessage());
				}
				catch (NonPublicMachineException e)
				{
					log.info("Exception: The machine is private such that the request cannot be processed!");
					response = null;
				}
				catch (CheatingException e)
				{
					log.info("Exception: " + e.getSignature() + " is cheating ...");
					response = null;
				}
				catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
						| InvalidAlgorithmParameterException | ShortBufferException | IllegalBlockSizeException
						| BadPaddingException | ClassNotFoundException | SignatureException | IOException
						| PublicKeyUnavailableException | SessionMismatchedException e)
				{
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
