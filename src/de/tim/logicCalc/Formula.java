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
import java.util.HashMap;

/**
 * TODO: Description
 * 
 * @author Tim Neumann
 */
public class Formula {

	private String input;
	private boolean valid = false;
	private FormulaNode root;
	//Need has map with int as key because HashSet doesn't know two variables are the same
	private HashMap<Integer, Variable> usedVars = new HashMap<>();

	private FormulaNode parseFormula(String f) {
		if (f.startsWith("!")) return (new FormulaNode(FormulaCharacter.NOT, null, parseFormula(f.substring(1))));
		if (f.startsWith("(") && f.endsWith(")")) {
			int pStat = 0;
			int pos;
			String subF = f.substring(1, f.length() - 1);
			for (pos = 0; pos < subF.length(); pos++) {
				if (subF.charAt(pos) == '(') {
					pStat++;
				}
				if (subF.charAt(pos) == ')') {
					pStat--;
				}
				if (pStat == 0) {
					if (subF.charAt(pos) == '&') return new FormulaNode(FormulaCharacter.AND, parseFormula(subF.substring(0, pos)), parseFormula(subF.substring(pos + 1)));
					if (subF.charAt(pos) == '|') return new FormulaNode(FormulaCharacter.OR, parseFormula(subF.substring(0, pos)), parseFormula(subF.substring(pos + 1)));
					if (subF.charAt(pos) == '>') return new FormulaNode(FormulaCharacter.IMPLIES, parseFormula(subF.substring(0, pos)), parseFormula(subF.substring(pos + 1)));
					if (subF.charAt(pos) == '<') return new FormulaNode(FormulaCharacter.EQUAL, parseFormula(subF.substring(0, pos)), parseFormula(subF.substring(pos + 1)));

				}
			}
		}
		Variable var = new Variable(f);
		if (var.getVar() != -1) {
			if (!this.usedVars.containsKey(new Integer(var.getVar()))) {
				this.usedVars.put(new Integer(var.getVar()), var);
			}
			return new FormulaNode(var, null, null);
		}
		System.err.println("Couldn't parse: " + f);
		this.valid = false;
		return null;
	}

	/**
	 * Creates a new formula
	 * 
	 * @param p_input
	 *            The user input string
	 */
	public Formula(String p_input) {
		this.input = p_input;
		String replacedI = p_input.replace("<->", "<");
		replacedI = replacedI.replace("->", ">");
		this.valid = true;
		this.root = parseFormula(replacedI);
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
			if (!values.containsKey(i)) throw new MalformedParametersException("Variable F" + i + " not set.");
		}
		return this.root.calculate(values);
	}

	/**
	 * @param letters
	 *            Whether to use letters for variables
	 * @return a string representation of this formula.
	 */
	public String getString(boolean letters) {
		return (this.root.getString(letters));
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
		private FormulaNode child1;
		private FormulaNode child2;

		/**
		 * Makes a new Formula Node
		 * 
		 * @param p_data
		 *            The data of the node
		 * @param p_child1
		 *            The child to the left.
		 * @param p_child2
		 *            The child to the right.
		 */
		public FormulaNode(ValidFormChar p_data, FormulaNode p_child1, FormulaNode p_child2) {
			this.data = p_data;
			this.child1 = p_child1;
			this.child2 = p_child2;
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
				return values.get(new Integer(var.getVar()));
			}
			else if (this.data instanceof FormulaCharacter) {
				FormulaCharacter c = (FormulaCharacter) this.data;
				switch (c) {
					case OR:
						return Math.max(this.child1.calculate(values), this.child2.calculate(values));
					case AND:
						return Math.min(this.child1.calculate(values), this.child2.calculate(values));
					case NOT:
						return 1 - this.child2.calculate(values);
					case IMPLIES:
						return Math.max((1 - this.child1.calculate(values)), this.child2.calculate(values));
					case EQUAL:
						if (this.child1.calculate(values) == this.child2.calculate(values)) return 1;
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
			return (this.child1 != null && this.child2 != null ? "(" : "") + (this.child1 != null ? this.child1.getString(letters) : "") + this.data.getFancy(letters) + (this.child2 != null ? this.child2.getString(letters) : "") + (this.child1 != null && this.child2 != null ? ")" : "");
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
		 * Get's {@link #child1 child1}
		 * 
		 * @return child1
		 */
		public FormulaNode getChild1() {
			return this.child1;
		}

		/**
		 * Get's {@link #child2 child2}
		 * 
		 * @return child2
		 */
		public FormulaNode getChild2() {
			return this.child2;
		}
	}

	/**
	 * All valid formula Icons other then Variables
	 * 
	 * @author Tim Neumann
	 */
	static enum FormulaCharacter implements ValidFormChar {
		/** Left bracket */
		P_LEFT("("),
		/** Right bracket */
		P_RIGHT(")"),
		/** Logical or */
		OR("|"),
		/** Logical and */
		AND("&"),
		/** Logical not */
		NOT("!"),
		/** Logical implies -> */
		IMPLIES(">", "->"),
		/** Logical equals <-> */
		EQUAL("<", "<->");

		private String icon;
		private String fancy;

		private FormulaCharacter(String p_icon) {
			this.icon = p_icon;
			this.fancy = p_icon;
		}

		private FormulaCharacter(String p_icon, String p_fancy) {
			this.icon = p_icon;
			this.fancy = p_fancy;
		}

		@Override
		public String getIcon() {
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
				if (fI.icon.equals(icon)) return fI;
			}
			return null;
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
			else if (p_var.startsWith("f")) {
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
			return "f" + this.var;
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
				char c = (char) (this.var + 96);
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
		 * @return Returns the string icon /symbol (May be multiple chars.
		 */
		public String getFancy(boolean letters);
	}
}
