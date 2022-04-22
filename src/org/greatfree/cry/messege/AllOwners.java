package org.greatfree.cry.messege;

import java.io.Serializable;
import java.util.Collection;

/**
 * 
 * @author libing
 * 
 * 03/26/2022
 *
 */
public class AllOwners implements Serializable
{
	private static final long serialVersionUID = -7441720698073161730L;
	
	private Collection<OwnerInfo> allOwners;
	private boolean isSucceeded;
	
	public AllOwners(Collection<OwnerInfo> allOwners, boolean isSucceeded)
	{
		this.allOwners = allOwners;
		this.isSucceeded = isSucceeded;
	}

	public AllOwners(boolean isSucceeded)
	{
//		this.allOwners = allOwners;
		this.isSucceeded = isSucceeded;
	}

	public Collection<OwnerInfo> getAllOwners()
	{
		return this.allOwners;
	}
	
	public boolean isSucceeded()
	{
		return this.isSucceeded;
	}
}
