import java.awt.*;  
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;


public class Main
{
	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				new Initialize();
				Frame frame=new Frame();
				frame.setVisible(true);
				
				JButton surrender = new JButton("Surrender");
				surrender.addActionListener(new ActionListener() 
				{
					public void actionPerformed(ActionEvent e) 
					{
						int i;
						Global.turnNumber++;
						Global.goodToGo=false;
						Global.onGoing=false;
						Global.record[Global.turnNumber]=Global.record[Global.turnNumber-1];
						if(Global.turnNumber%2==0)
						{
							Global.record[Global.turnNumber]+="0-1";
							JOptionPane.showMessageDialog(frame,"Black wins!","Surrender!",JOptionPane.INFORMATION_MESSAGE);
						}
						else
						{
							Global.record[Global.turnNumber]+="1-0";
							JOptionPane.showMessageDialog(frame,"Black wins!","Surrender!",JOptionPane.INFORMATION_MESSAGE);
						}
						for(i=0;i<14;i++)
							Global.bitBoard[Global.turnNumber][i]=Global.bitBoard[Global.turnNumber-1][i];
						frame.fresh();
						frame.repaint();
					}
				});
				surrender.setBounds(570,67,100,30);
				frame.add(surrender);
				
				ChessBoard chessBoard=new ChessBoard(frame);
				frame.add(chessBoard);
			}
		});
	}
}

class Initialize
{
	public Initialize()
	{
		int x,y,i,j;
		
		Global.goodToGo=false;
		Global.onGoing=true;
		
		Global.record[1]="    1.";
		
		for(i=0;i<8;i++)
		{
			Global.bitBoard[1][0 ]+=((1L<<(i+0*8))+(1L<<(i+1*8)));
			Global.bitBoard[1][1 ]+=((1L<<(i+6*8))+(1L<<(i+7*8)));
			Global.bitBoard[1][12]+=(1L<<(i+8));
			Global.bitBoard[1][13]+=(1L<<(i+6*8));
		}
		Global.bitBoard[1][2 ]=(1L<<(4+0*8));
		Global.bitBoard[1][3 ]=(1L<<(4+7*8));
		Global.bitBoard[1][4 ]=(1L<<(3+0*8));
		Global.bitBoard[1][5 ]=(1L<<(3+7*8));
		Global.bitBoard[1][6 ]=((1L<<(0+0*8))+(1L<<(7+0*8)));
		Global.bitBoard[1][7 ]=((1L<<(0+7*8))+(1L<<(7+7*8)));
		Global.bitBoard[1][8 ]=((1L<<(2+0*8))+(1L<<(5+0*8)));
		Global.bitBoard[1][9 ]=((1L<<(2+7*8))+(1L<<(5+7*8)));
		Global.bitBoard[1][10]=((1L<<(1+0*8))+(1L<<(6+0*8)));
		Global.bitBoard[1][11]=((1L<<(1+7*8))+(1L<<(6+7*8)));

		for(y=0;y<8;y++)
			for(x=0;x<8;x++)
				Global.mask[x+y*8]=(1L<<(x+y*8));
		
		for(y=0;y<8;y++)
			for(x=0;x<8;x++)
			{		
				//king
				if(x-1>=0)
				{
					Global.kingAttack[x+y*8]+=(1L<<((x-1)+(y  )*8));
					if(y-1>=0)
					{
						Global.kingAttack[x+y*8]+=(1L<<((x-1)+(y-1)*8));
						Global.kingAttack[x+y*8]+=(1L<<((x  )+(y-1)*8));
					}
					if(y+1<8)
					{
						Global.kingAttack[x+y*8]+=(1L<<((x-1)+(y+1)*8));
						Global.kingAttack[x+y*8]+=(1L<<((x  )+(y+1)*8));
					}
				}
				if(x+1<8)
				{
					Global.kingAttack[x+y*8]+=(1L<<((x+1)+(y  )*8));
					if(y-1>=0)
						Global.kingAttack[x+y*8]+=(1L<<((x+1)+(y-1)*8));
					if(y+1<8)
						Global.kingAttack[x+y*8]+=(1L<<((x+1)+(y+1)*8));
				}
				//king ends
				
				//rook
				for(i=0;i<256;i++)
				{
					for(j=x-1;j>=0;j--)
						if((Global.mask[j]&i)==0)
						{
							Global.rookAttack_rank_ally [x+y*8][i]+=Global.mask[j];
							Global.rookAttack_rank_enemy[x+y*8][i]+=Global.mask[j];
						}
						else
						{
							Global.rookAttack_rank_enemy[x+y*8][i]+=Global.mask[j];
							break;
						}
					for(j=x+1;j<8;j++)
						if((Global.mask[j]&i)==0)
						{
							Global.rookAttack_rank_ally [x+y*8][i]+=Global.mask[j];
							Global.rookAttack_rank_enemy[x+y*8][i]+=Global.mask[j];
						}
						else
						{
							Global.rookAttack_rank_enemy[x+y*8][i]+=Global.mask[j];
							break;
						}
					Global.rookAttack_rank_ally [x+y*8][i]<<=(y*8);
					Global.rookAttack_rank_enemy[x+y*8][i]<<=(y*8);
					for(j=0;j<8;j++)
					{
						if((Global.rookAttack_rank_ally [y][i]&(1L<<j))!=0)
							Global.rookAttack_file_ally [x+y*8][i]+=Global.mask[j*8];
						if((Global.rookAttack_rank_enemy[y][i]&(1L<<j))!=0)
							Global.rookAttack_file_enemy[x+y*8][i]+=Global.mask[j*8];
					}
					Global.rookAttack_file_ally [x+y*8][i]<<=x;
					Global.rookAttack_file_enemy[x+y*8][i]<<=x;
				}
				//rook ends
				
				//bishop
				for(i=0;i<8;i++)
				{
					//应采取45度旋转位棋盘实验，因过于复杂暂时使用替代方案
				}
				//bishop ends
								
				//knight
				if(x-2>=0)
				{
					if(y-1>=0)
						Global.knightAttack[x+y*8]+=(1L<<((x-2)+(y-1)*8));
					if(y+1<8)
						Global.knightAttack[x+y*8]+=(1L<<((x-2)+(y+1)*8));
					
				}
				if(x-1>=0)
				{
					if(y-2>=0)
						Global.knightAttack[x+y*8]+=(1L<<((x-1)+(y-2)*8));
					if(y+2<8)
						Global.knightAttack[x+y*8]+=(1L<<((x-1)+(y+2)*8));
					
				}
				if(x+1<8)
				{
					if(y-2>=0)
						Global.knightAttack[x+y*8]+=(1L<<((x+1)+(y-2)*8));
					if(y+2<8)
						Global.knightAttack[x+y*8]+=(1L<<((x+1)+(y+2)*8));
				}
				if(x+2<8)
				{
					if(y-1>=0)
						Global.knightAttack[x+y*8]+=(1L<<((x+2)+(y-1)*8));
					if(y+1<8)
						Global.knightAttack[x+y*8]+=(1L<<((x+2)+(y+1)*8));
				}
			}
	}
}



class Global
{
	static long mask[]					 =new long[64];
	static long bitBoard[][]				 =new long[400][14];
	/*2-white kings;4-white queens;6-white rooks;8-white bishops
	  10-white knights;12-white pawns;0- all white pieces*/
	static long tempBoard[]				 =new long[14];
	static long kingAttack[]			 =new long[64];
	static long rookAttack_rank_ally [][]=new long[64][256];
	static long rookAttack_rank_enemy[][]=new long[64][256];
	static long rookAttack_file_ally [][]=new long[64][256];
	static long rookAttack_file_enemy[][]=new long[64][256];
	static long bishopAttack_A1H8[][]	 =new long[64][256];
	static long bishopAttack_H1A8[][]	 =new long[64][256];
	static long knightAttack[]			 =new long[64];
	/*squares attacked by a certain piece on a certain square*/
	static int  turnNumber=1;
	static boolean goodToGo=false;
	static boolean onGoing=true;
	static long nextMove;
	static int currentX,currentY,currentKind;
	static int promotion=4;
	static String record[]=new String[400];
}