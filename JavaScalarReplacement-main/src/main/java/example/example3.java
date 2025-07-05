package example;
public class example3 {
    public static void main(String[] args) {
        foo();
    }
    static void foo() {
        Point p = new Point();
        p.x=10;
        p.y=20;
        Point q = new Point();
        q.x=5;
        q.y=15;
        Point r = new Point();
        r.x = 2;
        r.y = 4;
        int pqr = 34;
        int z = 30;
        bar(p, z);
        bar(q, z);
        bar(r,pqr);
    }
    static void bar(Point p, int z) {
        System.out.println(p.x+z);
    }
    static class Point {
        int x, y;
    }
}