package chess;

import boardgame.Board;
import boardgame.Position;
import chess.pieces.King;
import chess.pieces.Rook;

public class ChessMatch {
	private Board board;

	public ChessMatch() {
		board = new Board(8, 8);
		initialSetup();
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
	
	private void initialSetup() {
		board.placePiece(new  Rook(board,Color.WHITE),new Position(1, 2));
		board.placePiece(new  King(board,Color.BLACK),new Position(0, 4));
		board.placePiece(new  King(board,Color.WHITE),new Position(7, 4));
	}
}
