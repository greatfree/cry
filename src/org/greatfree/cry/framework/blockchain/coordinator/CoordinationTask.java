package org.greatfree.cry.framework.blockchain.coordinator;

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
import org.greatfree.cry.framework.blockchain.message.ChainAppID;
import org.greatfree.cry.framework.blockchain.message.JoinChainRequest;
import org.greatfree.cry.framework.blockchain.message.JoinChainResponse;
import org.greatfree.cry.framework.blockchain.message.PrecedingFingerPrintResponse;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;
import org.greatfree.server.container.ServerTask;

/**
 * 
 * @author libing
 * 
 * 01/26/2022, Bing Li
 *
 */
class CoordinationTask implements ServerTask
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.framework.blockchain.coordinator");

	@Override
	public void processNotification(Notification notification)
	{
		switch (notification.getApplicationID())
		{
			case ChainAppID.STOP_COORDINATOR_NOTIFICATION:
				log.info("STOP_COORDINATOR_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					Coordinator.CHAIN().stop();
				}
				catch (ClassNotFoundException | IOException | InterruptedException | RemoteReadException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException | CryptographyMismatchException | DistributedNodeFailedException | PublicKeyUnavailableException | SymmetricKeyUnavailableException e)
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
			case ChainAppID.PRECEDING_FINGER_PRINT_REQUEST:
				log.info("PRECEDING_FINGER_PRINT_REQUEST received @" + Calendar.getInstance().getTime());
				return new PrecedingFingerPrintResponse(Chain.BC().getPrecedingFingerPrint());

			case ChainAppID.JOIN_CHAIN_REQUEST:
				log.info("JOIN_CHAIN_REQUEST received @" + Calendar.getInstance().getTime());
				JoinChainRequest jcr = (JoinChainRequest)request;
				Chain.BC().joinChain(jcr.getBlockInfo());
//				return new JoinChainResponse(Chain.BC().getSequenceNO(), Chain.BC().getHeadPeerName(), Chain.BC().joinChain(jcr.getBlockInfo()));
				return new JoinChainResponse(Chain.BC().getSequenceNO(), Chain.BC().getHeadPeerName(), Chain.BC().getPrecedingChain());
		}
		return null;
	}
}
