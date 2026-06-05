package io.github.tiagofar78.grindstone.util;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TextFormattingTest {

    @Test
    public void testFormatCenteredNullReturnsEmpty() {
        String result = TextFormatting.formatCentered(null, 10);

        Assert.assertEquals(result, "");
    }

    @Test
    public void testFormatCenteredEmptyReturnsEmpty() {
        String result = TextFormatting.formatCentered("", 10);

        Assert.assertEquals(result, "");
    }

    @Test
    public void testFormatCenteredZeroWidthReturnsEmpty() {
        String result = TextFormatting.formatCentered("Hello", 0);

        Assert.assertEquals(result, "");
    }

    @Test
    public void testFormatCenteredSingleShortLine() {
        String result = TextFormatting.formatCentered("Hello", 9);

        Assert.assertEquals(result, "  Hello");
    }

    @Test
    public void testFormatCenteredSingleShortLineOddSpaces() {
        String result = TextFormatting.formatCentered("  Hello  ", 10);

        Assert.assertEquals(result, "  Hello");
    }

    @Test
    public void testFormatCenteredLongWordSplitsIntoLines() {
        String result = TextFormatting.formatCentered("ABCDEFGHIJ", 3);

        String expected = "ABC\nDEF\nGHI\n J\n";

        Assert.assertEquals(result, expected);
    }

    @Test
    public void testFormatCenteredWrapsWordsToLines() {
        String result = TextFormatting.formatCentered("ab cd ef", 5);

        String expected = "ab cd\n ef";

        Assert.assertEquals(result, expected);
    }

    @Test
    public void testFormatCenteredLongSentenceWraps() {
        String sentence = "This is a longer sentence that should wrap into multiple centered lines";
        int maxWidth = 20;

        String result = TextFormatting.formatCentered(sentence, maxWidth);
        String expected = "  This is a longer\nsentence that should\n wrap into multiple\n   centered lines";

        Assert.assertTrue(result.contains(expected));
    }

}
