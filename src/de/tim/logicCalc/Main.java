package de.tim.logicCalc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

/**
 * TODO: Description
 * 
 * @author Tim Neumann
 */
public class Main {

	//Need has map with int as key because HashSet doesn't know two variables are the same
	public static ArrayList<Formula> formulas = new ArrayList<>();
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
		System.out.println("q					- Quit.");
		System.out.println("h					- Print this help.");
		System.out.println("cl					- Clear data.");
		System.out.println("a [<forms>] 		- Adds a formula. If not specified the program will ask for it later.");
		System.out.println("list [l] 			- List data. If l is set, use letters.");
		System.out.println("comp [l] 			- Compute data and display as matrix. If l is set, use letters.");
		System.out.println("all <name>	<val>	- Checks if the formula ");
		System.out.println();
		System.out.println("Valid Variables:");
		System.out.println("Ai with i being a natural number. Or:");
		System.out.println("A-Z meaning A = A1, B = A2 , ... , Z = A26.");
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

		boolean letters;
		ArrayList<Integer> usedVars;

		while (!stop) {
			System.out.println();
			System.out.print(">");
			String line = Main.s.nextLine().toLowerCase();
			String[] parts = line.split(" ");

			switch (parts[0]) {
				case "h":
					Main.printHelp();
				break;
				case "cl":
					Main.formulas.clear();
				break;
				case "q":
					System.out.println("Bye.");
					System.exit(0);
				break;
				case "a":
					if (parts.length >= 2) {
						for (int i = 1; i < parts.length; i++) {
							Formula form = new Formula(parts[i], "F" + Main.formulas.size());
							if (!form.isValid()) {
								System.out.println(form.getInput() + " is not a valid formula.");
							}
							else {
								Main.formulas.add(form);
							}
						}
					}
					else {
						System.out.println("Please enter a formula");
						Formula form = new Formula(Main.s.nextLine().toLowerCase(), "F" + Main.formulas.size());
						if (!form.isValid()) {
							System.out.println(form.getInput() + " is not a valid formula.");
						}
						else {
							Main.formulas.add(form);
						}
					}
				break;
				case "all":
					if (parts.length >= 3) {
						Formula f = null;
						int value = -1;
						try {
							f = Main.formulas.get(Integer.parseInt(parts[1].substring(1)));
						} catch (NumberFormatException e) {
							System.out.println("Not a valid Formula");
							break;
						}
						try {
							value = Integer.parseInt(parts[2]);
							if (value < 0 || value > 1) throw new NumberFormatException();
						} catch (NumberFormatException e) {
							System.out.println("Not a valid value");
							break;
						}

						usedVars = new ArrayList<>();
						for (Formula.Variable v : f.getUsedVars().values()) {
							if (!usedVars.contains(new Integer(v.getVar()))) {
								usedVars.add(new Integer(v.getVar()));
							}
						}
						Collections.sort(usedVars);

						boolean loopRunning = true;
						boolean matched = true;
						int[] values = new int[usedVars.size()];
						HashMap<Integer, Integer> val;

						while (loopRunning) {
							val = new HashMap<>();

							for (int i = 0; i < values.length; i++) {
								val.put(usedVars.get(i), new Integer(values[i]));
							}

							if (f.calculate(val) != value) {
								matched = false;
							}

							int carry = 1;
							for (int i = values.length - 1; i >= 0; i--) {
								if (carry == 0) {
									continue;
								}

								if (values[i] == 0) {
									carry = 0;
									values[i] = 1;
								}
								else {
									values[i] = 0;
								}
							}
							if (carry == 1) {
								loopRunning = false;
							}
						}
						if (matched) {
							System.out.println("YES, this formula is always " + value);
						}
						else {
							System.out.println("NO, this formula is not always " + value);
						}
					}
					else {
						System.out.println("Need a name and a value.");
					}
				break;
				case "list":
					letters = false;
					if (parts.length > 1 && parts[1].equals("l")) {
						letters = true;
					}
					usedVars = new ArrayList<>();
					for (Formula f : Main.formulas) {
						for (Formula.Variable v : f.getUsedVars().values()) {
							if (!usedVars.contains(new Integer(v.getVar()))) {
								usedVars.add(new Integer(v.getVar()));
							}
						}
					}
					Collections.sort(usedVars);

					System.out.print("Used variables: ");
					for (Integer i : usedVars) {
						System.out.print(new Formula.Variable(i).getFancy(letters) + ", ");
					}
					System.out.println();

					System.out.println("Formulas:");
					for (Formula f : Main.formulas) {
						System.out.println("In:" + f.getInput() + "; parsed:" + f.getString(letters));
					}
				break;
				case "comp":
					letters = false;
					if (parts.length > 1 && parts[1].equals("l")) {
						letters = true;
					}
					usedVars = new ArrayList<>();
					for (Formula f : Main.formulas) {
						for (Formula.Variable v : f.getUsedVars().values()) {
							if (!usedVars.contains(new Integer(v.getVar()))) {
								usedVars.add(new Integer(v.getVar()));
							}
						}
					}
					Collections.sort(usedVars);
					System.out.println("Result:");
					System.out.print(" ");

					int[] lengthses = new int[usedVars.size() + Main.formulas.size()];
					int lengthsesC = 0;

					for (Integer i : usedVars) {
						String st = new Formula.Variable(i).getFancy(letters);
						lengthses[lengthsesC] = st.length();
						lengthsesC++;
						System.out.print(st + " | ");

					}

					for (Formula f : Main.formulas) {
						String st = f.getString(letters);
						lengthses[lengthsesC] = st.length();
						lengthsesC++;

						System.out.print(st + " | ");
					}

					//rows

					boolean loopRunning = true;
					int[] values = new int[usedVars.size()];
					HashMap<Integer, Integer> val;

					System.out.println();
					System.out.print(" ");

					while (loopRunning) {
						val = new HashMap<>();
						lengthsesC = 0;

						for (int i = 0; i < values.length; i++) {
							val.put(usedVars.get(i), new Integer(values[i]));
						}

						for (int i : values) {
							int len = lengthses[lengthsesC] - 1;
							lengthsesC++;
							int len2 = len / 2;
							String buff1 = "";
							String buff2 = "";

							for (int j = 0; j < len; j++) {
								if (j < len2) {
									buff1 += " ";
								}
								else {
									buff2 += " ";
								}
							}

							System.out.print(buff1 + i + buff2 + " | ");
						}
						for (Formula f : Main.formulas) {
							int len = lengthses[lengthsesC] - 1;
							lengthsesC++;
							int len2 = len / 2;
							String buff1 = "";
							String buff2 = "";

							for (int j = 0; j < len; j++) {
								if (j < len2) {
									buff1 += " ";
								}
								else {
									buff2 += " ";
								}
							}

							System.out.print(buff1 + f.calculate(val) + buff2 + " | ");
						}

						int carry = 1;
						for (int i = values.length - 1; i >= 0; i--) {
							if (carry == 0) {
								continue;
							}

							if (values[i] == 0) {
								carry = 0;
								values[i] = 1;
							}
							else {
								values[i] = 0;
							}
						}
						if (carry == 1) {
							loopRunning = false;
						}

						System.out.println();
						System.out.print(" ");
					}

				break;
				default:
					System.out.println(parts[0] + " is no valid command. Use h for help.");
			}

		}

	}

}
