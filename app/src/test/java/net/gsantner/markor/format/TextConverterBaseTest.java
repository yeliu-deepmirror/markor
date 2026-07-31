package net.gsantner.markor.format;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

public class TextConverterBaseTest {
    @Test
    public void imageLoadBaseFolderUsesDocumentFolderByDefault() {
        final File document = new File("/notes/article.md");
        final File fallback = new File("/notebook");

        assertEquals(new File("/notes"), TextConverterBase.getImageLoadBaseFolder(document, fallback, ""));
    }

    @Test
    public void imageLoadBaseFolderResolvesRelativeFolderFromDocumentFolder() {
        final File document = new File("/notes/articles/article.md");
        final File fallback = new File("/notebook");

        assertEquals(new File("/notes/articles/../images"),
                TextConverterBase.getImageLoadBaseFolder(document, fallback, "../images"));
    }

    @Test
    public void imageLoadBaseFolderUsesAbsoluteFolderDirectly() {
        final File document = new File("/notes/article.md");
        final File fallback = new File("/notebook");

        assertEquals(new File("/shared/images"),
                TextConverterBase.getImageLoadBaseFolder(document, fallback, "/shared/images"));
    }

    @Test
    public void configuredImageFolderResolvesRootRelativeImageSources() {
        assertEquals("<img src=\"assets/image.png\"><img src=\"//example.com/image.png\">",
                TextConverterBase.resolveConfiguredRootRelativeImageSources(
                        "<img src=\"/assets/image.png\"><img src=\"//example.com/image.png\">"));
    }
}
