package image;

import java.awt.*;
import java.io.IOException;

public interface Image {
    Color getPixel(int x, int y);
    int getWidth();
    int getHeight();

    static Image fromFile(String filename) {
        try {
            return new FileImage(filename);
        } catch(IOException ioe) {
            return null;
        }
    }

    default Iterable<Color> pixels() {
        return new ImageIterableProperty<>(
                this, this::getPixel,1,1);
    }

    default Iterable<Image> squareSubImagesOfSize(int pixels) {
        return new ImageIterableProperty<>(
                this,
                (x,y)->new ImageView(this,x,y,pixels,pixels),
                pixels,
                pixels);
    }
}
