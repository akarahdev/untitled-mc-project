package dev.akarah.network;

import dev.akarah.nbt.element.CompoundTag;
import dev.akarah.nbt.element.Tag;
import dev.akarah.types.ApiUsage;
import dev.akarah.types.BlockPos;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Formats allow you to create a Packet using a high-level interface.
 * This interface will handle the transformation of your packet's data into bytes.<br>
 * An example format is:
 * <code>
 * RecordFormat.ofRecord(<br>
 * RecordFormat.field(Format.varInt(), BlockPos::x),<br>
 * RecordFormat.field(Format.varInt(), BlockPos::y),<br>
 * RecordFormat.field(Format.varInt(), BlockPos::z),<br>
 * BlockPos::new<br>
 * )<br>
 * </code>
 * This example turns all 3 fields of a {@link BlockPos} into a single Format.
 * The Format will output 3 VarInts (with {@link Format#varInt}), one for each field of the BlockPos.
 *
 * @param <T> The type this format will produce.
 */
public interface Format<T> {


    /**
     * Creates a new value of a type by reading from a PacketBuf.
     *
     * @param buf The PacketBuf to read from.
     * @return The value read from the buffer.
     */
    T read(PacketBuf buf);

    /**
     * Writes the provided value into a PacketBuf.
     *
     * @param buf   The PacketBuf to write into.
     * @param value The value to write into the PacketBuf.
     */
    void write(PacketBuf buf, T value);

    /**
     * Calculates the size of the data appended to a PacketBuf
     * based off of the value provided.
     *
     * @param value The value to use to calculate the size of the data.
     * @return The size of the data in bytes.
     */
    int size(T value);


    /**
     * Creates a new Format with some provided functions.
     *
     * @param reader The function that transforms the read data into T.
     * @param writer The function that writes the T's value into the PacketBuf.
     * @param size   The function that calculates the size of T in the PacketBuf.
     * @param <T>    The type this Format should produce.
     * @return A new Format instance.
     */
    @ApiUsage.Internal
    static <T> Format<T> ofSimple(
        Function<PacketBuf, T> reader,
        BiConsumer<PacketBuf, T> writer,
        Function<T, Integer> size
    ) {
        return new Format<T>() {
            @Override
            public T read(PacketBuf buf) {
                return reader.apply(buf);
            }

            @Override
            public void write(PacketBuf buf, T value) {
                writer.accept(buf, value);
            }

            @Override
            public int size(T value) {
                return size.apply(value);
            }
        };
    }

    static Format<Byte> signedByte() {
        return Format.ofSimple(PacketBuf::readByte, PacketBuf::writeByte, it -> 1);
    }

    static Format<Boolean> bool() {
        return Format.ofSimple(
            (buf) -> buf.readByte() == 1,
            (buf, ty) -> buf.writeByte((byte) (ty ? 1 : 0)),
            it -> 1
        );
    }

    static Format<Short> signedShort() {
        return Format.ofSimple(PacketBuf::readShort, PacketBuf::writeShort, it -> 2);
    }

    static Format<Integer> unsignedShort() {
        return Format.ofSimple(PacketBuf::readUnsignedShort, PacketBuf::writeUnsignedShort, it -> 2);
    }

    static Format<Integer> signedInt() {
        return Format.ofSimple(PacketBuf::readInt, PacketBuf::writeInt, it -> 4);
    }

    static Format<Long> signedLong() {
        return Format.ofSimple(PacketBuf::readLong, PacketBuf::writeLong, it -> 8);
    }

    static int calculateVarIntSize(long value) {
        int size = 0;
        while (true) {
            if ((value & ~((long) PacketBuf.SEGMENT_BITS)) == 0) {
                size++;
                return size;
            }
            size++;
            value >>= 7;
        }
    }

    static Format<Integer> varInt() {
        return Format.ofSimple(
            PacketBuf::readVarInt,
            PacketBuf::writeVarInt,
            Format::calculateVarIntSize
        );
    }

    static Format<Long> varLong() {
        return Format.ofSimple(
            PacketBuf::readVarLong,
            PacketBuf::writeVarLong,
            Format::calculateVarIntSize
        );
    }

    static Format<CompoundTag> nbtCompound() {
        return Format.ofSimple(
            buf -> {
                // read compound tag id
                buf.readByte();
                return new CompoundTag(buf);
            },
            (buf, nbt) -> {
                buf.writeByte((byte) Tag.COMPOUND.id());
                nbt.write(buf);
            },
            (ty) -> ty.size() + 1
        );
    }

    static Format<UUID> uuid() {
        return Format.ofSimple(
                PacketBuf::readUuid,
                PacketBuf::writeUuid,
                f -> Long.BYTES * 2
        );
    }

    static Format<String> string() {
        return Format.ofSimple(
            PacketBuf::readString,
            PacketBuf::writeString, it ->
            it.getBytes(StandardCharsets.UTF_8).length
                + Format.calculateVarIntSize(it.getBytes(StandardCharsets.UTF_8).length));
    }

    static Format<BlockPos> blockPos() {
        return Format.ofSimple(
            PacketBuf::readPosition,
            PacketBuf::writePosition,
            it -> Long.BYTES);
    }

    static <OutputType> Format<Optional<OutputType>> optionalOf(Format<OutputType> subformat) {
        return Format.ofSimple(
            buf -> buf.readByte() == 0
                ? Optional.<OutputType>empty()
                : Optional.of(subformat.read(buf)),
            (buf, ty) -> {
                ty.ifPresentOrElse(
                    inner -> {
                        buf.writeByte((byte) 1);
                        subformat.write(buf, inner);
                    },
                    () -> buf.writeByte((byte) 0)
                );
            },
            ty -> ty.map(subformat::size).orElse(0) + 1
        );
    }

    static <OutputType> Format<Optional<OutputType>> terminalOptionalOf(Format<OutputType> subformat) {
        return Format.ofSimple(
            buf -> buf.writeOffset >= buf.buffer.length
                ? Optional.<OutputType>empty()
                : Optional.of(subformat.read(buf)),
            (buf, ty) -> {
                ty.ifPresent(inner -> subformat.write(buf, inner));
            },
            ty -> ty.map(subformat::size).orElse(0)
        );
    }

    static <ArrayType> Format<List<ArrayType>> arrayOf(Format<ArrayType> subformat) {
        return Format.ofSimple(
            buf -> {
                var len = buf.readVarInt();
                var arr = new ArrayList<ArrayType>(len);
                for(int i = 0; i < len; i++) {
                    arr.add(subformat.read(buf));
                }
                return arr;
            },
            (buf, ty) -> {
                buf.writeVarInt(ty.size());
                for (ArrayType arrayType : ty) {
                    subformat.write(buf, arrayType);
                }
            },
            ty -> ty.stream().mapToInt(subformat::size).sum()
                    + calculateVarIntSize(ty.size())
        );
    }

    static <ArrayType> Format<List<ArrayType>> terminalArrayOf(Format<ArrayType> subformat) {
        return Format.ofSimple(
                buf -> {
                    var arr = new ArrayList<ArrayType>();
                    while(buf.readOffset < buf.buffer.length) {
                        arr.add(subformat.read(buf));
                    }
                    return arr;
                },
                (buf, ty) -> {
                    for (ArrayType arrayType : ty) {
                        subformat.write(buf, arrayType);
                    }
                },
                ty -> ty.stream().mapToInt(subformat::size).sum()
        );
    }
}
