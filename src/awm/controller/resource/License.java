package awm.controller.resource;

public class License extends Resource {
    public License(String name, long avail) {
        super(avail);
        this.name = name;
    }

    public final String name;
}
