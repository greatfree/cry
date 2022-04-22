package org.greatfree.cry.framework.ownership.message;

import org.greatfree.message.ServerMessage;

/**
 * 
 * @author libing
 * 
 * 03/22/2022
 *
 */
public class ReadResponse extends ServerMessage
{
	private static final long serialVersionUID = -6353324236709873822L;
	
	private String response;

	public ReadResponse(String response)
	{
		super(OwnerAppID.READ_RESPONSE);
		this.response = response;
	}

	public String getResponse()
	{
		return this.response;
	}
}
