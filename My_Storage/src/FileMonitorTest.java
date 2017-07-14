  
import java.util.concurrent.TimeUnit;  
import org.apache.commons.io.filefilter.FileFilterUtils;  
import org.apache.commons.io.monitor.FileAlterationMonitor;  
import org.apache.commons.io.monitor.FileAlterationObserver;  
  
/** 
 * �ļ���ز��� 
 * @author   
 * @date    2010-11-16 
 * @file    org.demo.file.FileMonitor.java 
 */  
public class FileMonitorTest {  
  
    /** 
     * @param args 
     * @throws Exception  
     */  
    public static void main(String[] args) throws Exception {  
        // ���Ŀ¼  
        String rootDir = "/Users/yuqiao/workspace/Server/";  
        // ��ѯ��� 5 ��  
        long interval = TimeUnit.SECONDS.toMillis(5);  
        //   
        FileAlterationObserver observer = new FileAlterationObserver(  
                                              rootDir,   
                                              FileFilterUtils.and(  
                                               FileFilterUtils.fileFileFilter(),  
                                               FileFilterUtils.suffixFileFilter(".java")),   
                                              null);  
        observer.addListener(new MyFileListener());  
        FileAlterationMonitor monitor = new FileAlterationMonitor(interval,observer);  
        // ��ʼ���  
        monitor.start();  
    }  
}  