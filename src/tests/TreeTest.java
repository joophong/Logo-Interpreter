package tests;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import tree.Tree;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Enumeration;

/**
 * @author Joopyo Hong
 * @version April 5, 2014
 */
public class TreeTest {
	Tree<Integer> tree1a, tree1b, tree1c, tree2, tree3a, tree3b;
	Tree<Integer> tree3c,tree6, tree9, tree54;
	
	
	/**
	 * Set up before each test.
	 */
	@Before
	public void setup() {
		tree1a = new Tree<Integer>(1);
		tree1b = new Tree<Integer>(1);
		tree1c = new Tree<Integer>(1);
		tree2 = new Tree<Integer>(2);
		tree3a = new Tree<Integer>(3);
		tree3b = new Tree<Integer>(3);
		tree3c = new Tree<Integer>(3);
		tree6 = new Tree<Integer>(6, tree3a, tree2, tree1b, tree1c);
		tree9 = new Tree<Integer>(9, tree3b, tree3c);
		tree54 = new Tree<Integer>(54, tree1a, tree6, tree9);
	}
	
	
	/**
	 * Tests getValue.
	 */
	@Test
	public void getValueTest() {
		assertEquals((int)tree1a.getValue(), 1);
		assertEquals((int)tree54.getValue(), 54);
		assertNotEquals((int)tree1a.getValue(), 2);
		assertEquals((int)tree2.getValue(), 2);
	}
	
	/**
	 * Tests firstChild.
	 */
	@Test
	public void firstChildTest() {
		assertEquals(tree6.firstChild(), tree3a);
		assertEquals(tree6.firstChild(), tree3b);
		
		assertEquals(tree54.firstChild(), tree1a);
		assertNotEquals(tree6.firstChild(), tree6);
		assertNotEquals(tree6.firstChild(), tree1b);
		assertEquals(tree6.firstChild(), tree3a);
	}
	
	/**
	 * Tests lastChild.
	 */
	@Test
	public void lastChildTest() {
		assertEquals(tree6.lastChild(), tree1a);
		assertEquals(tree6.lastChild(), tree1b);
		assertEquals(tree6.lastChild(), tree1c);

		assertEquals(tree54.lastChild(), tree9);
		assertNotEquals(tree54.lastChild(), tree1a);
		assertNotEquals(tree54.lastChild(), tree6);
		assertNotEquals(tree54.lastChild(), tree3a);
		assertNotEquals(tree6.lastChild(), tree6);
		assertNotEquals(tree6.lastChild(), tree2);
		assertEquals(tree6.lastChild(), tree1a);
	}
	
	/**
	 * Tests numberOfChildren.
	 */
	@Test
	public void numberOfChildrenTest() {
		assertEquals(tree54.numberOfChildren(), 3);
		assertEquals(tree9.numberOfChildren(), 2);
		assertEquals(tree6.numberOfChildren(), 4);
		assertEquals(tree1a.numberOfChildren(), 0);
		assertEquals(tree3a.numberOfChildren(), 0);
		assertEquals(tree2.numberOfChildren(), 0);
	}
	
	/**
	 * Tests childTest.
	 */
	@Test
	public void childTest() {
		assertEquals(tree54.child(0), tree1a);
		assertEquals(tree54.child(0), tree1b);
		assertNotEquals(tree54.child(1), tree9);
		assertEquals(tree9.child(1), tree3c);
		assertEquals(tree6.child(1), tree2);
	}
	

	/**
	 * Tests children.
	 */
	@Test
	public void childrenTest() {
		Iterator<Tree<Integer>> it = tree54.children();
		assertTrue(it.hasNext());
		assertEquals(it.next(), tree1a);
		it.remove();
		assertEquals(it.next(), tree6);
		it.remove();
		assertEquals(it.next(), tree9);
		it.remove();
		assertFalse(it.hasNext());
	}
	
	/**
	 * Tests isLeaf.
	 */
	@Test
	public void isLeafTest() {
		assertFalse(tree54.isLeaf());
		assertFalse(tree6.isLeaf());
		assertFalse(tree9.isLeaf());
		assertTrue(tree1a.isLeaf());
		assertTrue(tree1b.isLeaf());
		assertTrue(tree1c.isLeaf());
		assertTrue(tree3a.isLeaf());
		assertTrue(tree3b.isLeaf());
		assertTrue(tree3c.isLeaf());
		assertTrue(tree2.isLeaf());

	}
	
	/**
	 * Tests equals.
	 */
	@Test
	public void equalsTest() {
		assertEquals(tree1a, tree1a);
		assertEquals(tree1a, tree1b);
		assertEquals(tree1a, tree1c);
		assertEquals(tree1b, tree1a);
		assertEquals(tree1c, tree1a);
		assertEquals(tree3a, tree3a);
		assertEquals(tree3a, tree3b);
		assertEquals(tree3a, tree3c);
		assertEquals(tree3b, tree3a);
		assertEquals(tree3c, tree3a);
		assertNotEquals(tree1a, tree54);
		assertNotEquals(tree54, tree1a);
		assertNotEquals(tree54, tree9);
		assertNotEquals(tree9, tree54);
		assertNotEquals(tree9, tree6);
	}
	
	/**
	 * Tests toString.
	 */
	@Test
	public void toStringTest() {
//		System.out.println(tree54.toString());
	}
	
	
	/**
	 * Tests setValue.
	 */
	@Test
	public void setValueTest() {
		tree1a.setValue(54);
		tree54.setValue(1);
		tree2.setValue(3);
		
		assertEquals((int)tree1a.getValue(), 54);
		assertEquals((int)tree54.getValue(), 1);
		assertEquals((int)tree2.getValue(), 3);
	}
	
	/**
	 * Tests addChild.
	 */
	@Test
	public void addChildTest() {

	    try {
	    	tree1b.addChild(tree6);
	    	fail("Should have thrown an exception");
	    } catch (IllegalArgumentException e) {
	    }
	    
	    try {
	    	tree3a.addChild(tree54);
	    	fail("Should have thrown an exception");
	    } catch (IllegalArgumentException e) {
	    }
	    
	    try {
	    	tree6.addChild(tree1b);
	    	tree6.addChild(tree1b);
	    } catch (IllegalArgumentException e) {
	    	fail("Should not have thrown an exception");
	    }
	    
	    try {
	    	tree6.addChild(tree1b);
	    } catch (IllegalArgumentException e) {
	    	fail("Should not have thrown an exception");
	    }
	    
	    try {
	    	tree3b.addChild(tree6);
	    } catch (IllegalArgumentException e) {
	    	fail("Should not have thrown an exception");
	    }
	    
	}
	
	/**
	 * Tests addChild with different signature.
	 */
	@Test
	public void addChild2Test() {
		
	    try {
	    	tree1b.addChild(0, tree6);
	    	fail("Should have thrown an exception");
	    } catch (IllegalArgumentException e) {
	    }
	    
	    try {
	    	tree3a.addChild(0, tree54);
	    	fail("Should have thrown an exception");
	    } catch (IllegalArgumentException e) {
	    }
	    
	    try {
	    	tree6.addChild(0, tree1b);
	    	tree6.addChild(0, tree1b);
	    } catch (IllegalArgumentException e) {
	    	fail("Should not have thrown an exception");
	    }
	    assertEquals(tree6.numberOfChildren(), 6);
	    
	    
	    try {
	    	tree1a.addChild(0, tree6);
	    } catch (IllegalArgumentException e) {
	    	fail("Should not have thrown an exception");
	    }
	    
	    try {
	    	tree1c.addChild(0, tree9);
	    } catch (IllegalArgumentException e) {
	    	fail("Should not have thrown an exception");
	    }
	    
	    try {
	    	tree6.addChild(0, tree9);
	    } catch (IllegalArgumentException e) {
	    	fail("Should not have thrown an exception");
	    }
	}
	
	/**
	 * Tests addChildren.
	 */
	@Test
	public void addChildrenTest() {
	    try {
	    	tree1b.addChildren(tree2, tree3b, tree3c);
	    } catch (IllegalArgumentException e) {
	    	fail("Should not have thrown an exception");
	    }
	    assertEquals(tree1b.child(0), tree2);
	    assertEquals(tree1b.child(1), tree3b);
	    assertEquals(tree1b.child(2), tree3c);
	    
	    try {
	    	tree3a.addChildren(tree2, tree1b, tree54);
	    	fail("Should have thrown an exception");
	    } catch (IllegalArgumentException e) {
	    }
	    assertEquals(tree3a.child(0), tree2);
	    assertEquals(tree3a.child(1), tree1b);
	}
	
	/**
	 * Tests removeChildren.
	 */
	@Test
	public void removeChildrenTest() {
	    try {
	    	tree1b.removeChild(0);
	    	fail("Should have thrown an exception");
	    } catch (NoSuchElementException e) {
	    }
	    
	    try {
	    	tree54.removeChild(3);
	    	fail("Should have thrown an exception");
	    } catch (NoSuchElementException e) {
	    }
	    
	    try {
	    	tree9.removeChild(0);
	    } catch (NoSuchElementException e) {
	    	fail("Should not have thrown an exception");
	    }
	    
	    assertEquals(tree9.child(0), tree3c);
    }
	
	/**
     * Tests parse.
     */
    @Test
    public final void testParse() {
        Tree<String> expected = new Tree<String>("abc");
        Tree<String> actual = Tree.parse("abc");
        assertEquals(expected, actual);
        
        expected.addChild(new Tree<String>("x"));
        actual = Tree.parse("abc(x)");
        assertEquals(expected, actual);
        
        expected.addChild(new Tree<String>("y"));
        actual = Tree.parse("abc(x y)");
        assertEquals(expected, actual);
        actual = Tree.parse("  abc  ( x   y ) ");
        assertEquals(expected, actual);
        
        expected.addChild(actual);
        actual = Tree.parse("abc(x y abc(x y))");
        assertEquals(expected, actual);
        
//        assertEquals(a1, Tree.parse("a(b (d e) c(f g))"));
        
        actual = Tree.parse("+(+(a b) c)");
        Tree<String> subtree = new Tree<String>("+");
        subtree.addChild(new Tree<String>("a"));
        subtree.addChild(new Tree<String>("b"));
        expected = new Tree<String>("+");
        expected.addChild(subtree);
        expected.addChild(new Tree<String>("c"));
        assertEquals(expected, actual);
    }
	
	
}
