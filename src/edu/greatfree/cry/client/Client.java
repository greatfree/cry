package edu.greatfree.cry.client;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import org.bouncycastle.util.encoders.Hex;
import org.greatfree.client.CSClient;
import org.greatfree.cluster.message.AdditionalChildrenRequest;
import org.greatfree.cluster.message.AdditionalChildrenResponse;
import org.greatfree.cluster.message.ClusterSizeRequest;
import org.greatfree.cluster.message.ClusterSizeResponse;
import org.greatfree.cluster.message.PartitionSizeRequest;
import org.greatfree.cluster.message.PartitionSizeResponse;
import org.greatfree.concurrency.SharedThreadPool;
import org.greatfree.data.ClientConfig;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.FutureExceptionHandler;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.container.p2p.message.PeerAddressRequest;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.message.PeerAddressResponse;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;
import org.greatfree.util.Builder;
import org.greatfree.util.IPAddress;

import edu.greatfree.cry.SymmetricCoder;
import edu.greatfree.cry.SymmetricCrypto;
import edu.greatfree.cry.messege.CryAppID;
import edu.greatfree.cry.messege.EncryptedNotification;
import edu.greatfree.cry.messege.EncryptedRequest;
import edu.greatfree.cry.messege.EncryptedResponse;
import edu.greatfree.cry.messege.SymmetricCryptoSessionRequest;
import edu.greatfree.cry.messege.SymmetricCryptoSessionResponse;

/**
 * 
 * @author libing
 * 
 * 01/06/2022, Bing Li
 *
 */
public class Client
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.client");

	private CSClient client;
	private SymmetricCrypto crypto;
	private IPAddress serverAddress;

	public Client(ClientBuilder builder) throws NoSuchAlgorithmException, ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
	{ 
		SharedThreadPool.SHARED().init(ServerConfig.SHARED_THREAD_POOL_SIZE, ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME);
		this.client = new CSClient.CSClientBuilder()
				.freeClientPoolSize(RegistryConfig.CLIENT_POOL_SIZE)
				.clientIdleCheckDelay(RegistryConfig.SYNC_EVENTER_IDLE_CHECK_DELAY)
				.clientIdleCheckPeriod(RegistryConfig.SYNC_EVENTER_IDLE_CHECK_PERIOD)
				.clientMaxIdleTime(RegistryConfig.SYNC_EVENTER_MAX_IDLE_TIME)
				.asyncEventQueueSize(RegistryConfig.ASYNC_EVENT_QUEUE_SIZE)
				.asyncEventerSize(RegistryConfig.ASYNC_EVENTER_SIZE)
				.asyncEventingWaitTime(RegistryConfig.ASYNC_EVENTING_WAIT_TIME)
//				.asyncEventerWaitTime(RegistryConfig.ASYNC_EVENTER_WAIT_TIME)
//				.asyncEventerWaitRound(RegistryConfig.ASYNC_EVENTER_WAIT_ROUND)
				.asyncEventQueueWaitTime(RegistryConfig.ASYNC_EVENT_QUEUE_WAIT_TIME)
				.asyncEventIdleCheckDelay(RegistryConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.asyncEventIdleCheckPeriod(RegistryConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(RegistryConfig.SCHEDULER_THREAD_POOL_SIZE)
				.schedulerKeepAliveTime(RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME)
				.asyncSchedulerShutdownTimeout(ClientConfig.ASYNC_SCHEDULER_SHUTDOWN_TIMEOUT)
				.readerClientSize(RegistryConfig.READER_CLIENT_SIZE)
				.readTimeoutExceptionHandler(builder.getFutureHandler())
				.pool(SharedThreadPool.SHARED().getPool())
				.build();
		
//		this.client.init(SharedThreadPool.SHARED().getPool());

//		this.crypto = SymmetricCoder.generateCrypto(builder.getCipherAlgorithm(), builder.getCipherSpecification(), builder.getCipherKeyLength(), builder.getIVKeyLength());
//		this.serverAddress = builder.getServerAddress();
//		this.sendCipherKey();
		this.reset(builder.getCipherAlgorithm(), builder.getCipherSpec(), builder.getCipherKeyLength(), builder.getIVKeyLength(), builder.getServerAddress());
	}
	
	public static class ClientBuilder implements Builder<Client>
	{
		private IPAddress serverAddress;
		private String cipherAlgorithm;
		private String cipherSpec;
		private int cipherKeyLength;
		private int ivKeyLength;
		private FutureExceptionHandler futureHandler;
		
		public ClientBuilder()
		{
		}
		
		public ClientBuilder serverAddress(IPAddress serverAddress)
		{
			this.serverAddress = serverAddress;
			return this;
		}
		
		public ClientBuilder cipherAlgorithm(String cipherAlgorithm)
		{
			this.cipherAlgorithm = cipherAlgorithm;
			return this;
		}
		
		public ClientBuilder cipherSpec(String cipherSpec)
		{
			this.cipherSpec = cipherSpec;
			return this;
		}
		
		public ClientBuilder cipherKeyLength(int cipherKeyLength)
		{
			this.cipherKeyLength = cipherKeyLength;
			return this;
		}
		
		public ClientBuilder ivKeyLength(int ivKeyLength)
		{
			this.ivKeyLength = ivKeyLength;
			return this;
		}
		
		public ClientBuilder futureHandler(FutureExceptionHandler futureHandler)
		{
			this.futureHandler = futureHandler;
			return this;
		}

		@Override
		public Client build() throws IOException
		{
			try
			{
				return new Client(this);
			}
			catch (NoSuchAlgorithmException | ClassNotFoundException | RemoteReadException | RemoteIPNotExistedException e)
			{
				e.printStackTrace();
			}
			return null;
		}

		public IPAddress getServerAddress()
		{
			return this.serverAddress;
		}
		
		public String getCipherAlgorithm()
		{
			return this.cipherAlgorithm;
		}
		
		public String getCipherSpec()
		{
			return this.cipherSpec;
		}
		
		public int getCipherKeyLength()
		{
			return this.cipherKeyLength;
		}
		
		public int getIVKeyLength()
		{
			return this.ivKeyLength;
		}
		
		public FutureExceptionHandler getFutureHandler()
		{
			return this.futureHandler;
		}
	}

	public void dispose() throws IOException, InterruptedException
	{
		if (this.client != null)
		{
			SharedThreadPool.SHARED().dispose(ServerConfig.SHARED_THREAD_POOL_SHUTDOWN_TIMEOUT);
			this.client.dispose();
		}
	}

	public synchronized boolean reset(String cipherAlgorithm, String cipherSpec, int cipherKeyLength, int ivKeyLength, IPAddress serverAddress) throws ClassNotFoundException, RemoteReadException, NoSuchAlgorithmException, RemoteIPNotExistedException
	{
//		this.crypto = SymmetricCoder.generateCrypto(Tools.generateUniqueKey(), Tools.generateUniqueKey(), cipherAlgorithm, cipherSpec, cipherKeyLength, ivKeyLength);
		this.crypto = SymmetricCoder.generateCrypto(cipherAlgorithm, cipherSpec, cipherKeyLength, ivKeyLength);
//		this.cryptos.put(crypto.getSessionKey(), crypto);
		this.serverAddress = serverAddress;
//		this.sendCipherKey();
//		this.currentSessionKey = crypto.getSessionKey();
//		return this.currentSessionKey;
		return((SymmetricCryptoSessionResponse)this.client.read(this.serverAddress.getIP(), this.serverAddress.getPort(), new SymmetricCryptoSessionRequest(this.crypto))).isDone();
	}

	public void syncNotify(Notification notification) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException, InterruptedException
	{
//		Crypto crypto = this.cryptos.get(sessionKey);
		byte[] enData = SymmetricCoder.encryptNotification(notification, this.crypto.getCipherKey(), this.crypto.getIVKey(), this.crypto.getCipherSpec());
		log.info("Encrypted Notification: " + Hex.toHexString(enData));

//		this.client.syncNotify(this.serverAddress.getIP(), this.serverAddress.getPort(), new EncryptedNotification(this.crypto.getSessionKey(), Coder.encryptNotification(notification, this.crypto.getCipherKey(), this.crypto.getIVKey(), this.crypto.getCipherSpecification())));
//		this.client.syncNotify(this.serverAddress.getIP(), this.serverAddress.getPort(), new EncryptedNotification(CryAppID.SYMMETRIC_ENCRYPTED_NOTIFICATION, this.crypto.getSessionKey(), enData));
		this.client.syncNotify(this.serverAddress.getIP(), this.serverAddress.getPort(), new EncryptedNotification(CryAppID.SYMMETRIC_ENCRYPTED_NOTIFICATION, this.crypto.getSourcePeerKey(), enData));
	}
	
	public void asyncNotify(Notification notification) throws InvalidKeyException, RejectedExecutionException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException
	{
//		this.client.asyncNotify(this.serverAddress.getIP(), this.serverAddress.getPort(), new EncryptedNotification(CryAppID.SYMMETRIC_ENCRYPTED_NOTIFICATION, this.crypto.getSessionKey(), SymmetricCoder.encryptNotification(notification, this.crypto.getCipherKey(), this.crypto.getIVKey(), this.crypto.getCipherSpec())));
		this.client.asyncNotify(this.serverAddress.getIP(), this.serverAddress.getPort(), new EncryptedNotification(CryAppID.SYMMETRIC_ENCRYPTED_NOTIFICATION, this.crypto.getSourcePeerKey(), SymmetricCoder.encryptNotification(notification, this.crypto.getCipherKey(), this.crypto.getIVKey(), this.crypto.getCipherSpec())));
	}
	
	public ServerMessage read(Request request) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, RemoteReadException, ShortBufferException, RemoteIPNotExistedException, IOException
	{
		byte[] enData = SymmetricCoder.encryptRequest(request, this.crypto.getCipherKey(), this.crypto.getIVKey(), this.crypto.getCipherSpec());
		log.info("Encrypted Request: " + Hex.toHexString(enData));

//		EncryptedResponse response = (EncryptedResponse)this.client.read(this.serverAddress.getIP(), this.serverAddress.getPort(), new EncryptedRequest(this.crypto.getSessionKey(), Coder.encryptRequest(request, this.crypto.getCipherKey(), this.crypto.getIVKey(), this.crypto.getCipherSpecification())));
//		EncryptedResponse response = (EncryptedResponse)this.client.read(this.serverAddress.getIP(), this.serverAddress.getPort(), new EncryptedRequest(CryAppID.SYMMETRIC_ENCRYPTED_REQUEST, this.crypto.getSessionKey(), enData));
		EncryptedResponse response = (EncryptedResponse)this.client.read(this.serverAddress.getIP(), this.serverAddress.getPort(), new EncryptedRequest(CryAppID.SYMMETRIC_ENCRYPTED_REQUEST, this.crypto.getSourcePeerKey(), enData));
		if (response != null)
		{
			log.info("Encrypted Response: " + Hex.toHexString(response.getEncryptedData()));
			return SymmetricCoder.decryptResponse(response.getEncryptedData(), this.crypto.getCipherKey(), this.crypto.getIVKey(), this.crypto.getCipherSpec());
		}
		return null;
	}
	
	public ServerMessage read(Request request, int timeout) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, RemoteReadException, ShortBufferException, IOException
	{
//		EncryptedResponse response = (EncryptedResponse)this.client.read(this.serverAddress.getIP(), this.serverAddress.getPort(), new EncryptedRequest(CryAppID.SYMMETRIC_ENCRYPTED_REQUEST, this.crypto.getSessionKey(), SymmetricCoder.encryptRequest(request, this.crypto.getCipherKey(), this.crypto.getIVKey(), this.crypto.getCipherSpec())), timeout);
		EncryptedResponse response = (EncryptedResponse)this.client.read(this.serverAddress.getIP(), this.serverAddress.getPort(), new EncryptedRequest(CryAppID.SYMMETRIC_ENCRYPTED_REQUEST, this.crypto.getSourcePeerKey(), SymmetricCoder.encryptRequest(request, this.crypto.getCipherKey(), this.crypto.getIVKey(), this.crypto.getCipherSpec())), timeout);
		if (response != null)
		{
			return SymmetricCoder.decryptResponse(response.getEncryptedData(), this.crypto.getCipherKey(), this.crypto.getIVKey(), this.crypto.getCipherSpec());
		}
		return null;
	}
	
	public Future<ServerMessage> futureRead(Request request) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException
	{
//		return this.client.futureRead(this.serverAddress.getIP(), this.serverAddress.getPort(), new EncryptedRequest(CryAppID.SYMMETRIC_ENCRYPTED_REQUEST, this.crypto.getSessionKey(), SymmetricCoder.encryptRequest(request, this.crypto.getCipherKey(), this.crypto.getIVKey(), this.crypto.getCipherSpec())));
		return this.client.futureRead(this.serverAddress.getIP(), this.serverAddress.getPort(), new EncryptedRequest(CryAppID.SYMMETRIC_ENCRYPTED_REQUEST, this.crypto.getSourcePeerKey(), SymmetricCoder.encryptRequest(request, this.crypto.getCipherKey(), this.crypto.getIVKey(), this.crypto.getCipherSpec())));
	}
	
	public Future<ServerMessage> futureRead(Request request, int timeout) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException
	{
//		return this.client.futureRead(this.serverAddress.getIP(), this.serverAddress.getPort(), new EncryptedRequest(CryAppID.SYMMETRIC_ENCRYPTED_REQUEST, this.crypto.getSessionKey(), SymmetricCoder.encryptRequest(request, this.crypto.getCipherKey(), this.crypto.getIVKey(), this.crypto.getCipherSpec())), timeout);
		return this.client.futureRead(this.serverAddress.getIP(), this.serverAddress.getPort(), new EncryptedRequest(CryAppID.SYMMETRIC_ENCRYPTED_REQUEST, this.crypto.getSourcePeerKey(), SymmetricCoder.encryptRequest(request, this.crypto.getCipherKey(), this.crypto.getIVKey(), this.crypto.getCipherSpec())), timeout);
	}

	/*
	private boolean sendCipherKey() throws ClassNotFoundException, RemoteReadException, IOException
	{
		return ((SymmetricCryptoSessionResponse)this.client.read(this.serverAddress.getIP(), this.serverAddress.getPort(), new SymmetricCryptoSessionRequest(this.crypto))).isDone();
	}
	*/

	/*
	 * The management method is NOT cryptographied. 01/07/2022, Bing Li
	 * 
	 * The method is common in many cases to access the IP of one node. 09/09/2020, Bing Li 
	 */
	public IPAddress getIPAddress(String registryIP, int registryPort, String nodeKey) throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
	{
		return ((PeerAddressResponse)this.client.read(registryIP,  registryPort, new PeerAddressRequest(nodeKey))).getPeerAddress();
	}

	/*
	 * The management method is NOT cryptographied. 01/07/2022, Bing Li
	 * 
	 * The method is useful for most storage systems, which need the partition information to design the upper level distribution strategy. 09/09/2020, Bing Li
	 */
	public int getPartitionSize(String clusterIP, int clusterPort) throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
	{
		return ((PartitionSizeResponse)this.client.read(clusterIP,  clusterPort, new PartitionSizeRequest())).getPartitionSize();
	}
	
	/*
	 * The management method is NOT cryptographied. 01/07/2022, Bing Li
	 * 
	 * The message is designed for the scalability such that all of the current children are replaced by new coming ones. In the storage system, the current ones is full in the disk space. In the case, they have to be replaced. But in other cases, it depends on the application level how to raise the scale and deal with the existing children. The system level cannot help. 09/12/2020, Bing Li
	 * 
	 * The message is an internal one, like the PartitionSizeRequest/PartitionSizeResponse, which is processed by the cluster root only. Programmers do not need to do anything but send it. So it inherits ServerMessage. 09/12/2020, Bing Li
	 */
	public int getClusterSize(String clusterIP, int clusterPort) throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
	{
		return ((ClusterSizeResponse)this.client.read(clusterIP,  clusterPort, new ClusterSizeRequest())).getSize();
	}

	/*
	 * The management method is NOT cryptographied. 01/07/2022, Bing Li
	 * 
	 * When additional children are needed by the task cluster, sometimes those children should be initialized or configured before joining. The method serves this goal. 09/13/2020, Bing Li
	 */
	public Set<String> getChildrenKeys(String clusterIP, int clusterPort, int size) throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
	{
		return ((AdditionalChildrenResponse)this.client.read(clusterIP, clusterPort, new AdditionalChildrenRequest(size))).getChildrenKeys();
	}
}
