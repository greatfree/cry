package edu.greatfree.cry.server;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Request;
import org.greatfree.util.ServerStatus;

import edu.greatfree.cry.exceptions.NonPublicMachineException;
import edu.greatfree.cry.messege.CryAppID;
import edu.greatfree.cry.messege.EncryptedRequest;
import edu.greatfree.cry.messege.EncryptedRequestStream;

/**
 * 
 * @author libing
 * 
 *         01/06/2022, Bing Li
 *
 */
// class EncryptedRequestThread extends RequestQueue<EncryptedRequest, EncryptedRequestStream, EncryptedResponse>
class EncryptedRequestThread extends RequestQueue<Request, EncryptedRequestStream, ServerMessage>
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.server");

	public EncryptedRequestThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		EncryptedRequestStream request;
//		EncryptedResponse response = null;
		ServerMessage response = null;
		while (!super.isShutdown())
		{
			while (!super.isEmpty())
			{
				request = super.dequeue();
				try
				{
					/*
					if (request.getMessage().getApplicationID() == CryAppID.SYMMETRIC_CRYPTO_SESSION_REQUEST)
					{
						log.info("The message, " + CryAppID.SYMMETRIC_CRYPTO_SESSION_REQUEST + ", is to be processed ...");
					}
					*/

					/*
					 * 
					 * The ATM should be improved to avoid the possible sequentially running. 04/20/2022, Bing Li
					 * 
					 * 
					 * One typical example for the below problem is that a broadcast request is sent from the client to the root. The root has to wait for all the children's responses. Thus, during the waiting time, any other messages to the root cannot be processed until the waiting time is passed. It is a big bug. 04/19/2022, Bing Li
					 * 
					 * One bug exists in the design as follows. Too many cases are listed here. Meanwhile, those cases are processed sequentially. Thus, if one message of one case cannot be processed fast enough, other cases have to wait for that. It is really unreasonable. 04/19/2022, Bing Li
					 * 
					 */
					switch (request.getMessage().getApplicationID())
					{
						case CryAppID.SYMMETRIC_ENCRYPTED_REQUEST:
							log.info("ENCRYPTED_SYMMETRIC_REQUEST received @" + Calendar.getInstance().getTime());
							try
							{
								response = ServiceProvider.CRY().processSymmetricRequest(super.getServerKey(), (EncryptedRequest)request.getMessage());
							}
							catch (NonPublicMachineException e)
							{
								log.info("Exception: The machine is private such that the request cannot be processed!");
								response = null;
							}
							break;

						default:
							log.info("REQUEST received @" + Calendar.getInstance().getTime());
							log.info("request.getMessage().getApplicationID() = " + request.getMessage().getApplicationID());
							try
							{
								response = ServiceProvider.CRY().processRequest(super.getServerKey(), request.getMessage());
							}
							catch (NonPublicMachineException e)
							{
								log.info("Exception: The machine is private such that the request cannot be processed!");
								response = null;
							}
							break;
					}
					super.respond(request.getOutStream(), request.getLock(), response);
					super.disposeMessage(request, response);
				}
				catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
						| InvalidAlgorithmParameterException | ShortBufferException | IllegalBlockSizeException
						| BadPaddingException | ClassNotFoundException | IOException e)
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
