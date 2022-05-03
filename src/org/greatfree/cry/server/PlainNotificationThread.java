package org.greatfree.cry.server;

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

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.cry.exceptions.NonPublicMachineException;
import org.greatfree.cry.exceptions.SessionMismatchedException;
import org.greatfree.cry.messege.CryAppID;
import org.greatfree.cry.messege.SayAsymmetricByeNotification;
import org.greatfree.cry.messege.SaySymmetricByeNotification;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.container.Notification;
import org.greatfree.util.ServerStatus;

/**
 * 
 * @author libing
 * 
 * 01/06/2022, Bing Li
 * 
 * This code conforms to the patterns to the NQ. But the message is updated to EncryptedNotification. 01/06/2022, Bing Li
 *
 */
// class EncryptedNotificationThread extends NotificationQueue<EncryptedNotification>
class PlainNotificationThread extends NotificationQueue<Notification>
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.server");

	public PlainNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
//		EncryptedNotification notification;
		Notification notification;
		while (!super.isShutdown())
		{
			while (!super.isEmpty())
			{
				try
				{
					notification = super.dequeue();
					switch (notification.getApplicationID())
					{
						/*
						case CryAppID.SYMMETRIC_ENCRYPTED_NOTIFICATION:
							log.info("SYMMETRIC_ENCRYPTED_NOTIFICATION received @" + Calendar.getInstance().getTime());
							try
							{
								ServiceProvider.CRY().processSymmetricNotification(super.getServerKey(), (EncryptedNotification)notification);
							}
							catch (NonPublicMachineException e)
							{
								log.info("Exception: The machine is private such that the notification cannot be processed!");
							}
							break;

						case CryAppID.ASYMMETRIC_ENCRYPTED_NOTIFICATION:
							log.info("ASYMMETRIC_ENCRYPTED_NOTIFICATION received @" + Calendar.getInstance().getTime());
							try
							{
								ServiceProvider.CRY().processAsymmetricNotification(super.getServerKey(), (AsymmetricEncryptedNotification)notification);
							}
							catch (NonPublicMachineException e)
							{
								log.info("Exception: The machine is private such that the notification cannot be processed!");
							}
							break;
							
						case CryAppID.SIGNED_ASYMMETRIC_ENCRYPTED_NOTIFICATION:
							log.info("SIGNED_ASYMMETRIC_ENCRYPTED_NOTIFICATION received @" + Calendar.getInstance().getTime());
							try
							{
								ServiceProvider.CRY().processAsymmetricNotification(super.getServerKey(), (SignedAsymmetricEncryptedNotification)notification);
							}
							catch (NonPublicMachineException e)
							{
								log.info("Exception: The machine is private such that the notification cannot be processed!");
							}
							catch (CheatingException e)
							{
								log.info("Exception: The machine is not owned by " + e.getSignature());
							}
							break;
							
						case CryAppID.PRIVATE_NOTIFICATION:
							log.info("PRIVATE_NOTIFICATION received @" + Calendar.getInstance().getTime());
							try
							{
								ServiceProvider.CRY().processAsymmetricNotification(super.getServerKey(), (PrivateNotification)notification);
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
							break;
							*/
							
						case CryAppID.SAY_ASYMMETRIC_BYE_NOTIFICATION:
							log.info("SAY_ASYMMETRIC_BYE_NOTIFICATION received @" + Calendar.getInstance().getTime());
//							ServiceProvider.CRY().removeAsymPartner((SayAsymmetricByeNotification)notification);
							ServiceProvider.CRY().removeAsymPartner(super.getServerKey(), (SayAsymmetricByeNotification)notification);
							break;
							
						case CryAppID.SAY_SYMMETRIC_BYE_NOTIFICATION:
							log.info("SAY_SYMMETRIC_BYE_NOTIFICATION received @" + Calendar.getInstance().getTime());
							ServiceProvider.CRY().removeSymPartner((SaySymmetricByeNotification)notification);
							break;
							
						default:
							log.info("NOTIFICATION received @" + Calendar.getInstance().getTime());
							try
							{
								ServiceProvider.CRY().processNotification(super.getServerKey(), notification);
							}
							catch (NonPublicMachineException e)
							{
								log.info("Exception: The machine is private such that the notification cannot be processed!");
							}
							break;
					}
					super.disposeMessage(notification);
				}
				catch (InterruptedException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | ShortBufferException | IllegalBlockSizeException | BadPaddingException | ClassNotFoundException | IOException | SessionMismatchedException e)
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
