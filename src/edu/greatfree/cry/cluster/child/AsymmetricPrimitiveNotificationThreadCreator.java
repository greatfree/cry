package edu.greatfree.cry.cluster.child;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;

import edu.greatfree.cry.messege.AsymmetricPrimitiveNotification;

/**
 * 
 * @author libing
 * 
 * 05/12/2022
 *
 */
final class AsymmetricPrimitiveNotificationThreadCreator implements NotificationQueueCreator<AsymmetricPrimitiveNotification, AsymmetricPrimitiveNotificationThread>
{

	@Override
	public AsymmetricPrimitiveNotificationThread createInstance(int taskSize)
	{
		return new AsymmetricPrimitiveNotificationThread(taskSize);
	}

}
