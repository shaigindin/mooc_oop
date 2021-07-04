/**
 * Inspired and partly copied from
 * https://github.com/korhner/asciimg/blob/95c7764a6abe0e893fae56b3b6b580e09e1de209/src/main/java/io/korhner/asciimg/image/AsciiImgCache.java
 * described in the blog:
 * https://dzone.com/articles/ascii-art-generator-java
 */

package ascii_art.img_to_char;

import java.awt.*;
import java.awt.image.BufferedImage;

class CharRenderer {

    private static final double X_OFFSET_FACTOR = 0.2;
    private static final double Y_OFFSET_FACTOR = 0.75;


    public static boolean[][] getImg(char c, int pixels, String fontName) {
        return render(c, pixels, fontName);
    }
    private static boolean[][] render(char c, int pixels, String fontName) {
        String charStr = Character.toString(c);
        Font font = new Font(fontName, Font.PLAIN, pixels);
        BufferedImage img = new BufferedImage(pixels, pixels, BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.getGraphics();
        g.setFont(font);
        int xOffset = (int)Math.round(pixels*X_OFFSET_FACTOR);
        int yOffset = (int)Math.round(pixels*Y_OFFSET_FACTOR);
        g.drawString(charStr, xOffset, yOffset);
        boolean[][] matrix = new boolean[pixels][pixels];
        for(int y = 0 ; y < pixels ; y++) {
            for(int x = 0 ; x < pixels ; x++) {
                matrix[y][x] = img.getRGB(x, y) == 0; //is the color black
            }
        }
        return matrix;
    }

    //for debugging
    public static void printBoolArr(boolean[][] arr) {
        for(int row = 0 ; row < arr.length ; row++) {
            for(int col = 0 ; col < arr[row].length ; col++) {
                System.out.print(arr[row][col]?' ':'#');
            }
            System.out.println();
        }
    }
}
