package dev.akarah.network;

import dev.akarah.functions.TriFunction;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface RecordFormat {
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

    // These record functions let you build multiple Formats into one, and then compile the output into
    // a constructor. These Formats can be chained together to make a packet, or have a packet with various subfields.
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

    static<RecordType, Param1, Param2, Param3> Format<RecordType> ofRecord(
        PacketField<RecordType, Param1> parameter1,
        PacketField<RecordType, Param2> parameter2,
        PacketField<RecordType, Param3> parameter3,
        TriFunction<Param1, Param2, Param3, RecordType> constructor
    ) {
        return Format.ofSimple(
            buf -> {
                return constructor.apply(
                    parameter1.parameter.read(buf),
                    parameter2.parameter.read(buf),
                    parameter3.parameter.read(buf)
                );
            },
            (buf, ty) -> {
                parameter1.parameter.write(buf, parameter1.getter.apply(ty));
                parameter2.parameter.write(buf, parameter2.getter.apply(ty));
                parameter3.parameter.write(buf, parameter3.getter.apply(ty));
            },
            ty -> {
                return parameter1.parameter.size(parameter1.getter.apply(ty))
                    + parameter2.parameter.size(parameter2.getter.apply(ty))
                    + parameter3.parameter.size(parameter3.getter.apply(ty));
            }
        );
    }
}
