package edu.greatfree.cry.framework.ownership.machine;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import edu.greatfree.cry.exceptions.CryptographyMismatchException;
import edu.greatfree.cry.exceptions.PublicKeyUnavailableException;
import edu.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import edu.greatfree.cry.exceptions.PeerNotRegisteredException;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.PeerNameIsNullException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;
import org.greatfree.server.container.ServerTask;

import edu.greatfree.cry.framework.ownership.message.OwnerAppID;
import edu.greatfree.cry.framework.ownership.message.ReadRequest;
import edu.greatfree.cry.framework.ownership.message.ReadResponse;
import edu.greatfree.cry.framework.ownership.message.WriteNotification;

/**
 * 
 * @author libing
 * 
 * 03/22/2022
 *
 */
final class MachineTask implements ServerTask
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.framework.ownership.machine");

	@Override
	public void processNotification(Notification notification)
	{
		switch (notification.getApplicationID())
		{
			case OwnerAppID.WRITE_NOTIFICATION:
				log.info("WRITE_NOTIFICATION received @" + Calendar.getInstance().getTime());
				WriteNotification wn = (WriteNotification)notification;
				log.info(wn.getNotification());
				break;
				
			case OwnerAppID.STOP_PEER_NOTIFICATION:
				log.info("STOP_PEER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					Machine.RSC().stop();
				}
				catch (ClassNotFoundException | IOException | InterruptedException | RemoteReadException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException | CryptographyMismatchException | DistributedNodeFailedException | PublicKeyUnavailableException | SymmetricKeyUnavailableException | PeerNotRegisteredException | RemoteIPNotExistedException e)
				{
					e.printStackTrace();
				}
				catch (PeerNameIsNullException e)
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
			case OwnerAppID.READ_REQUEST:
				log.info("READ_REQUEST received @" + Calendar.getInstance().getTime());
				ReadRequest rr = (ReadRequest)request;
				log.info(rr.getRequest());
				return new ReadResponse("What's up?");
		}
		return null;
	}

}
