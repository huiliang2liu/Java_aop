import com.lhl.aop.AnnotationLoadClass;
import com.lhl.aop.annotations.StatisticalTimeAnnotation;
import com.lhl.aop.annotations.ThreadAnnotation;
import com.lhl.aop.annotations.TimeOut;

public class Test {
    public void testError() {
        throw new RuntimeException("test");
    }

    @TimeOut(timeout = 3000)
    @StatisticalTimeAnnotation
    public void testTimeOut() {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @ThreadAnnotation(thread = AnnotationLoadClass.IO)
    public void ioTest() {
        System.out.println(Thread.currentThread().getName() + " ioTest");
    }

    @ThreadAnnotation(thread = AnnotationLoadClass.CUP)
    public String cupTest(String test, int t) {
        System.out.println(Thread.currentThread().getName() + " cupTest");
        return "";
    }

    @ThreadAnnotation(thread = AnnotationLoadClass.SINGLE)
    public void singleTest() {
        System.out.println(Thread.currentThread().getName() + " singleTest");
    }

    @ThreadAnnotation(thread = AnnotationLoadClass.DELAY, delay = 3000)
    public void delayTest() {
        System.out.println(Thread.currentThread().getName() + " delayTest");
    }

    int time = 0;

    @ThreadAnnotation(thread = AnnotationLoadClass.PERIOD, period = 1000)
    public boolean periodTest() {
        System.out.println(Thread.currentThread().getName() + " periodTest");
        synchronized (this) {
            time++;
            return time > 10;
        }
    }
}
