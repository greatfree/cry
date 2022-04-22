package org.greatfree.cry.framework.tncs.message;

import org.greatfree.message.ServerMessage;

/**
 * 
 * @author libing
 * 
 * 01/07/2022, Bing Li
 *
 */
public class CSResponse extends ServerMessage
{
	private static final long serialVersionUID = -3452814549651058093L;
	
	private String message;

	public CSResponse(String message)
	{
		super(CSAppID.CS_RESPONSE);
		this.message = message;
	}

	public String getMessage()
	{
		return this.message;
	}
}
