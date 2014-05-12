package tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import parser.Parser;
import parser.SyntaxException;
import tokenizer.Token;
import tokenizer.Tokenizer;
import tree.Tree;

/**
 * @author David Matuszek
 * @author Joopyo Hong
 * @version April 5, 2014
 */
public class ParserTest {
    private Parser parser;
    private Set<String> keywords = new HashSet<String>();

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
     // Nothing to do (yet)
    }

    /**
     * Test method for {@link parser.Parser#Parser(java.lang.String)}.
     */
    @Test
    public void testParser() {
     // Not much to test here; mostly that the Parser constructor doesn't crash
        parser = new Parser("");
        parser = new Parser("2 + 2");
    }

    /**
     * Test method for {@link parser.Parser#getTokenizer()}. 
     */
    @Test
    public void testGetTokenizer() {
        use("a a a");
        assertEquals("a", parser.getTokenizer().next().getValue());
        assertEquals("a", parser.getTokenizer().next().getValue());
        assertEquals("a", parser.getTokenizer().next().getValue());
        assertEquals(false, parser.getTokenizer().hasNext());
    }

    /**
     * Test method for {@link parser.Parser#isProgram()}. 
     */
    @Test
    public void testIsProgram() {
        use("do first \n do second 10 \n do first \n pendown \n do second w \n forward w \n left 90 \n");
        assertTrue(parser.isProgram());
        use("penup \n");
        assertTrue(parser.isProgram());
		assertStackTop(tree("program(block(penup) list)"));
        use("home \n");
        assertTrue(parser.isProgram());
		assertStackTop(tree("program(block(home) list)"));
        use("jump (7 * 8 * 8 + 9) dog * 10 + cat\n");
        assertTrue(parser.isProgram());
		assertStackTop(tree("program(block(jump(+(*(*(7 8) 8) 9) +(*(dog 10) cat))) list)"));
        use("set dog cat + mouse * (6 + 8)\n");
        assertTrue(parser.isProgram());
		assertStackTop(tree("program(block(set(dog +(cat *(mouse +(6 8))))) list)"));
        use("repeat (77 + 98 * dog){\npenup\n}\n");
        assertTrue(parser.isProgram());
		assertStackTop(tree("program(block(repeat(+(77 *(98 dog)) block(penup))) list)"));
        use("while (77) = (78){\ncolor 8 8 8\n}\n");
        assertTrue(parser.isProgram());
		assertStackTop(tree("program(block(while(=(77 78) block(color(8 8 8)))) list)"));
        use("if (77 * u + 8) = 78{\ncolor 8 8 8\n}\n");
        assertTrue(parser.isProgram());
		assertStackTop(tree("program(block(if(=(+(*(77 u) 8) 78) block(color(8 8 8)))) list)"));
        use("if 77 = 78{\ncolor 8 8 8\n}\n else {\ncolor 8 8 8\n}\n");
        assertTrue(parser.isProgram());
		assertStackTop(tree("program(block(if(=(77 78) block(color(8 8 8)) block(color(8 8 8)))) list)"));
        use("do john\n");
        assertTrue(parser.isProgram());
		assertStackTop(tree("program(block(do(john list)) list)"));
        use("do john 8 * j + 9 * 56\n");
        assertTrue(parser.isProgram());
		assertStackTop(tree("program(block(do(john list(+(*(8 j) *(9 56))))) list)"));
		
		
        use("penup \npenup \n");
        assertTrue(parser.isProgram());
		assertStackTop(tree("program(block(penup penup) list)"));
        use("home \nhome \n");
        assertTrue(parser.isProgram());
		assertStackTop(tree("program(block(home home) list)"));
        use("jump (7 * 8 * 8 + 9) dog * 10 + cat\njump (7 * 8 * 8 + 9) dog * 10 + cat\n");
        assertTrue(parser.isProgram());
		assertStackTop(tree("program(block(jump(+(*(*(7 8) 8) 9) +(*(dog 10) cat)) jump(+(*(*(7 8) 8) 9) +(*(dog 10) cat))) list)")); 
		use("repeat (77 + 98 * dog){\npenup\n}\nwhile (77) = (78){\ncolor 8 8 8\n}\n");
        assertTrue(parser.isProgram());
		assertStackTop(tree("program(block(repeat(+(77 *(98 dog)) block(penup)) while(=(77 78) block(color(8 8 8)))) list)"));       
		use("while (77) = (78){\ncolor 8 8 8\n}\nif (77 * u + 8) = 78{\ncolor 8 8 8\n}\n");
        assertTrue(parser.isProgram());
		assertStackTop(tree("program(block(while(=(77 78) block(color(8 8 8))) if(=(+(*(77 u) 8) 78) block(color(8 8 8)))) list)")); 
		use("if (77 * u + 8) = 78{\ncolor 8 8 8\n}\nif 77 = 78{\ncolor 8 8 8\n}\n else {\ncolor 8 8 8\n}\n");
        assertTrue(parser.isProgram());
		assertStackTop(tree("program(block(if(=(+(*(77 u) 8) 78) block(color(8 8 8))) if(=(77 78) block(color(8 8 8)) block(color(8 8 8)))) list)"));
		use("if 77 = 78{\ncolor 8 8 8\n}\n else {\ncolor 8 8 8\n}\ndo john\n");
        assertTrue(parser.isProgram());
		assertStackTop(tree("program(block(if(=(77 78) block(color(8 8 8)) block(color(8 8 8))) do(john list)) list)"));
		use("do john\ndo john 8 * j + 9 * 56\n");
        assertTrue(parser.isProgram());
		assertStackTop(tree("program(block(do(john list) do(john list(+(*(8 j) *(9 56))))) list)"));
		use("do john 8 * j + 9 * 56\npenup \n");
        assertTrue(parser.isProgram());
		assertStackTop(tree("program(block(do(john list(+(*(8 j) *(9 56)))) penup) list)"));
        
    }

    /**
     * Test method for {@link parser.Parser#isCommand()}. 
     */
    @Test
    public void testIsCommand() {
        use("penup\n");
        assertTrue(parser.isCommand());
        assertStackTop(tree("penup"));
        use("home \n");
        assertTrue(parser.isCommand());
        assertStackTop(tree("home"));
        use("jump (7 * 8 * 8 + 9) dog * 10 + cat\n");
        assertTrue(parser.isCommand());
        assertStackTop(tree("jump(+(*(*(7 8) 8) 9) +(*(dog 10) cat))"));
        use("set dog cat + mouse * (6 + 8)\n");
        assertTrue(parser.isCommand());
        assertStackTop(tree("set(dog +(cat *(mouse +(6 8))))"));
        use("repeat (77 + 98 * dog){\npenup\n}\n");
        assertTrue(parser.isCommand());
        assertStackTop(tree("repeat(+(77 *(98 dog)) block(penup))"));
        use("while (77) = (78) {\nred\n}\n");
        assertTrue(parser.isCommand());
        assertStackTop(tree("while(=(77 78) block(color(255 0 0)))"));
        use("if (77 * u + 8) = 78{\ncolor 8 8 8\n}\n");
        assertTrue(parser.isCommand());
        assertStackTop(tree("if(=(+(*(77 u) 8) 78) block(color(8 8 8)))"));
        use("if 77 = 78{\ncolor 8 8 8\n}\n else {\ncolor 8 8 8\n}\n");
        assertTrue(parser.isCommand());
        assertStackTop(tree("if(=(77 78) block(color(8 8 8)) block(color(8 8 8)))"));
        use("do john\n");
        assertTrue(parser.isCommand());
        assertStackTop(tree("do(john list)"));
        use("do john 8 * j + 9 * 56 13\n");
        assertTrue(parser.isCommand());
        assertStackTop(tree("do(john list(+(*(8 j) *(9 56)) 13))"));
    }
    /**
     * Test method for {@link parser.Parser#isProcedure()}. 
     */
    @Test
    public void testIsProcedure() {
        try {
            use("def jhkh k786 {\n}");
            parser.isProcedure();
            assert false;
        } catch (SyntaxException e) {
        }
        
        try {
            use("def 7786 {\n penup \n}\n");
            parser.isProcedure();
            assert false;
        } catch (SyntaxException e) {
        }
        
        use("def jhkh k786 {\n}\n");
        assertTrue(parser.isProcedure());
        assertStackTop(tree("def(header(jhkh list(k786)) block)"));
        use("def jhkh k786 {\n penup \n}\n");
        assertTrue(parser.isProcedure());
        assertStackTop(tree("def(header(jhkh list(k786)) block(penup))"));
        use("def jhkh k786 k785 {\n color dog dog dog\n}\n 8");
        assertTrue(parser.isProcedure());
        assertStackTop(tree("def(header(jhkh list(k786 k785)) block(color(dog dog dog)))"));

    }



    /**
     * Test method for {@link parser.Parser#isMove()}. 
     */
    @Test
    public void testIsMove() {
        use("forward right left face");
        assertTrue(parser.isMove());
        assertStackTop(tree("forward"));
        assertTrue(parser.isMove());
        assertStackTop(tree("right"));
        assertTrue(parser.isMove());
        assertStackTop(tree("left"));
        assertTrue(parser.isMove());
        assertStackTop(tree("face"));
    }

    /**
     * Test method for {@link parser.Parser#isColor()}. 
     */
    @Test
    public void testIsColor() {
        use("red orange magenta pink");
        
        assertTrue(parser.isColor());
        assertStackTop(tree("color(255 0 0)"));    
        assertTrue(parser.isColor());
        assertStackTop(tree("color(255 128 0)"));
        assertTrue(parser.isColor());
        assertStackTop(tree("color(255 0 255)"));
        assertTrue(parser.isColor());
        assertStackTop(tree("color(250 175 190)"));
        
        use("color dog 10 * 6 + 7 51 nocolor");
        assertTrue(parser.isColor());
        assertStackTop(tree("color(dog +(*(10 6) 7) 51)"));
        assertFalse(parser.isColor());
        
        
    }

    /**
     * Test method for {@link parser.Parser#isBlock()}.
     */
    @Test
    public void testIsBlock() throws IOException{
        
        try {
            use("{penup\n}\n");
            parser.isBlock();
            assert false;
        } catch (SyntaxException e) {
        }
        
        try {
            use("{\npenup\n}");
            parser.isBlock();
            assert false;
        } catch (SyntaxException e) {
        }

        use("{\n}\n");
        assertTrue(parser.isBlock());
        assertStackTop(tree("block"));
        use("{\npenup\n}\n");
        assertTrue(parser.isBlock());
        assertStackTop(tree("block(penup)"));
        use("{\nhome\n}\n");
        assertTrue(parser.isBlock());
        assertStackTop(tree("block(home)"));
        use("{\nforward dog\n}\n");
        assertTrue(parser.isBlock());
        assertStackTop(tree("block(forward(dog))"));
        use("{\nforward + dog * cat + dog\n}\n");
        assertTrue(parser.isBlock());
        assertStackTop(tree("block(forward(+(*(+(dog) cat) dog)))"));
        use("{\nforward + (dog * cat + dog)\n}\n");
        assertTrue(parser.isBlock());
        assertStackTop(tree("block(forward(+(+(*(dog cat) dog)))"));

        
        use("{\npenup\n}\ncat");
        assertTrue(parser.isBlock());
        assertFalse(parser.isBlock());
        unconsumedTokensShouldBe("cat");
    }

    /**
     * Test method for {@link parser.Parser#isCondition()}. 
     */
    @Test
    public void testIsCondition() {
        use("+dog = -cat");
        assertTrue(parser.isCondition());
        assertStackTop(tree("=(+(dog) -(cat)"));
        use("787 * 87 + 6 > 87 * 76 + 9 * 0");
        assertTrue(parser.isCondition());
        assertStackTop(tree(">(+(*(787 87) 6) +(*(87 76) *(9 0)))"));
        use("def778 = + ((((87 * 6 + 9))))");
        assertTrue(parser.isCondition());
        assertStackTop(tree("=(def778 +(+(*(87 6) 9)))"));

    }

    /**
     * Test method for {@link parser.Parser#isExpression()}. 
     */
    @Test
    public void testIsExpression() {
        Tree<Token> expected;
        
        use("250");
        assertTrue(parser.isExpression());
        assertStackTop(tree("250"));
        
        use("hello");
        assertTrue(parser.isExpression());
        assertStackTop(tree("hello"));

        use("(xyz + 3)");
        assertTrue(parser.isExpression());
        assertStackTop(tree("+(xyz 3)"));

        use("a + b + c");
        assertTrue(parser.isExpression());
        assertStackTop(tree("+(+(a b) c)"));

        use("3 * 12 - 7");
        assertTrue(parser.isExpression());
        assertStackTop(tree("-(*(3 12) 7)"));

        use("12 * 5 - 3 * 4 / 6 + 8");
        assertTrue(parser.isExpression());
        expected = tree("+( -(*(12 5) /(*(3 4) 6)) 8)");
        assertStackTop(expected);
                     
        use("12 * ((5 - 3) * 4) / 6 + (8)");
        assertTrue(parser.isExpression());
        expected = tree("+(/(*(12 *(-(5 3) 4)) 6) 8)");
        assertStackTop(expected);
        
        use("");
        assertFalse(parser.isExpression());
        
        use("#");
        assertFalse(parser.isExpression());
    }

    /**
     * Test method for {@link parser.Parser#isExpression()}. 
     */
    @Test(expected=RuntimeException.class)
    public void testIsBadExpression1() {
        use("17 +");
        parser.isExpression();
    }
    
    /**
     * Test method for {@link parser.Parser#isExpression()}. 
     */
    @Test(expected=RuntimeException.class)
    public void testIsBadExpression2() {
        use("22 *");
        parser.isExpression();
    }
 
    /**
     * Test method for {@link parser.Parser#isTerm()}. 
     */
    @Test
    public void testUnaryMinusBeforeFactor() {
        use("-a + b");
        assertTrue(parser.isExpression());
        assertStackTop(tree("+(-(a) b"));
        
        use("-a * b + c");
        assertTrue(parser.isExpression());
        assertStackTop(tree("+(*(-(a) b) c)"));
    }
    
    /**
     * Test method for {@link parser.Parser#isTerm()}. 
     */
    @Test
    public void testUnaryMinusInsideParentheses() {
        use("(-foo + 3) / bar");
        assertTrue(parser.isExpression());
        assertStackTop(tree("/(+(-(foo) 3) bar)"));
    }

    /**
     * Test method for {@link parser.Parser#isTerm()}. 
     */
    @Test
    public void testIsTerm() {
        use("3*12");
        assertTrue(parser.isTerm());
        assertStackTop(tree("*(3 12)"));

        use("-3*12");
        assertTrue(parser.isTerm());
        assertStackTop(tree("*(-(3) 12)"));
    }

    /**
     * Test method for {@link parser.Parser#isUnsignedTerm()}. 
     */
    @Test
    public void testIsUnsignedTerm() {
        use("12");
        assertTrue(parser.isUnsignedTerm());
        assertStackTop(tree("12"));

        use("3*12");
        assertTrue(parser.isUnsignedTerm());
        assertStackTop(tree("*(3 12)"));

        use("u * v * z");
        assertTrue(parser.isUnsignedTerm());
        assertStackTop(tree("*(*(u v) z)"));
        
        use("20 * 3 / 4");
        assertTrue(parser.isUnsignedTerm());
        assertStackTop(tree("/(*(20 3) 4)"));

        use("20 * 3 / 4 + 5");
        assertTrue(parser.isUnsignedTerm());
        assertStackTop(tree("/(*(20 3) 4)"));
        unconsumedTokensShouldBe("+ 5");
        
        use("");
        assertFalse(parser.isUnsignedTerm());
        unconsumedTokensShouldBe("");
        
        use("#");
        assertFalse(parser.isUnsignedTerm());
        unconsumedTokensShouldBe("#");
    }

    /**
     * Test method for {@link parser.Parser#isUnsignedFactor()}. 
     */
    @Test
    public void testIsUnsignedFactor() {
        use("12");
        assertTrue(parser.isUnsignedFactor());
        assertStackTop(tree("12"));

        use("hello");
        assertTrue(parser.isUnsignedFactor());
        assertStackTop(tree("hello"));
        
        use("(xyz + 3)");
        assertTrue(parser.isUnsignedFactor());
        assertStackTop(tree("+(xyz 3)"));
        
        use("12 * 5");
        assertTrue(parser.isUnsignedFactor());
        assertStackTop(tree("12"));
        unconsumedTokensShouldBe("* 5");
        
        use("17 +");
        assertTrue(parser.isUnsignedFactor());
        assertStackTop(tree("17"));
        unconsumedTokensShouldBe("+");

        use("");
        assertFalse(parser.isUnsignedFactor());
        unconsumedTokensShouldBe("");
        
        use("getX");
        assertTrue(parser.isUnsignedFactor());
        assertStackTop(tree("getX"));
        
        use("getY");
        assertTrue(parser.isUnsignedFactor());
        assertStackTop(tree("getY"));
        
        use("#");
        assertFalse(parser.isUnsignedFactor());
        unconsumedTokensShouldBe("#");
    }

    /**
     * Test method for {@link parser.Parser#isFactor()}. 
     */
    @Test
    public void testIsFactor() {
        use("12");
        assertTrue(parser.isFactor());
        assertStackTop(tree("12"));

        use("hello");
        assertTrue(parser.isFactor());
        assertStackTop(tree("hello"));
        
        use("-12");
        assertTrue(parser.isFactor());
        assertStackTop(tree("-(12)"));

        use("-hello");
        assertTrue(parser.isFactor());
        assertStackTop(tree("-(hello)"));
    }

    /**
     * Test method for {@link parser.Parser#isComparator()}. 
     */
    @Test
    public void testIsComparator() {
        
        use("< > - <");
        assertTrue(parser.isComparator());
        assertStackTop(tree("<"));
        assertTrue(parser.isComparator());
        assertStackTop(tree(">"));
        assertFalse(parser.isComparator());
        assertStackTop(tree(">"));
        unconsumedTokensShouldBe("- <");
    }

    /**
     * Test method for {@link parser.Parser#isAddOperator()}. 
     */
    @Test
    public void testIsAddOperator() {
        use("+ - + $");
        assertTrue(parser.isAddOperator());
        assertStackTop(tree("+"));
        assertTrue(parser.isAddOperator());
        assertStackTop(tree("-"));
        assertTrue(parser.isAddOperator());
        assertFalse(parser.isAddOperator());
        unconsumedTokensShouldBe("$");
    }

    /**
     * Test method for {@link parser.Parser#isMultiplyOperator()}. 
     */
    @Test
    public void testIsMultiplyOperator() {
        use("* / $");
        assertTrue(parser.isMultiplyOperator());
        assertTrue(parser.isMultiplyOperator());
        assertFalse(parser.isMultiplyOperator());
        unconsumedTokensShouldBe("$");
    }

    /**
     * Test method for {@link parser.Parser#isVariable()}. 
     */
    @Test
    public void testIsVariable() {
        use("hello   list abc123 header _");
        assertTrue(parser.isVariable());
        assertTrue(parser.isVariable());
        assertTrue(parser.isVariable());
        assertTrue(parser.isVariable());
        assertTrue(parser.isVariable());
    }

    /**
     * Test method for {@link parser.Parser#isEol()}. 
     */
    @Test
    public void testIsEol() {
        use("+\n+");
        assertTrue(parser.isAddOperator());
        assertTrue(parser.isEol());
        assertStackTop(tree("+"));
        unconsumedTokensShouldBe("+");
        
        
        use("+\n\n\n+");
        assertTrue(parser.isAddOperator());
        assertTrue(parser.isEol());
        assertStackTop(tree("+"));
        unconsumedTokensShouldBe("+");
    }
    
//  ----- "Helper" methods
    
    /**
     * The "isX" Parser methods try to recognize an X at the beginning
     * of the input, but should not consume tokens after the X. For example,
     * a factor may contain multiplications but not additions, so given a
     * string such as "3*x+5*y", the isFactor method should consume and
     * accept the "3*x" but leave the "+5*y" for later.
     * <p>
     * This method tests the input string to see whether the correct tokens
     * are left unconsumed. 
     * 
     * @param expectedTokens The following Tokens that should remain in the input
     *        string after the X has been consumed. 
     */
    private void unconsumedTokensShouldBe(String expectedTokens) {
        Token expectedToken;
        Token actualToken;
        
        Tokenizer unconsumedTokens = parser.getTokenizer();
        Tokenizer expected =
                new Tokenizer(new StringReader(expectedTokens), keywords);

        try {
            while (expected.hasNext()) {
                expectedToken = expected.next();
                assertTrue(unconsumedTokens.hasNext());
                actualToken = unconsumedTokens.next();
                assertEquals(expectedToken, actualToken);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Asserts that the parameter is equal to the top of the stack.
     * 
     * @param t The Tree to compare against the top of the stack.
     */
    private void assertStackTop(Tree<Token> t) {
        assertEquals(t, parser.stack.peek());
    }
    
    /**
     * Creates a Tree of Tokens from a String.
     * 
     * @param description The string representation of the Tree
     * (Note: Cannot include parentheses as values.)
     * 
     * @return A Tree of Tokens. 
     */
    private Tree<Token> tree(String description) {
        Tree<String> treeOfStrings = Tree.parse(description);
        return convertToTreeOfTokens(treeOfStrings);
    }
    
    /**
     * Creates a Tree of Tokens from a Tree of Strings. The given
     * Tree of Strings is unchanged.
     * 
     * @param tree The tree to be translated.
     * @return The resultant tree of tokens. 
     */
    private Tree<Token> convertToTreeOfTokens(Tree<String> tree) {
        Tree<Token> root = new Tree<Token>(makeOneToken(tree.getValue()));

        Iterator<Tree<String>> iter = tree.children();
        
        while (iter.hasNext()) {
            Tree<String> child = iter.next();
            root.addChildren(convertToTreeOfTokens(child));
        }
        return root;
    }
    
    /**
     * Returns a single token containing the given string.
     * 
     * @param word The thing to be turned into a Token.
     * @return The corresponding Token. 
     */
    private Token makeOneToken(String word) {
        Tokenizer tokenizer = new Tokenizer(new StringReader(word), keywords);;
        return tokenizer.next();
    }

    /**
     * 
     * @param s The string to be parsed.
     */
    private void use(String s) {
        parser = new Parser(s);
    }

}
