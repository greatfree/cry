package org.greatfree.cry.framework.bitcoin.peer;

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

import org.greatfree.cry.exceptions.CryptographyMismatchException;
import org.greatfree.cry.exceptions.PublicKeyUnavailableException;
import org.greatfree.cry.framework.bitcoin.CoinBlock;
import org.greatfree.cry.framework.bitcoin.Transaction;
import org.greatfree.cry.framework.bitcoin.message.GoAheadValidationNotification;
import org.greatfree.cry.framework.bitcoin.message.ValidationResultNotification;
import org.greatfree.cry.framework.bitcoin.message.VerificationResultNotification;
import org.greatfree.cry.framework.blockchain.message.SucceedingValidateRequest;
import org.greatfree.cry.framework.blockchain.message.SucceedingValidateResponse;
import org.greatfree.cry.framework.blockchain.message.ValidateChainRequest;
import org.greatfree.cry.framework.blockchain.message.ValidateChainResponse;
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
class Validator
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.framework.bitcoin.peer");

	/*
	 * Only if the method is located at the networking head, it can be invoked. Otherwise, it is not invoked. 02/26/2022, Bing Li
	 * 
	 * The validation is performed in an asynchronous way. That is fast. When the chain is long, it is preferred to invoke the method. 02/26/2022, Bing Li
	 */
//	public static void rapidValidate(Peer peer, CoinBlock block, String sessionKey, int chainLength) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, RemoteReadException, IOException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException, SignatureException, InterruptedException
	public static void rapidValidate(Peer peer, CoinBlock block, String sessionKey) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, RemoteReadException, IOException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException, SignatureException, InterruptedException
	{
//		ChainHead.COIN().setChainLength(sessionKey, this.getChainLength());
//		ChainHead.COIN().setChainLength(sessionKey, chainLength);
//		CoinBlock block = this.blocks.get(sessionKey);
		if (block.isSucceedingExisted())
		{
			/*
			 * The asynchronous notification aims to keep the high performance of the validation. 02/26/2022, Bing Li
			 */
			peer.asyncNotifyAsymmetrically(block.getSucceedingPeerName(), new GoAheadValidationNotification(sessionKey));
		}

		log.info("1) Validating block ...");
		
		if (!block.validate(block.getPrecedingFingerPrint(), block.getInputs(), block.getOutputs()))
		{
			notifyValidationStates(peer, block, sessionKey, false);
		}

		log.info("2) Validating block ...");
		
		if (!block.isSucceedingExisted())
		{
			ChainHead.COIN().incrementValidation(sessionKey);
			if (ChainHead.COIN().isValidationDone(sessionKey))
			{
				notifyValidationStates(peer, block, sessionKey, true);
			}
		}
	}

	/*
	 * If the node is not the head, when validating, the method is invoked. 02/26/2022, Bing Li
	 */
	public static void rapidValidate(Peer peer, CoinBlock block, String head, String sessionKey) throws ClassNotFoundException, RemoteReadException, IOException, DistributedNodeFailedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, SignatureException, InterruptedException, CryptographyMismatchException, PublicKeyUnavailableException
	{
//		CoinBlock block = this.blocks.get(sessionKey);
		if (block.isSucceedingExisted())
		{
			/*
			 * The asynchronous notification aims to keep the high performance of the validation. 02/26/2022, Bing Li
			 */
			peer.asyncNotifyAsymmetrically(block.getSucceedingPeerName(), new GoAheadValidationNotification(sessionKey));
		}
		
		if (!block.validate(block.getPrecedingFingerPrint(), block.getInputs(), block.getOutputs()))
		{
			peer.syncNotifyAsymmetrically(head, new ValidationResultNotification(sessionKey, false));
			return;
		}

		if (!block.isSucceedingExisted())
		{
			peer.syncNotifyAsymmetrically(head, new ValidationResultNotification(sessionKey, true));
		}
	}
	
	/*
	 * The method is invoked only if the node is the networking head. 02/26/2022, Bing Li
	 */
	public static void collectValidationResult(Peer peer, CoinBlock block, String sessionKey, boolean isValid) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, SignatureException, RemoteReadException, IOException, InterruptedException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException
	{
		if (isValid)
		{
			ChainHead.COIN().incrementValidation(sessionKey);
			if (ChainHead.COIN().isValidationDone(sessionKey))
			{
				notifyValidationStates(peer, block, sessionKey, true);
			}
		}
		else
		{
			notifyValidationStates(peer, block, sessionKey, false);
		}
	}

	/*
	 * The validation is performed in a synchronous way. That is slow. Only when the chain is short, it is reasonable. 02/26/2022, Bing Li
	 */
	public static ValidateChainResponse validate(Peer peer, CoinBlock block, String sessionKey) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, RemoteReadException, IOException, CryptographyMismatchException, DistributedNodeFailedException
	{
//		CoinBlock block = this.blocks.get(sessionKey);
		/*
		 * The condition is not necessary actually since the coordinator sends to the head of the block chain only when starting the validation. 02/26/2022, Bing Li 
		 */
		if (!block.isPrecedingExisted())
		{
			log.info("The current peer is the head of the block chain being validated ...");
			/*
			 * If the condition is fulfilled, it denotes that the current peer is the head. 01/27/2022, Bing Li
			 */
			SucceedingValidateResponse response = validateSucceedingly(peer, block, sessionKey, block.getFingerPrint());
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
				return (ValidateChainResponse)peer.readAsymmetrically(block.getHead(), new ValidateChainRequest());
			}
			catch (PublicKeyUnavailableException e)
			{
				return null;
			}
		}
	}

	public static SucceedingValidateResponse validateSucceedingly(Peer peer, CoinBlock block, String sessionKey, String precedingFingerPrint) throws IOException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, RemoteReadException, CryptographyMismatchException, DistributedNodeFailedException
	{
//		CoinBlock block = this.blocks.get(sessionKey);
		if (!block.validate(precedingFingerPrint, block.getInputs(), block.getOutputs()))
		{
			return new SucceedingValidateResponse(block.getSequenceNO(), false);
		}

		log.info("The current block is validated correctly ...");
		
		if (!block.isSucceedingExisted())
		{
			return new SucceedingValidateResponse(block.getSequenceNO(), true);
		}

		log.info("The succeeding ones of the current block is being validated ...");

		try
		{
			return (SucceedingValidateResponse)peer.readAsymmetrically(block.getSucceedingPeerName(), new SucceedingValidateRequest(block.getFingerPrint()));
		}
		catch (PublicKeyUnavailableException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * Only if the method is located at the networking head, it can be invoked. Otherwise, it is not invoked. 02/26/2022, Bing Li
	 */
	public static void notifyValidationStates(Peer peer, CoinBlock block, String sessionKey, boolean isValid) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, SignatureException, RemoteReadException, IOException, InterruptedException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException
	{
		log.info("Validation results are being sent to the original transactions creators ...");
//		Map<String, List<String>> ownerTrans = this.blocks.get(sessionKey).getOwners();
//		List<Transaction> trans = this.blocks.get(sessionKey).getTransactions();
		List<Transaction> trans = block.getTransactions();
		/*
		for (Map.Entry<String, List<String>> entry : ownerTrans.entrySet())
		{
			log.info("Transaction creator: " + entry.getKey());
			this.peer.syncNotifyBySignature(entry.getKey(), new VerificationResultNotification(entry.getValue(), isValid));
		}
		*/
		CoinNode.COIN().finalizeTransactionMining(sessionKey);
		for (Transaction entry : trans)
		{
			if (entry.isOutput())
			{
				peer.syncNotifyBySignature(entry.getOutputFrom(), new VerificationResultNotification(entry.getKey(), isValid));
			}
			else
			{
				peer.syncNotifyBySignature(entry.getInputTo(), new VerificationResultNotification(entry.getKey(), isValid));
			}
		}
		System.out.println("============= One round transactions mining is DONE! =============");
		log.info("Is TransactionPool is empty: " + TransactionPool.COIN().isEmpty());
	}
}
