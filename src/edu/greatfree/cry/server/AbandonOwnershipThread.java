package edu.greatfree.cry.server;

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
import org.greatfree.data.ServerConfig;
import org.greatfree.util.ServerStatus;

import edu.greatfree.cry.exceptions.CheatingException;
import edu.greatfree.cry.exceptions.NonPrivateMachineException;
import edu.greatfree.cry.exceptions.PublicKeyUnavailableException;
import edu.greatfree.cry.exceptions.SessionMismatchedException;
import edu.greatfree.cry.messege.AbandonOwnershipRequest;
import edu.greatfree.cry.messege.AbandonOwnershipResponse;
import edu.greatfree.cry.messege.AbandonOwnershipStream;

/**
 * 
 * @author libing
 * 
 * 05/16/2022
 *
 */
final class AbandonOwnershipThread extends RequestQueue<AbandonOwnershipRequest, AbandonOwnershipStream, AbandonOwnershipResponse>
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.server");

	public AbandonOwnershipThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		AbandonOwnershipStream request;
		AbandonOwnershipResponse response = null;
		while (!super.isShutdown())
		{
			while (!super.isEmpty())
			{
				request = super.dequeue();
				try
				{
					response = ServiceProvider.CRY().abandonOwner(super.getServerKey(), request.getMessage());
				}
				catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
						| IllegalBlockSizeException | BadPaddingException | ClassNotFoundException | SignatureException
						| InvalidAlgorithmParameterException | ShortBufferException | IOException | PublicKeyUnavailableException | SessionMismatchedException e)
				{
					ServerStatus.FREE().printException(e);
					response = null;
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
