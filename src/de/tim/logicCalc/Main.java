package de.tim.logicCalc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

/**
 * TODO: Description
 * 
 * @author Tim Neumann
 */
public class Main {

	private static HashSet<Formula.Variable> usedVariables = new HashSet<>();
	private static ArrayList<Formula> formulas = new ArrayList<>();
	private static Scanner s;

	/*	private static boolean validateFormula(String f) {
	
			
					Matcher mAnd = Pattern.compile("\\A\\((.*)&(.*)\\)\\Z").matcher(f);
					Matcher mOr = Pattern.compile("\\A\\((.*)\\|(.*)\\)\\Z").matcher(f);
					Matcher mNot = Pattern.compile("\\A!(.*)\\Z").matcher(f);
					Matcher mImp = Pattern.compile("\\A(.*)->(.*)\\Z").matcher(f);
					Matcher mEq = Pattern.compile("\\A(.*)<->(.*)\\Z").matcher(f);
					if (mAnd.matches())
						return "(" + Main.validateFormula(mAnd.group(1)) + "&" + Main.validateFormula(mAnd.group(2)) + ")";
					if (mOr.matches())
						return "(" + Main.validateFormula(mOr.group(1)) + "|" + Main.validateFormula(mOr.group(2)) + ")";
					if (mNot.matches())
						return "!" + Main.validateFormula(mNot.group(1));
					if (mImp.matches())
						return "(!" + Main.validateFormula(mImp.group(1)) + "|" + Main.validateFormula(mImp.group(2)) + ")";
					if (mEq.matches())
						return "((" + Main.validateFormula(mEq.group(1)) + "&" + Main.validateFormula(mEq.group(2)) + ")|(!" + Main.validateFormula(mAnd.group(1)) + "&!" + Main.validateFormula(mAnd.group(2)) + ")";
					Integer v = Main.validateVariable(f);
					if (!v.equals(new Integer(-1)))
						return "" + v;
					throw new MalformedParametersException("Invalid Formula");
		}	*/

	/**
	 * Prints the help
	 */
	public static void printHelp() {
		System.out.println("Help for the Logic Calculator.");
		System.out.println("Options:");
		System.out.println(" -h --help Print this help.");
		System.out.println();
		System.out.println("Cli Commands");
		System.out.println("q			- Quit.");
		System.out.println("h			- Print this help.");
		System.out.println("c			- Clear data.");
		System.out.println("a [v|f] 	- Add data. v for Variable and f for Formula");
		System.out.println("l [v|f] [c] - List data. v for Variable and f for Formula. If a is set use characters.");
		System.out.println();
		System.out.println("Valid Variables:");
		System.out.println("Fi with i being a natural number. Or:");
		System.out.println("A-Z meaning A = F1, B = F2 , ... , Z = F26.");
		System.out.println();
		System.out.println("Formula syntax:");
		System.out.println("( means (");
		System.out.println(") means )");
		System.out.println("& means and");
		System.out.println("| means or");
		System.out.println("! means not");
		System.out.println("-> means implies");
		System.out.println("<-> means equivalent");
		System.out.println("n times and and or is not support yet.");
		System.out.println("Every term except a single variable and the negation of a sub formula needs brackets.");
	}

	/**
	 * The main function.
	 * 
	 * @param args
	 *            Unused
	 */
	public static void main(String[] args) {
		for (String st : args) {
			if (st.toLowerCase().equals("-h") || st.toLowerCase().equals("--help")) {
				Main.printHelp();
				System.exit(0);
			}
		}
		System.out.println("Welcome to the logic calculator. Type h for help.");
		boolean stop = false;
		Main.s = new Scanner(System.in);
		while (!stop) {
			System.out.println();
			System.out.print(">");
			String line = Main.s.nextLine().toLowerCase();
			String[] parts = line.split(" ");

			switch (parts[0]) {
				case "h":
					Main.printHelp();
				break;
				case "c":
					Main.usedVariables.clear();
					Main.formulas.clear();
				break;
				case "a":
					if (parts.length < 2) {
						System.out.println("Need a argument to add. Use h for help.");
						break;
					}
					if (parts[1].equals("v")) {
						System.out.println("Please enter a variable");
						String v = Main.s.nextLine().toLowerCase();
						Formula.Variable var = new Formula.Variable(v);
						if (var.getVar() != -1) {
							Main.usedVariables.add(var);
						}
						else {
							System.out.println(v + " is not a valid variable.");
						}
					}
					else if (parts[1].equals("f")) {
						System.out.println("Please enter a formula");
						Formula form = new Formula(Main.s.nextLine().toLowerCase());
						if (!form.isValid()) {
							System.out.println(form.getInput() + " is not a valid formula.");
						}
						else {
							Main.formulas.add(form);
						}
					}
					else {
						System.out.println(parts[1] + " is no valid first argument for add. Use h for help.");
					}
				break;
				case "l":
					if (parts.length < 2) {
						System.out.println("Need a argument to list. Use h for help.");
						break;
					}
					if (parts[1].equals("v")) {
						for (Formula.Variable i : Main.usedVariables) {

						}
					}
					else if (parts[1].equals("f")) {

					}
				break;
				default:
					System.out.println(parts[0] + " is no valid command. Use h for help.");
			}

		}

	}

}
