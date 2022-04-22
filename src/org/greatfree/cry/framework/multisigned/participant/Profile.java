package org.greatfree.cry.framework.multisigned.participant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import org.greatfree.cry.messege.OwnerInfo;
import org.greatfree.util.Rand;

/**
 * 
 * @author libing
 * 
 * 03/24/2022
 *
 */
final class Profile
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.framework.multisigned.participant");

	private final String owner;
	private final String serverName;
	private List<String> partners;

	public Profile(String owner, String serverName)
	{
		this.owner = owner;
		this.serverName = serverName;
		this.partners = new ArrayList<String>();
	}
	
	public String getOwner()
	{
		return this.owner;
	}
	
	public String getServerName()
	{
		return this.serverName;
	}
	
	public String getRandomPartner()
	{
		return Rand.getRandomListElement(this.partners);
	}
	
	public List<String> getPartners()
	{
		return this.partners;
	}
	
	public void setPartners(Collection<OwnerInfo> owners)
	{
		for (OwnerInfo entry : owners)
		{
			if (!entry.getOwnerName().equals(this.owner))
			{
				log.info("existing owner = " + entry.getOwnerName());
				this.partners.add(entry.getOwnerName());
				
			}
		}
	}
	
	public void addPartner(String partner)
	{
		this.partners.add(partner);
	}
	
	public void removePartner(String partner)
	{
		this.partners.remove(partner);
	}
}
