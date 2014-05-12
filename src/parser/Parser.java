package parser;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

import tokenizer.Tokenizer;
import tokenizer.TokenType;
import tokenizer.Token;
import tree.Tree;
/**
 * @author David Matuszek
 * @author Joopyo Hong
 * @version April 6, 2014
 */
public class Parser {
    private Tokenizer tokenizer = null; 
    private static boolean debug = false;
    private static Set<String> keywords = new HashSet<String>();
    
    private static final String[] keywordList = {"penup", "pendown","home", "jump",
            "set", "repeat", "while", "if", "else", "do", "forward", "right", "left",
            "face", "red", "orange", "yellow", "green", "cyan", "blue", "purple",
            "magenta", "pink", "olive", "black", "gray", "white", "brown", "tan", "color", "def"};
    
    private static final List<String> colorList = Arrays.asList("red", "orange", "yellow", "green",
            "cyan", "blue", "purple", "magenta", "pink", "olive", "black", "gray", "white", "brown", "tan");
    
    private static final int[][] colorCode = {{255, 0, 0}, {255, 128, 0}, {255, 255, 0}, {0, 153, 0},
            {0, 255, 255}, {0, 64, 255}, {128, 0, 255}, {255, 0, 255}, {250, 175, 190}, {128, 128, 0},
            {0, 0, 0}, {128, 128, 128}, {255, 255, 255}, {128, 64, 0}, {210, 180, 140}};
    
    
    /**
     * The stack used for holding Trees as they are created.
     */
    public Stack<Tree<Token>> stack = new Stack<Tree<Token>>();

    /**
     * Constructs a Parser for the given string.
     * @param text The string to be parsed.
     */
    public Parser(String text) { 
             
        for (String s: keywordList) {
            keywords.add(s);
        }
        
        tokenizer = new Tokenizer(new StringReader(text), keywords);
    }
    
    /**
     * Returns this Parser's Tokenizer. Should be used <i>only</i>
     * for testing this Parser; external use of the Tokenizer will
     * change its state and invalidate this Parser.
     * 
     * @return This Parser's Tokenizer.
     */
    public Tokenizer getTokenizer() {
        return tokenizer;
    }

    /**
     * Tries to parse a &lt;program&gt;.
     * <pre>&lt;program&gt; ::= &lt;command&gt; { &lt;command&gt; } { &lt;procedure&gt; }</pre>
     * A <code>SyntaxException</code> will be thrown if the sequence specified above is not followed.
     * @return <code>true</code> if a &lt;program&gt; is recognized.
     * @throws SyntaxException  If syntax error is detected
     */
    public boolean isProgram() {
        if (!isCommand()) error("No <command> at the beginning of <program>");
        int numOfCommands = 1;
        while (isCommand()) {
            numOfCommands++;
        }
        makeRootDesignatedTree("block", numOfCommands);
        
        int numOfProcedures = 0;
        while (isProcedure()) {
            numOfProcedures++;
        }
        makeRootDesignatedTree("list", numOfProcedures);
        stack.push(new Tree<Token>(new Token(TokenType.KEYWORD, "program")));
        makeTree(1, 3, 2);
        if (tokenizer.hasNext()) error("No <command> or <procedure> at the end of <program>");
        
        return true;
    }
    
    /**
     * Tries to parse a &lt;procedure&gt;.
     * <pre>&lt;procedure&gt; ::= "def" &lt;name&gt; { &lt;variable&gt; } &lt;block&gt;</pre>
     * A <code>SyntaxException</code> will be thrown if "def" is present
     * but the sequence specified above is not followed.
     * @return <code>true</code> if a &lt;procedure&gt; is recognized.
     * @throws SyntaxException  If syntax error is detected
     */
    public boolean isProcedure() {
        if (!isKeyword("def")) return false;
        if (!isName()) error("No <name> after 'def'");
        int numOfVar = 0;
        while (isVariable()) {
            numOfVar++;
        }
        makeRootDesignatedTree("list", numOfVar);
        stack.push(new Tree<Token>(new Token(TokenType.KEYWORD, "header")));
        makeTree(1, 3, 2);    
        
        if (!isBlock()) error("No <block> at the end of <procedure>");
        makeTree(3, 2, 1);
                
        return true;
    }
    /**
     * Tries to parse a &lt;command&gt;.
     * <pre> &lt;command&gt; ::= &lt;move&gt; &lt;expression&gt; &lt;eol&gt; |
     * "penup" &lt;eol&gt; | "pendown" &lt;eol&gt; | &lt;color&gt; &lt;eol&gt; |
     * "home" &lt;eol&gt; | "jump" &lt;expression&gt; &lt;expression&gt; &lt;eol&gt; |
     * "set" &lt;variable&gt; &lt;expression&gt; &lt;eol&gt; |
     * "repeat" &lt;expression&gt; &lt;block&gt; | "while" &lt;condition&gt; &lt;block&gt; |
     * "if" &lt;condition&gt; &lt;block&gt; [ "else" &lt;block&gt; ] |
     * "do" &lt;name&gt; { &lt;expression&gt; } &lt;eol&gt;</pre>
     * A <code>SyntaxException</code> will be thrown if the sequence specified above is not followed.
     * 
     * @return <code>true</code> if a &lt;command&gt; is recognized. 
     * @throws IOException  If an I/O error occurs  If an I/O error occurs
     */ 
    public boolean isCommand() {
        
        if (isMove()) {
            if (!isExpression()) error("No <expression> following <move>");
            makeTree(2,1);
            if (!isEol()) error("No <eol> following <move> - <expression>");
            return true;
        }
        
        if (isColor()) {
            if (!isEol()) error("No <eol> following <color>");
            return true;
        }
        
        if (isPenup() || isPendown() || isHome() || isJump() || isSet() ||
                isRepeat() || isWhile() || isIf() || isDo()) return true;
        
        return false;
    }
    
    /**
     * Tries to parse a &lt;move&gt;.
     * <pre>&lt;move&gt; ::= "forward" | "right" | "left" | "face"</pre>
     * @return <code>true</code> if a &lt;move&gt; is recognized. 
     * @throws IOException  If an I/O error occurs  If an I/O error occurs
     */
    public boolean isMove() {
        return isKeyword("forward") || isKeyword("right") ||
                isKeyword("left") || isKeyword("face");
    }
    
    /**
     * Tries to parse a &lt;color&gt;.
     * <pre>&lt;color&gt; ::= "red" | "orange" | "yellow" | "green" | "cyan" | "blue" |
     * "purple" | "magenta" | "pink" | "olive" | "black" | "gray" | "white" | "brown" | "tan"|
     * "color" &lt;expression&gt; &lt;expression&gt; &lt;expression&gt;"</pre>
     * A <code>SyntaxException</code> will be thrown if "color" is not followed by three
     * consecutive &lt;expression&gt;.
     * @return <code>true</code> if a &lt;color&gt; is recognized. 
     * @throws IOException  If an I/O error occurs  If an I/O error occurs
     * @throws SyntaxException  If syntax error is detected
     */ 
    public boolean isColor() {
                
        for (String s: colorList) {
            if (isKeyword(s)) {
                int colorIndex = colorList.indexOf(s);
                Token root = new Token(TokenType.KEYWORD, "color");
                Token child1 = new Token(TokenType.NUMBER, "" + colorCode[colorIndex][0]);
                Token child2 = new Token(TokenType.NUMBER, "" + colorCode[colorIndex][1]);
                Token child3 = new Token(TokenType.NUMBER, "" + colorCode[colorIndex][2]);
                stack.pop();
                stack.push(new Tree<Token>(root, new Tree<Token>(child1), new Tree<Token>(child2), new Tree<Token>(child3)));
                
                return true;
            }
        }
        
        if (isKeyword("color")) {
            if (!isExpression()) error("No <expression> following 'color'");
            if (!isExpression()) error("No <expression> following 'color' - <expression>");
            if (!isExpression()) error("No <expression> following 'color' - <expression> - <expression>");
            
            makeTree(4, 3, 2, 1);
            
            return true;
        }

        return false;
    }

    /**
     * Tries to parse a &lt;block&gt;.
     * <pre>&lt;block&gt; ::= "{" &lt;eol&gt; { &lt;command&gt; } "}" &lt;eol&gt;</pre>
     * A <code>SyntaxException</code> will be thrown if an "{" is present
     * but the sequence specified above is not followed.
     * @return <code>true</code> if a &lt;block&gt; is recognized.
     * @throws SyntaxException  If syntax error is detected
     */
    public boolean isBlock() {
        if (!(isSymbol("{"))) return false;
        stack.pop();
        if (!(isEol())) error("No <eol> after the opening bracket");
        
        int numOfCommands = 0;
        while (isCommand()) {
            numOfCommands++;
        }
        if (!(isSymbol("}"))) error("Unclosed bracketted command");
        stack.pop();
        if (!(isEol())) error("No <eol> after the closing bracket");
        

        makeRootDesignatedTree("block", numOfCommands);
        return true;
    }
    
    /**
     * Tries to parse a &lt;condition&gt;.
     * <pre>&lt;condition&gt; ::= &lt;expression&gt; &lt;comparator&gt; &lt;expression&gt;</pre>
     * A <code>SyntaxException</code> will be thrown if an &lt;expression&gt;
     * is present but is not followed by a &lt;comparator&gt; and thereafter another &lt;expression&gt;.
     * @return <code>true</code> if a &lt;condition&gt; is recognized.
     * @throws SyntaxException  If syntax error is detected
     */
    public boolean isCondition() {
        if (!isExpression()) return false;
        if (!isComparator()) error("No <comparator> following <expression>");
        if (!isExpression()) error("No <expression> following <expression> - <comparator>");
        makeTree(2, 3, 1);
        
        return true;
    }
    
    /**
     * Tries to parse an &lt;expression&gt;.
     * <pre>&lt;expression&gt; ::= [ &lt;add_operator&gt; ] &lt;term&gt; { &lt;add_operator&gt; &lt;unsignedTerm&gt; }</pre>
     * A <code>SyntaxException</code> will be thrown if the add_operator
     * is present but not followed by a valid &lt;expression&gt;.
     * @return <code>true</code> if an expression is recognized.
     * @throws IOException  If an I/O error occurs 
     */
    public boolean isExpression() {
        if (!isTerm()) {
            return false;
        }
        while (isAddOperator()) {
            if (!isUnsignedTerm())
                error("Error in expression after '+' or '-'");
            makeTree(2, 3, 1);
        }
        return true;
    }
    
    /**
     * Tries to parse a &lt;term&gt;.
     * <pre>&lt;term&gt; ::= &lt;factor&gt; { &lt;multiply_operator&gt; &lt;factor&gt; }</pre>
     * A <code>SyntaxException</code> will be thrown if the multiply_operator
     * is present but not followed by a valid &lt;term&gt;.
     * @return <code>true</code> if a term is recognized.
     * @throws IOException  If an I/O error occurs 
     */
    public boolean isTerm() {
        if (!isFactor())
            return false;
        while (isMultiplyOperator()) {
            if (!isFactor()) error("No term after '*' or '/'");
            makeTree(2, 3, 1);
        }
        
        return true;
    }
    
    /**
     * Tries to parse an &lt;unsignedTerm&gt;.
     * <pre>&lt;unsignedTerm&gt; ::= &lt;unsignedFactor&gt; { &lt;multiply_operator&gt; &lt;factor&gt; }</pre>
     * A <code>SyntaxException</code> will be thrown if the multiply_operator
     * is present but not followed by a valid &lt;term&gt;.
     * @return <code>true</code> if an unsignedTerm is recognized.
     * @throws IOException  If an I/O error occurs 
     */
    public boolean isUnsignedTerm() {
        if (!isUnsignedFactor())
            return false;
        while (isMultiplyOperator()) {
            if (!isUnsignedFactor())
                error("No term after '*' or '/'");
            makeTree(2, 3, 1);
        }
        return true;
    }

    /**
     * Tries to parse an &lt;unsignedFactor&gt;.
     * <pre>&lt;unsignedFactor&gt; ::= &lt;variable&gt;
     *                   | &lt;number&gt;
     *                   | "(" &lt;expression&gt; ")"</pre>
     * A <code>SyntaxException</code> will be thrown if the opening
     * parenthesis is present but not followed by a valid
     * &lt;expression&gt; and a closing parenthesis.
     * @return <code>true</code> if an unsignedFactor is recognized.
     * @throws IOException  If an I/O error occurs 
     */
    public boolean isUnsignedFactor() {
        if (isVariable()) {
            return true;
        }
        if (isNumber()) {
            return true;
        }
        if (isKeyword("getX") || isKeyword("getY")) {
            return true;
        }
        if (isSymbol("(")) {
            stack.pop();
            if (!isExpression()) {
                error("Error in parenthesized expression");
            }
            if (!isSymbol(")")) {
                error("Unclosed parenthetical expression");
            }
            stack.pop();
            return true;
        }
        return false;
    }

    
    /**
     * Tries to parse a &lt;factor&gt;.
     * <pre>&lt;factor&gt; ::= [ &lt;addOperator&gt; ] &lt;unsignedFactor&gt;</pre>
     * @return <code>true</code> if a factor is recognized.
     * @throws IOException  If an I/O error occurs 
     */
    public boolean isFactor() {
        boolean prefix = isAddOperator();
        if (isUnsignedFactor()) {
            if (prefix) makeTree(2, 1);
            return true;
        }
        if (prefix) error("Unary sign not followed by a factor.");
        return false;
    }
    
    /**
     * Tries to parse a &lt;comparator&gt;.
     * <pre>&lt;comparator&gt; ::= "<" | "=" | ">"</pre>
     * @return <code>true</code> if a &lt;comparator&gt; is recognized. 
     */
    public boolean isComparator() {
        return isSymbol("<") || isSymbol("=") || isSymbol(">");
    }
    
    /**
     * Tries to parse an &lt;add_operator&gt;.
     * <pre>&lt;add_operator&gt; ::= "+" | "-"</pre>
     * @return <code>true</code> if an addop is recognized.
     * @throws IOException  If an I/O error occurs 
     */
    public boolean isAddOperator() {
        return isSymbol("+") || isSymbol("-");
    }

    /**
     * Tries to parse a &lt;multiply_operator&gt;.
     * <pre>&lt;multiply_operator&gt; ::= "*" | "/"</pre>
     * @return <code>true</code> if a multiply_operator is recognized.
     * @throws IOException  If an I/O error occurs 
     */
    public boolean isMultiplyOperator() {
        return isSymbol("*") || isSymbol("/");
    }
    
    /**
     * Tries to parse a &lt;variable&gt;, which is just a "name".
     * 
     * @return <code>true</code> if a variable is recognized.
     * @throws IOException  If an I/O error occurs 
     */
    public boolean isVariable() {
        return isName();
    }
    
    /**
     * Tries to parse an &lt;eol&gt;.
     * <pre>&lt;eol&gt; ::= &lt;newline&gt; { &lt;newline&gt; }</pre>
     * @return <code>true</code> if an &lt;eol&gt; is recognized.
     */
    public boolean isEol() {
        if (!(nextTokenMatches(TokenType.EOL))) {
            return false;
        }
        stack.pop();
        while (nextTokenMatches(TokenType.EOL)) {
            stack.pop();
        }
        
        return true;
    }
    
    //------------------------- Private "helper" methods

    /**
     * Tests whether the next Token is a number. If it is, the Token
     * is consumed, otherwise it is not.
     * 
     * @return <code>true</code> if the next Token is a number.
     * @throws IOException  If an I/O error occurs 
     */
    private boolean isNumber() {
        return nextTokenMatches(TokenType.NUMBER);
    }

    /**
     * Tests whether the next Token is a name. If it is, the Token
     * is consumed, otherwise it is not.
     * 
     * @return <code>true</code> if the next Token is a name.
     * @throws IOException  If an I/O error occurs 
     */
    private boolean isName() {
        return nextTokenMatches(TokenType.NAME);
    }

    /**
     * Tests whether the next Token is the expected name. If it is, the Token
     * is consumed, otherwise it is not.
     * 
     * @param expectedName The String value of the expected next Token.
     * @return <code>true</code> if the next Token is a name with the expected value.
     * @throws IOException  If an I/O error occurs 
     */
    private boolean isName(String expectedName) {
        return nextTokenMatches(TokenType.NAME, expectedName);
    }

    /**
     * Tests whether the next Token is the expected keyword. If it is,
     * the Token is consumed, otherwise it is not.
     * 
     * @param expectedName The String value of the expected next Token.
     * @return <code>true</code> if the next Token is a name with the
     *        expected value.
     * @throws IOException  If an I/O error occurs 
     */
    private boolean isKeyword(String expectedName) {
        return nextTokenMatches(TokenType.KEYWORD, expectedName);
    }

    /**
     * Tests whether the next Token is the expected symbol. If it is,
     * the Token is consumed, otherwise it is not.
     * @param expectedSymbol  The expected symbol.
     * 
     * @return <code>true</code> if the next Token is the expected symbol.
     * @throws IOException  If an I/O error occurs 
     */
    private boolean isSymbol(String expectedSymbol) {
        return nextTokenMatches(TokenType.SYMBOL, expectedSymbol);
    }

    /**
     * Tries to parse a sequence headed by "penup";.
     * <pre>"penup" &lt;eol&gt;</pre>
     * A <code>SyntaxException</code> will be thrown if a "penup" is present
     * but is not followed by &lt;eol&gt;.
     * @return <code>true</code> if a penup sequence is recognized.
     * @throws SyntaxException  If syntax error is detected
     */
    private boolean isPenup() {
        if (!isKeyword("penup")) return false;
        if (!isEol()) error("No <eol> following 'penup'");
        return true;
    }

    /**
     * Tries to parse a sequence headed by "pendown";.
     * <pre>"pendown" &lt;eol&gt;</pre>
     * A <code>SyntaxException</code> will be thrown if a "pendown" is present
     * but is not followed by &lt;eol&gt;.
     * @return <code>true</code> if a pendown sequence is recognized.
     * @throws SyntaxException  If syntax error is detected
     */    
    private boolean isPendown() {
        if (!isKeyword("pendown")) return false;
        if (!isEol()) error("No <eol> following 'pendown'");
        return true;
    }
    
    /**
     * Tries to parse a sequence headed by "home";.
     * <pre>"home" &lt;eol&gt;</pre>
     * A <code>SyntaxException</code> will be thrown if a "home" is present
     * but is not followed by &lt;eol&gt;.
     * @return <code>true</code> if a home sequence is recognized.
     * @throws SyntaxException  If syntax error is detected
     */
    private boolean isHome() {
        if (!isKeyword("home")) return false;
        if (!isEol()) error("No <eol> following 'home'");
        return true;
    }
    
    /**
     * Tries to parse a sequence headed by "jump";.
     * <pre>"jump" &lt;expression&gt; &lt;expression&gt; &lt;eol&gt;</pre>
     * A <code>SyntaxException</code> will be thrown if a "jump" is present
     * but is not followed by two &lt;expression&gt;s and a &lt;eol&gt;.
     * @return <code>true</code> if a jump sequence is recognized.
     * @throws SyntaxException  If syntax error is detected
     */
    private boolean isJump() {
        if (!isKeyword("jump")) return false;
        if (!isExpression()) error("No first <expression> following 'jump'");
        if (!isExpression()) error("No second <expression> following 'jump'");
        makeTree(3, 2, 1);
        if (!isEol()) error("No <eol> at the end of jump sequence");
        return true;
    }

    /**
     * Tries to parse a sequence headed by "set";.
     * <pre>"set" &lt;variable&gt; &lt;expression&gt; &lt;eol&gt;</pre>
     * A <code>SyntaxException</code> will be thrown if a "set" is present
     * but is not followed by a &lt;variable&gt;, a &lt;expression&gt;, and a &lt;eol&gt;.
     * @return <code>true</code> if a set sequence is recognized.
     * @throws SyntaxException  If syntax error is detected
     */
    private boolean isSet() {
        if (!isKeyword("set")) return false;
        if (!isVariable()) error("No <variable> following 'set'");
        if (!isExpression()) error("No <expression> following 'set' - <variable>");
        makeTree(3, 2, 1);
        if (!isEol()) error("No <eol> at the end of set sequence");
        
        return true;
    }
    
    
    /**
     * Tries to parse a sequence headed by "repeat";.
     * <pre>"repeat" &lt;expression&gt; &lt;block&gt;</pre>
     * A <code>SyntaxException</code> will be thrown if a "repeat" is present
     * but is not followed by a &lt;expression&gt; and a &lt;block&gt;.
     * @return <code>true</code> if a repeat sequence is recognized.
     * @throws SyntaxException  If syntax error is detected
     */
    private boolean isRepeat() {
        if (!isKeyword("repeat")) return false;
        if (!isExpression()) error("No <expression> following 'repeat'");
        if (!isBlock()) error("No <block> at the end of repeat sequence");
        makeTree(3, 2, 1);
        
        return true;
    }
    
    /**
     * Tries to parse a sequence headed by "while";.
     * <pre>"while" &lt;condition&gt; &lt;block&gt;</pre>
     * A <code>SyntaxException</code> will be thrown if a "while" is present
     * but is not followed by a &lt;condition&gt; and a &lt;block&gt;.
     * @return <code>true</code> if a while sequence is recognized.
     * @throws SyntaxException  If syntax error is detected
     */
    private boolean isWhile() {
        if (!isKeyword("while")) return false;
        if (!isCondition()) error("No <condition> following 'while'");
        if (!isBlock()) error("No <block> at the end of while sequence");
        makeTree(3, 2, 1);
        
        return true;
    }
    
    /**
     * Tries to parse a sequence headed by "if";.
     * <pre>"if" &lt;condition&gt; &lt;block&gt; [ "else" &lt;block&gt; ]</pre>
     * A <code>SyntaxException</code> will be thrown if an "if" is present
     * but is not followed by the sequence specified above.
     * @return <code>true</code> if an if sequence is recognized.
     * @throws SyntaxException  If syntax error is detected
     */
    private boolean isIf() {
        if (!isKeyword("if")) return false;
        if (!isCondition()) error("No <condition> following 'if'");
        if (!isBlock()) error("No <block> following 'if'-<condition>");
        int elseCount = 0;
        if (isKeyword("else")) {
            stack.pop();
            if (!isBlock()) error("No <block> following 'else'");
            elseCount++;
        }
        int numOfIndices = 2 + elseCount; // i.e. 3 + elseCount - 1
        int[] indexArray = new int[numOfIndices]; 
        for (int i = 0, j = numOfIndices; i < numOfIndices; i++, j--) {
            indexArray[i] = j;
        }
        
        makeTree(3 + elseCount, indexArray);
        // if else count = 0 makeTree(3,2,1) 2
        // if else count = 1 makeTree(4,3,2,1) 3      
        
        return true;
    }
    
    /**
     * Tries to parse a sequence headed by "do";.
     * <pre>"do" &lt;name&gt; { &lt;expression&gt; } &lt;eol&gt;</pre>
     * A <code>SyntaxException</code> will be thrown if an "do" is present
     * but is not followed by the sequence specified above.
     * @return <code>true</code> if a do sequence is recognized.
     * @throws SyntaxException  If syntax error is detected
     */
    private boolean isDo() {
        if (!isKeyword("do")) return false;
        if (!isName()) error("No <name> following 'do'");
        int numOfExpressions = 0;
        while (isExpression()) {
            numOfExpressions++;
        }
        
        makeRootDesignatedTree("list", numOfExpressions);
        makeTree(3, 2, 1);
        if (!isEol()) error("No <eol> at the end of do sequence");
        return true;
    }
    
    /**
     * Pushes a new Tree with designated root onto the stack. If there are leaf elements
     * on the stack, those are added to the root prior to the Tree's being pushed.
     * 
     * @param root  value of the Token(value) of the Tree
     * @param numOfLeaves Number of leaves to add to the list Tree
     */   
    private void makeRootDesignatedTree(String root, int numOfLeaves) {
        stack.push(new Tree<Token>(new Token(TokenType.KEYWORD, root)));
        if (numOfLeaves == 0) {
            makeTree(1); // Tree with designated root if list is empty
            return; 
        }
        
        int[] indexArray = new int[numOfLeaves]; 
        for (int i = 0, j = numOfLeaves + 1; i < numOfLeaves; i++, j--) {
            indexArray[i] = j;
        }
        makeTree(1, indexArray); // Tree with designated root otherwise
    }

    /**
     * If the next Token has the expected type, it is used as the
     * value of a new (childless) Tree node, and that node
     * is then pushed onto the stack. If the next Token does not
     * have the expected type, this method effectively does nothing.
     * 
     * @param type The expected type of the next Token.
     * @return <code>true</code> if the next Token has the expected type.
     * @throws IOException  If an I/O error occurs 
     */
    private boolean nextTokenMatches(TokenType type) {
        if (!tokenizer.hasNext()) {
            return false;
        }
        Token t = tokenizer.next();
        if (t.getType() == type) {
            stack.push(new Tree<Token>(t));
            return true;
        }
        tokenizer.pushBack();
        return false;
    }

    /**
     * If the next Token has the expected type and value, it is used as
     * the value of a new (childless) Tree node, and that node
     * is then pushed onto the stack; otherwise, this method does
     * nothing.
     *
     * @param type The expected type of the next Token.
     * @param value The expected value of the next Token; must
     *              not be <code>null</code>.
     * @return <code>true</code> if the next Token has the expected type.
     * @throws IOException  If an I/O error occurs 
     */
    private boolean nextTokenMatches(TokenType type, String value) {
        if (!tokenizer.hasNext()) {
            return false;
        }
        Token t = tokenizer.next();
        if (type == t.getType() && value.equals(t.getValue())) {
            stack.push(new Tree<Token>(t));
            return true;
        }
        tokenizer.pushBack();
        return false;
    }

    /**
     * Assembles some number of elements from the top of the global stack
     * into a new Tree, and replaces those elements with the new Tree.<p>
     * <b>Caution:</b> The arguments must be consecutive integers 1..N,
     * in any order, but with no gaps; for example, makeTree(2,4,1,5)
     * would cause problems (3 was omitted).
     * <p>Example: If the stack contains</p><pre>
     * 1  |  A  |                                     |    B    |       
     * 2  |  B  |                                     |   / \   |
     * 3  |  C  |  then makeTree(2, 3, 1) results in  |  C   A  |
     *    | ... |                                     |   ...   |
     *    |_____|                                     |_________|
     *    </pre>
     * @param rootIndex Which stack element (counting from top=1) to use as
     * the root of the new Tree.
     * @param childIndices Which stack elements to use as the children
     * of the root.
     */    
    private void makeTree(int rootIndex, int... childIndices) {
        // Get root from stack
        Tree<Token> root = getStackItem(rootIndex);
        // Get other trees from stack and add them as children of root
        for (int i = 0; i < childIndices.length; i++) {
            root.addChildren(getStackItem(childIndices[i]));
        }
        // Pop root and all children from stack
        for (int i = 0; i <= childIndices.length; i++) {
            stack.pop();
        }
        // Put the root back on the stack
        stack.push(root);
    }
      
    /**
     * Returns, as a Tree, the nth element from the top of the
     * instance variable <code>stack</code> (the top element is 1).
     * 
     * @param n Which element of the stack, counting
     *          1 as the top element, to return.
     * @return The indicated stack element.
     */
    private Tree<Token> getStackItem(int n) {
        return stack.get(stack.size() - n);
    }

    /**
     * Utility routine to throw a <code>SyntaxException</code> with the
     * given message.
     * @param message The text to put in the <code>SyntaxException</code>.
     */
    private void error(String message) {
        if (debug) {
            System.out.println("Stack = " + stack + " <--(top)");
            for (int i = 0; i < stack.size(); i++) {
                System.out.println("Stack " + i + ":");
                System.out.println(stack.get(i));
            }
        }
        throw new SyntaxException(message + "; stack = " + stack);
    }
}
