package dev.akarah.network;

import java.lang.reflect.Parameter;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface Format<T> {
    T read(PacketBuf buf);
    void write(PacketBuf buf, T value);
    int size(T value);

    static<T> Format<T> ofSimple(
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

    record PacketField<RecordType, ParameterType>(
        Format<ParameterType> parameter,
        Function<RecordType, ParameterType> getter
    ) {}

    static<RecordType, ParameterType> PacketField<RecordType, ParameterType> field(
        Format<ParameterType> parameter,
        Function<RecordType, ParameterType> getter
    ) {
        return new PacketField<>(parameter, getter);
    }

    static<RecordType, Param1> Format<RecordType> ofRecord(
        PacketField<RecordType, Param1> parameter1,
        Function<Param1, RecordType> constructor
    ) {
        return Format.ofSimple(
            buf -> {
                return constructor.apply(parameter1.parameter.read(buf));
            },
            (buf, ty) -> {
                parameter1.parameter.write(buf, parameter1.getter.apply(ty));
            },
            ty -> {
                return parameter1.parameter.size(parameter1.getter.apply(ty));
            }
        );
    }

    static<RecordType, Param1, Param2> Format<RecordType> ofRecord(
        PacketField<RecordType, Param1> parameter1,
        PacketField<RecordType, Param2> parameter2,
        BiFunction<Param1, Param2, RecordType> constructor
    ) {
        return Format.ofSimple(
            buf -> {
                return constructor.apply(
                    parameter1.parameter.read(buf),
                    parameter2.parameter.read(buf)
                );
            },
            (buf, ty) -> {
                parameter1.parameter.write(buf, parameter1.getter.apply(ty));
                parameter2.parameter.write(buf, parameter2.getter.apply(ty));
            },
            ty -> {
                return parameter1.parameter.size(parameter1.getter.apply(ty))
                    + parameter2.parameter.size(parameter2.getter.apply(ty));
            }
        );
    }

    static Format<Byte> signedByte() {
        return Format.ofSimple(PacketBuf::readByte, PacketBuf::writeByte, it -> 1);
    }

    static Format<Short> signedShort() {
        return Format.ofSimple(PacketBuf::readShort, PacketBuf::writeShort, it -> 1);
    }

    static Format<Integer> unsignedShort() {
        return Format.ofSimple(PacketBuf::readUnsignedShort, PacketBuf::writeUnsignedShort, it -> 1);
    }

    static Format<Integer> signedInt() {
        return Format.ofSimple(PacketBuf::readInt, PacketBuf::writeInt, it -> 1);
    }

    static Format<Long> signedLong() {
        return Format.ofSimple(PacketBuf::readLong, PacketBuf::writeLong, it -> 1);
    }

    static Integer calculateVarIntSize(long integer) {
        return (integer & ~PacketBuf.SEGMENT_BITS) == 0 ? 1 : 1 + calculateVarIntSize(integer >>> 7);
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

    static Format<String> string() {
        return Format.ofSimple(PacketBuf::readString, PacketBuf::writeString, String::length);
    }
}
