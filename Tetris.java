package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

public class Tetris extends JFrame implements ActionListener, KeyListener {

    private final int WIDTH = 10;
    private final int HEIGHT = 20;
    private final int CELL_SIZE = 30;

    private boolean[][] board;
    private int currentPieceX, currentPieceY;
    private boolean[][] currentPiece;
    private Timer timer;

    public Tetris() {
        setTitle("Tetris");
        setSize(WIDTH * CELL_SIZE, HEIGHT * CELL_SIZE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        board = new boolean[WIDTH][HEIGHT];
        currentPieceX = WIDTH / 2 - 1;
        currentPieceY = 0;
        currentPiece = generateRandomPiece();

        timer = new Timer(500, this);
        timer.start();

        addKeyListener(this);
        setFocusable(true);
    }

    public boolean[][] generateRandomPiece() {
        Random random = new Random();
        int pieceIndex = random.nextInt(Pieces.values().length);
        return Pieces.values()[pieceIndex].getRotation(0);
    }

    private void drawPiece(Graphics g) {
        g.setColor(Color.CYAN);
        for (int i = 0; i < currentPiece.length; i++) {
            for (int j = 0; j < currentPiece[0].length; j++) {
                if (currentPiece[i][j]) {
                    int x = (currentPieceX + i) * CELL_SIZE;
                    int y = (currentPieceY + j) * CELL_SIZE;
                    g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                }
            }
        }
    }

    private void drawBoard(Graphics g) {
        g.setColor(Color.BLACK);
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (board[i][j]) {
                    g.fillRect(i * CELL_SIZE, j * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
            }
        }
    }

    private void checkLines() {
        for (int j = HEIGHT - 1; j >= 0; j--) {
            boolean lineFull = true;
            for (int i = 0; i < WIDTH; i++) {
                if (!board[i][j]) {
                    lineFull = false;
                    break;
                }
            }
            if (lineFull) {
                removeLine(j);
                moveLinesDown(j);
            }
        }
    }

    private void removeLine(int line) {
        for (int i = 0; i < WIDTH; i++) {
            board[i][line] = false;
        }
    }

    private void moveLinesDown(int line) {
        for (int j = line - 1; j >= 0; j--) {
            for (int i = 0; i < WIDTH; i++) {
                board[i][j + 1] = board[i][j];
                board[i][j] = false;
            }
        }
    }

    private boolean isValidMove(int x, int y, boolean[][] piece) {
        for (int i = 0; i < piece.length; i++) {
            for (int j = 0; j < piece[0].length; j++) {
                if (piece[i][j]) {
                    int newX = x + i;
                    int newY = y + j;
                    if (newX < 0 || newX >= WIDTH || newY < 0 || newY >= HEIGHT || board[newX][newY]) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void mergePiece() {
        for (int i = 0; i < currentPiece.length; i++) {
            for (int j = 0; j < currentPiece[0].length; j++) {
                if (currentPiece[i][j]) {
                    int x = currentPieceX + i;
                    int y = currentPieceY + j;
                    board[x][y] = true;
                }
            }
        }
    }

    private void gameOver() {
        timer.stop();
        JOptionPane.showMessageDialog(this, "Game Over", "Game Over", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        drawPiece(g);
        drawBoard(g);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isValidMove(currentPieceX, currentPieceY + 1, currentPiece)) {
            currentPieceY++;
        } else {
            mergePiece();
            checkLines();
            currentPieceX = WIDTH / 2 - 1;
            currentPieceY = 0;
            currentPiece = generateRandomPiece();
            if (!isValidMove(currentPieceX, currentPieceY, currentPiece)) {
                gameOver();
            }
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        switch (keyCode) {
            case KeyEvent.VK_LEFT:
                if (isValidMove(currentPieceX - 1, currentPieceY, currentPiece)) {
                    currentPieceX--;
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (isValidMove(currentPieceX + 1, currentPieceY, currentPiece)) {
                    currentPieceX++;
                }
                break;
            case KeyEvent.VK_DOWN:
                if (isValidMove(currentPieceX, currentPieceY + 1, currentPiece)) {
                    currentPieceY++;
                }
                break;
            case KeyEvent.VK_UP:
                boolean[][] rotatedPiece = Pieces.rotatePiece(currentPiece);
                if (isValidMove(currentPieceX, currentPieceY, rotatedPiece)) {
                    currentPiece = rotatedPiece;
                }
                break;
        }
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Tetris tetris = new Tetris();
            tetris.setVisible(true);
        });
    }

    enum Pieces {
        // Define different Tetris pieces and their rotations
        I(new boolean[][]{{true, true, true, true}}),
        J(new boolean[][]{{true, false, false}, {true, true, true}}),
        L(new boolean[][]{{false, false, true}, {true, true, true}}),
        O(new boolean[][]{{true, true}, {true, true}}),
        S(new boolean[][]{{false, true, true}, {true, true, false}}),
        T(new boolean[][]{{false, true, false}, {true, true, true}}),
        Z(new boolean[][]{{true, true, false}, {false, true, true}});

        private boolean[][][] rotations;

        Pieces(boolean[][]... rotations) {
            this.rotations = new boolean[rotations.length][][];
            for (int i = 0; i < rotations.length; i++) {
                this.rotations[i] = rotations[i];
            }
        }

        public boolean[][] getRotation(int rotationIndex) {
            return rotations[rotationIndex % rotations.length];
        }

        public static boolean[][] rotatePiece(boolean[][] piece) {
            int width = piece.length;
            int height = piece[0].length;
            boolean[][] rotatedPiece = new boolean[height][width];
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    rotatedPiece[j][width - 1 - i] = piece[i][j];
                }
            }
            return rotatedPiece;
        }
    }
}
