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
import org.greatfree.multicast.root.SizeMulticastRequest;

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
final class SizeRootReadActor<Dispatcher extends CryptoCSDispatcher> extends AsyncMulticastor<SizeMulticastRequest, Dispatcher>
{
	private final static Logger log = Logger.getLogger("edu.greatfree.cry.multicast.root");

//	public SizeRootReadActor(RootSyncMulticastor multicastor)
	public SizeRootReadActor(RootSyncMulticastor<Dispatcher> multicastor, int cryptoOption)
	{
		super(multicastor, cryptoOption);
//		super(multicastor);
	}

	@Override
	public void notify(SizeMulticastRequest request)
	{
		try
		{
//			super.getMulticastor().readWithNResponses(request.getRequest(), request.getChildrenSize(), super.getCryptoOption());
			super.getMulticastor().readWithNResponses(request.getRequest(), request.getChildrenSize());
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
	public void perform(SizeMulticastRequest request, int cryptoOption)
	{
	}
	*/

}
