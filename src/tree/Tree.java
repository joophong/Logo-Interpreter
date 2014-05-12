
package tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * @author Joopyo Hong
 * @version March 13, 2014
 */
public class Tree<V> {
	private V value;
	private ArrayList<Tree<V>> children;
	
	
	/**
	 * Constructor for class Tree
	 * 
	 * @param value		value for this node
	 * @param children	array of children(nodes)
	 */
	public Tree(V value, Tree<V>... children) {
		this.value = value; 
		this.children = new ArrayList<Tree<V>>(Arrays.asList(children));
	}
	
	
	/**
	 * Gets value of this tree.
	 * 
	 * @return value
	 */
	public V getValue() {
		return value;
	}
	
	/**
	 * Gets the first child.
	 * 
	 * @return child at the very first index
	 */
	public Tree<V> firstChild() {
		if (this.numberOfChildren() == 0) return null;
		else return children.get(0);
	}
	
	/**
	 * Gets the last child.
	 * 
	 * @return child at the very last index
	 */
	public Tree<V> lastChild() {
		if (this.numberOfChildren() == 0) return null;
		else return children.get(numberOfChildren() - 1);
	}
	
	/**
	 * Gets the number of this tree's children.
	 * 
	 * @return number of children
	 */
	public int numberOfChildren() {
		return children.size();
	}
	
	/**
	 * Gets the child at given index.
	 * 
	 * @param index	
	 * @return child at given index
	 * @exception NoSuchElementException	if index is out of legal bound
	 */
	public Tree<V> child(int index) throws NoSuchElementException {
		if (index < 0 || index >= this.numberOfChildren()) {
			throw new NoSuchElementException();			
		} else return children.get(index);
	}
	
	/**
	 * Returns an iterator for the children of this tree.
	 * 
	 * @return iterator
	 */
	public Iterator<Tree<V>> children() {
		return children.iterator();
	}
	
	/**
	 * Returns whether or not the tree is a leaf, i.e. has any children.
	 * 
	 * @return whether or not the tree has any children
	 */
	public boolean isLeaf() {
		return numberOfChildren() == 0;
	}
	
	/**
	 * Checks whether a given node is part of this tree.
	 * 
	 * @param node		target node, for which the tree will be searched
	 * @return whether or not the the target node is the descendant of this tree
	 */
	private boolean contains(Tree<V> node) {		
		if (this == node) return true; // main operation
		
		if (this.isLeaf()) return false; // base case
		
		for (int i = 0; i < this.numberOfChildren(); i++) { // recursive calls
			if (this.child(i).contains(node)) return true;
		}
		return false;
	}
	
	/**
	 * Compares given object(potentially a tree) with this tree. 
	 * 
	 * @param object	target to compare this tree against
	 * @return whether or not given object and this tree are equal
	 */
	@Override public boolean equals(Object object) {
		
		if (object == null) return (this == object); // null scenario
		if (!(object instanceof Tree)) return false; // tree-or-not check
		
		Tree<V> target = (Tree<V>)object;
		if (this.getValue() == null) {
		    if (!(target.getValue() == null)) return false;                    // value check(null case)
		} else {
		    if (!(this.getValue().equals(target.getValue()))) return false;     // value check(otherwise)
		}
		if (this.numberOfChildren() != target.numberOfChildren()) return false; // # of children check
		
		for (int i = 0; i < this.numberOfChildren(); i++) {				// corresponding children equality check
			if (!(this.child(i).equals(target.child(i)))) return false;
		}
		
		return true;
	}
	
	/**
	 * Returns string representation of this tree.
	 * 
	 * @return string representation
	 */
	@Override
	public String toString() {
		return project(this, 0);
	}
	
	/**
	 * Returns string representation of this tree.
	 * Works as a helper method for toString()
	 * 
	 * @param tree		tree to be string-represented
	 * @param count		number of times 2 empty space are concatenated
	 * @return string representation
	 */
	private String project(Tree<V> tree, int count) {
		String str = tree.value.toString() + "\n";
		for (int i = 0; i < count; i++) {
			str = "  " + str;
		}
		
		if (tree.numberOfChildren() == 0) { // base case
			return str;
		} else {
			for (int i = 0; i < tree.numberOfChildren(); i++) {
				str = str + project(tree.child(i), count + 1);
			}
			return str;
		}
	}
	
	/**
	 * Sets value of the node.
	 * 
	 * @param value	new value of the node
	 */
	public void setValue(V value) {
		this.value = value;
	}
	
	/**
	 * Adds newChild as the new last child of this tree.
	 * 
	 * @param newChild						
	 * @exception IllegalArgumentException		if post-operation resultant tree is invalid
	 */
	public void addChild(Tree<V> newChild) throws IllegalArgumentException {
		if (newChild.contains(this)) {
			throw new IllegalArgumentException();
		} else {
			children.add(newChild);
		}
	}
	
	/**
	 * Adds newChild at given index in the tree.
	 * 
	 * @param index		
	 * @param newChild
	 * @exception IllegalArgumentException		if post-operation resultant tree is invalid
	 */
	public void addChild(int index, Tree<V> newChild) throws IllegalArgumentException {
		if (index == 0 && this.numberOfChildren() == 0 && !newChild.contains(this)) {
			children.add(newChild);
		} else if (index < 0 || index > this.numberOfChildren() || newChild.contains(this)) {
			throw new IllegalArgumentException();
		} else {
			children.add(index, newChild);				
		}
	}
	
	/**
	 * Adds each tree in children list as a new child of this tree node, after any existing children.
	 * 
	 * @param children							array of trees(nodes)
	 * @exception IllegalArgumentException		if post-operation resultant tree is invalid
	 */
	public void addChildren(Tree<V>... children ) throws IllegalArgumentException {
		for (int i = 0; i < children.length; i++) {
			if (children[i].contains(this)) {
				throw new IllegalArgumentException();
			} else {
				this.children.add(children[i]);
			} 
		}
	}
	
	/**
	 * Removes the child at given index.
	 * 
	 * @param index
	 * @return the removed child
	 * @exception NoSuchElementException	if index is out of legal bound
	 */
	public Tree<V> removeChild(int index) throws NoSuchElementException {
		if (index < 0 || index >= this.numberOfChildren()) {
			throw new NoSuchElementException();			
		} else return children.remove(index);
	}
	
    /**
     * Parses a string of the general form
     * <code>value(child, child, ..., child)</code> and returns the
     * corresponding tree. Children may be separated by commas and/or spaces.
     * Node values are all Strings.
     * 
     * @param s The String to be parsed.
     * @return The resultant Tree&lt;String&lt;.
     * @throws IllegalArgumentException
     *             If problems are detected in the input string.
     */
    public static Tree<String> parse(String s) throws IllegalArgumentException {
        StringTokenizer tokenizer = new StringTokenizer(s, " ()", true);
        List<String> tokens = new LinkedList<String>();
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (token.trim().length() == 0)
                continue;
            tokens.add(token);
        }
        Tree<String> result = parse(tokens);
        if (tokens.size() > 0) {
            throw new IllegalArgumentException("Leftover tokens: " + tokens);
        }
        return result;
    }
    
    /**
     * Parses and returns one tree, consisting of a value and possible children
     * (enclosed in parentheses), starting at the first element of tokens.
     * Returns null if this token is a close parenthesis, or if there are no
     * more tokens.
     * 
     * @param tokens The tokens that describe a Tree.
     * @return The Tree described by the tokens.
     * @throws IllegalArgumentException
     *             If problems are detected in the input list.
     */
    private static Tree<String> parse(List<String> tokens)
            throws IllegalArgumentException {
        // No tokens -- return null
        if (tokens.size() == 0) {
            return null;
        }
        // Get the next token and remove it from the list
        String token = tokens.remove(0);
        // If the token is an open parenthesis
        if (token.equals("(")) {
            throw new IllegalArgumentException(
                "Unexpected open parenthesis before " + tokens);
        }
        // If the token is a close parenthesis, we are at the end of a list of
        // children
        if (token.equals(")")) {
            return null;
        }
        // Make a tree with this token as its value
        Tree<String> tree = new Tree<String>(token);
        // Check for children
        if (tokens.size() > 0 && tokens.get(0).equals("(")) {
            tokens.remove(0);
            Tree<String> child;
            while ((child = parse(tokens)) != null) {
                tree.addChildren(child);
            }
        }
        return tree;
    }
	
}
