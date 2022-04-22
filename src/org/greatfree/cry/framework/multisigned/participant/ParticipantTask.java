package org.greatfree.cry.framework.multisigned.participant;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Calendar;
import java.util.logging.Logger;

import org.greatfree.cry.exceptions.CryptographyMismatchException;
import org.greatfree.cry.framework.multisigned.MSConfig;
import org.greatfree.cry.framework.multisigned.message.ApprovalRequest;
import org.greatfree.cry.framework.multisigned.message.ApprovalResponse;
import org.greatfree.cry.framework.multisigned.message.MSAppID;
import org.greatfree.cry.framework.multisigned.message.PartnerJoinNotification;
import org.greatfree.cry.framework.multisigned.message.PartnerLeaveNotification;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;
import org.greatfree.server.container.ServerTask;
import org.greatfree.util.Rand;

/**
 * 
 * @author libing
 * 
 * 03/24/2022
 *
 */
class ParticipantTask implements ServerTask
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.framework.multisigned.participant");

	@Override
	public void processNotification(Notification notification)
	{
		switch (notification.getApplicationID())
		{
			case MSAppID.PARTNER_JOIN_NOTIFICATION:
				log.info("PARTNER_JOIN_NOTIFICATION received @" + Calendar.getInstance().getTime());
				PartnerJoinNotification pjn = (PartnerJoinNotification)notification;
				try
				{
					Participant.CONS().addPartner(pjn.getPartner());
				}
				catch (InvalidKeyException | NoSuchAlgorithmException | ClassNotFoundException | SignatureException
						| RemoteReadException | IOException | DistributedNodeFailedException
						| CryptographyMismatchException e)
				{
					e.printStackTrace();
				}
				break;
				
			case MSAppID.PARTNER_LEAVE_NOTIFICATION:
				log.info("PARTNER_LEAVE_NOTIFICATION received @" + Calendar.getInstance().getTime());
				PartnerLeaveNotification pln = (PartnerLeaveNotification)notification;
				Participant.CONS().removePartner(pln.getPartner());
				break;
		}
		
	}

	@Override
	public ServerMessage processRequest(Request request)
	{
		switch (request.getApplicationID())
		{
			case MSAppID.APPROVAL_REQUEST:
				log.info("APPROVAL_REQUEST received @" + Calendar.getInstance().getTime());
				ApprovalRequest ar = (ApprovalRequest)request;
				log.info(ar.getDescription());
				if (Rand.getFRandom(MSConfig.MAX_APPROVAL_RATE) > 0.5f)
				{
					return new ApprovalResponse(true);
				}
				else
				{
					return new ApprovalResponse(false);
				}
		}
		return null;
	}

}
