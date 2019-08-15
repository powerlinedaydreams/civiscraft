package net.civiscraft.world.map.tile;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Nullable;

import io.netty.buffer.ByteBuf;
import net.civiscraft.lib.log.CCLog;
import net.civiscraft.lib.net.PacketBufferCC;
import net.civiscraft.lib.util.NBTUtil.NBTType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class TileBorder
{
	public final TilePos pos;
	public boolean complete = false, generating = false;
	public ArrayList<TreeMap<BlockPos, Integer>> northBorder, eastBorder, southBorder, westBorder;
	public World world;
	public BorderMapThread thread;
	private Tile tile;

	public TileBorder(TilePos pos, World world, Tile tile)
	{
		this.tile = tile;
		this.pos = pos;
		this.world = world;
		tile.force();
		thread = new BorderMapThread();
	}

	private TileBorder(TilePos pos, boolean complete, ArrayList<TreeMap<BlockPos, Integer>> northBorder,
			ArrayList<TreeMap<BlockPos, Integer>> eastBorder, ArrayList<TreeMap<BlockPos, Integer>> southBorder,
			ArrayList<TreeMap<BlockPos, Integer>> westBorder, @Nullable World world)
	{
		this.pos = pos;
		this.complete = complete;
		this.northBorder = northBorder;
		this.eastBorder = eastBorder;
		this.southBorder = southBorder;
		this.westBorder = westBorder;
		this.world = world;
	}

	public void addTile(Tile tile)
	{
		this.tile = tile;

		if(!complete)
		{
			tile.force();
			thread = new BorderMapThread();
			generate();
		}
	}

	public void generate()
	{
		generating = true;
		thread.setName(pos.toString());
		thread.run();
	}

	public void updateBorder(BlockPos block)
	{

	}

	public void borderCompleted()
	{
		complete = true;
		generating = false;
		thread = null;
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		nbt.setLong("pos", pos.toLong());
		nbt.setBoolean("complete", complete);
		if(complete)
		{

			NBTTagList northBorderNBT = new NBTTagList();
			for (int i = 0; i < 256; i++)
			{
				NBTTagList thisMapNBT = new NBTTagList();
				TreeMap<BlockPos, Integer> thisMap = northBorder.get(i);

				for (Map.Entry<BlockPos, Integer> entry : thisMap.entrySet())
				{
					NBTTagCompound tag = new NBTTagCompound();
					tag.setLong("block", entry.getKey().toLong());
					tag.setInteger("length", entry.getValue());

					thisMapNBT.appendTag(tag);
				}
				northBorderNBT.appendTag(thisMapNBT);
			}

			nbt.setTag("northBorder", northBorderNBT);

			NBTTagList eastBorderNBT = new NBTTagList();
			for (int i = 0; i < 256; i++)
			{
				NBTTagList thisMapNBT = new NBTTagList();
				TreeMap<BlockPos, Integer> thisMap = eastBorder.get(i);

				for (Map.Entry<BlockPos, Integer> entry : thisMap.entrySet())
				{
					NBTTagCompound tag = new NBTTagCompound();
					tag.setLong("block", entry.getKey().toLong());
					tag.setInteger("length", entry.getValue());

					thisMapNBT.appendTag(tag);
				}
				eastBorderNBT.appendTag(thisMapNBT);
			}

			nbt.setTag("eastBorder", eastBorderNBT);

			NBTTagList southBorderNBT = new NBTTagList();
			for (int i = 0; i < 256; i++)
			{
				NBTTagList thisMapNBT = new NBTTagList();
				TreeMap<BlockPos, Integer> thisMap = southBorder.get(i);

				for (Map.Entry<BlockPos, Integer> entry : thisMap.entrySet())
				{
					NBTTagCompound tag = new NBTTagCompound();
					tag.setLong("block", entry.getKey().toLong());
					tag.setInteger("length", entry.getValue());

					thisMapNBT.appendTag(tag);
				}
				southBorderNBT.appendTag(thisMapNBT);
			}

			nbt.setTag("southBorder", southBorderNBT);

			NBTTagList westBorderNBT = new NBTTagList();
			for (int i = 0; i < 256; i++)
			{
				NBTTagList thisMapNBT = new NBTTagList();
				TreeMap<BlockPos, Integer> thisMap = westBorder.get(i);

				for (Map.Entry<BlockPos, Integer> entry : thisMap.entrySet())
				{
					NBTTagCompound tag = new NBTTagCompound();
					tag.setLong("block", entry.getKey().toLong());
					tag.setInteger("length", entry.getValue());

					thisMapNBT.appendTag(tag);
				}
				westBorderNBT.appendTag(thisMapNBT);
			}

			nbt.setTag("westBorder", westBorderNBT);
		}

		return nbt;
	}

	public static TileBorder readFromNBT(NBTTagCompound nbt, World world)
	{
		TilePos _pos = TilePos.fromLong(nbt.getLong("pos"));
		boolean _complete = nbt.getBoolean("complete");

		if(_complete)
		{
			ArrayList<TreeMap<BlockPos, Integer>> _northBorder = new ArrayList<TreeMap<BlockPos, Integer>>(256);
			int size;
			NBTTagList northBorderNBT = nbt.getTagList("northBorder", NBTType.NBTTagList.i);

			for (int i = 0; i < 256; i++)
			{
				TreeMap<BlockPos, Integer> thisMap = new TreeMap<BlockPos, Integer>();
				NBTTagList thisMapNBT = (NBTTagList) northBorderNBT.get(i);
				size = thisMapNBT.tagCount();

				for (int j = 0; j < size; j++)
				{
					NBTTagCompound tag = thisMapNBT.getCompoundTagAt(j);
					BlockPos block = BlockPos.fromLong(tag.getLong("block"));
					int length = tag.getInteger("length");

					thisMap.put(block, length);
				}

				_northBorder.add(thisMap);
			}

			ArrayList<TreeMap<BlockPos, Integer>> _eastBorder = new ArrayList<TreeMap<BlockPos, Integer>>(256);
			NBTTagList eastBorderNBT = nbt.getTagList("eastBorder", NBTType.NBTTagList.i);

			for (int i = 0; i < 256; i++)
			{
				TreeMap<BlockPos, Integer> thisMap = new TreeMap<BlockPos, Integer>();
				NBTTagList thisMapNBT = (NBTTagList) eastBorderNBT.get(i);
				size = thisMapNBT.tagCount();

				for (int j = 0; j < size; j++)
				{
					NBTTagCompound tag = thisMapNBT.getCompoundTagAt(j);
					BlockPos block = BlockPos.fromLong(tag.getLong("block"));
					int length = tag.getInteger("length");

					thisMap.put(block, length);
				}

				_eastBorder.add(thisMap);
			}

			ArrayList<TreeMap<BlockPos, Integer>> _southBorder = new ArrayList<TreeMap<BlockPos, Integer>>(256);
			NBTTagList southBorderNBT = nbt.getTagList("southBorder", NBTType.NBTTagList.i);

			for (int i = 0; i < 256; i++)
			{
				TreeMap<BlockPos, Integer> thisMap = new TreeMap<BlockPos, Integer>();
				NBTTagList thisMapNBT = (NBTTagList) southBorderNBT.get(i);
				size = thisMapNBT.tagCount();

				for (int j = 0; j < size; j++)
				{
					NBTTagCompound tag = thisMapNBT.getCompoundTagAt(j);
					BlockPos block = BlockPos.fromLong(tag.getLong("block"));
					int length = tag.getInteger("length");

					thisMap.put(block, length);
				}

				_southBorder.add(thisMap);
			}

			ArrayList<TreeMap<BlockPos, Integer>> _westBorder = new ArrayList<TreeMap<BlockPos, Integer>>(256);
			NBTTagList westBorderNBT = nbt.getTagList("westBorder", NBTType.NBTTagList.i);

			for (int i = 0; i < 256; i++)
			{
				TreeMap<BlockPos, Integer> thisMap = new TreeMap<BlockPos, Integer>();
				NBTTagList thisMapNBT = (NBTTagList) westBorderNBT.get(i);
				size = thisMapNBT.tagCount();

				for (int j = 0; j < size; j++)
				{
					NBTTagCompound tag = thisMapNBT.getCompoundTagAt(j);
					BlockPos block = BlockPos.fromLong(tag.getLong("block"));
					int length = tag.getInteger("length");

					thisMap.put(block, length);
				}

				_westBorder.add(thisMap);
			}

			return new TileBorder(_pos, _complete, _northBorder, _eastBorder, _southBorder, _westBorder, world);
		}

		else
		{
			return new TileBorder(_pos, _complete, null, null, null, null, world);
		}
	}

	class BorderMapThread extends Thread
	{
		boolean running = false;
		ArrayList<TreeMap<BlockPos, Integer>> map;
		ChunkPos[][] chunksPos;
		Chunk[][] chunks = new Chunk[4][4];
		ChunkPos index;

		BorderMapThread()
		{
			index = pos.getIndexChunkPos();
			chunksPos = pos.generateChunks();
		}

		@Override
		public void run()
		{
			running = true;
			generating = true;
			boolean ready = false;
			boolean toggle = false;
			BlockPos block = new BlockPos(0, 0, 0);
			int counter = 0;

			if(world.getChunkProvider() == null)
			{
				CCLog.logger.warn("ChunkProvider is null");
				return;
			}

			/*
			for(int i=0; i<4; i++)
			{
				ChunkPos[] thesePos = chunksPos[i];
				for(int j=0; j<4; j++)
				{
					ChunkPos thisPos = thesePos[j];
					
					chunks[i][j] = world.getChunkFromChunkCoords(thisPos.x, thisPos.z);
				}
			}
			
			while(!ready)
			{
				for(Chunk[] theseChunks : chunks)
				{
					for(Chunk thisChunk : theseChunks)
					{
						if(thisChunk.isTerrainPopulated())
						{
							counter++;
						}
					}
				}
				
				if(counter == 16)
				{
					ready = true;
				}
				
				else
				{
					counter = 0;
					CCLog.logger.info("Chunks not yet generated for " + pos);
					try
					{
						BorderMapThread.sleep(500);
					}
					
					catch(InterruptedException e)
					{
						return;
					}
				}
			}
			*/

			counter = 0;

			//North: lowest x, lowest z, increase x, z constant
			int iX = index.x << 4;
			int iZ = index.z << 4;
			map = new ArrayList<TreeMap<BlockPos, Integer>>();

			for (int i = 0; i < 256; i++)
			{
				TreeMap<BlockPos, Integer> thisMap = new TreeMap<BlockPos, Integer>();
				counter = 0;
				toggle = false;

				for (int j = 0; j < 64; j++)
				{
					BlockPos blockPos = new BlockPos(iX + j, i, iZ);
					IBlockState state = world.getBlockState(blockPos);

					if(world.isBlockNormalCube(blockPos, false) && toggle)
					{
						counter++;
					}

					else if(world.isBlockNormalCube(blockPos, false) && !toggle)
					{
						counter = 0;
						toggle = true;
						block = blockPos;
					}

					else if(!world.isBlockNormalCube(blockPos, false) && toggle)
					{
						toggle = false;
						thisMap.put(block, counter);
					}

					if(toggle && j == 63)
					{
						thisMap.put(block, counter);
					}
				}

				map.add(i, thisMap);
			}

			northBorder = map;

			//East: lowest z, highest x, increase z, x constant
			iX = index.x << 4 + 63;
			iZ = index.z << 4;
			map = new ArrayList<TreeMap<BlockPos, Integer>>();

			for (int i = 0; i < 256; i++)
			{
				TreeMap<BlockPos, Integer> thisMap = new TreeMap<BlockPos, Integer>();
				counter = 0;
				toggle = false;

				for (int j = 0; j < 63; j++)
				{
					BlockPos blockPos = new BlockPos(iX, i, iZ + j);
					IBlockState state = world.getBlockState(blockPos);

					if(world.isBlockNormalCube(blockPos, false) && toggle)
					{
						counter++;
					}

					else if(world.isBlockNormalCube(blockPos, false) && !toggle)
					{
						counter = 0;
						toggle = true;
						block = blockPos;
					}

					else if(!world.isBlockNormalCube(blockPos, false) && toggle)
					{
						toggle = false;
						thisMap.put(block, counter);
					}

					if(toggle && j == 63)
					{
						thisMap.put(block, counter);
					}
				}

				map.add(i, thisMap);
			}

			eastBorder = map;

			//South: highest z, highest x, decrease x, z constant
			iX = index.x << 4 + 63;
			iZ = index.z << 4 + 63;
			map = new ArrayList<TreeMap<BlockPos, Integer>>();

			for (int i = 0; i < 256; i++)
			{
				TreeMap<BlockPos, Integer> thisMap = new TreeMap<BlockPos, Integer>();
				counter = 0;
				toggle = false;

				for (int j = 0; j < 63; j++)
				{
					BlockPos blockPos = new BlockPos(iX - j, i, iZ);
					IBlockState state = world.getBlockState(blockPos);

					if(world.isBlockNormalCube(blockPos, false) && toggle)
					{
						counter++;
					}

					else if(world.isBlockNormalCube(blockPos, false) && !toggle)
					{
						counter = 0;
						toggle = true;
						block = blockPos;
					}

					else if(!world.isBlockNormalCube(blockPos, false) && toggle)
					{
						toggle = false;
						thisMap.put(block, counter);
					}

					if(toggle && j == 63)
					{
						thisMap.put(block, counter);
					}
				}

				map.add(i, thisMap);
			}

			southBorder = map;

			//West: lowest x, highest z, decrease z, x constant
			iX = index.x << 4;
			iZ = index.z << 4 + 63;
			map = new ArrayList<TreeMap<BlockPos, Integer>>();

			for (int i = 0; i < 256; i++)
			{
				TreeMap<BlockPos, Integer> thisMap = new TreeMap<BlockPos, Integer>();
				for (int j = 0; j < 64; j++)
				{
					BlockPos blockPos = new BlockPos(iX, i, iZ - j);

					if(world.isBlockNormalCube(blockPos, false) && toggle)
					{
						counter++;
					}

					else if(world.isBlockNormalCube(blockPos, false) && !toggle)
					{
						counter = 0;
						toggle = true;
						block = blockPos;
					}

					else if(!world.isBlockNormalCube(blockPos, false) && toggle)
					{
						toggle = false;
						thisMap.put(block, counter);
					}

					if(toggle && j == 63)
					{
						thisMap.put(block, counter);
					}
				}

				map.add(i, thisMap);
			}

			westBorder = map;

			running = false;
			borderCompleted();
		}
	}

	public static TileBorder bufferRead(PacketBufferCC buffer)
	{
		TilePos _pos = TilePos.fromLong(buffer.readLong());
		boolean _complete = buffer.readBoolean();

		ArrayList<TreeMap<BlockPos, Integer>> _northBorder = new ArrayList<TreeMap<BlockPos, Integer>>(256);
		int size;

		for (int i = 0; i < 256; i++)
		{
			size = buffer.readInt();
			TreeMap<BlockPos, Integer> thisMap = new TreeMap<BlockPos, Integer>();

			for (int j = 0; j < size; j++)
			{
				BlockPos block = BlockPos.fromLong(buffer.readLong());
				int length = buffer.readInt();

				thisMap.put(block, length);
			}

			_northBorder.add(thisMap);
		}

		ArrayList<TreeMap<BlockPos, Integer>> _eastBorder = new ArrayList<TreeMap<BlockPos, Integer>>(256);

		for (int i = 0; i < 256; i++)
		{
			size = buffer.readInt();
			TreeMap<BlockPos, Integer> thisMap = new TreeMap<BlockPos, Integer>();

			for (int j = 0; j < size; j++)
			{
				BlockPos block = BlockPos.fromLong(buffer.readLong());
				int length = buffer.readInt();

				thisMap.put(block, length);
			}

			_eastBorder.add(thisMap);
		}

		ArrayList<TreeMap<BlockPos, Integer>> _southBorder = new ArrayList<TreeMap<BlockPos, Integer>>(256);

		for (int i = 0; i < 256; i++)
		{
			size = buffer.readInt();
			TreeMap<BlockPos, Integer> thisMap = new TreeMap<BlockPos, Integer>();

			for (int j = 0; j < size; j++)
			{
				BlockPos block = BlockPos.fromLong(buffer.readLong());
				int length = buffer.readInt();

				thisMap.put(block, length);
			}

			_southBorder.add(thisMap);
		}

		ArrayList<TreeMap<BlockPos, Integer>> _westBorder = new ArrayList<TreeMap<BlockPos, Integer>>(256);

		for (int i = 0; i < 256; i++)
		{
			size = buffer.readInt();
			TreeMap<BlockPos, Integer> thisMap = new TreeMap<BlockPos, Integer>();

			for (int j = 0; j < size; j++)
			{
				BlockPos block = BlockPos.fromLong(buffer.readLong());
				int length = buffer.readInt();

				thisMap.put(block, length);
			}

			_westBorder.add(thisMap);
		}

		return new TileBorder(_pos, _complete, _northBorder, _eastBorder, _southBorder, _westBorder, null);
	}

	public ByteBuf bufferWrite(PacketBufferCC buffer)
	{
		buffer.writeLong(pos.toLong());
		buffer.writeBoolean(complete);

		for (int i = 0; i < 256; i++)
		{
			TreeMap<BlockPos, Integer> thisMap = northBorder.get(i);
			buffer.writeInt(thisMap.size());

			for (Map.Entry<BlockPos, Integer> entry : thisMap.entrySet())
			{
				buffer.writeLong(entry.getKey().toLong());
				buffer.writeInt(entry.getValue());
			}
		}

		for (int i = 0; i < 256; i++)
		{
			TreeMap<BlockPos, Integer> thisMap = eastBorder.get(i);
			buffer.writeInt(thisMap.size());

			for (Map.Entry<BlockPos, Integer> entry : thisMap.entrySet())
			{
				buffer.writeLong(entry.getKey().toLong());
				buffer.writeInt(entry.getValue());
			}
		}

		for (int i = 0; i < 256; i++)
		{
			TreeMap<BlockPos, Integer> thisMap = southBorder.get(i);
			buffer.writeInt(thisMap.size());

			for (Map.Entry<BlockPos, Integer> entry : thisMap.entrySet())
			{
				buffer.writeLong(entry.getKey().toLong());
				buffer.writeInt(entry.getValue());
			}
		}

		for (int i = 0; i < 256; i++)
		{
			TreeMap<BlockPos, Integer> thisMap = westBorder.get(i);
			buffer.writeInt(thisMap.size());

			for (Map.Entry<BlockPos, Integer> entry : thisMap.entrySet())
			{
				buffer.writeLong(entry.getKey().toLong());
				buffer.writeInt(entry.getValue());
			}
		}

		return buffer;
	}
}
