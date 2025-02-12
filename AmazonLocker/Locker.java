package AmazonLocker;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;

enum Size {
    SMALL(0),
    MEDIUM(1),
    LARGE(2);

    private int numVal;

    Size(int numVal) {
        this.numVal = numVal;
    }

    public int getNumVal() {
        return numVal;
    }
}

class PickupLocation {

    Map<Size, Queue<Locker>> availableLockers;
    Map<String, Locker> packageLoc;

    public PickupLocation(Map<Size, Integer> lockerSizes) {
        availableLockers = new HashMap<>();
        packageLoc = new HashMap<>();
        // Initialize lockers
        for (Map.Entry<Size, Integer> entry : lockerSizes.entrySet()) {
            Queue<Locker> lockerQ = new LinkedList<>();
            for (int i = 0; i < entry.getValue(); i++) {
                lockerQ.add(new Locker(entry.getKey()));
            }
            availableLockers.put(entry.getKey(), lockerQ);
        }
    }

    public Locker assignPackage(Package p) {
        for (Size sz : Size.values()) {
            // Don't try to assign package to a smaller size
            if (sz.getNumVal() < p.getSize().getNumVal()) continue;
            Locker assignedLocker = assignLocker(p, sz);
            // There is a locker found for the package
            if (assignedLocker != null) return assignedLocker;
            // Continue with one size larger
        }
        return null;
    }

    public Package getPackage(String packageId) {
        if (!packageLoc.containsKey(packageId))
            return null;
        Locker locker = packageLoc.get(packageId);
        Package p = locker.emptyLocker();
        // Put the locker back in the available queue
        availableLockers.get(locker.getSize()).add(locker);
        return p;
    }

    private Locker assignLocker(Package p, Size sz) {
        if (availableLockers.get(sz).isEmpty())
            return null;
        // Remove locker from the available queue
        Locker lockerToAssign = availableLockers.get(sz).poll();
        lockerToAssign.assignPackage(p);
        packageLoc.put(p.getId(), lockerToAssign);
        return lockerToAssign;
    }
}

class Locker {
    private final Size lockerSize;
    private final String lockerId;
    private Package packageInsideLocker;

    Locker(Size size) {
        this.lockerSize = size;
        this.lockerId = UUID.randomUUID().toString();
    }

    Size getSize() {
        return lockerSize;
    }

    private String getId() {
        return lockerId;
    }

    void assignPackage(Package p) {
        packageInsideLocker = p;
    }

    Package emptyLocker() {
        Package p = packageInsideLocker;
        packageInsideLocker = null;
        return p;
    }
}

class Package {
    private final Size packageSize;
    private final String packageId;

    Package(Size size) {
        this.packageSize = size;
        this.packageId = UUID.randomUUID().toString();
    }

    Size getSize() {
        return packageSize;
    }

    String getId() {
        return packageId;
    }
}
