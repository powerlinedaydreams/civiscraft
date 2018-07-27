package net.civiscraft.world.map.tile;

import java.util.HashSet;

import io.netty.buffer.ByteBuf;
import net.civiscraft.lib.util.data.ICacheable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public class TilePos implements Comparable<TilePos>, ICacheable<TilePos>
{
	public final int x;
	public final int z;
	
	public TilePos(int x, int z)
	{
		this.x = x;
		this.z = z;
	}
	
	public TilePos(ChunkPos pos)
	{
		int tileZ = Math.floorDiv(pos.z, 4);
		int tileX = Math.floorDiv(pos.x + 2 * (tileZ % 2), 4);
		
		this.x = tileX;
		this.z = tileZ;
	}
	
	public TilePos(BlockPos blockPos)
	{
		this(new ChunkPos(blockPos));
	}
	
	@Override
	public String toString()
	{
		return "Tile(" + x + ":" + z + ")";
	}
	
	@Override
	public int compareTo(TilePos comparing)
	{
		int sum1 = x + z;
		int sum2 = comparing.x + comparing.z;
		
		if(sum1<sum2) {return -1;}
		
		else if(sum1>sum2) {return 1;}
		
		else
		{
			if(x < comparing.x) {return -1;}
			
			else if (x > comparing.x) {return 1;}
			
			else {return 0;}
		}
	}
	
	public TilePos[] getTilesInRange(int range)
	{
		if(range < 0)
		{
			throw new IllegalArgumentException("Range cannot be negative.");
		}
		
		HashSet<TilePos> posArray = new HashSet<>(20);
		
		for(int i=0; i<=range; i++)
		{
			for(int j=0; j <= range; j++)
			{
				int ppX = x + i;
				int ppZ = z + j;
				int pnX = x + i;
				int pnZ = z - j;
				int npX = x - i;
				int npZ = z + j;
				int nnX = x - i;
				int nnZ = z - j;
				
				TilePos ppT = new TilePos(ppX, ppZ);
				TilePos pnT = new TilePos(pnX, pnZ);
				TilePos npT = new TilePos(npX, npZ);
				TilePos nnT = new TilePos(nnX, nnZ);
				
				posArray.add(ppT);
				posArray.add(pnT);
				posArray.add(npT);
				posArray.add(nnT);
			}
		}
		
		TilePos[] output = new TilePos[posArray.size()];
		int i = 0;
		
		for(TilePos pos : posArray)
		{
			output[i++] = pos;
		}
		
		return output;
	}
	
	public boolean equals(TilePos comparing)
	{
		return x == comparing.x && z == comparing.z;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(!(o instanceof TilePos)) {return false;}
		
		TilePos pos = (TilePos) o;
		
		return x == pos.x && z == pos.z;
	}
	
	public ChunkPos getIndexChunkPos()
	{
		int chunkZ = z * 4;
		int chunkX = x * 4 - 2 * (z % 2);
		return new ChunkPos(chunkX, chunkZ);
	}
	
	public static TilePos fromLong(long l)
	{
		return new TilePos((int)(l >> 32), (int)l);
	}
	
	public long toLong()
	{
		return (long)x << 32 | z & 0xFFFFFFFFL;
	}

	public ChunkPos[][] generateChunks()
	{
		ChunkPos[][] chunkArray = new ChunkPos[4][4];
		
		for(int i=0; i<4; i++)
		{
			for(int j=0; j<2; j++)
			{
				int cz = z * 4 + j;
				int cx = x * 4 - 2 * (z % 2);
				
				chunkArray[i][j] = new ChunkPos(cx, cz);
			}
		}
		
		return chunkArray;
	}
	
	@Override
	public int hashCode()
	{
		return ((Long) toLong()).hashCode();
	}

	@Override
	public void writeBuffer(ByteBuf buffer)
	{
		buffer.writeLong(toLong());
	}
	
	public static TilePos readBuffer(ByteBuf buffer)
	{
		return fromLong(buffer.readLong());
	}
}
