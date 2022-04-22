package org.greatfree.cry.framework.multisigned.message;

import org.greatfree.message.container.Request;

/**
 * 
 * @author libing
 * 
 * 03/24/2022
 *
 */
public class ApprovalRequest extends Request
{
	private static final long serialVersionUID = 2598062436333769502L;
	
	private String description;

	public ApprovalRequest(String description)
	{
		super(MSAppID.APPROVAL_REQUEST);
		this.description = description;
	}

	public String getDescription()
	{
		return this.description;
	}
}
