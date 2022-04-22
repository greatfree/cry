package org.greatfree.cry.framework.multisigned.server;

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
import org.greatfree.cry.framework.multisigned.message.MSAppID;
import org.greatfree.cry.framework.multisigned.message.OperateNotification;
import org.greatfree.cry.framework.multisigned.message.OperateRequest;
import org.greatfree.cry.framework.multisigned.message.OperateResponse;
import org.greatfree.cry.messege.CryAppID;
import org.greatfree.cry.messege.OwnerJoinNotification;
import org.greatfree.cry.messege.OwnerLeaveNotification;
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
 * 03/24/2022
 *
 */
class Services implements ServerTask
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.framework.multisigned.server");

	@Override
	public void processNotification(Notification notification)
	{
		switch (notification.getApplicationID())
		{
			case CryAppID.OWNER_JOIN_NOTIFICATION:
				log.info("OWNER_JOIN_NOTIFICATION received @" + Calendar.getInstance().getTime());
				OwnerJoinNotification ojn = (OwnerJoinNotification)notification;
				try
				{
					log.info("joined owner = " + ojn.getOwnerName());
					Server.RSC().notifyOwnerJoin(ojn.getOwnerName());
				}
				catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException
						| IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException
						| RemoteReadException | IOException | InterruptedException | DistributedNodeFailedException
						| CryptographyMismatchException | PublicKeyUnavailableException e)
				{
					e.printStackTrace();
				}
				break;
				
			case CryAppID.OWNER_LEAVE_NOTIFICATION:
				log.info("OWNER_LEAVE_NOTIFICATION received @" + Calendar.getInstance().getTime());
				OwnerLeaveNotification oln = (OwnerLeaveNotification)notification;
				try
				{
					Server.RSC().notifyOwnerLeave(oln.getOwnerName());
				}
				catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException
						| IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException
						| RemoteReadException | IOException | InterruptedException | CryptographyMismatchException
						| DistributedNodeFailedException | PublicKeyUnavailableException e)
				{
					e.printStackTrace();
				}
				break;
				
			case MSAppID.OPERATE_NOTIFICATION:
				log.info("OPERATE_NOTIFICATION received @" + Calendar.getInstance().getTime());
				OperateNotification on = (OperateNotification)notification;
				log.info(on.getOperation() + ": " + on.getDescription());
				break;
				
			case MSAppID.STOP_SERVER_NOTIFICATION:
				log.info("STOP_SERVER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					Server.RSC().stop();
				}
				catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException
						| IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException | IOException
						| InterruptedException | RemoteReadException | CryptographyMismatchException
						| DistributedNodeFailedException | PublicKeyUnavailableException
						| SymmetricKeyUnavailableException e)
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
			case MSAppID.OPERATE_REQUEST:
				log.info("OPERATE_REQUEST received @" + Calendar.getInstance().getTime());
				OperateRequest or = (OperateRequest)request;
				log.info(or.getOperation() + ": " + or.getDescription());
				return new OperateResponse(true);
		}
		return new OperateResponse(false);
	}

}
