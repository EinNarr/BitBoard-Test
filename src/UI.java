import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


class Frame extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int WIDTH =710;
	public static final int HEIGHT=500;
	JTextArea text = new JTextArea();
	JScrollPane textpanel=new JScrollPane(text);
	public Frame()
	{
		text.setColumns(10);
		text.setEditable(false);
		
		JButton newgame = new JButton("New Game");
		newgame.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				Global.turnNumber=1;
				Global.goodToGo=false;
				Global.onGoing=true;
				Global.record[1]="    1.";
				fresh();
				repaint();
			}
		});
		newgame.setBounds(470,37,200,30);
		add(newgame);

		JButton undo = new JButton("Undo");
		undo.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				if(Global.turnNumber==2)
				{
					Global.turnNumber-=1;
					Global.record[1]="    1.";
				}
				if(Global.turnNumber>2)
				{
					Global.turnNumber-=1;
					Global.record[Global.turnNumber]=Global.record[Global.turnNumber-1];
				}
				Global.goodToGo=false;
				Global.onGoing=true;
				fresh();
				repaint();
			}
		});
		undo.setBounds(470,67,100,30);
		add(undo);
		
		fresh();
		add(textpanel);
		textpanel.setBounds(470,100,200,338);
		
		
		setLayout(null);
		setTitle("Chess by EinNarr Ver.0.30 Alpha");
		setSize(WIDTH, HEIGHT);
		setLocationRelativeTo(null); 
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}
	
	public void fresh()
	{
		text.setText(Global.record[Global.turnNumber]);
	}
}


class ChessBoard extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int square=50;
	private Image board;
	private Image chessMan[]=new Image[14];
	private Image blue =new ImageIcon("images/blueCircle.png").getImage();
	private Image green=new ImageIcon("images/greenCircle.png").getImage();
	/*2-white kings;4-white queens;6-white rooks;8-white bishops
	  10-white knights;12-white pawns;0- all white pieces*/
	public ChessBoard(Frame owner)
	{
		int titlebarH;
		
		PromotionDialog pDialog=new PromotionDialog();
		setLayout(null);
		setSize(square*8,square*8);
		titlebarH=owner.getInsets().top;
		setLocation((owner.getHeight()-titlebarH-getHeight())/2,(owner.getHeight()-titlebarH-getHeight())/2);
		board       =new ImageIcon("images/ChessBoard.png").getImage();
		chessMan[ 2]=new ImageIcon("images/WhiteKing.png").getImage();
		chessMan[ 3]=new ImageIcon("images/BlackKing.png").getImage();
		chessMan[ 4]=new ImageIcon("images/WhiteQueen.png").getImage();
		chessMan[ 5]=new ImageIcon("images/BlackQueen.png").getImage();
		chessMan[ 6]=new ImageIcon("images/WhiteRook.png").getImage();
		chessMan[ 7]=new ImageIcon("images/BlackRook.png").getImage();
		chessMan[ 8]=new ImageIcon("images/WhiteBishop.png").getImage();
		chessMan[ 9]=new ImageIcon("images/BlackBishop.png").getImage();
		chessMan[10]=new ImageIcon("images/WhiteKnight.png").getImage();
		chessMan[11]=new ImageIcon("images/BlackKnight.png").getImage();
		chessMan[12]=new ImageIcon("images/WhitePawn.png").getImage();
		chessMan[13]=new ImageIcon("images/BlackPawn.png").getImage();
		
		MouseAdapter mouseAdapter = new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				int x,y,i;
				
				x=e.getX();
				y=e.getY();
				x=x/square;
				y=7-y/square;
				if(Global.onGoing)
				{
					for(i=2+(Global.turnNumber+1)%2;i<14;i+=2)
						if((Global.mask[x+y*8]&Global.bitBoard[Global.turnNumber][i])!=0)
						{
							Global.currentX=x;
							Global.currentY=y;
							Global.currentKind=i;
							Global.goodToGo=true;
							if(i==2||i==3)
								Global.nextMove=Range.king(Global.turnNumber,x+y*8);
							else if(i==4||i==5)
								Global.nextMove=Range.queen(Global.turnNumber,x+y*8);
							else if(i==6||i==7)
								Global.nextMove=Range.rook(Global.turnNumber,x+y*8);
							else if(i==8||i==9)
								Global.nextMove=Range.bishop(Global.turnNumber,x+y*8);
							else if(i==10||i==11)
								Global.nextMove=Range.knight(Global.turnNumber,x+y*8);
							else if(i==12||i==13)
								Global.nextMove=Range.pawn(Global.turnNumber,x+y*8);
							Global.nextMove-=Range.illegal(Global.turnNumber);
							repaint();
							return;
						}
					
					if(Global.goodToGo)
						if((Global.mask[x+y*8]&Global.nextMove)!=0)
						{
							if((Global.currentKind/2==1)
									&&(Global.currentX==4)
									&&((x==2)||(x==6)))
							{
								Move.castling(x+y*8);
								if(x==2)
									Global.record[Global.turnNumber]+="0-0-0";
								if(x==6)
									Global.record[Global.turnNumber]+="0-0";
							}
							else if((Global.currentKind/2==6)
									&&(Global.currentX!=x)
									&&((Global.bitBoard[Global.turnNumber][Global.turnNumber%2]&Global.mask[x+y*8])==0))
							{
								Move.enPassant(x+y*8);
								Global.record[Global.turnNumber]+=(char)('a'+Global.currentX)+String.valueOf(Global.currentY+1)+"x"+(char)('a'+x)+String.valueOf(y)+"e.p.";
							}
							else if((Global.currentKind/2==6)
									&&((y==7)||(y==0)))
							{	
								pDialog.setLocation(owner.getX()+Math.abs(owner.getWidth()-pDialog.getWidth())/2,owner.getY()+Math.abs(owner.getHeight()-pDialog.getHeight())/2);
								pDialog.setVisible(true);
								Move.promotion(x+y*8,4+Global.promotion*2+(Global.turnNumber+1)%2);
								Global.record[Global.turnNumber]+=(char)('a'+Global.currentX)+String.valueOf(Global.currentY+1);
								if((Global.bitBoard[Global.turnNumber][Global.turnNumber%2]&Global.mask[x+y*8])==0)
									Global.record[Global.turnNumber]+="-";
								else
									Global.record[Global.turnNumber]+="x";
								Global.record[Global.turnNumber]+=(char)('a'+x)+String.valueOf(y+1)+"=";
								if(Global.promotion==0)
									Global.record[Global.turnNumber]+="Q";
								else if(Global.promotion==1)
									Global.record[Global.turnNumber]+="R";
								else if(Global.promotion==2)
									Global.record[Global.turnNumber]+="B";
								else if(Global.promotion==3)
									Global.record[Global.turnNumber]+="N";
							}
							else
							{
								Move.normal(x+y*8);
								if(Global.currentKind/2==1)
									Global.record[Global.turnNumber]+="K";
								else if(Global.currentKind/2==2)
									Global.record[Global.turnNumber]+="Q";
								else if(Global.currentKind/2==3)
									Global.record[Global.turnNumber]+="R";
								else if(Global.currentKind/2==4)
									Global.record[Global.turnNumber]+="B";
								else if(Global.currentKind/2==5)
									Global.record[Global.turnNumber]+="N";
								else
									Global.record[Global.turnNumber]+="";
								Global.record[Global.turnNumber]+=(char)('a'+Global.currentX)+String.valueOf(Global.currentY+1);
								if((Global.bitBoard[Global.turnNumber][Global.turnNumber%2]&Global.mask[x+y*8])==0)
									Global.record[Global.turnNumber]+="-";
								else
									Global.record[Global.turnNumber]+="x";
								Global.record[Global.turnNumber]+=(char)('a'+x)+String.valueOf(y+1);
							}
							owner.fresh();
							Global.turnNumber++;
							for(i=0;i<14;i++)
								Global.bitBoard[Global.turnNumber][i]=Global.tempBoard[i];
							Global.goodToGo=false;
							
							repaint();
			
							if(Situation.notMovable(Global.turnNumber))
								if(Situation.check(Global.turnNumber))
								{
									Global.onGoing=false;
									if(Global.turnNumber%2==0)
									{
										Global.record[Global.turnNumber-1]+="\t1-0";
										JOptionPane.showMessageDialog(owner,"White wins!","Checkmate!",JOptionPane.INFORMATION_MESSAGE);
									}
									else
									{
										Global.record[Global.turnNumber-1]+="\n       0-1";
										JOptionPane.showMessageDialog(owner,"Black wins!","Checkmate!",JOptionPane.INFORMATION_MESSAGE);
									}
								}
								else
								{
									Global.onGoing=false;
									if(Global.turnNumber%2==0)
										Global.record[Global.turnNumber-1]+="\t½–½";
									else
										Global.record[Global.turnNumber-1]+="\n       ½–½";
									JOptionPane.showMessageDialog(owner,"Draw!","Stalemate!",JOptionPane.INFORMATION_MESSAGE);
								}
							else if(Situation.insufficientMaterial(Global.turnNumber))
							{
								Global.onGoing=false;
								if(Global.turnNumber%2==0)
									Global.record[Global.turnNumber-1]+="\t½–½";
								else
									Global.record[Global.turnNumber-1]+="\n       ½–½";
								JOptionPane.showMessageDialog(owner,"Draw!","Insufficient Material!",JOptionPane.INFORMATION_MESSAGE);
							}else if(Situation.fiftyMoves(Global.turnNumber))
							{
								Global.onGoing=false;
								if(Global.turnNumber%2==0)
									Global.record[Global.turnNumber-1]+="\t½–½";
								else
									Global.record[Global.turnNumber-1]+="\n       ½–½";
								JOptionPane.showMessageDialog(owner,"Draw!","Fifty-Move Rule!",JOptionPane.INFORMATION_MESSAGE);
							}else if(Situation.threefoldRepetition(Global.turnNumber))
							{
								Global.onGoing=false;
								if(Global.turnNumber%2==0)
									Global.record[Global.turnNumber-1]+="\t½–½";
								else
									Global.record[Global.turnNumber-1]+="\n       ½–½";
								JOptionPane.showMessageDialog(owner,"Draw!","Threefold Repetition!",JOptionPane.INFORMATION_MESSAGE);
							}

							owner.fresh();
							if(Global.onGoing)
							{
								if(Situation.check(Global.turnNumber))
									Global.record[Global.turnNumber-1]+="+";
								if(Global.turnNumber%2==0)
									Global.record[Global.turnNumber-1]+="\t";
								else
								{
									Global.record[Global.turnNumber-1]+="\n";
									if(Global.turnNumber/2+1>=100)
										;
									else if(Global.turnNumber/2+1>=10)
										Global.record[Global.turnNumber-1]+="  ";
									else
										Global.record[Global.turnNumber-1]+="    ";
									Global.record[Global.turnNumber-1]+=String.valueOf(Global.turnNumber/2+1)+".";
								}

							}
							Global.record[Global.turnNumber]=Global.record[Global.turnNumber-1];
							owner.fresh();
							
						}
						else
						{
							Global.goodToGo=false;
							repaint();
						}
					
				}
			}
		};
		addMouseListener(mouseAdapter);
	}
	
	public void paintComponent(Graphics g)
	{
		
		int i,j,x,y;
		g.drawImage(board,0,0,square*8,square*8,null);
		if(Global.goodToGo)
		{
			x=(Global.currentX)*square;
			y=(7-Global.currentY)*square;
			g.drawImage(blue,x,y,square,square,null);
			for(i=2;i<14;i++)
				for(j=0;j<64;j++)
					if((Global.mask[j]&Global.nextMove)!=0)
					{
						x=j%8*square;
						y=(7-j/8)*square;
						g.drawImage(green,x,y,square,square,null);
					}
		}
		for(i=2;i<14;i++)
			for(j=0;j<64;j++)
				if((Global.mask[j]&Global.bitBoard[Global.turnNumber][i])!=0)
				{
					x=j%8*square;
					y=(7-j/8)*square;
					g.drawImage(chessMan[i],x,y,square,square,null);
				}
		
	}
}

class PromotionDialog extends JDialog
{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int WIDTH =320;
	public static final int HEIGHT=80;
	public int selected=4;

	public PromotionDialog()
	{
		setUndecorated(true);
		setModal(true);
		setSize(WIDTH, HEIGHT);
		setLocationRelativeTo(null);
		add(new PromotionPanel(this));
		
	}
		
}

class PromotionPanel extends JPanel
{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int WIDTH =320;
	public static final int HEIGHT=80;
	private Image chessMan[]=new Image[12];
	private Image green=new ImageIcon("/ProjectChess BitBoard Ver/images/greenSquare.png").getImage();
	public PromotionPanel(PromotionDialog owner)
	{
		setSize(WIDTH, HEIGHT);
		
		chessMan[ 4]=new ImageIcon("/ProjectChess BitBoard Ver/images/BlackBishop.png").getImage();
		chessMan[ 5]=new ImageIcon("/ProjectChess BitBoard Ver/images/BlackQueen.png").getImage();
		chessMan[ 6]=new ImageIcon("/ProjectChess BitBoard Ver/images/WhiteRook.png").getImage();
		chessMan[ 7]=new ImageIcon("/ProjectChess BitBoard Ver/images/BlackRook.png").getImage();
		chessMan[ 8]=new ImageIcon("/ProjectChess BitBoard Ver/images/WhiteBishop.png").getImage();
		chessMan[ 9]=new ImageIcon("/ProjectChess BitBoard Ver/images/BlackBishop.png").getImage();
		chessMan[10]=new ImageIcon("/ProjectChess BitBoard Ver/images/WhiteKnight.png").getImage();
		chessMan[11]=new ImageIcon("/ProjectChess BitBoard Ver/images/BlackKnight.png").getImage();
		
		MouseAdapter mouseAdapter = new MouseAdapter()
		{
			public void mouseMoved(MouseEvent e)
			{
				int x,y;
				x=e.getX();
				y=e.getY();
				
				if(y>0&&y<HEIGHT)
				{
					if(x>HEIGHT*0&&x<HEIGHT*1)
						Global.promotion=0;
					else if(x>HEIGHT*1&&x<HEIGHT*2)
						Global.promotion=1;
					else if(x>HEIGHT*2&&x<HEIGHT*3)
						Global.promotion=2;
					else if(x>HEIGHT*3&&x<HEIGHT*4)
						Global.promotion=3;
					else
						Global.promotion=4;
				}
				repaint(0,0,WIDTH,HEIGHT);
			}

			public void mousePressed(MouseEvent e)
			{
				owner.setVisible(false);
			}
		};
		addMouseListener(mouseAdapter);
		addMouseMotionListener(mouseAdapter);
	}
	public void paint(Graphics g)
	{
		g.setColor(new Color(255,255,255));
		g.fillRect(0,0,getWidth(),getHeight());
		g.drawImage(green,Global.promotion*HEIGHT,0,HEIGHT,HEIGHT,null);
		g.drawImage(chessMan[ 4+(Global.turnNumber+1)%2],HEIGHT*0,0,HEIGHT,HEIGHT,null);
		g.drawImage(chessMan[ 6+(Global.turnNumber+1)%2],HEIGHT*1,0,HEIGHT,HEIGHT,null);
		g.drawImage(chessMan[ 8+(Global.turnNumber+1)%2],HEIGHT*2,0,HEIGHT,HEIGHT,null);
		g.drawImage(chessMan[10+(Global.turnNumber+1)%2],HEIGHT*3,0,HEIGHT,HEIGHT,null);
	}
}