/**
 * @author Joopyo Hong
 */

package tokenizer;

import java.util.Set;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class Tokenizer {
    public boolean debugging = false;
	private enum States {
		READY, IN_NAME, IN_NUMBER, EOL;
	}
	private Reader reader;
	private Set<String> keywords;
	private Token backup = null;
	private boolean pushedBack = false;
	
	/**
	 * Constructor for class Tokenizer
	 * 
	 * @param reader	Reader to be stored in this token
	 * @param keywords	Set of keywords to be stored in this token
	 * @exception IllegalArgumentException If the argument map is null
	 */
	public Tokenizer(Reader reader, Set<String> keywords) {
		if (reader == null || keywords == null) throw new IllegalArgumentException();
		this.reader = reader;
		this.keywords = keywords;
	}
	
	/**
	 * Indicates whether there are more tokens to be returned.
	 * 
	 * @return True if there are more tokens to be returned
	 * @exception RuntimeException  If an I/O error occurs
	 */
	public boolean hasNext() {
		if (pushedBack) return true;
		int diagnosis;
		try {
    		reader.mark(1);
    		diagnosis = reader.read();
            while (diagnosis == ' ') {
                diagnosis = reader.read();
            }
    		reader.reset();
    		
    		// the following huge chunk deals with the case of comment
    		if (diagnosis == '/') {
    			reader.mark(1);
    			
    			int diagnosis2 = reader.read();
    	        while (diagnosis2 != '/') {
    	            diagnosis2 = reader.read();
    	        }
    			diagnosis2 = reader.read();
    			reader.reset();
    			
    			reader.mark(1);
    			if (diagnosis2 == '/') {
    				while (diagnosis2 != -1) {
    					if (diagnosis2 == '\n') {
    						reader.reset();
    						return true;
    					}
    					diagnosis2 = reader.read();
    				}
    				reader.reset();
    				return false;
    			}
    			reader.reset();
    			return true;
    		}
		} catch (IOException e) {
		    throw new RuntimeException("There was an IOException.");
		}
		
		return diagnosis != -1;
	}
	
	/**
	 * Returns the next token.
	 * 
	 * @return The next token
	 * @exception RuntimeException If there is no more token or if there is an IOException
	 */
	public Token next() {
			    
		if (pushedBack) {
			pushedBack = false;
			return backup;
		}
		
		States state = States.READY;
		TokenType type = TokenType.ERROR;
		String value = "";
		boolean dotted = false;
		boolean exponented = false;
		boolean lastWasE = false;
		boolean lastWasSign = false;
		
		try {
    		if (!hasNext()) {
    			throw new RuntimeException("No more tokens!");
    		}
    		
    		int ch = reader.read();
    		
    		while (ch != -1) {
    			switch (state) {
    				case READY: {
    					// name mode
    					if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch == '_') || (ch == '$')) {
    						type = TokenType.NAME;
    						state = States.IN_NAME;
    						break;
    					}
    					// number mode
    					if ((ch >= '0' && ch <= '9') || ch == '.') {
    						type = TokenType.NUMBER;
    						state = States.IN_NUMBER;
    						break;
    					}
    					// white space
    					if (ch == ' ' || ch == '\t') {
    						ch = reader.read();
    						break;
    					}
    					// comments
    					if (ch == '/') {
    						reader.mark(1);
    						ch = reader.read();
    						if (ch == '/') {
    							type = TokenType.EOL;
    							state = States.EOL;
    							break;
    						} else {
    							ch = '/';
    							reader.reset();
    						}
    					}
    					// symbol: any single character that isn't whitespace and isn't part of a name or number.
    					if (ch >= 0 && ch <= 255 && ch != '\n') {
    						backup = new Token(TokenType.SYMBOL, value + (char)ch);
    						return backup;
    					}
    					// EOL: newline.
    					if (ch == '\n') {
    						backup = new Token(TokenType.EOL, "\n");
    						return backup;
    					}
    					
    				}
    				
    				case IN_NAME: {
    					if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9') ||
    							(ch == '_') || (ch == '$')) {
    						value += (char)ch;
    						reader.mark(1);
    						ch = reader.read();
    						break;
    					} else {
    						reader.reset();
    						if (keywords.contains(value)) {
    							backup = new Token(TokenType.KEYWORD, value);
    							return backup;
    						} else {
    							backup = new Token(TokenType.NAME, value);
    							return backup;
    						}
    					}
    				}
    				case IN_NUMBER: {
    					
    					if (ch >= '0' && ch <= '9') {
    						value += (char)ch;
    						reader.mark(1);
    						ch = reader.read();
    						lastWasE = false;
    						lastWasSign = false;
    						break;
    					} else if (ch == '.') {
    						if (!(dotted || exponented)) {
    							value += (char)ch;
    							reader.mark(1);
    							ch = reader.read();
    							
    							// dot it and same as above
    							dotted = true;
    							lastWasE = false;
    							lastWasSign = false;
    							break;
    						} else {
    							reader.reset();
    							if (lastWasE || lastWasSign) {
    								backup = new Token(TokenType.ERROR, value);
    								return backup;
    							} else if (value.equals(".")){
    							    backup = new Token(TokenType.SYMBOL, value);
                                    return backup;
    							} else {
    								backup = new Token(TokenType.NUMBER, value);
    								return backup;
    							}
    						}
    					} else if (ch == 'e' || ch == 'E') { // this cannot possibly be the first encounter in this block in my setup
    						// d&f are taken care of as token is immediately returned when d&f are appended
    						// +&- are taken care of as +&- requires a preceding e
    						if (!exponented) {// no for d&f  as well!!!
    							value += (char)ch;
    							reader.mark(1);
    							ch = reader.read();
    							
    							dotted = true;
    							exponented = true;
    							lastWasE = true;
    							lastWasSign = false;
    							break;
    						} else {
    							reader.reset();
    							if (lastWasE || lastWasSign) {
    								backup = new Token(TokenType.ERROR, value);
    								return backup;
    							} else if (value.equals(".")){
                                    backup = new Token(TokenType.SYMBOL, value);
                                    return backup;
    							} else {
    								backup = new Token(TokenType.NUMBER, value);
    								return backup;
    							}
    						}
    						
    					} else if (ch == '+' || ch == '-') { // this cannot be the first encounter in this block in my setup
    						if (lastWasE) {
    							value += (char)ch;
    							reader.mark(1);
    							ch = reader.read();
    							dotted = true;
    							exponented = true;
    							lastWasE = false;
    							lastWasSign = true;
    							break;
    
    						} else {
    							reader.reset();
    							if (lastWasSign) {
    								backup = new Token(TokenType.ERROR, value);
    								return backup;
    							} else if (value.equals(".")){
                                    backup = new Token(TokenType.SYMBOL, value);
                                    return backup;
    							} else {
    								backup = new Token(TokenType.NUMBER, value);
    								return backup;
    							}
    						}
    						
    					} else if (ch == 'd' || ch == 'D' || ch == 'f' || ch == 'F' || ch == 'l' || ch == 'L') {
    						if (lastWasE || lastWasSign) {
    							reader.reset();
    							backup = new Token(TokenType.ERROR, value);
    							return backup;
    						} else if (value.equals(".")){
                                backup = new Token(TokenType.SYMBOL, value);
                                return backup;
                            } else {
    							value += (char)ch;
    							backup = new Token(TokenType.NUMBER, value);
    							return backup;
    						}
    					} else {
    						//if last was e or last was sign-> error
    						reader.reset();
    						if (lastWasE || lastWasSign) {
    							backup = new Token(TokenType.ERROR, value);
    							return backup;
    						} else if (value.equals(".")){
                                backup = new Token(TokenType.SYMBOL, value);
                                return backup;
                            } else {
    							backup = new Token(TokenType.NUMBER, value);
    							return backup;
    						}
    					}
    				}
    				case EOL: {
    					if (ch == '\n') {
    						backup = new Token(TokenType.EOL, "\n");
    						return backup;
    					}
    					ch = reader.read();
    					break;
    				}
    				default: { // this must never be the case
    					assert false;
    				}
    			}
    		}
    		if (state == States.EOL) { // this must never be the case
    			assert false;
    			return null;
    		} else {
    		    backup = new Token(type, value);
    		    if (type == TokenType.NAME) {
    		        if (keywords.contains(value)) backup = new Token(TokenType.KEYWORD, value);
    		    } else if (value.equals(".")){
                    backup = new Token(TokenType.SYMBOL, value);
                    return backup;
    		    } else if (type == TokenType.NUMBER) {
    		        if (lastWasE || lastWasSign) backup = new Token(TokenType.ERROR, value);
    		    }
    		    
                return backup;             
    		}
		} catch (IOException e) {
		    throw new RuntimeException("There was an IOException.");
		}
	}
	
	/**
	 * Takes back the token just returned by next(),
	 * so that a subsequent call to next() will return the same token again. 
	 * 
	 * @exception UnsupportedOperationException
	 */
	public void pushBack() {
		if (backup == null) throw new UnsupportedOperationException("Pushing back at the very beginning of the stream is illegal.");
		pushedBack = true;
	}
	

}
