package edu.greatfree.cry.server;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.SignatureException;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;
import org.greatfree.client.IPResource;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.PeerNameIsNullException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.framework.container.p2p.message.PeerAddressRequest;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.message.PeerAddressResponse;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;
import org.greatfree.server.container.PeerContainer;
import org.greatfree.server.container.PeerProfile;
import org.greatfree.server.container.ServerProfile;
import org.greatfree.server.container.ServerTask;
import org.greatfree.util.Builder;
import org.greatfree.util.IPAddress;
import org.greatfree.util.Rand;
import org.greatfree.util.TerminateSignal;

import edu.greatfree.cry.AsymCompCrypto;
import edu.greatfree.cry.AsymmetricCoder;
import edu.greatfree.cry.AsymmetricCrypto;
import edu.greatfree.cry.CryConfig;
import edu.greatfree.cry.PublicCrypto;
import edu.greatfree.cry.SymmetricCoder;
import edu.greatfree.cry.SymmetricCrypto;
import edu.greatfree.cry.exceptions.CheatingException;
import edu.greatfree.cry.exceptions.CryptographyMismatchException;
import edu.greatfree.cry.exceptions.IPNotExistedException;
import edu.greatfree.cry.exceptions.OwnerCheatingException;
import edu.greatfree.cry.exceptions.PeerNotRegisteredException;
import edu.greatfree.cry.exceptions.PublicKeyUnavailableException;
import edu.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import edu.greatfree.cry.messege.AbandonOwnershipRequest;
import edu.greatfree.cry.messege.AbandonOwnershipResponse;
import edu.greatfree.cry.messege.AllOwners;
import edu.greatfree.cry.messege.AsymmetricBye;
import edu.greatfree.cry.messege.AsymmetricEncryptedNotification;
import edu.greatfree.cry.messege.AsymmetricEncryptedRequest;
import edu.greatfree.cry.messege.AsymmetricEncryptedResponse;
import edu.greatfree.cry.messege.AsymmetricPrimitiveNotification;
import edu.greatfree.cry.messege.AsymmetricPrimitiveRequest;
import edu.greatfree.cry.messege.CryAppID;
import edu.greatfree.cry.messege.EncryptedNotification;
import edu.greatfree.cry.messege.EncryptedRequest;
import edu.greatfree.cry.messege.EncryptedResponse;
import edu.greatfree.cry.messege.OwnerInfo;
import edu.greatfree.cry.messege.OwnershipRequest;
import edu.greatfree.cry.messege.OwnershipResponse;
import edu.greatfree.cry.messege.PrivateNotification;
import edu.greatfree.cry.messege.PrivatePrimitiveNotification;
import edu.greatfree.cry.messege.PrivatePrimitiveRequest;
import edu.greatfree.cry.messege.PrivateRequest;
import edu.greatfree.cry.messege.PrivateResponse;
import edu.greatfree.cry.messege.PublicCryptoSessionRequest;
import edu.greatfree.cry.messege.PublicCryptoSessionResponse;
import edu.greatfree.cry.messege.SayAsymmetricByeNotification;
import edu.greatfree.cry.messege.SaySymmetricByeNotification;
import edu.greatfree.cry.messege.SignedAsymmetricEncryptedNotification;
import edu.greatfree.cry.messege.SignedAsymmetricEncryptedRequest;
import edu.greatfree.cry.messege.SignedAsymmetricEncryptedResponse;
import edu.greatfree.cry.messege.SignedPrimitiveNotification;
import edu.greatfree.cry.messege.SignedPrimitiveRequest;
import edu.greatfree.cry.messege.SymmetricBye;
import edu.greatfree.cry.messege.SymmetricCryptoSessionRequest;
import edu.greatfree.cry.messege.SymmetricCryptoSessionResponse;
import edu.greatfree.cry.messege.SymmetricPrimitiveNotification;
import edu.greatfree.cry.messege.SymmetricPrimitiveRequest;
import edu.greatfree.cry.multicast.MulticastConfig;

/**
 * 
 * @author libing
 * 
 * 01/11/2022, Bing Li
 *
 */
public final class Peer
{
	private final static Logger log = Logger.getLogger("edu.greatfree.cry.server");
	
	private org.greatfree.server.container.Peer<CryptoCSDispatcher> peer;
//	private String dsaAlgorithm;
	
//	private boolean isEncrypted;
	private AtomicBoolean isAsymCryptography;

	/*
	 * The symmetric cryptography parameter. 02/04/2022, Bing Li
	 */
//	private SymmetricCrypto symCrypto;
	private String symCipherAlgorithm;
	private String symCipherSpec;
	private int symCipherKeyLength;
	private int symIVKeyLength;

	/*
	 * The asymmetric cryptography parameters. 02/04/2022, Bing Li
	 */
	private String asymCipherAlgorithm;
	private int asymCipherKeyLength;

	public Peer(PeerBuilder builder) throws IOException, NoSuchAlgorithmException
	{
		Security.addProvider(new BouncyCastleProvider());
		CryptoCSDispatcher csd = new CryptoCSDispatcher(ServerConfig.SHARED_THREAD_POOL_SIZE, ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME);
		if (builder.getConfigXML() != null)
		{
//			log.info("1) Peer is initialized ...");
			
			PeerProfile.P2P().init(builder.getConfigXML());
			
			this.peer = new org.greatfree.server.container.Peer.PeerBuilder<CryptoCSDispatcher>()
					.peerPort(ServerProfile.CS().getPort())
					.peerName(PeerProfile.P2P().getPeerName())
					.registryServerIP(PeerProfile.P2P().getRegistryServerIP())
					.registryServerPort(PeerProfile.P2P().getRegistryServerPort())
					.isRegistryNeeded(PeerProfile.P2P().isRegistryNeeded())
					.listenerCount(ServerProfile.CS().getListeningThreadCount())
					.dispatcher(csd)
					.freeClientPoolSize(PeerProfile.P2P().getFreeClientPoolSize())
					.readerClientSize(PeerProfile.P2P().getReaderClientSize())
					.syncEventerIdleCheckDelay(PeerProfile.P2P().getSyncEventerIdleCheckDelay())
					.syncEventerIdleCheckPeriod(PeerProfile.P2P().getSyncEventerIdleCheckPeriod())
					.syncEventerMaxIdleTime(PeerProfile.P2P().getSyncEventerMaxIdleTime())
					.asyncEventQueueSize(PeerProfile.P2P().getAsyncEventQueueSize())
					.asyncEventerSize(PeerProfile.P2P().getAsyncEventerSize())
					.asyncEventingWaitTime(PeerProfile.P2P().getAsyncEventingWaitTime())
//					.asyncEventerWaitTime(PeerProfile.P2P().getAsyncEventerWaitTime())
					.asyncEventQueueWaitTime(PeerProfile.P2P().getAsyncEventQueueWaitTime())
//					.asyncEventerWaitRound(PeerProfile.P2P().getAsyncEventerWaitRound())
					.asyncEventIdleCheckDelay(PeerProfile.P2P().getAsyncEventIdleCheckDelay())
					.asyncEventIdleCheckPeriod(PeerProfile.P2P().getAsyncEventIdleCheckPeriod())
					.schedulerPoolSize(PeerProfile.P2P().getSchedulerPoolSize())
					.schedulerKeepAliveTime(PeerProfile.P2P().getSchedulerKeepAliveTime())
					.build();
		}
		else
		{
//			log.info("2) Peer is initialized ...");
			
			this.peer = new org.greatfree.server.container.Peer.PeerBuilder<CryptoCSDispatcher>()
					.peerPort(builder.getPort())
					.peerName(builder.getPeerName())
					.registryServerIP(builder.getRegistryServerIP())
					.registryServerPort(builder.getRegistryServerPort())
					.isRegistryNeeded(builder.isRegistryNeeded())
					.listenerCount(ServerConfig.LISTENING_THREAD_COUNT)
					.dispatcher(csd)
					.freeClientPoolSize(RegistryConfig.CLIENT_POOL_SIZE)
					.readerClientSize(RegistryConfig.READER_CLIENT_SIZE)
					.syncEventerIdleCheckDelay(RegistryConfig.SYNC_EVENTER_IDLE_CHECK_DELAY)
					.syncEventerIdleCheckPeriod(RegistryConfig.SYNC_EVENTER_IDLE_CHECK_PERIOD)
					.syncEventerMaxIdleTime(RegistryConfig.SYNC_EVENTER_MAX_IDLE_TIME)
					.asyncEventQueueSize(RegistryConfig.ASYNC_EVENT_QUEUE_SIZE)
					.asyncEventerSize(RegistryConfig.ASYNC_EVENTER_SIZE)
					.asyncEventingWaitTime(RegistryConfig.ASYNC_EVENTING_WAIT_TIME)
//					.asyncEventerWaitTime(RegistryConfig.ASYNC_EVENTER_WAIT_TIME)
					.asyncEventQueueWaitTime(RegistryConfig.ASYNC_EVENT_QUEUE_WAIT_TIME)
//					.asyncEventerWaitRound(RegistryConfig.ASYNC_EVENTER_WAIT_ROUND)
					.asyncEventIdleCheckDelay(RegistryConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
					.asyncEventIdleCheckPeriod(RegistryConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
					.schedulerPoolSize(RegistryConfig.SCHEDULER_THREAD_POOL_SIZE)
					.schedulerKeepAliveTime(RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME)
					.build();
		}
		
		csd.init();
//		this.dsaAlgorithm = builder.getDSAAlgorithm();

//		this.isEncrypted = builder.isEncrypted();
		
		this.isAsymCryptography = new AtomicBoolean(builder.isAsymCryptography());
		this.symCipherAlgorithm = builder.getSymCipherAlgorithm();
		this.symCipherSpec = builder.getSymCipherSpec();
		this.symCipherKeyLength = builder.getSymCipherKeyLength();
		this.symIVKeyLength = builder.getSymIVKeyLength();

//		if (this.isAsymCryptography)
//		{
		this.asymCipherAlgorithm = builder.getAsymCipherAlgorithm();
		this.asymCipherKeyLength = builder.getAsymCipherKeyLength();
	//		this.symCrypto = SymmetricCoder.generateCrypto(builder.getSymCipherAlgorithm(), builder.getSymCipherSpec(), builder.getSymCipherKeyLength(), builder.getSymIVKeyLength());

//			ServiceProvider.CRY().init(csd.getServerKey(), builder.getTask(), AsymmetricCoder.generateCrypto(builder.getAsymCipherAlgorithm(), builder.getAsymCipherKeyLength(), SymmetricCoder.generateCrypto(builder.getSymCipherAlgorithm(), builder.getSymCipherSpec(), builder.getSymCipherKeyLength(), builder.getSymIVKeyLength())));
		if (builder.getSignature() != null)
		{
			ServiceProvider.CRY().init(this.peer.getPeerName(), csd.getServerKey(), builder.getTask(), AsymmetricCoder.generateCrypto(builder.getAsymCipherAlgorithm(), builder.getAsymCipherKeyLength(), builder.getSymCipherAlgorithm(), builder.getSymCipherSpec(), builder.getSymCipherKeyLength(), builder.getSymIVKeyLength(), builder.getSignatureAlgorithm(), builder.getSignature()));
		}
		else
		{
			ServiceProvider.CRY().init(this.peer.getPeerName(), csd.getServerKey(), builder.getTask(), AsymmetricCoder.generateCrypto(builder.getAsymCipherAlgorithm(), builder.getAsymCipherKeyLength(), builder.getSymCipherAlgorithm(), builder.getSymCipherSpec(), builder.getSymCipherKeyLength(), builder.getSymIVKeyLength()));
		}
		
		ServiceProvider.CRY().setPrivate(builder.isPrivate());
		if (builder.isPrivate())
		{
			ServiceProvider.CRY().setOwnersSize(builder.getOwnersSize());
		}
		else
		{
			ServiceProvider.CRY().setOwnersSize(CryConfig.NO_OWNER_SIZE);
		}
//		}
//		else
//		{
//			this.symCrypto = SymmetricCoder.generateCrypto(builder.getSymCipherAlgorithm(), builder.getSymCipherSpec(), builder.getSymCipherKeyLength(), builder.getSymIVKeyLength());
//			ServiceProvider.CRY().init(csd.getServerKey(), builder.getTask());
//		}
//		ServiceProvider.CRY().init(csd.getServerKey(), builder.getTask());
//		this.isServerDisabled = new AtomicBoolean(builder.isServerDisabled());
//		this.isClientDisabled = new AtomicBoolean(builder.isClientDisabled());
	}
	
	public static class PeerBuilder implements Builder<Peer>
	{
		/*
		 * Most typical Peer (non-container pPeer) parameters are encapsulated. 04/24/2022, Bing Li
		 */
		private String peerName;
		private int port;
		private String registryServerIP;
		private int registryServerPort;
		private ServerTask task;
//		private MulticastTask task;
		private boolean isRegistryNeeded;
		
		private boolean isEncrypted;

		/*
		 * The below parameters regulate the approaches of cryptography. 02/04/2022, Bing Li
		 */
		private boolean isAsymCryptography;
		
		private String symCipherAlgorithm;
		private String symCipherSpec;
		private int symCipherKeyLength;
		private int symIVKeyLength;

//		private String dsaAlgorithm;
		private String asymCipherAlgorithm;
		private int asymCipherKeyLength;
		private String signatureAlgorithm;
		private String signature;
		
		private boolean isPrivate;
		private int ownersSize;

		private String configXML;
		
		public PeerBuilder()
		{
		}
		
		public PeerBuilder peerName(String peerName)
		{
			this.peerName = peerName;
			return this;
		}
		
		public PeerBuilder port(int port)
		{
			this.port = port;
			return this;
		}
		
		public PeerBuilder registryServerIP(String registryServerIP)
		{
			this.registryServerIP = registryServerIP;
			return this;
		}
		
		public PeerBuilder registryServerPort(int registryServerPort)
		{
			this.registryServerPort = registryServerPort;
			return this;
		}
		
//		public PeerBuilder task(MulticastTask task)
		public PeerBuilder task(ServerTask task)
		{
			this.task = task;
			return this;
		}
		
		public PeerBuilder isRegistryNeeded(boolean isRegistryNeeded)
		{
			this.isRegistryNeeded = isRegistryNeeded;
			return this;
		}

		/*
		public PeerBuilder dsaAlgorithm(String dsaAlgorithm)
		{
			this.dsaAlgorithm = dsaAlgorithm;
			return this;
		}
		*/

		public PeerBuilder isEncrypted(boolean isEncrypted)
		{
			this.isEncrypted = isEncrypted;
			return this;
		}

		public PeerBuilder isAsymCryptography(boolean isAsymCryptography)
		{
			this.isAsymCryptography = isAsymCryptography;
			return this;
		}

		public PeerBuilder symCipherAlgorithm(String symCipherAlgorithm)
		{
			this.symCipherAlgorithm = symCipherAlgorithm;
			return this;
		}

		public PeerBuilder symCipherSpec(String symCipherSpec)
		{
			this.symCipherSpec = symCipherSpec;
			return this;
		}

		public PeerBuilder symCipherKeyLength(int symCipherKeyLength)
		{
			this.symCipherKeyLength = symCipherKeyLength;
			return this;
		}

		public PeerBuilder symIVKeyLength(int symIVKeyLength)
		{
			this.symIVKeyLength = symIVKeyLength;
			return this;
		}

		public PeerBuilder asymCipherAlgorithm(String asymCipherAlgorithm)
		{
			this.asymCipherAlgorithm = asymCipherAlgorithm;
			return this;
		}
		
		public PeerBuilder asymCipherKeyLength(int asymCipherKeyLength)
		{
			this.asymCipherKeyLength = asymCipherKeyLength;
			return this;
		}

		public PeerBuilder signatureAlgorithm(String signatureAlgorithm)
		{
			this.signatureAlgorithm = signatureAlgorithm;
			return this;
		}
		
		public PeerBuilder signature(String signature)
		{
			this.signature = signature;
			return this;
		}

		public PeerBuilder isPrivate(boolean isPrivate)
		{
			this.isPrivate = isPrivate;
			return this;
		}

		public PeerBuilder ownersSize(int ownersSize)
		{
			this.ownersSize = ownersSize;
			return this;
		}

		public PeerBuilder configXML(String configXML)
		{
			this.configXML = configXML;
			return this;
		}

		@Override
		public Peer build() throws IOException
		{
			try
			{
				return new Peer(this);
			}
			catch (NoSuchAlgorithmException | IOException e)
			{
				e.printStackTrace();
			}
			return null;
		}
		
		public int getPort()
		{
			return this.port;
		}

		public String getRegistryServerIP()
		{
			return this.registryServerIP;
		}
		
		public int getRegistryServerPort()
		{
			return this.registryServerPort;
		}
		
//		public MulticastTask getTask()
		public ServerTask getTask()
		{
			return this.task;
		}
		
		public boolean isRegistryNeeded()
		{
			return this.isRegistryNeeded;
		}

		/*
		public String getDSAAlgorithm()
		{
			return this.dsaAlgorithm;
		}
		*/
		
		public boolean isEncrypted()
		{
			return this.isEncrypted;
		}
		
		public boolean isAsymCryptography()
		{
			return this.isAsymCryptography;
		}
		
		public String getSymCipherAlgorithm()
		{
			return this.symCipherAlgorithm;
		}
		
		public String getSymCipherSpec()
		{
			return this.symCipherSpec;
		}
		
		public int getSymCipherKeyLength()
		{
			return this.symCipherKeyLength;
		}
		
		public int getSymIVKeyLength()
		{
			return this.symIVKeyLength;
		}
		
		public String getAsymCipherAlgorithm()
		{
			return this.asymCipherAlgorithm;
		}
		
		public int getAsymCipherKeyLength()
		{
			return this.asymCipherKeyLength;
		}

		public String getPeerName()
		{
			return this.peerName;
		}
		
		public String getSignatureAlgorithm()
		{
			return this.signatureAlgorithm;
		}
		
		public String getSignature()
		{
			return this.signature;
		}
		
		public boolean isPrivate()
		{
			return this.isPrivate;
		}
		
		public int getOwnersSize()
		{
			return this.ownersSize;
		}
		
		public String getConfigXML()
		{
			return this.configXML;
		}
	}
	
	public void stop(long timeout) throws ClassNotFoundException, InterruptedException, RemoteReadException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException, SymmetricKeyUnavailableException, PeerNotRegisteredException, RemoteIPNotExistedException, IOException, PeerNameIsNullException
	{
		Collection<String> partners = ServiceProvider.CRY().getAllPeerNames();
		log.info("partners' size = " + partners.size());
		for (String entry : partners)
		{
			if (this.isAsymCryptography.get())
			{
//				this.syncNotifyAsymmetrically(entry, new SayAsymmetricByeNotification(this.peer.getPeerName(), this.peer.getPeerID(), ServiceProvider.CRY().getAsymmetricSessionKey(), ServiceProvider.CRY().getSignature()));
				try
				{
					this.syncNotifyAsymBye(entry, new AsymmetricBye(this.peer.getPeerName(), this.peer.getPeerID(), ServiceProvider.CRY().getAsymmetricSessionKey(), ServiceProvider.CRY().getSignature()));
				}
				catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException
						| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException
						| RemoteReadException | IOException | InterruptedException | PublicKeyUnavailableException
						| DistributedNodeFailedException e)
				{
					log.info("Remote parnters are down!");
				}
			}
			else
			{
//				this.syncNotifySymmetrically(entry, new SaySymmetricByeNotification(this.peer.getPeerName(), this.peer.getPeerID()));
				try
				{
					this.syncNotifySymBye(entry, new SymmetricBye(this.peer.getPeerName(), this.peer.getPeerID()));
				}
				catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException
						| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException
						| RemoteReadException | IOException | InterruptedException | PublicKeyUnavailableException
						| DistributedNodeFailedException | SymmetricKeyUnavailableException e)
				{
					log.info("Remote parnters are down!");
				}
			}
		}
		log.info("Notifying partners done!");
		TerminateSignal.SIGNAL().notifyAllTermination();
		this.peer.stop(timeout);
		log.info("Peer stopping done!");
	}
	
	public void start() throws ClassNotFoundException, RemoteReadException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		this.peer.start();
	}

	/*
	public boolean isServerDisabled()
	{
		return this.isServerDisabled.get();
	}
	
	public boolean isClientDisabled()
	{
		return this.isClientDisabled.get();
	}
	*/
	
	public boolean isRegistered()
	{
		return this.peer.isRegistered();
	}
	
	public String getPeerID()
	{
		return this.peer.getPeerID();
	}
	
	public String getPeerName()
	{
		return this.peer.getPeerName();
	}
	
	public void setPeerName(String peerName)
	{
		this.peer.setPeerName(peerName);
	}
	
	public IPAddress getPeerIPAddress()
	{
		return new IPAddress(this.peer.getPeerIP(), this.peer.getPort());
	}
	
	public String getPeerIP()
	{
		return this.peer.getPeerIP();
	}
	
	public int getPort()
	{
		return this.peer.getPort();
	}
	
	public String getLocalIPKey()
	{
		return this.peer.getLocalIPKey();
	}
	
	public ThreadPool getPool()
	{
		return this.peer.getPool();
	}
	
	public void clearIPs()
	{
		this.peer.getClientPool().clearAll();
	}
	
	public String getRegistryServerIP()
	{
		return this.peer.getRegistryServerIP();
	}
	
	public int getRegistryServerPort()
	{
		return this.peer.getRegistryServerPort();
	}
	
//	public void addPartners(String peerKey, String peerName, String ip, int port)
	public void addPartners(String ip, int port)
	{
//		this.peer.addPartners(peerKey, peerName, ip, port);
		this.peer.addPartners(ip, port);
	}
	
	public void removePartner(String ipKey) throws IOException
	{
		this.peer.removePartner(ipKey);
	}
	
	public void addPeer(String peerName, IPAddress ip)
	{
		ServiceProvider.CRY().addIP(peerName, ip);
	}
	
	public Set<String> getClientKeys()
	{
		return this.peer.getClientPool().getClientKeys();
	}
	
	public void removeClient(String clientKey) throws IOException
	{
		this.peer.getClientPool().removeClient(clientKey);
	}
	
	public Set<String> getClientKeys(int n)
	{
		Set<String> childrenKeys = this.peer.getClientPool().getClientKeys();
		if (n >= childrenKeys.size())
		{
			return childrenKeys;
		}
		else
		{
			return Rand.getRandomSet(childrenKeys, n);
		}
	}
	
	public IPAddress getIPAddressByKey(String ipKey)
	{
		return this.peer.getClientPool().getIPAddress(ipKey);
	}
	
	public String getPartnerName(String ipKey) throws IPNotExistedException
	{
		return ServiceProvider.CRY().getPartnerName(ipKey);
	}
	
	public int getClientSize()
	{
		return this.peer.getClientPool().getClientSourceSize();
	}
	
	public synchronized boolean isAsymCryptography()
	{
		return this.isAsymCryptography.get() && ServiceProvider.CRY().getSignature() == null;
	}
	
	public void setAsAsym()
	{
		if (!this.isAsymCryptography.get())
		{
			this.isAsymCryptography.set(true);
		}
	}
	
	public void setAsSym()
	{
		if (this.isAsymCryptography.get())
		{
			this.isAsymCryptography.set(false);
		}
	}

	/*
	public synchronized boolean isEncrypted()
	{
		return this.isEncrypted;
	}
	*/
	
	public synchronized boolean isSigned()
	{
		return ServiceProvider.CRY().getSignature() != null;
	}
	
	public boolean isSymCryptography()
	{
		return !this.isAsymCryptography.get();
	}
	
	public boolean isSymPartnerInvited(String partnerName)
	{
		return ServiceProvider.CRY().isSymPartnerInvited(PeerContainer.getPeerKey(partnerName));
	}
	
	public boolean isAsymPartnerInvited(String partnerName)
	{
		return ServiceProvider.CRY().isAsymPartnerInvited(PeerContainer.getPeerKey(partnerName));
	}
	
	public synchronized void resetCrypto(boolean isAsymCryptography)
	{
		this.isAsymCryptography.set(isAsymCryptography);
	}
	
//	public synchronized boolean reset(String asymmCipherAlgorithm, int asymmCipherKeyLength, String partnerName, String symCipherAlgorithm, String symCipherSpec, int symCipherKeyLength, int symIVKeyLength) throws NoSuchAlgorithmException, ClassNotFoundException, RemoteReadException, IOException
	public synchronized void resetAsym(String asymmCipherAlgorithm, int asymmCipherKeyLength, String symCipherAlgorithm, String symCipherSpec, int symCipherKeyLength, int symIVKeyLength) throws NoSuchAlgorithmException, ClassNotFoundException, RemoteReadException, IOException, CryptographyMismatchException
	{
		if (this.isAsymCryptography.get())
		{
			this.asymCipherAlgorithm = asymmCipherAlgorithm;
			this.asymCipherKeyLength = asymmCipherKeyLength;
			this.symCipherAlgorithm = symCipherAlgorithm;
			this.symCipherSpec = symCipherSpec;
			this.symCipherKeyLength = symCipherKeyLength;
			this.symIVKeyLength = symIVKeyLength;
//			AsymmetricCrypto crypto = AsymmetricCoder.generateCrypto(asymmCipherAlgorithm, asymmCipherKeyLength, SymmetricCoder.generateCrypto(symCipherAlgorithm, symCipherSpec, symCipherKeyLength, symIVKeyLength));
			AsymmetricCrypto crypto = AsymmetricCoder.generateCrypto(asymmCipherAlgorithm, asymmCipherKeyLength, symCipherAlgorithm, symCipherSpec, symCipherKeyLength, symIVKeyLength);
			ServiceProvider.CRY().reset(crypto);
			/*
			String peerKey = PeerContainer.getPeerKey(partnerName);
			IPAddress ip = this.getIPAddress(peerKey);
			if (ip != null)
			{
	//			return ((PublicCryptoSessionResponse)this.peer.read(ip.getIP(), ip.getPort(), new PublicCryptoSessionRequest(new PublicCrypto(crypto.getSessionKey(), this.peer.getPeerID(), crypto.getAsymAlgorithm(), crypto.getPublicKey()), crypto.getSymCrypto()))).isDone();
				return ((PublicCryptoSessionResponse)this.peer.read(ip.getIP(), ip.getPort(), new PublicCryptoSessionRequest(new PublicCrypto(crypto.getSessionKey(), this.peer.getPeerID(), crypto.getAsymAlgorithm(), crypto.getPublicKey())))).isDone();
			}
			return false;
			*/
		}
		else
		{
			throw new CryptographyMismatchException(CryConfig.SYMMETRIC_CRYPTOGRAPHY);
		}
	}

//	public synchronized boolean reset(String partnerName) throws NoSuchAlgorithmException, ClassNotFoundException, RemoteReadException, IOException
	public synchronized void resetAsym() throws NoSuchAlgorithmException, ClassNotFoundException, RemoteReadException, IOException, CryptographyMismatchException
	{
		if (this.isAsymCryptography.get())
		{
//			AsymmetricCrypto crypto = AsymmetricCoder.generateCrypto(this.asymCipherAlgorithm, this.asymCipherKeyLength, SymmetricCoder.generateCrypto(this.symCipherAlgorithm, this.symCipherSpec, this.symCipherKeyLength, this.symIVKeyLength));
			AsymmetricCrypto crypto = AsymmetricCoder.generateCrypto(this.asymCipherAlgorithm, this.asymCipherKeyLength, this.symCipherAlgorithm, this.symCipherSpec, this.symCipherKeyLength, this.symIVKeyLength);
			ServiceProvider.CRY().reset(crypto);
			/*
			String peerKey = PeerContainer.getPeerKey(partnerName);
			IPAddress ip = this.getIPAddress(peerKey);
			if (ip != null)
			{
				log.info("partner IP = " + ip);
				log.info("host peerKey = " + this.peer.getPeerID());
//				return ((PublicCryptoSessionResponse)this.peer.read(ip.getIP(), ip.getPort(), new PublicCryptoSessionRequest(new PublicCrypto(crypto.getSessionKey(), this.peer.getPeerID(), crypto.getAsymAlgorithm(), crypto.getPublicKey()), crypto.getSymCrypto()))).isDone();
				return ((PublicCryptoSessionResponse)this.peer.read(ip.getIP(), ip.getPort(), new PublicCryptoSessionRequest(new PublicCrypto(crypto.getSessionKey(), this.peer.getPeerID(), crypto.getAsymAlgorithm(), crypto.getPublicKey())))).isDone();
			}
			return false;
			*/
		}
		else
		{
			throw new CryptographyMismatchException(CryConfig.SYMMETRIC_CRYPTOGRAPHY);
		}
	}

	public synchronized void resetWithSignature(String asymmCipherAlgorithm, int asymmCipherKeyLength, String symCipherAlgorithm, String symCipherSpec, int symCipherKeyLength, int symIVKeyLength, String signatureAlgorithm, String signature) throws NoSuchAlgorithmException, ClassNotFoundException, RemoteReadException, IOException, CryptographyMismatchException
	{
		if (this.isAsymCryptography.get())
		{
			this.asymCipherAlgorithm = asymmCipherAlgorithm;
			this.asymCipherKeyLength = asymmCipherKeyLength;
			this.symCipherAlgorithm = symCipherAlgorithm;
			this.symCipherSpec = symCipherSpec;
			this.symCipherKeyLength = symCipherKeyLength;
			this.symIVKeyLength = symIVKeyLength;
			AsymmetricCrypto crypto = AsymmetricCoder.generateCrypto(asymmCipherAlgorithm, asymmCipherKeyLength, symCipherAlgorithm, symCipherSpec, symCipherKeyLength, symIVKeyLength, signatureAlgorithm, signature);
			ServiceProvider.CRY().reset(crypto);
		}
		else
		{
			throw new CryptographyMismatchException(CryConfig.SYMMETRIC_CRYPTOGRAPHY);
		}
	}

	public synchronized void resetWithSignature() throws NoSuchAlgorithmException, ClassNotFoundException, RemoteReadException, IOException, CryptographyMismatchException
	{
		if (this.isAsymCryptography.get())
		{
			AsymmetricCrypto crypto = AsymmetricCoder.generateCrypto(this.asymCipherAlgorithm, this.asymCipherKeyLength, this.symCipherAlgorithm, this.symCipherSpec, this.symCipherKeyLength, this.symIVKeyLength, ServiceProvider.CRY().getSignatureAlgorithm(), ServiceProvider.CRY().getSignature());
			ServiceProvider.CRY().reset(crypto);
		}
		else
		{
			throw new CryptographyMismatchException(CryConfig.SYMMETRIC_CRYPTOGRAPHY);
		}
	}
	
	public synchronized void resetSignature(String signature) throws CryptographyMismatchException
	{
		if (this.isAsymCryptography.get())
		{
			ServiceProvider.CRY().setSignature(signature + CryConfig.SIGNATURE_SUFFIX);
		}
		else
		{
			throw new CryptographyMismatchException(CryConfig.SYMMETRIC_CRYPTOGRAPHY);
		}
	}
	
	/*
	public synchronized void resetSym(String cipherAlgorithm, String cipherSpec, int cipherKeyLength, int ivKeyLength) throws NoSuchAlgorithmException, CryptographyMismatchException
	{
		if (!this.isAsymCryptography)
		{
			this.symCrypto = SymmetricCoder.generateCrypto(cipherAlgorithm, cipherSpec, cipherKeyLength, ivKeyLength);
		}
		throw new CryptographyMismatchException(CryConfig.ASYMMETRIC_CRYPTOGRAPHY);
	}
	*/
	
	public synchronized void inviteSymPartner(String partnerName) throws ClassNotFoundException, RemoteReadException, DistributedNodeFailedException, CryptographyMismatchException, NoSuchAlgorithmException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		if (!this.isAsymCryptography.get())
		{
			String peerKey = PeerContainer.getPeerKey(partnerName);
			if (!ServiceProvider.CRY().isSymmetricKeyExisted(peerKey))
			{
//				IPAddress ip = this.getIPAddress(peerKey);
				IPAddress ip = this.getIPAddress(partnerName);
				if (ip != null)
				{
//					SymmetricCrypto symCrypto = SymmetricCoder.generateCrypto(this.symCipherAlgorithm, this.symCipherSpec, this.symCipherKeyLength, this.symIVKeyLength);
					SymmetricCrypto symCrypto = SymmetricCoder.generateCrypto(this.peer.getPeerID(), peerKey, this.symCipherAlgorithm, this.symCipherSpec, this.symCipherKeyLength, this.symIVKeyLength);
					if (((SymmetricCryptoSessionResponse)this.peer.read(ip.getIP(), ip.getPort(), new SymmetricCryptoSessionRequest(this.peer.getPeerID(), symCrypto))).isDone())
					{
						log.info("Symmetric Crypto retained ...");
//						ServiceProvider.CRY().retainSymmetricCrypto(peerKey, symCrypto);
						ServiceProvider.CRY().retainSymmetricCrypto(symCrypto);
//						ServiceProvider.CRY().retainSymmetricCrypto(symCrypto);
					}
					else
					{
						log.info("Symmetric Crypto not received ...");
					}
				}
				else
				{
					throw new DistributedNodeFailedException(partnerName);
				}
			}
		}
		else
		{
			throw new CryptographyMismatchException(CryConfig.ASYMMETRIC_CRYPTOGRAPHY);
		}
	}

	public synchronized void inviteAsymPartner(String partnerName) throws NoSuchAlgorithmException, ClassNotFoundException, RemoteReadException, IOException, InvalidKeyException, SignatureException, DistributedNodeFailedException, CryptographyMismatchException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		if (this.isAsymCryptography.get())
		{
			log.info("Inviting " + partnerName);
//			AsymmetricCrypto crypto = AsymmetricCoder.generateCrypto(this.asymCipherAlgorithm, this.asymCipherKeyLength, SymmetricCoder.generateCrypto(this.symCipherAlgorithm, this.symCipherSpec, this.symCipherKeyLength, this.symIVKeyLength));
//			AsymmetricCrypto crypto = AsymmetricCoder.generateCrypto(this.asymCipherAlgorithm, this.asymCipherKeyLength, this.symCipherAlgorithm, this.symCipherSpec, this.symCipherKeyLength, this.symIVKeyLength);
//			ServiceProvider.CRY().reset(crypto);
			String peerKey = PeerContainer.getPeerKey(partnerName);
			if (!ServiceProvider.CRY().isPartnerExisted(peerKey))
			{
//				IPAddress ip = this.getIPAddress(peerKey);
				IPAddress ip = this.getIPAddress(partnerName);
				if (ip != null)
				{
//					log.info("partner IP = " + ip);
//					log.info("host peerKey = " + this.peer.getPeerID());
//					return ((PublicCryptoSessionResponse)this.peer.read(ip.getIP(), ip.getPort(), new PublicCryptoSessionRequest(new PublicCrypto(crypto.getSessionKey(), this.peer.getPeerID(), crypto.getAsymAlgorithm(), crypto.getPublicKey()), crypto.getSymCrypto()))).isDone();
//					return ((PublicCryptoSessionResponse)this.peer.read(ip.getIP(), ip.getPort(), new PublicCryptoSessionRequest(this.peer.getPeerName(), new PublicCrypto(ServiceProvider.CRY().getAsymmetricSessionKey(), this.peer.getPeerID(), ServiceProvider.CRY().getAsymmetricAlgorithm(), ServiceProvider.CRY().getPublicKey())))).isDone();
					PublicCryptoSessionResponse response;
					if (ServiceProvider.CRY().getSignature() != null)
					{
						byte[] signedInfo = AsymmetricCoder.sign(ServiceProvider.CRY().getSignatureAlgorithm(), ServiceProvider.CRY().getPrivateKey(), ServiceProvider.CRY().getSignature());
						response = (PublicCryptoSessionResponse)this.peer.read(ip.getIP(), ip.getPort(), new PublicCryptoSessionRequest(this.peer.getPeerName(), new PublicCrypto(ServiceProvider.CRY().getAsymmetricSessionKey(), this.peer.getPeerID(), ServiceProvider.CRY().getAsymmetricAlgorithm(), ServiceProvider.CRY().getPublicKey(), ServiceProvider.CRY().getSignatureAlgorithm(), ServiceProvider.CRY().getSignature()), signedInfo));
						if (response.isDone())
						{
							ServiceProvider.CRY().retainPublicCrypto(response.getPublicCrypto());
							log.info("The partner, " + partnerName + ", accepted your invitation!");
						}
						else
						{
							log.info("The partner, " + partnerName + ", did not accept your invitation!");
						}
					}
					else
					{
						response = (PublicCryptoSessionResponse)this.peer.read(ip.getIP(), ip.getPort(), new PublicCryptoSessionRequest(this.peer.getPeerName(), new PublicCrypto(ServiceProvider.CRY().getAsymmetricSessionKey(), this.peer.getPeerID(), ServiceProvider.CRY().getAsymmetricAlgorithm(), ServiceProvider.CRY().getPublicKey())));
						// For updates in later versions, the option is required to be set to avoid exceptions. 02/05/2023, Bing Li
						log.info("response.getPublicCrypto() = " + response.getPublicCrypto());
						ServiceProvider.CRY().retainPublicCrypto(response.getPublicCrypto());
					}
				}
				else
				{
					log.info(partnerName + "'s IP is not found!");
					throw new DistributedNodeFailedException(partnerName);
				}
			}
		}
		else
		{
			throw new CryptographyMismatchException(CryConfig.SYMMETRIC_CRYPTOGRAPHY);
		}
	}
	
//	public void syncNotify(String partnerName, Notification notification) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException, DistributedNodeFailedException
	public void syncNotify(String partnerName, ServerMessage notification) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException, DistributedNodeFailedException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
//		String peerKey = PeerContainer.getPeerKey(partnerName);
//		IPAddress ip = this.getIPAddress(peerKey);
		IPAddress ip = this.getIPAddress(partnerName);
		if (ip != null)
		{
//			log.info("notification type = " + notification.getType());
			this.peer.syncNotify(ip.getIP(), ip.getPort(), notification);
		}
		else
		{
			throw new DistributedNodeFailedException(partnerName);
		}
	}

//	private void syncNotifyByIPKey(String ipKey, Notification notification) throws IOException
	private void syncNotifyByIPKey(String ipKey, ServerMessage notification) throws IOException
	{
		this.peer.syncNotify(ipKey, notification);
	}
	
	public void syncNotifyByIP(IPResource ip, Notification notification) throws IOException, InterruptedException
	{
		this.peer.syncNotify(ip.getIP(), ip.getPort(), notification);
	}

	public void syncNotifyByIP(String ip, int port, Notification notification) throws IOException, InterruptedException
	{
		this.peer.syncNotify(ip, port, notification);
	}

	public void syncNotifyByIP(String ip, int port, ServerMessage notification) throws IOException, InterruptedException
	{
		this.peer.syncNotify(ip, port, notification);
	}

//	public void syncNotifySymmetrically(String partnerName, Notification notification) throws ClassNotFoundException, RemoteReadException, IOException, CryptographyMismatchException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InterruptedException, DistributedNodeFailedException, SymmetricKeyUnavailableException
	public void syncNotifySymmetrically(String partnerName, ServerMessage notification) throws ClassNotFoundException, RemoteReadException, IOException, CryptographyMismatchException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InterruptedException, DistributedNodeFailedException, SymmetricKeyUnavailableException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
	if (!this.isAsymCryptography.get())
		{
			String peerKey = PeerContainer.getPeerKey(partnerName);
//			IPAddress ip = this.getIPAddress(peerKey);
			IPAddress ip = this.getIPAddress(partnerName);
			if (ip != null)
			{
				SymmetricCrypto symCrypto = ServiceProvider.CRY().getSymmetricCrypto(peerKey);
				if (symCrypto != null)
				{
//					byte[] enData = SymmetricCoder.encryptNotification(notification, symCrypto.getCipherKey(), symCrypto.getIVKey(), symCrypto.getCipherSpec());
					byte[] enData = SymmetricCoder.encryptMessage(notification, symCrypto.getCipherKey(), symCrypto.getIVKey(), symCrypto.getCipherSpec());
					log.info("Encrypted Notification: " + Hex.toHexString(enData));
//					this.peer.syncNotify(ip.getIP(), ip.getPort(), new EncryptedNotification(CryAppID.SYMMETRIC_ENCRYPTED_NOTIFICATION, symCrypto.getSessionKey(), enData));
					this.peer.syncNotify(ip.getIP(), ip.getPort(), new EncryptedNotification(CryAppID.SYMMETRIC_ENCRYPTED_NOTIFICATION, this.peer.getPeerID(), enData));
				}
				else
				{
					throw new SymmetricKeyUnavailableException(partnerName);
				}
			}
			else
			{
				throw new DistributedNodeFailedException(partnerName);
			}
		}
		else
		{
			throw new CryptographyMismatchException(CryConfig.ASYMMETRIC_CRYPTOGRAPHY);
		}
	}

	public void syncPrmNotifySymmetrically(String partnerName, ServerMessage notification) throws ClassNotFoundException, RemoteReadException, IOException, CryptographyMismatchException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InterruptedException, DistributedNodeFailedException, SymmetricKeyUnavailableException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
	if (!this.isAsymCryptography.get())
		{
			String peerKey = PeerContainer.getPeerKey(partnerName);
			IPAddress ip = this.getIPAddress(partnerName);
			if (ip != null)
			{
				SymmetricCrypto symCrypto = ServiceProvider.CRY().getSymmetricCrypto(peerKey);
				if (symCrypto != null)
				{
					byte[] enData = SymmetricCoder.encryptMessage(notification, symCrypto.getCipherKey(), symCrypto.getIVKey(), symCrypto.getCipherSpec());
					log.info("Encrypted Notification: " + Hex.toHexString(enData));
					this.peer.syncNotify(ip.getIP(), ip.getPort(), new SymmetricPrimitiveNotification(this.peer.getPeerID(), enData));
				}
				else
				{
					throw new SymmetricKeyUnavailableException(partnerName);
				}
			}
			else
			{
				throw new DistributedNodeFailedException(partnerName);
			}
		}
		else
		{
			throw new CryptographyMismatchException(CryConfig.ASYMMETRIC_CRYPTOGRAPHY);
		}
	}

//	private void syncNotifySymmetricallyByIPKey(String ipKey, Notification notification) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException, InterruptedException, SymmetricKeyUnavailableException, DistributedNodeFailedException, CryptographyMismatchException
	private void syncNotifySymmetricallyByIPKey(String partnerName, String ipKey, ServerMessage notification) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException, InterruptedException, SymmetricKeyUnavailableException, DistributedNodeFailedException, CryptographyMismatchException
	{
		if (!this.isAsymCryptography.get())
		{
//			String partnerName = ServiceProvider.CRY().getPartnerName(ipKey);
			IPAddress ip = this.getIPAddressByKey(ipKey);
			if (ip != null)
			{
				SymmetricCrypto symCrypto = ServiceProvider.CRY().getSymmetricCrypto(PeerContainer.getPeerKey(partnerName));
				if (symCrypto != null)
				{
//					byte[] enData = SymmetricCoder.encryptNotification(notification, symCrypto.getCipherKey(), symCrypto.getIVKey(), symCrypto.getCipherSpec());
					byte[] enData = SymmetricCoder.encryptMessage(notification, symCrypto.getCipherKey(), symCrypto.getIVKey(), symCrypto.getCipherSpec());
					log.info("Encrypted Notification: " + Hex.toHexString(enData));
//					this.peer.syncNotify(ip.getIP(), ip.getPort(), new EncryptedNotification(CryAppID.SYMMETRIC_ENCRYPTED_NOTIFICATION, symCrypto.getSessionKey(), enData));
					this.peer.syncNotify(ip.getIP(), ip.getPort(), new EncryptedNotification(CryAppID.SYMMETRIC_ENCRYPTED_NOTIFICATION, this.peer.getPeerID(), enData));
				}
				else
				{
					throw new SymmetricKeyUnavailableException(partnerName);
				}
			}
			else
			{
				throw new DistributedNodeFailedException(partnerName);
			}
		}
		else
		{
			throw new CryptographyMismatchException(CryConfig.ASYMMETRIC_CRYPTOGRAPHY);
		}
	}
	
	public void syncNotifySymmetricallyByIP(IPResource ip, Notification notification) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException, InterruptedException, SymmetricKeyUnavailableException, CryptographyMismatchException, IPNotExistedException
	{
		if (!this.isAsymCryptography.get())
		{
			String partnerName = ServiceProvider.CRY().getPartnerName(ip.getObjectKey());
			SymmetricCrypto symCrypto = ServiceProvider.CRY().getSymmetricCrypto(PeerContainer.getPeerKey(partnerName));
			if (symCrypto != null)
			{
				byte[] enData = SymmetricCoder.encryptNotification(notification, symCrypto.getCipherKey(), symCrypto.getIVKey(), symCrypto.getCipherSpec());
				log.info("Encrypted Notification: " + Hex.toHexString(enData));
//				this.peer.syncNotify(ip.getIP(), ip.getPort(), new EncryptedNotification(CryAppID.SYMMETRIC_ENCRYPTED_NOTIFICATION, symCrypto.getSessionKey(), enData));
				this.peer.syncNotify(ip.getIP(), ip.getPort(), new EncryptedNotification(CryAppID.SYMMETRIC_ENCRYPTED_NOTIFICATION, this.peer.getPeerID(), enData));
			}
			else
			{
				throw new SymmetricKeyUnavailableException(partnerName);
			}
		}
		else
		{
			throw new CryptographyMismatchException(CryConfig.ASYMMETRIC_CRYPTOGRAPHY);
		}
	}

//	private void syncNotifySymmetricallyByIP(String ip, int port, Notification notification) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException, InterruptedException, SymmetricKeyUnavailableException, CryptographyMismatchException
	private void syncNotifySymmetricallyByIP(String partnerName, String ip, int port, ServerMessage notification) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException, InterruptedException, SymmetricKeyUnavailableException, CryptographyMismatchException
	{
		if (!this.isAsymCryptography.get())
		{
//			String partnerName = ServiceProvider.CRY().getPartnerName(Tools.getKeyOfFreeClient(ip, port));
			SymmetricCrypto symCrypto = ServiceProvider.CRY().getSymmetricCrypto(PeerContainer.getPeerKey(partnerName));
			if (symCrypto != null)
			{
//				byte[] enData = SymmetricCoder.encryptNotification(notification, symCrypto.getCipherKey(), symCrypto.getIVKey(), symCrypto.getCipherSpec());
				byte[] enData = SymmetricCoder.encryptMessage(notification, symCrypto.getCipherKey(), symCrypto.getIVKey(), symCrypto.getCipherSpec());
				log.info("Encrypted Notification: " + Hex.toHexString(enData));
//				this.peer.syncNotify(ip, port, new EncryptedNotification(CryAppID.SYMMETRIC_ENCRYPTED_NOTIFICATION, symCrypto.getSessionKey(), enData));
				this.peer.syncNotify(ip, port, new EncryptedNotification(CryAppID.SYMMETRIC_ENCRYPTED_NOTIFICATION, this.peer.getPeerID(), enData));
			}
			else
			{
				throw new SymmetricKeyUnavailableException(partnerName);
			}
		}
		else
		{
			throw new CryptographyMismatchException(CryConfig.ASYMMETRIC_CRYPTOGRAPHY);
		}
	}

//	public void syncNotifyAsymmetrically(String partnerName, Notification notification) throws ClassNotFoundException, RemoteReadException, IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InterruptedException, InvalidAlgorithmParameterException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException
	public void syncNotifyAsymmetrically(String partnerName, ServerMessage notification) throws ClassNotFoundException, RemoteReadException, IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InterruptedException, InvalidAlgorithmParameterException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		if (this.isAsymCryptography.get())
		{
			String peerKey = PeerContainer.getPeerKey(partnerName);
//			IPAddress ip = this.getIPAddress(peerKey);
			IPAddress ip = this.getIPAddress(partnerName);
			if (ip != null)
			{
//				log.info("partner IP = " + ip);
//				log.info("peerKey = " + peerKey);
				PublicCrypto publicKey = ServiceProvider.CRY().getPublicCryptoByPeer(peerKey);
				if (publicKey != null)
				{
//					log.info("Public key is found!");
//					SymmetricCrypto scrypto = SymmetricCoder.generateCrypto(this.symCipherAlgorithm, this.symCipherSpec, this.symCipherKeyLength, this.symIVKeyLength, this.peer.getPeerID());
					AsymCompCrypto scrypto = AsymmetricCoder.generateCompCrypto(this.symCipherAlgorithm, this.symCipherSpec, this.symCipherKeyLength, this.symIVKeyLength, this.peer.getPeerID());
//					byte[] enData = AsymmetricCoder.encryptNotification(notification, acrypto.getAlgorithm(), acrypto.getPublicKey());
//					byte[] enData = SymmetricCoder.encryptNotification(notification, scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec());
					byte[] enData = SymmetricCoder.encryptMessage(notification, scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec());
					log.info("Encrypted Notification: " + Hex.toHexString(enData));
					byte[] enScrypto = AsymmetricCoder.encrypt(scrypto, publicKey.getAsymAlgorithm(), publicKey.getPublicKey());
//					this.peer.syncNotify(ip.getIP(), ip.getPort(), new EncryptedNotification(CryAppID.ASYMMETRIC_ENCRYPTED_NOTIFICATION, acrypto.getSessionKey(), enData));
					this.peer.syncNotify(ip.getIP(), ip.getPort(), new AsymmetricEncryptedNotification(publicKey.getSessionKey(), enData, enScrypto));
				}
				else
				{
					log.info("Public key is not found!");
					throw new PublicKeyUnavailableException(partnerName);
				}
			}
			else
			{
				log.info(partnerName + "'s IP is not found!");
				throw new DistributedNodeFailedException(partnerName);
			}
		}
		else
		{
			throw new CryptographyMismatchException(CryConfig.SYMMETRIC_CRYPTOGRAPHY);
		}
	}

	public void syncPrmNotifyAsymmetrically(String partnerName, ServerMessage notification) throws ClassNotFoundException, RemoteReadException, IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InterruptedException, InvalidAlgorithmParameterException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		if (this.isAsymCryptography.get())
		{
			String peerKey = PeerContainer.getPeerKey(partnerName);
			IPAddress ip = this.getIPAddress(partnerName);
			if (ip != null)
			{
				PublicCrypto publicKey = ServiceProvider.CRY().getPublicCryptoByPeer(peerKey);
				if (publicKey != null)
				{
					AsymCompCrypto scrypto = AsymmetricCoder.generateCompCrypto(this.symCipherAlgorithm, this.symCipherSpec, this.symCipherKeyLength, this.symIVKeyLength, this.peer.getPeerID());
					byte[] enData = SymmetricCoder.encryptMessage(notification, scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec());
					log.info("Encrypted Notification: " + Hex.toHexString(enData));
					byte[] enScrypto = AsymmetricCoder.encrypt(scrypto, publicKey.getAsymAlgorithm(), publicKey.getPublicKey());
					this.peer.syncNotify(ip.getIP(), ip.getPort(), new AsymmetricPrimitiveNotification(publicKey.getSessionKey(), enData, enScrypto));
				}
				else
				{
					log.info("Public key is not found!");
					throw new PublicKeyUnavailableException(partnerName);
				}
			}
			else
			{
				log.info(partnerName + "'s IP is not found!");
				throw new DistributedNodeFailedException(partnerName);
			}
		}
		else
		{
			throw new CryptographyMismatchException(CryConfig.SYMMETRIC_CRYPTOGRAPHY);
		}
	}

//	private void syncNotifyAsymmetricallyByIPKey(String ipKey, Notification notification) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException, InterruptedException, PublicKeyUnavailableException, DistributedNodeFailedException, CryptographyMismatchException
	private void syncNotifyAsymmetricallyByIPKey(String partnerName, String ipKey, ServerMessage notification) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException, InterruptedException, PublicKeyUnavailableException, DistributedNodeFailedException, CryptographyMismatchException
	{
		if (this.isAsymCryptography.get())
		{
//			String partnerName = ServiceProvider.CRY().getPartnerName(ipKey);
			IPAddress ip = this.getIPAddressByKey(ipKey);
			if (ip != null)
			{
				PublicCrypto publicKey = ServiceProvider.CRY().getPublicCryptoByPeer(PeerContainer.getPeerKey(partnerName));
				if (publicKey != null)
				{
//					SymmetricCrypto scrypto = SymmetricCoder.generateCrypto(this.symCipherAlgorithm, this.symCipherSpec, this.symCipherKeyLength, this.symIVKeyLength, this.peer.getPeerID());
					AsymCompCrypto scrypto = AsymmetricCoder.generateCompCrypto(this.symCipherAlgorithm, this.symCipherSpec, this.symCipherKeyLength, this.symIVKeyLength, this.peer.getPeerID());
//					byte[] enData = SymmetricCoder.encryptNotification(notification, scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec());
					byte[] enData = SymmetricCoder.encryptMessage(notification, scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec());
					log.info("Encrypted Notification: " + Hex.toHexString(enData));
					byte[] enScrypto = AsymmetricCoder.encrypt(scrypto, publicKey.getAsymAlgorithm(), publicKey.getPublicKey());
					this.peer.syncNotify(ip.getIP(), ip.getPort(), new AsymmetricEncryptedNotification(publicKey.getSessionKey(), enData, enScrypto));
				}
				else
				{
					log.info("Public key is not found!");
					throw new PublicKeyUnavailableException(partnerName);
				}
			}
			else
			{
				log.info(partnerName + "'s IP is not found!");
				throw new DistributedNodeFailedException(partnerName);
			}
		}
		else
		{
			throw new CryptographyMismatchException(CryConfig.SYMMETRIC_CRYPTOGRAPHY);
		}
	}

	public void syncNotifyAsymmetricallyByIP(IPResource ip, Notification notification) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException, InterruptedException, PublicKeyUnavailableException, DistributedNodeFailedException, CryptographyMismatchException, IPNotExistedException
	{
		if (this.isAsymCryptography.get())
		{
			String partnerName = ServiceProvider.CRY().getPartnerName(ip.getObjectKey());
			PublicCrypto publicKey = ServiceProvider.CRY().getPublicCryptoByPeer(PeerContainer.getPeerKey(partnerName));
			if (publicKey != null)
			{
//				SymmetricCrypto scrypto = SymmetricCoder.generateCrypto(this.symCipherAlgorithm, this.symCipherSpec, this.symCipherKeyLength, this.symIVKeyLength, this.peer.getPeerID());
				AsymCompCrypto scrypto = AsymmetricCoder.generateCompCrypto(this.symCipherAlgorithm, this.symCipherSpec, this.symCipherKeyLength, this.symIVKeyLength, this.peer.getPeerID());
				byte[] enData = SymmetricCoder.encryptNotification(notification, scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec());
				log.info("Encrypted Notification: " + Hex.toHexString(enData));
				byte[] enScrypto = AsymmetricCoder.encrypt(scrypto, publicKey.getAsymAlgorithm(), publicKey.getPublicKey());
				this.peer.syncNotify(ip.getIP(), ip.getPort(), new AsymmetricEncryptedNotification(publicKey.getSessionKey(), enData, enScrypto));
			}
			else
			{
				log.info("Public key is not found!");
				throw new PublicKeyUnavailableException(partnerName);
			}
		}
		else
		{
			throw new CryptographyMismatchException(CryConfig.SYMMETRIC_CRYPTOGRAPHY);
		}
	}

//	private void syncNotifyAsymmetricallyByIP(String ip, int port, Notification notification) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException, InterruptedException, PublicKeyUnavailableException, DistributedNodeFailedException, CryptographyMismatchException
	private void syncNotifyAsymmetricallyByIP(String partnerName, String ip, int port, ServerMessage notification) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException, InterruptedException, PublicKeyUnavailableException, DistributedNodeFailedException, CryptographyMismatchException
	{
		if (this.isAsymCryptography.get())
		{
//			String partnerName = ServiceProvider.CRY().getPartnerName(Tools.getKeyOfFreeClient(ip, port));
			PublicCrypto publicKey = ServiceProvider.CRY().getPublicCryptoByPeer(PeerContainer.getPeerKey(partnerName));
			if (publicKey != null)
			{
//				SymmetricCrypto scrypto = SymmetricCoder.generateCrypto(this.symCipherAlgorithm, this.symCipherSpec, this.symCipherKeyLength, this.symIVKeyLength, this.peer.getPeerID());
				AsymCompCrypto scrypto = AsymmetricCoder.generateCompCrypto(this.symCipherAlgorithm, this.symCipherSpec, this.symCipherKeyLength, this.symIVKeyLength, this.peer.getPeerID());
//				byte[] enData = SymmetricCoder.encryptNotification(notification, scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec());
				byte[] enData = SymmetricCoder.encryptMessage(notification, scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec());
				log.info("Encrypted Notification: " + Hex.toHexString(enData));
				byte[] enScrypto = AsymmetricCoder.encrypt(scrypto, publicKey.getAsymAlgorithm(), publicKey.getPublicKey());
				this.peer.syncNotify(ip, port, new AsymmetricEncryptedNotification(publicKey.getSessionKey(), enData, enScrypto));
			}
			else
			{
				log.info("Public key is not found!");
				throw new PublicKeyUnavailableException(partnerName);
			}
		}
		else
		{
			throw new CryptographyMismatchException(CryConfig.SYMMETRIC_CRYPTOGRAPHY);
		}
	}

	/*
	public void syncNotifyAsymmetricallyByIP(String ip, int port, ServerMessage message) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException, InterruptedException, PublicKeyUnavailableException, DistributedNodeFailedException, CryptographyMismatchException
	{
		if (this.isAsymCryptography)
		{
			String partnerName = ServiceProvider.CRY().getPartnerName(Tools.getKeyOfFreeClient(ip, port));
			PublicCrypto publicKey = ServiceProvider.CRY().getPublicCryptoByPeer(PeerContainer.getPeerKey(partnerName));
			if (publicKey != null)
			{
				SymmetricCrypto scrypto = SymmetricCoder.generateCrypto(this.symCipherAlgorithm, this.symCipherSpec, this.symCipherKeyLength, this.symIVKeyLength, this.peer.getPeerID());
				byte[] enData = SymmetricCoder.encryptMessage(message, scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec());
				log.info("Encrypted Notification: " + Hex.toHexString(enData));
				byte[] enScrypto = AsymmetricCoder.encrypt(scrypto, publicKey.getAsymAlgorithm(), publicKey.getPublicKey());
				this.peer.syncNotify(ip, port, new AsymmetricEncryptedNotification(publicKey.getSessionKey(), enData, enScrypto, true));
			}
			else
			{
				log.info("Public key is not found!");
				throw new PublicKeyUnavailableException(partnerName);
			}
		}
		else
		{
			throw new CryptographyMismatchException(CryConfig.SYMMETRIC_CRYPTOGRAPHY);
		}
	}
	*/

//	public void syncNotifyBySignature(String partnerName, Notification notification) throws ClassNotFoundException, RemoteReadException, IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InterruptedException, InvalidAlgorithmParameterException, SignatureException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException
	public void syncNotifyBySignature(String partnerName, ServerMessage notification) throws ClassNotFoundException, RemoteReadException, IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InterruptedException, InvalidAlgorithmParameterException, SignatureException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
//		if (this.isAsymCryptography)
		if (this.isSigned())
		{
			String peerKey = PeerContainer.getPeerKey(partnerName);
//			IPAddress ip = this.getIPAddress(peerKey);
			IPAddress ip = this.getIPAddress(partnerName);
			if (ip != null)
			{
//				log.info("partner IP = " + ip);
				PublicCrypto publicKey = ServiceProvider.CRY().getPublicCryptoByPeer(peerKey);
				if (publicKey != null)
				{
//					log.info("Public key is found!");
//					SymmetricCrypto scrypto = SymmetricCoder.generateCrypto(this.symCipherAlgorithm, this.symCipherSpec, this.symCipherKeyLength, this.symIVKeyLength, this.peer.getPeerID());
					AsymCompCrypto scrypto = AsymmetricCoder.generateCompCrypto(this.symCipherAlgorithm, this.symCipherSpec, this.symCipherKeyLength, this.symIVKeyLength, this.peer.getPeerID());
//					byte[] enData = SymmetricCoder.encryptNotification(notification, scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec());
					byte[] enData = SymmetricCoder.encryptMessage(notification, scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec());
					log.info("Encrypted Notification: " + Hex.toHexString(enData));
					byte[] enScrypto = AsymmetricCoder.encrypt(scrypto, publicKey.getAsymAlgorithm(), publicKey.getPublicKey());
					byte[] signedInfo = AsymmetricCoder.sign(ServiceProvider.CRY().getSignatureAlgorithm(), ServiceProvider.CRY().getPrivateKey(), ServiceProvider.CRY().getSignature());
					log.info("Your signature is " + ServiceProvider.CRY().getSignature());
					this.peer.syncNotify(ip.getIP(), ip.getPort(), new SignedAsymmetricEncryptedNotification(publicKey.getSessionKey(), enData, enScrypto, ServiceProvider.CRY().getSignature(), signedInfo));
				}
				else
				{
					log.info("Public key is not found!");
					throw new PublicKeyUnavailableException(partnerName);
				}
			}
			else
			{
				throw new DistributedNodeFailedException(partnerName);
			}
		}
		else
		{
			throw new CryptographyMismatchException(CryConfig.SYMMETRIC_CRYPTOGRAPHY);
		}
	}

	public void syncPrmNotifyBySignature(String partnerName, ServerMessage notification) throws ClassNotFoundException, RemoteReadException, IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InterruptedException, InvalidAlgorithmParameterException, SignatureException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		if (this.isSigned())
		{
			String peerKey = PeerContainer.getPeerKey(partnerName);
			IPAddress ip = this.getIPAddress(partnerName);
			if (ip != null)
			{
				PublicCrypto publicKey = ServiceProvider.CRY().getPublicCryptoByPeer(peerKey);
				if (publicKey != null)
				{
					AsymCompCrypto scrypto = AsymmetricCoder.generateCompCrypto(this.symCipherAlgorithm, this.symCipherSpec, this.symCipherKeyLength, this.symIVKeyLength, this.peer.getPeerID());
					byte[] enData = SymmetricCoder.encryptMessage(notification, scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec());
					log.info("Encrypted Notification: " + Hex.toHexString(enData));
					byte[] enScrypto = AsymmetricCoder.encrypt(scrypto, publicKey.getAsymAlgorithm(), publicKey.getPublicKey());
					byte[] signedInfo = AsymmetricCoder.sign(ServiceProvider.CRY().getSignatureAlgorithm(), ServiceProvider.CRY().getPrivateKey(), ServiceProvider.CRY().getSignature());
					log.info("Your signature is " + ServiceProvider.CRY().getSignature());
					this.peer.syncNotify(ip.getIP(), ip.getPort(), new SignedPrimitiveNotification(publicKey.getSessionKey(), enData, enScrypto, ServiceProvider.CRY().getSignature(), signedInfo));
				}
				else
				{
					log.info("Public key is not found!");
					throw new PublicKeyUnavailableException(partnerName);
				}
			}
			else
			{
				throw new DistributedNodeFailedException(partnerName);
			}
		}
		else
		{
			throw new CryptographyMismatchException(CryConfig.SYMMETRIC_CRYPTOGRAPHY);
		}
	}

//	private void syncNotifyBySignatureByIPKey(String ipKey, Notification notification) throws CryptographyMismatchException, DistributedNodeFailedException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException, SignatureException, InterruptedException, PublicKeyUnavailableException
	private void syncNotifyBySignatureByIPKey(String partnerName, String ipKey, ServerMessage notification) throws CryptographyMismatchException, DistributedNodeFailedException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException, SignatureException, InterruptedException, PublicKeyUnavailableException
	{
		if (this.isSigned())
		{
//			String partnerName = ServiceProvider.CRY().getPartnerName(ipKey);
			IPAddress ip = this.getIPAddressByKey(ipKey);
			if (ip != null)
			{
				PublicCrypto publicKey = ServiceProvider.CRY().getPublicCryptoByPeer(PeerContainer.getPeerKey(partnerName));
				if (publicKey != null)
				{
//					SymmetricCrypto scrypto = SymmetricCoder.generateCrypto(this.symCipherAlgorithm, this.symCipherSpec, this.symCipherKeyLength, this.symIVKeyLength, this.peer.getPeerID());
					AsymCompCrypto scrypto = AsymmetricCoder.generateCompCrypto(this.symCipherAlgorithm, this.symCipherSpec, this.symCipherKeyLength, this.symIVKeyLength, this.peer.getPeerID());
//					byte[] enData = SymmetricCoder.encryptNotification(notification, scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec());
					byte[] enData = SymmetricCoder.encryptMessage(notification, scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec());
					log.info("Encrypted Notification: " + Hex.toHexString(enData));
					byte[] enScrypto = AsymmetricCoder.encrypt(scrypto, publicKey.getAsymAlgorithm(), publicKey.getPublicKey());
					byte[] signedInfo = AsymmetricCoder.sign(ServiceProvider.CRY().getSignatureAlgorithm(), ServiceProvider.CRY().getPrivateKey(), ServiceProvider.CRY().getSignature());
					log.info("Your signature is " + ServiceProvider.CRY().getSignature());
					this.peer.syncNotify(ip.getIP(), ip.getPort(), new SignedAsymmetricEncryptedNotification(publicKey.getSessionKey(), enData, enScrypto, ServiceProvider.CRY().getSignature(), signedInfo));
				}
				else
				{
					log.info("Public key is not found!");
					throw new PublicKeyUnavailableException(partnerName);
				}
			}
			else
			{
				throw new DistributedNodeFailedException(partnerName);
			}
		}
		else
		{
			throw new CryptographyMismatchException(CryConfig.SYMMETRIC_CRYPTOGRAPHY);
		}
	}

	/*
	public void syncNotifyBySignatureByIP(IPResource ip, Notification notification) throws CryptographyMismatchException, DistributedNodeFailedException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException, SignatureException, InterruptedException, PublicKeyUnavailableException
	{
		if (this.isSigned())
		{
			String partnerName = ServiceProvider.CRY().getPartnerName(ip.getObjectKey());
			PublicCrypto publicKey = ServiceProvider.CRY().getPublicCryptoByPeer(PeerContainer.getPeerKey(partnerName));
			if (publicKey != null)
			{
				SymmetricCrypto scrypto = SymmetricCoder.generateCrypto(this.symCipherAlgorithm, this.symCipherSpec, this.symCipherKeyLength, this.symIVKeyLength, this.peer.getPeerID());
				byte[] enData = SymmetricCoder.encryptNotification(notification, scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec());
				log.info("Encrypted Notification: " + Hex.toHexString(enData));
				byte[] enScrypto = AsymmetricCoder.encrypt(scrypto, publicKey.getAsymAlgorithm(), publicKey.getPublicKey());
				byte[] signedInfo = AsymmetricCoder.sign(ServiceProvider.CRY().getSignatureAlgorithm(), ServiceProvider.CRY().getPrivateKey(), ServiceProvider.CRY().getSignature());
				log.info("Your signature is " + ServiceProvider.CRY().getSignature());
				this.peer.syncNotify(ip.getIP(), ip.getPort(), new SignedAsymmetricEncryptedNotification(publicKey.getSessionKey(), enData, enScrypto, ServiceProvider.CRY().getSignature(), signedInfo));
			}
			else
			{
				log.info("Public key is not found!");
				throw new PublicKeyUnavailableException(partnerName);
			}
		}
		else
		{
			throw new CryptographyMismatchException(CryConfig.SYMMETRIC_CRYPTOGRAPHY);
		}
	}
	*/

//	private void syncNotifyBySignatureByIP(String ip, int port, Notification notification) throws CryptographyMismatchException, DistributedNodeFailedException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException, SignatureException, InterruptedException, PublicKeyUnavailableException
	private void syncNotifyBySignatureByIP(String partnerName, String ip, int port, ServerMessage notification) throws CryptographyMismatchException, DistributedNodeFailedException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException, SignatureException, InterruptedException, PublicKeyUnavailableException
	{
		if (this.isSigned())
		{
//			String partnerName = ServiceProvider.CRY().getPartnerName(Tools.getKeyOfFreeClient(ip, port));
			PublicCrypto publicKey = ServiceProvider.CRY().getPublicCryptoByPeer(PeerContainer.getPeerKey(partnerName));
			if (publicKey != null)
			{
//				SymmetricCrypto scrypto = SymmetricCoder.generateCrypto(this.symCipherAlgorithm, this.symCipherSpec, this.symCipherKeyLength, this.symIVKeyLength, this.peer.getPeerID());
				AsymCompCrypto scrypto = AsymmetricCoder.generateCompCrypto(this.symCipherAlgorithm, this.symCipherSpec, this.symCipherKeyLength, this.symIVKeyLength, this.peer.getPeerID());
				byte[] enData = SymmetricCoder.encryptMessage(notification, scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec());
				log.info("Encrypted Notification: " + Hex.toHexString(enData));
				byte[] enScrypto = AsymmetricCoder.encrypt(scrypto, publicKey.getAsymAlgorithm(), publicKey.getPublicKey());
				byte[] signedInfo = AsymmetricCoder.sign(ServiceProvider.CRY().getSignatureAlgorithm(), ServiceProvider.CRY().getPrivateKey(), ServiceProvider.CRY().getSignature());
				log.info("Your signature is " + ServiceProvider.CRY().getSignature());
				this.peer.syncNotify(ip, port, new SignedAsymmetricEncryptedNotification(publicKey.getSessionKey(), enData, enScrypto, ServiceProvider.CRY().getSignature(), signedInfo));
			}
			else
			{
				log.info("Public key is not found!");
				throw new PublicKeyUnavailableException(partnerName);
			}
		}
		else
		{
			throw new CryptographyMismatchException(CryConfig.SYMMETRIC_CRYPTOGRAPHY);
		}
	}

	public void syncNotifyPrivately(String ownerName, String partnerName, Notification notification) throws ClassNotFoundException, RemoteReadException, IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InterruptedException, InvalidAlgorithmParameterException, SignatureException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		if (this.isAsymCryptography.get())
		{
			String peerKey = PeerContainer.getPeerKey(partnerName);
//			IPAddress ip = this.getIPAddress(peerKey);
			IPAddress ip = this.getIPAddress(partnerName);
			if (ip != null)
			{
//				log.info("partner IP = " + ip);
				PublicCrypto publicKey = ServiceProvider.CRY().getPublicCryptoByPeer(peerKey);
				if (publicKey != null)
				{
//					log.info("Public key is found!");
//					SymmetricCrypto scrypto = SymmetricCoder.generateCrypto(this.symCipherAlgorithm, this.symCipherSpec, this.symCipherKeyLength, this.symIVKeyLength, this.peer.getPeerID());
					AsymCompCrypto scrypto = AsymmetricCoder.generateCompCrypto(this.symCipherAlgorithm, this.symCipherSpec, this.symCipherKeyLength, this.symIVKeyLength, this.peer.getPeerID());
					byte[] enData = SymmetricCoder.encryptNotification(notification, scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec());
					log.info("Encrypted Notification: " + Hex.toHexString(enData));
					byte[] enScrypto = AsymmetricCoder.encrypt(scrypto, publicKey.getAsymAlgorithm(), publicKey.getPublicKey());
					byte[] signedInfo = AsymmetricCoder.sign(ServiceProvider.CRY().getSignatureAlgorithm(), ServiceProvider.CRY().getPrivateKey(), ServiceProvider.CRY().getSignature());
					log.info("Your signature is " + ServiceProvider.CRY().getSignature());
//					this.peer.syncNotify(ip.getIP(), ip.getPort(), new SignedAsymmetricEncryptedNotification(publicKey.getSessionKey(), enData, enScrypto, ServiceProvider.CRY().getSignature(), signedInfo, ownerName));
					this.peer.syncNotify(ip.getIP(), ip.getPort(), new PrivateNotification(publicKey.getSessionKey(), enData, enScrypto, ServiceProvider.CRY().getSignature(), signedInfo, ownerName));
				}
				else
				{
					log.info("Public key is not found!");
					throw new PublicKeyUnavailableException(partnerName);
				}
			}
			else
			{
				throw new DistributedNodeFailedException(partnerName);
			}
		}
		else
		{
			throw new CryptographyMismatchException(CryConfig.SYMMETRIC_CRYPTOGRAPHY);
		}
	}

	public void syncPrmNotifyPrivately(String ownerName, String partnerName, ServerMessage notification) throws ClassNotFoundException, RemoteReadException, IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InterruptedException, InvalidAlgorithmParameterException, SignatureException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		if (this.isAsymCryptography.get())
		{
			String peerKey = PeerContainer.getPeerKey(partnerName);
			IPAddress ip = this.getIPAddress(partnerName);
			if (ip != null)
			{
				PublicCrypto publicKey = ServiceProvider.CRY().getPublicCryptoByPeer(peerKey);
				if (publicKey != null)
				{
					AsymCompCrypto scrypto = AsymmetricCoder.generateCompCrypto(this.symCipherAlgorithm, this.symCipherSpec, this.symCipherKeyLength, this.symIVKeyLength, this.peer.getPeerID());
					byte[] enData = SymmetricCoder.encryptMessage(notification, scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec());
					log.info("Encrypted Notification: " + Hex.toHexString(enData));
					byte[] enScrypto = AsymmetricCoder.encrypt(scrypto, publicKey.getAsymAlgorithm(), publicKey.getPublicKey());
					byte[] signedInfo = AsymmetricCoder.sign(ServiceProvider.CRY().getSignatureAlgorithm(), ServiceProvider.CRY().getPrivateKey(), ServiceProvider.CRY().getSignature());
					log.info("Your signature is " + ServiceProvider.CRY().getSignature());
					this.peer.syncNotify(ip.getIP(), ip.getPort(), new PrivatePrimitiveNotification(publicKey.getSessionKey(), enData, enScrypto, ServiceProvider.CRY().getSignature(), signedInfo, ownerName));
				}
				else
				{
					log.info("Public key is not found!");
					throw new PublicKeyUnavailableException(partnerName);
				}
			}
			else
			{
				throw new DistributedNodeFailedException(partnerName);
			}
		}
		else
		{
			throw new CryptographyMismatchException(CryConfig.SYMMETRIC_CRYPTOGRAPHY);
		}
	}

	public void asyncNotify(String partnerName, Notification notification) throws ClassNotFoundException, RemoteReadException, IOException, DistributedNodeFailedException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
//		String peerKey = PeerContainer.getPeerKey(partnerName);
//		IPAddress ip = this.getIPAddress(peerKey);
		IPAddress ip = this.getIPAddress(partnerName);
		if (ip != null)
		{
			this.peer.asyncNotify(ip.getIP(), ip.getPort(), notification);
		}
		else
		{
			throw new DistributedNodeFailedException(partnerName);
		}
	}

	public void asyncNotify(String ip, int port, Notification notification) throws ClassNotFoundException, RemoteReadException, IOException, DistributedNodeFailedException
	{
		this.peer.asyncNotify(ip, port, notification);
	}

	public void asyncNotify(String ip, int port, ServerMessage notification) throws ClassNotFoundException, RemoteReadException, IOException, DistributedNodeFailedException
	{
		this.peer.asyncNotify(ip, port, notification);
	}

	public void asyncNotifySymmetrically(String partnerName, Notification notification) throws ClassNotFoundException, RemoteReadException, IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, CryptographyMismatchException, DistributedNodeFailedException, SymmetricKeyUnavailableException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		if (!this.isAsymCryptography.get())
		{
			String peerKey = PeerContainer.getPeerKey(partnerName);
//			IPAddress ip = this.getIPAddress(peerKey);
			IPAddress ip = this.getIPAddress(partnerName);
			if (ip != null)
			{
				SymmetricCrypto symCrypto = ServiceProvider.CRY().getSymmetricCrypto(peerKey);
				if (symCrypto != null)
				{
					byte[] enData = SymmetricCoder.encryptNotification(notification, symCrypto.getCipherKey(), symCrypto.getIVKey(), symCrypto.getCipherSpec());
					log.info("Encrypted Notification: " + Hex.toHexString(enData));
//					this.peer.asyncNotify(ip.getIP(), ip.getPort(), new EncryptedNotification(CryAppID.SYMMETRIC_ENCRYPTED_NOTIFICATION, symCrypto.getSessionKey(), enData));
					this.peer.asyncNotify(ip.getIP(), ip.getPort(), new EncryptedNotification(CryAppID.SYMMETRIC_ENCRYPTED_NOTIFICATION, this.peer.getPeerID(), enData));
				}
				else
				{
					throw new SymmetricKeyUnavailableException(partnerName);
				}
			}
			else
			{
				throw new DistributedNodeFailedException(partnerName);
			}
		}
		else
		{
			throw new CryptographyMismatchException(CryConfig.ASYMMETRIC_CRYPTOGRAPHY);
		}
	}

	public void asyncNotifyAsymmetrically(String partnerName, Notification notification) throws ClassNotFoundException, RemoteReadException, IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		if (this.isAsymCryptography.get())
		{
			String peerKey = PeerContainer.getPeerKey(partnerName);
//			IPAddress ip = this.getIPAddress(peerKey);
			IPAddress ip = this.getIPAddress(partnerName);
			if (ip != null)
			{
				PublicCrypto publicKey = ServiceProvider.CRY().getPublicCryptoByPeer(peerKey);
				if (publicKey != null)
				{
//					SymmetricCrypto scrypto = SymmetricCoder.generateCrypto(this.symCipherAlgorithm, this.symCipherSpec, this.symCipherKeyLength, this.symIVKeyLength, this.peer.getPeerID());
					AsymCompCrypto scrypto = AsymmetricCoder.generateCompCrypto(this.symCipherAlgorithm, this.symCipherSpec, this.symCipherKeyLength, this.symIVKeyLength, this.peer.getPeerID());
//					byte[] enData = AsymmetricCoder.encrypt(notification, crypto.getAlgorithm(), crypto.getPublicKey());
					byte[] enData = SymmetricCoder.encryptNotification(notification, scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec());
					byte[] enScrypto = AsymmetricCoder.encrypt(scrypto, publicKey.getAsymAlgorithm(), publicKey.getPublicKey());
//					this.peer.asyncNotify(ip.getIP(), ip.getPort(), new EncryptedNotification(CryAppID.ASYMMETRIC_ENCRYPTED_NOTIFICATION, crypto.getSessionKey(), enData));
					this.peer.asyncNotify(ip.getIP(), ip.getPort(), new AsymmetricEncryptedNotification(publicKey.getSessionKey(), enData, enScrypto));
				}
				else
				{
					log.info("Public key is not found!");
					throw new PublicKeyUnavailableException(partnerName);
				}
			}
			else
			{
				throw new DistributedNodeFailedException(partnerName);
			}
		}
		else
		{
			throw new CryptographyMismatchException(CryConfig.SYMMETRIC_CRYPTOGRAPHY);
		}
	}

	public void asyncNotifyBySignature(String partnerName, Notification notification) throws ClassNotFoundException, RemoteReadException, IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, SignatureException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
//		if (this.isAsymCryptography)
		if (this.isSigned())
		{
			String peerKey = PeerContainer.getPeerKey(partnerName);
//			IPAddress ip = this.getIPAddress(peerKey);
			IPAddress ip = this.getIPAddress(partnerName);
			if (ip != null)
			{
				PublicCrypto publicKey = ServiceProvider.CRY().getPublicCryptoByPeer(peerKey);
				if (publicKey != null)
				{
//					SymmetricCrypto scrypto = SymmetricCoder.generateCrypto(this.symCipherAlgorithm, this.symCipherSpec, this.symCipherKeyLength, this.symIVKeyLength, this.peer.getPeerID());
					AsymCompCrypto scrypto = AsymmetricCoder.generateCompCrypto(this.symCipherAlgorithm, this.symCipherSpec, this.symCipherKeyLength, this.symIVKeyLength, this.peer.getPeerID());
					byte[] enData = SymmetricCoder.encryptNotification(notification, scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec());
					byte[] enScrypto = AsymmetricCoder.encrypt(scrypto, publicKey.getAsymAlgorithm(), publicKey.getPublicKey());
					byte[] signedInfo = AsymmetricCoder.sign(ServiceProvider.CRY().getSignatureAlgorithm(), ServiceProvider.CRY().getPrivateKey(), ServiceProvider.CRY().getSignature());
					log.info("Your signature is " + ServiceProvider.CRY().getSignature());
					this.peer.asyncNotify(ip.getIP(), ip.getPort(), new SignedAsymmetricEncryptedNotification(publicKey.getSessionKey(), enData, enScrypto, ServiceProvider.CRY().getSignature(), signedInfo));
				}
				else
				{
					log.info("Public key is not found!");
					throw new PublicKeyUnavailableException(partnerName);
				}
			}
			else
			{
				throw new DistributedNodeFailedException(partnerName);
			}
		}
		else
		{
			throw new CryptographyMismatchException(CryConfig.SYMMETRIC_CRYPTOGRAPHY);
		}
	}
  
	public void asyncNotifyPrivately(String ownerName, String partnerName, Notification notification) throws ClassNotFoundException, RemoteReadException, IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, SignatureException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		if (this.isAsymCryptography.get())
		{
			String peerKey = PeerContainer.getPeerKey(partnerName);
//			IPAddress ip = this.getIPAddress(peerKey);
			IPAddress ip = this.getIPAddress(partnerName);
			if (ip != null)
			{
				PublicCrypto publicKey = ServiceProvider.CRY().getPublicCryptoByPeer(peerKey);
				if (publicKey != null)
				{
//					SymmetricCrypto scrypto = SymmetricCoder.generateCrypto(this.symCipherAlgorithm, this.symCipherSpec, this.symCipherKeyLength, this.symIVKeyLength, this.peer.getPeerID());
					AsymCompCrypto scrypto = AsymmetricCoder.generateCompCrypto(this.symCipherAlgorithm, this.symCipherSpec, this.symCipherKeyLength, this.symIVKeyLength, this.peer.getPeerID());
					byte[] enData = SymmetricCoder.encryptNotification(notification, scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec());
					byte[] enScrypto = AsymmetricCoder.encrypt(scrypto, publicKey.getAsymAlgorithm(), publicKey.getPublicKey());
					byte[] signedInfo = AsymmetricCoder.sign(ServiceProvider.CRY().getSignatureAlgorithm(), ServiceProvider.CRY().getPrivateKey(), ServiceProvider.CRY().getSignature());
					log.info("Your signature is " + ServiceProvider.CRY().getSignature());
//					this.peer.asyncNotify(ip.getIP(), ip.getPort(), new SignedAsymmetricEncryptedNotification(publicKey.getSessionKey(), enData, enScrypto, ServiceProvider.CRY().getSignature(), signedInfo, ownerName));
					this.peer.asyncNotify(ip.getIP(), ip.getPort(), new PrivateNotification(publicKey.getSessionKey(), enData, enScrypto, ServiceProvider.CRY().getSignature(), signedInfo, ownerName));
				}
				else
				{
					log.info("Public key is not found!");
					throw new PublicKeyUnavailableException(partnerName);
				}
			}
			else
			{
				throw new DistributedNodeFailedException(partnerName);
			}
		}
		else
		{
			throw new CryptographyMismatchException(CryConfig.SYMMETRIC_CRYPTOGRAPHY);
		}
	}

//	public ServerMessage read(String partnerName, Request request) throws ClassNotFoundException, RemoteReadException, IOException, DistributedNodeFailedException
	public ServerMessage read(String partnerName, ServerMessage request) throws ClassNotFoundException, RemoteReadException, DistributedNodeFailedException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
//		String peerKey = PeerContainer.getPeerKey(partnerName);
//		IPAddress ip = this.getIPAddress(peerKey);
		IPAddress ip = this.getIPAddress(partnerName);
		if (ip != null)
		{
			return this.peer.read(ip.getIP(), ip.getPort(), request);
		}
		else
		{
			throw new DistributedNodeFailedException(partnerName);
		}
	}

	public ServerMessage read(String ip, int port, Request request) throws ClassNotFoundException, RemoteReadException, DistributedNodeFailedException, RemoteIPNotExistedException
	{
		return this.peer.read(ip, port, request);
	}

	public ServerMessage read(String ip, int port, ServerMessage request) throws ClassNotFoundException, RemoteReadException, DistributedNodeFailedException, RemoteIPNotExistedException
	{
		return this.peer.read(ip, port, request);
	}

//	public ServerMessage readSymmetrically(String partnerName, Request request) throws ClassNotFoundException, RemoteReadException, IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, ShortBufferException, CryptographyMismatchException, DistributedNodeFailedException, SymmetricKeyUnavailableException
	public ServerMessage readSymmetrically(String partnerName, ServerMessage request) throws ClassNotFoundException, RemoteReadException, IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, ShortBufferException, CryptographyMismatchException, DistributedNodeFailedException, SymmetricKeyUnavailableException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		if (!this.isAsymCryptography.get())
		{
			String peerKey = PeerContainer.getPeerKey(partnerName);
//			IPAddress ip = this.getIPAddress(peerKey);
			IPAddress ip = this.getIPAddress(partnerName);
			if (ip != null)
			{
				SymmetricCrypto symCrypto = ServiceProvider.CRY().getSymmetricCrypto(peerKey);
				if (symCrypto != null)
				{
//					byte[] enData = SymmetricCoder.encryptRequest(request, symCrypto.getCipherKey(), symCrypto.getIVKey(), symCrypto.getCipherSpec());
					byte[] enData = SymmetricCoder.encryptMessage(request, symCrypto.getCipherKey(), symCrypto.getIVKey(), symCrypto.getCipherSpec());
					log.info("Encrypted Request: " + Hex.toHexString(enData));
//					EncryptedResponse response = (EncryptedResponse)this.peer.read(ip.getIP(), ip.getPort(), new EncryptedRequest(CryAppID.SYMMETRIC_ENCRYPTED_REQUEST, symCrypto.getSessionKey(), enData));
					EncryptedResponse response = (EncryptedResponse)this.peer.read(ip.getIP(), ip.getPort(), new EncryptedRequest(CryAppID.SYMMETRIC_ENCRYPTED_REQUEST, this.peer.getPeerID(), enData));
					if (response != null)
					{
						log.info("Encrypted Response: " + Hex.toHexString(response.getEncryptedData()));
						return SymmetricCoder.decryptResponse(response.getEncryptedData(), symCrypto.getCipherKey(), symCrypto.getIVKey(), symCrypto.getCipherSpec());
					}
				}
				else
				{
					throw new SymmetricKeyUnavailableException(partnerName);
				}
			}
			else
			{
				throw new DistributedNodeFailedException(partnerName);
			}
		}
		else
		{
			throw new CryptographyMismatchException(CryConfig.ASYMMETRIC_CRYPTOGRAPHY);
		}
		return null;
	}

	public ServerMessage prmReadSymmetrically(String partnerName, ServerMessage request) throws ClassNotFoundException, RemoteReadException, IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, ShortBufferException, CryptographyMismatchException, DistributedNodeFailedException, SymmetricKeyUnavailableException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		if (!this.isAsymCryptography.get())
		{
			String peerKey = PeerContainer.getPeerKey(partnerName);
			IPAddress ip = this.getIPAddress(partnerName);
			if (ip != null)
			{
				SymmetricCrypto symCrypto = ServiceProvider.CRY().getSymmetricCrypto(peerKey);
				if (symCrypto != null)
				{
					byte[] enData = SymmetricCoder.encryptMessage(request, symCrypto.getCipherKey(), symCrypto.getIVKey(), symCrypto.getCipherSpec());
					log.info("Encrypted Request: " + Hex.toHexString(enData));
					EncryptedResponse response = (EncryptedResponse)this.peer.read(ip.getIP(), ip.getPort(), new SymmetricPrimitiveRequest(this.peer.getPeerID(), enData));
					if (response != null)
					{
						log.info("Encrypted Response: " + Hex.toHexString(response.getEncryptedData()));
						return SymmetricCoder.decryptResponse(response.getEncryptedData(), symCrypto.getCipherKey(), symCrypto.getIVKey(), symCrypto.getCipherSpec());
					}
				}
				else
				{
					throw new SymmetricKeyUnavailableException(partnerName);
				}
			}
			else
			{
				throw new DistributedNodeFailedException(partnerName);
			}
		}
		else
		{
			throw new CryptographyMismatchException(CryConfig.ASYMMETRIC_CRYPTOGRAPHY);
		}
		return null;
	}

//	public ServerMessage readAsymmetrically(String partnerName, Request request) throws ClassNotFoundException, RemoteReadException, IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException
	public ServerMessage readAsymmetrically(String partnerName, ServerMessage request) throws ClassNotFoundException, RemoteReadException, IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		if (this.isAsymCryptography.get())
		{
			String peerKey = PeerContainer.getPeerKey(partnerName);
//			IPAddress ip = this.getIPAddress(peerKey);
			IPAddress ip = this.getIPAddress(partnerName);
			if (ip != null)
			{
//				log.info("partner IP = " + ip);
				PublicCrypto publicKey = ServiceProvider.CRY().getPublicCryptoByPeer(peerKey);
				if (publicKey != null)
				{
//					SymmetricCrypto scrypto = SymmetricCoder.generateCrypto(this.symCipherAlgorithm, this.symCipherSpec, this.symCipherKeyLength, this.symIVKeyLength, this.peer.getPeerID());
					AsymCompCrypto scrypto = AsymmetricCoder.generateCompCrypto(this.symCipherAlgorithm, this.symCipherSpec, this.symCipherKeyLength, this.symIVKeyLength, this.peer.getPeerID());
//					byte[] enData = AsymmetricCoder.encryptRequest(request, acrypto.getAlgorithm(), acrypto.getPublicKey());
//					byte[] enData = SymmetricCoder.encryptRequest(request, scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec());
					byte[] enData = SymmetricCoder.encryptMessage(request, scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec());
					byte[] enScrypto = AsymmetricCoder.encrypt(scrypto, publicKey.getAsymAlgorithm(), publicKey.getPublicKey());
					log.info("Encrypted Request: " + Hex.toHexString(enData));
//					EncryptedResponse response = (EncryptedResponse)this.peer.read(ip.getIP(), ip.getPort(), new EncryptedRequest(CryAppID.ASYMMETRIC_ENCRYPTED_REQUEST, acrypto.getSessionKey(), enData));
					AsymmetricEncryptedResponse response = (AsymmetricEncryptedResponse)this.peer.read(ip.getIP(), ip.getPort(), new AsymmetricEncryptedRequest(publicKey.getSessionKey(), enData, enScrypto));
//					AsymmetricEncryptedResponse response = (AsymmetricEncryptedResponse)this.peer.read(ip.getIP(), ip.getPort(), new AsymmetricEncryptedRequest(ServiceProvider.CRY().getAsymmetricSessionKey(), enData, enScrypto));
					if (response != null)
					{
						log.info("Encrypted Response: " + Hex.toHexString(response.getEncryptedData()));
						SymmetricCrypto resScrypto = (SymmetricCrypto)AsymmetricCoder.decrypt(response.getEncryptedSymCrypto(), ServiceProvider.CRY().getAsymmetricAlgorithm(), ServiceProvider.CRY().getPrivateKey());
//						return AsymmetricCoder.decryptResponse(response.getEncryptedData(), ServiceProvider.CRY().getAsymmetricAlgorithm(), ServiceProvider.CRY().getPrivateKey());
						return SymmetricCoder.decryptResponse(response.getEncryptedData(), resScrypto.getCipherKey(), resScrypto.getIVKey(), resScrypto.getCipherSpec());
					}
				}
				else
				{
					log.info("Public key is not found!");
					throw new PublicKeyUnavailableException(partnerName);
				}
			}
			else
			{
				throw new DistributedNodeFailedException(partnerName);
			}
		}
		else
		{
			throw new CryptographyMismatchException(CryConfig.SYMMETRIC_CRYPTOGRAPHY);
		}
		return null;
	}

	public ServerMessage prmReadAsymmetrically(String partnerName, ServerMessage request) throws ClassNotFoundException, RemoteReadException, IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		if (this.isAsymCryptography.get())
		{
			String peerKey = PeerContainer.getPeerKey(partnerName);
			IPAddress ip = this.getIPAddress(partnerName);
			if (ip != null)
			{
				PublicCrypto publicKey = ServiceProvider.CRY().getPublicCryptoByPeer(peerKey);
				if (publicKey != null)
				{
					AsymCompCrypto scrypto = AsymmetricCoder.generateCompCrypto(this.symCipherAlgorithm, this.symCipherSpec, this.symCipherKeyLength, this.symIVKeyLength, this.peer.getPeerID());
					byte[] enData = SymmetricCoder.encryptMessage(request, scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec());
					byte[] enScrypto = AsymmetricCoder.encrypt(scrypto, publicKey.getAsymAlgorithm(), publicKey.getPublicKey());
					log.info("Encrypted Request: " + Hex.toHexString(enData));
					AsymmetricEncryptedResponse response = (AsymmetricEncryptedResponse)this.peer.read(ip.getIP(), ip.getPort(), new AsymmetricPrimitiveRequest(publicKey.getSessionKey(), enData, enScrypto));
					if (response != null)
					{
						log.info("Encrypted Response: " + Hex.toHexString(response.getEncryptedData()));
						SymmetricCrypto resScrypto = (SymmetricCrypto)AsymmetricCoder.decrypt(response.getEncryptedSymCrypto(), ServiceProvider.CRY().getAsymmetricAlgorithm(), ServiceProvider.CRY().getPrivateKey());
						return SymmetricCoder.decryptResponse(response.getEncryptedData(), resScrypto.getCipherKey(), resScrypto.getIVKey(), resScrypto.getCipherSpec());
					}
				}
				else
				{
					log.info("Public key is not found!");
					throw new PublicKeyUnavailableException(partnerName);
				}
			}
			else
			{
				throw new DistributedNodeFailedException(partnerName);
			}
		}
		else
		{
			throw new CryptographyMismatchException(CryConfig.SYMMETRIC_CRYPTOGRAPHY);
		}
		return null;
	}

//	public ServerMessage readBySignature(String partnerName, Request request) throws ClassNotFoundException, RemoteReadException, IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, SignatureException, CryptographyMismatchException, DistributedNodeFailedException, OwnerCheatingException, CheatingException, PublicKeyUnavailableException
	public ServerMessage readBySignature(String partnerName, ServerMessage request) throws ClassNotFoundException, RemoteReadException, IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, SignatureException, CryptographyMismatchException, DistributedNodeFailedException, OwnerCheatingException, CheatingException, PublicKeyUnavailableException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
//		if (this.isAsymCryptography)
		if (this.isSigned())
		{
			String peerKey = PeerContainer.getPeerKey(partnerName);
//			IPAddress ip = this.getIPAddress(peerKey);
			IPAddress ip = this.getIPAddress(partnerName);
			if (ip != null)
			{
//				log.info("partner IP = " + ip);
				PublicCrypto publicKey = ServiceProvider.CRY().getPublicCryptoByPeer(peerKey);
				if (publicKey != null)
				{
//					SymmetricCrypto scrypto = SymmetricCoder.generateCrypto(this.symCipherAlgorithm, this.symCipherSpec, this.symCipherKeyLength, this.symIVKeyLength, this.peer.getPeerID());
					AsymCompCrypto scrypto = AsymmetricCoder.generateCompCrypto(this.symCipherAlgorithm, this.symCipherSpec, this.symCipherKeyLength, this.symIVKeyLength, this.peer.getPeerID());
//					byte[] enData = SymmetricCoder.encryptRequest(request, scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec());
					byte[] enData = SymmetricCoder.encryptMessage(request, scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec());
					byte[] enScrypto = AsymmetricCoder.encrypt(scrypto, publicKey.getAsymAlgorithm(), publicKey.getPublicKey());
					byte[] signedInfo = AsymmetricCoder.sign(ServiceProvider.CRY().getSignatureAlgorithm(), ServiceProvider.CRY().getPrivateKey(), ServiceProvider.CRY().getSignature());
					log.info("Your signature is " + ServiceProvider.CRY().getSignature());
					log.info("Encrypted Request: " + Hex.toHexString(enData));
					SignedAsymmetricEncryptedResponse response = (SignedAsymmetricEncryptedResponse)this.peer.read(ip.getIP(), ip.getPort(), new SignedAsymmetricEncryptedRequest(publicKey.getSessionKey(), enData, enScrypto, ServiceProvider.CRY().getSignature(), signedInfo));
					if (response != null)
					{
//						if (AsymmetricCoder.verify(ServiceProvider.CRY().getAsymmetricAlgorithm(), publicKey.getPublicKey(), publicKey.getSignature(), response.getSignature()))
//						if (AsymmetricCoder.verify(publicKey.getSignatureAlgorithm(), publicKey.getPublicKey(), publicKey.getSignature(), response.getEncryptedSignature()))
//						if (response.isOwner())
//						{
						if (ServiceProvider.CRY().isSignatureTrusted(response.getSignature(), response.getEncryptedSignature(), false))
						{
							log.info(publicKey.getSignature() + " is responding to you!");
							SymmetricCrypto resScrypto = (SymmetricCrypto)AsymmetricCoder.decrypt(response.getEncryptedSymCrypto(), ServiceProvider.CRY().getAsymmetricAlgorithm(), ServiceProvider.CRY().getPrivateKey());
							log.info("Encrypted Response: " + Hex.toHexString(response.getEncryptedData()));
							return SymmetricCoder.decryptResponse(response.getEncryptedData(), resScrypto.getCipherKey(), resScrypto.getIVKey(), resScrypto.getCipherSpec());
						}
						else
						{
							log.info("Someone is cheating ...");
							throw new CheatingException(publicKey.getSignature());
						}
//						}
//						else
//						{
//							throw new OwnerCheatingException(response.getCorrectOwnerName());
//						}
					}
					else
					{
//						log.info("response is null in Peer ...");
					}
				}
				else
				{
					log.info("Public key is not found!");
					throw new PublicKeyUnavailableException(partnerName);
				}
			}
			else
			{
				log.info("DistributedNodeFailedException in Peer ...");
				throw new DistributedNodeFailedException(partnerName);
			}
		}
		else
		{
			log.info("CryptographyMismatchException in Peer ...");
			throw new CryptographyMismatchException(CryConfig.SYMMETRIC_CRYPTOGRAPHY);
		}
//		log.info("Last: response is null in Peer ...");
		return null;
	}

	public ServerMessage prmReadBySignature(String partnerName, ServerMessage request) throws ClassNotFoundException, RemoteReadException, IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, SignatureException, CryptographyMismatchException, DistributedNodeFailedException, OwnerCheatingException, CheatingException, PublicKeyUnavailableException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		if (this.isSigned())
		{
			String peerKey = PeerContainer.getPeerKey(partnerName);
			IPAddress ip = this.getIPAddress(partnerName);
			if (ip != null)
			{
				PublicCrypto publicKey = ServiceProvider.CRY().getPublicCryptoByPeer(peerKey);
				if (publicKey != null)
				{
					AsymCompCrypto scrypto = AsymmetricCoder.generateCompCrypto(this.symCipherAlgorithm, this.symCipherSpec, this.symCipherKeyLength, this.symIVKeyLength, this.peer.getPeerID());
					byte[] enData = SymmetricCoder.encryptMessage(request, scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec());
					byte[] enScrypto = AsymmetricCoder.encrypt(scrypto, publicKey.getAsymAlgorithm(), publicKey.getPublicKey());
					byte[] signedInfo = AsymmetricCoder.sign(ServiceProvider.CRY().getSignatureAlgorithm(), ServiceProvider.CRY().getPrivateKey(), ServiceProvider.CRY().getSignature());
					log.info("Your signature is " + ServiceProvider.CRY().getSignature());
					log.info("Encrypted Request: " + Hex.toHexString(enData));
					SignedAsymmetricEncryptedResponse response = (SignedAsymmetricEncryptedResponse)this.peer.read(ip.getIP(), ip.getPort(), new SignedPrimitiveRequest(publicKey.getSessionKey(), enData, enScrypto, ServiceProvider.CRY().getSignature(), signedInfo));
					if (response != null)
					{
						if (ServiceProvider.CRY().isSignatureTrusted(response.getSignature(), response.getEncryptedSignature(), false))
						{
							log.info(publicKey.getSignature() + " is responding to you!");
							SymmetricCrypto resScrypto = (SymmetricCrypto)AsymmetricCoder.decrypt(response.getEncryptedSymCrypto(), ServiceProvider.CRY().getAsymmetricAlgorithm(), ServiceProvider.CRY().getPrivateKey());
							log.info("Encrypted Response: " + Hex.toHexString(response.getEncryptedData()));
							return SymmetricCoder.decryptResponse(response.getEncryptedData(), resScrypto.getCipherKey(), resScrypto.getIVKey(), resScrypto.getCipherSpec());
						}
						else
						{
							log.info("Someone is cheating ...");
							throw new CheatingException(publicKey.getSignature());
						}
					}
					else
					{
						log.info("response is null in Peer ...");
					}
				}
				else
				{
					log.info("Public key is not found!");
					throw new PublicKeyUnavailableException(partnerName);
				}
			}
			else
			{
				log.info("DistributedNodeFailedException in Peer ...");
				throw new DistributedNodeFailedException(partnerName);
			}
		}
		else
		{
			log.info("CryptographyMismatchException in Peer ...");
			throw new CryptographyMismatchException(CryConfig.SYMMETRIC_CRYPTOGRAPHY);
		}
		return null;
	}

//	public boolean claimOwner(String ownerName, String partnerName) throws ClassNotFoundException, RemoteReadException, IOException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, ShortBufferException, CheatingException, PublicKeyUnavailableException, DistributedNodeFailedException, CryptographyMismatchException
	public AllOwners claimOwner(String ownerName, String partnerName) throws ClassNotFoundException, RemoteReadException, IOException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, ShortBufferException, CheatingException, PublicKeyUnavailableException, DistributedNodeFailedException, CryptographyMismatchException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		if (this.isAsymCryptography.get())
		{
			String peerKey = PeerContainer.getPeerKey(partnerName);
//			IPAddress ip = this.getIPAddress(peerKey);
			IPAddress ip = this.getIPAddress(partnerName);
			if (ip != null)
			{
//				log.info("partner IP = " + ip);
				PublicCrypto publicKey = ServiceProvider.CRY().getPublicCryptoByPeer(peerKey);
				if (publicKey != null)
				{
//					SymmetricCrypto scrypto = SymmetricCoder.generateCrypto(this.symCipherAlgorithm, this.symCipherSpec, this.symCipherKeyLength, this.symIVKeyLength, this.peer.getPeerID());
					AsymCompCrypto scrypto = AsymmetricCoder.generateCompCrypto(this.symCipherAlgorithm, this.symCipherSpec, this.symCipherKeyLength, this.symIVKeyLength, this.peer.getPeerID());
//					byte[] enData = SymmetricCoder.encryptObject(new OwnerInfo(ServiceProvider.CRY().getAsymmetricSessionKey(), ServiceProvider.CRY().getSignature(), ownerName), scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec());
					byte[] enData = SymmetricCoder.encryptObject(new OwnerInfo(ServiceProvider.CRY().getSignature(), ownerName), scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec());
//					byte[] enData = SymmetricCoder.encryptObject(new OwnerInfo(publicKey.getSessionKey(), ServiceProvider.CRY().getSignature(), ownerName), scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec());
					byte[] enScrypto = AsymmetricCoder.encrypt(scrypto, publicKey.getAsymAlgorithm(), publicKey.getPublicKey());
					byte[] signedInfo = AsymmetricCoder.sign(ServiceProvider.CRY().getSignatureAlgorithm(), ServiceProvider.CRY().getPrivateKey(), ServiceProvider.CRY().getSignature());
					log.info("Your signature is " + ServiceProvider.CRY().getSignature());
					log.info("Encrypted Request: " + Hex.toHexString(enData));
					OwnershipResponse response = (OwnershipResponse)this.peer.read(ip.getIP(), ip.getPort(), new OwnershipRequest(publicKey.getSessionKey(), enData, enScrypto, ServiceProvider.CRY().getSignature(), signedInfo));
					if (response != null)
					{
						if (ServiceProvider.CRY().isSignatureTrusted(response.getSignature(), response.getEncryptedSignature(), false))
						{
							log.info(publicKey.getSignature() + " is responding to you!");
							SymmetricCrypto resScrypto = (SymmetricCrypto)AsymmetricCoder.decrypt(response.getEncryptedSymCrypto(), ServiceProvider.CRY().getAsymmetricAlgorithm(), ServiceProvider.CRY().getPrivateKey());
							log.info("Encrypted Response: " + Hex.toHexString(response.getEncryptedData()));
//							return (Boolean)SymmetricCoder.decryptObject(response.getEncryptedData(), resScrypto.getCipherKey(), resScrypto.getIVKey(), resScrypto.getCipherSpec());
							return (AllOwners)SymmetricCoder.decryptObject(response.getEncryptedData(), resScrypto.getCipherKey(), resScrypto.getIVKey(), resScrypto.getCipherSpec());
						}
						else
						{
							log.info("Someone is cheating ...");
							throw new CheatingException(publicKey.getSignature());
						}
					}
				}
				else
				{
					log.info("Public key is not found!");
					throw new PublicKeyUnavailableException(partnerName);
				}
			}
			else
			{
				throw new DistributedNodeFailedException(partnerName);
			}
		}
		else
		{
			throw new CryptographyMismatchException(CryConfig.SYMMETRIC_CRYPTOGRAPHY);
		}
		return new AllOwners(null, false);
	}

	public AllOwners abandonOwner(String ownerName, String partnerName) throws ClassNotFoundException, RemoteReadException, IOException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, ShortBufferException, CheatingException, PublicKeyUnavailableException, DistributedNodeFailedException, CryptographyMismatchException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		if (this.isAsymCryptography.get())
		{
			String peerKey = PeerContainer.getPeerKey(partnerName);
			IPAddress ip = this.getIPAddress(partnerName);
			if (ip != null)
			{
				PublicCrypto publicKey = ServiceProvider.CRY().getPublicCryptoByPeer(peerKey);
				if (publicKey != null)
				{
					AsymCompCrypto scrypto = AsymmetricCoder.generateCompCrypto(this.symCipherAlgorithm, this.symCipherSpec, this.symCipherKeyLength, this.symIVKeyLength, this.peer.getPeerID());
					byte[] enData = SymmetricCoder.encryptObject(new OwnerInfo(ServiceProvider.CRY().getSignature(), ownerName), scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec());
					byte[] enScrypto = AsymmetricCoder.encrypt(scrypto, publicKey.getAsymAlgorithm(), publicKey.getPublicKey());
					byte[] signedInfo = AsymmetricCoder.sign(ServiceProvider.CRY().getSignatureAlgorithm(), ServiceProvider.CRY().getPrivateKey(), ServiceProvider.CRY().getSignature());
					log.info("Your signature is " + ServiceProvider.CRY().getSignature());
					log.info("Encrypted Request: " + Hex.toHexString(enData));
					AbandonOwnershipResponse response = (AbandonOwnershipResponse)this.peer.read(ip.getIP(), ip.getPort(), new AbandonOwnershipRequest(publicKey.getSessionKey(), enData, enScrypto, ServiceProvider.CRY().getSignature(), signedInfo));
					if (response != null)
					{
						if (ServiceProvider.CRY().isSignatureTrusted(response.getSignature(), response.getEncryptedSignature(), false))
						{
							log.info(publicKey.getSignature() + " is responding to you!");
							SymmetricCrypto resScrypto = (SymmetricCrypto)AsymmetricCoder.decrypt(response.getEncryptedSymCrypto(), ServiceProvider.CRY().getAsymmetricAlgorithm(), ServiceProvider.CRY().getPrivateKey());
							log.info("Encrypted Response: " + Hex.toHexString(response.getEncryptedData()));
							return (AllOwners)SymmetricCoder.decryptObject(response.getEncryptedData(), resScrypto.getCipherKey(), resScrypto.getIVKey(), resScrypto.getCipherSpec());
						}
						else
						{
							log.info("Someone is cheating ...");
							throw new CheatingException(publicKey.getSignature());
						}
					}
				}
				else
				{
					log.info("Public key is not found!");
					throw new PublicKeyUnavailableException(partnerName);
				}
			}
			else
			{
				throw new DistributedNodeFailedException(partnerName);
			}
		}
		else
		{
			throw new CryptographyMismatchException(CryConfig.SYMMETRIC_CRYPTOGRAPHY);
		}
		return new AllOwners(null, false);
	}

	public ServerMessage readPrivately(String ownerName, String partnerName, Request request) throws ClassNotFoundException, RemoteReadException, IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, SignatureException, CryptographyMismatchException, DistributedNodeFailedException, OwnerCheatingException, CheatingException, PublicKeyUnavailableException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		if (this.isAsymCryptography.get())
		{
			String peerKey = PeerContainer.getPeerKey(partnerName);
//			IPAddress ip = this.getIPAddress(peerKey);
			IPAddress ip = this.getIPAddress(partnerName);
			if (ip != null)
			{
//				log.info("partner IP = " + ip);
				PublicCrypto publicKey = ServiceProvider.CRY().getPublicCryptoByPeer(peerKey);
				if (publicKey != null)
				{
//					SymmetricCrypto scrypto = SymmetricCoder.generateCrypto(this.symCipherAlgorithm, this.symCipherSpec, this.symCipherKeyLength, this.symIVKeyLength, this.peer.getPeerID());
					AsymCompCrypto scrypto = AsymmetricCoder.generateCompCrypto(this.symCipherAlgorithm, this.symCipherSpec, this.symCipherKeyLength, this.symIVKeyLength, this.peer.getPeerID());
					byte[] enData = SymmetricCoder.encryptRequest(request, scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec());
					byte[] enScrypto = AsymmetricCoder.encrypt(scrypto, publicKey.getAsymAlgorithm(), publicKey.getPublicKey());
					byte[] signedInfo = AsymmetricCoder.sign(ServiceProvider.CRY().getSignatureAlgorithm(), ServiceProvider.CRY().getPrivateKey(), ServiceProvider.CRY().getSignature());
					log.info("Your signature is " + ServiceProvider.CRY().getSignature());
					log.info("Encrypted Request: " + Hex.toHexString(enData));
//					SignedAsymmetricEncryptedResponse response = (SignedAsymmetricEncryptedResponse)this.peer.read(ip.getIP(), ip.getPort(), new SignedAsymmetricEncryptedRequest(publicKey.getSessionKey(), enData, enScrypto, ServiceProvider.CRY().getSignature(), signedInfo, ownerName));
					PrivateResponse response = (PrivateResponse)this.peer.read(ip.getIP(), ip.getPort(), new PrivateRequest(publicKey.getSessionKey(), enData, enScrypto, ServiceProvider.CRY().getSignature(), signedInfo, ownerName));
					if (response != null)
					{
//						if (AsymmetricCoder.verify(ServiceProvider.CRY().getAsymmetricAlgorithm(), publicKey.getPublicKey(), publicKey.getSignature(), response.getSignature()))
//						if (AsymmetricCoder.verify(publicKey.getSignatureAlgorithm(), publicKey.getPublicKey(), publicKey.getSignature(), response.getEncryptedSignature()))
						if (response.isOwner())
						{
							if (ServiceProvider.CRY().isSignatureTrusted(response.getSignature(), response.getEncryptedSignature(), false))
							{
								log.info(publicKey.getSignature() + " is responding to you!");
								SymmetricCrypto resScrypto = (SymmetricCrypto)AsymmetricCoder.decrypt(response.getEncryptedSymCrypto(), ServiceProvider.CRY().getAsymmetricAlgorithm(), ServiceProvider.CRY().getPrivateKey());
								log.info("Encrypted Response: " + Hex.toHexString(response.getEncryptedData()));
								return SymmetricCoder.decryptResponse(response.getEncryptedData(), resScrypto.getCipherKey(), resScrypto.getIVKey(), resScrypto.getCipherSpec());
							}
							else
							{
								log.info("Someone is cheating ...");
								throw new CheatingException(publicKey.getSignature());
							}
						}
						else
						{
							throw new OwnerCheatingException(response.getOwnerName());
						}
					}
				}
				else
				{
					log.info("Public key is not found!");
					throw new PublicKeyUnavailableException(partnerName);
				}
			}
			else
			{
				throw new DistributedNodeFailedException(partnerName);
			}
		}
		else
		{
			throw new CryptographyMismatchException(CryConfig.SYMMETRIC_CRYPTOGRAPHY);
		}
		return null;
	}

	public ServerMessage prmReadPrivately(String ownerName, String partnerName, ServerMessage request) throws ClassNotFoundException, RemoteReadException, IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, SignatureException, CryptographyMismatchException, DistributedNodeFailedException, OwnerCheatingException, CheatingException, PublicKeyUnavailableException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		if (this.isAsymCryptography.get())
		{
			String peerKey = PeerContainer.getPeerKey(partnerName);
			IPAddress ip = this.getIPAddress(partnerName);
			if (ip != null)
			{
				PublicCrypto publicKey = ServiceProvider.CRY().getPublicCryptoByPeer(peerKey);
				if (publicKey != null)
				{
					AsymCompCrypto scrypto = AsymmetricCoder.generateCompCrypto(this.symCipherAlgorithm, this.symCipherSpec, this.symCipherKeyLength, this.symIVKeyLength, this.peer.getPeerID());
					byte[] enData = SymmetricCoder.encryptMessage(request, scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec());
					byte[] enScrypto = AsymmetricCoder.encrypt(scrypto, publicKey.getAsymAlgorithm(), publicKey.getPublicKey());
					byte[] signedInfo = AsymmetricCoder.sign(ServiceProvider.CRY().getSignatureAlgorithm(), ServiceProvider.CRY().getPrivateKey(), ServiceProvider.CRY().getSignature());
					log.info("Your signature is " + ServiceProvider.CRY().getSignature());
					log.info("Encrypted Request: " + Hex.toHexString(enData));
					PrivateResponse response = (PrivateResponse)this.peer.read(ip.getIP(), ip.getPort(), new PrivatePrimitiveRequest(publicKey.getSessionKey(), enData, enScrypto, ServiceProvider.CRY().getSignature(), signedInfo, ownerName));
					if (response != null)
					{
						if (response.isOwner())
						{
							if (ServiceProvider.CRY().isSignatureTrusted(response.getSignature(), response.getEncryptedSignature(), false))
							{
								log.info(publicKey.getSignature() + " is responding to you!");
								SymmetricCrypto resScrypto = (SymmetricCrypto)AsymmetricCoder.decrypt(response.getEncryptedSymCrypto(), ServiceProvider.CRY().getAsymmetricAlgorithm(), ServiceProvider.CRY().getPrivateKey());
								log.info("Encrypted Response: " + Hex.toHexString(response.getEncryptedData()));
								return SymmetricCoder.decryptResponse(response.getEncryptedData(), resScrypto.getCipherKey(), resScrypto.getIVKey(), resScrypto.getCipherSpec());
							}
							else
							{
								log.info("Someone is cheating ...");
								throw new CheatingException(publicKey.getSignature());
							}
						}
						else
						{
							throw new OwnerCheatingException(response.getOwnerName());
						}
					}
				}
				else
				{
					log.info("Public key is not found!");
					throw new PublicKeyUnavailableException(partnerName);
				}
			}
			else
			{
				throw new DistributedNodeFailedException(partnerName);
			}
		}
		else
		{
			throw new CryptographyMismatchException(CryConfig.SYMMETRIC_CRYPTOGRAPHY);
		}
		return null;
	}

	/*
	public void stopRegistryServer() throws IOException, InterruptedException
	{
		this.peer.syncNotify(this.peer.getRegistryServerIP(), this.peer.getRegistryServerPort(), new ShutdownServerNotification());
	}
	*/

	/*
	public void setOwner(OwnerInfo oi)
	{
		ServiceProvider.CRY().setOwner(oi);
	}
	*/
	
	public Collection<OwnerInfo> getOwners()
	{
		return ServiceProvider.CRY().getOwners();
	}

	/*
	public boolean isOwner(String owner, Notification notification) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, SignatureException, IOException
	{
		return ServiceProvider.CRY().isOwner(owner, notification);
	}
	
	public boolean isOwner(String owner, Request request) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, SignatureException, IOException
	{
		return ServiceProvider.CRY().isOwner(owner, request);
	}
	*/

//	public IPAddress getIPAddress(String peerKey) throws ClassNotFoundException, RemoteReadException, IOException
	public IPAddress getIPAddress(String peerName) throws ClassNotFoundException, RemoteReadException, PeerNotRegisteredException, RemoteIPNotExistedException
	{
		IPAddress ip = ServiceProvider.CRY().getIP(peerName);
		if (ip == null)
		{
			ip = ((PeerAddressResponse)this.peer.read(this.peer.getRegistryServerIP(), this.peer.getRegistryServerPort(), new PeerAddressRequest(PeerContainer.getPeerKey(peerName)))).getPeerAddress();
			if (ip != null)
			{
				ServiceProvider.CRY().addIP(peerName, ip);
			}
			else
			{
				throw new PeerNotRegisteredException(peerName);
			}
		}
		return ip;
	}

	/*
	public void syncCryptoNotifyByIP(String ip, int port, Notification notification, int cryptoOption) throws IOException, InterruptedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SymmetricKeyUnavailableException, CryptographyMismatchException, PublicKeyUnavailableException, DistributedNodeFailedException, SignatureException
	{
		switch (cryptoOption)
		{
			case MulticastConfig.PLAIN:
				this.syncNotifyByIP(ip, port, notification);
				break;
				
			case MulticastConfig.SYM:
				this.syncNotifySymmetricallyByIP(ip, port, notification);
				break;

			case MulticastConfig.ASYM:
				this.syncNotifyAsymmetricallyByIP(ip, port, notification);
				break;

			case MulticastConfig.SIGNED:
				this.syncNotifyBySignatureByIP(ip, port, notification);
				break;
		}
	}
	*/

//	public void syncCryptoNotifyByName(String peerName, Notification notification, int cryptoOption) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException, DistributedNodeFailedException, NoSuchAlgorithmException, CryptographyMismatchException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SymmetricKeyUnavailableException, SignatureException, PublicKeyUnavailableException
	public void syncCryptoNotifyByName(String peerName, ServerMessage notification, int cryptoOption) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException, DistributedNodeFailedException, NoSuchAlgorithmException, CryptographyMismatchException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SymmetricKeyUnavailableException, SignatureException, PublicKeyUnavailableException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		switch (cryptoOption)
		{
			case MulticastConfig.PLAIN:
				this.syncNotify(peerName, notification);
				break;
				
			case MulticastConfig.SYM:
				this.setAsSym();
				if (!this.isSymPartnerInvited(peerName))
				{
					this.inviteSymPartner(peerName);
				}
				this.syncNotifySymmetrically(peerName, notification);
				break;
				
			case MulticastConfig.ASYM:
				this.setAsAsym();
				if (!this.isAsymPartnerInvited(peerName))
				{
					this.inviteAsymPartner(peerName);
				}
				this.syncNotifyAsymmetrically(peerName, notification);
				break;
				
			case MulticastConfig.SIGNED:
				this.setAsAsym();
				if (!this.isAsymPartnerInvited(peerName))
				{
					this.inviteAsymPartner(peerName);
				}
				this.syncNotifyBySignature(peerName, notification);
				break;
		}
	}

//	public ServerMessage cryptoReadByName(String peerName, Request request, int cryptoOption) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException, DistributedNodeFailedException, NoSuchAlgorithmException, CryptographyMismatchException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SymmetricKeyUnavailableException, SignatureException, PublicKeyUnavailableException, ShortBufferException, OwnerCheatingException, CheatingException
	public ServerMessage cryptoReadByName(String peerName, ServerMessage request, int cryptoOption) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException, DistributedNodeFailedException, NoSuchAlgorithmException, CryptographyMismatchException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SymmetricKeyUnavailableException, SignatureException, PublicKeyUnavailableException, ShortBufferException, OwnerCheatingException, CheatingException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		switch (cryptoOption)
		{
			case MulticastConfig.PLAIN:
				return this.read(peerName, request);
				
			case MulticastConfig.SYM:
				this.setAsSym();
				if (!this.isSymPartnerInvited(peerName))
				{
					this.inviteSymPartner(peerName);
				}
				return this.readSymmetrically(peerName, request);
				
			case MulticastConfig.ASYM:
				this.setAsAsym();
				if (!this.isAsymPartnerInvited(peerName))
				{
					this.inviteAsymPartner(peerName);
				}
				return this.readAsymmetrically(peerName, request);
				
			case MulticastConfig.SIGNED:
				this.setAsAsym();
				if (!this.isAsymPartnerInvited(peerName))
				{
					this.inviteAsymPartner(peerName);
				}
				return this.readBySignature(peerName, request);
		}
		return null;
	}

//	public void syncCryptoNotifyByIP(String peerName, IPAddress ip, Notification notification, int cryptoOption) throws IOException, InterruptedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SymmetricKeyUnavailableException, CryptographyMismatchException, PublicKeyUnavailableException, DistributedNodeFailedException, SignatureException, ClassNotFoundException, RemoteReadException
	public void syncCryptoNotifyByIP(String peerName, IPAddress ip, ServerMessage notification, int cryptoOption) throws IOException, InterruptedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SymmetricKeyUnavailableException, CryptographyMismatchException, PublicKeyUnavailableException, DistributedNodeFailedException, SignatureException, ClassNotFoundException, RemoteReadException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		switch (cryptoOption)
		{
			case MulticastConfig.PLAIN:
				this.syncNotifyByIP(ip.getIP(), ip.getPort(), notification);
				break;
				
			case MulticastConfig.SYM:
				this.setAsSym();
				if (!this.isSymPartnerInvited(peerName))
				{
					this.inviteSymPartner(peerName);
				}
				this.syncNotifySymmetricallyByIP(peerName, ip.getIP(), ip.getPort(), notification);
				break;

			case MulticastConfig.ASYM:
				this.setAsAsym();
				if (!this.isAsymPartnerInvited(peerName))
				{
					this.inviteAsymPartner(peerName);
				}
				this.syncNotifyAsymmetricallyByIP(peerName, ip.getIP(), ip.getPort(), notification);
				break;

			case MulticastConfig.SIGNED:
				this.setAsAsym();
				if (!this.isAsymPartnerInvited(peerName))
				{
					this.inviteAsymPartner(peerName);
				}
				this.syncNotifyBySignatureByIP(peerName, ip.getIP(), ip.getPort(), notification);
				break;
		}
	}

	public void syncCryptoNotifyByIP(String peerName, String ip, int port, Notification notification, int cryptoOption) throws IOException, InterruptedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SymmetricKeyUnavailableException, CryptographyMismatchException, PublicKeyUnavailableException, DistributedNodeFailedException, SignatureException, ClassNotFoundException, RemoteReadException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		switch (cryptoOption)
		{
			case MulticastConfig.PLAIN:
				this.syncNotifyByIP(ip, port, notification);
				break;
				
			case MulticastConfig.SYM:
				this.setAsSym();
				if (!this.isSymPartnerInvited(peerName))
				{
					this.inviteSymPartner(peerName);
				}
				this.syncNotifySymmetricallyByIP(peerName, ip, port, notification);
				break;

			case MulticastConfig.ASYM:
				this.setAsAsym();
				if (!this.isAsymPartnerInvited(peerName))
				{
					this.inviteAsymPartner(peerName);
				}
				this.syncNotifyAsymmetricallyByIP(peerName, ip, port, notification);
				break;

			case MulticastConfig.SIGNED:
				this.setAsAsym();
				if (!this.isAsymPartnerInvited(peerName))
				{
					this.inviteAsymPartner(peerName);
				}
				this.syncNotifyBySignatureByIP(peerName, ip, port, notification);
				break;
		}
	}

//	public void syncCryptoNotifyByIPKey(String ipKey, Notification notification, int cryptoOption) throws ClassNotFoundException, NoSuchAlgorithmException, RemoteReadException, DistributedNodeFailedException, CryptographyMismatchException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InterruptedException, SymmetricKeyUnavailableException, SignatureException, PublicKeyUnavailableException, IOException
	public void syncCryptoNotifyByIPKey(String ipKey, ServerMessage notification, int cryptoOption) throws ClassNotFoundException, NoSuchAlgorithmException, RemoteReadException, DistributedNodeFailedException, CryptographyMismatchException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InterruptedException, SymmetricKeyUnavailableException, SignatureException, PublicKeyUnavailableException, IOException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException, IPNotExistedException
	{
		try
		{
			String partnerName;
			switch (cryptoOption)
			{
				case MulticastConfig.PLAIN:
					this.syncNotifyByIPKey(ipKey, notification);
					break;
					
				case MulticastConfig.SYM:
					this.setAsSym();
					partnerName = this.getPartnerName(ipKey);
					if (!this.isSymPartnerInvited(partnerName))
					{
						this.inviteSymPartner(partnerName);
					}
					this.syncNotifySymmetricallyByIPKey(partnerName, ipKey, notification);
					break;
					
				case MulticastConfig.ASYM:
					this.setAsAsym();
					partnerName = this.getPartnerName(ipKey);
					if (!this.isAsymPartnerInvited(partnerName))
					{
						this.inviteAsymPartner(partnerName);
					}
					this.syncNotifyAsymmetricallyByIPKey(partnerName, ipKey, notification);
					break;
					
				case MulticastConfig.SIGNED:
					this.setAsAsym();
					partnerName = this.getPartnerName(ipKey);
					if (!this.isAsymPartnerInvited(partnerName))
					{
						this.inviteAsymPartner(partnerName);
					}
					this.syncNotifyBySignatureByIPKey(partnerName, ipKey, notification);
					break;
			}
		}
		catch (IOException e)
		{
			this.removeClient(ipKey);
			throw new DistributedNodeFailedException(ipKey);
		}
	}

	private void syncNotifyAsymBye(String partnerName, AsymmetricBye bye) throws ClassNotFoundException, RemoteReadException, IOException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InterruptedException, PublicKeyUnavailableException, DistributedNodeFailedException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		String peerKey = PeerContainer.getPeerKey(partnerName);
		IPAddress ip = this.getIPAddress(partnerName);
		if (ip != null)
		{
			PublicCrypto publicKey = ServiceProvider.CRY().getPublicCryptoByPeer(peerKey);
			if (publicKey != null)
			{
//				SymmetricCrypto scrypto = SymmetricCoder.generateCrypto(this.symCipherAlgorithm, this.symCipherSpec, this.symCipherKeyLength, this.symIVKeyLength, this.peer.getPeerID());
				AsymCompCrypto scrypto = AsymmetricCoder.generateCompCrypto(this.symCipherAlgorithm, this.symCipherSpec, this.symCipherKeyLength, this.symIVKeyLength, this.peer.getPeerID());
				byte[] enData = SymmetricCoder.encryptObject(bye, scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec());
				log.info("Encrypted Bye: " + Hex.toHexString(enData));
				byte[] enScrypto = AsymmetricCoder.encrypt(scrypto, publicKey.getAsymAlgorithm(), publicKey.getPublicKey());
				this.peer.syncNotify(ip.getIP(), ip.getPort(), new SayAsymmetricByeNotification(publicKey.getSessionKey(), enData, enScrypto));
			}
			else
			{
				log.info("Public key is not found!");
				throw new PublicKeyUnavailableException(partnerName);
			}
		}
		else
		{
			log.info(partnerName + "'s IP is not found!");
			throw new DistributedNodeFailedException(partnerName);
		}
	}

	private void syncNotifySymBye(String partnerName, SymmetricBye bye) throws ClassNotFoundException, RemoteReadException, IOException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InterruptedException, PublicKeyUnavailableException, DistributedNodeFailedException, SymmetricKeyUnavailableException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		String peerKey = PeerContainer.getPeerKey(partnerName);
		IPAddress ip = this.getIPAddress(partnerName);
		if (ip != null)
		{
			SymmetricCrypto symCrypto = ServiceProvider.CRY().getSymmetricCrypto(peerKey);
			if (symCrypto != null)
			{
				byte[] enData = SymmetricCoder.encryptObject(bye, symCrypto.getCipherKey(), symCrypto.getIVKey(), symCrypto.getCipherSpec());
				log.info("Encrypted Notification: " + Hex.toHexString(enData));
//				this.peer.syncNotify(ip.getIP(), ip.getPort(), new SaySymmetricByeNotification(symCrypto.getSessionKey(), enData));
				this.peer.syncNotify(ip.getIP(), ip.getPort(), new SaySymmetricByeNotification(this.peer.getPeerID(), enData));
			}
			else
			{
				throw new SymmetricKeyUnavailableException(partnerName);
			}
		}
		else
		{
			throw new DistributedNodeFailedException(partnerName);
		}
	}

	public void syncPrmNotifyByName(String peerName, ServerMessage notification, int cryptoOption) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException, DistributedNodeFailedException, NoSuchAlgorithmException, CryptographyMismatchException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SymmetricKeyUnavailableException, SignatureException, PublicKeyUnavailableException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		switch (cryptoOption)
		{
			case MulticastConfig.PLAIN:
				this.syncNotify(peerName, notification);
				break;
				
			case MulticastConfig.SYM:
				this.setAsSym();
				if (!this.isSymPartnerInvited(peerName))
				{
					this.inviteSymPartner(peerName);
				}
				this.syncPrmNotifySymmetrically(peerName, notification);
				break;
				
			case MulticastConfig.ASYM:
				this.setAsAsym();
				if (!this.isAsymPartnerInvited(peerName))
				{
					this.inviteAsymPartner(peerName);
				}
				this.syncPrmNotifyAsymmetrically(peerName, notification);
				break;
				
			case MulticastConfig.SIGNED:
				this.setAsAsym();
				if (!this.isAsymPartnerInvited(peerName))
				{
					this.inviteAsymPartner(peerName);
				}
				this.syncPrmNotifyBySignature(peerName, notification);
				break;
				
			case MulticastConfig.PRIVATE:
				this.setAsAsym();
				if (!this.isAsymPartnerInvited(peerName))
				{
					this.inviteAsymPartner(peerName);
				}
				this.syncPrmNotifyPrivately(this.peer.getPeerName(), peerName, notification);
				break;
		}
	}

	public ServerMessage prmReadByName(String peerName, ServerMessage request, int cryptoOption) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException, DistributedNodeFailedException, NoSuchAlgorithmException, CryptographyMismatchException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SymmetricKeyUnavailableException, SignatureException, PublicKeyUnavailableException, ShortBufferException, OwnerCheatingException, CheatingException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		switch (cryptoOption)
		{
			case MulticastConfig.PLAIN:
				return this.read(peerName, request);
				
			case MulticastConfig.SYM:
				this.setAsSym();
				if (!this.isSymPartnerInvited(peerName))
				{
					this.inviteSymPartner(peerName);
				}
				return this.prmReadSymmetrically(peerName, request);
				
			case MulticastConfig.ASYM:
				this.setAsAsym();
				if (!this.isAsymPartnerInvited(peerName))
				{
					this.inviteAsymPartner(peerName);
				}
				return this.prmReadAsymmetrically(peerName, request);
				
			case MulticastConfig.SIGNED:
				this.setAsAsym();
				if (!this.isAsymPartnerInvited(peerName))
				{
					this.inviteAsymPartner(peerName);
				}
				return this.prmReadBySignature(peerName, request);
				
			case MulticastConfig.PRIVATE:
				this.setAsAsym();
				if (!this.isAsymPartnerInvited(peerName))
				{
					this.inviteAsymPartner(peerName);
				}
				return this.prmReadPrivately(this.peer.getPeerName(), peerName, request);
		}
		return null;
	}
}
