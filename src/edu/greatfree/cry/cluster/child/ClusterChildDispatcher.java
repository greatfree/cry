package edu.greatfree.cry.cluster.child;

import java.util.Calendar;
import java.util.logging.Logger;

import org.greatfree.cluster.message.ClusterMessageType;
import org.greatfree.concurrency.reactive.NotificationDispatcher;
import org.greatfree.concurrency.reactive.RequestDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.server.MessageStream;
import org.greatfree.server.container.ServerProfile;

import edu.greatfree.cry.messege.AsymmetricPrimitiveNotification;
import edu.greatfree.cry.messege.CryAppID;
import edu.greatfree.cry.messege.PrivatePrimitiveNotification;
import edu.greatfree.cry.messege.ServerMessageStream;
import edu.greatfree.cry.messege.SignedPrimitiveNotification;
import edu.greatfree.cry.messege.SymmetricPrimitiveNotification;
import edu.greatfree.cry.server.CryptoCSDispatcher;

/**
 * 
 * @author libing
 * 
 * 04/29/2022
 *
 */
// class ClusterChildDispatcher extends ClusterRootDispatcher
class ClusterChildDispatcher extends CryptoCSDispatcher
{
	private final static Logger log = Logger.getLogger("edu.greatfree.cry.cluster.child");

	private NotificationDispatcher<ServerMessage, ServerNotificationThread, ServerNotificationThreadCreator> serverNotifyDispatcher;
	private NotificationDispatcher<SymmetricPrimitiveNotification, SymmetricPrimitiveNotificationThread, SymmetricPrimitiveNotificationThreadCreator> symNotifyDispatcher;
	private NotificationDispatcher<AsymmetricPrimitiveNotification, AsymmetricPrimitiveNotificationThread, AsymmetricPrimitiveNotificationThreadCreator> asymNotifyDispatcher;
	private NotificationDispatcher<SignedPrimitiveNotification, SignedPrimitiveNotificationThread, SignedPrimitiveNotificationThreadCreator> signedNotifyDispatcher;
	private NotificationDispatcher<PrivatePrimitiveNotification, PrivatePrimitiveNotificationThread, PrivatePrimitiveNotificationThreadCreator> privateNotifyDispatcher;

	private RequestDispatcher<ServerMessage, ServerMessageStream, ServerMessage, ServerRequestThread, ServerRequestThreadCreator> serverReqDispatcher;
	/*
	 * The below code does not make sense. No long-term direct connections are established between the root and the child. Any requests are converted to notifications plus the rendezvous point. Additionally, the child has no direct connection with the client. 06/21/2022, Bing Li
	 * 
	private RequestDispatcher<SymmetricPrimitiveRequest, SymmetricPrimitiveStream, EncryptedResponse, SymmetricPrimitiveRequestThread, SymmetricPrimitiveRequestThreadCreator> symPrmReqDispatcher;
	private RequestDispatcher<AsymmetricPrimitiveRequest, AsymmetricPrimitiveStream, AsymmetricEncryptedResponse, AsymmetricPrimitiveRequestThread, AsymmetricPrimitiveRequestThreadCreator> asymPrmReqDispatcher;
	private RequestDispatcher<SignedPrimitiveRequest, SignedPrimitiveStream, SignedAsymmetricEncryptedResponse, SignedPrimitiveRequestThread, SignedPrimitiveRequestThreadCreator> signedPrmReqDispatcher;
	private RequestDispatcher<PrivatePrimitiveRequest, PrivatePrimitiveStream, PrivateResponse, PrivatePrimitiveRequestThread, PrivatePrimitiveRequestThreadCreator> privatePrmReqDispatcher;
	*/

	public ClusterChildDispatcher(int serverThreadPoolSize, long serverThreadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		super(serverThreadPoolSize, serverThreadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);
		super.init();
		if (ServerProfile.CS().isDefault())
		{
			this.serverNotifyDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<ServerMessage, ServerNotificationThread, ServerNotificationThreadCreator>()
					.serverKey(super.getServerKey())
					.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
					.threadCreator(new ServerNotificationThreadCreator())
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

			this.serverReqDispatcher = new RequestDispatcher.RequestDispatcherBuilder<ServerMessage, ServerMessageStream, ServerMessage, ServerRequestThread, ServerRequestThreadCreator>()
					.serverKey(super.getServerKey())
					.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
					.threadCreator(new ServerRequestThreadCreator())
					.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
					.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
//					.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
					.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
					.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
					.scheduler(super.getScheduler())
					.build();

			/*
			this.symPrmReqDispatcher = new RequestDispatcher.RequestDispatcherBuilder<SymmetricPrimitiveRequest, SymmetricPrimitiveStream, EncryptedResponse, SymmetricPrimitiveRequestThread, SymmetricPrimitiveRequestThreadCreator>()
					.serverKey(super.getServerKey())
					.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
					.threadCreator(new SymmetricPrimitiveRequestThreadCreator())
					.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
					.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
					.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
					.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
					.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
					.scheduler(super.getScheduler())
					.build();

			this.asymPrmReqDispatcher = new RequestDispatcher.RequestDispatcherBuilder<AsymmetricPrimitiveRequest, AsymmetricPrimitiveStream, AsymmetricEncryptedResponse, AsymmetricPrimitiveRequestThread, AsymmetricPrimitiveRequestThreadCreator>()
					.serverKey(super.getServerKey())
					.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
					.threadCreator(new AsymmetricPrimitiveRequestThreadCreator())
					.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
					.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
					.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
					.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
					.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
					.scheduler(super.getScheduler())
					.build();

			this.signedPrmReqDispatcher = new RequestDispatcher.RequestDispatcherBuilder<SignedPrimitiveRequest, SignedPrimitiveStream, SignedAsymmetricEncryptedResponse, SignedPrimitiveRequestThread, SignedPrimitiveRequestThreadCreator>()
					.serverKey(super.getServerKey())
					.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
					.threadCreator(new SignedPrimitiveRequestThreadCreator())
					.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
					.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
					.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
					.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
					.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
					.scheduler(super.getScheduler())
					.build();

			this.privatePrmReqDispatcher = new RequestDispatcher.RequestDispatcherBuilder<PrivatePrimitiveRequest, PrivatePrimitiveStream, PrivateResponse, PrivatePrimitiveRequestThread, PrivatePrimitiveRequestThreadCreator>()
					.serverKey(super.getServerKey())
					.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
					.threadCreator(new PrivatePrimitiveRequestThreadCreator())
					.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
					.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
					.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
					.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
					.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
					.scheduler(super.getScheduler())
					.build();
					*/
		}
		else
		{
			this.serverNotifyDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<ServerMessage, ServerNotificationThread, ServerNotificationThreadCreator>()
					.serverKey(super.getServerKey())
					.poolSize(ServerProfile.CS().getNotificationDispatcherPoolSize())
					.threadCreator(new ServerNotificationThreadCreator())
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

			this.serverReqDispatcher = new RequestDispatcher.RequestDispatcherBuilder<ServerMessage, ServerMessageStream, ServerMessage, ServerRequestThread, ServerRequestThreadCreator>()
					.serverKey(super.getServerKey())
					.poolSize(ServerProfile.CS().getRequestDispatcherPoolSize())
					.threadCreator(new ServerRequestThreadCreator())
					.requestQueueSize(ServerProfile.CS().getRequestQueueSize())
					.dispatcherWaitTime(ServerProfile.CS().getRequestDispatcherWaitTime())
//					.waitRound(ServerProfile.CS().getNotificationDispatcherWaitRound())
					.idleCheckDelay(ServerProfile.CS().getRequestDispatcherIdleCheckDelay())
					.idleCheckPeriod(ServerProfile.CS().getRequestDispatcherIdleCheckPeriod())
					.scheduler(super.getScheduler())
					.build();

			/*
			this.symPrmReqDispatcher = new RequestDispatcher.RequestDispatcherBuilder<SymmetricPrimitiveRequest, SymmetricPrimitiveStream, EncryptedResponse, SymmetricPrimitiveRequestThread, SymmetricPrimitiveRequestThreadCreator>()
					.serverKey(super.getServerKey())
					.poolSize(ServerProfile.CS().getRequestDispatcherPoolSize())
					.threadCreator(new SymmetricPrimitiveRequestThreadCreator())
					.requestQueueSize(ServerProfile.CS().getRequestQueueSize())
					.dispatcherWaitTime(ServerProfile.CS().getRequestDispatcherWaitTime())
					.waitRound(ServerProfile.CS().getNotificationDispatcherWaitRound())
					.idleCheckDelay(ServerProfile.CS().getRequestDispatcherIdleCheckDelay())
					.idleCheckPeriod(ServerProfile.CS().getRequestDispatcherIdleCheckPeriod())
					.scheduler(super.getScheduler())
					.build();

			this.asymPrmReqDispatcher = new RequestDispatcher.RequestDispatcherBuilder<AsymmetricPrimitiveRequest, AsymmetricPrimitiveStream, AsymmetricEncryptedResponse, AsymmetricPrimitiveRequestThread, AsymmetricPrimitiveRequestThreadCreator>()
					.serverKey(super.getServerKey())
					.poolSize(ServerProfile.CS().getRequestDispatcherPoolSize())
					.threadCreator(new AsymmetricPrimitiveRequestThreadCreator())
					.requestQueueSize(ServerProfile.CS().getRequestQueueSize())
					.dispatcherWaitTime(ServerProfile.CS().getRequestDispatcherWaitTime())
					.waitRound(ServerProfile.CS().getNotificationDispatcherWaitRound())
					.idleCheckDelay(ServerProfile.CS().getRequestDispatcherIdleCheckDelay())
					.idleCheckPeriod(ServerProfile.CS().getRequestDispatcherIdleCheckPeriod())
					.scheduler(super.getScheduler())
					.build();

			this.signedPrmReqDispatcher = new RequestDispatcher.RequestDispatcherBuilder<SignedPrimitiveRequest, SignedPrimitiveStream, SignedAsymmetricEncryptedResponse, SignedPrimitiveRequestThread, SignedPrimitiveRequestThreadCreator>()
					.serverKey(super.getServerKey())
					.poolSize(ServerProfile.CS().getRequestDispatcherPoolSize())
					.threadCreator(new SignedPrimitiveRequestThreadCreator())
					.requestQueueSize(ServerProfile.CS().getRequestQueueSize())
					.dispatcherWaitTime(ServerProfile.CS().getRequestDispatcherWaitTime())
					.waitRound(ServerProfile.CS().getNotificationDispatcherWaitRound())
					.idleCheckDelay(ServerProfile.CS().getRequestDispatcherIdleCheckDelay())
					.idleCheckPeriod(ServerProfile.CS().getRequestDispatcherIdleCheckPeriod())
					.scheduler(super.getScheduler())
					.build();

			this.privatePrmReqDispatcher = new RequestDispatcher.RequestDispatcherBuilder<PrivatePrimitiveRequest, PrivatePrimitiveStream, PrivateResponse, PrivatePrimitiveRequestThread, PrivatePrimitiveRequestThreadCreator>()
					.serverKey(super.getServerKey())
					.poolSize(ServerProfile.CS().getRequestDispatcherPoolSize())
					.threadCreator(new PrivatePrimitiveRequestThreadCreator())
					.requestQueueSize(ServerProfile.CS().getRequestQueueSize())
					.dispatcherWaitTime(ServerProfile.CS().getRequestDispatcherWaitTime())
					.waitRound(ServerProfile.CS().getNotificationDispatcherWaitRound())
					.idleCheckDelay(ServerProfile.CS().getRequestDispatcherIdleCheckDelay())
					.idleCheckPeriod(ServerProfile.CS().getRequestDispatcherIdleCheckPeriod())
					.scheduler(super.getScheduler())
					.build();
					*/
		}
	}
	
	public void dispose(long timeout) throws InterruptedException
	{
		super.dispose(timeout);
		this.serverNotifyDispatcher.dispose();
		this.symNotifyDispatcher.dispose();
		this.asymNotifyDispatcher.dispose();
		this.signedNotifyDispatcher.dispose();
		this.privateNotifyDispatcher.dispose();

		this.serverReqDispatcher.dispose();
		/*
		this.symPrmReqDispatcher.dispose();
		this.asymPrmReqDispatcher.dispose();
		this.signedPrmReqDispatcher.dispose();
		this.privatePrmReqDispatcher.dispose();
		*/
	}

	public void process(MessageStream<ServerMessage> message)
	{
//		log.info("message.getMessage().getType() = " + message.getMessage().getType());
		super.process(message);
		switch (message.getMessage().getType())
		{
			case ClusterMessageType.CHILD_RESPONSE:
				log.info("CHILD_RESPONSE received @" + Calendar.getInstance().getTime());
				if (!this.serverNotifyDispatcher.isReady())
				{
					super.execute(this.serverNotifyDispatcher);
				}
				this.serverNotifyDispatcher.enqueue(message.getMessage());
				break;

			case MulticastMessageType.INTERCAST_NOTIFICATION:
				log.info("INTERCAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.serverNotifyDispatcher.isReady())
				{
					super.execute(this.serverNotifyDispatcher);
				}
				this.serverNotifyDispatcher.enqueue(message.getMessage());
				break;
				
			case MulticastMessageType.MULTICAST_NOTIFICATION:
				log.info("MULTICAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.serverNotifyDispatcher.isReady())
				{
					super.execute(this.serverNotifyDispatcher);
				}
				this.serverNotifyDispatcher.enqueue(message.getMessage());
				break;
				
			case MulticastMessageType.MULTICAST_REQUEST:
				log.info("MULTICAST_REQUEST received @" + Calendar.getInstance().getTime());
				if (!this.serverNotifyDispatcher.isReady())
				{
					super.execute(this.serverNotifyDispatcher);
				}
				this.serverNotifyDispatcher.enqueue(message.getMessage());
				break;

			case MulticastMessageType.INTERCAST_REQUEST:
				log.info("INTERCAST_REQUEST received @" + Calendar.getInstance().getTime());
				if (!this.serverReqDispatcher.isReady())
				{
					super.execute(this.serverReqDispatcher);
				}
				this.serverReqDispatcher.enqueue(new ServerMessageStream(message.getOutStream(), message.getLock(), message.getMessage()));
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

				/*
			case CryAppID.SYMMETRIC_PRIMITIVE_REQUEST:
				log.info("SYMMETRIC_PRIMITIVE_REQUEST received @" + Calendar.getInstance().getTime());
				if (!this.symPrmReqDispatcher.isReady())
				{
					super.execute(this.symPrmReqDispatcher);
				}
				this.symPrmReqDispatcher.enqueue(new SymmetricPrimitiveStream(message.getOutStream(), message.getLock(), (SymmetricPrimitiveRequest)message.getMessage()));
				break;
				
			case CryAppID.ASYMMETRIC_PRIMITIVE_REQUEST:
				log.info("ASYMMETRIC_PRIMITIVE_REQUEST received @" + Calendar.getInstance().getTime());
				if (!this.asymPrmReqDispatcher.isReady())
				{
					super.execute(this.asymPrmReqDispatcher);
				}
				this.asymPrmReqDispatcher.enqueue(new AsymmetricPrimitiveStream(message.getOutStream(), message.getLock(), (AsymmetricPrimitiveRequest)message.getMessage()));
				break;
				
			case CryAppID.SIGNED_PRIMITIVE_REQUEST:
				log.info("SIGNED_PRIMITIVE_REQUEST received @" + Calendar.getInstance().getTime());
				if (!this.signedPrmReqDispatcher.isReady())
				{
					super.execute(this.signedPrmReqDispatcher);
				}
				this.signedPrmReqDispatcher.enqueue(new SignedPrimitiveStream(message.getOutStream(), message.getLock(), (SignedPrimitiveRequest)message.getMessage()));
				break;
				
			case CryAppID.PRIVATE_PRIMITIVE_REQUEST:
				log.info("PRIVATE_PRIMITIVE_REQUEST received @" + Calendar.getInstance().getTime());
				if (!this.privatePrmReqDispatcher.isReady())
				{
					super.execute(this.privatePrmReqDispatcher);
				}
				this.privatePrmReqDispatcher.enqueue(new PrivatePrimitiveStream(message.getOutStream(), message.getLock(), (PrivatePrimitiveRequest)message.getMessage()));
				break;
				*/
		}
	}
	

}
