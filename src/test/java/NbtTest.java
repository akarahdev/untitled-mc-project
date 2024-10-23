import dev.akarah.nbt.element.CompoundTag;
import dev.akarah.nbt.element.IntTag;
import dev.akarah.nbt.element.LongTag;
import dev.akarah.nbt.element.Tag;
import dev.akarah.network.PacketBuf;

import java.util.Arrays;

public class NbtTest {
    public static void main(String[] args) {
        var nbt = new CompoundTag();
        nbt.put("abc", new LongTag(10));

        System.out.println(nbt);

        var buf = PacketBuf.allocate(nbt.size() + 1);
        buf.writeByte((byte) Tag.COMPOUND.id());
        nbt.write(buf);

        System.out.println(Arrays.toString(buf.toArray()));
    }
}
