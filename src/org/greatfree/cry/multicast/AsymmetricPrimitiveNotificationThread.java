package org.greatfree.cry.multicast;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.cry.exceptions.NonPublicMachineException;
import org.greatfree.cry.exceptions.SessionMismatchedException;
import org.greatfree.cry.messege.AsymmetricPrimitiveNotification;
import org.greatfree.cry.server.ServiceProvider;
import org.greatfree.data.ServerConfig;
import org.greatfree.util.ServerStatus;

/**
 * 
 * @author libing
 * 
 * 04/30/2022
 *
 */
public final class AsymmetricPrimitiveNotificationThread extends NotificationQueue<AsymmetricPrimitiveNotification>
{
	public AsymmetricPrimitiveNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		AsymmetricPrimitiveNotification notification;
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
				catch (InterruptedException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | ShortBufferException | IllegalBlockSizeException | BadPaddingException | ClassNotFoundException | IOException | NonPublicMachineException | SessionMismatchedException e)
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
