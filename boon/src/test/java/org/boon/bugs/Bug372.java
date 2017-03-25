package org.boon.bugs;

import static junit.framework.Assert.fail;

import java.util.Map;

import org.boon.json.JsonException;
import org.boon.json.implementation.JsonFastParser;
import org.junit.Test;

/**
 * Created by michele vivoda on 26/3/2017
 */
public class Bug372 {


	@Test
	public void testJsonNumberError_DotZero()
	{
		// http://deron.meranda.us/python/comparing_json_modules/numbers#t5-3
		assertInvalidNumber(".0");

	}
	@Test
	public void testJsonNumberError_ZeroDot()
	{
		assertInvalidNumber("0.");

	}
	@Test
	public void testJsonNumberError_PlusZero()
	{
		assertInvalidNumber("+0");

	}
	@Test
	public void testJsonNumberError_034()
	{
		assertInvalidNumber("034");
		assertInvalidNumber("-034");

	}
	@Test
	public void testJsonNumberError_Minus()
	{
		// a new one, for line 834 CharScanner
//		if (negative) {
//            num = (digitChars[ offset ] - '0');
        // gives -77 because 77 is '{' - '0'    
		
		assertInvalidNumber("-");

	}

	private void assertInvalidNumber(String numberLexical)
	{
		try
		{
			final String json = "{\"a\": " + numberLexical + "}";
			Map o = (Map) new JsonFastParser().parse(json);
			Object jn = (Object) o.get("a");
			if (jn==null) fail("did not fail but return null");
			else fail("Did not fail, returned " + jn + " - " + jn.getClass().getName());
		}
		catch(JsonException e)
		{
			// ok 
		}

	}

}
