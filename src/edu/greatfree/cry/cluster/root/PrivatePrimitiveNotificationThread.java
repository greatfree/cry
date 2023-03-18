package edu.greatfree.cry.cluster.root;

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
import edu.greatfree.cry.exceptions.NonPublicMachineException;
import edu.greatfree.cry.exceptions.OwnerCheatingException;
import edu.greatfree.cry.exceptions.PublicKeyUnavailableException;
import edu.greatfree.cry.exceptions.SessionMismatchedException;
import edu.greatfree.cry.messege.PrivatePrimitiveNotification;
import edu.greatfree.cry.server.ServiceProvider;

/**
 * 
 * @author libing
 * 
 * 05/11/2022
 *
 */
final class PrivatePrimitiveNotificationThread extends NotificationQueue<PrivatePrimitiveNotification>
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.cluster.root");

	public PrivatePrimitiveNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		PrivatePrimitiveNotification notification;
		while (!super.isShutdown())
		{
			while (!super.isEmpty())
			{
				try
				{
					notification = super.dequeue();
					ServiceProvider.CRY().processClusterRootAsymmetricNotification(super.getServerKey(), notification);
					super.disposeMessage(notification);
				}
				catch (NonPublicMachineException e)
				{
					log.info("The machine is not public!");
				}
				catch (MachineNotOwnedException e)
				{
					log.info("The machine is not owned by the notification sender!");
				}
				catch (NonPrivateMachineException e)
				{
					log.info("The machine is not private!");
				}
				catch (InterruptedException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | ShortBufferException | IllegalBlockSizeException | BadPaddingException | ClassNotFoundException | SignatureException | IOException | CheatingException | PublicKeyUnavailableException | OwnerCheatingException | SessionMismatchedException e)
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
