class Range
{
	static long king(int turn,int position)
	{
		int i;
		boolean flagA=true,flagH=true;
		long tempBitBoardAlly,result;
		tempBitBoardAlly =Global.kingAttack[position]&Global.bitBoard[Global.turnNumber][(turn+1)%2];
		result=Global.kingAttack[position]-tempBitBoardAlly;
		
		for(i=1;i<=turn&&(flagA||flagH);i++)
		{
			if(Global.bitBoard[i][2+(turn+1)%2]!=Global.bitBoard[1][2+(turn+1)%2])
			{
				flagA=false;
				flagH=false;
			}
			else
			{
				if(flagA)
					if((Global.bitBoard[i][6+(turn+1)%2]&Global.mask[56-turn%2*56])==0)
						flagA=false;
				if(flagH)
					if((Global.bitBoard[i][6+(turn+1)%2]&Global.mask[63-turn%2*56])==0)
						flagH=false;
			}
		}
		
		if(flagA)
			if((((Global.bitBoard[Global.turnNumber][0]|Global.bitBoard[Global.turnNumber][1])
					&(Global.mask[57-turn%2*56]
							+Global.mask[58-turn%2*56]
									+Global.mask[59-turn%2*56]))==0)
					&&!Situation.attacked(turn,(Global.mask[58-turn%2*56]+Global.mask[59-turn%2*56]+Global.mask[60-turn%2*56])))
				result+=Global.mask[58-turn%2*56];
		if(flagH)
			if((((Global.bitBoard[Global.turnNumber][0]|Global.bitBoard[Global.turnNumber][1])
					&(Global.mask[61-turn%2*56]
							+Global.mask[62-turn%2*56]))==0)
					&&!Situation.attacked(turn,(Global.mask[60-turn%2*56]+Global.mask[61-turn%2*56]+Global.mask[62-turn%2*56])))
				result+=Global.mask[62-turn%2*56];
		
		return result;
	}
	
	static long queen(int turn,int position)
	{
		long result;
		result=rook(turn,position)+bishop(turn,position);
		return result;
	}//done
	
	static long rook(int turn,int position)
	{
		long tempBitBoardRank,tempBitBoardFile,result,temp;
		int rank,file,i;
		rank=(int)(0xFF&(Global.bitBoard[Global.turnNumber][(turn+1)%2]>>(position/8*8)));
		tempBitBoardRank =Global.rookAttack_rank_ally [position][rank];
		rank=(int)(0xFF&(Global.bitBoard[Global.turnNumber][(turn  )%2]>>(position/8*8)));
		tempBitBoardRank&=Global.rookAttack_rank_enemy[position][rank];
		temp=(Global.bitBoard[Global.turnNumber][(turn+1)%2]>>(position%8))&(0x101010101010101L);
		file=0;
		for(i=0;i<8;i++)
			file+=(((temp>>(8*i))&1L)<<i);
		tempBitBoardFile =Global.rookAttack_file_ally [position][file];
		temp=(Global.bitBoard[Global.turnNumber][(turn  )%2]>>(position%8))&(0x101010101010101L);
		file=0;
		for(i=0;i<8;i++)
			file+=(((temp>>8*i)&1L)<<i);
		tempBitBoardFile&=Global.rookAttack_file_enemy[position][file];
		result=tempBitBoardFile|tempBitBoardRank;
		return result;
	}/*divide the situation of rook into rank and file. 
	and then divide each of them into enemy and ally.then combine all 4 situations.*/
	
	static long bishop(int turn,int position)
	{
		long tempBitBoardAlly=0,tempBitBoardEnemy=0,result;
		int i;
		for(i=position+7;(i%8<7)&&(i<64);i+=7)
			if((Global.bitBoard[Global.turnNumber][(turn  )%2]&Global.mask[i])==0)
				tempBitBoardEnemy+=Global.mask[i];
			else
			{
				tempBitBoardEnemy+=Global.mask[i];
				break;
			}
		for(i=position+7;(i%8<7)&&(i<64);i+=7)
			if((Global.bitBoard[Global.turnNumber][(turn+1)%2]&Global.mask[i])==0)
				tempBitBoardAlly +=Global.mask[i];
			else
				break;
		
		for(i=position+9;(i%8>0)&&(i<64);i+=9)
			if((Global.bitBoard[Global.turnNumber][(turn  )%2]&Global.mask[i])==0)
				tempBitBoardEnemy+=Global.mask[i];
			else
			{
				tempBitBoardEnemy+=Global.mask[i];
				break;
			}
		for(i=position+9;(i%8>0)&&(i<64);i+=9)
			if((Global.bitBoard[Global.turnNumber][(turn+1)%2]&Global.mask[i])==0)
				tempBitBoardAlly +=Global.mask[i];
			else
				break;
		
		for(i=position-9;(i%8<7)&&(i>=0);i-=9)
			if((Global.bitBoard[Global.turnNumber][(turn  )%2]&Global.mask[i])==0)
				tempBitBoardEnemy+=Global.mask[i];
			else
			{
				tempBitBoardEnemy+=Global.mask[i];
				break;
			}
		for(i=position-9;(i%8<7)&&(i>=0);i-=9)
			if((Global.bitBoard[Global.turnNumber][(turn+1)%2]&Global.mask[i])==0)
				tempBitBoardAlly +=Global.mask[i];
			else
				break;

		for(i=position-7;(i%8>0)&&(i>=0);i-=7)
			if((Global.bitBoard[Global.turnNumber][(turn  )%2]&Global.mask[i])==0)
				tempBitBoardEnemy+=Global.mask[i];
			else
			{
				tempBitBoardEnemy+=Global.mask[i];
				break;
			}
		for(i=position-7;(i%8>0)&&(i>=0);i-=7)
			if((Global.bitBoard[Global.turnNumber][(turn+1)%2]&Global.mask[i])==0)
				tempBitBoardAlly +=Global.mask[i];
			else
				break;

		result=tempBitBoardAlly&tempBitBoardEnemy;
		return result;
	}
	
	static long knight(int turn,int position)
	{
		long tempBitBoardAlly,result;
		tempBitBoardAlly =Global.knightAttack[position]&Global.bitBoard[Global.turnNumber][(turn+1)%2];
		result=Global.knightAttack[position]-tempBitBoardAlly;
		return result;
	}
	
	static long pawn_move(int turn,int position)
	{
		long result=0;
		if((Global.mask[position-8+turn%2*16]&Global.bitBoard[Global.turnNumber][0])==0)
			if((Global.mask[position-8+turn%2*16]&Global.bitBoard[Global.turnNumber][1])==0)
			{
				result+=Global.mask[position-8+turn%2*16];
				if(position/8==6-turn%2*5)
					if((Global.mask[position-16+turn%2*32]&Global.bitBoard[Global.turnNumber][0])==0)
						if((Global.mask[position-16+turn%2*32]&Global.bitBoard[Global.turnNumber][1])==0)
					result+=Global.mask[position-16+turn%2*32];
			}
		return result;
	}
	
	static long pawn_capture(int turn,int position)
	{
		long result=0;
		if((position%8>0||turn%2==0)&&(position%8<7||turn%2==1)&&position-7+turn%2*14>=0&&position-7+turn%2*14<64)
			if((Global.mask[position-7+turn%2*14]&Global.bitBoard[Global.turnNumber][turn%2])!=0)
				result+=Global.mask[position-7+turn%2*14];
		if((position%8>0||turn%2==1)&&(position%8<7||turn%2==0)&&position-9+turn%2*18>=0&&position-9+turn%2*18<64)
			if((Global.mask[position-9+turn%2*18]&Global.bitBoard[Global.turnNumber][turn%2])!=0)
				result+=Global.mask[position-9+turn%2*18];
		
		//enPassant
		if(position/8==3+turn%2)
		{
			if((Global.mask[position-1]&Global.bitBoard[Global.turnNumber][12+turn%2])!=0)
				if((Global.mask[position-1-16+turn%2*32]&Global.bitBoard[Global.turnNumber][12+turn%2])==0)
					if((Global.mask[position-1-16+turn%2*32]&Global.bitBoard[turn-1][12+turn%2])!=0)
						result+=Global.mask[position-9+turn%2*16];
			if((Global.mask[position+1]&Global.bitBoard[Global.turnNumber][12+turn%2])!=0)
				if((Global.mask[position+1-16+turn%2*32]&Global.bitBoard[Global.turnNumber][12+turn%2])==0)
					if((Global.mask[position+1-16+turn%2*32]&Global.bitBoard[turn-1][12+turn%2])!=0)
						result+=Global.mask[position-7+turn%2*16];
		}
		//enPassant end
		return result;
	}
	
	static long pawn(int turn,int position)
	{
		return pawn_move(turn,position)|pawn_capture(turn,position);
	}
	
	static long illegal(int turn)
	{
		long result=0;
		long[] temp=new long[14];
		int i,j;
		for(i=0;i<64;i++)
			if((Global.nextMove&Global.mask[i])!=0)
			{
				if((Global.currentKind/2==6)
						&&(Global.currentX!=i%8)
						&&((Global.bitBoard[Global.turnNumber][Global.turnNumber%2]&Global.mask[i])==0))
					Move.enPassant(i);
				else
					Move.normal(i);
				for(j=0;j<14;j++)
				{
					temp[j]=Global.bitBoard[Global.turnNumber][j];
					Global.bitBoard[Global.turnNumber][j]=Global.tempBoard[j];
				}
				if(Situation.check(turn))
					result+=Global.mask[i];
				for(j=0;j<14;j++)
					Global.bitBoard[Global.turnNumber][j]=temp[j];
			}
		return result;
	}
}

class Move
{
	static void normal(int destination)
	{
		int origin;
		int i;
		origin		=Global.currentX+Global.currentY*8;
		for(i=0;i<14;i++)
			Global.tempBoard[i]=Global.bitBoard[Global.turnNumber][i];
		for(i=2;i<14;i++)
			if((Global.tempBoard[i]&Global.mask[destination])!=0)
			{
				Global.tempBoard[i  ]-=Global.mask[destination];
				Global.tempBoard[i%2]-=Global.mask[destination];
				break;
			}
			Global.tempBoard[Global.currentKind]
					-=Global.mask[origin];
			Global.tempBoard[Global.currentKind]
					+=Global.mask[destination];
			Global.tempBoard[Global.currentKind%2]
					-=Global.mask[origin];
			Global.tempBoard[Global.currentKind%2]
					+=Global.mask[destination];
	}

	static void castling(int destination)
	{
		int i;
		for(i=0;i<14;i++)
			Global.tempBoard[i]=Global.bitBoard[Global.turnNumber][i];
		Global.tempBoard[0+(Global.turnNumber+1)%2]-=Global.tempBoard[2+(Global.turnNumber+1)%2];
		Global.tempBoard[2+(Global.turnNumber+1)%2] =Global.mask[destination];
		Global.tempBoard[0+(Global.turnNumber+1)%2]+=Global.tempBoard[2+(Global.turnNumber+1)%2];
		if(destination%8==2)
		{
			Global.tempBoard[6+(Global.turnNumber+1)%2]
					-=Global.mask[56-Global.turnNumber%2*56];
			Global.tempBoard[6+(Global.turnNumber+1)%2]
					+=Global.mask[59-Global.turnNumber%2*56];
			Global.tempBoard[0+(Global.turnNumber+1)%2]
					-=Global.mask[56-Global.turnNumber%2*56];
			Global.tempBoard[0+(Global.turnNumber+1)%2]
					+=Global.mask[59-Global.turnNumber%2*56];
		}
		if(destination%8==6)
		{
			Global.tempBoard[6+(Global.turnNumber+1)%2]
					-=Global.mask[63-Global.turnNumber%2*56];
			Global.tempBoard[6+(Global.turnNumber+1)%2]
					+=Global.mask[61-Global.turnNumber%2*56];
			Global.tempBoard[0+(Global.turnNumber+1)%2]
					-=Global.mask[63-Global.turnNumber%2*56];
			Global.tempBoard[0+(Global.turnNumber+1)%2]
					+=Global.mask[61-Global.turnNumber%2*56];
		}
	}

	static void enPassant(int destination)
	{
		int origin;
		int i;
		origin		=Global.currentX+Global.currentY*8;
		for(i=0;i<14;i++)
			Global.tempBoard[i]=Global.bitBoard[Global.turnNumber][i];
		Global.tempBoard[12+(Global.turnNumber+1)%2]
				-=Global.mask[origin];
		Global.tempBoard[12+(Global.turnNumber+1)%2]
				+=Global.mask[destination];
		Global.tempBoard[0 +(Global.turnNumber+1)%2]
				-=Global.mask[origin];
		Global.tempBoard[0 +(Global.turnNumber+1)%2]
				+=Global.mask[destination];
		Global.tempBoard[12+(Global.turnNumber  )%2]
				-=Global.mask[destination+8-Global.turnNumber%2*16];
		Global.tempBoard[0 +(Global.turnNumber  )%2]
				-=Global.mask[destination+8-Global.turnNumber%2*16];
	}

	static void promotion(int destination,int piece)//piece uses the same token as Global.bitBoard
	{
		int origin;
		int i;
		origin		=Global.currentX+Global.currentY*8;
		for(i=0;i<14;i++)
			Global.tempBoard[i]=Global.bitBoard[Global.turnNumber][i];
		for(i=2;i<14;i++)
			if((Global.tempBoard[i]&Global.mask[destination])!=0)
			{
				Global.tempBoard[i  ]-=Global.mask[destination];
				Global.tempBoard[i%2]-=Global.mask[destination];
				break;
			}
		Global.tempBoard[12+(Global.turnNumber+1)%2]
				-=Global.mask[origin];
		Global.tempBoard[0 +(Global.turnNumber+1)%2]
				-=Global.mask[origin];
		Global.tempBoard[0 +(Global.turnNumber+1)%2]
				+=Global.mask[destination];
		Global.tempBoard[piece]
				+=Global.mask[destination];
	}
}

class Situation
{
	static boolean attacked(int turn,long position)//bitBoard
	{
		int i,j;
		long temp;
		for(i=2+turn%2;i<14;i+=2)
			for(j=0;j<64;j++)
				if((Global.bitBoard[Global.turnNumber][i]&Global.mask[j])!=0)
				{
					if(i==2||i==3)
						temp=Range.king(turn+1,j);
					else if(i==4||i==5)
						temp=Range.queen(turn+1,j);
					else if(i==6||i==7)
						temp=Range.rook(turn+1,j);
					else if(i==8||i==9)
						temp=Range.bishop(turn+1,j);
					else if(i==10||i==11)
						temp=Range.knight(turn+1,j);
					else
						temp=Range.pawn_capture(turn+1,j);
					if((temp&position)!=0)
						return true;
				}
		return false;
	}
	
	static boolean check(int turn)
	{
		return attacked(turn,Global.bitBoard[Global.turnNumber][2+(turn+1)%2]);
	}
	
	static boolean notMovable(int turn)
	{
		int i,j=0;
		long temp=0;
		for(i=2+(turn+1)%2;i<14;i+=2)
		{
			for(j=0;j<64;j++)
				if((Global.bitBoard[Global.turnNumber][i]&Global.mask[j])!=0)
				{
					Global.currentKind=i;
					Global.currentX=j%8;
					Global.currentY=j/8;
					if(i==2||i==3)
					{
						Global.nextMove=Range.king(turn,j);
						temp=Range.king(turn,j)-Range.illegal(turn);
					}
					else if(i==4||i==5)
					{
						Global.nextMove=Range.queen(turn,j);
						temp=Range.queen(turn,j)-Range.illegal(turn);
					}
					else if(i==6||i==7)
					{
						Global.nextMove=Range.rook(turn,j);
						temp=Range.rook(turn,j)-Range.illegal(turn);
					}
					else if(i==8||i==9)
					{
						Global.nextMove=Range.bishop(turn,j);
						temp=Range.bishop(turn,j)-Range.illegal(turn);
					}
					else if(i==10||i==11)
					{
						Global.nextMove=Range.knight(turn,j);
						temp=Range.knight(turn,j)-Range.illegal(turn);
					}
					else
					{
						Global.nextMove=Range.pawn(turn,j);
						temp=Range.pawn(turn,j)-Range.illegal(turn);
					}
					if(temp!=0)
						break;
				}
			if(j<64)
				break;
		}
		if(i>=14)
			return true;
		return false;
	}
	
	static boolean insufficientMaterial(int turn)
	{
		int j,wN=0,bN=0,wB=0,bB=0;
		
		if((Global.bitBoard[Global.turnNumber][4]|Global.bitBoard[Global.turnNumber][5]
				|Global.bitBoard[Global.turnNumber][6]|Global.bitBoard[Global.turnNumber][7]
				|Global.bitBoard[Global.turnNumber][12]|Global.bitBoard[Global.turnNumber][13])==0)
		{
			for(j=0;j<64;j++)
			{
				if((Global.bitBoard[Global.turnNumber][10]&Global.mask[j])!=0)
					wN++;
				if((Global.bitBoard[Global.turnNumber][10]&Global.mask[j])!=0)
					bN++;
			}										//count number of knights
			if((Global.bitBoard[Global.turnNumber][ 8]&0xAAAAAAAAAAAAAAAAL)!=0)
				wB++;
			if((Global.bitBoard[Global.turnNumber][ 8]&0x5555555555555555L)!=0)
				wB++;
			if((Global.bitBoard[Global.turnNumber][ 9]&0xAAAAAAAAAAAAAAAAL)!=0)
				bB++;
			if((Global.bitBoard[Global.turnNumber][ 9]&0x5555555555555555L)!=0)
				bB++;			//count number of bishops on the different colors
			if(wN==0&&bN==0&&wB==0&&bB==0)
				return true;
			if(wN==0&&bN==0&&wB==1&&bB==0)
				return true;
			if(wN==0&&bN==0&&wB==0&&bB==1)
				return true;
			if(wN==0&&bN==0&&wB==1&&bB==1)
				return true;
			if(wN==1&&bN==0&&wB==0&&bB==0)
				return true;
			if(wN==0&&bN==1&&wB==0&&bB==0)
				return true;
			if(wN==1&&bN==1&&wB==0&&bB==0)
				return true;
		}
		return false;
	}
		
	static boolean fiftyMoves(int turn)
	{
		int i,j,n,n0;
		
		if(Global.turnNumber>=101)
		{
			n0=0;
			
			for(j=0;j<64;j++)
			{
				if((Global.mask[j]&Global.bitBoard[Global.turnNumber][0])!=0)
					n0++;
				if((Global.mask[j]&Global.bitBoard[Global.turnNumber][1])!=0)
					n0++;
			}
			
			for(i=Global.turnNumber-1;i>Global.turnNumber-100;i--)
			{
				n=0;
				for(j=0;j<64;j++)
				{
					if((Global.mask[j]&Global.bitBoard[i][0])!=0)
						n++;
					if((Global.mask[j]&Global.bitBoard[i][1])!=0)
						n++;
				}
				if(n!=n0)
					break;
				if(Global.bitBoard[i][12]!=Global.bitBoard[Global.turnNumber][12])
					break;
				if(Global.bitBoard[i][13]!=Global.bitBoard[Global.turnNumber][13])
					break;
			}
			if(i==Global.turnNumber-100)
				return true;
		}
		return false;
	}
		
	static boolean threefoldRepetition(int turn)
	{
		int i,j,n;
	
		n=0;
		for(i=1;i<=Global.turnNumber;i++)
		{
			for(j=2;j<14;j++)
				if(Global.bitBoard[i][j]!=Global.bitBoard[Global.turnNumber][j])
					break;
			if(j==14)
				n++;
		}
		if(n>2)
			return true;
		return false;
	}
}