package boardgame;

public class BoardExecption extends RuntimeException{
	private static final long serialVersionUID = 1L;
	
	public BoardExecption(String msg) {
		super(msg);
	}
}
