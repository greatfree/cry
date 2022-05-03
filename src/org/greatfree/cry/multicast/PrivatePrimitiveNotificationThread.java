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
import org.greatfree.cry.exceptions.MachineNotOwnedException;
import org.greatfree.cry.exceptions.NonPrivateMachineException;
import org.greatfree.cry.exceptions.OwnerCheatingException;
import org.greatfree.cry.exceptions.PublicKeyUnavailableException;
import org.greatfree.cry.exceptions.SessionMismatchedException;
import org.greatfree.cry.messege.PrivatePrimitiveNotification;
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
public final class PrivatePrimitiveNotificationThread extends NotificationQueue<PrivatePrimitiveNotification>
{

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
					ServiceProvider.CRY().processAsymmetricNotification(super.getServerKey(), notification);
					super.disposeMessage(notification);
				}
				catch (InterruptedException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | ShortBufferException | IllegalBlockSizeException | BadPaddingException | ClassNotFoundException | SignatureException | IOException | CheatingException | PublicKeyUnavailableException | OwnerCheatingException | NonPrivateMachineException | SessionMismatchedException | MachineNotOwnedException e)
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
