import dev.akarah.network.Format;
import dev.akarah.network.RecordFormat;

public class TestServer {
    record TestRecord(int size) {

    }
    public static void main(String[] args) {
        var fmt = RecordFormat.ofRecord(
            RecordFormat.field(Format.signedInt(), TestRecord::size),
            TestRecord::new
        );
    }
}
