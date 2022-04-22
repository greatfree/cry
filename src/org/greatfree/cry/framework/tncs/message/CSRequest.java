package org.greatfree.cry.framework.tncs.message;

import org.greatfree.message.container.Request;

/**
 * 
 * @author libing
 * 
 * 01/07/2022, Bing Li
 *
 */
public class CSRequest extends Request
{
	private static final long serialVersionUID = -3381691637241415037L;
	
	private String message;

	public CSRequest(String message)
	{
		super(CSAppID.CS_REQUEST);
		this.message = message;
	}

	public String getMessage()
	{
		return this.message;
	}
}
