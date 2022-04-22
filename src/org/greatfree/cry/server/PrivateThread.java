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
import org.greatfree.cry.exceptions.MachineNotOwnedException;
import org.greatfree.cry.exceptions.NonPrivateMachineException;
import org.greatfree.cry.exceptions.OwnerCheatingException;
import org.greatfree.cry.exceptions.PublicKeyUnavailableException;
import org.greatfree.cry.exceptions.SessionMismatchedException;
import org.greatfree.cry.messege.PrivateRequest;
import org.greatfree.cry.messege.PrivateResponse;
import org.greatfree.cry.messege.PrivateStream;
import org.greatfree.data.ServerConfig;
import org.greatfree.util.ServerStatus;

/**
 * 
 * @author libing
 * 
 * 04/20/2022
 *
 */
class PrivateThread extends RequestQueue<PrivateRequest, PrivateStream, PrivateResponse>
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.server");

	public PrivateThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		PrivateStream request;
		PrivateResponse response = null;
		while (!super.isShutdown())
		{
			while (!super.isEmpty())
			{
				request = super.dequeue();
				try
				{
					response = ServiceProvider.CRY().processAsymmetricRequest(super.getServerKey(), request.getMessage());
					super.respond(request.getOutStream(), request.getLock(), response);
				}
				catch (NonPrivateMachineException e)
				{
					log.info("Exception: The machine is public such that the private request cannot be processed!");
					response = new PrivateResponse(e.getMessage());
				}
				catch (MachineNotOwnedException e)
				{
					log.info("Exception: The machine is not owned by " + e.getSignature());
					response = new PrivateResponse(e.getSignature());
				}
				catch (CheatingException e)
				{
					log.info("Exception: " + e.getSignature() + " is cheating ...");
					response = new PrivateResponse(e.getSignature());
				}
				catch (OwnerCheatingException e)
				{
					log.info("Exception: The machine is not owned by " + e.getOwner());
					response = null;
				}
				catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
						| InvalidAlgorithmParameterException | ShortBufferException | IllegalBlockSizeException
						| BadPaddingException | ClassNotFoundException | SignatureException | IOException
						| PublicKeyUnavailableException | SessionMismatchedException e)
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
