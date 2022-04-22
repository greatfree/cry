package org.greatfree.cry.framework.multisigned.message;

import org.greatfree.message.container.Request;

/**
 * 
 * @author libing
 * 
 * 03/24/2022
 *
 */
public class OperateRequest extends Request
{
	private static final long serialVersionUID = 3076908474208016843L;
	
	private String operation;
	private String description;

	public OperateRequest(String operation, String description)
	{
		super(MSAppID.OPERATE_REQUEST);
		this.operation = operation;
		this.description = description;
	}

	public String getOperation()
	{
		return this.operation;
	}
	
	public String getDescription()
	{
		return this.description;
	}
}
