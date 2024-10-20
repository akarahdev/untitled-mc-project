import dev.akarah.network.Format;
import dev.akarah.network.PacketBuf;
import dev.akarah.types.BlockPos;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public class TestServer {
    record TestRecord(int size) {

    }
    public static void main(String[] args) {
        var fmt = Format.ofRecord(
            Format.field(Format.signedInt(), TestRecord::size),
            TestRecord::new
        );
    }
}
