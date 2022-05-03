package org.greatfree.cry.multicast;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.cry.exceptions.CheatingException;
import org.greatfree.cry.exceptions.NonPublicMachineException;
import org.greatfree.cry.exceptions.PublicKeyUnavailableException;
import org.greatfree.cry.exceptions.SessionMismatchedException;
import org.greatfree.cry.messege.SignedPrimitiveNotification;
import org.greatfree.cry.server.ServiceProvider;
import org.greatfree.data.ServerConfig;
import org.greatfree.util.ServerStatus;

/**
 * 
 * @author libing
 * 
 * 05/01/2022
 *
 */
public final class SignedPrimitiveNotificationThread extends NotificationQueue<SignedPrimitiveNotification>
{
	public SignedPrimitiveNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		SignedPrimitiveNotification notification;
		while (!super.isShutdown())
		{
			while (!super.isEmpty())
			{
				try
				{
					notification = super.dequeue();
					ServiceProvider.CRY().processAsymmetricNotification(super.getServerKey(), notification);
					super.disposeMessage(notification);
				}
				catch (InterruptedException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | ShortBufferException | IllegalBlockSizeException | BadPaddingException | ClassNotFoundException | IOException | NonPublicMachineException | SignatureException | CheatingException | PublicKeyUnavailableException | SessionMismatchedException e)
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
