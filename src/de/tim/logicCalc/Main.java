package de.tim.logicCalc;

/*
 * LogicCalculator
 *
 * TODO: Project Beschreibung
 *
 * @author Tim Neumann
 * @version 1.0.0
 *
 */

import java.lang.reflect.MalformedParametersException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.util.Pair;

/**
 * TODO: Description
 * 
 * @author Tim Neumann
 */
public class Main {

	private static HashSet<Integer> usedVariables = new HashSet<>();
	private static ArrayList<Pair<String, String>> formula = new ArrayList<>();
	private static Scanner s;

	private static Integer validateVariable(String v) {
		if (v.length() == 1 && v.charAt(0) > 96 && v.charAt(0) < 123)
			return (new Integer(v.charAt(0) - 96));
		else if (v.startsWith("f")) {
			try {
				return (new Integer(Integer.parseInt(v.substring(1))));
			} catch (NumberFormatException e) {
				return new Integer(-1);
			}
		}
		else
			return new Integer(-1);

	}

	private static String checkFormula(String f) throws MalformedParametersException {

		Matcher mAnd = Pattern.compile("\\A\\((.*)&(.*)\\)\\Z").matcher(f);
		Matcher mOr = Pattern.compile("\\A\\((.*)\\|(.*)\\)\\Z").matcher(f);
		Matcher mNot = Pattern.compile("\\A!(.*)\\Z").matcher(f);
		Matcher mImp = Pattern.compile("\\A(.*)->(.*)\\Z").matcher(f);
		Matcher mEq = Pattern.compile("\\A(.*)<->(.*)\\Z").matcher(f);
		if (mAnd.matches())
			return "(" + Main.checkFormula(mAnd.group(1)) + "&" + Main.checkFormula(mAnd.group(2)) + ")";
		if (mOr.matches())
			return "(" + Main.checkFormula(mOr.group(1)) + "|" + Main.checkFormula(mOr.group(2)) + ")";
		if (mNot.matches())
			return "!" + Main.checkFormula(mNot.group(1));
		if (mImp.matches())
			return "(!" + Main.checkFormula(mImp.group(1)) + "|" + Main.checkFormula(mImp.group(2)) + ")";
		if (mEq.matches())
			return "((" + Main.checkFormula(mEq.group(1)) + "&" + Main.checkFormula(mEq.group(2)) + ")|(!" + Main.checkFormula(mAnd.group(1)) + "&!" + Main.checkFormula(mAnd.group(2)) + ")";
		Integer v = Main.validateVariable(f);
		if (!v.equals(new Integer(-1)))
			return "" + v;
		throw new MalformedParametersException("Invalid Formula");

	}

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
	}

	/**
	 * The main function.
	 * @param args Unused
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
					Main.formula.clear();
				break;
				case "a":
					if (parts.length < 2) {
						System.out.println("Need a argument to add. Use h for help.");
						break;
					}
					if (parts[1].equals("v")) {
						System.out.println("Please enter a variable");
						String v = Main.s.nextLine().toLowerCase();
						Integer vNum = Main.validateVariable(v);
						if (!vNum.equals(new Integer(-1))) {
							Main.usedVariables.add(vNum);
						}
						else {
							System.out.println(v + " is not a valid variable.");
						}
					}
					else if (parts[1].equals("f")) {
						System.out.println("Please enter a formula");
						String f1 = Main.s.nextLine().toLowerCase();
						String f2 = Main.checkFormula(f1);
						if (f2 == "") {
							System.out.println(f1 + " is not a valid formula.");
						}
						else {
							Main.formula.add(new Pair<>(f1, f2));
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
						for (Integer i : Main.usedVariables) {

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
