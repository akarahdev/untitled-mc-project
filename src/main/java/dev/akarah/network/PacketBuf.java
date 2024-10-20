package dev.akarah.network;

import dev.akarah.types.BlockPos;

import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class PacketBuf {
    byte[] buffer;

    int readOffset = 0;
    int writeOffset = 0;

    private static final int SEGMENT_BITS = 0x7F;
    private static final int CONTINUE_BIT = 0x80;

    static VarHandle VH_SHORT = MethodHandles.byteArrayViewVarHandle(short[].class, ByteOrder.BIG_ENDIAN);
    static VarHandle VH_INT = MethodHandles.byteArrayViewVarHandle(int[].class, ByteOrder.BIG_ENDIAN);
    static VarHandle VH_LONG = MethodHandles.byteArrayViewVarHandle(long[].class, ByteOrder.BIG_ENDIAN);

    static VarHandle VH_FLOAT = MethodHandles.byteArrayViewVarHandle(float[].class, ByteOrder.BIG_ENDIAN);
    static VarHandle VH_DOUBLE = MethodHandles.byteArrayViewVarHandle(double[].class, ByteOrder.BIG_ENDIAN);

    private PacketBuf() {}

    public static PacketBuf allocate(int size) {
        var pb = new PacketBuf();
        pb.buffer = new byte[size];
        return pb;
    }

    public PacketBuf withReading() {
        var pb = new PacketBuf();
        pb.buffer = this.buffer;
        return pb;
    }

    public PacketBuf withWriting() {
        var pb = new PacketBuf();
        pb.buffer = new byte[this.buffer.length];
        System.arraycopy(this.buffer, 0, pb.buffer, 0, this.buffer.length);
        return pb;
    }

    public PacketBuf writeByte(byte value) {
        this.buffer[this.writeOffset] = value;
        this.writeOffset += 1;
        return this;
    }

    public byte readByte() {
        var v = this.buffer[this.readOffset];
        this.readOffset += 1;
        return v;
    }

    public PacketBuf writeUnsignedByte(int value) {
        this.buffer[this.writeOffset] = (byte) (value & 0xFF);
        this.writeOffset += 1;
        return this;
    }

    public int readUnsignedByte() {
        var v = this.buffer[this.readOffset];
        this.readOffset += 1;
        return Byte.toUnsignedInt(v) & 0xFF;
    }

    public PacketBuf writeShort(short value) {
        VH_SHORT.set(this.buffer, writeOffset, value);
        writeOffset += Short.BYTES;
        return this;
    }

    public short readShort() {
        var value = (short) VH_SHORT.get(this.buffer, readOffset);
        readOffset += Short.BYTES;
        return value;
    }

    public PacketBuf writeUnsignedShort(int value) {
        VH_SHORT.set(this.buffer, writeOffset, (short) value & 0xFFFF);
        writeOffset += Short.BYTES;
        return this;
    }

    public int readUnsignedShort() {
        var value = Short.toUnsignedInt((short) VH_SHORT.get(this.buffer, readOffset));
        readOffset += Short.BYTES;
        return value;
    }

    public PacketBuf writeInt(int value) {
        VH_INT.set(this.buffer, writeOffset, value);
        writeOffset += Integer.BYTES;
        return this;
    }

    public int readInt() {
        var value = (int) VH_INT.get(this.buffer, readOffset);
        readOffset += Integer.BYTES;
        return value;
    }

    public PacketBuf writeLong(long value) {
        VH_LONG.set(this.buffer, writeOffset, value);
        writeOffset += Long.BYTES;
        return this;
    }

    public long readLong() {
        var value = (long) VH_LONG.get(this.buffer, readOffset);
        readOffset += Long.BYTES;
        return value;
    }

    public int readVarInt() {
        int value = 0;
        int position = 0;
        byte currentByte;

        while (true) {
            currentByte = readByte();
            value |= (currentByte & SEGMENT_BITS) << position;

            if ((currentByte & CONTINUE_BIT) == 0) break;

            position += 7;

            if (position >= 32) throw new RuntimeException("VarInt is too big");
        }

        return value;
    }

    public long readVarLong() {
        long value = 0;
        int position = 0;
        byte currentByte;

        while (true) {
            currentByte = readByte();
            value |= (long) (currentByte & SEGMENT_BITS) << position;

            if ((currentByte & CONTINUE_BIT) == 0) break;

            position += 7;

            if (position >= 64) throw new RuntimeException("VarLong is too big");
        }

        return value;
    }

    public void writeVarInt(int value) {
        while (true) {
            if ((value & ~SEGMENT_BITS) == 0) {
                writeByte((byte) value);
                return;
            }

            writeByte((byte) ((value & SEGMENT_BITS) | CONTINUE_BIT));

            // Note: >>> means that the sign bit is shifted with the rest of the number rather than being left alone
            value >>>= 7;
        }
    }

    public void writeVarLong(long value) {
        while (true) {
            if ((value & ~((long) SEGMENT_BITS)) == 0) {
                writeByte((byte) value);
                return;
            }

            writeByte((byte) ((value & SEGMENT_BITS) | CONTINUE_BIT));

            // Note: >>> means that the sign bit is shifted with the rest of the number rather than being left alone
            value >>>= 7;
        }
    }

    public PacketBuf writeString(String value) {
        writeVarInt(value.length());
        for(var bv : value.getBytes(StandardCharsets.UTF_8))
            writeByte(bv);
        return this;
    }

    public String readString() {
        var length = readVarInt();
        var bytes = new byte[length];
        for(int i = 0; i < length; i++) {
            bytes[i] = readByte();
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public PacketBuf writeUuid(UUID uuid) {
        writeLong(uuid.getMostSignificantBits());
        writeLong(uuid.getLeastSignificantBits());
        return this;
    }

    public UUID readUuid() {
        return new UUID(
            readLong(),
            readLong()
        );
    }

    public PacketBuf writePosition(int x, int y, int z) {
        writeLong(((long) (x & 0x3FFFFFF) << 38) | ((long) (z & 0x3FFFFFF) << 12) | (y & 0xFFF));
        return this;
    }

    public PacketBuf writePosition(BlockPos blockPos) {
        writePosition(blockPos.x(), blockPos.y(), blockPos.z());
        return this;
    }

//    public<T> Optional<T> readOptional(Function<PacketBuf, T> operation) {
//        if(this.readOffset >= this.buffer.length)
//            return Optional.empty();
//        return Optional.of(operation.apply(this));
//    }
//
//    public<T> PacketBuf writeOptional(Optional<T> value, BiFunction<PacketBuf, T, PacketBuf> function) {
//        value.ifPresent(t -> function.apply(this, t));
//        return this;
//    }
//
//    public<T> T[] readArray(Function<PacketBuf, T> operation) {
//        var length = readVarInt();
//        var array = new Object[length];
//        for(int i = 0; i < length; i++) {
//            array[i] = operation.apply(this);
//        }
//        return (T[]) array;
//    }
//
//    public<T> PacketBuf writeArray(T[] value, BiFunction<PacketBuf, T, PacketBuf> function) {
//        writeVarInt(value.length);
//        for(var element : value)
//            function.apply(this, element);
//        return this;
//    }

    public BlockPos readPosition() {
        var val = readLong();
        return new BlockPos(
            (int) (val >> 38),
            (int) (val << 52 >> 52),
            (int) (val << 26 >> 38)
        );
    }
}
