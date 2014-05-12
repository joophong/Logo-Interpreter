package tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import tokenizer.Token;
import tokenizer.TokenType;

/**
 * @author Joopyo Hong
 * @version April 5, 2014
 */
public class TokenTest {
	Token toke1, toke2, toke3, toke4, toke5;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		toke1 = new Token(TokenType.NUMBER, "123");
		toke2 = new Token(TokenType.NUMBER, "321");
		toke3 = new Token(TokenType.NUMBER, "123");
		toke4 = new Token(TokenType.NUMBER, " 123");
		toke5 = new Token(TokenType.NAME, "123");
	}

	/**
	 * Test method for {@link tokenizer.Token#Token(tokenizer.TokenType, java.lang.String)}.
	 */
	@Test
	public void testToken() {
//		System.out.println("[a-zA-Z_$]".contains("b"));
	    try {
	        new Token(null, null);
	        fail("Exception was expected for null input");
        } catch (IllegalArgumentException e) {}
	    
	    try {
	    	new Token(TokenType.NUMBER, "123");
    	} catch (IllegalArgumentException e) {
    		fail("Exception was not expected");
		}
	    
	}

	/**
	 * Test method for {@link tokenizer.Token#getValue()}.
	 */
	@Test
	public void testGetValue() {
		assertEquals(toke1.getValue(), "123");
		assertEquals(toke2.getValue(), "321");
		assertEquals(toke3.getValue(), "123");
		assertEquals(toke4.getValue(), " 123");
		
	}

	/**
	 * Test method for {@link tokenizer.Token#getType()}.
	 */
	@Test
	public void testGetType() {
		assertEquals(toke1.getType(), TokenType.NUMBER);
		assertEquals(toke2.getType(), TokenType.NUMBER);
		assertEquals(toke3.getType(), TokenType.NUMBER);
		assertEquals(toke4.getType(), TokenType.NUMBER);
	}

	/**
	 * Test method for {@link tokenizer.Token#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsObject() {
		assertNotEquals(toke1, null);
		assertNotEquals(toke1,toke2);
		assertEquals(toke1,toke1);
		assertEquals(toke1,toke3);
		assertNotEquals(toke1,toke4);
//		assertNotEquals(toke1,toke5);

	}
	
	/**
	 * Test method for {@link tokenizer.Token#hashCode()}.
	 */
	@Test
	public void testHashCode() {
		assertNotEquals(toke1.hashCode(),toke2.hashCode());
		assertEquals(toke1.hashCode(),toke1.hashCode());
		assertEquals(toke1.hashCode(),toke3.hashCode());
		assertNotEquals(toke1.hashCode(),toke4.hashCode());
		assertEquals(toke1.hashCode(),toke5.hashCode());
	}

}
