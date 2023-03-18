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

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.util.ServerStatus;

import edu.greatfree.cry.exceptions.CheatingException;
import edu.greatfree.cry.exceptions.MachineNotOwnedException;
import edu.greatfree.cry.exceptions.NonPrivateMachineException;
import edu.greatfree.cry.exceptions.OwnerCheatingException;
import edu.greatfree.cry.exceptions.PublicKeyUnavailableException;
import edu.greatfree.cry.exceptions.SessionMismatchedException;
import edu.greatfree.cry.messege.PrivateNotification;

/**
 * 
 * @author libing
 * 
 * 04/20/2022
 *
 */
class PrivateNotificationThread extends NotificationQueue<PrivateNotification>
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.server");

	public PrivateNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		PrivateNotification notification;
		while (!super.isShutdown())
		{
			while (!super.isEmpty())
			{
				try
				{
					notification = super.dequeue();
					try
					{
						ServiceProvider.CRY().processAsymmetricNotification(super.getServerKey(), notification);
					}
					catch (NonPrivateMachineException e)
					{
						log.info("Exception: The machine is public such that the private notification cannot be processed!");
					}
					catch (MachineNotOwnedException e)
					{
						log.info("Exception: The machine is not owned by " + e.getSignature());
					}
					catch (CheatingException e)
					{
						log.info("Exception: The machine is not owned by " + e.getSignature());
					}
					catch (OwnerCheatingException e)
					{
						log.info("Exception: The machine is not owned by " + e.getOwner());
					}
					super.disposeMessage(notification);
				}
				catch (InterruptedException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
						| InvalidAlgorithmParameterException | ShortBufferException | IllegalBlockSizeException
						| BadPaddingException | ClassNotFoundException | SignatureException | IOException
						| PublicKeyUnavailableException | SessionMismatchedException e)
				{
					e.printStackTrace();
				}
				
			}
			try
			{
				super.holdOn(ServerConfig.NOTIFICATION_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				ServerStatus.FREE().printException(e);
			}
		}
	}

}
