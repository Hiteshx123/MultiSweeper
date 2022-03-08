import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Minesweeper extends JFrame implements ActionListener, MouseListener
{
	JPanel boardPanel;
	JToggleButton[][]board;
	int dimR = 9, dimC = 9;

	boolean firstClick = true, gameOn = true;
	int numMines = 10;

	public Minesweeper()
	{
		createBoard(dimR, dimC);
		this.setVisible(true);
	}

	public void createBoard(int row, int col)
	{
		if (boardPanel!=null) this.remove(boardPanel);

		board = new JToggleButton[row][col];
		boardPanel = new JPanel();
		boardPanel.setLayout(new GridLayout(row, col));

		for (int r = 0; r<board.length; r++)
		{
			for (int c = 0; c<board[r].length; c++)
			{
				board[r][c] = new JToggleButton();
				board[r][c].putClientProperty("row", r);
				board[r][c].putClientProperty("col", c);
				board[r][c].putClientProperty("state", 0);
				board[r][c].addMouseListener(this);
				boardPanel.add(board[r][c]);
			}

		}

		this.add(boardPanel);
		this.setSize(board[0].length*35, board.length*35);

	}

	public void setBombsAndNums(int selectedRow, int selectedCol)
	{
		int count = numMines;

		while(count > 0)
		{
			int row = (int)(Math.random()*dimR);
			int col = (int)(Math.random()*dimC);

			int state = Integer.parseInt("" + board[row][col].getClientProperty("state"));

			if (state==0 && (row!=selectedRow || col!= selectedCol))
			{
				board[row][col].putClientProperty("state", -1);
				count--;
			}

		}

		/*
		for (int r = 0; r<dimR; r++)
		{
			for (int c = 0; c<dimC; c++)
			{
				int state = Integer.parseInt("" + board[r][c].getClientProperty("state"));
				count = 0;

				for (int lilR = r-1; lilR <=  r+1; lilR++)
				{
					for (int lilC = c-1; lilC <= c+1; lilC++)
					{
						try{int toggleState = Integer.parseInt("" + board[lilR][lilC].getClientProperty("state"));
							if (state==-1) count++;
						}catch(ArrayIndexOutOfBoundsException e){}
					}
				}

			board[r][c].putClientProperty("state", count);

			}

		}
		*/

	}

	public void colorChange(int r, int c, int s)
	{
		Color color;
		switch(s)
		{
			case 1: color = Color.BLUE; break;
			case 2: color = Color.GREEN; break;
			case 3: color = Color.RED; break;
			case 4: color = new Color(128, 0, 128); break;
			case 5: color = new Color(0, 128, 0); break;
			case 6: color = Color.MAGENTA; break;

            case 7: color = Color.CYAN; break;
			default: color = Color.ORANGE; break;
		}

		if (s > 0)
		{
			board[r][c].setForeground(color);
		}

	}

	public void actionPerformed(ActionEvent e)
	{

	}

	public void mouseReleased(MouseEvent e)
	{
		int row = (int)((JToggleButton)e.getComponent()).getClientProperty("row");
		int col = (int)((JToggleButton)e.getComponent()).getClientProperty("col");

		if (gameOn)
		{
			if (e.getButton()==MouseEvent.BUTTON1 && board[row][col].isEnabled())
			{
				if (firstClick)
				{
					setBombsAndNums(row, col);
					firstClick = false;
				}

				int state = (int)board[row][col].getClientProperty("state");
				if (state==-1)
				{
					board[row][col].setContentAreaFilled(false);
					board[row][col].setOpaque(true);
					board[row][col].setBackground(Color.BLACK);
					board[row][col].setEnabled(false);
				}
				/*
				else
				{
					colorChange(row, col, state);
				}
				*/

			}
		}

	}

	public void mouseClicked(MouseEvent e)
	{

	}

	public void mousePressed(MouseEvent e)
	{

	}

	public void mouseExited(MouseEvent e)
	{

	}

	public void mouseEntered(MouseEvent e)
	{

	}

    public static void main(String[]args){
        Minesweeper sMinesweeper = new Minesweeper();
    }

}