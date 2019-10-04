package boardgame;

public abstract class Piece {
	protected Position position;
	private Board board;
	
	public Piece(Board board) {
		this.board = board;
		position = null;
	}
	
	protected Board getBoard() {
		return board;
	}
	
	public abstract boolean[][] PossibleMoves();
	
	public boolean PossibleMove(Position position) {
		return PossibleMoves()[position.getRow()][position.getColumn()];
	}
	
	public boolean IsThereAnyPossibleMove() {
		boolean[][] mat = PossibleMoves();
		for (int l=0; l<mat.length;l++) {
			for (int c=0;c<mat.length;c++) {
				if(mat[l][c]) {
					return true;
				}
			}
		}
		return false;
	}
}
