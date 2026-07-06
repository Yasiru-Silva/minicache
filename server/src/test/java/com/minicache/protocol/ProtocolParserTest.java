package com.minicache.protocol;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProtocolParserTest {

    private ProtocolParser parser;

    @BeforeEach
    void setUp() {
        parser = new ProtocolParser();
    }

    @Test
    void testParsePing() {
        Command command = parser.parse("PING");
        assertNotNull(command);
        assertEquals("PING", command.getName());
        assertEquals(0, command.getArgs().size());
    }

    @Test
    void testParseSet() {
        Command command = parser.parse("SET foo bar");
        assertNotNull(command);
        assertEquals("SET", command.getName());
        assertEquals("foo", command.getArg(0));
        assertEquals("bar", command.getArg(1));
    }

    @Test
    void testParseSetWithExpiry() {
        Command command = parser.parse("SET foo bar EX 30");
        assertNotNull(command);
        assertEquals("SET", command.getName());
        assertEquals("foo", command.getArg(0));
        assertEquals("bar", command.getArg(1));
        assertEquals("EX", command.getArg(2));
        assertEquals("30", command.getArg(3));
    }

    @Test
    void testParseGet() {
        Command command = parser.parse("GET foo");
        assertNotNull(command);
        assertEquals("GET", command.getName());
        assertEquals("foo", command.getArg(0));
    }

    @Test
    void testParseDel() {
        Command command = parser.parse("DEL foo");
        assertNotNull(command);
        assertEquals("DEL", command.getName());
        assertEquals("foo", command.getArg(0));
    }

    @Test
    void testParseExists() {
        Command command = parser.parse("EXISTS foo");
        assertNotNull(command);
        assertEquals("EXISTS", command.getName());
        assertEquals("foo", command.getArg(0));
    }

    @Test
    void testParseKeys() {
        Command command = parser.parse("KEYS");
        assertNotNull(command);
        assertEquals("KEYS", command.getName());
        assertEquals(0, command.getArgs().size());
    }

    @Test
    void testCaseInsensitive() {
        Command command = parser.parse("set foo bar");
        assertNotNull(command);
        assertEquals("SET", command.getName());
    }

    @Test
    void testInvalidCommand() {
        Command command = parser.parse("INVALID foo");
        assertNull(command);
    }

    @Test
    void testEmptyInput() {
        Command command = parser.parse("");
        assertNull(command);
    }

    @Test
    void testNullInput() {
        Command command = parser.parse(null);
        assertNull(command);
    }

    @Test
    void testWrongArgCount() {
        // SET with only one argument should fail
        Command command = parser.parse("SET foo");
        assertNull(command);
    }
}