
public class Catalan {

	public static int answers = 0;

	// 请实现go函数
	public static void go(Deque from, Deque to, Stack s) {
		 if((from.size() == 0)&&(s.size()==0)&&(to.size() == 7)) {
		        answers++;
		        return;
		    }
		    if(from.size()!=0/*&&s.size()<5*/){//入栈
		        int v = from.getFirst();   
		        s.push(v);   
		        from.removeFirst();;
		        go(from,to,s);
		        s.pop();
		        from.addFirst(v);;//恢复断点
		    }
		    if(s.size()!=0/*&&s.size()<5*/) //出栈
		    {
		        int v = s.pop();
		        to.addFirst(v);;
		        go(from,to,s);
		        to.removeFirst();;
		        s.push(v);//恢复断点
		    }
		    return;

	}
	public static void main(String[] args) {
		Deque from = new Deque();
		Deque to = new Deque();
		Stack s = new Stack();

		for (int i = 1; i <= 7; i++) {
			from.addLast(i);
		}

		go(from, to, s);

		System.out.println(answers);

	}

}
