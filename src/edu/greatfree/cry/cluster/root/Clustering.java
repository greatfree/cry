package edu.greatfree.cry.cluster.root;

import java.io.IOException;
import java.util.logging.Logger;

import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.PeerNameIsNullException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.container.p2p.message.PeerAddressRequest;
import org.greatfree.message.PeerAddressResponse;
import org.greatfree.util.TerminateSignal;

/**
 * 
 * @author libing
 * 
 * 04/25/2022
 *
 */
final class Clustering
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.framework.cluster.root");

	public static void addChild(String childID) throws ClassNotFoundException, RemoteReadException, IOException, DistributedNodeFailedException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		PeerAddressResponse response = (PeerAddressResponse)ClusterRoot.CRY().readRegistry(new PeerAddressRequest(childID));
//		ClusterRoot.CRY().addChild(childID, response.getPeerAddress().getIPKey(), response.getPeerAddress().getIP(), response.getPeerAddress().getPort());
		ClusterRoot.CRY().addChild(childID, response.getPeerAddress());
	}

	public static void removeChild(String childID) throws IOException
	{
		ClusterRoot.CRY().removeChild(childID);
		log.info("Clustering-removeChild(): children count = " + ClusterRoot.CRY().getChildrenCount());
		if (ClusterRoot.CRY().getChildrenCount() <= 0)
		{
			log.info("Clustering-removeChild(): notified!");
			TerminateSignal.SIGNAL().notifyTermination();
		}
		else
		{
			log.info("Clustering-removeChild(): NOT notified!");
		}
	}
}
