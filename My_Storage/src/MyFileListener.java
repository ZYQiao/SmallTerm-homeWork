  
import java.io.File;  
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;  
  
/** 
 * �Զ����ļ������� 
 * @author   
 * @date    2010-11-16 
 * @file    org.demo.file.MyFileListener.java 
 */  
public class MyFileListener extends FileAlterationListenerAdaptor{  
    @Override  
    public void onFileCreate(File file) {  
        System.out.println("[�½�]:" + file.getAbsolutePath());  
    }  
    @Override  
    public void onFileChange(File file) {  
        System.out.println("[�޸�]:" + file.getAbsolutePath());  
    }  
    @Override  
    public void onFileDelete(File file) {  
        System.out.println("[ɾ��]:" + file.getAbsolutePath());  
    }  
}  