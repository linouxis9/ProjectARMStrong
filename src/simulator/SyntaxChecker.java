package simulator;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

public class SyntaxChecker {

	/**
	 * Small Set containing the instructions that takes as input two Registers and
	 * an Operand2.
	 */
	private static final Set<String> RROP2 = new HashSet<String>(
			Arrays.asList(new String[] { "adc", "add", "and", "bic", "eor", "sub", "mul", "orr", "sdiv", "udiv" }));

	/**
	 * Small Set containing the instructions that takes as input only one Operand2.
	 */
	private static final Set<String> OOP2 = new HashSet<String>(
			Arrays.asList(new String[] { "swi", "svc" }));
	/**
	 * Small Set containing the instructions that takes as input one Register and
	 * one Operand2.
	 */
	private static final Set<String> ROP2 = new HashSet<String>(
			Arrays.asList(new String[] { "cmp", "cmn", "tst", "teq", "mov", "mvn" }));

	/**
	 * Small Set containing the special instructions that takes as input one
	 * Operand2.
	 */
	private static final Set<String> LSOP2 = new HashSet<String>(Arrays.asList(new String[] { "ldr", "str" }));

	/**
	 * Small Set containing the branching instructions that takes as input one
	 * Operand2.
	 */
	private static final Set<String> BOP2 = new HashSet<String>(Arrays.asList(new String[] { "b", "bl" }));

	
	// TODO write javadoc comment
	/**
	 * 
	 * @param tokens
	 * @throws InvalidSyntaxException
	 * @throws InvalidOperationException
	 * @throws InvalidRegisterException
	 */
	public static void checkSyntax(List<Token> tokens)
			throws InvalidSyntaxException, InvalidOperationException, InvalidRegisterException {
		SyntaxChecker.checkSyntax(tokens, 0);
	}

	// TODO write javadoc comment
	/**
	 * 
	 * @param tokens
	 * @param line
	 * @throws InvalidSyntaxException
	 * @throws InvalidOperationException
	 * @throws InvalidRegisterException
	 */
	public static void checkSyntax(List<Token> tokens, int line)
			throws InvalidSyntaxException, InvalidOperationException, InvalidRegisterException {

		try {
			int i = 0;
			
			if (tokens.get(i).getToken() == TokenType.LABEL) {
				i++;
			}
			
			String op = tokens.get(i).getValue();
			if (tokens.get(i).getToken() != TokenType.OPERATION
					&& !(SyntaxChecker.RROP2.contains(op) || SyntaxChecker.OOP2.contains(op)
							|| SyntaxChecker.ROP2.contains(op) || SyntaxChecker.LSOP2.contains(op))) {
				throw new InvalidOperationException(line, op);
			}

			while (tokens.get(i).getToken() == TokenType.FLAG) {
				i++;
			}

			if (tokens.get(i).getToken() == TokenType.CONDITIONCODE) {
				i++;
			}

			for (Token token : tokens) {
				if (token.getToken() == TokenType.REGISTER) {
					int register = Integer.parseInt(token.getValue());
					if (SyntaxChecker.checkRegister(register)) {
						throw new InvalidRegisterException(line, register);
					}

				}
			}
			
			boolean error = false;
			i++;
			System.out.println(tokens);
			if (SyntaxChecker.RROP2.contains(op)) {
				error = SyntaxChecker.checkRROP2(tokens, i);
			} else if (SyntaxChecker.OOP2.contains(op)) {
				error = SyntaxChecker.checkOOP2(tokens, i);
			} else if (SyntaxChecker.ROP2.contains(op)) {
				error = SyntaxChecker.checkROP2(tokens, i);
			} else if (SyntaxChecker.LSOP2.contains(op)) {
				error = SyntaxChecker.checkLSOP2(tokens, i);
			} else if (SyntaxChecker.BOP2.contains(op)) {
				error = SyntaxChecker.checkBOP2(tokens, i);
			}

			if (error) {
				throw new InvalidSyntaxException(line);
			}

		} catch (IndexOutOfBoundsException e) {
			throw new InvalidSyntaxException(line);
		}
	}

	/**
	 * Ensure that a register is comprised between [0;15]
	 * 
	 * @param register
	 *            The Register id to test
	 * @return True if the register is invalid, else otherwise.
	 */
	private static boolean checkRegister(int register) {
		if (register > 15 || register < 0) {
			return true;
		}
		return false;
	}

	/**
	 * Ensure the syntax correctness of a RROP2 (Two Registers, One Operand2)
	 * instruction.
	 * 
	 * @param tokens
	 *            The instruction's parsable tokens
	 * @param i
	 *            The index of the first element of the right-hand expression
	 * @return True if the instruction is invalid, else otherwise.
	 */
	private static boolean checkRROP2(List<Token> tokens, int i) {
		if ((tokens.get(i).getToken() != TokenType.REGISTER) || (tokens.get(i + 1).getToken() != TokenType.COMMA)
				|| (tokens.get(i + 2).getToken() != TokenType.REGISTER)
				|| (tokens.get(i + 3).getToken() != TokenType.COMMA)
				|| (tokens.get(i + 4).getToken() != TokenType.REGISTER
						&& tokens.get(i + 4).getToken() != TokenType.HASH)) {
			return true;
		}
		return false;
	}

	/**
	 * Ensure the syntax correctness of a OOP2 (Only One Operand2) instruction.
	 * 
	 * @param tokens
	 *            The instruction's parsable tokens
	 * @param i
	 *            The index of the first element of the right-hand expression
	 * @return True if the instruction is invalid, else otherwise.
	 */
	private static boolean checkOOP2(List<Token> tokens, int i) {
		if (tokens.get(i).getToken() != TokenType.REGISTER && tokens.get(i).getToken() != TokenType.HASH) {
			return true;
		}
		return false;
	}

	/**
	 * Ensure the syntax correctness of a ROP2 (One Register, One Operand2)
	 * instruction.
	 * 
	 * @param tokens
	 *            The instruction's parsable tokens
	 * @param i
	 *            The index of the first element of the right-hand expression
	 * @return True if the instruction is invalid, else otherwise.
	 */
	private static boolean checkROP2(List<Token> tokens, int i) {
		if ((tokens.get(i).getToken() != TokenType.REGISTER) || (tokens.get(i + 1).getToken() != TokenType.COMMA)
				|| (tokens.get(i + 2).getToken() != TokenType.REGISTER
						&& tokens.get(i + 2).getToken() != TokenType.HASH)) {
			return true;
		}
		return false;
	}

	/**
	 * Ensure the syntax correctness of a LSOP2 (Special Instruction with One
	 * Operand2) instruction.
	 * 
	 * @param tokens
	 *            The instruction's parsable tokens
	 * @param i
	 *            The index of the first element of the right-hand expression
	 * @return True if the instruction is invalid, else otherwise.
	 */
	private static boolean checkLSOP2(List<Token> tokens, int i) {
		if (tokens.get(i).getToken() == TokenType.REGISTER && tokens.get(i + 1).getToken() == TokenType.COMMA) {
			Token token = tokens.get(i + 2);
			switch (token.getToken()) {
			case OFFSET:
				return SyntaxChecker.checkRegister(Integer.parseInt(token.getValue()));
			case INDEXEDOFFSET:
				String offset = token.getValue();
				return SyntaxChecker.checkRegister(Integer.parseInt(offset.substring(0, offset.indexOf(","))));
			default:
				return true;
			case HASH:
			case REGISTER:
			}
		} else {
			return true;
		}
		return false;
	}
	
	/**
	 * Ensure the syntax correctness of a BOP2 (Branching Instruction with One
	 * Operand2) instruction.
	 * 
	 * @param tokens
	 *            The instruction's parsable tokens
	 * @param i
	 *            The index of the first element of the right-hand expression
	 * @return True if the instruction is invalid, else otherwise.
	 */
	private static boolean checkBOP2(List<Token> tokens, int i) {
		if (tokens.get(i).getToken() == TokenType.LABEL) {
			return true;
		}
		return false;
	}
}