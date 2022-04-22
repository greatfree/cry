package org.greatfree.cry.server;

import java.util.Calendar;
import java.util.logging.Logger;

import org.greatfree.concurrency.reactive.NotificationDispatcher;
import org.greatfree.concurrency.reactive.RequestDispatcher;
import org.greatfree.cry.messege.AsymmetricEncryptedNotification;
import org.greatfree.cry.messege.AsymmetricEncryptedRequest;
import org.greatfree.cry.messege.AsymmetricEncryptedResponse;
import org.greatfree.cry.messege.AsymmetricEncryptedStream;
import org.greatfree.cry.messege.CryAppID;
import org.greatfree.cry.messege.EncryptedRequestStream;
import org.greatfree.cry.messege.OwnershipRequest;
import org.greatfree.cry.messege.OwnershipResponse;
import org.greatfree.cry.messege.OwnershipStream;
import org.greatfree.cry.messege.PrivateNotification;
import org.greatfree.cry.messege.PrivateRequest;
import org.greatfree.cry.messege.PrivateResponse;
import org.greatfree.cry.messege.PrivateStream;
import org.greatfree.cry.messege.PublicCryptoSessionRequest;
import org.greatfree.cry.messege.PublicCryptoSessionResponse;
import org.greatfree.cry.messege.PublicCryptoSessionStream;
import org.greatfree.cry.messege.SignedAsymmetricEncryptedNotification;
import org.greatfree.cry.messege.SignedAsymmetricEncryptedRequest;
import org.greatfree.cry.messege.SignedAsymmetricEncryptedResponse;
import org.greatfree.cry.messege.SignedAsymmetricEncryptedStream;
import org.greatfree.cry.messege.SymmetricCryptoSessionRequest;
import org.greatfree.cry.messege.SymmetricCryptoSessionResponse;
import org.greatfree.cry.messege.SymmetricCryptoSessionStream;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;
import org.greatfree.server.MessageStream;
import org.greatfree.server.ServerDispatcher;
import org.greatfree.server.container.CSMessageType;
import org.greatfree.server.container.ServerProfile;

/**
 * 
 * @author libing
 * 
 * 01/06/2022, Bing Li
 * 
 * It is necessary to rewrite the dispatcher to run the cryptography-related techniques. 01/06/2022, Bing Li
 *
 */
class CryptoCSDispatcher extends ServerDispatcher<ServerMessage>
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.server");

//	private NotificationDispatcher<EncryptedNotification, EncryptedNotificationThread, EncryptedNotificationThreadCreator> notificationDispatcher;
	private NotificationDispatcher<Notification, EncryptedNotificationThread, EncryptedNotificationThreadCreator> notificationDispatcher;
	private NotificationDispatcher<AsymmetricEncryptedNotification, AsymmetricEncryptedNotificationThread, AsymmetricEncryptedNotificationThreadCreator> asymNotifyDispatcher;
	private NotificationDispatcher<SignedAsymmetricEncryptedNotification, SignedAsymmetricEncryptedNotificationThread, SignedAsymmetricEncryptedNotificationThreadCreator> signedNotifyDispatcher;
	private NotificationDispatcher<PrivateNotification, PrivateNotificationThread, PrivateNotificationThreadCreator> privateNotifyDispatcher;

	//	private RequestDispatcher<EncryptedRequest, EncryptedRequestStream, EncryptedResponse, EncryptedRequestThread, EncryptedRequestThreadCreator> requestDispatcher;
	private RequestDispatcher<Request, EncryptedRequestStream, ServerMessage, EncryptedRequestThread, EncryptedRequestThreadCreator> requestDispatcher;
	private RequestDispatcher<SymmetricCryptoSessionRequest, SymmetricCryptoSessionStream, SymmetricCryptoSessionResponse, SymmetricCryptoSessionThread, SymmetricCryptoSessionThreadCreator> symReqDispatcher;
	private RequestDispatcher<AsymmetricEncryptedRequest, AsymmetricEncryptedStream, AsymmetricEncryptedResponse, AsymmetricEncryptedThread, AsymmetricEncryptedThreadCreator> asymReqDispatcher;
	private RequestDispatcher<PublicCryptoSessionRequest, PublicCryptoSessionStream, PublicCryptoSessionResponse, PublicCryptoSessionThread, PublicCryptoSessionThreadCreator> pubReqDispatcher;
	private RequestDispatcher<SignedAsymmetricEncryptedRequest, SignedAsymmetricEncryptedStream, SignedAsymmetricEncryptedResponse, SignedAsymmetricEncryptedThread, SignedAsymmetricEncryptedThreadCreator> signedReqDispatcher;
	private RequestDispatcher<OwnershipRequest, OwnershipStream, OwnershipResponse, OwnershipThread, OwnershipThreadCreator> ownReqDispatcher;
	private RequestDispatcher<PrivateRequest, PrivateStream, PrivateResponse, PrivateThread, PrivateThreadCreator> privateReqDispatcher;

	public CryptoCSDispatcher(int serverThreadPoolSize, long serverThreadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		super(serverThreadPoolSize, serverThreadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);
	}
	
	public void init()
	{
		if (ServerProfile.CS().isDefault())
		{
//			this.notificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<EncryptedNotification, EncryptedNotificationThread, EncryptedNotificationThreadCreator>()
			this.notificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<Notification, EncryptedNotificationThread, EncryptedNotificationThreadCreator>()
					.serverKey(super.getServerKey())
					.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
					.threadCreator(new EncryptedNotificationThreadCreator())
					.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
					.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
					.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
					.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
					.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
					.scheduler(super.getScheduler())
					.build();

			this.asymNotifyDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<AsymmetricEncryptedNotification, AsymmetricEncryptedNotificationThread, AsymmetricEncryptedNotificationThreadCreator>()
					.serverKey(super.getServerKey())
					.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
					.threadCreator(new AsymmetricEncryptedNotificationThreadCreator())
					.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
					.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
					.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
					.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
					.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
					.scheduler(super.getScheduler())
					.build();

			this.signedNotifyDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<SignedAsymmetricEncryptedNotification, SignedAsymmetricEncryptedNotificationThread, SignedAsymmetricEncryptedNotificationThreadCreator>()
					.serverKey(super.getServerKey())
					.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
					.threadCreator(new SignedAsymmetricEncryptedNotificationThreadCreator())
					.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
					.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
					.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
					.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
					.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
					.scheduler(super.getScheduler())
					.build();

			this.privateNotifyDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<PrivateNotification, PrivateNotificationThread, PrivateNotificationThreadCreator>()
					.serverKey(super.getServerKey())
					.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
					.threadCreator(new PrivateNotificationThreadCreator())
					.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
					.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
					.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
					.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
					.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
					.scheduler(super.getScheduler())
					.build();

//			this.requestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<EncryptedRequest, EncryptedRequestStream, EncryptedResponse, EncryptedRequestThread, EncryptedRequestThreadCreator>()
			this.requestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<Request, EncryptedRequestStream, ServerMessage, EncryptedRequestThread, EncryptedRequestThreadCreator>()
					.serverKey(super.getServerKey())
					.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
					.threadCreator(new EncryptedRequestThreadCreator())
					.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
					.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
					.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
					.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
					.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
					.scheduler(super.getScheduler())
					.build();

			this.symReqDispatcher = new RequestDispatcher.RequestDispatcherBuilder<SymmetricCryptoSessionRequest, SymmetricCryptoSessionStream, SymmetricCryptoSessionResponse, SymmetricCryptoSessionThread, SymmetricCryptoSessionThreadCreator>()
					.serverKey(super.getServerKey())
					.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
					.threadCreator(new SymmetricCryptoSessionThreadCreator())
					.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
					.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
					.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
					.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
					.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
					.scheduler(super.getScheduler())
					.build();

			this.asymReqDispatcher = new RequestDispatcher.RequestDispatcherBuilder<AsymmetricEncryptedRequest, AsymmetricEncryptedStream, AsymmetricEncryptedResponse, AsymmetricEncryptedThread, AsymmetricEncryptedThreadCreator>()
					.serverKey(super.getServerKey())
					.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
					.threadCreator(new AsymmetricEncryptedThreadCreator())
					.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
					.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
					.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
					.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
					.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
					.scheduler(super.getScheduler())
					.build();

			this.pubReqDispatcher = new RequestDispatcher.RequestDispatcherBuilder<PublicCryptoSessionRequest, PublicCryptoSessionStream, PublicCryptoSessionResponse, PublicCryptoSessionThread, PublicCryptoSessionThreadCreator>()
					.serverKey(super.getServerKey())
					.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
					.threadCreator(new PublicCryptoSessionThreadCreator())
					.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
					.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
					.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
					.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
					.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
					.scheduler(super.getScheduler())
					.build();

			this.signedReqDispatcher = new RequestDispatcher.RequestDispatcherBuilder<SignedAsymmetricEncryptedRequest, SignedAsymmetricEncryptedStream, SignedAsymmetricEncryptedResponse, SignedAsymmetricEncryptedThread, SignedAsymmetricEncryptedThreadCreator>()
					.serverKey(super.getServerKey())
					.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
					.threadCreator(new SignedAsymmetricEncryptedThreadCreator())
					.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
					.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
					.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
					.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
					.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
					.scheduler(super.getScheduler())
					.build();

			this.ownReqDispatcher = new RequestDispatcher.RequestDispatcherBuilder<OwnershipRequest, OwnershipStream, OwnershipResponse, OwnershipThread, OwnershipThreadCreator>()
					.serverKey(super.getServerKey())
					.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
					.threadCreator(new OwnershipThreadCreator())
					.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
					.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
					.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
					.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
					.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
					.scheduler(super.getScheduler())
					.build();

			this.privateReqDispatcher = new RequestDispatcher.RequestDispatcherBuilder<PrivateRequest, PrivateStream, PrivateResponse, PrivateThread, PrivateThreadCreator>()
					.serverKey(super.getServerKey())
					.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
					.threadCreator(new PrivateThreadCreator())
					.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
					.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
					.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
					.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
					.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
					.scheduler(super.getScheduler())
					.build();
		}
		else
		{
//			this.notificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<EncryptedNotification, EncryptedNotificationThread, EncryptedNotificationThreadCreator>()
			this.notificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<Notification, EncryptedNotificationThread, EncryptedNotificationThreadCreator>()
					.serverKey(super.getServerKey())
					.poolSize(ServerProfile.CS().getNotificationDispatcherPoolSize())
					.threadCreator(new EncryptedNotificationThreadCreator())
					.notificationQueueSize(ServerProfile.CS().getNotificationQueueSize())
					.dispatcherWaitTime(ServerProfile.CS().getNotificationDispatcherWaitTime())
					.waitRound(ServerProfile.CS().getNotificationDispatcherWaitRound())
					.idleCheckDelay(ServerProfile.CS().getNotificationDispatcherIdleCheckDelay())
					.idleCheckPeriod(ServerProfile.CS().getNotificationDispatcherIdleCheckPeriod())
					.scheduler(super.getScheduler())
					.build();

			this.asymNotifyDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<AsymmetricEncryptedNotification, AsymmetricEncryptedNotificationThread, AsymmetricEncryptedNotificationThreadCreator>()
					.serverKey(super.getServerKey())
					.poolSize(ServerProfile.CS().getNotificationDispatcherPoolSize())
					.threadCreator(new AsymmetricEncryptedNotificationThreadCreator())
					.notificationQueueSize(ServerProfile.CS().getNotificationQueueSize())
					.dispatcherWaitTime(ServerProfile.CS().getNotificationDispatcherWaitTime())
					.waitRound(ServerProfile.CS().getNotificationDispatcherWaitRound())
					.idleCheckDelay(ServerProfile.CS().getNotificationDispatcherIdleCheckDelay())
					.idleCheckPeriod(ServerProfile.CS().getNotificationDispatcherIdleCheckPeriod())
					.scheduler(super.getScheduler())
					.build();

			this.signedNotifyDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<SignedAsymmetricEncryptedNotification, SignedAsymmetricEncryptedNotificationThread, SignedAsymmetricEncryptedNotificationThreadCreator>()
					.serverKey(super.getServerKey())
					.poolSize(ServerProfile.CS().getNotificationDispatcherPoolSize())
					.threadCreator(new SignedAsymmetricEncryptedNotificationThreadCreator())
					.notificationQueueSize(ServerProfile.CS().getNotificationQueueSize())
					.dispatcherWaitTime(ServerProfile.CS().getNotificationDispatcherWaitTime())
					.waitRound(ServerProfile.CS().getNotificationDispatcherWaitRound())
					.idleCheckDelay(ServerProfile.CS().getNotificationDispatcherIdleCheckDelay())
					.idleCheckPeriod(ServerProfile.CS().getNotificationDispatcherIdleCheckPeriod())
					.scheduler(super.getScheduler())
					.build();

			this.privateNotifyDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<PrivateNotification, PrivateNotificationThread, PrivateNotificationThreadCreator>()
					.serverKey(super.getServerKey())
					.poolSize(ServerProfile.CS().getNotificationDispatcherPoolSize())
					.threadCreator(new PrivateNotificationThreadCreator())
					.notificationQueueSize(ServerProfile.CS().getNotificationQueueSize())
					.dispatcherWaitTime(ServerProfile.CS().getNotificationDispatcherWaitTime())
					.waitRound(ServerProfile.CS().getNotificationDispatcherWaitRound())
					.idleCheckDelay(ServerProfile.CS().getNotificationDispatcherIdleCheckDelay())
					.idleCheckPeriod(ServerProfile.CS().getNotificationDispatcherIdleCheckPeriod())
					.scheduler(super.getScheduler())
					.build();

//			this.requestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<EncryptedRequest, EncryptedRequestStream, EncryptedResponse, EncryptedRequestThread, EncryptedRequestThreadCreator>()
			this.requestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<Request, EncryptedRequestStream, ServerMessage, EncryptedRequestThread, EncryptedRequestThreadCreator>()
					.serverKey(super.getServerKey())
					.poolSize(ServerProfile.CS().getRequestDispatcherPoolSize())
					.threadCreator(new EncryptedRequestThreadCreator())
					.requestQueueSize(ServerProfile.CS().getRequestQueueSize())
					.dispatcherWaitTime(ServerProfile.CS().getRequestDispatcherWaitTime())
					.waitRound(ServerProfile.CS().getNotificationDispatcherWaitRound())
					.idleCheckDelay(ServerProfile.CS().getRequestDispatcherIdleCheckDelay())
					.idleCheckPeriod(ServerProfile.CS().getRequestDispatcherIdleCheckPeriod())
					.scheduler(super.getScheduler())
					.build();

			this.symReqDispatcher = new RequestDispatcher.RequestDispatcherBuilder<SymmetricCryptoSessionRequest, SymmetricCryptoSessionStream, SymmetricCryptoSessionResponse, SymmetricCryptoSessionThread, SymmetricCryptoSessionThreadCreator>()
					.serverKey(super.getServerKey())
					.poolSize(ServerProfile.CS().getRequestDispatcherPoolSize())
					.threadCreator(new SymmetricCryptoSessionThreadCreator())
					.requestQueueSize(ServerProfile.CS().getRequestQueueSize())
					.dispatcherWaitTime(ServerProfile.CS().getRequestDispatcherWaitTime())
					.waitRound(ServerProfile.CS().getNotificationDispatcherWaitRound())
					.idleCheckDelay(ServerProfile.CS().getRequestDispatcherIdleCheckDelay())
					.idleCheckPeriod(ServerProfile.CS().getRequestDispatcherIdleCheckPeriod())
					.scheduler(super.getScheduler())
					.build();

			this.asymReqDispatcher = new RequestDispatcher.RequestDispatcherBuilder<AsymmetricEncryptedRequest, AsymmetricEncryptedStream, AsymmetricEncryptedResponse, AsymmetricEncryptedThread, AsymmetricEncryptedThreadCreator>()
					.serverKey(super.getServerKey())
					.poolSize(ServerProfile.CS().getRequestDispatcherPoolSize())
					.threadCreator(new AsymmetricEncryptedThreadCreator())
					.requestQueueSize(ServerProfile.CS().getRequestQueueSize())
					.dispatcherWaitTime(ServerProfile.CS().getRequestDispatcherWaitTime())
					.waitRound(ServerProfile.CS().getNotificationDispatcherWaitRound())
					.idleCheckDelay(ServerProfile.CS().getRequestDispatcherIdleCheckDelay())
					.idleCheckPeriod(ServerProfile.CS().getRequestDispatcherIdleCheckPeriod())
					.scheduler(super.getScheduler())
					.build();

			this.pubReqDispatcher = new RequestDispatcher.RequestDispatcherBuilder<PublicCryptoSessionRequest, PublicCryptoSessionStream, PublicCryptoSessionResponse, PublicCryptoSessionThread, PublicCryptoSessionThreadCreator>()
					.serverKey(super.getServerKey())
					.poolSize(ServerProfile.CS().getRequestDispatcherPoolSize())
					.threadCreator(new PublicCryptoSessionThreadCreator())
					.requestQueueSize(ServerProfile.CS().getRequestQueueSize())
					.dispatcherWaitTime(ServerProfile.CS().getRequestDispatcherWaitTime())
					.waitRound(ServerProfile.CS().getNotificationDispatcherWaitRound())
					.idleCheckDelay(ServerProfile.CS().getRequestDispatcherIdleCheckDelay())
					.idleCheckPeriod(ServerProfile.CS().getRequestDispatcherIdleCheckPeriod())
					.scheduler(super.getScheduler())
					.build();

			this.signedReqDispatcher = new RequestDispatcher.RequestDispatcherBuilder<SignedAsymmetricEncryptedRequest, SignedAsymmetricEncryptedStream, SignedAsymmetricEncryptedResponse, SignedAsymmetricEncryptedThread, SignedAsymmetricEncryptedThreadCreator>()
					.serverKey(super.getServerKey())
					.poolSize(ServerProfile.CS().getRequestDispatcherPoolSize())
					.threadCreator(new SignedAsymmetricEncryptedThreadCreator())
					.requestQueueSize(ServerProfile.CS().getRequestQueueSize())
					.dispatcherWaitTime(ServerProfile.CS().getRequestDispatcherWaitTime())
					.waitRound(ServerProfile.CS().getNotificationDispatcherWaitRound())
					.idleCheckDelay(ServerProfile.CS().getRequestDispatcherIdleCheckDelay())
					.idleCheckPeriod(ServerProfile.CS().getRequestDispatcherIdleCheckPeriod())
					.scheduler(super.getScheduler())
					.build();

			this.ownReqDispatcher = new RequestDispatcher.RequestDispatcherBuilder<OwnershipRequest, OwnershipStream, OwnershipResponse, OwnershipThread, OwnershipThreadCreator>()
					.serverKey(super.getServerKey())
					.poolSize(ServerProfile.CS().getRequestDispatcherPoolSize())
					.threadCreator(new OwnershipThreadCreator())
					.requestQueueSize(ServerProfile.CS().getRequestQueueSize())
					.dispatcherWaitTime(ServerProfile.CS().getRequestDispatcherWaitTime())
					.waitRound(ServerProfile.CS().getNotificationDispatcherWaitRound())
					.idleCheckDelay(ServerProfile.CS().getRequestDispatcherIdleCheckDelay())
					.idleCheckPeriod(ServerProfile.CS().getRequestDispatcherIdleCheckPeriod())
					.scheduler(super.getScheduler())
					.build();

			this.privateReqDispatcher = new RequestDispatcher.RequestDispatcherBuilder<PrivateRequest, PrivateStream, PrivateResponse, PrivateThread, PrivateThreadCreator>()
					.serverKey(super.getServerKey())
					.poolSize(ServerProfile.CS().getRequestDispatcherPoolSize())
					.threadCreator(new PrivateThreadCreator())
					.requestQueueSize(ServerProfile.CS().getRequestQueueSize())
					.dispatcherWaitTime(ServerProfile.CS().getRequestDispatcherWaitTime())
					.waitRound(ServerProfile.CS().getNotificationDispatcherWaitRound())
					.idleCheckDelay(ServerProfile.CS().getRequestDispatcherIdleCheckDelay())
					.idleCheckPeriod(ServerProfile.CS().getRequestDispatcherIdleCheckPeriod())
					.scheduler(super.getScheduler())
					.build();
		}
	}

	@Override
	public void dispose(long timeout) throws InterruptedException
	{
		super.shutdown(timeout);
		this.notificationDispatcher.dispose();
		this.asymNotifyDispatcher.dispose();
		this.signedNotifyDispatcher.dispose();
		this.privateNotifyDispatcher.dispose();
		
		this.requestDispatcher.dispose();
		this.symReqDispatcher.dispose();
		this.asymReqDispatcher.dispose();
		this.pubReqDispatcher.dispose();
		this.signedReqDispatcher.dispose();
		this.ownReqDispatcher.dispose();
		this.privateReqDispatcher.dispose();
	}

	@Override
	public void process(MessageStream<ServerMessage> message)
	{
		switch (message.getMessage().getType())
		{
			case CSMessageType.NOTIFICATION:
				Notification notification = (Notification)message.getMessage();
				log.info("NOTIFICATION received @" + Calendar.getInstance().getTime());
				switch (notification.getApplicationID())
				{
					case CryAppID.ASYMMETRIC_ENCRYPTED_NOTIFICATION:
						log.info("ASYMMETRIC_ENCRYPTED_NOTIFICATION received @" + Calendar.getInstance().getTime());
						if (!this.asymNotifyDispatcher.isReady())
						{
							super.execute(this.asymNotifyDispatcher);
						}
						this.asymNotifyDispatcher.enqueue((AsymmetricEncryptedNotification)notification);
						break;
						
					case CryAppID.SIGNED_ASYMMETRIC_ENCRYPTED_NOTIFICATION:
						log.info("SIGNED_ASYMMETRIC_ENCRYPTED_NOTIFICATION received @" + Calendar.getInstance().getTime());
						if (!this.signedNotifyDispatcher.isReady())
						{
							super.execute(this.signedNotifyDispatcher);
						}
						this.signedNotifyDispatcher.enqueue((SignedAsymmetricEncryptedNotification)notification);
						break;
						
					case CryAppID.PRIVATE_NOTIFICATION:
						log.info("PRIVATE_NOTIFICATION received @" + Calendar.getInstance().getTime());
						if (!this.privateNotifyDispatcher.isReady())
						{
							super.execute(this.privateNotifyDispatcher);
						}
						this.privateNotifyDispatcher.enqueue((PrivateNotification)notification);
						break;
						
					default:
						if (!this.notificationDispatcher.isReady())
						{
							super.execute(this.notificationDispatcher);
						}
//							this.notificationDispatcher.enqueue((EncryptedNotification)message.getMessage());
//							this.notificationDispatcher.enqueue((Notification)message.getMessage());
						this.notificationDispatcher.enqueue(notification);
						break;
				}
				break;
				
			case CSMessageType.REQUEST:
				log.info("REQUEST received @" + Calendar.getInstance().getTime());
				Request request = (Request)message.getMessage();
				switch (request.getApplicationID())
				{
					case CryAppID.SYMMETRIC_CRYPTO_SESSION_REQUEST:
						log.info("SYMMETRIC_CRYPTO_SESSION_REQUEST received @" + Calendar.getInstance().getTime());
						if (!this.symReqDispatcher.isReady())
						{
							super.execute(this.symReqDispatcher);
						}
						this.symReqDispatcher.enqueue(new SymmetricCryptoSessionStream(message.getOutStream(), message.getLock(), (SymmetricCryptoSessionRequest)message.getMessage()));
						break;
						
					case CryAppID.ASYMMETRIC_ENCRYPTED_REQUEST:
						log.info("ASYMMETRIC_ENCRYPTED_REQUEST received @" + Calendar.getInstance().getTime());
						if (!this.asymReqDispatcher.isReady())
						{
							super.execute(this.asymReqDispatcher);
						}
						this.asymReqDispatcher.enqueue(new AsymmetricEncryptedStream(message.getOutStream(), message.getLock(), (AsymmetricEncryptedRequest)message.getMessage()));
						break;
						
					case CryAppID.PUBLIC_CRYPTO_SESSION_REQUEST:
						log.info("PUBLIC_CRYPTO_SESSION_REQUEST received @" + Calendar.getInstance().getTime());
						if (!this.pubReqDispatcher.isReady())
						{
							super.execute(this.pubReqDispatcher);
						}
						this.pubReqDispatcher.enqueue(new PublicCryptoSessionStream(message.getOutStream(), message.getLock(), (PublicCryptoSessionRequest)message.getMessage()));
						break;
						
					case CryAppID.SIGNED_ASYMMETRIC_ENCRYPTED_REQUEST:
						log.info("SIGNED_ASYMMETRIC_ENCRYPTED_REQUEST received @" + Calendar.getInstance().getTime());
						if (!this.signedReqDispatcher.isReady())
						{
							super.execute(this.signedReqDispatcher);
						}
						this.signedReqDispatcher.enqueue(new SignedAsymmetricEncryptedStream(message.getOutStream(), message.getLock(), (SignedAsymmetricEncryptedRequest)message.getMessage()));
						break;

					case CryAppID.OWNERSHIP_REQUEST:
						log.info("OWNERSHIP_REQUEST received @" + Calendar.getInstance().getTime());
						if (!this.ownReqDispatcher.isReady())
						{
							super.execute(this.ownReqDispatcher);
						}
						this.ownReqDispatcher.enqueue(new OwnershipStream(message.getOutStream(), message.getLock(), (OwnershipRequest)message.getMessage()));
						break;
						
					case CryAppID.PRIVATE_REQUEST:
						log.info("PRIVATE_REQUEST received @" + Calendar.getInstance().getTime());
						if (!this.privateReqDispatcher.isReady())
						{
							super.execute(this.privateReqDispatcher);
						}
						this.privateReqDispatcher.enqueue(new PrivateStream(message.getOutStream(), message.getLock(), (PrivateRequest)message.getMessage()));
						break;
					
					default:
//						log.info("req AppID = " + req.getApplicationID());
//						log.info("req type = " + req.getType());
						if (!this.requestDispatcher.isReady())
						{
							super.execute(this.requestDispatcher);
						}
//						log.info("After if (!this.requestDispatcher.isReady()) ...");
//						this.requestDispatcher.enqueue(new EncryptedRequestStream(message.getOutStream(), message.getLock(), (EncryptedRequest)message.getMessage()));
						this.requestDispatcher.enqueue(new EncryptedRequestStream(message.getOutStream(), message.getLock(), request));
//						log.info("After requestDispatcher.enqueue ...");
						break;
				}
				break;
		}
		
	}

}
