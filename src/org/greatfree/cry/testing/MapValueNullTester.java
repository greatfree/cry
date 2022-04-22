package org.greatfree.cry.testing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.greatfree.cry.framework.bitcoin.Output;

/**
 * 
 * @author Bing Li
 * 
 * 02/17/2022
 *
 */
class MapValueNullTester
{

	public static void main(String[] args)
	{
		Map<String, List<Output>> outputs = new ConcurrentHashMap<String, List<Output>>();
		List<Output> os = new ArrayList<Output>();
//		outputs.put("1", null);
		outputs.put("1", os);
	}

}
