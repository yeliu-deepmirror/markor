package net.gsantner.markor.format.markdown;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MarkdownTextConverterTest {
    @Test
    public void headingAfterEmptyHtmlParagraphIsSeparated() {
        assertEquals("<p></p>\n\n### Header", MarkdownTextConverter.separateEmptyHtmlParagraphFromHeading("<p></p>\n### Header"));
    }
}
