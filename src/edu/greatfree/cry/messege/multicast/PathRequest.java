package edu.greatfree.cry.messege.multicast;

import org.greatfree.cluster.message.ClusterMessageType;

/**
 * 
 * @author libing
 * 
 * 05/11/2022
 *
 */
public class PathRequest extends ChildRootRequest
{
	private static final long serialVersionUID = 7172962329689295088L;
	private String relativePath;

	public PathRequest(String relativePath)
	{
		super(ClusterMessageType.PATH_REQUEST);
		this.relativePath = relativePath;
	}

	public String getRelativePath()
	{
		return this.relativePath;
	}
}
