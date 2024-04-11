package kopo.springnosql;

public class MyTest {
    public static void main(String[] args) {
        int n = 9; // 별의 개수 (홀수)

        // 윗부분
        for (int i = 0; i < n / 2; i++) {
            for (int j = 0; j < n; j++) {
                if (j == n / 2 - i || j == n / 2 + i) {
                    System.out.print("*");
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }

        // 가운데 부분
        for (int i = 0; i < n; i++) {
            System.out.print("*");
        }
        System.out.println();

        // 아랫부분
        for (int i = n / 2 - 1; i >= 0; i--) {
            for (int j = 0; j < n; j++) {
                if (j == n / 2 - i || j == n / 2 + i) {
                    System.out.print("*");
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }

        System.out.println();
        System.out.println();

        int size = 8; // 십자가의 크기

        // 십자가 그리기
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if ((i == size / 2) || (j == size / 2)) {
                    System.out.print("+");
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
    }

}
