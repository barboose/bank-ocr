package hu.bbs.ocr;

import hu.bbs.ocr.exception.DigitNotFoundException;
import hu.bbs.ocr.exception.OcrValidationException;
import hu.bbs.ocr.util.Util;
import org.apache.commons.collections4.ListUtils;

import java.util.*;

/**
 * @author bvarga
 */
public class AccountNumberOcr {
	private static final Map<Integer, String> digits = new HashMap<>();
	private static final int FROM_INDEX = 0;
	private static final int WIDTH_OF_ONE_DIGIT = 3;
	private static final int NUMBER_OF_DIGITS = 9;
	private static final int WIDTH_OF_ONE_LINE = WIDTH_OF_ONE_DIGIT * NUMBER_OF_DIGITS;
	private static final int ENTRY_LINES = 4;
	private static final String DEFAULT_PARSED_ITEM_VALUE = "";

	static {
		digits.put(0, " _ " + "| |" + "|_|");
		digits.put(1, "   " + "  |" + "  |");
		digits.put(2, " _ " + " _|" + "|_ ");
		digits.put(3, " _ " + " _|" + " _|");
		digits.put(4, "   " + "|_|" + "  |");
		digits.put(5, " _ " + "|_ " + " _|");
		digits.put(6, " _ " + "|_ " + "|_|");
		digits.put(7, " _ " + "  |" + "  |");
		digits.put(8, " _ " + "|_|" + "|_|");
		digits.put(9, " _ " + "|_|" + " _|");
	}

	public static List<String> parse(List<String> lines) throws DigitNotFoundException, OcrValidationException {
		if (isValidEntryLines(lines)) {
			final List<String> accountNumbers = new ArrayList<>();
			final List<List<String>> partition = ListUtils.partition(lines, ENTRY_LINES);

			for (List<String> strings : partition) {
				accountNumbers.add(parseSingleEntry(strings));
			}
			return accountNumbers;
		}
		throw new OcrValidationException("Each entry must be 4 lines long..");
	}

	private static String parseSingleEntry(List<String> lines) throws OcrValidationException, DigitNotFoundException {
		if (isValidEntryLines(lines)) {
			final String[] rawDigitsArray = new String[NUMBER_OF_DIGITS];
			Arrays.fill(rawDigitsArray, FROM_INDEX, NUMBER_OF_DIGITS, DEFAULT_PARSED_ITEM_VALUE);

			for (String line : lines) {
				if (!line.trim().isEmpty() && line.length() == WIDTH_OF_ONE_LINE) {
					final String[] strings = Util.splitAfterNThNumber(line, WIDTH_OF_ONE_DIGIT);
					for (int i = 0; i < NUMBER_OF_DIGITS; i++) {
						rawDigitsArray[i] += strings[i];
					}
				}
			}

			String accountNumber = "";
			for (String rawDigit : rawDigitsArray) {
				final int recognizedDigit = findDigitByRawValue(rawDigit);
				if (recognizedDigit != -1) {
					accountNumber += recognizedDigit;
				} else {
					throw new DigitNotFoundException("Cannot recognize this raw digit value: " + rawDigit);
				}
			}
			return accountNumber;
		} else {
			throw new OcrValidationException("Each entry must be 4 lines long..");
		}
	}

	private static boolean isValidEntryLines(List lines) {
		return lines != null && !lines.isEmpty() && lines.size() % 4 == 0;
	}

	private static int findDigitByRawValue(String value) {
		for (Map.Entry<Integer, String> entry : digits.entrySet()) {
			if (entry.getValue().equalsIgnoreCase(value)) {
				return entry.getKey();
			}
		}
		return -1;
	}

}
