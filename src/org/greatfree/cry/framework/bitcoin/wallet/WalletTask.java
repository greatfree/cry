package org.greatfree.cry.framework.bitcoin.wallet;

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

import org.greatfree.cry.exceptions.CryptographyMismatchException;
import org.greatfree.cry.exceptions.PublicKeyUnavailableException;
import org.greatfree.cry.framework.bitcoin.message.CoinAppID;
import org.greatfree.cry.framework.bitcoin.message.CoinGeneratedNotification;
import org.greatfree.cry.framework.bitcoin.message.VerificationResultNotification;
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
 * 02/06/2022
 *
 */
class WalletTask implements ServerTask
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.framework.bitcoin.client");

	@Override
	public void processNotification(Notification notification)
	{
		switch (notification.getApplicationID())
		{
			case CoinAppID.COIN_GENERATED_NOTIFICATION:
				log.info("COIN_GENERATED_NOTIFICATION received @" + Calendar.getInstance().getTime());
				CoinGeneratedNotification cfn = (CoinGeneratedNotification)notification;
				try
				{
					WalletNode.CHAIN().addCoin(cfn.getCoin());
				}
				catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException
						| InvalidAlgorithmParameterException | SignatureException | RemoteReadException | IOException | InterruptedException | CryptographyMismatchException
						| DistributedNodeFailedException | PublicKeyUnavailableException e)
				{
					e.printStackTrace();
				}
				break;
				
			case CoinAppID.VERIFICATION_RESULT_NOTIFICATION:
				log.info("VERIFICATION_RESULT_NOTIFICATION received @" + Calendar.getInstance().getTime());
				VerificationResultNotification vrn = (VerificationResultNotification)notification;
				/*
				for (String entry : vrn.getTransactions())
				{
					log.info("Transaction-" + entry + ": " + vrn.isValid());
				}
				*/
				log.info("Transaction-" + vrn.getTransactionKey() + " is validated as " + vrn.isValid());
				break;
		}
	}

	@Override
	public ServerMessage processRequest(Request request)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
