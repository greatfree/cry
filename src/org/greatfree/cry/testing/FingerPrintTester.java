package org.greatfree.cry.testing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bouncycastle.util.encoders.Hex;
import org.greatfree.cry.framework.bitcoin.Input;
import org.greatfree.cry.framework.bitcoin.Output;
import org.greatfree.cry.framework.bitcoin.Script;
import org.greatfree.cry.framework.bitcoin.Transaction;
import org.greatfree.util.Tools;

/*
 *
 * 
49
aced0005737200266a6176612e7574696c2e636f6e63757272656e742e436f6e63757272656e74486173684d61706499de129d87293d03000349000b7365676d656e744d61736b49000c7365676d656e7453686966745b00087365676d656e74737400315b4c6a6176612f7574696c2f636f6e63757272656e742f436f6e63757272656e74486173684d6170245365676d656e743b78700000000f0000001c757200315b4c6a6176612e7574696c2e636f6e63757272656e742e436f6e63757272656e74486173684d6170245365676d656e743b52773f41329b39740200007870000000107372002e6a6176612e7574696c2e636f6e63757272656e742e436f6e63757272656e74486173684d6170245365676d656e741f364c905893293d02000146000a6c6f6164466163746f72787200286a6176612e7574696c2e636f6e63757272656e742e6c6f636b732e5265656e7472616e744c6f636b6655a82c2cc86aeb0200014c000473796e6374002f4c6a6176612f7574696c2f636f6e63757272656e742f6c6f636b732f5265656e7472616e744c6f636b2453796e633b7870737200346a6176612e7574696c2e636f6e63757272656e742e6c6f636b732e5265656e7472616e744c6f636b244e6f6e6661697253796e63658832e7537bbf0b0200007872002d6a6176612e7574696c2e636f6e63757272656e742e6c6f636b732e5265656e7472616e744c6f636b2453796e63b81ea294aa445a7c020000787200356a6176612e7574696c2e636f6e63757272656e742e6c6f636b732e416273747261637451756575656453796e6368726f6e697a65726655a843753f52e30200014900057374617465787200366a6176612e7574696c2e636f6e63757272656e742e6c6f636b732e41627374726163744f776e61626c6553796e6368726f6e697a657233dfafb9ad6d6fa90200007870000000003f4000007371007e00057371007e0009000000003f4000007371007e00057371007e0009000000003f4000007371007e00057371007e0009000000003f4000007371007e00057371007e0009000000003f4000007371007e00057371007e0009000000003f4000007371007e00057371007e0009000000003f4000007371007e00057371007e0009000000003f4000007371007e00057371007e0009000000003f4000007371007e00057371007e0009000000003f4000007371007e00057371007e0009000000003f4000007371007e00057371007e0009000000003f4000007371007e00057371007e0009000000003f4000007371007e00057371007e0009000000003f4000007371007e00057371007e0009000000003f4000007371007e00057371007e0009000000003f40000074000130737200136a6176612e7574696c2e41727261794c6973747881d21d99c7619d03000149000473697a6578700000000077040000000078707078
824909357
aced0005737200266a6176612e7574696c2e636f6e63757272656e742e436f6e63757272656e74486173684d61706499de129d87293d03000349000b7365676d656e744d61736b49000c7365676d656e7453686966745b00087365676d656e74737400315b4c6a6176612f7574696c2f636f6e63757272656e742f436f6e63757272656e74486173684d6170245365676d656e743b78700000000f0000001c757200315b4c6a6176612e7574696c2e636f6e63757272656e742e436f6e63757272656e74486173684d6170245365676d656e743b52773f41329b39740200007870000000107372002e6a6176612e7574696c2e636f6e63757272656e742e436f6e63757272656e74486173684d6170245365676d656e741f364c905893293d02000146000a6c6f6164466163746f72787200286a6176612e7574696c2e636f6e63757272656e742e6c6f636b732e5265656e7472616e744c6f636b6655a82c2cc86aeb0200014c000473796e6374002f4c6a6176612f7574696c2f636f6e63757272656e742f6c6f636b732f5265656e7472616e744c6f636b2453796e633b7870737200346a6176612e7574696c2e636f6e63757272656e742e6c6f636b732e5265656e7472616e744c6f636b244e6f6e6661697253796e63658832e7537bbf0b0200007872002d6a6176612e7574696c2e636f6e63757272656e742e6c6f636b732e5265656e7472616e744c6f636b2453796e63b81ea294aa445a7c020000787200356a6176612e7574696c2e636f6e63757272656e742e6c6f636b732e416273747261637451756575656453796e6368726f6e697a65726655a843753f52e30200014900057374617465787200366a6176612e7574696c2e636f6e63757272656e742e6c6f636b732e41627374726163744f776e61626c6553796e6368726f6e697a657233dfafb9ad6d6fa90200007870000000003f4000007371007e00057371007e0009000000003f4000007371007e00057371007e0009000000003f4000007371007e00057371007e0009000000003f4000007371007e00057371007e0009000000003f4000007371007e00057371007e0009000000003f4000007371007e00057371007e0009000000003f4000007371007e00057371007e0009000000003f4000007371007e00057371007e0009000000003f4000007371007e00057371007e0009000000003f4000007371007e00057371007e0009000000003f4000007371007e00057371007e0009000000003f4000007371007e00057371007e0009000000003f4000007371007e00057371007e0009000000003f4000007371007e00057371007e0009000000003f4000007371007e00057371007e0009000000003f40000074000130737200136a6176612e7574696c2e41727261794c6973747881d21d99c7619d03000149000473697a6578700000000077040000000078740001317371007e002d000000017704000000017372002f6f72672e6772656174667265652e6372792e6672616d65776f726b2e626974636f696e2e5472616e73616374696f6e842fe05548fe191a0200034c0005696e70757474002b4c6f72672f6772656174667265652f6372792f6672616d65776f726b2f626974636f696e2f496e7075743b4c00066f757470757474002c4c6f72672f6772656174667265652f6372792f6672616d65776f726b2f626974636f696e2f4f75747075743b4c000474696d657400104c6a6176612f7574696c2f446174653b7870707372002a6f72672e6772656174667265652e6372792e6672616d65776f726b2e626974636f696e2e4f75747075747f5b0e8cb751fc8702000546000f76616c7565496e43757272656e63794c000466726f6d7400124c6a6176612f6c616e672f537472696e673b4c000673637269707474002c4c6f72672f6772656174667265652f6372792f6672616d65776f726b2f626974636f696e2f5363726970743b4c0002746f71007e00374c000e7472616e73616374696f6e4b657971007e0037787041a8000071007e002f7372002a6f72672e6772656174667265652e6372792e6672616d65776f726b2e626974636f696e2e536372697074740908d77880fb460200007872000f6a6176612e7574696c2e537461636b10fe2ac2bb09861d020000787200106a6176612e7574696c2e566563746f72d9977d5b803baf010300034900116361706163697479496e6372656d656e7449000c656c656d656e74436f756e745b000b656c656d656e74446174617400135b4c6a6176612f6c616e672f4f626a6563743b78700000000000000000757200135b4c6a6176612e6c616e672e4f626a6563743b90ce589f1073296c02000078700000000a707070707070707070707871007e002f74002430666665663866632d303961322d346566332d613266362d3163656662663861653762357372000e6a6176612e7574696c2e44617465686a81014b597419030000787077080000017efdf9bdb57878707078
They are NOT identical ... * 
 * 
 */

/**
 * 
 * @author Bing Li
 * 
 * 02/12/2022
 *
 */
class FingerPrintTester
{

	public static void main(String[] args) throws IOException
	{
		Map<String, List<Transaction>> trans = new ConcurrentHashMap<String, List<Transaction>>();
		
		trans.put("0", new ArrayList<Transaction>());
		
//		int nonce = 0;
//		System.out.println(BlockCryptor.calculateFingerPrint("0", Calendar.getInstance().getTimeInMillis(), nonce, trans));
		
		System.out.println(trans.hashCode());
		
		String transKey1 = Hex.toHexString(Tools.serialize(trans));
		System.out.println(transKey1);
		
		trans.put("1", new ArrayList<Transaction>());
//		Input i1 = new Input(Tools.generateUniqueKey(), 1, "1", "1", 33f, new Script());
//		Input i1 = new Input(Tools.generateUniqueKey(), "1", "1", 33f, new Script());
		Input i1 = new Input("1", "1", 33f, new Script());
		List<Input> iList = new ArrayList<Input>();
		iList.add(i1);
		List<Output> oList = new ArrayList<Output>();
//		Output o1 = new Output(Tools.generateUniqueKey(), 2, "1", "1", 21.0f, new Script());
//		Output o1 = new Output(Tools.generateUniqueKey(), "1", "1", 21.0f, new Script());
		Output o1 = new Output("1", "1", 21.0f, new Script());
		oList.add(o1);
//		trans.get("1").add(new Transaction("1", "0", "1", 22f, iList, oList));
//		trans.get("1").add(new Transaction("1", "0", "1", 22f, o1));
//		trans.get("1").add(new Transaction("0", "1", 22f, o1));
		trans.get("1").add(new Transaction(o1));
		
		System.out.println(trans.hashCode());
		
		String transKey2 = Hex.toHexString(Tools.serialize(trans));
		System.out.println(transKey2);
		
		if (transKey1.equals(transKey2))
		{
			System.out.print("They are identical ...");
		}
		else
		{
			System.out.print("They are NOT identical ...");
		}
	}

}
