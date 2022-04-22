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
import org.greatfree.cry.exceptions.NonPrivateMachineException;
import org.greatfree.cry.exceptions.PublicKeyUnavailableException;
import org.greatfree.cry.exceptions.SessionMismatchedException;
import org.greatfree.cry.messege.OwnershipRequest;
import org.greatfree.cry.messege.OwnershipResponse;
import org.greatfree.cry.messege.OwnershipStream;
import org.greatfree.data.ServerConfig;
import org.greatfree.util.ServerStatus;

/**
 * 
 * @author libing
 * 
 * 04/20/2022
 *
 */
class OwnershipThread extends RequestQueue<OwnershipRequest, OwnershipStream, OwnershipResponse>
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.server");

	public OwnershipThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		OwnershipStream request;
		OwnershipResponse response = null;
		while (!super.isShutdown())
		{
			while (!super.isEmpty())
			{
				request = super.dequeue();
				try
				{
					response = ServiceProvider.CRY().setOwner(super.getServerKey(), request.getMessage());
					super.respond(request.getOutStream(), request.getLock(), response);
				}
				catch (NonPrivateMachineException e)
				{
					log.info("Exception: The machine is public such that the machine cannot be owned!");
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
					ServerStatus.FREE().printException(e);
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
