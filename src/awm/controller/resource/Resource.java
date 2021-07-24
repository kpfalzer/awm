package awm.controller.resource;

/**
 * Consumable resource.
 */
public class Resource {
    public Resource(long allotment) {
        setAvailable(allotment);
    }

    public long available() {
        return __available;
    }

    public long setAvailable(long amt) {
        return (__available = amt);
    }

    public boolean available(long request) {
        return request <= __available;
    }

    public long debit(long request) {
        return (__available -= request);
    }

    public long credit(long used) {
        return (__available += used);
    }

    private long __available;
}
