package net.civiscraft.lib.net;

import com.google.common.base.Charsets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.MathHelper;

public class PacketBufferCC extends PacketBuffer
{
	private int readPartialOffset = 8;
	private int readPartialCache;
	
	private int writePartialIndex = -1;
	private int writePartialOffset;
	private int writePartialCache;
	
	public PacketBufferCC(ByteBuf wrapped)
	{
		super(wrapped);
	}
	
	public static PacketBufferCC asPacketBufferCc(ByteBuf buf)
	{
		if (buf instanceof PacketBufferCC) {return (PacketBufferCC) buf;}
		
		else {return new PacketBufferCC(buf);}
	}
	
	public static PacketBufferCC write(IMessageWriter writer)
	{
		PacketBufferCC buffer = new PacketBufferCC(Unpooled.buffer());
		writer.write(buffer);
		return buffer;
	}
	
	@Override
	public PacketBufferCC clear()
	{
		super.clear();
		readPartialOffset = 8;
		readPartialCache = 0;
		writePartialIndex = -1;
		writePartialOffset = 0;
		writePartialCache = 0;
		return this;
	}
	
	void writePartialBitsBegin() 
	{
        if (writePartialIndex == -1 || writePartialOffset == 8) 
        {
            writePartialIndex = writerIndex();
            writePartialOffset = 0;
            writePartialCache = 0;
            writeByte(0);
        }
    }

    void readPartialBitsBegin() 
    {
        if (readPartialOffset == 8) 
        {
            readPartialOffset = 0;
            readPartialCache = readUnsignedByte();
        }
	}
    
    @Override
    public PacketBufferCC writeBoolean(boolean flag) 
    {
        writePartialBitsBegin();
        int toWrite = (flag ? 1 : 0) << writePartialOffset;
        writePartialCache |= toWrite;
        writePartialOffset++;
        setByte(writePartialIndex, writePartialCache);
        return this;
    }
    
    @Override
    public boolean readBoolean() 
    {
        readPartialBitsBegin();
        int offset = 1 << readPartialOffset++;
        return (readPartialCache & offset) == offset;
    }
    
    public PacketBufferCC writeFixedBits(int value, int length) throws IllegalArgumentException 
    {
        if (length <= 0) 
        {
            throw new IllegalArgumentException("Tried to write too few bits! (" + length + ")");
        }
        
        if (length > 32) 
        {
            throw new IllegalArgumentException("Tried to write more bits than are in an integer! (" + length + ")");
        }

        writePartialBitsBegin();

        // - length = 10
        // - bits = 0123456789

        // current
        // (# = already written, _ is not yet written)
        // - in buffer [######## _#######]
        // - writePartialCache = "_#######"
        // - writePartialOffset = 7

        // want we want:
        // - in buffer [######## 0###### 12345678 _______9 ]
        // - writePartialCache = "_______9"
        // - writePartialOffset = 1

        // first stage: take the toppermost bits and append them to the cache (if the cache contains bits)
        if (writePartialOffset > 0) 
        {

            // top length = 8 - (num bits in cache) or length, whichever is SMALLER
            int availableBits = 8 - writePartialOffset;

            if (availableBits >= length) 
            {
                int mask = (1 << length) - 1;
                int bitsToWrite = value & mask;

                writePartialCache |= bitsToWrite << writePartialOffset;
                setByte(writePartialIndex, writePartialCache);
                writePartialOffset += length;
                // we just wrote out the entire length, no need to do anything else.
                return this;
            } 
            
            else 
            { // topLength < length -- we will still need to be writing out more bits after this
                // length = 10
                // topLength = 1
                // value = __01 2345 6789
                // want == ____ ____ ___0
                // mask == ____ ____ ___1
                // shift back = 9

                int mask = (1 << availableBits) - 1;

                int shift = length - availableBits;

                int bitsToWrite = (value >>> shift) & mask;

                writePartialCache |= bitsToWrite << writePartialOffset;
                setByte(writePartialIndex, writePartialCache);

                // we finished a byte, reset values so that the next write will reset and create a new byte
                writePartialCache = 0;
                writePartialOffset = 8;

                // now shift the value down ready for the next iteration
                length -= availableBits;
            }
        }
        
        while (length >= 8) 
        {
            // write out full 8 bit chunks of the length until we reach 0
            writePartialBitsBegin();

            int byteToWrite = (value >>> (length - 8)) & 0xFF;

            setByte(writePartialIndex, byteToWrite);

            // we finished a byte, reset values so that the next write will reset and create a new byte
            writePartialCache = 0;
            writePartialOffset = 8;

            length -= 8;
        }

        if (length > 0) 
        {
            // we have a few bits left over to append
            writePartialBitsBegin();

            int mask = (1 << length) - 1;
            writePartialCache = value & mask;
            setByte(writePartialIndex, writePartialCache);
            writePartialOffset = length;
        }

        return this;
    }
    
    public int readFixedBits(int length) throws IllegalArgumentException 
    {
        if (length <= 0) 
        {
            throw new IllegalArgumentException("Tried to read too few bits! (" + length + ")");
        }
        
        if (length > 32) 
        {
            throw new IllegalArgumentException("Tried to read more bits than are in an integer! (" + length + ")");
        }
        
        readPartialBitsBegin();

        int value = 0;

        if (readPartialOffset > 0) 
        {
            // If we have bits left at the top of the buffer...
            int availableBits = 8 - readPartialOffset;
            
            if (availableBits >= length) 
            {
                // If the wanted bits are completely contained within the cache
                int mask = (1 << length) - 1;
                value = (readPartialCache >>> readPartialOffset) & mask;
                readPartialOffset += length;
                return value;
            } 
            
            else 
            {
                // If we need to read more bits than are available in the cache
                int bitsRead = readPartialCache >>> readPartialOffset;

                value = bitsRead;

                // We finished reading a byte, reset values so the next step will read them properly

                readPartialCache = 0;
                readPartialOffset = 8;

                length -= availableBits;
            }
        }

        while (length >= 8) 
        {
            readPartialBitsBegin();
            length -= 8;
            value <<= 8;
            value |= readPartialCache;
            readPartialOffset = 8;
        }

        if (length > 0) 
        {
            readPartialBitsBegin();

            int mask = (1 << length) - 1;

            value <<= length;
            value |= readPartialCache & mask;
            readPartialOffset = length;
        }

        return value;
    }
    
    @Override
    public PacketBufferCC writeEnumValue(Enum<?> value)
    {
    	Enum<?>[] possible = value.getClass().getEnumConstants();
    	if(possible == null)
    	{
    		throw new IllegalArgumentException("Not an enum " + value.getClass());
    	}
    	
    	if(possible.length == 0)
    	{
    		throw new IllegalArgumentException("Tried to write an enum value without any values.");
    	}
    	
    	if(possible.length == 1) { return this;}
    	
    	writeFixedBits(value.ordinal(), MathHelper.log2DeBruijn(possible.length));
    	
    	return this;
    }
    
    @Override
    public <E extends Enum<E>> E readEnumValue(Class<E> enumClass) 
    {
        E[] enums = enumClass.getEnumConstants();
        if (enums == null) throw new IllegalArgumentException("Not an enum " + enumClass);
        if (enums.length == 0) throw new IllegalArgumentException("Tried to read an enum value without any values! How did you do this?");
        if (enums.length == 1) return enums[0];
        int length = MathHelper.log2DeBruijn(enums.length);
        int index = readFixedBits(length);
        return enums[index];
    }
    
    public String readString() 
    {
        return new String(readBytes(readVarInt()).array(), Charsets.UTF_8);
    }
}
