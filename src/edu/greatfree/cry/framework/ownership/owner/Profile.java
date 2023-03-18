package edu.greatfree.cry.framework.ownership.owner;

/**
 * 
 * @author libing
 * 
 * 03/19/2022
 *
 */
final class Profile
{
	private final String owner;
	private final String machineName;
	
	public Profile(String owner, String machineName)
	{
		this.owner = owner;
		this.machineName = machineName;
	}
	
	public String getOwner()
	{
		return this.owner;
	}
	
	public String getMachineName()
	{
		return this.machineName;
	}
}
