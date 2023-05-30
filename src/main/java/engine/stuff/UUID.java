package engine.stuff;

public class UUID {

    private final long uuid;

    public UUID() { this.uuid = (long) (Math.random() * Long.MAX_VALUE); }

    public UUID(long uuid) { this.uuid = uuid; }

    public UUID(UUID ref) { this.uuid = ref.uuid; }

    @Override
    public String toString() { return "" + this.uuid; }

    public long get() { return this.uuid; }
}
