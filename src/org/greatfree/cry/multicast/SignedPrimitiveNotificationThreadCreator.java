package org.greatfree.cry.multicast;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.cry.messege.SignedPrimitiveNotification;

/**
 * 
 * @author libing
 * 
 * 05/01/2022
 *
 */
public final class SignedPrimitiveNotificationThreadCreator implements NotificationQueueCreator<SignedPrimitiveNotification, SignedPrimitiveNotificationThread>
{

	@Override
	public SignedPrimitiveNotificationThread createInstance(int taskSize)
	{
		return new SignedPrimitiveNotificationThread(taskSize);
	}

}
