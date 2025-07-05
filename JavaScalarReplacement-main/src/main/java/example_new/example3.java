package example;

public class example3 {

    public static void main(String[] args) {
        foo();
    }

    static void foo() {
        int p_x;
        int p_y;
        p_x = 10;
        p_y = 20;
        int q_x;
        int q_y;
        q_x = 5;
        q_y = 15;
        int r_x;
        int r_y;
        r_x = 2;
        r_y = 4;
        int pqr = 34;
        int z = 30;
        bar(p_x, z);
        bar(q_x, z);
        bar(r_x, pqr);
    }

    static void bar(int p_x, int z) {
        System.out.println(p_x + z);
    }

    static class Point {

        int x, y;
    }
}
