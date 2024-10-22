package dev.akarah.nbt.element;

import java.util.List;

public sealed interface Tag<T extends NbtTag> {
    public static Tag<ByteTag> END = new EndType();
    public static Tag<dev.akarah.nbt.element.ByteTag> BYTE = new ByteType();
    public static Tag<ShortTag> SHORT = new ShortType();
    public static Tag<IntTag> INTEGER = new IntegerType();
    public static Tag<LongTag> LONG = new LongType();
    public static Tag<FloatTag> FLOAT = new FloatType();
    public static Tag<DoubleTag> DOUBLE = new DoubleType();
    public static Tag<ByteArrayTag> BYTE_ARRAY = new ByteArrayType();
    public static Tag<StringTag> STRING = new StringType();
    public static Tag<ListTag<NbtTag>> LIST = new ListType<NbtTag>();
    public static Tag<CompoundTag> COMPOUND = new CompoundType();
    public static Tag<?> INT_ARRAY = new IntArrayType();
    public static Tag<?> LONG_ARRAY = new IntArrayType();
    public static List<Tag<?>> TAGS = List.of(
        END,
        BYTE,
        SHORT,
        INTEGER,
        LONG,
        FLOAT,
        DOUBLE,
        BYTE_ARRAY,
        STRING,
        LIST,
        COMPOUND,
        INT_ARRAY,
        LONG_ARRAY
    );

    int id();
    Class<T> getTypeClass();

    record EndType() implements Tag<ByteTag> {
        @Override
        public int id() {
            return 0;
        }

        @Override
        public Class<ByteTag> getTypeClass() {
            return ByteTag.class;
        }
    }
    record ByteType() implements Tag<ByteTag> {
        @Override
        public int id() {
            return 1;
        }

        @Override
        public Class<ByteTag> getTypeClass() {
            return ByteTag.class;
        }
    }
    record ShortType() implements Tag<ShortTag> {
        @Override
        public int id() {
            return 2;
        }

        @Override
        public Class<ShortTag> getTypeClass() {
            return ShortTag.class;
        }
    }
    record IntegerType() implements Tag<IntTag> {
        @Override
        public int id() {
            return 3;
        }

        @Override
        public Class<IntTag> getTypeClass() {
            return IntTag.class;
        }
    }
    record LongType() implements Tag<LongTag> {
        @Override
        public int id() {
            return 4;
        }

        @Override
        public Class<LongTag> getTypeClass() {
            return LongTag.class;
        }
    }
    record FloatType() implements Tag<FloatTag> {
        @Override
        public int id() {
            return 5;
        }

        @Override
        public Class<FloatTag> getTypeClass() {
            return FloatTag.class;
        }
    }
    record DoubleType() implements Tag<DoubleTag> {
        @Override
        public int id() {
            return 6;
        }

        @Override
        public Class<DoubleTag> getTypeClass() {
            return DoubleTag.class;
        }
    }
    record ByteArrayType() implements Tag<ByteArrayTag> {
        @Override
        public int id() {
            return 7;
        }

        @Override
        public Class<ByteArrayTag> getTypeClass() {
            return ByteArrayTag.class;
        }
    }
    record StringType() implements Tag<StringTag> {
        @Override
        public int id() {
            return 8;
        }

        @Override
        public Class<StringTag> getTypeClass() {
            return StringTag.class;
        }
    }
    record ListType<T extends NbtTag>() implements Tag<ListTag<T>> {
        @Override
        public int id() {
            return 9;
        }

        @Override
        public Class<ListTag<T>> getTypeClass() {
            return (Class<ListTag<T>>) (Class<?>) ListTag.class;
        }
    }
    record CompoundType() implements Tag<CompoundTag> {
        @Override
        public int id() {
            return 10;
        }

        @Override
        public Class<CompoundTag> getTypeClass() {
            return CompoundTag.class;
        }
    }
    record IntArrayType() implements Tag<dev.akarah.nbt.element.ByteTag> {
        @Override
        public int id() {
            return 11;
        }

        @Override
        public Class<ByteTag> getTypeClass() {
            return ByteTag.class;
        }
    }
    record LongArrayType() implements Tag<dev.akarah.nbt.element.ByteTag> {
        @Override
        public int id() {
            return 12;
        }

        @Override
        public Class<ByteTag> getTypeClass() {
            return ByteTag.class;
        }
    }
}
