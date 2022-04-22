package org.greatfree.cry.framework.bitcoin.peer;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import org.greatfree.cry.exceptions.CheatingException;
import org.greatfree.cry.exceptions.CryptographyMismatchException;
import org.greatfree.cry.exceptions.OwnerCheatingException;
import org.greatfree.cry.exceptions.PublicKeyUnavailableException;
import org.greatfree.cry.framework.bitcoin.CoinBlock;
import org.greatfree.cry.framework.bitcoin.CoinBlockInfo;
import org.greatfree.cry.framework.bitcoin.CoinConfig;
import org.greatfree.cry.framework.bitcoin.Transaction;
import org.greatfree.cry.framework.bitcoin.message.JoinCoinChainRequest;
import org.greatfree.cry.framework.bitcoin.message.JoinCoinChainResponse;
import org.greatfree.cry.framework.blockchain.BlockConfig;
import org.greatfree.cry.framework.blockchain.message.PrecedingFingerPrintRequest;
import org.greatfree.cry.framework.blockchain.message.PrecedingFingerPrintResponse;
import org.greatfree.cry.framework.blockchain.message.SucceedingPeerNotification;
import org.greatfree.cry.server.Peer;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;

/**
 * 
 * The program aims to lowers the lines of code of the CoinNode. 02/27/2022, Bing Li
 * 
 * @author libing
 * 
 * 02/27/2022
 *
 */
class Joint
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.framework.bitcoin.peer");

	/*
	 * In the current version, the method is invoked by the intermediate peer and the end peer of the chain once if the peers receive the request from the head of the networking. 02/25/2022, Bing Li
	 */
//	public void joinChain(String sessionKey, Date timeStamp) throws IOException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, SignatureException, RemoteReadException, InterruptedException, DistributedNodeFailedException, CryptographyMismatchException
	public static boolean joinChain(Peer peer, Map<String, CoinBlock> blocks, String sessionKey) throws IOException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, SignatureException, RemoteReadException, InterruptedException, DistributedNodeFailedException, CryptographyMismatchException
	{
		/*
		 * Spend efforts to create one new block upon the preceding finger print. 01/28/2022, Bing Li
		 */
		CoinBlock block = new CoinBlock(sessionKey, peer.getPeerName());
		blocks.put(block.getSessionKey(), block);
		PrecedingFingerPrintResponse pfpr;
		try
		{
			pfpr = (PrecedingFingerPrintResponse)peer.readBySignature(CoinConfig.COIN_COORDINATOR, new PrecedingFingerPrintRequest(sessionKey));
			block.setPrecedingFingerPrint(pfpr.getPrecedingFingerPrint());
//			this.loadTransactions(block, timeStamp);
			TransactionPool.COIN().loadTransactions(block);
			log.info("The preceding finger print is " + pfpr.getPrecedingFingerPrint());
			log.info("Starting to mine the new block with the difficulty degree, " + BlockConfig.BLOCK_FINGER_PRINT_DIFFICULTY);
			block.mine(BlockConfig.BLOCK_FINGER_PRINT_DIFFICULTY, block.getInputs(), block.getOutputs());
			log.info("Mining the new block is DONE!");
			log.info("The new block's finger print is " + block.getFingerPrint());

//			JoinCoinChainResponse jcr = (JoinCoinChainResponse)this.peer.readBySignature(CoinConfig.COIN_COORDINATOR, new JoinCoinChainRequest(new CoinBlockInfo(sessionKey, this.peer.getPeerName(), block.getFingerPrint(), timeStamp)));
			JoinCoinChainResponse jcr = (JoinCoinChainResponse)peer.readBySignature(CoinConfig.COIN_COORDINATOR, new JoinCoinChainRequest(new CoinBlockInfo(sessionKey, peer.getPeerName(), block.getFingerPrint())));
			log.info("You are the NO. " + jcr.getSequenceNO() + " block!");
			block.setSequenceNO(jcr.getSequenceNO());
			if (jcr.getPrecedingBlockInfo() != null)
			{
				log.info("Your preceding node is " + jcr.getPrecedingBlockInfo().getNodeName());
				block.setHead(jcr.getHeadPeerName());
				peer.inviteAsymPartner(jcr.getHeadPeerName());
				if (!jcr.getHeadPeerName().equals(jcr.getPrecedingBlockInfo().getNodeName()))
				{
					peer.inviteAsymPartner(jcr.getPrecedingBlockInfo().getNodeName());
				}
				peer.syncNotifyBySignature(jcr.getPrecedingBlockInfo().getNodeName(), new SucceedingPeerNotification(peer.getPeerName()));
			}
			else
			{
				log.info("No preceding block is available right now!");
				/*
				 * If the below line is executed, it indicates that the current node is the first one in the chain and no other partners are available. 01/27/2022, Bing Li
				 */
				block.setHead(peer.getPeerName());
				block.setPrecedingFingerPrint(BlockConfig.NO_PRECEDING_FINGER_PRINT);
			}
		}
		catch (OwnerCheatingException | CheatingException | PublicKeyUnavailableException e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/*
	 * Only the head of the networking invokes the method. 02/25/2022, Bing Li
	 */
	public static void joinChain(Peer peer, Map<String, CoinBlock> blocks, String sessionKey, List<Transaction> trans) throws IOException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, SignatureException, RemoteReadException, InterruptedException, DistributedNodeFailedException, CryptographyMismatchException
	{
		log.info("Starting to join a chain ...");
		/*
		 * Spend efforts to create one new block upon the preceding finger print. 01/28/2022, Bing Li
		 */
		CoinBlock block = new CoinBlock(sessionKey, peer.getPeerName());
		blocks.put(block.getSessionKey(), block);
		PrecedingFingerPrintResponse pfpr;
		try
		{
			pfpr = (PrecedingFingerPrintResponse)peer.readBySignature(CoinConfig.COIN_COORDINATOR, new PrecedingFingerPrintRequest(sessionKey));
			block.setPrecedingFingerPrint(pfpr.getPrecedingFingerPrint());
//			this.loadTransactions(block, timeStamp);
			TransactionPool.COIN().loadTransactions(block, trans);
			log.info("The preceding finger print is " + pfpr.getPrecedingFingerPrint());
			log.info("Starting to mine the new block with the difficulty degree, " + BlockConfig.BLOCK_FINGER_PRINT_DIFFICULTY);
			block.mine(BlockConfig.BLOCK_FINGER_PRINT_DIFFICULTY, block.getInputs(), block.getOutputs());
			log.info("Mining the new block is DONE!");
			log.info("The new block's finger print is " + block.getFingerPrint());

//			JoinCoinChainResponse jcr = (JoinCoinChainResponse)this.peer.readBySignature(CoinConfig.COIN_COORDINATOR, new JoinCoinChainRequest(new CoinBlockInfo(sessionKey, this.peer.getPeerName(), block.getFingerPrint(), timeStamp)));
			JoinCoinChainResponse jcr = (JoinCoinChainResponse)peer.readBySignature(CoinConfig.COIN_COORDINATOR, new JoinCoinChainRequest(new CoinBlockInfo(sessionKey, peer.getPeerName(), block.getFingerPrint())));
			log.info("You are the NO. " + jcr.getSequenceNO() + " block!");
			block.setSequenceNO(jcr.getSequenceNO());
			if (jcr.getPrecedingBlockInfo() != null)
			{
				log.info("Your preceding node is " + jcr.getPrecedingBlockInfo().getNodeName());
				block.setHead(jcr.getHeadPeerName());
				peer.inviteAsymPartner(jcr.getHeadPeerName());
				if (!jcr.getHeadPeerName().equals(jcr.getPrecedingBlockInfo().getNodeName()))
				{
					peer.inviteAsymPartner(jcr.getPrecedingBlockInfo().getNodeName());
				}
				peer.syncNotifyBySignature(jcr.getPrecedingBlockInfo().getNodeName(), new SucceedingPeerNotification(peer.getPeerName()));
			}
			else
			{
				log.info("No preceding block is available right now!");
				/*
				 * If the below line is executed, it indicates that the current node is the first one in the chain and no other partners are available. 01/27/2022, Bing Li
				 */
				block.setHead(peer.getPeerName());
				block.setPrecedingFingerPrint(BlockConfig.NO_PRECEDING_FINGER_PRINT);
			}
		}
		catch (OwnerCheatingException | CheatingException | PublicKeyUnavailableException e)
		{
			e.printStackTrace();
		}
	}
}
