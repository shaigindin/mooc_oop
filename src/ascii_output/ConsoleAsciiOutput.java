package ascii_output;

public class ConsoleAsciiOutput implements AsciiOutput{
    @Override
    public void output(char[][] chars) {
        for (int y = 0; y < chars.length ; y++) {
            for (int x = 0; x < chars[y].length; x++) {
                System.out.print(chars[y][x] + " ");
            }
            System.out.println();
        }
    }
}
