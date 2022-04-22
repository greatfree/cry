package org.greatfree.cry.framework.blockchain.peer;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import org.greatfree.cry.exceptions.CryptographyMismatchException;
import org.greatfree.cry.framework.blockchain.message.ChainAppID;
import org.greatfree.cry.framework.blockchain.message.SucceedingBlockResponse;
import org.greatfree.cry.framework.blockchain.message.SucceedingPeerNotification;
import org.greatfree.cry.framework.blockchain.message.SucceedingValidateRequest;
import org.greatfree.cry.framework.blockchain.message.TraverseChainResponse;
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
class ChainNodeTask implements ServerTask
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.framework.blockchain.peer");

	@Override
	public void processNotification(Notification notification)
	{
		switch (notification.getApplicationID())
		{
			case ChainAppID.SUCCEEDING_PEER_NOTIFICATION:
				log.info("SUCCEEDING_PEER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				SucceedingPeerNotification spn = (SucceedingPeerNotification)notification;
				ChainNode.CHAIN().setSucceedingNodeName(spn.getPeerName());
				break;
		}
	}

	@Override
	public ServerMessage processRequest(Request request)
	{
		switch (request.getApplicationID())
		{
			case ChainAppID.TRAVERSE_CHAIN_REQUEST:
				log.info("TRAVERSE_CHAIN_REQUEST received @" + Calendar.getInstance().getTime());
				try
				{
					return new TraverseChainResponse(ChainNode.CHAIN().querySucceedingly());
				}
				catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException
						| IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException
						| ShortBufferException | RemoteReadException | IOException | CryptographyMismatchException | DistributedNodeFailedException e)
				{
					e.printStackTrace();
				}
				
			case ChainAppID.SUCCEEDING_BLOCK_REQUEST:
				log.info("SUCCEEDING_BLOCK_REQUEST received @" + Calendar.getInstance().getTime());
				try
				{
					return new SucceedingBlockResponse(ChainNode.CHAIN().querySucceedingly());
				}
				catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException
						| IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException
						| ShortBufferException | RemoteReadException | IOException | CryptographyMismatchException | DistributedNodeFailedException e)
				{
					e.printStackTrace();
				}
				
			case ChainAppID.VALIDATE_CHAIN_REQUEST:
				log.info("VALIDATE_CHAIN_REQUEST received @" + Calendar.getInstance().getTime());
				try
				{
					return ChainNode.CHAIN().validate();
				}
				catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException
						| IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException
						| ShortBufferException | RemoteReadException | IOException | CryptographyMismatchException | DistributedNodeFailedException e)
				{
					e.printStackTrace();
				}
				
			case ChainAppID.SUCCEEDING_VALIDATE_REQUEST:
				log.info("SUCCEEDING_VALIDATE_REQUEST received @" + Calendar.getInstance().getTime());
				SucceedingValidateRequest svr = (SucceedingValidateRequest)request;
				try
				{
					return ChainNode.CHAIN().validateSucceedingly(svr.getFingerPrint());
				}
				catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException
						| IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException
						| ShortBufferException | IOException | RemoteReadException | CryptographyMismatchException | DistributedNodeFailedException e)
				{
					e.printStackTrace();
				}

				/*
			case ChainAppID.OWNERSHIP_REQUEST:
				log.info("OWNERSHIP_REQUEST received @" + Calendar.getInstance().getTime());
				OwnershipRequest oir = (OwnershipRequest)request;
//				return new OwnershipResponse(MinerMan.COIN().setOwner(oir.getOwner()));
				ChainNode.CHAIN().setOwner(oir.getOwner());
				return new OwnershipResponse(true);
				*/
		}
		return null;
	}

}
