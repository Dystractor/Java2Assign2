package application.pane;

import java.io.Closeable;
import java.io.IOException;

public class TeConnect {
  public static void close(Closeable ... connections) {
    for (Closeable Connect : connections) {
      if(Connect != null) {
        try {
          Connect.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
