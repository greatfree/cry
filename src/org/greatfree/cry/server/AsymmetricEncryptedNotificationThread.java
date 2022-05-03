package org.greatfree.cry.server;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.cry.exceptions.NonPublicMachineException;
import org.greatfree.cry.exceptions.SessionMismatchedException;
import org.greatfree.cry.messege.AsymmetricEncryptedNotification;
import org.greatfree.data.ServerConfig;
import org.greatfree.util.ServerStatus;

/**
 * 
 * @author libing
 * 
 * 04/20/2022
 *
 */
class AsymmetricEncryptedNotificationThread extends NotificationQueue<AsymmetricEncryptedNotification>
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.server");

	public AsymmetricEncryptedNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		AsymmetricEncryptedNotification notification;
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
					catch (NonPublicMachineException e)
					{
						log.info("Exception: The machine is private such that the notification cannot be processed!");
					}
					super.disposeMessage(notification);
				}
				catch (InterruptedException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
						| InvalidAlgorithmParameterException | ShortBufferException | IllegalBlockSizeException
						| BadPaddingException | ClassNotFoundException | IOException
						| SessionMismatchedException e)
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
