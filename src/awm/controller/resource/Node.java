package awm.controller.resource;

public class Node {
    public Node(String name, long memKB, long ncores) {
        this.name = name;
        __memKB = new Resource(memKB);
        __ncores = new Resource(ncores);
    }

    public long memAvail() {
        return __memKB.available();
    }

    public long ncoreAvail() {
        return __ncores.available();
    }

    public long updateMemAvail(long memKB) {
        return __memKB.setAvailable(memKB);
    }

    public final String name;
    private final Resource __memKB, __ncores;
}
