package gh2;

import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;
 /**
  *  @author Lyrine Yang
  *  */

public class GuitarHero {
    public static final String KEYBOARD = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
    public static final int KEYLENGTH = KEYBOARD.length();
    public static void main(String[] args) {
        GuitarString[] strings = new GuitarString[KEYLENGTH];
        for (int i = 0; i < KEYLENGTH; i += 1) {
            double frequency = 440.0 * Math.pow(2, (i - 24.0) / 12.0);
            strings[i] = new GuitarString(frequency);
        }
        final char EXIT_KEY = 27;
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                if (key == EXIT_KEY) {
                    System.out.println("Program exit.");
                    break;
                }
                int index = KEYBOARD.indexOf(key);
                if (index != -1) {
                    strings[index].pluck();
                }
            }
            double sample = 0.0;
            for (int i = 0; i < KEYLENGTH; i += 1) {
                sample += strings[i].sample();
            }
            StdAudio.play(sample);
            for (int i = 0; i < KEYLENGTH; i++) {
                strings[i].tic();
            }
        }
    }

}
