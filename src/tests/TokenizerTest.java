

package tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import tokenizer.Token;
import tokenizer.TokenType;
import tokenizer.Tokenizer;

/**
 * @author Joopyo Hong
 * @version April 5, 2014
 */
public class TokenizerTest {
	Set<String> keywords = new HashSet<String>();
	Tokenizer tokenizer;
	Reader reader;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		keywords.add("abstract");
		keywords.add("boolean");
	}

	/**
	 * Test method for {@link tokenizer.Tokenizer#Tokenizer(Reader, Set)}.
	 *   
	 */
	@Test
	public void testTokenizer()  {
		
		reader = new StringReader("uiuiuyuyuyiuughgjhjfghdjyty6r7^87.5\nfd8sh.74e+9oes");
		try {
			tokenizer = new Tokenizer(reader, null);
			fail("Exception was expected");
		} catch (IllegalArgumentException e) {		
		}
		
		try {
			tokenizer = new Tokenizer(null, keywords);
			fail("Exception was expected");
		} catch (IllegalArgumentException e) {		
		}		
	}

	/**
	 * Test method for {@link tokenizer.Tokenizer#hasNext()}.
	 *   
	 */
	@Test
	public void testHasNext()  {
		reader = new StringReader("Dogs7ate&$my shoes");
		tokenizer = new Tokenizer(reader, keywords);
		
		assertTrue(tokenizer.hasNext());
		tokenizer.next().getValue();
		assertTrue(tokenizer.hasNext());
		tokenizer.next().getValue();
		assertTrue(tokenizer.hasNext());
		tokenizer.next().getValue();
		assertTrue(tokenizer.hasNext());
		tokenizer.next().getValue();
		assertFalse(tokenizer.hasNext());

	}

	/**
	 * Test method for {@link tokenizer.Tokenizer#next()}.
	 *   
	 */
	@Test
	public void testNext()  {
		Token testT;
			
		// test1
		reader = new StringReader("  ");
		tokenizer = new Tokenizer(reader, keywords);
		if (tokenizer.debugging)  System.out.println("Test1:");

						
		try {
			tokenizer.next();
			fail("Exception was expected");
		} catch (RuntimeException e) {
		}
		
		
		// test2
		
		reader = new StringReader("*Dogs+7.8e+abstract-eaabsractte&g* / n9uiuiuyuyuyiuughgjhjfghdjyty6r7^*^ ybb&^jhi\nh978$my 987.5fd8sh.74e+9oes");
		tokenizer = new Tokenizer(reader, keywords);
		if (tokenizer.debugging)  System.out.println("Test2:");
		
		while (tokenizer.hasNext()) {
			testT = tokenizer.next(); 
			if (tokenizer.debugging) System.out.print(testT.getType() + ": ");
			if (tokenizer.debugging) System.out.println(testT.getValue());
		}

		tokenizer.pushBack();
		
		testT = tokenizer.next();
		assertEquals(testT.getType(), TokenType.NAME);
		assertEquals(testT.getValue(), "oes");
		
	    // test3
        reader = new StringReader("*Cats+\n7.8de+abstract-eaabsractte&//n9uiuiuyuyuyiuughgjhjfghdjyty6r7^*^ ybb&^jhih978$my 987.5\nfd8sh.74e+9oes");
        tokenizer = new Tokenizer(reader, keywords);
        if (tokenizer.debugging)  System.out.println("Test3:");

        
        while (tokenizer.hasNext()) {
            testT = tokenizer.next(); 
            if (tokenizer.debugging) System.out.print(testT.getType() + ": ");
            if (tokenizer.debugging) System.out.println(testT.getValue());
        }
	}

	/**
	 * Test method for {@link tokenizer.Tokenizer#pushBack()}.
	 *   
	 */
	@Test
	public void testPushBack()  {
		
		// test1
		reader = new StringReader("");
		tokenizer = new Tokenizer(reader, keywords);		
		if (tokenizer.debugging)  System.out.println("Test1:");
		
		try {
			tokenizer.pushBack();
			fail("Exception was expected");
		} catch (UnsupportedOperationException e) {
		}
		
		
		// test2
		
		Token testT;
		reader = new StringReader("a");
		tokenizer = new Tokenizer(reader, keywords);
		if (tokenizer.debugging)  System.out.println("Test2:");
		
		while (tokenizer.hasNext()) {
			testT = tokenizer.next(); 
			if (tokenizer.debugging) System.out.print(testT.getType() + ": ");
			if (tokenizer.debugging) System.out.println(testT.getValue());
		}

		tokenizer.pushBack();
		
		testT = tokenizer.next();
		assertEquals(testT.getType(), TokenType.NAME);
		assertEquals(testT.getValue(), "a");
		
		tokenizer.pushBack();
		

	}

}
