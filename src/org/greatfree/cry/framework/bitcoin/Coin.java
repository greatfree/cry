package org.greatfree.cry.framework.bitcoin;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author Bing Li
 * 
 * 02/03/2022, Bing Li
 *
 */
public class Coin implements Serializable
{
	private static final long serialVersionUID = 409816868354645894L;

	private final String coinHash;
	private final Date time;
	
	public Coin(String coinHash, Date time)
	{
		this.coinHash = coinHash;
		this.time = time;
	}

	public String getCoinHash()
	{
		return this.coinHash;
	}
	
	public Date getTime()
	{
		return this.time;
	}
}
