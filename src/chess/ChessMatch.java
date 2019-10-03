package chess;

import boardgame.Board;

public class ChessMatch {
	private Board board;

	public ChessMatch() {
		board = new Board(8, 8);
	}
	
	public ChessPiece[][] getPieces(){
		ChessPiece [][] mat = new ChessPiece[board.getRows()][board.getColumns()];
		for (int lin=0;lin<board.getRows();lin++) {
			for (int col=0;col<board.getColumns();col++) {
				mat[lin][col] = (ChessPiece) board.piece(lin,col);
			}
		}
		return mat;
	}
	
}
