package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.Color;

public class King extends ChessPiece {

	private ChessMatch chessMatch;
	
	public King(Board board, Color color, ChessMatch chessMatch) {
		super(board, color);
		this.chessMatch = chessMatch;
	}
	
	private boolean canMove(Position position) {
		ChessPiece p = (ChessPiece)getBoard().piece(position);
		return p == null || p.getColor() != getColor();
	}
	
	private boolean testRookCastling(Position position) {
		ChessPiece p = (ChessPiece)getBoard().piece(position);
		return p != null && p instanceof Rook && p.getColor() == getColor() && p.getMoveCount() == 0;
	}
	
	@Override
	public String toString() {
		return "K";
	}

	@Override
	public boolean[][] PossibleMoves() {
		boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];
		Position p = new Position(0, 0);
		
		for (int l = -1;l<=1;l++) {
			for (int c=-1;c<=1;c++) {
				p.setValues(position.getRow()+l, position.getColumn()+c);
				if(getBoard().positionExists(p) && canMove(p)) {
					mat[p.getRow()][p.getColumn()] = true;
				}
			}
		}
		// #specialmoves Castling
		if(getMoveCount()==0 && !chessMatch.getCheck()) {
			boolean cl = false;
			boolean cr = false;
						
			p.setValues(position.getRow(), position.getColumn() - 4);
			if (testRookCastling(p)) cl = true;
			p.setValues(position.getRow(), position.getColumn() + 3);
			if (testRookCastling(p)) cr = true;
			
			for (int c = -3; c<3; c++) {
				p.setValues(position.getRow(), position.getColumn() + c);
				if (c < 0) {
					if (getBoard().piece(p) != null) cl = false;
				}
				else if  (c > 0) {
					if (getBoard().piece(p) != null) cr = false;
				}
			}
			mat[position.getRow()][position.getColumn() - 2] = cl;
			mat[position.getRow()][position.getColumn() + 2] = cr;
		}
		
		return mat;
	}
	
}
