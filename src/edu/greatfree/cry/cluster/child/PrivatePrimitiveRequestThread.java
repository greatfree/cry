package edu.greatfree.cry.cluster.child;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.util.ServerStatus;

import edu.greatfree.cry.exceptions.CheatingException;
import edu.greatfree.cry.exceptions.MachineNotOwnedException;
import edu.greatfree.cry.exceptions.NonPrivateMachineException;
import edu.greatfree.cry.exceptions.NonPublicMachineException;
import edu.greatfree.cry.exceptions.OwnerCheatingException;
import edu.greatfree.cry.exceptions.PublicKeyUnavailableException;
import edu.greatfree.cry.exceptions.SessionMismatchedException;
import edu.greatfree.cry.messege.PrivatePrimitiveRequest;
import edu.greatfree.cry.messege.PrivatePrimitiveStream;
import edu.greatfree.cry.messege.PrivateResponse;
import edu.greatfree.cry.server.ServiceProvider;

/**
 * 
 * @author libing
 * 
 * 05/12/2022
 *
 */
final class PrivatePrimitiveRequestThread extends RequestQueue<PrivatePrimitiveRequest, PrivatePrimitiveStream, PrivateResponse>
{
	public PrivatePrimitiveRequestThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		PrivatePrimitiveStream request;
		PrivateResponse response;
		while (!super.isShutdown())
		{
			while (!super.isEmpty())
			{
				request = super.dequeue();
				try
				{
					response = ServiceProvider.CRY().processClusterChildAsymmetricRequest(super.getServerKey(), request.getMessage());
					super.respond(request.getOutStream(), request.getLock(), response);
					super.disposeMessage(request, response);
				}
				catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
						| InvalidAlgorithmParameterException | ShortBufferException | IllegalBlockSizeException
						| BadPaddingException | ClassNotFoundException | SignatureException | IOException
						| CheatingException | PublicKeyUnavailableException | NonPrivateMachineException
						| SessionMismatchedException | MachineNotOwnedException | OwnerCheatingException | NonPublicMachineException e)
				{
					e.printStackTrace();
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
