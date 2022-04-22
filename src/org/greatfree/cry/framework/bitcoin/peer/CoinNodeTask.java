package org.greatfree.cry.framework.bitcoin.peer;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Calendar;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import org.greatfree.concurrency.Scheduler;
import org.greatfree.cry.exceptions.CryptographyMismatchException;
import org.greatfree.cry.exceptions.JoinChainFailedException;
import org.greatfree.cry.exceptions.PublicKeyUnavailableException;
import org.greatfree.cry.framework.bitcoin.CoinConfig;
import org.greatfree.cry.framework.bitcoin.message.AddCoinNotification;
import org.greatfree.cry.framework.bitcoin.message.CheckBalanceResponse;
import org.greatfree.cry.framework.bitcoin.message.CoinAppID;
import org.greatfree.cry.framework.bitcoin.message.FinalizeTransactionMiningNotification;
import org.greatfree.cry.framework.bitcoin.message.GoAheadValidationNotification;
import org.greatfree.cry.framework.bitcoin.message.JoinChainNotification;
import org.greatfree.cry.framework.bitcoin.message.JoinStateNotification;
import org.greatfree.cry.framework.bitcoin.message.LeaveNotification;
import org.greatfree.cry.framework.bitcoin.message.LinkPrecedingNotification;
import org.greatfree.cry.framework.bitcoin.message.PropagateSucceedinglyRequest;
import org.greatfree.cry.framework.bitcoin.message.PropagateSucceedinglyResponse;
import org.greatfree.cry.framework.bitcoin.message.RetainTransactionNotification;
import org.greatfree.cry.framework.bitcoin.message.StartCoinMiningResponse;
import org.greatfree.cry.framework.bitcoin.message.ValidationResultNotification;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;
import org.greatfree.server.container.ServerTask;

/**
 * 
 * @author Bing Li
 * 
 * 02/14/2022
 *
 */
class CoinNodeTask implements ServerTask
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.framework.bitcoin.peer");

	@Override
	public void processNotification(Notification notification)
	{
		switch (notification.getApplicationID())
		{
			/*
			case CoinAppID.HEAD_NOTIFICATION:
				log.info("HEAD_NOTIFICATION received @" + Calendar.getInstance().getTime());
				CoinNode.COIN().setHead(true);
				break;
				*/

			case CoinAppID.STOP_COIN_MINING_NOTIFICATION:
				log.info("STOP_COIN_MINING_NOTIFICATION received @" + Calendar.getInstance().getTime());
				MiningTaskManager.COIN().stopCoinMiningTask();
				break;
				
			case CoinAppID.LINK_PRECEDING_NOTIFICATION:
				log.info("LINK_PRECEDING_NOTIFICATION received @" + Calendar.getInstance().getTime());
				LinkPrecedingNotification lpn = (LinkPrecedingNotification)notification;
				try
				{
					CoinNode.COIN().setSucceedingPeer(lpn.getPeerName());
				}
				catch (InvalidKeyException | NoSuchAlgorithmException | ClassNotFoundException | SignatureException | RemoteReadException | IOException | DistributedNodeFailedException
						| CryptographyMismatchException e)
				{
					e.printStackTrace();
				}
				break;
				
			case CoinAppID.LEAVE_NOTIFICATION:
				log.info("LEAVE_NOTIFICATION received @" + Calendar.getInstance().getTime());
				LeaveNotification ln = (LeaveNotification)notification;
				try
				{
					/*
					if (!ln.getNewSucceedingPN().equals(CoinConfig.NO_PEER))
					{
						CoinNode.COIN().setSucceedingPeer(ln.getNewSucceedingPN());
					}
					*/
					CoinNode.COIN().setSucceedingPeer(ln.getNewSucceedingPN());
				}
				catch (InvalidKeyException | NoSuchAlgorithmException | ClassNotFoundException | SignatureException | RemoteReadException | IOException | DistributedNodeFailedException
						| CryptographyMismatchException e)
				{
					e.printStackTrace();
				}
				break;

				/*
			case CoinAppID.NO_PRECEDING_NOTIFICATION:
				log.info("NO_PRECEDING_NOTIFICATION received @" + Calendar.getInstance().getTime());
				CoinNode.COIN().setAsHead();
				break;
				*/
				
				/*
				 * Since the ATM is not properly designed, to avoid the waiting of the client, the request/response is to change to a notification. But that is a temporary solution. 02/15/2022, Bing Li
				 * The current solution results in the long term waiting at the client side since no independent threads are created to separate the block chain creation from the interaction between the client and the server. 02/15/2022, Bing Li
				 * 
				 * If the ATM is improved, a more proper solution here is to raise a thread to create a block chain. Now no dedicated thread is created for the block chain creation. 02/15/2022, Bing Li
				 */
			case CoinAppID.ADD_COIN_NOTIFICATION:
				log.info("ADD_COIN_NOTIFICATION received @" + Calendar.getInstance().getTime());
				AddCoinNotification acn = (AddCoinNotification)notification;
				WalletStorage.COIN().add(acn.getCoin());
				try
				{
//					CoinNode.COIN().forwardTransaction(acn.getTransaction());
//					CoinNode.COIN().putIntoPool(acn.getTransaction());
//					TransactionPool.COIN().enqueue(acn.getTransaction());
					CoinNode.COIN().forwardToHeadIfApplicable(acn.getTransaction());
				}
				catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException
						| InvalidAlgorithmParameterException | SignatureException | RemoteReadException | IOException | InterruptedException | CryptographyMismatchException
						| DistributedNodeFailedException | PublicKeyUnavailableException e)
				{
					e.printStackTrace();
				}
				break;

			case CoinAppID.RETAIN_TRANSACTION_NOTIFICATION:
				log.info("RETAIN_TRANSACTION_NOTIFICATION received @" + Calendar.getInstance().getTime());
				RetainTransactionNotification ftn = (RetainTransactionNotification)notification;
				/*
				try
				{
//					CoinNode.COIN().putIntoPool(ftn.getTransaction());
					CoinNode.COIN().forwardTransaction(ftn.getTransaction());
//					TransactionPool.COIN().enqueue(ftn.getTransaction());
				}
				catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException
						| InvalidAlgorithmParameterException | SignatureException | RemoteReadException | IOException | InterruptedException | CryptographyMismatchException
						| DistributedNodeFailedException | PublicKeyUnavailableException e)
				{
					e.printStackTrace();
				}
				*/
				CoinNode.COIN().retainTransaction(ftn.getTransaction());
				break;
				
				/*
				 * Since the ATM is not properly designed, to avoid the waiting of the client, the request/response is to change to a notification. But that is a temporary solution. 02/15/2022, Bing Li
				 * The current solution results in the long term waiting at the client side since no independent threads are created to separate the block chain creation from the interaction between the client and the server. 02/15/2022, Bing Li
				 * 
				 * If the ATM is improved, a more proper solution here is to raise a thread to create a block chain. Now no dedicated thread is created for the block chain creation. 02/15/2022, Bing Li
				 */
				/*
			case CoinAppID.EARN_COIN_TRANSACTION_NOTIFICATION:
				log.info("EARN_COIN_TRANSACTION_NOTIFICATION received @" + Calendar.getInstance().getTime());
				EarnCoinTransactionNotification ectn = (EarnCoinTransactionNotification)notification;
				Date timeStamp = TransactionPool.COIN().enqueue(ectn.getTransaction());
				if (timeStamp != UtilConfig.NO_TIME)
				{
					try
					{
						CoinNode.COIN().joinChain(CoinNode.COIN().createChainSession(timeStamp), timeStamp);
					}
					catch (ClassNotFoundException | RemoteReadException | IOException | DistributedNodeFailedException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException | ShortBufferException | SignatureException | InterruptedException | CryptographyMismatchException e)
					{
						e.printStackTrace();
					}
				}
				break;
				*/
				
			case CoinAppID.JOIN_CHAIN_NOTIFICATION:
				log.info("JOIN_CHAIN_NOTIFICATION received @" + Calendar.getInstance().getTime());
				JoinChainNotification jcn = (JoinChainNotification)notification;
				try
				{
					CoinNode.COIN().rapidJoinChain(jcn.getSessionKey());
				}
				catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException
						| IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException
						| RemoteReadException | IOException | CryptographyMismatchException | DistributedNodeFailedException
						| PublicKeyUnavailableException e)
				{
					e.printStackTrace();
				}
				break;
				
			case CoinAppID.JOIN_STATE_NOTIFICATION:
				log.info("JOIN_STATE_NOTIFICATION received @" + Calendar.getInstance().getTime());
				JoinStateNotification jsn = (JoinStateNotification)notification;
				try
				{
					CoinNode.COIN().collectJoinState(jsn.getPeerName(), jsn.getSessionKey(), jsn.isDone());
				}
				catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException
						| IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException
						| SignatureException | JoinChainFailedException | RemoteReadException | IOException
						| CryptographyMismatchException | DistributedNodeFailedException | PublicKeyUnavailableException
						| InterruptedException e)
				{
					e.printStackTrace();
				}
				break;
				
				/*
				 * The below code does not work since the validation is controlled by the networking head. 02/28/2022, Bing Li
				 */
				/*
			case CoinAppID.START_VALIDATE_NOTIFICATION:
				log.info("START_VALIDATE_NOTIFICATION received @" + Calendar.getInstance().getTime());
				StartValidateNotification svn = (StartValidateNotification)notification;
				// It is recommended to create a thread (the ATM) to perform the validation. 02/16/2022, Bing Li
				try
				{
					// If the chain length is short, the distributed recursive validation is invoked, which is a synchronous and slow solution. 02/26/2022, Bing Li
					ValidateChainResponse vcr = CoinNode.COIN().validate(svn.getSessionKey());
					CoinNode.COIN().notifyValidationStates(svn.getSessionKey(), vcr.isValid());
				}
				catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException
						| InvalidAlgorithmParameterException | ShortBufferException | RemoteReadException | IOException | CryptographyMismatchException | DistributedNodeFailedException | SignatureException | InterruptedException | PublicKeyUnavailableException e)
				{
					e.printStackTrace();
				}
				break;
				*/
				
			case CoinAppID.GO_AHEAD_VALIDATION_NOTIFICATION:
				log.info("GO_AHEAD_VALIDATION_NOTIFICATION received @" + Calendar.getInstance().getTime());
				GoAheadValidationNotification gavn = (GoAheadValidationNotification)notification; 
				try
				{
					CoinNode.COIN().rapidValidate(gavn.getSessionKey());
				}
				catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException
						| IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException
						| SignatureException | RemoteReadException | IOException | DistributedNodeFailedException
						| InterruptedException | CryptographyMismatchException | PublicKeyUnavailableException e)
				{
					e.printStackTrace();
				}
				break;
				
			case CoinAppID.VALIDATION_RESULT_NOTIFICATION:
				log.info("VALIDATION_RESULT_NOTIFICATION received @" + Calendar.getInstance().getTime());
				ValidationResultNotification vrn = (ValidationResultNotification)notification;
				try
				{
					CoinNode.COIN().collectValidationResult(vrn.getSessionKey(), vrn.isValid());
				}
				catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException
						| IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException
						| SignatureException | RemoteReadException | IOException | InterruptedException
						| CryptographyMismatchException | DistributedNodeFailedException | PublicKeyUnavailableException e)
				{
					e.printStackTrace();
				}
				break;
				
			case CoinAppID.FINALIZE_TRANSACTION_MINING_NOTIFICATION:
				log.info("FINALIZE_TRANSACTION_MINING_NOTIFICATION received @" + Calendar.getInstance().getTime());
				FinalizeTransactionMiningNotification ftmn = (FinalizeTransactionMiningNotification)notification;
				try
				{
					CoinNode.COIN().finalizeTransactionMining(ftmn.getSessionKey());
				}
				catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException
						| IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException
						| RemoteReadException | IOException | CryptographyMismatchException | DistributedNodeFailedException
						| PublicKeyUnavailableException e)
				{
					e.printStackTrace();
				}
				break;
				
		}
	}

	@Override
	public ServerMessage processRequest(Request request)
	{
		switch (request.getApplicationID())
		{
			/*
			 * It is necessary to employ the ATM to do that. Before the next step, the ATM improvement is critical. 02/19/2022, Bing Li
			 */
			case CoinAppID.START_COIN_MINING_REQUEST:
				log.info("START_COIN_MINING_REQUEST received @" + Calendar.getInstance().getTime());
				if (!MiningTaskManager.COIN().isCoinMiningStarted())
				{
					MiningTaskManager.COIN().setCoinMiningTask(Scheduler.GREATFREE().submit(new CoinMiningTask(), CoinConfig.COIN_MINING_DELAY, CoinConfig.COIN_MINING_PERIOD));
				}
				return new StartCoinMiningResponse(true);
				
			case CoinAppID.CHECK_BALANCE_REQUEST:
				log.info("CHECK_BALANCE_REQUEST received @" + Calendar.getInstance().getTime());
				return new CheckBalanceResponse(2, 3.5f, true);
				
			case CoinAppID.PROPAGATE_SUCCEEDINGLY_REQUEST:
				log.info("=============== PROPAGATE_SUCCEEDINGLY_REQUEST =============== received @" + Calendar.getInstance().getTime());
				PropagateSucceedinglyRequest psr = (PropagateSucceedinglyRequest)request;
				try
				{
					return new PropagateSucceedinglyResponse(CoinNode.COIN().propagateSucceedingly(psr.getTransactions()));
				}
				catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException
						| IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException
						| ShortBufferException | RemoteReadException | IOException | CryptographyMismatchException
						| DistributedNodeFailedException | PublicKeyUnavailableException e)
				{
					return new PropagateSucceedinglyResponse(false);
				}

				/*
				 * Since the ATM is not properly designed, to avoid the waiting of the client, the request/response is to change to a notification. But that is a temporary solution. 02/15/2022, Bing Li
				 * The current solution results in the long term waiting at the client side since no independent threads are created to separate the block chain creation from the interaction between the client and the server. 02/15/2022, Bing Li
				 * 
				 * If the ATM is improved, a more proper solution here is to raise a thread to create a block chain. Now no dedicated thread is created for the block chain creation. 02/15/2022, Bing Li
				 */
				/*
			case CoinAppID.EARN_COIN_TRANSACTION_NOTIFICATION:
				log.info("EARN_COIN_TRANSACTION_REQUEST received @" + Calendar.getInstance().getTime());
				EarnCoinTransactionNotification ectr = (EarnCoinTransactionNotification)request;
				if (TransactionPool.COIN().enqueue(ectr.getTransaction()))
				{
				}
				else
				{
					return new EarnCoinTransactionResponse(false);
				}
				*/
		}
		return null;
	}

}
