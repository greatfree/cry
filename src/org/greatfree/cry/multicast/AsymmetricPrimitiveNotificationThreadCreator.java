package org.greatfree.cry.multicast;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.cry.messege.AsymmetricPrimitiveNotification;

/**
 * 
 * @author libing
 * 
 * 04/30/2022
 *
 */
public final class AsymmetricPrimitiveNotificationThreadCreator implements NotificationQueueCreator<AsymmetricPrimitiveNotification, AsymmetricPrimitiveNotificationThread>
{

	@Override
	public AsymmetricPrimitiveNotificationThread createInstance(int taskSize)
	{
		return new AsymmetricPrimitiveNotificationThread(taskSize);
	}

}
