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

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.cry.exceptions.CheatingException;
import org.greatfree.cry.exceptions.NonPublicMachineException;
import org.greatfree.cry.exceptions.PublicKeyUnavailableException;
import org.greatfree.cry.exceptions.SessionMismatchedException;
import org.greatfree.cry.messege.SignedAsymmetricEncryptedNotification;
import org.greatfree.data.ServerConfig;
import org.greatfree.util.ServerStatus;

/**
 * 
 * @author libing
 * 
 * 04/20/2022
 *
 */
class SignedAsymmetricEncryptedNotificationThread extends NotificationQueue<SignedAsymmetricEncryptedNotification>
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.server");

	public SignedAsymmetricEncryptedNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		SignedAsymmetricEncryptedNotification notification;
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
					catch (CheatingException e)
					{
						log.info("Exception: The machine is not owned by " + e.getSignature());
					}
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
