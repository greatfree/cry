package org.greatfree.cry.framework.multicast.message;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author libing
 * 
 * 04/17/2022
 *
 */
public class HelloWorld implements Serializable
{
	private static final long serialVersionUID = 7779696380256004028L;
	
	private String message;
	private Date createdTime;
	
	public HelloWorld(String msg, Date ct)
	{
		this.message = msg;
		this.createdTime = ct;
	}

	public String getMessage()
	{
		return this.message;
	}
	
	public Date getCreatedTime()
	{
		return this.createdTime;
	}
}
