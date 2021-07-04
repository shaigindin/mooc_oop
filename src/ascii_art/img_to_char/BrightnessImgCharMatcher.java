package ascii_art.img_to_char;

import image.Image;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.Arrays;



public class BrightnessImgCharMatcher implements ImgCharMatcher {
    class CharBriPair {
        public char c;
        public Double bri;
        public CharBriPair(char c, double bri) { this.c = c; this.bri = bri; }
    }

    private static final int CHAR_RENDERING_RESOLUTION = 16;
    private static final double[] BRIGHTNESS_RGB_WEIGHTS = new double[] { 0.2126, 0.7152, 0.0722 };

    private final Image img;
    private final String fontName;

    public BrightnessImgCharMatcher(Image img, String fontName) {
        this.img = img;
        this.fontName = fontName;

    }

    public CharBriPair[] createPairs(Character[] charSet){
        CharBriPair[] charPair = new CharBriPair[charSet.length];
        for (int i=0; i<charSet.length; i++){
            charPair[i] = new CharBriPair(
                    charSet[i],
                    getCharBrightness(charSet[i], CHAR_RENDERING_RESOLUTION));
        }
        return charPair;
    }

    public static void main(String[] args){
//        boolean[][] cs = CharRenderer.getImg('M', 16, "Ariel");
        Character[] chars = {'a','b','c','d'};
        CharBriPair[] pairs = new BrightnessImgCharMatcher(null, "Ariel").createPairs(chars);
        Arrays.sort(pairs, Comparator.comparing(obj -> obj.bri));
        new BrightnessImgCharMatcher(null, "Ariel").stretchValues(pairs);
        for (CharBriPair p: pairs){
            System.out.printf("%c %f%n", p.c, p.bri);
        }
        Image image=Image.fromFile("/Users/shaigindin/ex6-noCollection/bored.jpeg");
        var imgBri = new BrightnessImgCharMatcher(null, "Ariel").calcImgBrightness(image);
        System.out.println(imgBri);
    }

    @Override
    public char[][] chooseChars(int numCharsInRow, Character[] charSet) {
        //part A - create class for char to brightness connection
        CharBriPair[] charPair = createPairs(charSet);

        //part b - sort array from dark to bright
        Arrays.sort(charPair, Comparator.comparing(obj -> obj.bri));

        //part c - strech
        stretchValues(charPair);

        // part d - iterate sub image and convert to asci art
        return converImageToAscii(charPair, numCharsInRow);
    }

    private char[][] converImageToAscii(CharBriPair[] charPair, int numCharsInRow) {
        int pixels = img.getWidth() / numCharsInRow;
        char[][] bestChars = new char[img.getHeight()/pixels][img.getWidth()/pixels];
        int x = 0, y = 0;
        for(Image subImage : img.squareSubImagesOfSize(pixels)) {
            var imgBri = calcImgBrightness(subImage);
            int charIndex = indexOfClosestInSorted(charPair, imgBri);
            bestChars[y][x] = charPair[charIndex].c;
            x++;
            if(x >= bestChars[y].length) {
                x = 0;
                y++;
            }
        }
        return bestChars;
    }

    public void stretchValues(CharBriPair[] charPair) {
        var minCharBri = charPair[0].bri;
        var maxCharBri = charPair[charPair.length-1].bri;
        var chrBrightnessScale = maxCharBri>minCharBri?1/(maxCharBri - minCharBri):1;
        for (CharBriPair charBriPair : charPair) {
            charBriPair.bri = (charBriPair.bri - minCharBri) * chrBrightnessScale;
        }
    }


    public double calcImgBrightness(Image img){
        ArrayList<Double> d = new ArrayList<>();
        img.pixels().forEach(o -> d.add(calcColorBrightness(o)));
        var brightnessSum = d.stream().mapToDouble(a -> a).sum();
        return brightnessSum / (img.getHeight() * img.getWidth());
    }



    private double getCharBrightness(char c, int pixels) {
        var charImg = CharRenderer.getImg(c, pixels, this.fontName);
        double charBrightnessSum = 0;
        for (boolean[] booleans : charImg) {
            for (boolean aBoolean : booleans) {
                charBrightnessSum += aBoolean ? 1 : 0;
            }
        }
        return charBrightnessSum / charImg.length / charImg[0].length;
    }

    private static double calcColorBrightness(Color c) {
        return (  BRIGHTNESS_RGB_WEIGHTS[0]*c.getRed()
                + BRIGHTNESS_RGB_WEIGHTS[1]*c.getGreen()
                + BRIGHTNESS_RGB_WEIGHTS[2]*c.getBlue())/255;
    }

    //didnt change this one but there are much simpler 
    private static int indexOfClosestInSorted(CharBriPair[] charPair, double value) {
        List<Double> list = new ArrayList<>();
        for (CharBriPair p: charPair){
            list.add(p.bri);
        }
        int index = Collections.binarySearch(list, value);
        if(index >= 0)
            return index;
        index = -index-1;//index is now the first bigger element, or size if all are smaller
        if(index == 0)
            return index;
        if(index == list.size())
            return index-1;
        return list.get(index)-value < value-list.get(index-1) ? index : index-1;
    }
}
