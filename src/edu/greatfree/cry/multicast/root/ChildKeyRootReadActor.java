package edu.greatfree.cry.multicast.root;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.PeerNameIsNullException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.multicast.root.ChildKeyMulticastRequest;

import edu.greatfree.cry.exceptions.CryptographyMismatchException;
import edu.greatfree.cry.exceptions.IPNotExistedException;
import edu.greatfree.cry.exceptions.PublicKeyUnavailableException;
import edu.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import edu.greatfree.cry.server.CryptoCSDispatcher;

/**
 * 
 * @author libing
 * 
 * 04/08/2022
 *
 */
final class ChildKeyRootReadActor<Dispatcher extends CryptoCSDispatcher> extends AsyncMulticastor<ChildKeyMulticastRequest, Dispatcher>
{
	private final static Logger log = Logger.getLogger("edu.greatfree.cry.multicast.root");

//	public ChildKeyRootReadActor(RootSyncMulticastor multicastor)
	public ChildKeyRootReadActor(RootSyncMulticastor<Dispatcher> multicastor, int cryptoOption)
	{
		super(multicastor, cryptoOption);
//		super(multicastor);
	}

	@Override
	public void notify(ChildKeyMulticastRequest request)
	{
		try
		{
//			super.getMulticastor().read(request.getChildKey(), request.getRequest(), super.getCryptoOption());
			super.getMulticastor().read(request.getChildKey(), request.getRequest());
		}
		catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException
				| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException
				| SignatureException | IOException | DistributedNodeFailedException | RemoteReadException
				| CryptographyMismatchException | InterruptedException | SymmetricKeyUnavailableException
				| PublicKeyUnavailableException | RemoteIPNotExistedException e)
		{
			e.printStackTrace();
		}
		catch (PeerNameIsNullException e)
		{
			e.printStackTrace();
			log.info(e.toString());
		}
		catch (IPNotExistedException e)
		{
			log.info(e.toString());
			e.printStackTrace();
		}
	}

	/*
	@Override
	public void perform(ChildKeyMulticastRequest request, int cryptoOption)
	{
	}
	*/

}
