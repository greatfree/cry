package org.greatfree.cry.framework.cluster;

import org.greatfree.util.Tools;

/**
 * 
 * @author libing
 * 
 * 04/26/2022
 *
 */
public final class ClusterConfig
{
	public final static String REGISTRY_IP = "192.168.1.18";
	public final static int REGISTRY_PORT = 8941;
	
	public final static int ROOT_PORT = 8944;
	public final static String ROOT_NAME = "Root";
	public final static String ROOT_KEY = Tools.getHash(ROOT_NAME);

	public final static int CLIENT_PORT = 8900;
}
