package org.greatfree.cry.framework.multisigned.message;

import org.greatfree.message.ServerMessage;

/**
 * 
 * @author libing
 * 
 * 03/24/2022
 *
 */
public class OperateResponse extends ServerMessage
{
	private static final long serialVersionUID = -9139316061569780102L;
	
	private boolean isDone;

	public OperateResponse(boolean isDone)
	{
		super(MSAppID.OPERATE_RESPONSE);
		this.isDone = isDone;
	}

	public boolean isDone()
	{
		return this.isDone;
	}
}
