package utils;

public class Stdout {
    public static <T> void showArray(T[] list) {
        for (int i = 0; i < list.length; i++) {
            T item = list[i];

            System.out.printf("%d. %s\n", i, item.toString());
        }
    }
}
