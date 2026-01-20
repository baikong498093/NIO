package NIO.c4_1;

public class Demo {
    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            // ||和|的区别
            // || 短路或，只要有一个为true，就会短路，不会继续执行后面的表达式
            // |  非短路或，无论前面的表达式是否为true，都会继续执行后面的表达式
            if (i==2 | i==5) {
                System.out.println(i);
            }
        }
    }
}
