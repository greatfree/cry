package edu.greatfree.cry.multicast.child;

import java.util.Calendar;
import java.util.logging.Logger;

import org.greatfree.concurrency.reactive.NotificationDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.server.MessageStream;
import org.greatfree.server.container.ServerProfile;

import edu.greatfree.cry.messege.AsymmetricPrimitiveNotification;
import edu.greatfree.cry.messege.CryAppID;
import edu.greatfree.cry.messege.PrivatePrimitiveNotification;
import edu.greatfree.cry.messege.SignedPrimitiveNotification;
import edu.greatfree.cry.messege.SymmetricPrimitiveNotification;
import edu.greatfree.cry.messege.multicast.PrimitiveMulticastNotification;
import edu.greatfree.cry.messege.multicast.PrimitiveMulticastRequest;
import edu.greatfree.cry.multicast.AsymmetricPrimitiveNotificationThread;
import edu.greatfree.cry.multicast.AsymmetricPrimitiveNotificationThreadCreator;
import edu.greatfree.cry.multicast.PrivatePrimitiveNotificationThread;
import edu.greatfree.cry.multicast.PrivatePrimitiveNotificationThreadCreator;
import edu.greatfree.cry.multicast.SignedPrimitiveNotificationThread;
import edu.greatfree.cry.multicast.SignedPrimitiveNotificationThreadCreator;
import edu.greatfree.cry.multicast.SymmetricPrimitiveNotificationThread;
import edu.greatfree.cry.multicast.SymmetricPrimitiveNotificationThreadCreator;
import edu.greatfree.cry.server.CryptoCSDispatcher;

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
// public final class MulticastChildDispatcher extends ServerDispatcher<ServerMessage>
public final class MulticastChildDispatcher extends CryptoCSDispatcher
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.multicast.child");

//	private NotificationDispatcher<MulticastNotification, MulticastNotificationThread, MulticastNotificationThreadCreator> multiNotifyDispatcher;
	private NotificationDispatcher<PrimitiveMulticastNotification, MulticastNotificationThread, MulticastNotificationThreadCreator> multiNotifyDispatcher;
//	private NotificationDispatcher<MulticastRequest, MulticastRequestThread, MulticastRequestThreadCreator> multiReqDispatcher;
	private NotificationDispatcher<PrimitiveMulticastRequest, MulticastRequestThread, MulticastRequestThreadCreator> multiReqDispatcher;

	private NotificationDispatcher<SymmetricPrimitiveNotification, SymmetricPrimitiveNotificationThread, SymmetricPrimitiveNotificationThreadCreator> symNotifyDispatcher;
	private NotificationDispatcher<AsymmetricPrimitiveNotification, AsymmetricPrimitiveNotificationThread, AsymmetricPrimitiveNotificationThreadCreator> asymNotifyDispatcher;
	private NotificationDispatcher<SignedPrimitiveNotification, SignedPrimitiveNotificationThread, SignedPrimitiveNotificationThreadCreator> signedNotifyDispatcher;
	private NotificationDispatcher<PrivatePrimitiveNotification, PrivatePrimitiveNotificationThread, PrivatePrimitiveNotificationThreadCreator> privateNotifyDispatcher;

	public MulticastChildDispatcher(int serverThreadPoolSize, long serverThreadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		super(serverThreadPoolSize, serverThreadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);
		super.init();
		if (ServerProfile.CS().isDefault())
		{
//			log.info("super.getServerKey() = " + super.getServerKey());
			this.multiNotifyDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<PrimitiveMulticastNotification, MulticastNotificationThread, MulticastNotificationThreadCreator>()
					.serverKey(super.getServerKey())
					.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
					.threadCreator(new MulticastNotificationThreadCreator())
					.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
					.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
//					.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
					.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
					.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
					.scheduler(super.getScheduler())
					.build();

			this.multiReqDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<PrimitiveMulticastRequest, MulticastRequestThread, MulticastRequestThreadCreator>()
					.serverKey(super.getServerKey())
					.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
					.threadCreator(new MulticastRequestThreadCreator())
					.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
					.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
//					.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
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
//					.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
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
//					.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
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
//					.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
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
//					.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
					.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
					.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
					.scheduler(super.getScheduler())
					.build();
		}
		else
		{
			this.multiNotifyDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<PrimitiveMulticastNotification, MulticastNotificationThread, MulticastNotificationThreadCreator>()
					.serverKey(super.getServerKey())
					.poolSize(ServerProfile.CS().getNotificationDispatcherPoolSize())
					.threadCreator(new MulticastNotificationThreadCreator())
					.notificationQueueSize(ServerProfile.CS().getNotificationQueueSize())
					.dispatcherWaitTime(ServerProfile.CS().getNotificationDispatcherWaitTime())
//					.waitRound(ServerProfile.CS().getNotificationDispatcherWaitRound())
					.idleCheckDelay(ServerProfile.CS().getNotificationDispatcherIdleCheckDelay())
					.idleCheckPeriod(ServerProfile.CS().getNotificationDispatcherIdleCheckPeriod())
					.scheduler(super.getScheduler())
					.build();

			this.multiReqDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<PrimitiveMulticastRequest, MulticastRequestThread, MulticastRequestThreadCreator>()
					.serverKey(super.getServerKey())
					.poolSize(ServerProfile.CS().getNotificationDispatcherPoolSize())
					.threadCreator(new MulticastRequestThreadCreator())
					.notificationQueueSize(ServerProfile.CS().getNotificationQueueSize())
					.dispatcherWaitTime(ServerProfile.CS().getNotificationDispatcherWaitTime())
//					.waitRound(ServerProfile.CS().getNotificationDispatcherWaitRound())
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
//					.waitRound(ServerProfile.CS().getNotificationDispatcherWaitRound())
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
//					.waitRound(ServerProfile.CS().getNotificationDispatcherWaitRound())
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
//					.waitRound(ServerProfile.CS().getNotificationDispatcherWaitRound())
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
//					.waitRound(ServerProfile.CS().getNotificationDispatcherWaitRound())
					.idleCheckDelay(ServerProfile.CS().getNotificationDispatcherIdleCheckDelay())
					.idleCheckPeriod(ServerProfile.CS().getNotificationDispatcherIdleCheckPeriod())
					.scheduler(super.getScheduler())
					.build();
		}
	}

	@Override
	public void dispose(long timeout) throws InterruptedException
	{
		log.info("Starting to dispose child dispatcher ...");
		super.dispose(timeout);
		this.multiNotifyDispatcher.dispose();
		this.multiReqDispatcher.dispose();
		this.symNotifyDispatcher.dispose();
		this.asymNotifyDispatcher.dispose();
		this.signedNotifyDispatcher.dispose();
		this.privateNotifyDispatcher.dispose();
		log.info("Dhild dispatcher disposed ...");
	}

	@Override
	public void process(MessageStream<ServerMessage> message)
	{
		log.info("message.getType() = " + message.getMessage().getType());
		super.process(message);
		switch (message.getMessage().getType())
		{
			case MulticastMessageType.MULTICAST_NOTIFICATION:
				log.info("MULTICAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.multiNotifyDispatcher.isReady())
				{
					super.execute(this.multiNotifyDispatcher);
				}
//				this.multiNotifyDispatcher.enqueue((MulticastNotification)message.getMessage());
				this.multiNotifyDispatcher.enqueue((PrimitiveMulticastNotification)message.getMessage());
				break;
				
			case MulticastMessageType.MULTICAST_REQUEST:
				log.info("MULTICAST_REQUEST received @" + Calendar.getInstance().getTime());
				if (!this.multiReqDispatcher.isReady())
				{
					super.execute(this.multiReqDispatcher);
				}
//				this.multiReqDispatcher.enqueue((MulticastRequest)message.getMessage());
				this.multiReqDispatcher.enqueue((PrimitiveMulticastRequest)message.getMessage());
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
