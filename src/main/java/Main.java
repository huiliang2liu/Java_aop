import com.lhl.aop.AnnotationLoadClass;

import java.lang.reflect.Method;

public class Main {
    public static void main(String[] args) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                AnnotationLoadClass loadClass = AnnotationLoadClass.getInstance();
                Test test = loadClass.newInstance(Test.class, Test.class.getName() + "$Test", null);
                loadClass.setError((Throwable error) -> {
                    error.printStackTrace();
                });
                loadClass.registerStatisticalTime(test, (Object object, Method method, long time) -> {
                    System.out.println("registerStatisticalTime " + method.getName() + " " + time);
                });
                loadClass.registerTimeOutError(test, (Object object, Method method, long time) -> {
                    System.out.println("registerTimeOutError " + method.getName() + " " + time);
                });
                System.out.println(test.getClass().getName());
                test.cupTest("", 0);
                test.cupTest("", 0);
                test.delayTest();
                test.ioTest();
                test.periodTest();
                test.singleTest();
                test.testError();
                test.testTimeOut();
            }
        }.start();
        for (int i = 0; i < 10; i++) {
            Runtime.getRuntime().gc();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            Thread.sleep(10000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        Class<? extends Test> dynamicType = new ByteBuddy()
//                // 实现一个Function子类
//                .subclass(Test.class)
//                .method(ElementMatchers.named("testTimeOut"))
//                // 拦截Function.apply调用，委托给GreetingInterceptor处理
//                .intercept(MethodDelegation.to(new GreetingInterceptor()))
//                .make()
//                .load(Main.class.getClassLoader())
//                .getLoaded();
//        try {
//            Test test = dynamicType.newInstance();
//            System.out.println(test.testTimeOut() + "  ====");
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
    }

    public static class GreetingInterceptor {
        // 方法签名随意
        public Object greet(Object argument) {
            System.out.println("daad");
            return "Hello from ";
        }
    }

}
