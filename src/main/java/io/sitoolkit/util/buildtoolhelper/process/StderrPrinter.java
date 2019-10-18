package io.sitoolkit.util.buildtoolhelper.process;

public class StderrPrinter {

  public static void main(String[] args) {

    for (int i = 0; i < 100; i++) {
      StringBuilder sb = new StringBuilder();
      for (int j = 0; j < 100; j++) {
        sb.append("a");
      }
      System.err.println(sb);
    }
  }
}
