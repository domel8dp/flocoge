package pl.dpawlak.flocoge.log;

import static org.junit.Assert.*;

import org.junit.Test;

import pl.dpawlak.flocoge.log.Logger.Formatter;

public class FormatterTest {

    @Test
    public void testFormatting() {
        assertEquals("This is a test message with 4 placeholders, null",
            Formatter.buildMsg("{} is a {} message with {} placeholders, {}", "This", "test", 4, null));
    }

    @Test
    public void testNotEnoughPlaceholders() {
        assertEquals("This is a test message", Formatter.buildMsg("{} is a {} message", "This", "test", 4, null));
    }

    @Test
    public void testTooManyPlaceholders() {
        assertEquals("This is a {} message with {} placeholders, {}",
            Formatter.buildMsg("{} is a {} message with {} placeholders, {}", "This"));
    }
}
