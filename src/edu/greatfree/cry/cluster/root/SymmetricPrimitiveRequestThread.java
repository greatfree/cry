package edu.greatfree.cry.cluster.root;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.util.ServerStatus;

import edu.greatfree.cry.exceptions.NonPublicMachineException;
import edu.greatfree.cry.messege.EncryptedResponse;
import edu.greatfree.cry.messege.SymmetricPrimitiveRequest;
import edu.greatfree.cry.messege.SymmetricPrimitiveStream;
import edu.greatfree.cry.server.ServiceProvider;

/**
 * 
 * @author libing
 * 
 * 05/04/2022
 *
 */
final class SymmetricPrimitiveRequestThread extends RequestQueue<SymmetricPrimitiveRequest, SymmetricPrimitiveStream, EncryptedResponse>
{

	public SymmetricPrimitiveRequestThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		SymmetricPrimitiveStream request;
		EncryptedResponse response = null;
		while (!super.isShutdown())
		{
			while (!super.isEmpty())
			{
				request = super.dequeue();
				try
				{
					response = ServiceProvider.CRY().processClusterRootSymmetricRequest(super.getServerKey(), request.getMessage());
				}
				catch (NonPublicMachineException e)
				{
					response = null;
				}
				catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
						| InvalidAlgorithmParameterException | ShortBufferException | IllegalBlockSizeException
						| BadPaddingException | ClassNotFoundException | IOException e)
				{
					e.printStackTrace();
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
