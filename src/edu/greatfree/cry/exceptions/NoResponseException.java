package edu.greatfree.cry.exceptions;

/**
 * 
 * @author libing
 * 
 * 05/07/2022
 *
 */
public class NoResponseException extends Exception
{
	private static final long serialVersionUID = 1930415281399064587L;
	
	private String ip;
	private int port;
	
	public NoResponseException(String ip, int port)
	{
		this.ip = ip;
		this.port = port;
	}

	public String getIP()
	{
		return this.ip;
	}
	
	public int getPort()
	{
		return this.port;
	}
}
