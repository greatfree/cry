package org.greatfree.cry.framework.bitcoin.coordinator;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.greatfree.cry.exceptions.CryptographyMismatchException;
import org.greatfree.cry.exceptions.PublicKeyUnavailableException;
import org.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import org.greatfree.cry.framework.bitcoin.message.ChainLengthResponse;
import org.greatfree.cry.framework.bitcoin.message.CoinAppID;
import org.greatfree.cry.framework.bitcoin.message.JoinCoinChainRequest;
import org.greatfree.cry.framework.bitcoin.message.JoinCoinChainResponse;
import org.greatfree.cry.framework.bitcoin.message.JoinCoinSystemRequest;
import org.greatfree.cry.framework.bitcoin.message.JoinCoinSystemResponse;
import org.greatfree.cry.framework.bitcoin.message.LeaveCoinSystemRequest;
import org.greatfree.cry.framework.bitcoin.message.LeaveCoinSystemResponse;
import org.greatfree.cry.framework.blockchain.message.ChainAppID;
import org.greatfree.cry.framework.blockchain.message.PrecedingFingerPrintRequest;
import org.greatfree.cry.framework.blockchain.message.PrecedingFingerPrintResponse;
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
class CoordinationTask implements ServerTask
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.framework.bitcoin.collaborator");

	@Override
	public void processNotification(Notification notification)
	{
		switch (notification.getApplicationID())
		{
			case ChainAppID.STOP_COORDINATOR_NOTIFICATION:
				log.info("STOP_COORDINATOR_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					Coordinator.COIN().stop();
				}
				catch (ClassNotFoundException | IOException | InterruptedException | RemoteReadException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException | CryptographyMismatchException | DistributedNodeFailedException | PublicKeyUnavailableException | SymmetricKeyUnavailableException e)
				{
					e.printStackTrace();
				}
				break;

				/*
				 * The below code does not work since the validation is controlled by the networking head. 02/28/2022, Bing Li
				 */
				/*
			case CoinAppID.JOIN_DONE_NOTIFICATION:
				log.info("JOIN_DONE_NOTIFICATION received @" + Calendar.getInstance().getTime());
				JoinDoneNotification jdn = (JoinDoneNotification)notification;
				if (ChainBoss.COIN().joinDone(jdn.getSessionKey()))
				{
					try
					{
						Coordinator.COIN().startValidate(jdn.getSessionKey());
					}
					catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException
							| InvalidAlgorithmParameterException | SignatureException | RemoteReadException | IOException | InterruptedException | CryptographyMismatchException
							| DistributedNodeFailedException | PublicKeyUnavailableException e)
					{
						e.printStackTrace();
					}
				}
				break;
				*/
		}
	}

	@Override
	public ServerMessage processRequest(Request request)
	{
		switch (request.getApplicationID())
		{
			/*
			case CoinAppID.CREATE_BLOCKCHAIN_REQUEST:
				log.info("CREATE_BLOCKCHAIN_REQUEST received @" + Calendar.getInstance().getTime());
				CreateBlockChainRequest cbcr = (CreateBlockChainRequest)request;
				break;
				*/

			case CoinAppID.JOIN_COIN_SYSTEM_REQUEST:
				log.info("JOIN_COIN_SYSTEM_REQUEST received @" + Calendar.getInstance().getTime());
				JoinCoinSystemRequest jcsr = (JoinCoinSystemRequest)request;
				return new JoinCoinSystemResponse(ChainBoss.COIN().getHead(jcsr.getPeerName()), ChainBoss.COIN().joinCoinSystem(jcsr.getPeerName()));
				
			case CoinAppID.LEAVE_COIN_SYSTEM_REQUEST:
				log.info("LEAVE_COIN_SYSTEM_REQUEST received @" + Calendar.getInstance().getTime());
				LeaveCoinSystemRequest lcsr = (LeaveCoinSystemRequest)request;
				ChainBoss.COIN().leaveCoinSystem(lcsr.getPeerName());
				return new LeaveCoinSystemResponse(true);
			
			case ChainAppID.PRECEDING_FINGER_PRINT_REQUEST:
				log.info("PRECEDING_FINGER_PRINT_REQUEST received @" + Calendar.getInstance().getTime());
				PrecedingFingerPrintRequest pfpr = (PrecedingFingerPrintRequest)request;
				return new PrecedingFingerPrintResponse(ChainBoss.COIN().getPrecedingFingerPrint(pfpr.getSessionKey()));

			case CoinAppID.JOIN_COIN_CHAIN_REQUEST:
				log.info("JOIN_COIN_CHAIN_REQUEST received @" + Calendar.getInstance().getTime());
				JoinCoinChainRequest jcr = (JoinCoinChainRequest)request;
				ChainBoss.COIN().joinChain(jcr.getBlockInfo());
				return new JoinCoinChainResponse(ChainBoss.COIN().getSequenceNO(jcr.getBlockInfo().getSessionKey()), ChainBoss.COIN().getChainHead(jcr.getBlockInfo().getSessionKey()), ChainBoss.COIN().getPrecedingBlock(jcr.getBlockInfo().getSessionKey()));
				
			case CoinAppID.CHAIN_LENGTH_REQUEST:
				log.info("CHAIN_LENGTH_REQUEST received @" + Calendar.getInstance().getTime());
				return new ChainLengthResponse(ChainBoss.COIN().getChainLength());
		}
		return null;
	}

}
