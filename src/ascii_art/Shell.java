package ascii_art;

import ascii_art.img_to_char.*;
import ascii_output.*;
import image.Image;

import java.util.*;
import java.util.function.Consumer;

public class Shell {
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";

    private static final String CMD_EXIT = "exit";
    private static final String FONT_NAME = "Courier New";
    private static final String OUTPUT_FILENAME = "out.html";
    private static final int INITIAL_PIXEL_SQUARE_SIZE = 32;


    private final Map<String, Consumer<String>> commandProcessors = Map.of(
            "add"   , this::addChars,
            "remove", this::remChars,
            "chars" , this::showChars,
            "render", this::render,
            "res"   , this::resChange
    );

    private Character[] charSet = {'a','b','c','d'};
    private int pixel_square_size;
    private final ImgCharMatcher charMatcher;
    private final List<AsciiOutput> outputs;
    private int chars_in_a_row;
    private final int image_width;

    public Shell(Image img) {
        charMatcher = new BrightnessImgCharMatcher(img, FONT_NAME);
        outputs = List.of(
                new HtmlAsciiOutput(OUTPUT_FILENAME, FONT_NAME)
        );
        pixel_square_size = INITIAL_PIXEL_SQUARE_SIZE;
        image_width = img.getWidth();
        chars_in_a_row = image_width / pixel_square_size;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.print(">>> ");
        String cmd = scanner.nextLine().trim();
        while(!cmd.toLowerCase().equals(CMD_EXIT)) {
            if(!cmd.equals("")){
                try {
                    String[] param = cmd.split(" ");
                    Consumer<String> func = commandProcessors.get(param[0]);
                    if(func != null)
                        func.accept(param[1]);
                    else
                        System.out.println(ANSI_RED +"Unsupported command"+ANSI_RESET);
                }
                catch (Exception e){
                    Consumer<String> func = commandProcessors.get(cmd);
                    if(func != null)
                        func.accept("");
                    else{
                        System.out.println(ANSI_RED +"Unsupported command"+ANSI_RESET);
                    }
                }
            }
            System.out.print(">>> ");
            cmd = scanner.nextLine();
        }
    }

    private void resChange(String s) {
        s = s.toLowerCase();
        try {
            pixel_square_size = Integer.parseInt(s);
            chars_in_a_row = image_width / pixel_square_size;
        }
        catch (Exception e){
            System.out.println(ANSI_RED+"Unsupported command: resolution must be an integer"+ANSI_RESET);
        }
        System.out.println(ANSI_RED+"pixels per square is " + pixel_square_size +ANSI_RESET);
    }

    private void render(String s) {
        var chars = charMatcher.chooseChars(chars_in_a_row, charSet);
        outputs.forEach(o->o.output(chars));
    }

    private void showChars(String s) {
        System.out.println(Arrays.toString(charSet));
    }

    private void remChars(String s) {
        if(charSet.length == 1){
            System.out.println(ANSI_RED+"Unsupported command: must be one char at use"+ANSI_RESET);
        }
        else if(s.length() != 1){
            System.out.println(ANSI_RED+"Unsupported command: must remove one char at once"+ANSI_RESET);
        }
        else{
            char c = s.charAt(0);
            if (c >= 'a' && c <= 'z'){
                remChar(c);
            }
            else{
                System.out.println(ANSI_RED+"Unsupported command: must be char between a-z"+ANSI_RESET);
            }
        }
    }

    private void remChar(char toRemove) {
        if(inCharSet(toRemove)){
            Character[] temp = new Character[charSet.length-1];
            int index = 0;
            for (char c:charSet){
                if(c!=toRemove){
                    temp[index] = c;
                    index++;
                }
            }
            charSet = temp;
        }
        else{
            System.out.println(ANSI_RED+"Unsupported command: input char not in use"+ANSI_RESET);
        }
    }

    private boolean inCharSet(char toRemove) {
        for (char c:charSet){
            if (c == toRemove) {
                return true;
            }
        }
        return false;
    }

    private void addChars(String s) {
        if(charSet.length == 1){
            System.out.println(ANSI_RED+"Unsupported command: must be one char at use"+ANSI_RESET);
        }
        else if(s.length() != 1){
            System.out.println(ANSI_RED+"Unsupported command: must remove one char at once"+ANSI_RESET);
        }
        else{
            char c = s.charAt(0);
            if (c >= 'a' && c <= 'z'){
                addChar(c);
            }
            else{
                System.out.println(ANSI_RED+"Unsupported command: must be char between a-z"+ANSI_RESET);
            }
        }
    }

    private void addChar(char toAdd) {
        if(inCharSet(toAdd)){
            System.out.println(ANSI_RED+"Unsupported command: input char already in use"+ANSI_RESET);
        }
        else{
            Character[] temp = new Character[charSet.length+1];
            int index=0;
            for (char c:charSet){
                    temp[index]=c;
                    index++;
                }
            temp[index] = toAdd;
            charSet = temp;
            Arrays.sort(charSet);
        }
    }
}
