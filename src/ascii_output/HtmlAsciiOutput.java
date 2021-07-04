package ascii_output;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

public class HtmlAsciiOutput implements AsciiOutput {
    private static final double BASE_LINE_SPACING = 0.8;
    private static final double BASE_FONT_SIZE = 150.0;

    private final String fontName;
    private final String filename;

    public HtmlAsciiOutput(String filename, String fontName) {
        this.fontName = fontName;
        this.filename = filename;
    }

    @Override
    public void output(char[][] chars) {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(String.format(
                """
                    <!DOCTYPE html>\n
                    <html>\n
                    <body style=\"
                        COLOR:#000000;
                        TEXT-ALIGN:center;
                        FONT-SIZE:1px;
                    \">\n
                    <p style=\"
                        white-space:pre;
                        FONT-FAMILY:%s;
                        FONT-SIZE:%frem;
                        LETTER-SPACING:0.15em;
                        LINE-HEIGHT:%fem;
                    \">\n
                """,
                    fontName, BASE_FONT_SIZE/chars[0].length, BASE_LINE_SPACING));

            for(int y = 0 ; y < chars.length ; y++) {
                for (int x = 0; x < chars[y].length ; x++) {
                    String htmlRep = switch(chars[y][x]) {
                        case '<' -> "&lt;";
                        case '>' -> "&gt;";
                        case '&' -> "&amp;";
                        default  -> String.valueOf(chars[y][x]);
                    };
                    writer.write(htmlRep);
                }
                writer.newLine();
            }
            writer.write(
                """
                    </p>\n
                    </body>\n
                    </html>\n
                """);
        } catch(IOException e) {
            Logger.getGlobal().severe(String.format("Failed to write to \"%s\"", filename));
        }
    }
}
