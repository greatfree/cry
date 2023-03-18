package edu.greatfree.cry.framework.ownership.message;

import org.greatfree.message.container.Request;

/**
 * 
 * @author libing
 * 
 * 03/22/2022
 *
 */
public class ReadRequest extends Request
{
	private static final long serialVersionUID = -4835781481012094212L;
	
	private String request;

	public ReadRequest(String request)
	{
		super(OwnerAppID.READ_REQUEST);
		this.request = request;
	}

	public String getRequest()
	{
		return this.request;
	}
}
