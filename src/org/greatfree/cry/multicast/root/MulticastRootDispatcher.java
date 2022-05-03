package org.greatfree.cry.multicast.root;

import java.util.Calendar;
import java.util.logging.Logger;

import org.greatfree.concurrency.reactive.NotificationDispatcher;
import org.greatfree.cry.messege.AsymmetricPrimitiveNotification;
import org.greatfree.cry.messege.CryAppID;
import org.greatfree.cry.messege.PrivatePrimitiveNotification;
import org.greatfree.cry.messege.SignedPrimitiveNotification;
import org.greatfree.cry.messege.SymmetricPrimitiveNotification;
import org.greatfree.cry.multicast.AsymmetricPrimitiveNotificationThread;
import org.greatfree.cry.multicast.AsymmetricPrimitiveNotificationThreadCreator;
import org.greatfree.cry.multicast.PrivatePrimitiveNotificationThread;
import org.greatfree.cry.multicast.PrivatePrimitiveNotificationThreadCreator;
import org.greatfree.cry.multicast.SignedPrimitiveNotificationThread;
import org.greatfree.cry.multicast.SignedPrimitiveNotificationThreadCreator;
import org.greatfree.cry.multicast.SymmetricPrimitiveNotificationThread;
import org.greatfree.cry.multicast.SymmetricPrimitiveNotificationThreadCreator;
import org.greatfree.cry.server.CryptoCSDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.MulticastResponse;
import org.greatfree.server.MessageStream;
import org.greatfree.server.container.ServerProfile;

/**
 * 
 * @author libing
 * 
 * 04/28/2022
 *
 * MulticastChildDispatcher inherits ServerDispatcher rather than CryptoCSDispatcher since all the messages it processes inherit from ServerMessage instead of Notification/Request. 04/30/2022, Bing Li
 * 
 * In contrast, MulticastRootDispatcher inherits CryptoCSDispatcher since some of its processed messages inherit ServerMessage and some inherit Notification/Request. 04/30/2022, Bing Li
 *
 */
public final class MulticastRootDispatcher extends CryptoCSDispatcher
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.multicast.root");
	
	private NotificationDispatcher<MulticastResponse, MulticastResponseThread, MulticastResponseThreadCreator> multiResDispatcher;
	private NotificationDispatcher<SymmetricPrimitiveNotification, SymmetricPrimitiveNotificationThread, SymmetricPrimitiveNotificationThreadCreator> symNotifyDispatcher;
	private NotificationDispatcher<AsymmetricPrimitiveNotification, AsymmetricPrimitiveNotificationThread, AsymmetricPrimitiveNotificationThreadCreator> asymNotifyDispatcher;
	private NotificationDispatcher<SignedPrimitiveNotification, SignedPrimitiveNotificationThread, SignedPrimitiveNotificationThreadCreator> signedNotifyDispatcher;
	private NotificationDispatcher<PrivatePrimitiveNotification, PrivatePrimitiveNotificationThread, PrivatePrimitiveNotificationThreadCreator> privateNotifyDispatcher;

	public MulticastRootDispatcher(int serverThreadPoolSize, long serverThreadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		super(serverThreadPoolSize, serverThreadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);
		super.init();
		if (ServerProfile.CS().isDefault())
		{
			this.multiResDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<MulticastResponse, MulticastResponseThread, MulticastResponseThreadCreator>()
					.serverKey(super.getServerKey())
					.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
					.threadCreator(new MulticastResponseThreadCreator())
					.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
					.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
					.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
					.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
					.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
					.scheduler(super.getScheduler())
					.build();

			this.symNotifyDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<SymmetricPrimitiveNotification, SymmetricPrimitiveNotificationThread, SymmetricPrimitiveNotificationThreadCreator>()
					.serverKey(super.getServerKey())
					.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
					.threadCreator(new SymmetricPrimitiveNotificationThreadCreator())
					.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
					.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
					.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
					.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
					.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
					.scheduler(super.getScheduler())
					.build();

			this.asymNotifyDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<AsymmetricPrimitiveNotification, AsymmetricPrimitiveNotificationThread, AsymmetricPrimitiveNotificationThreadCreator>()
					.serverKey(super.getServerKey())
					.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
					.threadCreator(new AsymmetricPrimitiveNotificationThreadCreator())
					.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
					.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
					.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
					.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
					.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
					.scheduler(super.getScheduler())
					.build();

			this.signedNotifyDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<SignedPrimitiveNotification, SignedPrimitiveNotificationThread, SignedPrimitiveNotificationThreadCreator>()
					.serverKey(super.getServerKey())
					.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
					.threadCreator(new SignedPrimitiveNotificationThreadCreator())
					.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
					.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
					.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
					.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
					.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
					.scheduler(super.getScheduler())
					.build();

			this.privateNotifyDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<PrivatePrimitiveNotification, PrivatePrimitiveNotificationThread, PrivatePrimitiveNotificationThreadCreator>()
					.serverKey(super.getServerKey())
					.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
					.threadCreator(new PrivatePrimitiveNotificationThreadCreator())
					.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
					.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
					.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
					.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
					.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
					.scheduler(super.getScheduler())
					.build();
		}
		else
		{
			this.multiResDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<MulticastResponse, MulticastResponseThread, MulticastResponseThreadCreator>()
					.serverKey(super.getServerKey())
					.poolSize(ServerProfile.CS().getNotificationDispatcherPoolSize())
					.threadCreator(new MulticastResponseThreadCreator())
					.notificationQueueSize(ServerProfile.CS().getNotificationQueueSize())
					.dispatcherWaitTime(ServerProfile.CS().getNotificationDispatcherWaitTime())
					.waitRound(ServerProfile.CS().getNotificationDispatcherWaitRound())
					.idleCheckDelay(ServerProfile.CS().getNotificationDispatcherIdleCheckDelay())
					.idleCheckPeriod(ServerProfile.CS().getNotificationDispatcherIdleCheckPeriod())
					.scheduler(super.getScheduler())
					.build();

			this.symNotifyDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<SymmetricPrimitiveNotification, SymmetricPrimitiveNotificationThread, SymmetricPrimitiveNotificationThreadCreator>()
					.serverKey(super.getServerKey())
					.poolSize(ServerProfile.CS().getNotificationDispatcherPoolSize())
					.threadCreator(new SymmetricPrimitiveNotificationThreadCreator())
					.notificationQueueSize(ServerProfile.CS().getNotificationQueueSize())
					.dispatcherWaitTime(ServerProfile.CS().getNotificationDispatcherWaitTime())
					.waitRound(ServerProfile.CS().getNotificationDispatcherWaitRound())
					.idleCheckDelay(ServerProfile.CS().getNotificationDispatcherIdleCheckDelay())
					.idleCheckPeriod(ServerProfile.CS().getNotificationDispatcherIdleCheckPeriod())
					.scheduler(super.getScheduler())
					.build();

			this.asymNotifyDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<AsymmetricPrimitiveNotification, AsymmetricPrimitiveNotificationThread, AsymmetricPrimitiveNotificationThreadCreator>()
					.serverKey(super.getServerKey())
					.poolSize(ServerProfile.CS().getNotificationDispatcherPoolSize())
					.threadCreator(new AsymmetricPrimitiveNotificationThreadCreator())
					.notificationQueueSize(ServerProfile.CS().getNotificationQueueSize())
					.dispatcherWaitTime(ServerProfile.CS().getNotificationDispatcherWaitTime())
					.waitRound(ServerProfile.CS().getNotificationDispatcherWaitRound())
					.idleCheckDelay(ServerProfile.CS().getNotificationDispatcherIdleCheckDelay())
					.idleCheckPeriod(ServerProfile.CS().getNotificationDispatcherIdleCheckPeriod())
					.scheduler(super.getScheduler())
					.build();

			this.signedNotifyDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<SignedPrimitiveNotification, SignedPrimitiveNotificationThread, SignedPrimitiveNotificationThreadCreator>()
					.serverKey(super.getServerKey())
					.poolSize(ServerProfile.CS().getNotificationDispatcherPoolSize())
					.threadCreator(new SignedPrimitiveNotificationThreadCreator())
					.notificationQueueSize(ServerProfile.CS().getNotificationQueueSize())
					.dispatcherWaitTime(ServerProfile.CS().getNotificationDispatcherWaitTime())
					.waitRound(ServerProfile.CS().getNotificationDispatcherWaitRound())
					.idleCheckDelay(ServerProfile.CS().getNotificationDispatcherIdleCheckDelay())
					.idleCheckPeriod(ServerProfile.CS().getNotificationDispatcherIdleCheckPeriod())
					.scheduler(super.getScheduler())
					.build();

			this.privateNotifyDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<PrivatePrimitiveNotification, PrivatePrimitiveNotificationThread, PrivatePrimitiveNotificationThreadCreator>()
					.serverKey(super.getServerKey())
					.poolSize(ServerProfile.CS().getNotificationDispatcherPoolSize())
					.threadCreator(new PrivatePrimitiveNotificationThreadCreator())
					.notificationQueueSize(ServerProfile.CS().getNotificationQueueSize())
					.dispatcherWaitTime(ServerProfile.CS().getNotificationDispatcherWaitTime())
					.waitRound(ServerProfile.CS().getNotificationDispatcherWaitRound())
					.idleCheckDelay(ServerProfile.CS().getNotificationDispatcherIdleCheckDelay())
					.idleCheckPeriod(ServerProfile.CS().getNotificationDispatcherIdleCheckPeriod())
					.scheduler(super.getScheduler())
					.build();
		}
	}
	
	public void dispose(long timeout) throws InterruptedException
	{
		super.dispose(timeout);
		this.multiResDispatcher.dispose();
		this.symNotifyDispatcher.dispose();
		this.asymNotifyDispatcher.dispose();
		this.signedNotifyDispatcher.dispose();
		this.privateNotifyDispatcher.dispose();
	}

	public void process(MessageStream<ServerMessage> message)
	{
		super.process(message);
		switch (message.getMessage().getType())
		{
			case MulticastMessageType.MULTICAST_RESPONSE:
				log.info("MULTICAST_RESPONSE received @" + Calendar.getInstance().getTime());
				if (!this.multiResDispatcher.isReady())
				{
					super.execute(this.multiResDispatcher);
				}
				this.multiResDispatcher.enqueue((MulticastResponse)message.getMessage());
				break;
				
			case CryAppID.SYMMETRIC_PRIMITIVE_NOTIFICATION:
				log.info("SYMMETRIC_PRIMITIVE_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.symNotifyDispatcher.isReady())
				{
					super.execute(this.symNotifyDispatcher);
				}
				this.symNotifyDispatcher.enqueue((SymmetricPrimitiveNotification)message.getMessage());
				break;
				
			case CryAppID.ASYMMETRIC_PRIMITIVE_NOTIFICATION:
				log.info("ASYMMETRIC_PRIMITIVE_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.asymNotifyDispatcher.isReady())
				{
					super.execute(this.asymNotifyDispatcher);
				}
				this.asymNotifyDispatcher.enqueue((AsymmetricPrimitiveNotification)message.getMessage());
				break;
				
			case CryAppID.SIGNED_PRIMITIVE_NOTIFICATION:
				log.info("SIGNED_PRIMITIVE_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.signedNotifyDispatcher.isReady())
				{
					super.execute(this.signedNotifyDispatcher);
				}
				this.signedNotifyDispatcher.enqueue((SignedPrimitiveNotification)message.getMessage());
				break;
				
			case CryAppID.PRIVATE_PRIMITIVE_NOTIFICATION:
				log.info("PRIVATE_PRIMITIVE_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.privateNotifyDispatcher.isReady())
				{
					super.execute(this.privateNotifyDispatcher);
				}
				this.privateNotifyDispatcher.enqueue((PrivatePrimitiveNotification)message.getMessage());
				break;
		}
	}

}
