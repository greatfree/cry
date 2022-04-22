package org.greatfree.cry.framework.multisigned.message;

import org.greatfree.message.container.Notification;

/**
 * 
 * @author libing
 * 
 * 03/24/2022
 *
 */
public class PartnerJoinNotification extends Notification
{
	private static final long serialVersionUID = 701088796041246042L;
	
	private String partner;

	public PartnerJoinNotification(String partner)
	{
		super(MSAppID.PARTNER_JOIN_NOTIFICATION);
		this.partner = partner;
	}

	public String getPartner()
	{
		return this.partner;
	}
}
