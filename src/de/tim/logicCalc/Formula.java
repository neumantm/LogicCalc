/*
 * LogicCalc
 *
 * TODO: Project Beschreibung
 *
 * @author Tim Neumann
 * @version 1.0.0
 *
 */
package de.tim.logicCalc;

import java.lang.reflect.MalformedParametersException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * TODO: Description
 * 
 * @author Tim Neumann
 */
public class Formula {

	private String name;
	private String input;
	private boolean valid = false;
	private FormulaNode root;
	//Need has map with int as key because HashSet doesn't know two variables are the same
	private HashMap<Integer, Variable> usedVars = new HashMap<>();

	private FormulaNode parseFormula(String f) {
		//Process single Varibales
		Variable var = new Variable(f);
		if (var.getVar() != -1) {
			if (!this.usedVars.containsKey(new Integer(var.getVar()))) {
				this.usedVars.put(new Integer(var.getVar()), var);
			}
			return new FormulaNode(var);
		}
		//Process single formula names
		for (Formula form : Main.formulas) {
			if (form.getName().toLowerCase().equals(f)) {
				this.usedVars.putAll(form.usedVars);
				return form.root;
			}
		}

		//Process parantheses on the outside
		int paranthesCount = 1;
		if (f.startsWith("(") && f.endsWith(")")) {
			int pos = 1;
			while (paranthesCount > 0) {
				if (f.charAt(pos) == '(') {
					paranthesCount++;
				}
				else if (f.charAt(pos) == ')') {
					paranthesCount--;
				}
				pos++;
				if (pos == f.length()) return parseFormula(f.substring(1, f.length() - 1));
			}
		}

		//Process all binary operators
		FormulaCharacter lookingFor = FormulaCharacter.AND;
		ArrayList<Integer> positions = new ArrayList<>();
		paranthesCount = 0;

		for (int i = 0; i < f.length(); i++) {
			if (f.charAt(i) == '(') {
				paranthesCount++;
			}
			else if (f.charAt(i) == ')') {
				paranthesCount--;
			}
			else if (paranthesCount == 0) {
				if (positions.size() == 0) {
					if (f.charAt(i) == '&') {
						lookingFor = FormulaCharacter.AND;
						positions.add(new Integer(i));
					}
					if (f.charAt(i) == '|') {
						lookingFor = FormulaCharacter.OR;
						positions.add(new Integer(i));
					}
					if (f.charAt(i) == '>') {
						lookingFor = FormulaCharacter.IMPLIES;
						positions.add(new Integer(i));
					}
					if (f.charAt(i) == '<') {
						lookingFor = FormulaCharacter.EQUAL;
						positions.add(new Integer(i));
					}
				}
				else if (f.charAt(i) == lookingFor.getIconChar()) {
					positions.add(new Integer(i));
				}
				else if (f.charAt(i) == FormulaCharacter.AND.getIconChar() || f.charAt(i) == FormulaCharacter.OR.getIconChar() || f.charAt(i) == FormulaCharacter.IMPLIES.getIconChar() || f.charAt(i) == FormulaCharacter.EQUAL.getIconChar()) {
					//An other binary operator in the same paranthese.
					System.err.println("Couldn't parse: " + f);
					this.valid = false;
					return null;
				}
			}
		}

		if (positions.size() == 0) {
			//Process Not if no binary is found
			if (f.startsWith("!")) return (new FormulaNode(FormulaCharacter.NOT, parseFormula(f.substring(1))));

			System.err.println("Couldn't parse: " + f);
			this.valid = false;
			return null;
		}

		if (lookingFor == FormulaCharacter.IMPLIES) {
			if (positions.size() > 1) {
				System.err.println("Couldn't parse: " + f);
				this.valid = false;
				return null;
			}
			int pos = positions.get(0).intValue();
			return new FormulaNode(FormulaCharacter.IMPLIES, parseFormula(f.substring(0, pos)), parseFormula(f.substring(pos + 1)));
		}
		else if (lookingFor == FormulaCharacter.EQUAL) {
			if (positions.size() > 1) {
				System.err.println("Couldn't parse: " + f);
				this.valid = false;
				return null;
			}
			int pos = positions.get(0).intValue();
			return new FormulaNode(FormulaCharacter.EQUAL, parseFormula(f.substring(0, pos)), parseFormula(f.substring(pos + 1)));
		}
		else {
			int pos;
			int lastPos = -1;
			ArrayList<FormulaNode> subForms = new ArrayList<>();
			for (int i = 0; i < positions.size(); i++) {
				pos = positions.get(i).intValue();
				subForms.add(parseFormula(f.substring(lastPos + 1, pos)));
				lastPos = pos;
			}
			subForms.add(parseFormula(f.substring(lastPos + 1)));
			return new FormulaNode(lookingFor, subForms);
		}

		/**
		 * if (f.startsWith("(") && f.endsWith(")")) {
		 * int pStat = 0;
		 * int pos;
		 * String subF = f.substring(1, f.length() - 1);
		 * for (pos = 0; pos < subF.length(); pos++) {
		 * if (subF.charAt(pos) == '(') {
		 * pStat++;
		 * }
		 * if (subF.charAt(pos) == ')') {
		 * pStat--;
		 * }
		 * if (pStat == 0) {
		 * if (subF.charAt(pos) == '&') return new
		 * FormulaNode(FormulaCharacter.AND, parseFormula(subF.substring(0,
		 * pos)), parseFormula(subF.substring(pos + 1)));
		 * if (subF.charAt(pos) == '|') return new
		 * FormulaNode(FormulaCharacter.OR, parseFormula(subF.substring(0,
		 * pos)), parseFormula(subF.substring(pos + 1)));
		 * if (subF.charAt(pos) == '>') return new
		 * FormulaNode(FormulaCharacter.IMPLIES, parseFormula(subF.substring(0,
		 * pos)), parseFormula(subF.substring(pos + 1)));
		 * if (subF.charAt(pos) == '<') return new
		 * FormulaNode(FormulaCharacter.EQUAL, parseFormula(subF.substring(0,
		 * pos)), parseFormula(subF.substring(pos + 1)));
		 * 
		 * }
		 * }
		 * 
		 * }
		 */
	}

	/**
	 * Creates a new formula
	 * 
	 * @param p_input
	 *            The user input string
	 * @param p_name
	 *            The name of the formula
	 */
	public Formula(String p_input, String p_name) {
		this.name = p_name;
		this.input = p_input;
		String replacedI = p_input.replace("<->", "<");
		replacedI = replacedI.replace("->", ">");
		this.valid = true;
		this.root = parseFormula(replacedI);
	}

	private Formula(String p_input, String p_name, FormulaNode p_root, HashMap<Integer, Variable> p_usedVars) {
		this.name = p_name;
		this.input = p_input;
		this.valid = true;
		this.root = p_root;
		this.usedVars = p_usedVars;
	}

	/**
	 * Calculates the value of the formula vor given values.
	 * 
	 * @param values
	 *            The values of the variables (Variable -> Value)
	 * @return The result.
	 * @throws MalformedParametersException
	 *             When a needed variable is not defined.
	 */
	public int calculate(HashMap<Integer, Integer> values) throws MalformedParametersException {
		for (Integer i : this.usedVars.keySet()) {
			if (!values.containsKey(i)) throw new MalformedParametersException("Variable A" + i + " not set.");
		}
		return this.root.calculate(values);
	}

	/**
	 * @param letters
	 *            Whether to use letters for variables
	 * @return a string representation of this formula.
	 */
	public String getString(boolean letters) {
		return (this.name + "=" + this.root.getString(letters));
	}

	/**
	 * 
	 * @param letters
	 *            Whether to use letters for variables
	 * 
	 * @return The latex code for this formula
	 */
	public String getLatex(boolean letters) {
		return ("$" + this.name + "=" + this.root.getLatex(letters) + "$");
	}

	/**
	 * 
	 * @param letters
	 *            Whether to use letters for variables
	 * 
	 * @return The latex code for the syntax tree for this formula
	 */
	public String getLatexTree(boolean letters) {
		String tree = "\\begin{tikzpicture}[level/.style ={sibling distance=60mm/#1}]\n" +
				"\\node[circle,draw] ";
		tree += "{$" + this.root.getData().getLatex(letters) + "$}";
		tree += "\n";
		for (int i = 0; i < this.root.getChildren().size(); i++) {
			String[] childCont = this.root.getChildren().get(i).getLatexTreePart(letters);
			for (int j = 0; j < childCont.length; j++) {
				String line = childCont[j];
				if (i == this.root.getChildren().size() - 1 && j == childCont.length - 1) {
					tree += "    " + line + ";\n";
				}
				else {
					tree += "    " + line + "\n";
				}
			}
		}
		tree += "\\end{tikzpicture}";
		return tree;
	}

	/**
	 * Converts this formula to simple notation without implies and equals.
	 * 
	 * @param newName
	 *            The new name for the formula
	 * 
	 * @return The new Formula.
	 */
	public Formula convertToSimple(String newName) {
		FormulaNode newRoot = this.root.convertToSimple();
		HashMap<Integer, Variable> newUsedVars = new HashMap<>();
		for (Entry<Integer, Formula.Variable> e : this.usedVars.entrySet()) {
			newUsedVars.put(new Integer(e.getKey().intValue()), (Variable) e.getValue().copy());
		}

		return new Formula(this.input + " (converted)", newName, newRoot, newUsedVars);
	}

	/**
	 * Get's {@link #input input}
	 * 
	 * @return input
	 */
	public String getInput() {
		return this.input;
	}

	/**
	 * Get's {@link #valid valid}
	 * 
	 * @return valid
	 */
	public boolean isValid() {
		return this.valid;
	}

	/**
	 * Get's {@link #usedVars usedVars}
	 * 
	 * @return usedVars
	 */
	public HashMap<Integer, Variable> getUsedVars() {
		return this.usedVars;
	}

	/**
	 * A formula Node
	 * 
	 * @author Tim Neumann
	 */
	static class FormulaNode {
		private ValidFormChar data;
		private ArrayList<FormulaNode> children;

		/**
		 * Makes a new Formula Node
		 * 
		 * @param p_data
		 *            The data of the node
		 * @param p_children
		 *            The children of the Formula Node
		 */
		public FormulaNode(ValidFormChar p_data, ArrayList<FormulaNode> p_children) {
			this.data = p_data;
			this.children = p_children;
		}

		/**
		 * Makes a new Formula Node
		 * 
		 * @param p_data
		 *            The data of the node
		 * @param p_only_child
		 *            The children of the Formula Node
		 */
		public FormulaNode(ValidFormChar p_data, FormulaNode p_only_child) {
			this.data = p_data;
			this.children = new ArrayList<>();
			this.children.add(p_only_child);
		}

		/**
		 * Makes a new Formula Node without ny children
		 * 
		 * @param p_data
		 *            The data of the node
		 */
		public FormulaNode(ValidFormChar p_data) {
			this.data = p_data;
			this.children = new ArrayList<>();
		}

		/**
		 * Makes a new Formula Node
		 * 
		 * @param p_data
		 *            The data of the node
		 * @param p_child1
		 *            The children of the Formula Node
		 * @param p_child2
		 *            The children of the Formula Node
		 */
		public FormulaNode(ValidFormChar p_data, FormulaNode p_child1, FormulaNode p_child2) {
			this.data = p_data;
			this.children = new ArrayList<>();
			this.children.add(p_child1);
			this.children.add(p_child2);
		}

		/**
		 * Calculates the value of this subtree of the formula
		 * 
		 * @param values
		 *            The values of all required variables.
		 * @return The result.
		 */
		public int calculate(HashMap<Integer, Integer> values) {

			if (this.data instanceof Variable) {
				Variable var = (Variable) this.data;
				return values.get(new Integer(var.getVar())).intValue();
			}
			else if (this.data instanceof FormulaCharacter) {
				FormulaCharacter c = (FormulaCharacter) this.data;
				int res;
				switch (c) {
					case OR:
						res = 0;
						for (FormulaNode child : this.children) {
							int cCalc = child.calculate(values);
							if (cCalc > res) {
								res = cCalc;
								break;
							}
						}
						return res;
					case AND:
						res = 1;
						for (FormulaNode child : this.children) {
							int cCalc = child.calculate(values);
							if (cCalc < res) {
								res = cCalc;
								break;
							}
						}
						return res;
					case NOT:
						return 1 - this.children.get(0).calculate(values);
					case IMPLIES:
						return Math.max((1 - this.children.get(0).calculate(values)), this.children.get(1).calculate(values));
					case EQUAL:
						if (this.children.get(0).calculate(values) == this.children.get(1).calculate(values)) return 1;
						return 0;
					default:
						System.err.println("A invalud logic Operator!!! Exiting");
						System.exit(1);
				}
			}
			else {
				System.err.println("A invalud valudFormulaCharacter!!! Exiting");
				System.exit(1);
			}

			return -1;

		}

		/**
		 * @param letters
		 *            Whether to use letters for variables
		 * @return A string representation of this node and it's childs
		 */
		public String getString(boolean letters) {
			boolean needsPrantheses = (this.data == FormulaCharacter.AND) || (this.data == FormulaCharacter.OR);
			String ret = (needsPrantheses ? "(" : "");

			if (this.children.size() <= 1) {
				ret += this.data.getFancy(letters);
			}

			for (int i = 0; i < this.children.size(); i++) {
				if (i != 0) {
					ret += this.data.getFancy(letters);
				}
				ret += this.children.get(i).getString(letters);
			}
			ret += (needsPrantheses ? ")" : "");

			return ret;
		}

		/**
		 * @param letters
		 *            Whether to use letters for variables
		 * @return The latex code of this node and it's childs
		 */
		public String getLatex(boolean letters) {
			boolean needsPrantheses = (this.data == FormulaCharacter.AND) || (this.data == FormulaCharacter.OR);
			String ret = (needsPrantheses ? "(" : "");

			if (this.children.size() <= 1) {
				ret += this.data.getLatex(letters);
			}

			for (int i = 0; i < this.children.size(); i++) {
				if (i != 0) {
					ret += this.data.getLatex(letters);
				}
				ret += this.children.get(i).getLatex(letters);
			}
			ret += (needsPrantheses ? ")" : "");

			return ret;
		}

		/**
		 * @param letters
		 *            Whether to use letters for variables
		 * @return The latex code for the subtree(of the syntax tree) of this
		 *         node and it's childs
		 */
		public String[] getLatexTreePart(boolean letters) {
			ArrayList<String> ret = new ArrayList<>();
			String line1 = "child {node [circle,draw] ";
			line1 += "{$" + this.data.getLatex(letters) + "$}";

			if (this.children.size() == 0) {
				line1 += "}";
				String[] arr = new String[1];
				arr[0] = line1;
				return arr;
			}

			ret.add(line1);

			for (int i = 0; i < this.children.size(); i++) {
				String[] childCont = this.children.get(i).getLatexTreePart(letters);
				for (String s : childCont) {
					ret.add("    " + s);
				}
			}
			ret.add("}");

			return ret.toArray(new String[1]);
		}

		/**
		 * Converts this node and all children to simple noatation.
		 * 
		 * @return The new node.
		 */
		public FormulaNode convertToSimple() {
			if ((this.data != FormulaCharacter.IMPLIES && this.data != FormulaCharacter.EQUAL)) {
				ArrayList<FormulaNode> newChilds = new ArrayList<>(this.children.size());
				for (FormulaNode fN : this.children) {
					newChilds.add(fN.convertToSimple());
				}
				return new FormulaNode(this.data.copy(), newChilds);
			}
			else if (this.data == FormulaCharacter.IMPLIES) {
				FormulaNode child1 = this.children.get(0).convertToSimple();
				FormulaNode child2 = this.children.get(1).convertToSimple();
				FormulaNode notNode = new FormulaNode(FormulaCharacter.NOT, child1);
				FormulaNode newNode = new FormulaNode(FormulaCharacter.OR, notNode, child2);
				return newNode;
			}
			else {
				FormulaNode child1 = this.children.get(0).convertToSimple();
				FormulaNode child2 = this.children.get(1).convertToSimple();
				FormulaNode trueNode = new FormulaNode(FormulaCharacter.AND, child1, child2);
				FormulaNode falseNode = new FormulaNode(FormulaCharacter.AND, new FormulaNode(FormulaCharacter.NOT, child1), new FormulaNode(FormulaCharacter.NOT, child2));
				FormulaNode newNode = new FormulaNode(FormulaCharacter.OR, trueNode, falseNode);
				return newNode;
			}
		}

		/**
		 * Get's {@link #data data}
		 * 
		 * @return data
		 */
		public ValidFormChar getData() {
			return this.data;
		}

		/**
		 * Get's {@link #children children}
		 * 
		 * @return children
		 */
		public ArrayList<FormulaNode> getChildren() {
			return this.children;
		}
	}

	/**
	 * All valid formula Icons other then Variables
	 * 
	 * @author Tim Neumann
	 */
	static enum FormulaCharacter implements ValidFormChar {
		/** Left bracket */
		P_LEFT('(', "("),
		/** Right bracket */
		P_RIGHT(')', ")"),
		/** Logical or */
		OR('|', " \\lor "),
		/** Logical and */
		AND('&', " \\land "),
		/** Logical not */
		NOT('!', " \\neg "),
		/** Logical implies -> */
		IMPLIES('>', "->", " \\to "),
		/** Logical equals <-> */
		EQUAL('<', "<->", " \\leftrightarrow ");

		private char icon;
		private String fancy;
		private String latexCode;

		private FormulaCharacter(char p_icon, String p_latex) {
			this.icon = p_icon;
			this.fancy = p_icon + "";
			this.latexCode = p_latex;
		}

		private FormulaCharacter(char p_icon, String p_fancy, String p_latex) {
			this.icon = p_icon;
			this.fancy = p_fancy;
			this.latexCode = p_latex;
		}

		@Override
		public String getIcon() {
			return this.icon + "";
		}

		/**
		 * @return The icon as a char rather then a string.
		 */
		public char getIconChar() {
			return this.icon;
		}

		@Override
		public String getFancy(boolean letters) {
			return this.fancy;
		}

		/**
		 * Returns the Formula character by icon /symbol string
		 * 
		 * @param icon
		 *            The icon / symbol string
		 * @return The formula character
		 */
		public static FormulaCharacter getFromIcon(String icon) {
			for (FormulaCharacter fI : FormulaCharacter.values()) {
				if (fI.getIcon().equals(icon)) return fI;
			}
			return null;
		}

		/**
		 * @see de.tim.logicCalc.Formula.ValidFormChar#getLatex(boolean)
		 */
		@Override
		public String getLatex(boolean letters) {
			return this.latexCode;
		}

		/**
		 * @see de.tim.logicCalc.Formula.ValidFormChar#copy()
		 */
		@Override
		public ValidFormChar copy() {
			return this;
		}
	}

	/**
	 * A valid variable.
	 * 
	 * @author Tim Neumann
	 */
	static class Variable implements ValidFormChar {
		private int var;

		/**
		 * Inits a new variable
		 * 
		 * @param p_var
		 *            The value
		 */
		public Variable(int p_var) {
			this.var = p_var;
		}

		/**
		 * Inits a new variable with a string
		 * 
		 * @param p_var
		 *            The value
		 */
		public Variable(String p_var) {
			if (p_var.length() == 1 && p_var.charAt(0) > 96 && p_var.charAt(0) < 123) {
				this.var = (p_var.charAt(0) - 96);
			}
			else if (p_var.startsWith("a")) {
				try {
					this.var = Integer.parseInt(p_var.substring(1));
				} catch (NumberFormatException e) {
					this.var = -1;
				}
			}
			else {
				this.var = -1;
			}
		}

		/**
		 * @see de.tim.logicCalc.Formula.ValidFormChar#getIcon()
		 */
		@Override
		public String getIcon() {
			return "A" + this.var;
		}

		/**
		 * @see de.tim.logicCalc.Formula.ValidFormChar#getFancy()
		 */
		@Override
		public String getFancy(boolean letters) {
			return (letters ? getIconAsLetter() : getIcon());
		}

		/**
		 * @return the variable as letter (A-Z) if possible otherwise as in
		 *         getIcon()
		 */
		public String getIconAsLetter() {
			if (this.var < 27) {
				char c = (char) (this.var + 64);
				return c + "";
			}
			return getIcon();
		}

		/**
		 * Get's {@link #var var}
		 * 
		 * @return var
		 */
		public int getVar() {
			return this.var;
		}

		/**
		 * Set's {@link #var var}
		 * 
		 * @param var
		 *            var
		 */
		public void setVar(int var) {
			this.var = var;
		}

		/**
		 * @see de.tim.logicCalc.Formula.ValidFormChar#getLatex(boolean)
		 */
		@Override
		public String getLatex(boolean letters) {
			return getFancy(letters);
		}

		/**
		 * @see de.tim.logicCalc.Formula.ValidFormChar#copy()
		 */
		@Override
		public ValidFormChar copy() {
			return new Variable(this.var);
		}
	}

	/**
	 * A valid formula character
	 * 
	 * @author Tim Neumann
	 */
	static interface ValidFormChar {
		/**
		 * @return Returns the string icon /symbol
		 */
		public String getIcon();

		/**
		 * @param letters
		 *            Whether to print variables as letters
		 * @return Returns the string icon /symbol (May be multiple chars.)
		 */
		public String getFancy(boolean letters);

		/**
		 * @param letters
		 *            Whether to print variables as letters
		 * @return Returns the latex code for this icon/symbol
		 */
		public String getLatex(boolean letters);

		/**
		 * Copys it self.
		 * 
		 * @return A new ValidFormCharacter with the same content.
		 */
		public ValidFormChar copy();
	}

	/**
	 * Get's {@link #name name}
	 * 
	 * @return name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Set's {@link #name name}
	 * 
	 * @param name
	 *            name
	 */
	public void setName(String name) {
		this.name = name;
	}
}
