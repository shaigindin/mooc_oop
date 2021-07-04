package ascii_art.img_to_char;


public interface ImgCharMatcher {
    char[][] chooseChars(int numCharsInRow, Character[] charSet);
}
