package edu.greatfree.cry.server;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import org.bouncycastle.util.encoders.Hex;
import org.greatfree.exceptions.PeerNameIsNullException;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;
import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.IntercastRequest;
import org.greatfree.server.container.PeerContainer;
import org.greatfree.server.container.ServerTask;
import org.greatfree.util.IPAddress;

import edu.greatfree.cry.AsymCompCrypto;
import edu.greatfree.cry.AsymmetricCoder;
import edu.greatfree.cry.AsymmetricCrypto;
import edu.greatfree.cry.PublicCrypto;
import edu.greatfree.cry.SymmetricCoder;
import edu.greatfree.cry.SymmetricCrypto;
import edu.greatfree.cry.cluster.ClusterTask;
import edu.greatfree.cry.exceptions.CheatingException;
import edu.greatfree.cry.exceptions.IPNotExistedException;
import edu.greatfree.cry.exceptions.MachineNotOwnedException;
import edu.greatfree.cry.exceptions.NonPrivateMachineException;
import edu.greatfree.cry.exceptions.NonPublicMachineException;
import edu.greatfree.cry.exceptions.OwnerCheatingException;
import edu.greatfree.cry.exceptions.PublicKeyUnavailableException;
import edu.greatfree.cry.exceptions.SessionMismatchedException;
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
import edu.greatfree.cry.messege.OwnerJoinNotification;
import edu.greatfree.cry.messege.OwnerLeaveNotification;
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
import edu.greatfree.cry.messege.cluster.ChildrenMessages;
import edu.greatfree.cry.messege.multicast.ChildRootRequest;
import edu.greatfree.cry.messege.multicast.ClusterNotification;
import edu.greatfree.cry.messege.multicast.ClusterRequest;
import edu.greatfree.cry.messege.multicast.PrimitiveMulticastNotification;
import edu.greatfree.cry.messege.multicast.PrimitiveMulticastRequest;
import edu.greatfree.cry.messege.multicast.PrimitiveMulticastResponse;
import edu.greatfree.cry.multicast.MulticastTask;

/**
 * 
 * @author libing
 * 
 *         01/04/2022, Bing Li
 *
 */
public final class ServiceProvider
{
	private final static Logger log = Logger.getLogger("edu.greatfree.cry.server");

	private String hostPeerName;
	/*
	 * With the structure, the IP address can be retrieved by the partner's name. 04/14/2022, Bing Li
	 */
	private Map<String, IPAddress> ips;
	/*
	 * With the structure, the partner's name can be retrieved by the IP key. 04/14/2022, Bing Li
	 */
	private Map<String, String> ipPeers;
	
	private Map<String, ServerTask> tasks;
//	private Map<String, MulticastTask> tasks;

	private Map<String, SymmetricCrypto> symmetricCryptos;

	private AsymmetricCrypto asymCrypto;
	// The public keys are generated by remote partners. Using them, the messages to those partners can be encrypted. The key of the map is the session key of the AsymmetricCrypto of those remote partners. 01/11/2022, Bing Li
	private Map<String, PublicCrypto> publicCryptos;
	// The collection is used to map the peer key to the session key, which is used to retrieve the partner's public key. 01/11/2022, Bing Li
	private Map<String, String> peerSessionKeys;
	private Map<String, PublicCrypto> signaturePublicKeys;

	/*
	 * The below properties define the constraints for ownership. 03/22/2022, Bing Li
	 */
//	private boolean isPrivate;
	private AtomicBoolean isPrivate;
	private Map<String, OwnerInfo> owners;
	private AtomicInteger ownerSize;
	
//	private AtomicBoolean isBye;

	private ServiceProvider()
	{
		this.tasks = new ConcurrentHashMap<String, ServerTask>();
//		this.tasks = new ConcurrentHashMap<String, MulticastTask>();
		this.symmetricCryptos = new ConcurrentHashMap<String, SymmetricCrypto>();
		this.publicCryptos = new ConcurrentHashMap<String, PublicCrypto>();
		this.ips = new ConcurrentHashMap<String, IPAddress>();
		this.ipPeers = new ConcurrentHashMap<String, String>();
		
		this.peerSessionKeys = new ConcurrentHashMap<String, String>();
		this.signaturePublicKeys = new ConcurrentHashMap<String, PublicCrypto>();
		this.isPrivate = new AtomicBoolean(false);
		this.owners = new ConcurrentHashMap<String, OwnerInfo>();
		this.ownerSize = new AtomicInteger(0);
		
//		this.isBye = new AtomicBoolean(false);
	}
	
	private static ServiceProvider instance = new ServiceProvider();
	
	public static ServiceProvider CRY()
	{
		if (instance == null)
		{
			instance = new ServiceProvider();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * The initialization is called only by Server. 03/21/2022, Bing Li
	 */
//	public void init(String serverKey, MulticastTask task)
	public void init(String serverKey, ServerTask task)
	{
		this.tasks.put(serverKey, task);
	}

	/*
	public void init(String hostPeerName, String serverKey, ServerTask task)
	{
		this.hostPeerName = hostPeerName;
		this.tasks.put(serverKey, task);
	}
	*/

	/*
	 * The initialization is called only by Peer. 03/21/2022, Bing Li
	 */
//	public void init(String hostPeerName, String serverKey, MulticastTask task, AsymmetricCrypto asymCrypto)
	public void init(String hostPeerName, String serverKey, ServerTask task, AsymmetricCrypto asymCrypto)
	{
		this.hostPeerName = hostPeerName;
//		log.info("serverKey for task = " + serverKey);
		this.tasks.put(serverKey, task);
		this.asymCrypto = asymCrypto;
	}
	
//	public void removeAsymPartner(SayAsymmetricByeNotification notification) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException, InvalidAlgorithmParameterException, ShortBufferException, SessionMismatchedException
	public void removeAsymPartner(String serverKey, SayAsymmetricByeNotification notification) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException, InvalidAlgorithmParameterException, ShortBufferException, SessionMismatchedException
	{
//		this.isBye.set(true);
		if (this.asymCrypto.getSessionKey().equals(notification.getSessionKey()))
		{
//			SymmetricCrypto scrypto = (SymmetricCrypto)AsymmetricCoder.decrypt(notification.getEncryptedSymCrypto(), this.asymCrypto.getAsymAlgorithm(), this.asymCrypto.getPrivateKey());
			AsymCompCrypto scrypto = (AsymCompCrypto)AsymmetricCoder.decrypt(notification.getEncryptedSymCrypto(), this.asymCrypto.getAsymAlgorithm(), this.asymCrypto.getPrivateKey());
			log.info("Received Encrypted Notification: " + Hex.toHexString(notification.getEncryptedData()));
			AsymmetricBye bye = (AsymmetricBye)SymmetricCoder.decryptObject(notification.getEncryptedData(), scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec());
			this.publicCryptos.remove(bye.getPublicCryptoSessionKey());
			this.signaturePublicKeys.remove(bye.getSignature());
			this.peerSessionKeys.remove(bye.getHostPeerKey());
			this.ips.remove(bye.getHostPeerName());
			IPAddress ip = this.ips.get(bye.getHostPeerName());
			if (ip != null)
			{
				this.ipPeers.remove(ip.getIPKey());
			}
			if (this.owners.containsKey(bye.getSignature()))
			{
				this.owners.remove(bye.getSignature());
				/*
				 * This is the first time to raise an event to notify the upper level about the lower level updates. This is a good design. 03/24/2022, Bing Li
				 */
				this.tasks.get(serverKey).processNotification(new OwnerLeaveNotification(bye.getHostPeerName()));
			}
		}
		else
		{
			log.info("Session mismatched!");
			throw new SessionMismatchedException("The session of asymmetric encrypted notification mismatched!");
		}
	}
	
	public void removeSymPartner(SaySymmetricByeNotification notification) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, ShortBufferException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException
	{
//		this.isBye.set(true);
		SymmetricCrypto sc = this.symmetricCryptos.get(notification.getSessionKey());
		log.info("Received Encrypted Notification: " + Hex.toHexString(notification.getEncryptedData()));
		SymmetricBye bye = (SymmetricBye)SymmetricCoder.decryptObject(notification.getEncryptedData(), sc.getCipherKey(), sc.getIVKey(), sc.getCipherSpec());
		this.symmetricCryptos.remove(bye.getHostPeerKey());
		IPAddress ip = this.ips.get(bye.getHostPeerName());
		this.ips.remove(bye.getHostPeerName());
		if (ip != null)
		{
			this.ipPeers.remove(ip.getIPKey());
		}
	}

	public void setPrivate(boolean isPrivate)
	{
		this.isPrivate.set(isPrivate);
	}
	
	public void setOwnersSize(int size)
	{
		if (this.isPrivate.get())
		{
			this.ownerSize.set(size);
		}
	}
	
	public Collection<OwnerInfo> getOwners()
	{
		if (this.isPrivate.get())
		{
			return this.owners.values();
		}
		return null;
	}
	
	public void reset(AsymmetricCrypto asymCrypto)
	{
		this.asymCrypto = asymCrypto;
	}

	/*
	public boolean isOwnerSet()
	{
		return this.asymCrypto.isOwnerSet();
	}
	*/

	/*
	public void setOwner(OwnerInfo oi)
	{
		this.asymCrypto.setOwner(oi);
	}
	*/

	/*
	public OwnerInfo getOwner()
	{
		return this.asymCrypto.getOwner();
	}
	*/
	
	public boolean isPartnerExisted(String peerKey)
	{
		return this.peerSessionKeys.containsKey(peerKey);
	}
	
	public void setPublicCrypto(PublicCrypto crypto)
	{
//		log.info("crypto sessionKey = " + crypto.getSessionKey());
		this.publicCryptos.put(crypto.getSessionKey(), crypto);
//		this.publicCryptos.put(crypto.getPeerKey(), crypto);
	}
	
//	public PublicCrypto getPublicCrypto(String peerKey)
	/*
	public PublicCrypto getPublicCryptoByPeer(String peerKey)
	{
		
		return this.publicCryptos.get(sessionKey);
	}
	*/
	
	public Map<String, PublicCrypto> getPublicCryptos()
	{
		return this.publicCryptos;
	}

	public PublicCrypto getPublicCryptoByPeer(String peerKey)
	{
//		log.info("peerKey = " + peerKey);
		String sessionKey = this.peerSessionKeys.get(peerKey);
		if (sessionKey != null)
		{
			return this.publicCryptos.get(sessionKey);
		}
		else
		{
			log.info("sessionKey is NULL!");
		}
		return null;
	}
	
	public PublicCrypto getPublicCryptoBySession(String sessionKey)
	{
		return this.publicCryptos.get(sessionKey);
	}
	
	public String getAsymmetricSessionKey()
	{
		return this.asymCrypto.getSessionKey();
	}
	
	public String getAsymmetricAlgorithm()
	{
		return this.asymCrypto.getAsymAlgorithm();
	}
	
	public PrivateKey getPrivateKey()
	{
		return this.asymCrypto.getPrivateKey();
	}
	
	public PublicKey getPublicKey()
	{
		return this.asymCrypto.getPublicKey();
	}
	
	public String getSignatureAlgorithm()
	{
		return this.asymCrypto.getSignatureAlgorithm();
	}
	
	public String getSignature()
	{
		return this.asymCrypto.getSignature();
	}
	
	public void setSignature(String signature)
	{
		this.asymCrypto.setSignature(signature);
	}

	public IPAddress getIP(String peerName)
	{
		return this.ips.get(peerName);
	}
	
	public String getPartnerName(String ipKey) throws IPNotExistedException
	{
		if (this.ipPeers.containsKey(ipKey))
		{
			return this.ipPeers.get(ipKey);
		}
		else
		{
			throw new IPNotExistedException(ipKey);
		}
	}
	
	public void addIP(String peerName, IPAddress ip)
	{
		try
		{
			ip.setPeerName(peerName);
		}
		catch (PeerNameIsNullException e)
		{
			e.printStackTrace();
		}
		this.ips.put(peerName, ip);
		this.ipPeers.put(ip.getIPKey(), peerName);
	}
	
	public Collection<String> getAllPeerNames()
	{
		return this.ips.keySet();
	}

	/*
	private void addSession(Crypto crypto)
	{
		this.cryptos.put(crypto.getSessionKey(), crypto);
	}
	*/
	
	public boolean isSymPartnerInvited(String partnerKey)
	{
		return this.symmetricCryptos.containsKey(partnerKey);
	}
	
	public boolean isSymmetricKeyExisted(String partnerKey)
	{
		return this.symmetricCryptos.containsKey(partnerKey);
	}

//	public void retainSymmetricCrypto(String partnerKey, SymmetricCrypto symCrypto)
	public void retainSymmetricCrypto(SymmetricCrypto symCrypto)
	{
//		this.symmetricCryptos.put(symCrypto.getSessionKey(), symCrypto);
//		this.symmetricCryptos.put(partnerKey, symCrypto);
//		this.symmetricCryptos.put(symCrypto.getSourcePeerKey(), symCrypto);
		this.symmetricCryptos.put(symCrypto.getDestinationPeerKey(), symCrypto);
	}

	/*
	 * The sender invokes the method to get the shared key. The shared key is retrieved by the partner's key. 02/05/2022, Bing Li
	 */
	public SymmetricCrypto getSymmetricCrypto(String partnerKey)
	{
		return this.symmetricCryptos.get(partnerKey);
	}

	/*
	 * 
	 * The receiver invokes the method to retain the sender's shared key. The shared key is retrieved by the session key. 02/05/2022, Bing Li
	 */
	public SymmetricCryptoSessionResponse retainSymmetricCrypto(SymmetricCryptoSessionRequest request)
	{
//		this.symmetricCryptos.put(request.getCrypto().getSessionKey(), request.getCrypto());
		this.symmetricCryptos.put(request.getCrypto().getSourcePeerKey(), request.getCrypto());
//		this.symmetricCryptos.put(request.getCrypto().getPeerKey(), request.getCrypto());
		return new SymmetricCryptoSessionResponse(true);
	}

	/*
	public void processNotification(String serverKey, ServerMessage notification) throws NonPublicMachineException
	{
		if (!this.isPrivate)
		{
			this.tasks.get(serverKey).processNotification(notification);
		}
		else
		{
			throw new NonPublicMachineException("The machine is non-public!");
		}
	}
	*/

	public void processNotification(String serverKey, Notification notification) throws NonPublicMachineException
	{
		if (!this.isPrivate.get())
		{
//			log.info("Before processing service notifications ...");
			this.tasks.get(serverKey).processNotification(notification);
		}
		else
		{
			throw new NonPublicMachineException("The machine is non-public!");
		}
	}
	
//	public void processMulticastNotification(String serverKey, MulticastNotification notification) throws NonPublicMachineException
	public void processMulticastNotification(String serverKey, PrimitiveMulticastNotification notification) throws NonPublicMachineException
	{
		if (!this.isPrivate.get())
		{
			((MulticastTask)this.tasks.get(serverKey)).processNotification(notification);
		}
		else
		{
			throw new NonPublicMachineException("The machine is non-public!");
		}
	}

	/*
//	public void processClusterNotification(String serverKey, MulticastNotification notification) throws NonPublicMachineException
	public void processClusterNotification(String serverKey, PrimitiveMulticastNotification notification) throws NonPublicMachineException
	{
		if (!this.isPrivate)
		{
			((ClusterTask)this.tasks.get(serverKey)).processNotification(notification);
		}
		else
		{
			throw new NonPublicMachineException("The machine is non-public!");
		}
	}
	*/

//	public void processMulticastRequest(String serverKey, MulticastRequest notification) throws NonPublicMachineException
	public void processMulticastRequest(String serverKey, PrimitiveMulticastRequest notification) throws NonPublicMachineException
	{
		if (!this.isPrivate.get())
		{
			((MulticastTask)this.tasks.get(serverKey)).processRequest(notification);
		}
		else
		{
			throw new NonPublicMachineException("The machine is non-public!");
		}
	}

	/*
	public void processClusterRequest(String serverKey, PrimitiveMulticastRequest notification) throws NonPublicMachineException
	{
		if (!this.isPrivate)
		{
			((ClusterTask)this.tasks.get(serverKey)).processRequest(notification);
		}
		else
		{
			throw new NonPublicMachineException("The machine is non-public!");
		}
	}
	*/

	public void processClusterRootNotification(String serverKey, ServerMessage notification) throws NonPublicMachineException
	{
//			if (notification.getType() != MulticastMessageType.CLUSTER_NOTIFICATION)
		/*
		 * Some messages to the cluster root is not used for multicasting, but for cluster management. 06/19/2022, Bing Li
		 */
		if (notification.getType() != MulticastMessageType.MULTICAST_NOTIFICATION)
		{
			if (ChildrenMessages.CRY().isClusterMessage(notification.getType()))
			{
				/*
				 * Cluster management messages inherit from ServerMessage such that the notification is needed to be converted. 06/19/2022, Bing Li
				 */
				((ClusterTask)this.tasks.get(serverKey)).processNotification(notification);
			}
			else
			{
				/*
				 * Private messages are not processed in the method. 06/19/2022, Bing Li
				 */
				if (!this.isPrivate.get())
				{
					((ClusterTask)this.tasks.get(serverKey)).processNotification(notification);
				}
				else
				{
					throw new NonPublicMachineException("The machine is non-public!");
				}
			}
		}
		else
		{
			/*
			 * Cluster notifications inherit the multicasting notification. It is processed in below lines. 06/19/2022, Bing Li
			 */
			if (!this.isPrivate.get())
			{
				PrimitiveMulticastNotification pmn = (PrimitiveMulticastNotification)notification;
				if (pmn.getMultiAppID() == MulticastMessageType.CLUSTER_NOTIFICATION)
				{
					((ClusterTask)this.tasks.get(serverKey)).processNotification((ClusterNotification)pmn);
				}
				else
				{
					/*
					 * At least, the current code does not execute the below line. 06/19/2022, Bing Li
					 */
					((ClusterTask)this.tasks.get(serverKey)).processNotification(pmn);
				}
			}
			else
			{
				throw new NonPublicMachineException("The machine is non-public!");
			}
		}
	}

	private void processPrivateRootNotification(String serverKey, ServerMessage notification) throws NonPublicMachineException
	{
		((ClusterTask)this.tasks.get(serverKey)).processNotification((ClusterNotification)notification);
	}

	public ServerMessage processClusterRootRequest(String serverKey, ServerMessage request) throws NonPublicMachineException
	{
//		log.info("1) processClusterRootRequest() ...");
		/*
		switch (request.getType())
		{
			case MulticastMessageType.MULTICAST_REQUEST:
				log.info("MULTICAST_REQUEST received @" + Calendar.getInstance().getTime());
				return ((ClusterTask)this.tasks.get(serverKey)).processRequest((PrimitiveMulticastRequest)request);
				
			case MulticastMessageType.CHILD_ROOT_REQUEST:
				log.info("CHILD_ROOT_REQUEST received @" + Calendar.getInstance().getTime());
				return ((ClusterTask)this.tasks.get(serverKey)).processRequest((ChildRootRequest)request);
				
			default:
				return ((ClusterTask)this.tasks.get(serverKey)).processRequest(request);
		}
		*/
		if (request.getType() != MulticastMessageType.MULTICAST_REQUEST)
		{
//			log.info("2) processClusterRootRequest() ...");
			if (ChildrenMessages.CRY().isClusterMessage(request.getType()))
			{
				return ((ClusterTask)this.tasks.get(serverKey)).processRequest(request);
			}
			else
			{
				if (!this.isPrivate.get())
				{
					return ((ClusterTask)this.tasks.get(serverKey)).processRequest(request);
				}
				else
				{
					throw new NonPublicMachineException("The machine is non-public!");
				}
			}
		}
		else
		{
			if (!this.isPrivate.get())
			{
//				log.info("3) processClusterRootRequest() ...");
				PrimitiveMulticastRequest pmr = (PrimitiveMulticastRequest)request;
				if (pmr.getMultiAppID() == MulticastMessageType.CLUSTER_REQUEST)
				{
					/*
					 * The below code is designed for testing. 11/08/2022, Bing Li
					 */
					/*
					ClusterRequest cr = (ClusterRequest)pmr;
					if (cr.getClusterAppID() == CLERAppID.CHILD_ALL_ACCOUNTS_REQUEST)
					{
						log.info("CHILD_ALL_ACCOUNTS_REQUEST received!");
					}
					*/
					
					return ((ClusterTask)this.tasks.get(serverKey)).processRootRequest((ClusterRequest)pmr);
				}
				else if (pmr.getMultiAppID() == MulticastMessageType.CHILD_ROOT_REQUEST)
				{
					return ((ClusterTask)this.tasks.get(serverKey)).processRequest((ChildRootRequest)pmr);
				}
				else
				{
					return null;
				}
			}
			else
			{
				throw new NonPublicMachineException("The machine is non-public!");
			}
		}
	}
	
	private ServerMessage processPrivateRootRequest(String serverKey, ServerMessage request) throws NonPublicMachineException
	{
		return ((ClusterTask)this.tasks.get(serverKey)).processRootRequest((ClusterRequest)request);
	}
	
	public void processClusterChildNotification(String serverKey, ServerMessage notification) throws NonPublicMachineException
	{
//		log.info("1) ************ processClusterChildNotification() ...");
		/*
		 * A child can never be private. So the condition does not make sense. 05/18/2022, Bing Li
		 */
//		if (!this.isPrivate.get())
//		{
		if (notification.getType() != MulticastMessageType.MULTICAST_NOTIFICATION && notification.getType() != MulticastMessageType.MULTICAST_REQUEST)
		{
			((ClusterTask)this.tasks.get(serverKey)).processNotification(notification);
		}
		else if (notification.getType() == MulticastMessageType.MULTICAST_REQUEST)
		{
//			log.info("2) ************ processClusterChildNotification() ...");
			((ClusterTask)this.tasks.get(serverKey)).processChildRequest((ClusterRequest)notification);
		}
		else
		{
			PrimitiveMulticastNotification pmn = (PrimitiveMulticastNotification)notification;
			if (pmn.getMultiAppID() == MulticastMessageType.CLUSTER_NOTIFICATION)
			{
				((ClusterTask)this.tasks.get(serverKey)).processNotification((ClusterNotification)pmn);
			}
			else
			{
				((ClusterTask)this.tasks.get(serverKey)).processNotification(pmn);
			}
		}
//		}
//		else
//		{
//			throw new NonPublicMachineException("The machine is non-public!");
//		}
	}

	public ServerMessage processClusterChildRequest(String serverKey, ServerMessage request) throws NonPublicMachineException
	{
//		log.info("1) processClusterChildRequest() ...");
		/*
		 * A child can never be private. So the condition does not make sense. 05/18/2022, Bing Li
		 */
//		if (!this.isPrivate.get())
//		{
		if (request.getType() == MulticastMessageType.INTERCAST_REQUEST)
		{
//			log.info("2) processClusterChildRequest() ...");
			return ((ClusterTask)this.tasks.get(serverKey)).processRequest((IntercastRequest)request);
		}
//		log.info("3) processClusterChildRequest() ...");
		return null;
//		}
//		else
//		{
//			throw new NonPublicMachineException("The machine is non-public!");
//		}
	}
	
//	public void processMulticastResponse(String serverKey, MulticastResponse notification) throws NonPublicMachineException
	public void processMulticastResponse(String serverKey, PrimitiveMulticastResponse notification) throws NonPublicMachineException
	{
		if (!this.isPrivate.get())
		{
			((MulticastTask)this.tasks.get(serverKey)).processNotification(notification);
		}
		else
		{
			throw new NonPublicMachineException("The machine is non-public!");
		}
	}
	
	public ServerMessage processRequest(String serverKey, Request request) throws NonPublicMachineException
	{
		if (!this.isPrivate.get())
		{
			return this.tasks.get(serverKey).processRequest(request);
		}
		else
		{
			throw new NonPublicMachineException("The machine is non-public!");
		}
	}
	
	public void processSymmetricNotification(String serverKey, EncryptedNotification notification) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, ShortBufferException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException, NonPublicMachineException
	{
		if (!this.isPrivate.get())
		{
			SymmetricCrypto sc = this.symmetricCryptos.get(notification.getSessionKey());
			log.info("Received Encrypted Notification: " + Hex.toHexString(notification.getEncryptedData()));
			this.tasks.get(serverKey).processNotification(SymmetricCoder.decryptNotification(notification.getEncryptedData(), sc.getCipherKey(), sc.getIVKey(), sc.getCipherSpec()));
		}
		else
		{
			throw new NonPublicMachineException("The machine is non-public!");
		}
	}

	public void processSymmetricNotification(String serverKey, SymmetricPrimitiveNotification notification) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, ShortBufferException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException, NonPublicMachineException
	{
		if (!this.isPrivate.get())
		{
			SymmetricCrypto sc = this.symmetricCryptos.get(notification.getSessionKey());
			log.info("Received Encrypted Notification: " + Hex.toHexString(notification.getEncryptedData()));
			this.processPrimitive(serverKey, SymmetricCoder.decryptMessage(notification.getEncryptedData(), sc.getCipherKey(), sc.getIVKey(), sc.getCipherSpec()));
		}
		else
		{
			throw new NonPublicMachineException("The machine is non-public!");
		}
	}
	
	public void processClusterRootSymmetricNotification(String serverKey, SymmetricPrimitiveNotification notification) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, ShortBufferException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException, NonPublicMachineException
	{
		log.info("SymmetricPrimitiveNotification received ...");
		if (!this.isPrivate.get())
		{
			SymmetricCrypto sc = this.symmetricCryptos.get(notification.getSessionKey());
			log.info("Received Encrypted Notification: " + Hex.toHexString(notification.getEncryptedData()));
			this.processClusterRootNotification(serverKey, SymmetricCoder.decryptMessage(notification.getEncryptedData(), sc.getCipherKey(), sc.getIVKey(), sc.getCipherSpec()));
		}
		else
		{
			throw new NonPublicMachineException("The machine is non-public!");
		}
	}

	public void processClusterChildSymmetricNotification(String serverKey, SymmetricPrimitiveNotification notification) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, ShortBufferException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException, NonPublicMachineException
	{
		log.info("SymmetricPrimitiveNotification received ...");
		if (!this.isPrivate.get())
		{
			SymmetricCrypto sc = this.symmetricCryptos.get(notification.getSessionKey());
			log.info("Received Encrypted Notification: " + Hex.toHexString(notification.getEncryptedData()));
			this.processClusterChildNotification(serverKey, SymmetricCoder.decryptMessage(notification.getEncryptedData(), sc.getCipherKey(), sc.getIVKey(), sc.getCipherSpec()));
		}
		else
		{
			throw new NonPublicMachineException("The machine is non-public!");
		}
	}

	public EncryptedResponse processClusterRootSymmetricRequest(String serverKey, SymmetricPrimitiveRequest request) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, ShortBufferException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException, NonPublicMachineException
	{
		if (!this.isPrivate.get())
		{
			SymmetricCrypto c = this.symmetricCryptos.get(request.getSessionKey());
			log.info("Received Encrypted Request: " + Hex.toHexString(request.getEncryptedData()));
//			ServerMessage response = ((ClusterTask)this.tasks.get(serverKey)).processRequest(SymmetricCoder.decryptMessage(request.getEncryptedData(), c.getCipherKey(), c.getIVKey(), c.getCipherSpec()));
			ServerMessage response = this.processClusterRootRequest(serverKey, SymmetricCoder.decryptMessage(request.getEncryptedData(), c.getCipherKey(), c.getIVKey(), c.getCipherSpec()));
			byte[] resData = SymmetricCoder.encryptResponse(response, c.getCipherKey(), c.getIVKey(), c.getCipherSpec());
			log.info("Encrypted Response: " + Hex.toHexString(resData));
			return new EncryptedResponse(CryAppID.SYMMETRIC_ENCRYPTED_RESPONSE, resData);
		}
		else
		{
			throw new NonPublicMachineException("The machine is non-public!");
		}
	}

	public EncryptedResponse processClusterChildSymmetricRequest(String serverKey, SymmetricPrimitiveRequest request) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, ShortBufferException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException, NonPublicMachineException
	{
		if (!this.isPrivate.get())
		{
			SymmetricCrypto c = this.symmetricCryptos.get(request.getSessionKey());
			log.info("Received Encrypted Request: " + Hex.toHexString(request.getEncryptedData()));
			ServerMessage response = this.processClusterChildRequest(serverKey, SymmetricCoder.decryptMessage(request.getEncryptedData(), c.getCipherKey(), c.getIVKey(), c.getCipherSpec()));
			byte[] resData = SymmetricCoder.encryptResponse(response, c.getCipherKey(), c.getIVKey(), c.getCipherSpec());
			log.info("Encrypted Response: " + Hex.toHexString(resData));
			return new EncryptedResponse(CryAppID.SYMMETRIC_ENCRYPTED_RESPONSE, resData);
		}
		else
		{
			throw new NonPublicMachineException("The machine is non-public!");
		}
	}

	public EncryptedResponse processSymmetricRequest(String serverKey, EncryptedRequest request) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, ShortBufferException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException, NonPublicMachineException
	{
		if (!this.isPrivate.get())
		{
			SymmetricCrypto c = this.symmetricCryptos.get(request.getSessionKey());
			log.info("Received Encrypted Request: " + Hex.toHexString(request.getEncryptedData()));
//			return this.tasks.get(serverKey).processRequest(c, Coder.decryptRequest(request.getEncryptedData(), c.getCipherKey(), c.getIVKey(), c.getCipherApproach()));
			ServerMessage response = this.tasks.get(serverKey).processRequest(SymmetricCoder.decryptRequest(request.getEncryptedData(), c.getCipherKey(), c.getIVKey(), c.getCipherSpec()));
			byte[] resData = SymmetricCoder.encryptResponse(response, c.getCipherKey(), c.getIVKey(), c.getCipherSpec());
			log.info("Encrypted Response: " + Hex.toHexString(resData));
			return new EncryptedResponse(CryAppID.SYMMETRIC_ENCRYPTED_RESPONSE, resData);
		}
		else
		{
			throw new NonPublicMachineException("The machine is non-public!");
		}
	}
	
	public boolean isAsymPartnerInvited(String peerKey)
	{
		return this.peerSessionKeys.containsKey(peerKey);
	}

	public PublicCryptoSessionResponse retainPublicCrypto(PublicCryptoSessionRequest request) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException, IOException
	{
		log.info(request.getHostPeerName() + "'s public key received!");
//		log.info("peerKey = " + request.getPublicCrypto().getHostPeerKey());
//		this.asymCrypto.setPublicCrypto(request.getPublicCrypto());
		this.setPublicCrypto(request.getPublicCrypto());
		this.peerSessionKeys.put(request.getPublicCrypto().getHostPeerKey(), request.getPublicCrypto().getSessionKey());
		if (request.getPublicCrypto().getSignature() != null)
		{
			/*
			 * It is not reasonable to make a judgment here since the signed interactions are performed after the public cryptography information is exchanged. 02/18/2022, Bing Li
			 */
			if (this.isSignatureTrusted(request.getPublicCrypto().getSignature(), request.getEncryptedSignature(), true))
			{
				this.signaturePublicKeys.put(request.getPublicCrypto().getSignature(), request.getPublicCrypto());
				return new PublicCryptoSessionResponse(this.hostPeerName, new PublicCrypto(ServiceProvider.CRY().getAsymmetricSessionKey(), PeerContainer.getPeerKey(this.hostPeerName), ServiceProvider.CRY().getAsymmetricAlgorithm(), ServiceProvider.CRY().getPublicKey(), ServiceProvider.CRY().getSignatureAlgorithm(), ServiceProvider.CRY().getSignature()), true);
			}
			else
			{
				return new PublicCryptoSessionResponse(this.hostPeerName, new PublicCrypto(ServiceProvider.CRY().getAsymmetricSessionKey(), PeerContainer.getPeerKey(this.hostPeerName), ServiceProvider.CRY().getAsymmetricAlgorithm(), ServiceProvider.CRY().getPublicKey(), ServiceProvider.CRY().getSignatureAlgorithm(), ServiceProvider.CRY().getSignature()), false);
			}
		}
		else
		{
			return new PublicCryptoSessionResponse(this.hostPeerName, new PublicCrypto(ServiceProvider.CRY().getAsymmetricSessionKey(), PeerContainer.getPeerKey(this.hostPeerName), ServiceProvider.CRY().getAsymmetricAlgorithm(), ServiceProvider.CRY().getPublicKey()), true);
		}
	}
	
	/*
	 * The method is invoked only when the partner is trusted only such that it is not necessary to verify. 01/15/2022, Bing Li
	 */
	public void retainPublicCrypto(PublicCrypto pc)
	{
//		log.info("PC's hostPeerKey = " + pc.getHostPeerKey());
		this.setPublicCrypto(pc);
		this.peerSessionKeys.put(pc.getHostPeerKey(), pc.getSessionKey());
		// When signing is not needed, the signature is null. To avoid exceptions, the condition is required. 02/05/2023, Bing Li
		if (pc.getSignature() != null)
		{
			this.signaturePublicKeys.put(pc.getSignature(), pc);
		}
	}

//	public void processAsymmetricNotification(String serverKey, EncryptedNotification notification) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, ShortBufferException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException
	public void processAsymmetricNotification(String serverKey, AsymmetricEncryptedNotification notification) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, ShortBufferException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException, NonPublicMachineException, SessionMismatchedException
	{
		if (!this.isPrivate.get())
		{
			if (this.asymCrypto.getSessionKey().equals(notification.getSessionKey()))
			{
//				SymmetricCrypto scrypto = (SymmetricCrypto)AsymmetricCoder.decrypt(notification.getEncryptedSymCrypto(), this.asymCrypto.getAsymAlgorithm(), this.asymCrypto.getPrivateKey());
				AsymCompCrypto scrypto = (AsymCompCrypto)AsymmetricCoder.decrypt(notification.getEncryptedSymCrypto(), this.asymCrypto.getAsymAlgorithm(), this.asymCrypto.getPrivateKey());
				log.info("Received Encrypted Notification: " + Hex.toHexString(notification.getEncryptedData()));
//				this.tasks.get(serverKey).processNotification(AsymmetricCoder.decrypt(notification.getEncryptedData(), this.asymCrypto.getAsymAlgorithm(), this.asymCrypto.getPrivateKey()));
				this.tasks.get(serverKey).processNotification(SymmetricCoder.decryptNotification(notification.getEncryptedData(), scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec()));
			}
			else
			{
				log.info("Session mismatched!");
				throw new SessionMismatchedException("The session of asymmetric encrypted notification mismatched!");
			}
		}
		else
		{
			throw new NonPublicMachineException("The machine is non-public!");
		}
	}

	public void processAsymmetricNotification(String serverKey, AsymmetricPrimitiveNotification notification) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, ShortBufferException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException, NonPublicMachineException, SessionMismatchedException
	{
		if (!this.isPrivate.get())
		{
			if (this.asymCrypto.getSessionKey().equals(notification.getSessionKey()))
			{
				AsymCompCrypto scrypto = (AsymCompCrypto)AsymmetricCoder.decrypt(notification.getEncryptedSymCrypto(), this.asymCrypto.getAsymAlgorithm(), this.asymCrypto.getPrivateKey());
				log.info("Received Encrypted Notification: " + Hex.toHexString(notification.getEncryptedData()));
				this.processPrimitive(serverKey, SymmetricCoder.decryptMessage(notification.getEncryptedData(), scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec()));
			}
			else
			{
				log.info("Session mismatched!");
				throw new SessionMismatchedException("The session of asymmetric encrypted notification mismatched!");
			}
		}
		else
		{
			throw new NonPublicMachineException("The machine is non-public!");
		}
	}

	public void processClusterRootAsymmetricNotification(String serverKey, AsymmetricPrimitiveNotification notification) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, ShortBufferException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException, NonPublicMachineException, SessionMismatchedException
	{
		if (!this.isPrivate.get())
		{
			if (this.asymCrypto.getSessionKey().equals(notification.getSessionKey()))
			{
				AsymCompCrypto scrypto = (AsymCompCrypto)AsymmetricCoder.decrypt(notification.getEncryptedSymCrypto(), this.asymCrypto.getAsymAlgorithm(), this.asymCrypto.getPrivateKey());
				log.info("Received Encrypted Notification: " + Hex.toHexString(notification.getEncryptedData()));
				this.processClusterRootNotification(serverKey, SymmetricCoder.decryptMessage(notification.getEncryptedData(), scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec()));
			}
			else
			{
				log.info("Session mismatched!");
				throw new SessionMismatchedException("The session of asymmetric encrypted notification mismatched!");
			}
		}
		else
		{
			throw new NonPublicMachineException("The machine is non-public!");
		}
	}

	public void processClusterChildAsymmetricNotification(String serverKey, AsymmetricPrimitiveNotification notification) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, ShortBufferException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException, NonPublicMachineException, SessionMismatchedException
	{
		if (!this.isPrivate.get())
		{
			if (this.asymCrypto.getSessionKey().equals(notification.getSessionKey()))
			{
				AsymCompCrypto scrypto = (AsymCompCrypto)AsymmetricCoder.decrypt(notification.getEncryptedSymCrypto(), this.asymCrypto.getAsymAlgorithm(), this.asymCrypto.getPrivateKey());
				log.info("Received Encrypted Notification: " + Hex.toHexString(notification.getEncryptedData()));
				this.processClusterChildNotification(serverKey, SymmetricCoder.decryptMessage(notification.getEncryptedData(), scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec()));
			}
			else
			{
				log.info("Session mismatched!");
				throw new SessionMismatchedException("The session of asymmetric encrypted notification mismatched!");
			}
		}
		else
		{
			throw new NonPublicMachineException("The machine is non-public!");
		}
	}

	/*
	public boolean isOwner(String owner, Notification notification) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException, SignatureException
	{
		SignedAsymmetricEncryptedNotification signedNotification = (SignedAsymmetricEncryptedNotification)notification;
		if (this.asymCrypto.getSessionKey().equals(signedNotification.getSessionKey()))
		{
			SymmetricCrypto scrypto = (SymmetricCrypto)AsymmetricCoder.decrypt(signedNotification.getEncryptedSymCrypto(), this.asymCrypto.getAsymAlgorithm(), this.asymCrypto.getPrivateKey());
			PublicCrypto publicKey = this.getPublicCryptoByPeer(scrypto.getPeerKey());
			if (publicKey != null)
			{
				if (this.isSignatureTrusted(signedNotification.getSignature(), signedNotification.getEncryptedSignature(), false))
				{
					if (signedNotification.getSignature().equals(owner))
					{
						return true;
					}
				}
			}
		}
		return false;
	}
	*/

	public void processAsymmetricNotification(String serverKey, SignedAsymmetricEncryptedNotification notification) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, ShortBufferException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException, SignatureException, CheatingException, PublicKeyUnavailableException, NonPublicMachineException, SessionMismatchedException
	{
		if (!this.isPrivate.get())
		{
//			log.info("notification sessionKey = " + notification.getSessionKey());
			if (this.asymCrypto.getSessionKey().equals(notification.getSessionKey()))
			{
//				SymmetricCrypto scrypto = (SymmetricCrypto)AsymmetricCoder.decrypt(notification.getEncryptedSymCrypto(), this.asymCrypto.getAsymAlgorithm(), this.asymCrypto.getPrivateKey());
				AsymCompCrypto scrypto = (AsymCompCrypto)AsymmetricCoder.decrypt(notification.getEncryptedSymCrypto(), this.asymCrypto.getAsymAlgorithm(), this.asymCrypto.getPrivateKey());
				PublicCrypto publicKey = this.getPublicCryptoByPeer(scrypto.getPeerKey());
				if (publicKey != null)
				{
//					if (AsymmetricCoder.verify(publicKey.getSignatureAlgorithm(), publicKey.getPublicKey(), publicKey.getSignature(), notification.getEncryptedSignature()))
					if (this.isSignatureTrusted(notification.getSignature(), notification.getEncryptedSignature(), false))
					{
						/*
						if (notification.isOwnerRequired())
						{
							if (this.owners.containsKey(notification.getSessionKey()))
							{
								if (!notification.getCorrectOwnerName().equals(this.owners.get(notification.getSessionKey()).getOwnerName()) || !notification.getSignature().equals(this.owners.get(notification.getSessionKey()).getSignature()))
								{
									log.info("Someone is cheating as " + notification.getCorrectOwnerName());
									throw new OwnerCheatingException(notification.getCorrectOwnerName());
								}
							}
						}
						*/
						log.info(publicKey.getSignature() + " is notifying to you!");
						log.info("Received Encrypted Notification: " + Hex.toHexString(notification.getEncryptedData()));
						this.tasks.get(serverKey).processNotification(SymmetricCoder.decryptNotification(notification.getEncryptedData(), scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec()));
					}
					else
					{
						log.info("Someone is cheating as " + publicKey.getSignature());
						throw new CheatingException(publicKey.getSignature());
					}
				}
				else
				{
					/*
					if (!this.isBye.get())
					{
						log.info("Public key is not found!");
						throw new PublicKeyUnavailableException(scrypto.getPeerKey());
					}
					else
					{
						log.info("The node is shutdown! Hehe!");
					}
					*/
					log.info("Public key is not found!");
					throw new PublicKeyUnavailableException(scrypto.getPeerKey());
				}
			}
			else
			{
				log.info("Session mismatched!");
				throw new SessionMismatchedException("The session of signed asymmetric encrypted notification mismatched!");
			}
		}
		else
		{
			throw new NonPublicMachineException("The machine is non-public!");
		}
	}

	public void processAsymmetricNotification(String serverKey, SignedPrimitiveNotification notification) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, ShortBufferException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException, SignatureException, CheatingException, PublicKeyUnavailableException, NonPublicMachineException, SessionMismatchedException
	{
		if (!this.isPrivate.get())
		{
			if (this.asymCrypto.getSessionKey().equals(notification.getSessionKey()))
			{
				AsymCompCrypto scrypto = (AsymCompCrypto)AsymmetricCoder.decrypt(notification.getEncryptedSymCrypto(), this.asymCrypto.getAsymAlgorithm(), this.asymCrypto.getPrivateKey());
				PublicCrypto publicKey = this.getPublicCryptoByPeer(scrypto.getPeerKey());
				if (publicKey != null)
				{
					if (this.isSignatureTrusted(notification.getSignature(), notification.getEncryptedSignature(), false))
					{
						log.info(publicKey.getSignature() + " is notifying to you!");
						log.info("Received Encrypted Notification: " + Hex.toHexString(notification.getEncryptedData()));
						this.processPrimitive(serverKey, SymmetricCoder.decryptMessage(notification.getEncryptedData(), scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec()));
					}
					else
					{
						log.info("Someone is cheating as " + publicKey.getSignature());
						throw new CheatingException(publicKey.getSignature());
					}
				}
				else
				{
					/*
					if (!this.isBye.get())
					{
						log.info("Public key is not found!");
						throw new PublicKeyUnavailableException(scrypto.getPeerKey());
					}
					else
					{
						log.info("The node is shutdown! Hehe!");
					}
					*/
					log.info("Public key is not found!");
					throw new PublicKeyUnavailableException(scrypto.getPeerKey());
				}
			}
			else
			{
				log.info("Session mismatched!");
				throw new SessionMismatchedException("The session of signed asymmetric encrypted notification mismatched!");
			}
		}
		else
		{
			throw new NonPublicMachineException("The machine is non-public!");
		}
	}

	public void processClusterRootAsymmetricNotification(String serverKey, SignedPrimitiveNotification notification) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, ShortBufferException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException, SignatureException, CheatingException, PublicKeyUnavailableException, NonPublicMachineException, SessionMismatchedException
	{
		if (!this.isPrivate.get())
		{
			if (this.asymCrypto.getSessionKey().equals(notification.getSessionKey()))
			{
				AsymCompCrypto scrypto = (AsymCompCrypto)AsymmetricCoder.decrypt(notification.getEncryptedSymCrypto(), this.asymCrypto.getAsymAlgorithm(), this.asymCrypto.getPrivateKey());
				PublicCrypto publicKey = this.getPublicCryptoByPeer(scrypto.getPeerKey());
				if (publicKey != null)
				{
					if (this.isSignatureTrusted(notification.getSignature(), notification.getEncryptedSignature(), false))
					{
						log.info(publicKey.getSignature() + " is notifying to you!");
						log.info("Received Encrypted Notification: " + Hex.toHexString(notification.getEncryptedData()));
						this.processClusterRootNotification(serverKey, SymmetricCoder.decryptMessage(notification.getEncryptedData(), scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec()));
					}
					else
					{
						log.info("Someone is cheating as " + publicKey.getSignature());
						throw new CheatingException(publicKey.getSignature());
					}
				}
				else
				{
					/*
					if (!this.isBye.get())
					{
						log.info("Public key is not found!");
						throw new PublicKeyUnavailableException(scrypto.getPeerKey());
					}
					else
					{
						log.info("The node is shutdown! Hehe!");
					}
					*/
					log.info("Public key is not found!");
					throw new PublicKeyUnavailableException(scrypto.getPeerKey());
				}
			}
			else
			{
				log.info("Session mismatched!");
				throw new SessionMismatchedException("The session of signed asymmetric encrypted notification mismatched!");
			}
		}
		else
		{
			throw new NonPublicMachineException("The machine is non-public!");
		}
	}

	public void processClusterChildAsymmetricNotification(String serverKey, SignedPrimitiveNotification notification) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, ShortBufferException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException, SignatureException, CheatingException, PublicKeyUnavailableException, NonPublicMachineException, SessionMismatchedException
	{
		if (!this.isPrivate.get())
		{
			if (this.asymCrypto.getSessionKey().equals(notification.getSessionKey()))
			{
				AsymCompCrypto scrypto = (AsymCompCrypto)AsymmetricCoder.decrypt(notification.getEncryptedSymCrypto(), this.asymCrypto.getAsymAlgorithm(), this.asymCrypto.getPrivateKey());
				PublicCrypto publicKey = this.getPublicCryptoByPeer(scrypto.getPeerKey());
				if (publicKey != null)
				{
					if (this.isSignatureTrusted(notification.getSignature(), notification.getEncryptedSignature(), false))
					{
						log.info(publicKey.getSignature() + " is notifying to you!");
						log.info("Received Encrypted Notification: " + Hex.toHexString(notification.getEncryptedData()));
						this.processClusterChildNotification(serverKey, SymmetricCoder.decryptMessage(notification.getEncryptedData(), scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec()));
					}
					else
					{
						log.info("Someone is cheating as " + publicKey.getSignature());
						throw new CheatingException(publicKey.getSignature());
					}
				}
				else
				{
					/*
					if (!this.isBye.get())
					{
						log.info("Public key is not found!");
						throw new PublicKeyUnavailableException(scrypto.getPeerKey());
					}
					else
					{
						log.info("The node is shutdown! Hehe!");
					}
					*/
					log.info("Public key is not found!");
					throw new PublicKeyUnavailableException(scrypto.getPeerKey());
				}
			}
			else
			{
				log.info("Session mismatched!");
				throw new SessionMismatchedException("The session of signed asymmetric encrypted notification mismatched!");
			}
		}
		else
		{
			throw new NonPublicMachineException("The machine is non-public!");
		}
	}

	public void processAsymmetricNotification(String serverKey, PrivateNotification notification) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, ShortBufferException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException, SignatureException, CheatingException, PublicKeyUnavailableException, OwnerCheatingException, NonPrivateMachineException, SessionMismatchedException, MachineNotOwnedException
	{
		if (this.isPrivate.get())
		{
//			log.info("notification sessionKey = " + notification.getSessionKey());
			if (this.asymCrypto.getSessionKey().equals(notification.getSessionKey()))
			{
//				SymmetricCrypto scrypto = (SymmetricCrypto)AsymmetricCoder.decrypt(notification.getEncryptedSymCrypto(), this.asymCrypto.getAsymAlgorithm(), this.asymCrypto.getPrivateKey());
				AsymCompCrypto scrypto = (AsymCompCrypto)AsymmetricCoder.decrypt(notification.getEncryptedSymCrypto(), this.asymCrypto.getAsymAlgorithm(), this.asymCrypto.getPrivateKey());
				PublicCrypto publicKey = this.getPublicCryptoByPeer(scrypto.getPeerKey());
				if (publicKey != null)
				{
					if (this.isSignatureTrusted(notification.getSignature(), notification.getEncryptedSignature(), false))
					{
//						if (this.owners.containsKey(notification.getSessionKey()))
						if (this.owners.containsKey(notification.getSignature()))
						{
							log.info("owner = " + this.owners.get(notification.getSignature()));
							log.info("notification's ownerName = " + notification.getOwnerName());
							log.info("notification's signature = " + notification.getSignature());
//							if (!notification.getOwnerName().equals(this.owners.get(notification.getSessionKey()).getOwnerName()) || !notification.getSignature().equals(this.owners.get(notification.getSessionKey()).getSignature()))
							if (!notification.getOwnerName().equals(this.owners.get(notification.getSignature()).getOwnerName()) || !notification.getSignature().equals(this.owners.get(notification.getSignature()).getSignature()))
							{
								log.info("Someone is cheating as " + notification.getOwnerName());
								throw new OwnerCheatingException(notification.getOwnerName());
							}
							log.info(publicKey.getSignature() + " is notifying to you!");
							log.info("Received Encrypted Notification: " + Hex.toHexString(notification.getEncryptedData()));
							this.tasks.get(serverKey).processNotification(SymmetricCoder.decryptNotification(notification.getEncryptedData(), scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec()));
						}
						else
						{
							throw new MachineNotOwnedException(publicKey.getSignature());
						}
					}
					else
					{
						log.info("Someone is cheating as " + publicKey.getSignature());
						throw new CheatingException(publicKey.getSignature());
					}
				}
				else
				{
					/*
					if (!this.isBye.get())
					{
						log.info("Public key is not found!");
						throw new PublicKeyUnavailableException(scrypto.getPeerKey());
					}
					else
					{
						log.info("The node is shutdown! Hehe!");
					}
					*/
					log.info("Public key is not found!");
					throw new PublicKeyUnavailableException(scrypto.getPeerKey());
				}
			}
			else
			{
				log.info("Session mismatched!");
				throw new SessionMismatchedException("The session of private notification mismatched!");
			}
		}
		else
		{
			throw new NonPrivateMachineException("The machine is non-private!");
		}
	}

	public void processAsymmetricNotification(String serverKey, PrivatePrimitiveNotification notification) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, ShortBufferException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException, SignatureException, CheatingException, PublicKeyUnavailableException, OwnerCheatingException, NonPrivateMachineException, SessionMismatchedException, MachineNotOwnedException
	{
		if (this.isPrivate.get())
		{
			if (this.asymCrypto.getSessionKey().equals(notification.getSessionKey()))
			{
				AsymCompCrypto scrypto = (AsymCompCrypto)AsymmetricCoder.decrypt(notification.getEncryptedSymCrypto(), this.asymCrypto.getAsymAlgorithm(), this.asymCrypto.getPrivateKey());
				PublicCrypto publicKey = this.getPublicCryptoByPeer(scrypto.getPeerKey());
				if (publicKey != null)
				{
					if (this.isSignatureTrusted(notification.getSignature(), notification.getEncryptedSignature(), false))
					{
						if (this.owners.containsKey(notification.getSignature()))
						{
							log.info("owner = " + this.owners.get(notification.getSignature()));
							log.info("notification's ownerName = " + notification.getOwnerName());
							log.info("notification's signature = " + notification.getSignature());
							if (!notification.getOwnerName().equals(this.owners.get(notification.getSignature()).getOwnerName()) || !notification.getSignature().equals(this.owners.get(notification.getSignature()).getSignature()))
							{
								log.info("Someone is cheating as " + notification.getOwnerName());
								throw new OwnerCheatingException(notification.getOwnerName());
							}
							log.info(publicKey.getSignature() + " is notifying to you!");
							log.info("Received Encrypted Notification: " + Hex.toHexString(notification.getEncryptedData()));
							this.processPrimitive(serverKey, SymmetricCoder.decryptMessage(notification.getEncryptedData(), scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec()));
						}
						else
						{
							throw new MachineNotOwnedException(publicKey.getSignature());
						}
					}
					else
					{
						log.info("Someone is cheating as " + publicKey.getSignature());
						throw new CheatingException(publicKey.getSignature());
					}
				}
				else
				{
					/*
					if (!this.isBye.get())
					{
						log.info("Public key is not found!");
						throw new PublicKeyUnavailableException(scrypto.getPeerKey());
					}
					else
					{
						log.info("The node is shutdown! Hehe!");
					}
					*/
					log.info("Public key is not found!");
					throw new PublicKeyUnavailableException(scrypto.getPeerKey());
				}
			}
			else
			{
				log.info("Session mismatched!");
				throw new SessionMismatchedException("The session of private notification mismatched!");
			}
		}
		else
		{
			throw new NonPrivateMachineException("The machine is non-private!");
		}
	}

	public void processClusterRootAsymmetricNotification(String serverKey, PrivatePrimitiveNotification notification) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, ShortBufferException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException, SignatureException, CheatingException, PublicKeyUnavailableException, OwnerCheatingException, NonPrivateMachineException, SessionMismatchedException, MachineNotOwnedException, NonPublicMachineException
	{
		if (this.isPrivate.get())
		{
			if (this.asymCrypto.getSessionKey().equals(notification.getSessionKey()))
			{
				AsymCompCrypto scrypto = (AsymCompCrypto)AsymmetricCoder.decrypt(notification.getEncryptedSymCrypto(), this.asymCrypto.getAsymAlgorithm(), this.asymCrypto.getPrivateKey());
				PublicCrypto publicKey = this.getPublicCryptoByPeer(scrypto.getPeerKey());
				if (publicKey != null)
				{
					if (this.isSignatureTrusted(notification.getSignature(), notification.getEncryptedSignature(), false))
					{
						if (this.owners.containsKey(notification.getSignature()))
						{
							log.info("owner = " + this.owners.get(notification.getSignature()));
							log.info("notification's ownerName = " + notification.getOwnerName());
							log.info("notification's signature = " + notification.getSignature());
							if (!notification.getOwnerName().equals(this.owners.get(notification.getSignature()).getOwnerName()) || !notification.getSignature().equals(this.owners.get(notification.getSignature()).getSignature()))
							{
								log.info("Someone is cheating as " + notification.getOwnerName());
								throw new OwnerCheatingException(notification.getOwnerName());
							}
							log.info(publicKey.getSignature() + " is notifying to you!");
							log.info("Received Encrypted Notification: " + Hex.toHexString(notification.getEncryptedData()));
							
//							this.processClusterRootNotification(serverKey, SymmetricCoder.decryptMessage(notification.getEncryptedData(), scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec()));
							this.processPrivateRootNotification(serverKey, SymmetricCoder.decryptMessage(notification.getEncryptedData(), scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec()));
						}
						else
						{
							throw new MachineNotOwnedException(publicKey.getSignature());
						}
					}
					else
					{
						log.info("Someone is cheating as " + publicKey.getSignature());
						throw new CheatingException(publicKey.getSignature());
					}
				}
				else
				{
					/*
					if (!this.isBye.get())
					{
						log.info("Public key is not found!");
						throw new PublicKeyUnavailableException(scrypto.getPeerKey());
					}
					else
					{
						log.info("The node is shutdown! Hehe!");
					}
					*/
					log.info("Public key is not found!");
					throw new PublicKeyUnavailableException(scrypto.getPeerKey());
				}
			}
			else
			{
				log.info("Session mismatched!");
				throw new SessionMismatchedException("The session of private notification mismatched!");
			}
		}
		else
		{
			throw new NonPrivateMachineException("The machine is non-private!");
		}
	}

	public void processClusterChildAsymmetricNotification(String serverKey, PrivatePrimitiveNotification notification) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, ShortBufferException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException, SignatureException, CheatingException, PublicKeyUnavailableException, OwnerCheatingException, NonPrivateMachineException, SessionMismatchedException, MachineNotOwnedException, NonPublicMachineException
	{
		if (this.isPrivate.get())
		{
			if (this.asymCrypto.getSessionKey().equals(notification.getSessionKey()))
			{
				AsymCompCrypto scrypto = (AsymCompCrypto)AsymmetricCoder.decrypt(notification.getEncryptedSymCrypto(), this.asymCrypto.getAsymAlgorithm(), this.asymCrypto.getPrivateKey());
				PublicCrypto publicKey = this.getPublicCryptoByPeer(scrypto.getPeerKey());
				if (publicKey != null)
				{
					if (this.isSignatureTrusted(notification.getSignature(), notification.getEncryptedSignature(), false))
					{
						if (this.owners.containsKey(notification.getSignature()))
						{
							log.info("owner = " + this.owners.get(notification.getSignature()));
							log.info("notification's ownerName = " + notification.getOwnerName());
							log.info("notification's signature = " + notification.getSignature());
							if (!notification.getOwnerName().equals(this.owners.get(notification.getSignature()).getOwnerName()) || !notification.getSignature().equals(this.owners.get(notification.getSignature()).getSignature()))
							{
								log.info("Someone is cheating as " + notification.getOwnerName());
								throw new OwnerCheatingException(notification.getOwnerName());
							}
							log.info(publicKey.getSignature() + " is notifying to you!");
							log.info("Received Encrypted Notification: " + Hex.toHexString(notification.getEncryptedData()));
							this.processClusterChildNotification(serverKey, SymmetricCoder.decryptMessage(notification.getEncryptedData(), scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec()));
						}
						else
						{
							throw new MachineNotOwnedException(publicKey.getSignature());
						}
					}
					else
					{
						log.info("Someone is cheating as " + publicKey.getSignature());
						throw new CheatingException(publicKey.getSignature());
					}
				}
				else
				{
					/*
					if (!this.isBye.get())
					{
						log.info("Public key is not found!");
						throw new PublicKeyUnavailableException(scrypto.getPeerKey());
					}
					else
					{
						log.info("The node is shutdown! Hehe!");
					}
					*/
					log.info("Public key is not found!");
					throw new PublicKeyUnavailableException(scrypto.getPeerKey());
				}
			}
			else
			{
				log.info("Session mismatched!");
				throw new SessionMismatchedException("The session of private notification mismatched!");
			}
		}
		else
		{
			throw new NonPrivateMachineException("The machine is non-private!");
		}
	}

//	public EncryptedResponse processAsymmetricRequest(String serverKey, EncryptedRequest request) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, ShortBufferException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException
	public AsymmetricEncryptedResponse processAsymmetricRequest(String serverKey, AsymmetricEncryptedRequest request) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, ShortBufferException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException, PublicKeyUnavailableException, NonPublicMachineException, SessionMismatchedException
	{
		if (!this.isPrivate.get())
		{
			if (request.getSessionKey().equals(this.asymCrypto.getSessionKey()))
			{
//				SymmetricCrypto scrypto = (SymmetricCrypto)AsymmetricCoder.decrypt(request.getEncryptedSymCrypto(), this.asymCrypto.getAsymAlgorithm(), this.asymCrypto.getPrivateKey());
				AsymCompCrypto scrypto = (AsymCompCrypto)AsymmetricCoder.decrypt(request.getEncryptedSymCrypto(), this.asymCrypto.getAsymAlgorithm(), this.asymCrypto.getPrivateKey());
				log.info("Received Encrypted Request: " + Hex.toHexString(request.getEncryptedData()));
//				ServerMessage response = this.tasks.get(serverKey).processRequest(AsymmetricCoder.decryptRequest(request.getEncryptedData(), this.asymCrypto.getAsymAlgorithm(), this.asymCrypto.getPrivateKey()));
				ServerMessage response = this.tasks.get(serverKey).processRequest(SymmetricCoder.decryptRequest(request.getEncryptedData(), scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec()));
//				PublicCrypto pc = this.asymCrypto.getPublicCrypto(request.getSessionKey());
//				PublicCrypto pc = this.getPublicCryptoBySession(request.getSessionKey());
				PublicCrypto pc = this.getPublicCryptoByPeer(scrypto.getPeerKey());
				if (pc != null)
				{
//					SymmetricCrypto nscrypto = SymmetricCoder.generateCrypto(this.asymCrypto.getSymCipherAlgorithm(), this.asymCrypto.getSymCipherSpec(), this.asymCrypto.getCipherKeyLength(), this.asymCrypto.getIVKeyLength());
					SymmetricCrypto nscrypto = SymmetricCoder.generateCrypto(this.asymCrypto.getSymCipherAlgorithm(), this.asymCrypto.getSymCipherSpec(), this.asymCrypto.getCipherKeyLength(), this.asymCrypto.getIVKeyLength());
//					byte[] resData = AsymmetricCoder.encryptResponse(response, pc.getAlgorithm(), pc.getPublicKey());
					byte[] resData = SymmetricCoder.encryptResponse(response, nscrypto.getCipherKey(), nscrypto.getIVKey(), nscrypto.getCipherSpec());
					byte[] enNScryptoData = AsymmetricCoder.encrypt(nscrypto, pc.getAsymAlgorithm(), pc.getPublicKey());
					log.info("Encrypted Response: " + Hex.toHexString(resData));
					return new AsymmetricEncryptedResponse(resData, enNScryptoData);
				}
				else
				{
					/*
					if (!this.isBye.get())
					{
						log.info("Public key is not found!");
						throw new PublicKeyUnavailableException(scrypto.getPeerKey());
					}
					else
					{
						log.info("The node is shutdown! Hehe!");
						return null;
					}
					*/
					log.info("Public key is not found!");
					throw new PublicKeyUnavailableException(scrypto.getPeerKey());
				}
			}
			else
			{
				log.info("Session mismatched!!");
				throw new SessionMismatchedException("The session of asymmetric encrypted notification mismatched!");
			}
		}
		else
		{
			throw new NonPublicMachineException("The machine is non-public!");
		}
	}

	public AsymmetricEncryptedResponse processClusterRootAsymmetricRequest(String serverKey, AsymmetricPrimitiveRequest request) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, ShortBufferException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException, PublicKeyUnavailableException, NonPublicMachineException, SessionMismatchedException
	{
		if (!this.isPrivate.get())
		{
			if (request.getSessionKey().equals(this.asymCrypto.getSessionKey()))
			{
				AsymCompCrypto scrypto = (AsymCompCrypto)AsymmetricCoder.decrypt(request.getEncryptedSymCrypto(), this.asymCrypto.getAsymAlgorithm(), this.asymCrypto.getPrivateKey());
				log.info("Received Encrypted Request: " + Hex.toHexString(request.getEncryptedData()));
//				ServerMessage response = ((ClusterTask)this.tasks.get(serverKey)).processRequest(SymmetricCoder.decryptMessage(request.getEncryptedData(), scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec()));
				ServerMessage response = this.processClusterRootRequest(serverKey, SymmetricCoder.decryptMessage(request.getEncryptedData(), scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec()));
				PublicCrypto pc = this.getPublicCryptoByPeer(scrypto.getPeerKey());
				if (pc != null)
				{
					SymmetricCrypto nscrypto = SymmetricCoder.generateCrypto(this.asymCrypto.getSymCipherAlgorithm(), this.asymCrypto.getSymCipherSpec(), this.asymCrypto.getCipherKeyLength(), this.asymCrypto.getIVKeyLength());
					byte[] resData = SymmetricCoder.encryptResponse(response, nscrypto.getCipherKey(), nscrypto.getIVKey(), nscrypto.getCipherSpec());
					byte[] enNScryptoData = AsymmetricCoder.encrypt(nscrypto, pc.getAsymAlgorithm(), pc.getPublicKey());
					log.info("Encrypted Response: " + Hex.toHexString(resData));
					return new AsymmetricEncryptedResponse(resData, enNScryptoData);
				}
				else
				{
					/*
					if (!this.isBye.get())
					{
						log.info("Public key is not found!");
						throw new PublicKeyUnavailableException(scrypto.getPeerKey());
					}
					else
					{
						log.info("The node is shutdown! Hehe!");
						return null;
					}
					*/
					log.info("Public key is not found!");
					throw new PublicKeyUnavailableException(scrypto.getPeerKey());
				}
			}
			else
			{
				log.info("Session mismatched!!");
				throw new SessionMismatchedException("The session of asymmetric encrypted notification mismatched!");
			}
		}
		else
		{
			throw new NonPublicMachineException("The machine is non-public!");
		}
	}

	public AsymmetricEncryptedResponse processClusterChildAsymmetricRequest(String serverKey, AsymmetricPrimitiveRequest request) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, ShortBufferException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException, PublicKeyUnavailableException, NonPublicMachineException, SessionMismatchedException
	{
		if (!this.isPrivate.get())
		{
			if (request.getSessionKey().equals(this.asymCrypto.getSessionKey()))
			{
				AsymCompCrypto scrypto = (AsymCompCrypto)AsymmetricCoder.decrypt(request.getEncryptedSymCrypto(), this.asymCrypto.getAsymAlgorithm(), this.asymCrypto.getPrivateKey());
				log.info("Received Encrypted Request: " + Hex.toHexString(request.getEncryptedData()));
				ServerMessage response = this.processClusterChildRequest(serverKey, SymmetricCoder.decryptMessage(request.getEncryptedData(), scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec()));
				PublicCrypto pc = this.getPublicCryptoByPeer(scrypto.getPeerKey());
				if (pc != null)
				{
					SymmetricCrypto nscrypto = SymmetricCoder.generateCrypto(this.asymCrypto.getSymCipherAlgorithm(), this.asymCrypto.getSymCipherSpec(), this.asymCrypto.getCipherKeyLength(), this.asymCrypto.getIVKeyLength());
					byte[] resData = SymmetricCoder.encryptResponse(response, nscrypto.getCipherKey(), nscrypto.getIVKey(), nscrypto.getCipherSpec());
					byte[] enNScryptoData = AsymmetricCoder.encrypt(nscrypto, pc.getAsymAlgorithm(), pc.getPublicKey());
					log.info("Encrypted Response: " + Hex.toHexString(resData));
					return new AsymmetricEncryptedResponse(resData, enNScryptoData);
				}
				else
				{
					/*
					if (!this.isBye.get())
					{
						log.info("Public key is not found!");
						throw new PublicKeyUnavailableException(scrypto.getPeerKey());
					}
					else
					{
						log.info("The node is shutdown! Hehe!");
						return null;
					}
					*/
					log.info("Public key is not found!");
					throw new PublicKeyUnavailableException(scrypto.getPeerKey());
				}
			}
			else
			{
				log.info("Session mismatched!!");
				throw new SessionMismatchedException("The session of asymmetric encrypted notification mismatched!");
			}
		}
		else
		{
			throw new NonPublicMachineException("The machine is non-public!");
		}
	}

	/*
	public boolean isOwner(String owner, SignedAsymmetricEncryptedRequest request) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException, SignatureException
	{
//		SignedAsymmetricEncryptedRequest signedRequest = (SignedAsymmetricEncryptedRequest)request;
		if (request.getSessionKey().equals(this.asymCrypto.getSessionKey()))
		{
			SymmetricCrypto scrypto = (SymmetricCrypto)AsymmetricCoder.decrypt(request.getEncryptedSymCrypto(), this.asymCrypto.getAsymAlgorithm(), this.asymCrypto.getPrivateKey());
			PublicCrypto publicKey = this.getPublicCryptoByPeer(scrypto.getPeerKey());
			if (publicKey != null)
			{
				if (this.isSignatureTrusted(request.getSignature(), request.getEncryptedSignature(), false))
				{
					if (request.getSignature().equals(owner))
					{
						return true;
					}
				}
			}
		}
		return false;
	}
	*/

//	public OwnershipResponse setOwner(OwnershipRequest request) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, ShortBufferException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException, SignatureException, CheatingException, PublicKeyUnavailableException, NonPrivateMachineException, SessionMismatchedException
	public OwnershipResponse setOwner(String serverKey, OwnershipRequest request) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, ShortBufferException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException, SignatureException, CheatingException, PublicKeyUnavailableException, NonPrivateMachineException, SessionMismatchedException
	{
		if (this.isPrivate.get())
		{
			if (request.getSessionKey().equals(this.asymCrypto.getSessionKey()))
			{
//				SymmetricCrypto scrypto = (SymmetricCrypto)AsymmetricCoder.decrypt(request.getEncryptedSymCrypto(), this.asymCrypto.getAsymAlgorithm(), this.asymCrypto.getPrivateKey());
				AsymCompCrypto scrypto = (AsymCompCrypto)AsymmetricCoder.decrypt(request.getEncryptedSymCrypto(), this.asymCrypto.getAsymAlgorithm(), this.asymCrypto.getPrivateKey());
				PublicCrypto publicKey = this.getPublicCryptoByPeer(scrypto.getPeerKey());
				if (publicKey != null)
				{
					if (this.isSignatureTrusted(request.getSignature(), request.getEncryptedSignature(), false))
					{
						log.info(publicKey.getSignature() + " is requesting to you!");
						log.info("Received Encrypted Request: " + Hex.toHexString(request.getEncryptedData()));
						OwnerInfo oi = (OwnerInfo)SymmetricCoder.decryptObject(request.getEncryptedData(), scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec());
//						boolean isSucceeded;
						/*
						if (!this.asymCrypto.isOwnerSet())
						{
							this.asymCrypto.setOwner(oi);
							isSucceeded = true;
						}
						else
						{
							isSucceeded = false;
						}
						*/
						AllOwners aos;
						if (this.owners.size() < this.ownerSize.get())
						{
							aos = new AllOwners(this.owners.values(), true);
//							this.owners.put(oi.getSessionKey(), oi);
							this.owners.put(oi.getSignature(), oi);
//							isSucceeded = true;
							
							/*
							 * This is the first time to raise an event to notify the upper level about the lower level updates. This is a good design. 03/24/2022, Bing Li
							 */
							log.info("OwnerJoinNotification is notified ...");
							this.tasks.get(serverKey).processNotification(new OwnerJoinNotification(oi.getOwnerName()));
						}
						else
						{
							aos = new AllOwners(false);
//							isSucceeded = false;
							log.info("Owners exceed the upper limit!");
						}

						SymmetricCrypto nscrypto = SymmetricCoder.generateCrypto(this.asymCrypto.getSymCipherAlgorithm(), this.asymCrypto.getSymCipherSpec(), this.asymCrypto.getCipherKeyLength(), this.asymCrypto.getIVKeyLength());
//						AllOwners aos = new AllOwners(this.owners.keySet(), isSucceeded);
//						byte[] resData = SymmetricCoder.encryptObject(isSucceeded, nscrypto.getCipherKey(), nscrypto.getIVKey(), nscrypto.getCipherSpec());
						byte[] resData = SymmetricCoder.encryptObject(aos, nscrypto.getCipherKey(), nscrypto.getIVKey(), nscrypto.getCipherSpec());
						byte[] enNScryptoData = AsymmetricCoder.encrypt(nscrypto, publicKey.getAsymAlgorithm(), publicKey.getPublicKey());
						byte[] signedInfo = AsymmetricCoder.sign(ServiceProvider.CRY().getSignatureAlgorithm(), ServiceProvider.CRY().getPrivateKey(), ServiceProvider.CRY().getSignature());
						log.info("Encrypted Response: " + Hex.toHexString(resData));
						return new OwnershipResponse(resData, enNScryptoData, ServiceProvider.CRY().getSignature(), signedInfo);
					}
					else
					{
						log.info("Someone is cheating as " + publicKey.getSignature());
						throw new CheatingException(publicKey.getSignature());
					}
				}
				else
				{
					/*
					if (!this.isBye.get())
					{
						log.info("Public key is not found!");
						throw new PublicKeyUnavailableException(scrypto.getPeerKey());
					}
					else
					{
						log.info("The node is shutdown! Hehe!");
						return null;
					}
					*/
					log.info("Public key is not found!");
					throw new PublicKeyUnavailableException(scrypto.getPeerKey());
				}
			}
			else
			{
				log.info("Session mismatched!!");
				throw new SessionMismatchedException("The session of ownership request mismatched!");
			}
		}
		else
		{
			throw new NonPrivateMachineException("The machine is non-private!");
		}
	}
	
	public AbandonOwnershipResponse abandonOwner(String serverKey, AbandonOwnershipRequest request) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException, SignatureException, InvalidAlgorithmParameterException, ShortBufferException, CheatingException, PublicKeyUnavailableException, SessionMismatchedException, NonPrivateMachineException
	{
		if (this.isPrivate.get())
		{
			if (request.getSessionKey().equals(this.asymCrypto.getSessionKey()))
			{
				AsymCompCrypto scrypto = (AsymCompCrypto)AsymmetricCoder.decrypt(request.getEncryptedSymCrypto(), this.asymCrypto.getAsymAlgorithm(), this.asymCrypto.getPrivateKey());
				PublicCrypto publicKey = this.getPublicCryptoByPeer(scrypto.getPeerKey());
				if (publicKey != null)
				{
					if (this.isSignatureTrusted(request.getSignature(), request.getEncryptedSignature(), false))
					{
						log.info(publicKey.getSignature() + " is requesting to you!");
						log.info("Received Encrypted Request: " + Hex.toHexString(request.getEncryptedData()));
						OwnerInfo oi = (OwnerInfo)SymmetricCoder.decryptObject(request.getEncryptedData(), scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec());
						this.owners.remove(oi.getSignature());
						AllOwners aos = new AllOwners(this.owners.values(), true);
						log.info("OwnerLeaveNotification is notified ...");
						this.tasks.get(serverKey).processNotification(new OwnerLeaveNotification(oi.getOwnerName()));
						
						SymmetricCrypto nscrypto = SymmetricCoder.generateCrypto(this.asymCrypto.getSymCipherAlgorithm(), this.asymCrypto.getSymCipherSpec(), this.asymCrypto.getCipherKeyLength(), this.asymCrypto.getIVKeyLength());
						byte[] resData = SymmetricCoder.encryptObject(aos, nscrypto.getCipherKey(), nscrypto.getIVKey(), nscrypto.getCipherSpec());
						byte[] enNScryptoData = AsymmetricCoder.encrypt(nscrypto, publicKey.getAsymAlgorithm(), publicKey.getPublicKey());
						byte[] signedInfo = AsymmetricCoder.sign(ServiceProvider.CRY().getSignatureAlgorithm(), ServiceProvider.CRY().getPrivateKey(), ServiceProvider.CRY().getSignature());
						log.info("Encrypted Response: " + Hex.toHexString(resData));
						return new AbandonOwnershipResponse(resData, enNScryptoData, ServiceProvider.CRY().getSignature(), signedInfo);
					}
					else
					{
						log.info("Someone is cheating as " + publicKey.getSignature());
						throw new CheatingException(publicKey.getSignature());
					}
				}
				else
				{
					/*
					if (!this.isBye.get())
					{
						log.info("Public key is not found!");
						throw new PublicKeyUnavailableException(scrypto.getPeerKey());
					}
					else
					{
						log.info("The node is shutdown! Hehe!");
						return null;
					}
					*/
					log.info("Public key is not found!");
					throw new PublicKeyUnavailableException(scrypto.getPeerKey());
				}
			}
			else
			{
				log.info("Session mismatched!!");
				throw new SessionMismatchedException("The session of ownership request mismatched!");
			}
		}
		else
		{
			throw new NonPrivateMachineException("The machine is non-private!");
		}
	}

	public SignedAsymmetricEncryptedResponse processAsymmetricRequest(String serverKey, SignedAsymmetricEncryptedRequest request) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, ShortBufferException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException, SignatureException, CheatingException, PublicKeyUnavailableException, NonPublicMachineException, SessionMismatchedException
	{
		if (!this.isPrivate.get())
		{
			if (request.getSessionKey().equals(this.asymCrypto.getSessionKey()))
			{
//				SymmetricCrypto scrypto = (SymmetricCrypto)AsymmetricCoder.decrypt(request.getEncryptedSymCrypto(), this.asymCrypto.getAsymAlgorithm(), this.asymCrypto.getPrivateKey());
				AsymCompCrypto scrypto = (AsymCompCrypto)AsymmetricCoder.decrypt(request.getEncryptedSymCrypto(), this.asymCrypto.getAsymAlgorithm(), this.asymCrypto.getPrivateKey());
				PublicCrypto publicKey = this.getPublicCryptoByPeer(scrypto.getPeerKey());
				if (publicKey != null)
				{
//					if (AsymmetricCoder.verify(publicKey.getSignatureAlgorithm(), publicKey.getPublicKey(), publicKey.getSignature(), request.getEncryptedSignature()))
					if (this.isSignatureTrusted(request.getSignature(), request.getEncryptedSignature(), false))
					{
						/*
						if (request.isOwnerRequired())
						{
							if (this.owners.containsKey(request.getSessionKey()))
							{
								log.info("Owner = " + this.owners.get(request.getSessionKey()).getOwnerName() + ", Owner@request = " + request.getOwnerName());
								if (!request.getOwnerName().equals(this.owners.get(request.getSessionKey()).getOwnerName()) || !request.getSignature().equals(this.owners.get(request.getSessionKey()).getSignature()))
								{
									return new SignedAsymmetricEncryptedResponse(request.getOwnerName());
								}
							}
						}
						*/
						log.info(publicKey.getSignature() + " is requesting to you!");
						log.info("Received Encrypted Request: " + Hex.toHexString(request.getEncryptedData()));
						ServerMessage response = this.tasks.get(serverKey).processRequest(SymmetricCoder.decryptRequest(request.getEncryptedData(), scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec()));
						SymmetricCrypto nscrypto = SymmetricCoder.generateCrypto(this.asymCrypto.getSymCipherAlgorithm(), this.asymCrypto.getSymCipherSpec(), this.asymCrypto.getCipherKeyLength(), this.asymCrypto.getIVKeyLength());
						byte[] resData = SymmetricCoder.encryptResponse(response, nscrypto.getCipherKey(), nscrypto.getIVKey(), nscrypto.getCipherSpec());
						byte[] enNScryptoData = AsymmetricCoder.encrypt(nscrypto, publicKey.getAsymAlgorithm(), publicKey.getPublicKey());
						byte[] signedInfo = AsymmetricCoder.sign(ServiceProvider.CRY().getSignatureAlgorithm(), ServiceProvider.CRY().getPrivateKey(), ServiceProvider.CRY().getSignature());
						log.info("Encrypted Response: " + Hex.toHexString(resData));
						return new SignedAsymmetricEncryptedResponse(resData, enNScryptoData, ServiceProvider.CRY().getSignature(), signedInfo);
					}
					else
					{
						log.info("Someone is cheating as " + publicKey.getSignature());
						throw new CheatingException(publicKey.getSignature());
					}
				}
				else
				{
					/*
					if (!this.isBye.get())
					{
						log.info("Public key is not found!");
						throw new PublicKeyUnavailableException(scrypto.getPeerKey());
					}
					else
					{
						log.info("The node is shutdown! Hehe!");
						return null;
					}
					*/
					log.info("Public key is not found!");
					throw new PublicKeyUnavailableException(scrypto.getPeerKey());
				}
			}
			else
			{
				log.info("Session mismatched!");
				throw new SessionMismatchedException("The session of signed asymmetric encrypted request mismatched!");
			}
		}
		else
		{
			throw new NonPublicMachineException("The machine is a non-public!");
		}
	}

	public SignedAsymmetricEncryptedResponse processClusterRootAsymmetricRequest(String serverKey, SignedPrimitiveRequest request) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, ShortBufferException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException, SignatureException, CheatingException, PublicKeyUnavailableException, NonPublicMachineException, SessionMismatchedException
	{
		if (!this.isPrivate.get())
		{
			if (request.getSessionKey().equals(this.asymCrypto.getSessionKey()))
			{
				AsymCompCrypto scrypto = (AsymCompCrypto)AsymmetricCoder.decrypt(request.getEncryptedSymCrypto(), this.asymCrypto.getAsymAlgorithm(), this.asymCrypto.getPrivateKey());
				PublicCrypto publicKey = this.getPublicCryptoByPeer(scrypto.getPeerKey());
				if (publicKey != null)
				{
					if (this.isSignatureTrusted(request.getSignature(), request.getEncryptedSignature(), false))
					{
						log.info(publicKey.getSignature() + " is requesting to you!");
						log.info("Received Encrypted Request: " + Hex.toHexString(request.getEncryptedData()));
//						ServerMessage response = ((ClusterTask)this.tasks.get(serverKey)).processRequest(SymmetricCoder.decryptMessage(request.getEncryptedData(), scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec()));
						ServerMessage response = this.processClusterRootRequest(serverKey, SymmetricCoder.decryptMessage(request.getEncryptedData(), scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec()));
						SymmetricCrypto nscrypto = SymmetricCoder.generateCrypto(this.asymCrypto.getSymCipherAlgorithm(), this.asymCrypto.getSymCipherSpec(), this.asymCrypto.getCipherKeyLength(), this.asymCrypto.getIVKeyLength());
						byte[] resData = SymmetricCoder.encryptResponse(response, nscrypto.getCipherKey(), nscrypto.getIVKey(), nscrypto.getCipherSpec());
						byte[] enNScryptoData = AsymmetricCoder.encrypt(nscrypto, publicKey.getAsymAlgorithm(), publicKey.getPublicKey());
						byte[] signedInfo = AsymmetricCoder.sign(ServiceProvider.CRY().getSignatureAlgorithm(), ServiceProvider.CRY().getPrivateKey(), ServiceProvider.CRY().getSignature());
						log.info("Encrypted Response: " + Hex.toHexString(resData));
						return new SignedAsymmetricEncryptedResponse(resData, enNScryptoData, ServiceProvider.CRY().getSignature(), signedInfo);
					}
					else
					{
						log.info("Someone is cheating as " + publicKey.getSignature());
						throw new CheatingException(publicKey.getSignature());
					}
				}
				else
				{
					/*
					if (!this.isBye.get())
					{
						log.info("Public key is not found!");
						throw new PublicKeyUnavailableException(scrypto.getPeerKey());
					}
					else
					{
						log.info("The node is shutdown! Hehe!");
						return null;
					}
					*/
					log.info("Public key is not found!");
					throw new PublicKeyUnavailableException(scrypto.getPeerKey());
				}
			}
			else
			{
				log.info("Session mismatched!");
				throw new SessionMismatchedException("The session of signed asymmetric encrypted request mismatched!");
			}
		}
		else
		{
			throw new NonPublicMachineException("The machine is a non-public!");
		}
	}
	

	public SignedAsymmetricEncryptedResponse processClusterChildAsymmetricRequest(String serverKey, SignedPrimitiveRequest request) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, ShortBufferException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException, SignatureException, CheatingException, PublicKeyUnavailableException, NonPublicMachineException, SessionMismatchedException
	{
		if (!this.isPrivate.get())
		{
			if (request.getSessionKey().equals(this.asymCrypto.getSessionKey()))
			{
				AsymCompCrypto scrypto = (AsymCompCrypto)AsymmetricCoder.decrypt(request.getEncryptedSymCrypto(), this.asymCrypto.getAsymAlgorithm(), this.asymCrypto.getPrivateKey());
				PublicCrypto publicKey = this.getPublicCryptoByPeer(scrypto.getPeerKey());
				if (publicKey != null)
				{
					if (this.isSignatureTrusted(request.getSignature(), request.getEncryptedSignature(), false))
					{
						log.info(publicKey.getSignature() + " is requesting to you!");
						log.info("Received Encrypted Request: " + Hex.toHexString(request.getEncryptedData()));
						ServerMessage response = this.processClusterChildRequest(serverKey, SymmetricCoder.decryptMessage(request.getEncryptedData(), scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec()));
						SymmetricCrypto nscrypto = SymmetricCoder.generateCrypto(this.asymCrypto.getSymCipherAlgorithm(), this.asymCrypto.getSymCipherSpec(), this.asymCrypto.getCipherKeyLength(), this.asymCrypto.getIVKeyLength());
						byte[] resData = SymmetricCoder.encryptResponse(response, nscrypto.getCipherKey(), nscrypto.getIVKey(), nscrypto.getCipherSpec());
						byte[] enNScryptoData = AsymmetricCoder.encrypt(nscrypto, publicKey.getAsymAlgorithm(), publicKey.getPublicKey());
						byte[] signedInfo = AsymmetricCoder.sign(ServiceProvider.CRY().getSignatureAlgorithm(), ServiceProvider.CRY().getPrivateKey(), ServiceProvider.CRY().getSignature());
						log.info("Encrypted Response: " + Hex.toHexString(resData));
						return new SignedAsymmetricEncryptedResponse(resData, enNScryptoData, ServiceProvider.CRY().getSignature(), signedInfo);
					}
					else
					{
						log.info("Someone is cheating as " + publicKey.getSignature());
						throw new CheatingException(publicKey.getSignature());
					}
				}
				else
				{
					/*
					if (!this.isBye.get())
					{
						log.info("Public key is not found!");
						throw new PublicKeyUnavailableException(scrypto.getPeerKey());
					}
					else
					{
						log.info("The node is shutdown! Hehe!");
						return null;
					}
					*/
					log.info("Public key is not found!");
					throw new PublicKeyUnavailableException(scrypto.getPeerKey());
				}
			}
			else
			{
				log.info("Session mismatched!");
				throw new SessionMismatchedException("The session of signed asymmetric encrypted request mismatched!");
			}
		}
		else
		{
			throw new NonPublicMachineException("The machine is a non-public!");
		}
	}

	public PrivateResponse processAsymmetricRequest(String serverKey, PrivateRequest request) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, ShortBufferException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException, SignatureException, CheatingException, PublicKeyUnavailableException, NonPrivateMachineException, SessionMismatchedException, MachineNotOwnedException, OwnerCheatingException
	{
		if (this.isPrivate.get())
		{
			if (request.getSessionKey().equals(this.asymCrypto.getSessionKey()))
			{
//				SymmetricCrypto scrypto = (SymmetricCrypto)AsymmetricCoder.decrypt(request.getEncryptedSymCrypto(), this.asymCrypto.getAsymAlgorithm(), this.asymCrypto.getPrivateKey());
				AsymCompCrypto scrypto = (AsymCompCrypto)AsymmetricCoder.decrypt(request.getEncryptedSymCrypto(), this.asymCrypto.getAsymAlgorithm(), this.asymCrypto.getPrivateKey());
				PublicCrypto publicKey = this.getPublicCryptoByPeer(scrypto.getPeerKey());
				if (publicKey != null)
				{
//					if (AsymmetricCoder.verify(publicKey.getSignatureAlgorithm(), publicKey.getPublicKey(), publicKey.getSignature(), request.getEncryptedSignature()))
					if (this.isSignatureTrusted(request.getSignature(), request.getEncryptedSignature(), false))
					{
//						if (this.owners.containsKey(request.getSessionKey()))
						if (this.owners.containsKey(request.getSignature()))
						{
//							log.info("Owner = " + this.owners.get(request.getSessionKey()).getOwnerName() + ", Owner@request = " + request.getOwnerName());
							log.info("owner = " + this.owners.get(request.getSignature()));
							log.info("request ownerName = " + request.getOwnerName());
							log.info("request signature = " + request.getSignature());
//							if (!request.getOwnerName().equals(this.owners.get(request.getSessionKey()).getOwnerName()) || !request.getSignature().equals(this.owners.get(request.getSessionKey()).getSignature()))
							if (!request.getOwnerName().equals(this.owners.get(request.getSignature()).getOwnerName()) || !request.getSignature().equals(this.owners.get(request.getSignature()).getSignature()))
							{
//								return new PrivateResponse(request.getOwnerName());
								throw new OwnerCheatingException(request.getOwnerName());
							}
							log.info(publicKey.getSignature() + " is requesting to you!");
							log.info("Received Encrypted Request: " + Hex.toHexString(request.getEncryptedData()));
							ServerMessage response = this.tasks.get(serverKey).processRequest(SymmetricCoder.decryptRequest(request.getEncryptedData(), scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec()));
							SymmetricCrypto nscrypto = SymmetricCoder.generateCrypto(this.asymCrypto.getSymCipherAlgorithm(), this.asymCrypto.getSymCipherSpec(), this.asymCrypto.getCipherKeyLength(), this.asymCrypto.getIVKeyLength());
							byte[] resData = SymmetricCoder.encryptResponse(response, nscrypto.getCipherKey(), nscrypto.getIVKey(), nscrypto.getCipherSpec());
							byte[] enNScryptoData = AsymmetricCoder.encrypt(nscrypto, publicKey.getAsymAlgorithm(), publicKey.getPublicKey());
							byte[] signedInfo = AsymmetricCoder.sign(ServiceProvider.CRY().getSignatureAlgorithm(), ServiceProvider.CRY().getPrivateKey(), ServiceProvider.CRY().getSignature());
							log.info("Encrypted Response: " + Hex.toHexString(resData));
							return new PrivateResponse(resData, enNScryptoData, ServiceProvider.CRY().getSignature(), signedInfo);
						}
						else
						{
							throw new MachineNotOwnedException(publicKey.getSignature());
						}
					}
					else
					{
						log.info("Someone is cheating as " + publicKey.getSignature());
						throw new CheatingException(publicKey.getSignature());
					}
				}
				else
				{
					/*
					if (!this.isBye.get())
					{
						log.info("Public key is not found!");
						throw new PublicKeyUnavailableException(scrypto.getPeerKey());
					}
					else
					{
						log.info("The node is shutdown! Hehe!");
						return null;
					}
					*/
					log.info("Public key is not found!");
					throw new PublicKeyUnavailableException(scrypto.getPeerKey());
				}
			}
			else
			{
				log.info("Session mismatched!!");
				throw new SessionMismatchedException("The session of private request mismatched!");
			}
		}
		else
		{
			throw new NonPrivateMachineException("The machine is non-private!");
		}
	}

	public PrivateResponse processClusterRootAsymmetricRequest(String serverKey, PrivatePrimitiveRequest request) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, ShortBufferException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException, SignatureException, CheatingException, PublicKeyUnavailableException, NonPrivateMachineException, SessionMismatchedException, MachineNotOwnedException, OwnerCheatingException, NonPublicMachineException
	{
		if (this.isPrivate.get())
		{
			if (request.getSessionKey().equals(this.asymCrypto.getSessionKey()))
			{
				AsymCompCrypto scrypto = (AsymCompCrypto)AsymmetricCoder.decrypt(request.getEncryptedSymCrypto(), this.asymCrypto.getAsymAlgorithm(), this.asymCrypto.getPrivateKey());
				PublicCrypto publicKey = this.getPublicCryptoByPeer(scrypto.getPeerKey());
				if (publicKey != null)
				{
					if (this.isSignatureTrusted(request.getSignature(), request.getEncryptedSignature(), false))
					{
						if (this.owners.containsKey(request.getSignature()))
						{
							log.info("owner = " + this.owners.get(request.getSignature()));
							log.info("request ownerName = " + request.getOwnerName());
							log.info("request signature = " + request.getSignature());
							if (!request.getOwnerName().equals(this.owners.get(request.getSignature()).getOwnerName()) || !request.getSignature().equals(this.owners.get(request.getSignature()).getSignature()))
							{
								throw new OwnerCheatingException(request.getOwnerName());
							}
							log.info(publicKey.getSignature() + " is requesting to you!");
							log.info("Received Encrypted Request: " + Hex.toHexString(request.getEncryptedData()));
//							ServerMessage response = ((ClusterTask)this.tasks.get(serverKey)).processRequest(SymmetricCoder.decryptMessage(request.getEncryptedData(), scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec()));
							
							// processPrivateRootRequest
//							ServerMessage response = this.processClusterRootRequest(serverKey, SymmetricCoder.decryptMessage(request.getEncryptedData(), scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec()));
							ServerMessage response = this.processPrivateRootRequest(serverKey, SymmetricCoder.decryptMessage(request.getEncryptedData(), scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec()));
							SymmetricCrypto nscrypto = SymmetricCoder.generateCrypto(this.asymCrypto.getSymCipherAlgorithm(), this.asymCrypto.getSymCipherSpec(), this.asymCrypto.getCipherKeyLength(), this.asymCrypto.getIVKeyLength());
							byte[] resData = SymmetricCoder.encryptResponse(response, nscrypto.getCipherKey(), nscrypto.getIVKey(), nscrypto.getCipherSpec());
							byte[] enNScryptoData = AsymmetricCoder.encrypt(nscrypto, publicKey.getAsymAlgorithm(), publicKey.getPublicKey());
							byte[] signedInfo = AsymmetricCoder.sign(ServiceProvider.CRY().getSignatureAlgorithm(), ServiceProvider.CRY().getPrivateKey(), ServiceProvider.CRY().getSignature());
							log.info("Encrypted Response: " + Hex.toHexString(resData));
							return new PrivateResponse(resData, enNScryptoData, ServiceProvider.CRY().getSignature(), signedInfo);
						}
						else
						{
							throw new MachineNotOwnedException(publicKey.getSignature());
						}
					}
					else
					{
						log.info("Someone is cheating as " + publicKey.getSignature());
						throw new CheatingException(publicKey.getSignature());
					}
				}
				else
				{
					/*
					if (!this.isBye.get())
					{
						log.info("Public key is not found!");
						throw new PublicKeyUnavailableException(scrypto.getPeerKey());
					}
					else
					{
						log.info("The node is shutdown! Hehe!");
						return null;
					}
					*/
					log.info("Public key is not found!");
					throw new PublicKeyUnavailableException(scrypto.getPeerKey());
				}
			}
			else
			{
				log.info("Session mismatched!!");
				throw new SessionMismatchedException("The session of private request mismatched!");
			}
		}
		else
		{
			throw new NonPrivateMachineException("The machine is non-private!");
		}
	}

	public PrivateResponse processClusterChildAsymmetricRequest(String serverKey, PrivatePrimitiveRequest request) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, ShortBufferException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException, SignatureException, CheatingException, PublicKeyUnavailableException, NonPrivateMachineException, SessionMismatchedException, MachineNotOwnedException, OwnerCheatingException, NonPublicMachineException
	{
		if (this.isPrivate.get())
		{
			if (request.getSessionKey().equals(this.asymCrypto.getSessionKey()))
			{
				AsymCompCrypto scrypto = (AsymCompCrypto)AsymmetricCoder.decrypt(request.getEncryptedSymCrypto(), this.asymCrypto.getAsymAlgorithm(), this.asymCrypto.getPrivateKey());
				PublicCrypto publicKey = this.getPublicCryptoByPeer(scrypto.getPeerKey());
				if (publicKey != null)
				{
					if (this.isSignatureTrusted(request.getSignature(), request.getEncryptedSignature(), false))
					{
						if (this.owners.containsKey(request.getSignature()))
						{
							log.info("owner = " + this.owners.get(request.getSignature()));
							log.info("request ownerName = " + request.getOwnerName());
							log.info("request signature = " + request.getSignature());
							if (!request.getOwnerName().equals(this.owners.get(request.getSignature()).getOwnerName()) || !request.getSignature().equals(this.owners.get(request.getSignature()).getSignature()))
							{
								throw new OwnerCheatingException(request.getOwnerName());
							}
							log.info(publicKey.getSignature() + " is requesting to you!");
							log.info("Received Encrypted Request: " + Hex.toHexString(request.getEncryptedData()));
							ServerMessage response = this.processClusterChildRequest(serverKey, SymmetricCoder.decryptMessage(request.getEncryptedData(), scrypto.getCipherKey(), scrypto.getIVKey(), scrypto.getCipherSpec()));
							SymmetricCrypto nscrypto = SymmetricCoder.generateCrypto(this.asymCrypto.getSymCipherAlgorithm(), this.asymCrypto.getSymCipherSpec(), this.asymCrypto.getCipherKeyLength(), this.asymCrypto.getIVKeyLength());
							byte[] resData = SymmetricCoder.encryptResponse(response, nscrypto.getCipherKey(), nscrypto.getIVKey(), nscrypto.getCipherSpec());
							byte[] enNScryptoData = AsymmetricCoder.encrypt(nscrypto, publicKey.getAsymAlgorithm(), publicKey.getPublicKey());
							byte[] signedInfo = AsymmetricCoder.sign(ServiceProvider.CRY().getSignatureAlgorithm(), ServiceProvider.CRY().getPrivateKey(), ServiceProvider.CRY().getSignature());
							log.info("Encrypted Response: " + Hex.toHexString(resData));
							return new PrivateResponse(resData, enNScryptoData, ServiceProvider.CRY().getSignature(), signedInfo);
						}
						else
						{
							throw new MachineNotOwnedException(publicKey.getSignature());
						}
					}
					else
					{
						log.info("Someone is cheating as " + publicKey.getSignature());
						throw new CheatingException(publicKey.getSignature());
					}
				}
				else
				{
					/*
					if (!this.isBye.get())
					{
						log.info("Public key is not found!");
						throw new PublicKeyUnavailableException(scrypto.getPeerKey());
					}
					else
					{
						log.info("The node is shutdown! Hehe!");
						return null;
					}
					*/
					log.info("Public key is not found!");
					throw new PublicKeyUnavailableException(scrypto.getPeerKey());
				}
			}
			else
			{
				log.info("Session mismatched!!");
				throw new SessionMismatchedException("The session of private request mismatched!");
			}
		}
		else
		{
			throw new NonPrivateMachineException("The machine is non-private!");
		}
	}

	public boolean isSignatureTrusted(String signature, byte[] encryptedSignature, boolean isInit) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException, IOException
	{
		PublicCrypto existingPC = this.signaturePublicKeys.get(signature);
		if (existingPC != null)
		{
			log.info("Signature: " + signature + " is being verified ...");
			if (!AsymmetricCoder.verify(existingPC.getSignatureAlgorithm(), existingPC.getPublicKey(), existingPC.getSignature(), encryptedSignature))
			{
				log.info("Someone is pretending to be " + existingPC.getSignature());
				return false;
			}
			else
			{
				log.info("A trusted partner, " + signature + ", is starting to interact with you!");
				return true;
			}
		}
		else
		{
			if (!isInit)
			{
				log.info("Signature: " + signature + "'s public key is not found ...");
			}
		}
		if (isInit)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private void processPrimitive(String serverKey, ServerMessage message)
	{
		switch (message.getType())
		{
			case MulticastMessageType.MULTICAST_RESPONSE:
				log.info("MULTICAST_RESPONSE received @" + Calendar.getInstance().getTime());
//				((MulticastTask)this.tasks.get(serverKey)).processNotification((MulticastResponse)message);
				((MulticastTask)this.tasks.get(serverKey)).processNotification((PrimitiveMulticastResponse)message);
				break;
				
			case MulticastMessageType.MULTICAST_NOTIFICATION:
				log.info("MULTICAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
//				((MulticastTask)this.tasks.get(serverKey)).processNotification((MulticastNotification)message);
				((MulticastTask)this.tasks.get(serverKey)).processNotification((PrimitiveMulticastNotification)message);
				break;
				
			case MulticastMessageType.MULTICAST_REQUEST:
				log.info("MULTICAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
//				((MulticastTask)this.tasks.get(serverKey)).processRequest((MulticastRequest)message);
				((MulticastTask)this.tasks.get(serverKey)).processRequest((PrimitiveMulticastRequest)message);
				break;
		}
	}
}
