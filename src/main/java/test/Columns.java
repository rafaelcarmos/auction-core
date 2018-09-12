package test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Columns {

    final List<List<String>> lines = new ArrayList<>();
    final List<Integer> maxLengths = new ArrayList<>();
    int numColumns = -1;

    public Columns addLine(String... line) {

        if (numColumns == -1) {
            numColumns = line.length;
            for (int column = 0; column < numColumns; column++) {
                maxLengths.add(0);
            }
        }

        if (numColumns != line.length) {
            throw new IllegalArgumentException();
        }

        for (int column = 0; column < numColumns; column++) {
            int length = Math
                    .max(
                            maxLengths.get(column),
                            line[column].length()
                    );
            maxLengths.set(column, length);
        }

        lines.add(Arrays.asList(line));

        return this;
    }

    public void print() {
        System.out.println(toString());
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        for (List<String> line : lines) {
            for (int i = 0; i < numColumns; i++) {
                result.append(pad(line.get(i), maxLengths.get(i) + 1));
            }
            result.append(System.lineSeparator());
        }
        return result.toString();
    }

    private String pad(String word, int newLength) {
        StringBuilder wordBuilder = new StringBuilder(word);
        while (wordBuilder.length() < newLength) {
            wordBuilder.append(" ");
        }
        word = wordBuilder.toString();
        word += "     ";
        return word;
    }
}
