package hu.bbs.ocr;

import hu.bbs.ocr.exception.DigitNotFoundException;
import hu.bbs.ocr.exception.OcrValidationException;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

/**
 * @author bvarga
 */
public class AccountNumberOcrTest {

	@Test
	public void testParseSingleEntry() throws URISyntaxException, IOException, OcrValidationException, DigitNotFoundException {
		final List<String> collect = getFileLines("single_entry.txt");
		assertNotNull(collect);
		assertTrue(collect.size() > 0);

		List<String> accountNumbers = AccountNumberOcr.parse(collect);
		assertNotNull(accountNumbers);
		assertTrue(accountNumbers.size() > 0);
		assertTrue(accountNumbers.contains("123456789"));
	}

	@Test
	public void testParseMultipleEntries() throws URISyntaxException, IOException, OcrValidationException, DigitNotFoundException {
		final List<String> collect = getFileLines("multiple_entries.txt");
		assertNotNull(collect);
		assertTrue(collect.size() > 0);

		List<String> accountNumbers = AccountNumberOcr.parse(collect);
		assertNotNull(accountNumbers);
		assertTrue(accountNumbers.size() > 0);
		assertTrue(accountNumbers.contains("123456789"));
		assertTrue(accountNumbers.contains("888888888"));
	}

	@Test
	public void testInvalidAccountNumber() throws IOException, URISyntaxException {
		final List<String> collect = getFileLines("invalid_entry.txt");
		assertNotNull(collect);
		assertTrue(collect.size() > 0);
		try {
			List<String> accountNumbers = AccountNumberOcr.parse(collect);
			assertNull(accountNumbers);
		} catch (Exception e) {
			assertEquals(OcrValidationException.class, e.getClass());
			assertEquals("Each entry must be 4 lines long..", e.getMessage());
		}
	}

	private List<String> getFileLines(String filename) throws URISyntaxException, IOException {
		final URL resource = getClass().getClassLoader().getResource(filename);
		if (resource != null) {
			Path path = Paths.get(resource.toURI());

			Stream<String> lines = Files.lines(path, Charset.defaultCharset());
			final List<String> collect = lines.collect(Collectors.toList());
			lines.close();
			return collect;
		}
		return Collections.emptyList();
	}
}
