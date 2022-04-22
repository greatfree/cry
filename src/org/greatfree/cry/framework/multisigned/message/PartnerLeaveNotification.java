package org.greatfree.cry.framework.multisigned.message;

import org.greatfree.message.container.Notification;

/**
 * 
 * @author libing
 * 
 * 03/24/2022
 *
 */
public class PartnerLeaveNotification extends Notification
{
	private static final long serialVersionUID = 1116799040211634828L;
	
	private String partner;

	public PartnerLeaveNotification(String partner)
	{
		super(MSAppID.PARTNER_LEAVE_NOTIFICATION);
		this.partner = partner;
	}

	public String getPartner()
	{
		return this.partner;
	}
}
