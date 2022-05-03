package org.greatfree.cry.framework.multisigned.participant;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.List;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import org.greatfree.cry.exceptions.CheatingException;
import org.greatfree.cry.exceptions.CryptographyMismatchException;
import org.greatfree.cry.exceptions.OwnerCheatingException;
import org.greatfree.cry.exceptions.PublicKeyUnavailableException;
import org.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import org.greatfree.cry.framework.blockchain.BlockConfig;
import org.greatfree.cry.framework.multisigned.message.ApprovalRequest;
import org.greatfree.cry.framework.multisigned.message.ApprovalResponse;
import org.greatfree.cry.framework.multisigned.message.OperateNotification;
import org.greatfree.cry.framework.multisigned.message.OperateRequest;
import org.greatfree.cry.framework.multisigned.message.OperateResponse;
import org.greatfree.cry.framework.multisigned.message.StopServerNotification;
import org.greatfree.cry.framework.tncs.CryptoConfig;
import org.greatfree.cry.messege.AllOwners;
import org.greatfree.cry.server.Peer;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.UtilConfig;

/**
 * 
 * @author libing
 * 
 * 03/24/2022
 *
 */
class Participant
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.framework.multisigned.participant");

	private Peer peer;
	private Profile profile;

	private Participant()
	{
	}
	
	private static Participant instance = new Participant();
	
	public static Participant CONS()
	{
		if (instance == null)
		{
			instance = new Participant();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void stop() throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException, SymmetricKeyUnavailableException
	{
		this.peer.stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
	}

	public void start(String peerName, String ownerName, String signature, String machineName) throws IOException, ClassNotFoundException, RemoteReadException
	{
		this.peer = new Peer.PeerBuilder()
				.peerName(peerName)
				.port(BlockConfig.CHAIN_NODE_PORT)
				.registryServerIP(BlockConfig.BC_REGISTRY_IP)
				.registryServerPort(BlockConfig.BC_REGISTRY_PORT)
				.task(new ParticipantTask())
				.isRegistryNeeded(true)
				.asymCipherAlgorithm(CryptoConfig.RSA)
				.asymCipherKeyLength(CryptoConfig.RSA_LENGTH)
				.symCipherAlgorithm(CryptoConfig.AES)
				.symCipherSpec(CryptoConfig.AES_SPEC)
				.symCipherKeyLength(CryptoConfig.SYMMETRIC_KEY_LENGTH)
				.symIVKeyLength(CryptoConfig.SYMMETRIC_IV_KEY_LENGTH)
				.signatureAlgorithm(CryptoConfig.SHA_WITH_RSA)
				.signature(signature + CryptoConfig.SIGNATURE_SUFFIX)
				.isAsymCryptography(true)
				.isPrivate(false)
				.build();
		this.peer.start();
		this.profile = new Profile(ownerName, machineName);
	}
	
	public void addPartner(String partner) throws InvalidKeyException, NoSuchAlgorithmException, ClassNotFoundException, SignatureException, RemoteReadException, IOException, DistributedNodeFailedException, CryptographyMismatchException
	{
		this.profile.addPartner(partner);
		this.peer.inviteAsymPartner(partner);
	}
	
	public void removePartner(String partner)
	{
		this.profile.removePartner(partner);
	}

	public void inviteServer() throws InvalidKeyException, NoSuchAlgorithmException, ClassNotFoundException, SignatureException, RemoteReadException, IOException, DistributedNodeFailedException, CryptographyMismatchException
	{
		this.peer.inviteAsymPartner(this.profile.getServerName());
	}

	/*
	private void inviteOwner(String owner) throws InvalidKeyException, NoSuchAlgorithmException, ClassNotFoundException, SignatureException, RemoteReadException, IOException, DistributedNodeFailedException, CryptographyMismatchException
	{
		this.peer.inviteAsymPartner(owner);
	}
	*/
	
	public boolean claimAsMachineOwner() throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, SignatureException, CryptographyMismatchException, RemoteReadException, IOException, DistributedNodeFailedException, OwnerCheatingException, CheatingException, PublicKeyUnavailableException
	{
		AllOwners aos = this.peer.claimOwner(this.profile.getOwner(), this.profile.getServerName());
		if (aos.getAllOwners() != null)
		{
//			log.info("AllOwners is NOT null");
			this.profile.setPartners(aos.getAllOwners());
			/*
			for (OwnerInfo entry : aos.getAllOwners())
			{
				if (!entry.getOwnerName().equals(this.profile.getOwner()))
				{
					this.inviteOwner(entry.getOwnerName());
				}
			}
			*/
		}
		else
		{
			log.info("AllOwners is null");
		}
//		return this.peer.claimOwner(this.profile.getOwner(), this.profile.getServerName());
		return aos.isSucceeded();
	}

	public String getServerName()
	{
		return this.profile.getServerName();
	}
	
	public void uniConsensusRequest(String operation, String description) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, SignatureException, RemoteReadException, IOException, CryptographyMismatchException, DistributedNodeFailedException, CheatingException, PublicKeyUnavailableException
	{
		String partnerName = this.profile.getRandomPartner();
		if (!partnerName.equals(UtilConfig.EMPTY_STRING))
		{
			if (this.requestApproval(partnerName, description))
			{
				log.info("The notification operation, " + operation + ", is performed upon the consensus of " + partnerName);
				try
				{
					OperateResponse response = (OperateResponse)this.peer.readPrivately(this.profile.getOwner(), this.profile.getServerName(), new OperateRequest(operation, description));
					if (response != null)
					{
						if (response.isDone())
						{
							log.info("The request operation, " + operation + ", is done successfully!");
						}
						else
						{
							log.info("The request operation, " + operation + ", is failed to be done!");
						}
					}
					else
					{
						log.info("The request operation, " + operation + ", is failed to be done!");
					}
				}
				catch (OwnerCheatingException e)
				{
					log.info("OwnerCheatingException: The request operation, " + operation + ", is failed to be done because you are NOT the owner ...");
				}
			}
			else
			{
				log.info("The request operation, " + operation + ", is not performed because of the disagreement of " + partnerName);
			}
		}
		else
		{
			log.info(this.profile.getOwner() + " is the unique participant such that the consensus is reached by default!");
			try
			{
				OperateResponse response = (OperateResponse)this.peer.readPrivately(this.profile.getOwner(), this.profile.getServerName(), new OperateRequest(operation, description));
				if (response != null)
				{
					if (response.isDone())
					{
						log.info("The request operation, " + operation + ", is done successfully!");
					}
					else
					{
						log.info("The request operation, " + operation + ", is failed to be done!");
					}
				}
				else
				{
					log.info("The request operation, " + operation + ", is failed to be done!");
				}
			}
			catch (OwnerCheatingException e)
			{
				log.info("OwnerCheatingException: The request operation, " + operation + ", is failed to be done because you are NOT the owner ...");
			}
		}
	}
	
	public void uniConsensusNotify(String operation, String description) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, SignatureException, RemoteReadException, IOException, CryptographyMismatchException, DistributedNodeFailedException, OwnerCheatingException, CheatingException, PublicKeyUnavailableException, InterruptedException
	{
		String partnerName = this.profile.getRandomPartner();
		if (!partnerName.equals(UtilConfig.EMPTY_STRING))
		{
//			log.info("partnerName = " + partnerName);
			if (this.requestApproval(partnerName, description))
			{
				log.info("The notification operation, " + operation + ", is performed upon the consensus of " + partnerName);
				this.peer.syncNotifyPrivately(this.profile.getOwner(), this.profile.getServerName(), new OperateNotification(operation, description));
				log.info("The notification operation, " + operation + ", is performed!");
			}
			else
			{
				log.info("The notification operation, " + operation + ", is not performed because of the disagreement of " + partnerName);
			}
		}
		else
		{
			log.info(this.profile.getOwner() + " is the unique participant such that the consensus is reached by default!");
			this.peer.syncNotifyPrivately(this.profile.getOwner(), this.profile.getServerName(), new OperateNotification(operation, description));
			log.info("The notification operation, " + operation + ", is performed!");
		}
	}

	public void anyConsensusRequest(String operation, String description) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, SignatureException, RemoteReadException, IOException, CryptographyMismatchException, DistributedNodeFailedException, CheatingException, PublicKeyUnavailableException
	{
		List<String> partnerNames = this.profile.getPartners();
		if (partnerNames.size() > 0)
		{
			for (String entry : partnerNames)
			{
				if (this.requestApproval(entry, description))
				{
					log.info("The request operation, " + operation + ", is performed upon the consensus of " + entry);
					try
					{
						OperateResponse response = (OperateResponse)this.peer.readPrivately(this.profile.getOwner(), this.profile.getServerName(), new OperateRequest(operation, description));
						if (response != null)
						{
							if (response.isDone())
							{
								log.info("The request operation, " + operation + ", is done successfully!");
							}
							else
							{
								log.info("The request operation, " + operation + ", is failed to be done!");
							}
							break;
						}
						else
						{
							log.info("The request operation, " + operation + ", is failed to be done!");
							break;
						}
					}
					catch (OwnerCheatingException e)
					{
						log.info("OwnerCheatingException: The request operation, " + operation + ", is failed to be done because you are NOT the owner ...");
					}
				}
				else
				{
					log.info("The request operation, " + operation + ", is not performed because the disagreement of " + entry);
				}
			}
		}
		else
		{
			log.info(this.profile.getOwner() + " is the unique participant such that the consensus is reached by default!");
			try
			{
				OperateResponse response = (OperateResponse)this.peer.readPrivately(this.profile.getOwner(), this.profile.getServerName(), new OperateRequest(operation, description));
				if (response != null)
				{
					if (response.isDone())
					{
						log.info("The request operation, " + operation + ", is performed successfully!");
					}
					else
					{
						log.info("The request operation, " + operation + ", is failed to be performed!");
					}
				}
				else
				{
					log.info("The request operation, " + operation + ", is failed to be done!");
				}
			}
			catch (OwnerCheatingException e)
			{
				log.info("OwnerCheatingException: The request operation, " + operation + ", is failed to be done because you are NOT the owner ...");
			}
		}
	}

	public void anyConsensusNotify(String operation, String description) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, SignatureException, RemoteReadException, IOException, CryptographyMismatchException, DistributedNodeFailedException, OwnerCheatingException, CheatingException, PublicKeyUnavailableException, InterruptedException
	{
		List<String> partnerNames = this.profile.getPartners();
		if (partnerNames.size() > 0)
		{
			for (String entry : partnerNames)
			{
				if (this.requestApproval(entry, description))
				{
					log.info("The notification operation, " + operation + ", is performed upon the consensus of " + entry);
					this.peer.syncNotifyPrivately(this.profile.getOwner(), this.profile.getServerName(), new OperateNotification(operation, description));
					log.info("The notification operation, " + operation + ", is performed!");
					break;
				}
				else
				{
					log.info("The notification operation, " + operation + ", is not performed because the disagreement of " + entry);
				}
			}
		}
		else
		{
			log.info(this.profile.getOwner() + " is the unique participant such that the consensus is reached by default!");
			this.peer.syncNotifyPrivately(this.profile.getOwner(), this.profile.getServerName(), new OperateNotification(operation, description));
			log.info("The notification operation, " + operation + ", is performed!");
		}
	}

	public void broadConsensusRequest(String operation, String description) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, SignatureException, RemoteReadException, IOException, CryptographyMismatchException, DistributedNodeFailedException, CheatingException, PublicKeyUnavailableException
	{
		List<String> partnerNames = this.profile.getPartners();
		if (partnerNames.size() > 0)
		{
			boolean isApproved = true;
			for (String entry : partnerNames)
			{
				if (!this.requestApproval(entry, description))
				{
					isApproved = false;
					break;
				}
			}
			if (isApproved)
			{
				log.info("The request operation, " + operation + ", is performed upon the consensus of all the participants!");
				try
				{
					OperateResponse response = (OperateResponse)this.peer.readPrivately(this.profile.getOwner(), this.profile.getServerName(), new OperateRequest(operation, description));
					if (response != null)
					{
						if (response.isDone())
						{
							log.info("The request operation, " + operation + ", is done successfully!");
						}
						else
						{
							log.info("The request operation, " + operation + ", is failed to be done!");
						}
					}
					else
					{
						log.info("The request operation, " + operation + ", is failed to be done!");
					}
				}
				catch (OwnerCheatingException e)
				{
					log.info("OwnerCheatingException: The request operation, " + operation + ", is failed to be done because you are NOT the owner ...");
				}
			}
			else
			{
				log.info("The request operation, " + operation + ", is not performed because of the disagreement of the participants!");
			}
		}
		else
		{
			log.info(this.profile.getOwner() + " is the unique participant such that the consensus is reached by default!");
			try
			{
				OperateResponse response = (OperateResponse)this.peer.readPrivately(this.profile.getOwner(), this.profile.getServerName(), new OperateRequest(operation, description));
				if (response != null)
				{
					if (response.isDone())
					{
						log.info("The request operation, " + operation + ", is done successfully!");
					}
					else
					{
						log.info("The request operation, " + operation + ", is failed to be done!");
					}
				}
				else
				{
					log.info("The request operation, " + operation + ", is failed to be done!");
				}
			}
			catch (OwnerCheatingException e)
			{
				log.info("OwnerCheatingException: The request operation, " + operation + ", is failed to be done because you are NOT the owner ...");
			}
		}
	}

	public void broadConsensusNotify(String operation, String description) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, SignatureException, RemoteReadException, IOException, CryptographyMismatchException, DistributedNodeFailedException, OwnerCheatingException, CheatingException, PublicKeyUnavailableException, InterruptedException
	{
		List<String> partnerNames = this.profile.getPartners();
		if (partnerNames.size() > 0)
		{
			boolean isApproved = true;
			for (String entry : partnerNames)
			{
				if (!this.requestApproval(entry, description))
				{
					isApproved = false;
					break;
				}
			}
			if (isApproved)
			{
				log.info("The notification operation, " + operation + ", is performed upon the consensus of all the participants!");
				this.peer.syncNotifyPrivately(this.profile.getOwner(), this.profile.getServerName(), new OperateNotification(operation, description));
				log.info("The notification operation, " + operation + ", is performed!");
			}
			else
			{
				log.info("The notification operation, " + operation + ", is not performed because of the disagreement of the participants!");
			}
		}
		else
		{
			log.info(this.profile.getOwner() + " is the unique participant such that the consensus is reached by default!");
			this.peer.syncNotifyPrivately(this.profile.getOwner(), this.profile.getServerName(), new OperateNotification(operation, description));
			log.info("The notification operation, " + operation + ", is performed!");
		}
	}
	
	public void stopServer() throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, SignatureException, RemoteReadException, IOException, InterruptedException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException
	{
		this.peer.syncNotifyPrivately(this.profile.getOwner(), this.profile.getServerName(), new StopServerNotification());
	}

	private boolean requestApproval(String partner, String description) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, SignatureException, RemoteReadException, IOException, CryptographyMismatchException, DistributedNodeFailedException, CheatingException, PublicKeyUnavailableException
	{
		if (!partner.equals(this.profile.getOwner()))
		{
			ApprovalResponse response;
			try
			{
				response = (ApprovalResponse)this.peer.readBySignature(partner, new ApprovalRequest(description));
				if (response != null)
				{
					return response.isApproved();
				}
				return false;
			}
			catch (OwnerCheatingException e)
			{
				e.printStackTrace();
			}
		}
		return true;
	}
}
