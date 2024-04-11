package kopo.springnosql;

public class MyTest2 {
    public static void main(String[] args) {
        // 상단 부분 출력
        for (int i = 0; i < 2; i++) {
            System.out.println("   +++");
        }

        // 중간 부분 출력
        for (int i = 0; i < 2; i++) {
            System.out.println("+++++++++");
        }

        // 하단 부분 출력
        for (int i = 0; i < 4; i++) {
            System.out.println("   +++");
        }

        for (int i = 0; i < 8; i++) {

        }
        int current = 1;
        int previous = 0;
        for (int i = 0; i < 6; i++) {
            int temp = previous;

            previous = current;

            current += temp;
        System.out.println(current);
        }
    }
}
