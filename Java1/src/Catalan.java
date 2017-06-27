
public class Catalan {
	
	public static int answers = 0;
	
	//请实现go函数
	public static void go(Deque from, Deque to, Stack s) {
		
		if(from.size() == 0){
			if(s.empty()){
				answers++;
			}else{
				to.addLast(s.pop());
				go(from,to,s);
			}
		}else{
			if(s.empty()){
				s.push(from.getLast());
				from.removeLast();
				go(from,to,s);
			}else{
				Deque f = from,t = to;
				Stack st = s;
				
			}
			
		}


	}

	public static void main(String[] args) {
		Deque from = new Deque();
		Deque to = new Deque();
		Stack s = new Stack();
		
		for(int i=1;i<=7;i++) {
			from.addLast(i);
		}
		
		go(from, to, s);


		System.out.println(answers);
		

	}

}
