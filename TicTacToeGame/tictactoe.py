from abc import ABC, abstractmethod
from enum import Enum
from collections import deque
class PieceSymbol(Enum):
    X = "X"
    O = "O"

class PieceType(ABC):
    @abstractmethod
    def __init__(self, piece):
        self.piece = piece
        pass

class PieceX(PieceType):
    def __init__(self):
        super().__init__(PieceSymbol.X)

class PieceY(PieceType):
    def __init__(self):
        super().__init__(PieceSymbol.O)

class Player:
    def __init__(self, name: str, piece:PieceType):
        self.name = name
        self.piece = piece

    def move(self, board, X, Y):
        board.placePiece(X, Y, self.piece)
        return

class Board:
    def __init__(self, size):
        self.size = size
        self.grid = [[-1 for _ in range(size)] for _ in range(size)]
    
    def printBoard(self):
        #Print Logic
        pass

    def isTaken(self, X, Y):
        return self.grid[X][Y] != -1
    
    def placePiece(self, X, Y, piece):
        self.grid[X][Y] = piece.piece.value
        return
    
    def isWinner(self, X, Y, player):
        playerPiece = player.piece.piece.value
        size = self.size
        matchRow,matchCol,matchDia,matchAntiDia = True, True, True, True
        for i in range(size):
            if self.grid[X][i]!=playerPiece:
                matchRow = False
        
        for i in range(size):
            if self.grid[i][Y] !=playerPiece:
                matchCol = False
        
        for i in range(size):
            if self.grid[i][i] != playerPiece:
                matchDia = False
        
        for i in range(size):
            if self.grid[i][size-i-1] != playerPiece:
                matchAntiDia = False
        
        return matchAntiDia or matchDia or matchCol or matchRow
    
    def isFilled(self):
        for i in range(self.size):
            if -1 in self.grid[i]:
                return False
        return True

class TicTacToe:
    def __init__(self, boardSize, *players):
        self.gameBoard = Board(boardSize)
        self.queue = deque()
        self.queue.extend(players)
    
    def startGame(self):

        while True:
            if self.gameBoard.isFilled():
                return "Game Draw"
            player = self.queue.popleft()
            move = input("Give your next Move ")
            X, Y = int(move.split(" ")[0]), int(move.split(" ")[1])
            if self.gameBoard.isTaken(X, Y):
                print("Invalid Move, Try Again")
                queue.appendLeft(player)
                continue
            player.move(self.gameBoard, X, Y)
            if self.gameBoard.isWinner(X, Y, player):
                return "Winner is Player {0}".format(player.name)
            self.queue.append(player)
        return

if __name__ == "__main__": 
    PlayerX = Player("XP", PieceX())
    PlayerY = Player("Y", PieceY())
    game = TicTacToe(3, PlayerX, PlayerY)

    print(game.startGame())