#ifndef MYPLAYER_H 
 #define MYPLAYER_H 
  
 #include <iostream> 
 #include <string> 
 #include <stdio.h> 
 #include <Windows.h> 
 #include <mmsystem.h>	// mci库头文件 
 #pragma comment(lib, "winmm.lib")	// 指定MCI库，mciSendString函数的定义在winmm.lib中 
  
 using namespace std; 
  
 bool flag = 1; 
 char name[50]; 

   // 播放当前曲 
 void play(const char *name)          //播放音乐 
 { 
     char cmd[MAX_PATH] = {0}; 
     char pathname[MAX_PATH] = {0}; 
  
     // 加路径 
     sprintf(pathname, ".\\music\\%s", name); 
     // GetShortPathName用来转换短名，要求被转换的歌名必须能在指定目录下找到文件，否则转换失败。 
     // 第一个参数：源文件名，第二个参数：目的文件名，第三个参数：目的数组长度。 
     GetShortPathName(pathname, pathname, MAX_PATH); 
  
  
     // 定义发往MCI的命令，cmd指定命令存储的数组，后面参数跟printf()相同 
     sprintf(cmd, "open %s", pathname); 
     // 发送命令。 
     // 一、存储命令的数组首地址，二、接受MCI返回的信息，三、接受数组的长度，四、没用，NULL 
     mciSendString(cmd, "", 0, NULL); 
     sprintf(cmd, "play %s", pathname); 
     mciSendString(cmd, "", 0, NULL); 
 } 
  
  
 // 暂停当前曲
 void pause(const char *name)        // 暂停播放 
 { 
     char cmd[MAX_PATH] = {0}; 
     char pathname[MAX_PATH] = {0}; 
  
     // 加路径 
     sprintf(pathname, ".\\music\\%s", name); 
     // GetShortPathName用来转换短名，要求被转换的歌名必须能在指定目录下找到文件，否则转换失败。 
     // 第一个参数：源文件名，第二个参数：目的文件名，第三个参数：目的数组长度。 
     GetShortPathName(pathname, pathname, MAX_PATH); 
  
     sprintf(cmd, "pause %s", pathname); 
     mciSendString(cmd,"",0,NULL); 
 } 


 // 停止当前曲
 void Stop(const char *name) 
 { 
	 flag = 1;
     char cmd[MAX_PATH] = {0}; 
     char pathname[MAX_PATH] = {0}; 
  
     // 加路径 
     sprintf(pathname, ".\\music\\%s", name); 
     // GetShortPathName用来转换短名，要求被转换的歌名必须能在指定目录下找到文件，否则转换失败。 
     // 第一个参数：源文件名，第二个参数：目的文件名，第三个参数：目的数组长度。 
     GetShortPathName(pathname, pathname, MAX_PATH); 
  
     sprintf(cmd, "stop %s", pathname); 
     mciSendString(cmd,"",0,NULL); 
     sprintf(cmd, "close %s", pathname); 
     mciSendString(cmd,"",0,NULL); 
 } 
  
  
  
  
 void Station(){ 
     if(!flag) 
         cout << "当前为播放状态" <<endl; 
     else 
         cout << "当前为停止状态" <<endl; 
 } 
  
 void Page1(int &c){ 
     system("cls"); 
     Station(); 
     cout << "1.播放控制"<<endl; 
     cout << "2.音量调节"<<endl; 
     cout << "0.退出"<<endl; 
     cout << "请输入操作类型：" <<endl; 
     cin >> c; 
 } 
  
 void Page2(int &c){ 
     system("cls"); 
     Station(); 
     cout << "1.播放/暂停" <<endl; 
     cout << "2.停止" <<endl; 
     cout << "3.上一曲" <<endl; 
     cout << "4.下一曲" <<endl; 
     cout << "0.退出" <<endl; 
     cout << "请输入操作类型：" <<endl; 
     cin>>c; 
 } 
  
 void Page3(int &c){ 
     system("cls"); 
     Station(); 
     cout << "1.音量增大" <<endl; 
     cout << "2.音量减小" <<endl; 
     cout << "0.退出" <<endl; 
     cout << "请输入操作类型：" <<endl; 
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
             cout << "请输入MP3媒体路径:"<<endl; 
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
