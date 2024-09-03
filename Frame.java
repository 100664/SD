public class Frame {

    public final int tag;
    public final Serializavel data;

    public Frame(int tag, Serializavel data) {
        this.tag = tag;
        this.data = data;
    }
}
