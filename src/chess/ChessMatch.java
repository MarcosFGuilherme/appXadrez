package chess;

import java.nio.channels.IllegalSelectorException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.Bishop;
import chess.pieces.King;
import chess.pieces.Knight;
import chess.pieces.Pawn;
import chess.pieces.Queen;
import chess.pieces.Rook;

public class ChessMatch {
	private int turn;
	private Color currentPlayer;
	private Board board;
	private boolean check;
	private boolean checkMate;
	private ChessPiece enPassantVulnerable;
	private ChessPiece promoted;

	private List<Piece> piecesOnTheBoard = new ArrayList<>();
	private List<Piece> capturePieces = new ArrayList<>();

	public ChessMatch() {
		board = new Board(8, 8);
		turn = 1;
		currentPlayer = Color.WHITE;
		initialSetup();
	}

	public int getTurn() {
		return turn;
	}

	public Color getCurrentPlayer() {
		return currentPlayer;
	}

	public boolean getCheck() {
		return check;
	}

	public boolean getCheckMate() {
		return checkMate;
	}

	public ChessPiece getEnPassantVulnerable() {
		return enPassantVulnerable;
	}
	public ChessPiece getPromoted() {
		return promoted;
	}
	public ChessPiece[][] getPieces() {
		ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
		for (int lin = 0; lin < board.getRows(); lin++) {
			for (int col = 0; col < board.getColumns(); col++) {
				mat[lin][col] = (ChessPiece) board.piece(lin, col);
			}
		}
		return mat;
	}

	public boolean[][] possibleMoves(ChessPosition sourcePosition) {
		Position position = sourcePosition.toPosition();
		ValidadeSourcePosition(position);
		return board.piece(position).PossibleMoves();
	}

	public ChessPiece PerformChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
		Position source = sourcePosition.toPosition();
		Position target = targetPosition.toPosition();
		ValidadeSourcePosition(source);
		ValidateTargetPosition(source, target);
		Piece capturePiece = MakeMove(source, target);

		if (testCheck(currentPlayer)) {
			undoMove(source, target, capturePiece);
			throw new ChessException("You can't put yourself in check.");
		}

		ChessPiece movePiece = (ChessPiece) board.piece(target);
		
		// #Specila move Pomotion
		promoted = null;
		
		if (movePiece instanceof Pawn) {
			int iPosPro = (movePiece.getColor() == Color.WHITE)? 0:7;
			if (target.getRow() == iPosPro) {
				promoted = (ChessPiece)board.piece(target);
				promoted = replacePromotedPiece("Q");
				
			}
		}

		check = (testCheck(opponent(currentPlayer))) ? true : false;

		if (testCheckMate(opponent(currentPlayer))) {
			checkMate = true;
		} else {
			nextTurn();
		}

		// #specialmove En Passant
		if (movePiece instanceof Pawn && (Math.abs(target.getRow() - source.getRow()) == 2)) {
			enPassantVulnerable = movePiece;
		} else {
			enPassantVulnerable = null;
		}

		return (ChessPiece) capturePiece;
	}
	
	public ChessPiece replacePromotedPiece(String type) {
		if( promoted == null) {
			throw new  IllegalStateException("There is no piece to be promoted.");
		}
		if( !type.equals("B") && !type.equals("N") && !type.equals("Q") && !type.equals("R")) {
			throw new InvalidParameterException("Invalid type for promotion.");
		}
		
		Position pos = promoted.getChessPosition().toPosition();
		Piece piece = board.removePiece(pos);
		piecesOnTheBoard.remove(piece);
		
		ChessPiece newPiece = newPiece(type, promoted.getColor());
		board.placePiece(newPiece, pos);
		piecesOnTheBoard.add(newPiece);
		
		return newPiece;
	}
	
	private ChessPiece newPiece (String type, Color color) {
		if (type.equals("B")) return new Bishop(board, color);
		if (type.equals("N")) return new Knight(board, color);
		if (type.equals("Q")) return new Queen(board, color);
		return new Rook(board, color);
	}
	
	public Piece MakeMove(Position source, Position target) {
		ChessPiece piece = (ChessPiece) board.removePiece(source);
		piece.incriseMoveCount();

		Piece capturePiece = board.removePiece(target);
		board.placePiece(piece, target);

		if (capturePiece != null) {
			piecesOnTheBoard.remove(capturePiece);
			capturePieces.add(capturePiece);
		}

		if (piece instanceof King) {
			int h = target.getColumn() - source.getColumn();
			if (Math.abs(h) == 2) {
				int s = (h < 0) ? -4 : 3;
				int t = (h < 0) ? -1 : 1;
				Position sourceR = new Position(source.getRow(), source.getColumn() + s);
				Position targetR = new Position(source.getRow(), source.getColumn() + t);
				ChessPiece rook = (ChessPiece) board.removePiece(sourceR);
				board.placePiece(rook, targetR);
				rook.incriseMoveCount();
			}
		}

		// #specialmove En Passant
		if (piece instanceof Pawn) {
			if (source.getColumn() != target.getColumn() && capturePiece == null) {
				int iLin = (piece.getColor() == Color.WHITE) ? 1 : -1;
				Position oPiecePosition = new Position(target.getRow() + iLin, target.getColumn());
				capturePiece = board.removePiece(oPiecePosition);
				capturePieces.add(capturePiece);
				piecesOnTheBoard.remove(capturePiece);
			}
		}

		return capturePiece;
	}

	private void undoMove(Position source, Position target, Piece capturePiece) {
		ChessPiece piece = (ChessPiece) board.removePiece(target);
		piece.decriseMoveCount();

		board.placePiece(piece, source);
		if (capturePiece != null) {
			board.placePiece(capturePiece, target);
			capturePieces.remove(capturePiece);
			piecesOnTheBoard.add(capturePiece);
		}

		if (piece instanceof King) {
			int h = target.getColumn() - source.getColumn();
			if (Math.abs(h) == 2) {
				int s = (h < 0) ? -4 : 3;
				int t = (h < 0) ? -1 : 1;
				Position sourceR = new Position(source.getRow(), source.getColumn() + s);
				Position targetR = new Position(source.getRow(), source.getColumn() + t);
				ChessPiece rook = (ChessPiece) board.removePiece(targetR);
				board.placePiece(rook, sourceR);
				rook.decriseMoveCount();
			}
		}

		// #specialmove En Passant
		if (piece instanceof Pawn) {
			if (source.getColumn() != target.getColumn() && capturePiece == enPassantVulnerable) {
				ChessPiece oPiece = (ChessPiece)board.removePiece(target);
				int iLin = (piece.getColor() == Color.WHITE) ? 3 : 4;
				Position oPiecePosition = new Position(iLin, target.getColumn());
				board.placePiece(oPiece, oPiecePosition);
			}
		}
	}

	public void ValidadeSourcePosition(Position position) {
		if (!board.thereIsAPiece(position)) {
			throw new ChessException("There is no piece source position.");
		}
		if (currentPlayer != ((ChessPiece) board.piece(position)).getColor()) {
			throw new ChessException("The chosen piece is not yours.");
		}
		if (!board.piece(position).IsThereAnyPossibleMove()) {
			throw new ChessException("There is no possible moves for the chosen piece.");
		}
	}

	public void ValidateTargetPosition(Position source, Position target) {
		if (!board.piece(source).PossibleMove(target)) {
			throw new ChessException("The chosen piece can't move to target position.");
		}
	}

	public void nextTurn() {
		turn++;
		currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}

	private Color opponent(Color color) {
		return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}

	private ChessPiece king(Color color) {
		List<Piece> kings = piecesOnTheBoard.stream().filter(x -> ((ChessPiece) x).getColor() == color)
				.collect(Collectors.toList());
		for (Piece p : kings) {
			if (p instanceof King) {
				return (ChessPiece) p;
			}
		}
		throw new IllegalStateException("There is no " + color + " king on the board.");
	}

	private boolean testCheck(Color color) {
		Position kingPosition = king(color).getChessPosition().toPosition();
		List<Piece> opponentPieces = piecesOnTheBoard.stream()
				.filter(x -> ((ChessPiece) x).getColor() == opponent(color)).collect(Collectors.toList());
		for (Piece p : opponentPieces) {
			boolean[][] mat = p.PossibleMoves();
			if (mat[kingPosition.getRow()][kingPosition.getColumn()]) {
				return true;
			}
		}
		return false;
	}

	private boolean testCheckMate(Color color) {
		if (!testCheck(color)) {
			return false;
		}
		List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece) x).getColor() == color)
				.collect(Collectors.toList());
		for (Piece p : list) {
			boolean[][] mat = p.PossibleMoves();
			for (int l = 0; l < board.getRows(); l++) {
				for (int c = 0; c < board.getColumns(); c++) {
					if (mat[l][c]) {
						Position source = ((ChessPiece) p).getChessPosition().toPosition();
						Position target = new Position(l, c);
						Piece capturePiece = MakeMove(source, target);
						boolean testCheck = testCheck(color);
						undoMove(source, target, capturePiece);
						if (!testCheck) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	private void placeNewPiece(char column, int row, ChessPiece piece) {
		board.placePiece(piece, new ChessPosition(column, row).toPosition());
		piecesOnTheBoard.add(piece);

	}

	private void initialSetup() {
		/*
		 * placeNewPiece('c', 1, new Rook(board, Color.WHITE)); placeNewPiece('c', 2,
		 * new Rook(board, Color.WHITE)); placeNewPiece('d', 2, new Rook(board,
		 * Color.WHITE)); placeNewPiece('e', 2, new Rook(board, Color.WHITE));
		 * placeNewPiece('e', 1, new Rook(board, Color.WHITE)); placeNewPiece('d', 1,
		 * new King(board, Color.WHITE));
		 * 
		 * placeNewPiece('c', 7, new Rook(board, Color.BLACK)); placeNewPiece('c', 8,
		 * new Rook(board, Color.BLACK)); placeNewPiece('d', 7, new Rook(board,
		 * Color.BLACK)); placeNewPiece('e', 7, new Rook(board, Color.BLACK));
		 * placeNewPiece('e', 8, new Rook(board, Color.BLACK)); placeNewPiece('d', 8,
		 * new King(board, Color.BLACK));
		 * 
		 * placeNewPiece('h', 7, new Rook(board, Color.WHITE)); placeNewPiece('d', 1,
		 * new Rook(board, Color.WHITE)); placeNewPiece('e', 1, new King(board,
		 * Color.WHITE));
		 * 
		 * placeNewPiece('b', 8, new Rook(board, Color.BLACK)); placeNewPiece('a', 8,
		 * new King(board, Color.BLACK));
		 */
		placeNewPiece('a', 1, new Rook(board, Color.WHITE));
		placeNewPiece('b', 1, new Knight(board, Color.WHITE));
		placeNewPiece('c', 1, new Bishop(board, Color.WHITE));
		placeNewPiece('d', 1, new Queen(board, Color.WHITE));
		placeNewPiece('e', 1, new King(board, Color.WHITE, this));
		placeNewPiece('f', 1, new Bishop(board, Color.WHITE));
		placeNewPiece('g', 1, new Knight(board, Color.WHITE));
		placeNewPiece('h', 1, new Rook(board, Color.WHITE));
		placeNewPiece('a', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('b', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('c', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('d', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('e', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('f', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('g', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('h', 2, new Pawn(board, Color.WHITE, this));

		placeNewPiece('a', 8, new Rook(board, Color.BLACK));
		placeNewPiece('b', 8, new Knight(board, Color.BLACK));
		placeNewPiece('c', 8, new Bishop(board, Color.BLACK));
		placeNewPiece('d', 8, new Queen(board, Color.BLACK));
		placeNewPiece('e', 8, new King(board, Color.BLACK, this));
		placeNewPiece('f', 8, new Bishop(board, Color.BLACK));
		placeNewPiece('g', 8, new Knight(board, Color.BLACK));
		placeNewPiece('h', 8, new Rook(board, Color.BLACK));
		placeNewPiece('a', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('b', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('c', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('d', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('e', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('f', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('g', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('h', 7, new Pawn(board, Color.BLACK, this));

	}

}
