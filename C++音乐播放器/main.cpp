#ifndef MYPLAYER_H 
 #define MYPLAYER_H 
  
 #include <iostream> 
 #include <string> 
 #include <stdio.h> 
 #include <Windows.h> 
 #include <mmsystem.h>	// mci��ͷ�ļ� 
 #pragma comment(lib, "winmm.lib")	// ָ��MCI�⣬mciSendString�����Ķ�����winmm.lib�� 
  
 using namespace std; 
  
 bool flag = 1; 
 char name[50]; 

   // ���ŵ�ǰ�� 
 void play(const char *name)          //�������� 
 { 
     char cmd[MAX_PATH] = {0}; 
     char pathname[MAX_PATH] = {0}; 
  
     // ��·�� 
     sprintf(pathname, ".\\music\\%s", name); 
     // GetShortPathName����ת��������Ҫ��ת���ĸ�����������ָ��Ŀ¼���ҵ��ļ�������ת��ʧ�ܡ� 
     // ��һ��������Դ�ļ������ڶ���������Ŀ���ļ�����������������Ŀ�����鳤�ȡ� 
     GetShortPathName(pathname, pathname, MAX_PATH); 
  
  
     // ���巢��MCI�����cmdָ������洢�����飬���������printf()��ͬ 
     sprintf(cmd, "open %s", pathname); 
     // ������� 
     // һ���洢����������׵�ַ����������MCI���ص���Ϣ��������������ĳ��ȣ��ġ�û�ã�NULL 
     mciSendString(cmd, "", 0, NULL); 
     sprintf(cmd, "play %s", pathname); 
     mciSendString(cmd, "", 0, NULL); 
 } 
  
  
 // ��ͣ��ǰ��
 void pause(const char *name)        // ��ͣ���� 
 { 
     char cmd[MAX_PATH] = {0}; 
     char pathname[MAX_PATH] = {0}; 
  
     // ��·�� 
     sprintf(pathname, ".\\music\\%s", name); 
     // GetShortPathName����ת��������Ҫ��ת���ĸ�����������ָ��Ŀ¼���ҵ��ļ�������ת��ʧ�ܡ� 
     // ��һ��������Դ�ļ������ڶ���������Ŀ���ļ�����������������Ŀ�����鳤�ȡ� 
     GetShortPathName(pathname, pathname, MAX_PATH); 
  
     sprintf(cmd, "pause %s", pathname); 
     mciSendString(cmd,"",0,NULL); 
 } 


 // ֹͣ��ǰ��
 void Stop(const char *name) 
 { 
	 flag = 1;
     char cmd[MAX_PATH] = {0}; 
     char pathname[MAX_PATH] = {0}; 
  
     // ��·�� 
     sprintf(pathname, ".\\music\\%s", name); 
     // GetShortPathName����ת��������Ҫ��ת���ĸ�����������ָ��Ŀ¼���ҵ��ļ�������ת��ʧ�ܡ� 
     // ��һ��������Դ�ļ������ڶ���������Ŀ���ļ�����������������Ŀ�����鳤�ȡ� 
     GetShortPathName(pathname, pathname, MAX_PATH); 
  
     sprintf(cmd, "stop %s", pathname); 
     mciSendString(cmd,"",0,NULL); 
     sprintf(cmd, "close %s", pathname); 
     mciSendString(cmd,"",0,NULL); 
 } 
  
  
  
  
 void Station(){ 
     if(!flag) 
         cout << "��ǰΪ����״̬" <<endl; 
     else 
         cout << "��ǰΪֹͣ״̬" <<endl; 
 } 
  
 void Page1(int &c){ 
     system("cls"); 
     Station(); 
     cout << "1.���ſ���"<<endl; 
     cout << "2.��������"<<endl; 
     cout << "0.�˳�"<<endl; 
     cout << "������������ͣ�" <<endl; 
     cin >> c; 
 } 
  
 void Page2(int &c){ 
     system("cls"); 
     Station(); 
     cout << "1.����/��ͣ" <<endl; 
     cout << "2.ֹͣ" <<endl; 
     cout << "3.��һ��" <<endl; 
     cout << "4.��һ��" <<endl; 
     cout << "0.�˳�" <<endl; 
     cout << "������������ͣ�" <<endl; 
     cin>>c; 
 } 
  
 void Page3(int &c){ 
     system("cls"); 
     Station(); 
     cout << "1.��������" <<endl; 
     cout << "2.������С" <<endl; 
     cout << "0.�˳�" <<endl; 
     cout << "������������ͣ�" <<endl; 
     cin >> c; 
 } 
  
  
  

  
 void SorP(){ 
     if(flag) 
         play(name); 
     else 
         pause(name); 
     flag = !flag; 
 } 
  
  
  
 void Up(){ 
  
 } 
  
 void Down(){ 
  
 } 
  
  
  
  
 void volUp(){ 
  
 } 
  
 void volDown(){ 
  
 } 
  
  
 int main() 
 { 
     int c = -1; 
     while(1){ 
         if(!name[0]){ 
             system("cls"); 
             cout << "������MP3ý��·��:"<<endl; 
             gets(name);

         } 
         while(1){ 
             if(c == -1){ 
                 Page1(c); 
                 if(c != 1 && c != 2){ 
                     system("cls"); 
                     return 0; 
                 }else{ 
                     while(1){ 
                         if(c != 1 && c != 2 ){ 
                             break; 
                         }else if(c == 1){ 
                             Page2(c); 
                             switch (c){ 
                                 case 1: 
                                         SorP(); 
 
                                         c = 1; 
                                         break; 
                                 case 2: 
                                         Stop(name); 

                                         c = 1; 
                                         break; 
                                 case 3: 
                                         Up(); 
 
                                         c = 1; 
                                         break; 
                                 case 4: 
                                         Down(); 

                                         c = 1; 
                                         break; 
                                 case 0: 
                                         c = -1; 
                                         break; 
                                 default : 
                                         c = 1; 
                                         break; 
                             } 
                         }else if(c == 2){ 
                             Page3(c); 
                             switch (c){ 
                                 case 1: 
                                         volUp(); 

                                         c = 2; 
                                         break; 
                                 case 2: 
                                         volDown(); 

                                         c = 2; 
                                         break; 
                                 case 0: 
                                         c = -1; 
                                         break; 
                                 default : 
                                         c = 2; 
                                         break; 
                             } 
                         } 
                     } 
                 } 
             } 
         } 
     } 
 } 
  
  
  
 #endif // MYPLAYER_H 
