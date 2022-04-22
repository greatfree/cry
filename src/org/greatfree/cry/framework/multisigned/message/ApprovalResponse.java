package org.greatfree.cry.framework.multisigned.message;

import org.greatfree.message.ServerMessage;

/**
 * 
 * @author libing
 * 
 * 03/24/2022
 *
 */
public class ApprovalResponse extends ServerMessage
{
	private static final long serialVersionUID = -498062191957477771L;
	
	private boolean isApproved;

	public ApprovalResponse(boolean isApproved)
	{
		super(MSAppID.APPROVAL_RESPONSE);
		this.isApproved = isApproved;
	}

	public boolean isApproved()
	{
		return this.isApproved;
	}
}
