package interpreter;

import java.awt.Color;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import tree.Tree;
import tokenizer.Token;
import tokenizer.TokenType;

/**
 * @author Joopyo Hong
 * @version April 12, 2014
 */
public class Interpreter {
    private Turtle turtle;
    private DrawingArea canvas;
    private Hashtable<String, Double> globalVariables;
    private Map<String, Tree<Token>> procedures;
    private Stack<Hashtable<String, Double>> stackFrames;
    boolean running = true;

    /**
     * Creates an Interpreter.
     * 
     * @param canvas The area on which to do the drawing.
     */
    public Interpreter(DrawingArea canvas) {
        this.canvas = canvas;
        turtle = new Turtle(canvas);
        globalVariables = new Hashtable<String, Double>();
        procedures = new HashMap<String, Tree<Token>>();
    }
    
    /**
     * Erases the canvas and initializes or re-initializes all values.
     */
    void initialize() {
        canvas.setBackground(Color.WHITE);
        turtle.home();
        globalVariables.clear();
        procedures.clear();
        stackFrames = new Stack<Hashtable<String, Double>>();
        stackFrames.push(globalVariables);
    }

    /**
     * Interprets the tree rooted at the given node.
     * 
     * @param node The root of the tree to be interpreted.
     */
    void interpret(Tree<Token> node) {
        if (!running) return;
        if (node == null)
            return;
        
        String command = getStringFrom(node);
        
        switch (command) {
            case "program": {
                childrenCheck(node, 2);
                findAllProcedures(node.child(1));
                interpret(node.child(0));
                break;
            }
                
            case "block": {
                Iterator<Tree<Token>> children = node.children();
                while (children.hasNext() == true) {
                    interpret(children.next());
                }
                break;
            }
                
            case "forward": {
                childrenCheck(node, 1);
                double distance = evaluateExpression(node.child(0));
                turtle.forward(distance);
                break;
            }
                
            case "right": {
                childrenCheck(node, 1);
                double turnDegree = evaluateExpression(node.child(0));
                turtle.right(turnDegree);
                break;
            }
            
            case "left": {
                childrenCheck(node, 1);
                double turnDegree = evaluateExpression(node.child(0));
                turtle.left(turnDegree);
                break;
            }
                
            case "face": {
                childrenCheck(node, 1);
                double newDegree = evaluateExpression(node.child(0));
                turtle.setDegrees(-newDegree);
                break;
            }
            
            case "penup": {
                childrenCheck(node, 0);
                turtle.penup();
                break;
            }
            
            case "pendown": {
                childrenCheck(node, 0);
                turtle.pendown();
                break;
            }
            
            case "home": {
                childrenCheck(node, 0);
                turtle.home();
                break;
            }
            
            case "color": {
                childrenCheck(node, 3);
                double firstRGB = evaluateExpression(node.child(0));
                double secondRGB = evaluateExpression(node.child(1));
                double thirdRGB = evaluateExpression(node.child(2));
               
                
                if (firstRGB > 255 || secondRGB > 255 || thirdRGB > 255 ||
                        firstRGB < 0 || secondRGB < 0 || thirdRGB < 0) {
                    error("R, g, or b are outside of the range 0 to 255, inclusive.");
                }
                
                turtle.color((int)firstRGB, (int)secondRGB, (int)thirdRGB);
                break;
            }
            
            case "jump": {
                childrenCheck(node, 2);
                double x = evaluateExpression(node.child(0));
                double y = evaluateExpression(node.child(1));
                
                turtle.jump(x,y);
                break;
            }
            
            case "set": {
                childrenCheck(node, 2);
                String variable = getStringFrom(node.child(0));
                double value = evaluateExpression(node.child(1));                
                store(variable, value);
                break;
            }
            
            case "repeat": {
                childrenCheck(node, 2);
                double preCast = evaluateExpression(node.child(0));
                int postCast = (int) preCast;
                if ((double)postCast != preCast)
                    error("Expression following repeat must evalulate to an int value.");
                
                for (int i = 0; i < postCast; i++) {
                    interpret(node.child(1));
                }
                break;
            }
            
            case "while": {
                childrenCheck(node, 2);
                while (evaluateCondition(node.child(0))) {
                    interpret(node.child(1));
                }
                break;
            }
            
            case "if": {
                if (node.numberOfChildren() == 2) {
                    if (evaluateCondition(node.child(0))) {
                        interpret(node.child(1));
                    }
                } else if (node.numberOfChildren() == 3) {
                    if (evaluateCondition(node.child(0))) {
                        interpret(node.child(1));
                    } else interpret(node.child(2));
                } else {
                    error("Tree that represents 'if' must have either 2 or 3 children.");
                }
                    
                break;
            }
            
            case "do": {
                childrenCheck(node, 2);
                callProcedure(getStringFrom(node.child(0)), node.child(1));
                break;
                
            }
            
            default: {
                error("Unimplemented command:\n" + command);
            }
        }              
       
    }
    
    /**
     * Tells the turtle to pause.
     */
    void pauseTurtle() {
        turtle.setPaused(true);
    }
    
    /**
     * Tells the turtle to resume after being paused.
     */
    void resumeTurtle() {
        turtle.setPaused(false);
    }
    
    /**
     * Tells the turtle to set its speed.
     * 
     * @param speedControlValue The new speed (0 to 100).
     */
    void setTurtleSpeed(int speedControlValue) {
        turtle.setSpeed(speedControlValue);
    }
    
    /**
     * Checks whether the number of children is correct for the given root.
     * 
     * @param node Root of the Tree to examine
     * @param rightNumOfChildren The correct number of children for the given root
     */
    void childrenCheck(Tree<Token> node, int rightNumOfChildren) {
        if (node.numberOfChildren() != rightNumOfChildren)
            error("Tree that represents '" + getStringFrom(node) + "' must have " + 
        rightNumOfChildren + " children.");
    }
    
    /**
     * Checks whether the number of children is correct for the given root.
     * 
     * @param node Root of the Tree to examine
     * @param rightNumOfChildren The correct number of children for the given root
     */
    boolean childrenCheckBool(Tree<Token> node, int rightNumOfChildren) {
        return (node.numberOfChildren() == rightNumOfChildren);
    }
    
    /**
     * Given the root of a Tree representing an expression (which may
     * be a simple variable or number, or something much more complex),
     * evaluate the Tree and return the computed value.
     * 
     * @param node The root of the expression Tree.
     * @return The value of the expression.
     */
    private double evaluateExpression(Tree<Token> node) {
        String root = getStringFrom(node);
        // check children's # as well so as to bar away unary cases where +/- is merely a sign 
        if (root.equals("+") && childrenCheckBool(node, 2)) {
            if (getStringFrom(node.child(0)) == "+" || getStringFrom(node.child(0)) == "-") {
                return evaluateExpression(node.child(0)) + evaluateUnsignedTerm(node.child(1));
            } else {
                return evaluateTerm(node.child(0)) + evaluateUnsignedTerm(node.child(1));
            }
        } else if (root.equals("-") && childrenCheckBool(node, 2)) {
            if (getStringFrom(node.child(0)) == "+" || getStringFrom(node.child(0)) == "-") {
                return evaluateExpression(node.child(0)) - evaluateUnsignedTerm(node.child(1));
            } else {
                return evaluateTerm(node.child(0)) - evaluateUnsignedTerm(node.child(1));
            }
        } else {
            return evaluateTerm(node);
        }

    }
    
    /**
     * Given the root of a Tree representing an unsigned term (which may
     * be a simple variable or number, or something much more complex),
     * evaluate the Tree and return the computed value.
     * 
     * @param node The root of the expression Tree.
     * @return The value of the unsigned term.
     */
    private double evaluateUnsignedTerm(Tree<Token> node) {
        String root = getStringFrom(node);
        if (root.equals("*")) {
            if (getStringFrom(node.child(0)) == "*" || getStringFrom(node.child(0)) == "/") {
                return evaluateUnsignedTerm(node.child(0)) * evaluateFactor(node.child(1));
            } else {
                return evaluateUnsignedFactor(node.child(0)) * evaluateFactor(node.child(1));
            }
        } else if (root.equals("/")) {
            if (getStringFrom(node.child(0)) == "*" || getStringFrom(node.child(0)) == "/") {
                return evaluateUnsignedTerm(node.child(0)) / evaluateFactor(node.child(1));
            } else {
                return evaluateUnsignedFactor(node.child(0)) / evaluateFactor(node.child(1));
            }
        } else {
            return evaluateTerm(node);
        }
    }

    /**
     * Given the root of a Tree representing a term (which may
     * be a simple variable or number, or something much more complex),
     * evaluate the Tree and return the computed value.
     * 
     * @param node The root of the expression Tree.
     * @return The value of the term.
     */
    private double evaluateTerm(Tree<Token> node) {
        String root = getStringFrom(node);
        if (root.equals("*")) {
            if (getStringFrom(node.child(0)) == "*" || getStringFrom(node.child(0)) == "/") {
                return evaluateTerm(node.child(0)) * evaluateFactor(node.child(1));
            } else {
                return evaluateFactor(node.child(0)) * evaluateFactor(node.child(1));
            }
        } else if (root.equals("/")) {
            if (getStringFrom(node.child(0)) == "*" || getStringFrom(node.child(0)) == "/") {
                return evaluateTerm(node.child(0)) / evaluateFactor(node.child(1));
            } else {
                return evaluateFactor(node.child(0)) / evaluateFactor(node.child(1));
            }
        } else {
            return evaluateFactor(node);
        }
    }
    
    /**
     * Given the root of a Tree representing a factor (which may
     * be a simple variable or number, or something much more complex),
     * evaluate the Tree and return the computed value.
     * 
     * @param node The root of the expression Tree.
     * @return The value of the factor.
     */
    private double evaluateFactor(Tree<Token> node) {
        String root = getStringFrom(node);
        // check children's # as well so as to bar away unsigned factor that has +/- operator as its root

        if (root.equals("+") && childrenCheckBool(node, 1)) {
            childrenCheck(node, 1);
            return evaluateUnsignedFactor(node.child(0));
        } else if (root.equals("-") && childrenCheckBool(node, 1)) {
            childrenCheck(node, 1);
            return -(evaluateUnsignedFactor(node.child(0)));
        } else {
            return evaluateUnsignedFactor(node);
        }
    }
    
    /**
     * Given the root of a Tree representing an unsigned factor (which may
     * be a simple variable or number, or something much more complex),
     * evaluate the Tree and return the computed value.
     * 
     * @param node The root of the expression Tree.
     * @return The value of the unsigned factor.
     */
    private double evaluateUnsignedFactor(Tree<Token> node) {
        String root = getStringFrom(node);
        if (root.equals("getX")) {
            return turtle.getX();
        } else if (root.equals("getY")) {
            return turtle.getY();
        } else if (node.getValue().getType() == TokenType.NAME) {
            return fetch(root);
        } else if (node.getValue().getType() == TokenType.NUMBER) {
            return Float.parseFloat(root);
        } else {
            return evaluateExpression(node);
        }
    }
    
    /**
     * Given the root of a Tree representing a condition,
     * evaluate the Tree and return the result.
     * 
     * @param node The root of the condition Tree.
     * @return The value (true or false) of the condition.
     */
    private boolean evaluateCondition(Tree<Token> node) {
        childrenCheck(node, 2);
             
        String comparator = getStringFrom(node);
        double left = evaluateExpression(node.child(0));
        double right = evaluateExpression(node.child(1));
        
        if (comparator.equals(">")) {
            return left > right;
            
        } else if (comparator.equals("<")) {
            return left < right;
            
        } else if (comparator.equals("=")) {
            return left == right;
            
        } else {
            error("Comparator was not found at the root of the Tree that represents condition.");
            return false;
        }
        
    }

    /**
     * Given the name of a variable, return its value. It is an error
     * if the variable has not been given a value.
     * 
     * @param name A named variable.
     * @return The value of that variable.
     */
    private double fetch(String name) {
        Hashtable<String, Double> localVar = stackFrames.peek();
        Double value = null;
        
        if (localVar.containsKey(name)) {
            value = localVar.get(name);
        } else if (globalVariables.containsKey(name)) {
            value = globalVariables.get(name);
        } else {
            error("Variable was undefined.");
            
        }
            
        return value;
    }

    /**
     * Stores the given value in the named variable. If the variable
     * does not previously exist, it is created.
     * 
     * @param name The variable whose value is to be changed.
     * @param value The value to be given to the variable.
     */
    private void store(String name, double value) {
        Hashtable<String, Double> localVar = stackFrames.peek();
        if (localVar.containsKey(name)) {
            localVar.put(name, value);
        } else if (globalVariables.containsKey(name)) {
            globalVariables.put(name, value);
        } else {
            localVar.put(name, value);
        }
    }

    /**
     * Does all the work required to call a Logo procedure. Specifically, it:
     * <ul><li>Uses the procedure name to find its procedure body,</li>
     *     <li>Creates a HashMap for the procedure's local variable,</li>
     *     <li>Pushes the HashMap onto the stack of HashMaps,</li>
     *     <li>Evaluates each actual parameter, and store the value in
     *         the HashMap for the corresponding formal parameter,</li>
     *     <li>Interprets the procedure body, and</li>
     *     <li>Pops the procedure's HashMap from the stack of HashMaps.</li>
     * </ul>
     * @param name The name of the procedure to be interpreted.
     * @param actualParameterListNode The list of actual parameters.
     */
    private void callProcedure(String name, Tree<Token> actualParameterListNode) {
        Tree<Token> def = procedures.get(name);
        Tree<Token> parameterListNode = def.child(0).child(1);
        
        if (parameterListNode.numberOfChildren() != actualParameterListNode.numberOfChildren())
            error("Incorrect number of parameters for \"" + name + "\".");
        
        Hashtable<String, Double> localVariable = new Hashtable<String, Double>();
        
        for (int i = 0; i < parameterListNode.numberOfChildren(); i++) {
            String param = getStringFrom(parameterListNode.child(i));
            double arg = evaluateExpression(actualParameterListNode.child(i));
            
            localVariable.put(param, arg);
        }
        
        stackFrames.push(localVariable);
        interpret(def.child(1));
        stackFrames.pop();
    }

    /**
     * Finds all the procedures in the parse tree and puts references to them in
     * the global variable <code>procedures</code>.
     * 
     * @param listOfProcedures
     *            The root of the tree of procedures.
     */
    private void findAllProcedures(Tree<Token> listOfProcedures) {
        Iterator<Tree<Token>> children = listOfProcedures.children();
        
        while (children.hasNext()) {
            Tree<Token> def = children.next();
            String name = getStringFrom(def.child(0).child(0));
            procedures.put(name, def);
        }
    }

    /**
     * Returns the String in the Token in the given node.
     * 
     * @param node
     *            The Tree<Token> from which to extract the String.
     * @return The String value in this Tree<Token> node.
     */
    private static String getStringFrom(Tree<Token> node) {
        return node.getValue().getValue();
    }

    /**
     * Throws a RuntimeException containing a message.
     * @param string The message to be displayed.
     */
    private void error(String string) {
        throw new RuntimeException(string);
    }
}
