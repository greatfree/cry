package org.greatfree.cry.framework.blockchain.peer;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.ArrayList;
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
import org.greatfree.cry.framework.blockchain.Block;
import org.greatfree.cry.framework.blockchain.BlockConfig;
import org.greatfree.cry.framework.blockchain.BlockInfo;
import org.greatfree.cry.framework.blockchain.message.JoinChainRequest;
import org.greatfree.cry.framework.blockchain.message.JoinChainResponse;
import org.greatfree.cry.framework.blockchain.message.PrecedingFingerPrintRequest;
import org.greatfree.cry.framework.blockchain.message.PrecedingFingerPrintResponse;
import org.greatfree.cry.framework.blockchain.message.SucceedingPeerNotification;
import org.greatfree.cry.framework.blockchain.message.SucceedingValidateRequest;
import org.greatfree.cry.framework.blockchain.message.SucceedingValidateResponse;
import org.greatfree.cry.framework.blockchain.message.SucceedingBlockRequest;
import org.greatfree.cry.framework.blockchain.message.SucceedingBlockResponse;
import org.greatfree.cry.framework.blockchain.message.TraverseChainRequest;
import org.greatfree.cry.framework.blockchain.message.TraverseChainResponse;
import org.greatfree.cry.framework.blockchain.message.ValidateChainRequest;
import org.greatfree.cry.framework.blockchain.message.ValidateChainResponse;
import org.greatfree.cry.framework.tncs.CryptoConfig;
import org.greatfree.cry.server.Peer;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;

/**
 * 
 * @author libing
 * 
 * 01/26/2022, Bing Li
 *
 */
public class ChainNode
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.framework.blockchain.peer");

	private Peer peer;
	private Block block;
	
	private ChainNode()
	{
	}
	
	private static ChainNode instance = new ChainNode();
	
	public static ChainNode CHAIN()
	{
		if (instance == null)
		{
			instance = new ChainNode();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void stop() throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException, SymmetricKeyUnavailableException
	{
		if (this.block != null)
		{
			this.block.close();
		}
		this.peer.stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
	}
	
	public void start(String nodeName) throws IOException, ClassNotFoundException, RemoteReadException, InvalidKeyException, NoSuchAlgorithmException, SignatureException, DistributedNodeFailedException, CryptographyMismatchException
	{
		this.peer = new Peer.PeerBuilder()
				.peerName(nodeName)
				.port(BlockConfig.CHAIN_NODE_PORT)
				.registryServerIP(BlockConfig.BC_REGISTRY_IP)
				.registryServerPort(BlockConfig.BC_REGISTRY_PORT)
				.task(new ChainNodeTask())
				.isRegistryNeeded(true)
				.asymCipherAlgorithm(CryptoConfig.RSA)
				.asymCipherKeyLength(CryptoConfig.RSA_LENGTH)
				.symCipherAlgorithm(CryptoConfig.AES)
				.symCipherSpec(CryptoConfig.AES_SPEC)
				.symCipherKeyLength(CryptoConfig.SYMMETRIC_KEY_LENGTH)
				.symIVKeyLength(CryptoConfig.SYMMETRIC_IV_KEY_LENGTH)
				.signatureAlgorithm(CryptoConfig.SHA_WITH_RSA)
				.signature(nodeName + CryptoConfig.SIGNATURE_SUFFIX)
				.isAsymCryptography(true)
				.build();
		this.peer.start();
		this.peer.inviteAsymPartner(BlockConfig.BC_COORDINATOR);
	}
	
	public void joinChain() throws IOException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, SignatureException, RemoteReadException, InterruptedException, DistributedNodeFailedException, CryptographyMismatchException
	{
		/*
		 * Spend efforts to create one new block upon the preceding finger print. 01/28/2022, Bing Li
		 */
		this.block = new Block(this.peer.getPeerName());
		PrecedingFingerPrintResponse pfpr;
		try
		{
			pfpr = (PrecedingFingerPrintResponse)this.peer.readBySignature(BlockConfig.BC_COORDINATOR, new PrecedingFingerPrintRequest());
			this.block.setPrecedingFingerPrint(pfpr.getPrecedingFingerPrint());
			log.info("The preceding finger print is " + pfpr.getPrecedingFingerPrint());
			log.info("Starting to mine the new block with the difficulty degree, " + BlockConfig.BLOCK_FINGER_PRINT_DIFFICULTY);
			this.block.mine(BlockConfig.BLOCK_FINGER_PRINT_DIFFICULTY);
			log.info("Mining the new block is DONE!");
			log.info("The new block's finger print is " + this.block.getFingerPrint());

			JoinChainResponse jcr = (JoinChainResponse)this.peer.readBySignature(BlockConfig.BC_COORDINATOR, new JoinChainRequest(new BlockInfo(this.peer.getPeerName(), this.block.getFingerPrint())));
			log.info("You are the NO. " + jcr.getSequenceNO() + " block!");
			this.block.setSequenceNO(jcr.getSequenceNO());
			if (jcr.getPrecedingBlockInfo() != null)
			{
				log.info("Your preceding node is " + jcr.getPrecedingBlockInfo().getNodeName());
				this.block.setHead(jcr.getHeadPeerName());
//				this.block.setPrecedingFingerPrint(jcr.getPrecedingBlockInfo().getFingerPrint());
				this.peer.inviteAsymPartner(jcr.getHeadPeerName());
				if (!jcr.getHeadPeerName().equals(jcr.getPrecedingBlockInfo().getNodeName()))
				{
					this.peer.inviteAsymPartner(jcr.getPrecedingBlockInfo().getNodeName());
				}
				this.peer.syncNotifyBySignature(jcr.getPrecedingBlockInfo().getNodeName(), new SucceedingPeerNotification(this.peer.getPeerName()));
			}
			else
			{
				log.info("No preceding block is available right now!");
				/*
				 * If the below line is executed, it indicates that the current node is the first one in the chain and no other partners are available. 01/27/2022, Bing Li
				 */
				this.block.setHead(this.peer.getPeerName());
				this.block.setPrecedingFingerPrint(BlockConfig.NO_PRECEDING_FINGER_PRINT);
			}
		}
		catch (OwnerCheatingException | CheatingException | PublicKeyUnavailableException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public List<String> traverse() throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, RemoteReadException, IOException, CryptographyMismatchException, DistributedNodeFailedException
	{
		if (!this.block.isPrecedingExisted())
		{
			log.info("The current block is the head!");
			/*
			 * If the above condition is fulfilled, it denotes that the current peer is the head. 01/27/2022, Bing Li
			 */
			return this.querySucceedingly();
		}
		else
		{
			log.info("The current block is the intermediate node!");
			List<String> blocks = new ArrayList<String>();
			/*
			 * If the condition is fulfilled, it denotes that the current peer is an intermediate node or the end of the chain. Thus, the node needs to send the traversal request to the head and get the response from the head. 01/27/2022, Bing Li
			 * 
			 * It implies that the traversal is always started from the head. 01/27/2022, Bing Li
			 * 
			 */
//				List<String> succeedingBlocks = ((TraverseChainResponse)this.peer.read(this.block.getHead(), new TraverseChainRequest(this.peer.getPeerName()))).getBlocksInJSON();
			List<String> succeedingBlocks;
			try
			{
				succeedingBlocks = ((TraverseChainResponse)this.peer.readAsymmetrically(this.block.getHead(), new TraverseChainRequest())).getBlocksInJSON();
				blocks.addAll(succeedingBlocks);
			}
			catch (PublicKeyUnavailableException e)
			{
				e.printStackTrace();
			}
			return blocks;
		}
	}
	
	public List<String> querySucceedingly() throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, RemoteReadException, IOException, CryptographyMismatchException, DistributedNodeFailedException
	{
		if (!this.block.isSucceedingExisted())
		{
			List<String> blocks = new ArrayList<String>();
			blocks.add(this.block.getBlockView());
			return blocks;
		}
		SucceedingBlockResponse response;
		try
		{
			response = (SucceedingBlockResponse)this.peer.readAsymmetrically(this.block.getSucceedingPeerName(), new SucceedingBlockRequest());
			List<String> blocks = response.getBlocksInJSON();
			blocks.add(this.block.getBlockView());
			return blocks;
		}
		catch (PublicKeyUnavailableException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public ValidateChainResponse validate() throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, RemoteReadException, IOException, CryptographyMismatchException, DistributedNodeFailedException
	{
		if (!this.block.isPrecedingExisted())
		{
			/*
			 * If the condition is fulfilled, it denotes that the current peer is the head. 01/27/2022, Bing Li
			 */
			SucceedingValidateResponse response = this.validateSucceedingly(this.block.getFingerPrint());
			return new ValidateChainResponse(response.getSequenceNO(), response.isValid());
		}
		else
		{
			/*
			 * If the condition is fulfilled, it denotes that the current peer is an intermediate node or the end of the chain. Thus, the node needs to send the validation request to the head and get the response from the head. 01/27/2022, Bing Li
			 * 
			 * It implies that the validation is always started from the head. 01/27/2022, Bing Li
			 * 
			 */
			try
			{
				return (ValidateChainResponse)this.peer.readAsymmetrically(this.block.getHead(), new ValidateChainRequest());
			}
			catch (PublicKeyUnavailableException e)
			{
				return null;
			}
		}
	}
	
	public SucceedingValidateResponse validateSucceedingly(String precedingFingerPrint) throws IOException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, RemoteReadException, CryptographyMismatchException, DistributedNodeFailedException
	{
		if (!this.block.validate(precedingFingerPrint))
		{
			return new SucceedingValidateResponse(this.block.getSequenceNO(), false);
		}
		
		if (!this.block.isSucceedingExisted())
		{
			return new SucceedingValidateResponse(this.block.getSequenceNO(), true);
		}
		try
		{
			return (SucceedingValidateResponse)this.peer.readAsymmetrically(this.block.getSucceedingPeerName(), new SucceedingValidateRequest(this.block.getFingerPrint()));
		}
		catch (PublicKeyUnavailableException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public void setSucceedingNodeName(String nodeName)
	{
		this.block.setSucceedingPeerName(nodeName);
	}

	/*
	public void setOwner(OwnerInfo ownerName)
	{
		this.peer.setOwner(ownerName);
	}
	
	public OwnerInfo getOwner()
	{
		return this.peer.getOwner();
	}
	*/

	/*
	public boolean isOwner(String owner, Notification notification) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, SignatureException, IOException
	{
		return this.peer.isOwner(owner, notification);
	}
	
	public boolean isOwner(String owner, Request request) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, SignatureException, IOException
	{
		return this.peer.isOwner(owner, request);
	}
	*/
}
