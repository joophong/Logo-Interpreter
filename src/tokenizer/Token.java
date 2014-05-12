
package tokenizer;

/**
 * @author Joopyo Hong
 * @version March 20, 2014
 */
public class Token {
	
	private TokenType type;
	private String value;

	/**
	 * Constructor for class Token
	 * 
	 * @param type	type to be stored in this token
	 * @param value	value to be stored in this token
	 * @exception IllegalArgumentException if the argument map is null
	 */
	public Token(TokenType type, String value) {
		if (type == null || value == null) throw new IllegalArgumentException();
		this.type = type;
		this.value = value;
	}
	
	/**
	 * Gets value of this token.
	 * 
	 * @return value of this token 
	 */
	public String getValue() {
		return this.value;
	}
	
	/**
	 * Gets type of this token.
	 * 
	 * @return type of this token 
	 */
	public TokenType getType() {
		return this.type;
	}
	
	/**
	 * Indicates whether some other token is equal to this one.
	 * 
	 * @param o		the reference object with which to compare
	 * @return true if type and value of this object is the same as those of object argument; false otherwise
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Token)) return false;
		return (this.value).equals(((Token)o).value);
//		return this.type == ((Token)o).type && (this.value).equals(((Token)o).value);
	}
	
	/**
	 * Returns hash code for this token.
	 * 
	 * @return the hash code for this token
	 */ 
	@Override
	public int hashCode() {
		return value.hashCode();
	}
	
	   /**
     * Returns string representation of this token.
     * 
     * @return the string representation of this token
     */ 
    @Override
    public String toString() {
        return value;
    }
}
