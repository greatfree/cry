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
import org.greatfree.cry.exceptions.JoinChainFailedException;
import org.greatfree.cry.exceptions.PublicKeyUnavailableException;
import org.greatfree.cry.framework.bitcoin.Transaction;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.Tools;

/*
 * 
 * 02/19/2022, Bing Li
 * 
Another difficulty is that it is necessary to find a proper way to keep consistent for the decentralized system.

1) Data propagation.

	When to start?
	
	Where to start?

2) Chain validation.

	When to start?
	
	Where to start?

If consistency cannot be maintained well, the block chain does not work at all.

The coordinator and the networking head are responsible for the management in the current version.
*/

/**
 * 
 * The solution to the problem is that only the networking head is able to start the block chain validation. Then, the head ensures whether the propagation is done before starting the validation. 02/19/2022, Bing Li
 * 
 * One problem to start the block chain validation periodically is that it is possible to result in the transactions are not finished propagating. 02/19/2022, Bing Li 
 * 
 * @author Bing Li
 * 
 * 02/17/2022
 *
 */
class TransactionsMiningTask implements Runnable
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.framework.bitcoin.peer");

	@Override
	public void run()
	{
		/*
			if (!TransactionPool.COIN().isEmpty())
			{
				try
				{
//					CoinNode.COIN().processTransactions();
					String sessionKey = Tools.generateUniqueKey();
					CoinNode.COIN().joinChain(sessionKey);
					CoinNode.COIN().notifyJoinDone(sessionKey);
				}
				catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException
						| InvalidAlgorithmParameterException | ShortBufferException | SignatureException | IOException | RemoteReadException | InterruptedException | DistributedNodeFailedException
						| CryptographyMismatchException | PublicKeyUnavailableException e)
				{
					e.printStackTrace();
				}
			}
			*/
//		log.info("TransactionsMiningTask is started ...");
		try
		{
			if (!TransactionPool.COIN().isEmpty())
			{
				log.info("TransactionPool is NOT empty ...");
				List<Transaction> trans = TransactionPool.COIN().loadTransactions();
				log.info("TransactionPool.COIN().isEmpty() = " + TransactionPool.COIN().isEmpty());
				log.info(trans.size() + " transactions are loaded ...");
				if (CoinNode.COIN().propagateTransactions(trans))
				{
					log.info("Propagation is done ...");
					String sessionKey = Tools.generateUniqueKey();
					int chainLength = CoinNode.COIN().getChainLength();
					log.info("chainLength = " + chainLength);
					/*
					CoinNode.COIN().joinChain(sessionKey, trans);
					if (CoinNode.COIN().joinChainTogether(sessionKey))
					{
						CoinNode.COIN().notifyJoinDone(sessionKey);
					}
					*/
					try
					{
						CoinNode.COIN().rapidJoinChain(sessionKey, chainLength, trans);
					}
					catch (JoinChainFailedException e)
					{
						e.printStackTrace();
					}
				}
			}
			else
			{
//				log.info("TransactionPool is empty ...");
			}
		}
		catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException
				| IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException
				| SignatureException | ShortBufferException | RemoteReadException | IOException
				| InterruptedException | CryptographyMismatchException | DistributedNodeFailedException
				| PublicKeyUnavailableException e)
		{
			e.printStackTrace();
		}
	}

}
