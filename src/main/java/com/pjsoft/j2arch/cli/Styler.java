package com.pjsoft.j2arch.cli;

import org.fusesource.jansi.Ansi;

/**
 * Utility class for styling console output using ANSI escape codes.
 * 
 * This class provides constants and methods for applying colors, bold text,
 * and other styles to console output. It supports both simple and dynamic
 * styling, making it easier to create visually appealing console applications.
 * 
 * Responsibilities:
 * - Provides predefined constants for common ANSI styles (e.g., colors, bold).
 * - Offers utility methods for applying styles to text.
 * - Supports combining multiple styles dynamically.
 * 
 * Usage Examples:
 * 
 * 1. Using predefined methods for simple styling:
 * {@code
 * System.out.println(Styler.blue("This is blue text"));
 * System.out.println(Styler.green("This is green text"));
 * System.out.println(Styler.bold("This is bold text"));
 * }
 * 
 * 2. Using constants for manual styling:
 * {@code
 * System.out.println(Styler.BOLD + Styler.RED + "This is bold red text" + Styler.RESET);
 * }
 * 
 * 3. Combining multiple styles dynamically:
 * {@code
 * System.out.println(Styler.style("This is bold blue text", Styler.BOLD, Styler.BLUE));
 * }
 * 
 * Dependencies:
 * - Jansi library for ANSI escape code generation.
 * 
 * Thread Safety:
 * - This class is thread-safe as it only provides static constants and methods.
 * 
 * Limitations:
 * - ANSI escape codes may not work on all terminals (e.g., Windows Command Prompt without additional configuration).
 * 
 * @author JagsProgramming
 * @since 1.0
 */
public class Styler {

    // Constants for ANSI escape codes
    /**
     * ANSI escape code for blue text.
     */
    public static final String BLUE = Ansi.ansi().fg(Ansi.Color.BLUE).toString();

    /**
     * ANSI escape code for green text.
     */
    public static final String GREEN = Ansi.ansi().fg(Ansi.Color.GREEN).toString();

    /**
     * ANSI escape code for yellow text.
     */
    public static final String YELLOW = Ansi.ansi().fg(Ansi.Color.YELLOW).toString();

    /**
     * ANSI escape code for red text.
     */
    public static final String RED = Ansi.ansi().fg(Ansi.Color.RED).toString();

    /**
     * ANSI escape code for bold text.
     */
    public static final String BOLD = Ansi.ansi().bold().toString();

    /**
     * ANSI escape code to reset text styles to default.
     */
    public static final String RESET = Ansi.ansi().reset().toString();

    // Methods for styled text

    /**
     * Applies blue color to the given text.
     * 
     * @param text the text to style.
     * @return the styled text with blue color.
     * @since 1.0
     */
    public static String blue(String text) {
        return BLUE + text + RESET;
    }

    /**
     * Applies green color to the given text.
     * 
     * @param text the text to style.
     * @return the styled text with green color.
     * @since 1.0
     */
    public static String green(String text) {
        return GREEN + text + RESET;
    }

    /**
     * Applies yellow color to the given text.
     * 
     * @param text the text to style.
     * @return the styled text with yellow color.
     * @since 1.0
     */
    public static String yellow(String text) {
        return YELLOW + text + RESET;
    }

    /**
     * Applies red color to the given text.
     * 
     * @param text the text to style.
     * @return the styled text with red color.
     * @since 1.0
     */
    public static String red(String text) {
        return RED + text + RESET;
    }

    /**
     * Applies bold style to the given text.
     * 
     * @param text the text to style.
     * @return the styled text with bold formatting.
     * @since 1.0
     */
    public static String bold(String text) {
        return BOLD + text + RESET;
    }

    /**
     * Combines multiple styles dynamically and applies them to the given text.
     * 
     * Usage Example:
     * {@code
     * System.out.println(Styler.style("This is bold blue text", Styler.BOLD, Styler.BLUE));
     * }
     * 
     * @param text   the text to style.
     * @param styles the styles to apply (e.g., {@link #BOLD}, {@link #BLUE}).
     * @return the styled text with the combined styles.
     * @since 1.0
     */
    public static String style(String text, String... styles) {
        StringBuilder styledText = new StringBuilder();
        for (String style : styles) {
            styledText.append(style);
        }
        styledText.append(text).append(RESET);
        return styledText.toString();
    }
}
