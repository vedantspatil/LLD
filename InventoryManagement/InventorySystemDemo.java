package InventoryManagement;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

enum RequestType { ADD, REMOVE, TRANSFER; }

class Warehouse {
    private String id;
    private Map<String, Integer> inventory = new HashMap<>();
    public Warehouse(String id) { this.id = id; }
    public String getId() { return id; }
    public void addStock(String productId, int qty) {
        inventory.put(productId, inventory.getOrDefault(productId, 0) + qty);
    }
    public boolean removeStock(String productId, int qty) {
        int cur = inventory.getOrDefault(productId, 0);
        if (cur < qty) return false;
        inventory.put(productId, cur - qty);
        return true;
    }
    public int getStock(String productId) { return inventory.getOrDefault(productId, 0); }
}

class InventoryManager {
    private Map<String, Warehouse> warehouses = new HashMap<>();
    private Queue<InventoryRequest> requests = new LinkedList<>();
    
    public void addWarehouse(Warehouse w) { warehouses.put(w.getId(), w); }
    public void addRequest(InventoryRequest req) { requests.add(req); }
    
    public void processRequests() {
        while (!requests.isEmpty()) {
            InventoryRequest req = requests.poll();
            switch (req.type) {
                case ADD:
                    warehouses.get(req.warehouseId).addStock(req.productId, req.quantity);
                    break;
                case REMOVE:
                    warehouses.get(req.warehouseId).removeStock(req.productId, req.quantity);
                    break;
                case TRANSFER:
                    Warehouse from = warehouses.get(req.warehouseId);
                    Warehouse to = warehouses.get(req.targetWarehouseId);
                    if (from.removeStock(req.productId, req.quantity))
                        to.addStock(req.productId, req.quantity);
                    break;
            }
        }
    }
    
    // Inner class representing a single inventory request.
    public static class InventoryRequest {
        RequestType type;
        String productId;
        int quantity;
        String warehouseId;       // For ADD, REMOVE, or source for TRANSFER.
        String targetWarehouseId; // For TRANSFER only.
        
        public InventoryRequest(RequestType type, String productId, int qty, String warehouseId) {
            this.type = type; this.productId = productId; this.quantity = qty; this.warehouseId = warehouseId;
        }
        public InventoryRequest(String productId, int qty, String fromWarehouseId, String toWarehouseId) {
            this.type = RequestType.TRANSFER; this.productId = productId; this.quantity = qty;
            this.warehouseId = fromWarehouseId; this.targetWarehouseId = toWarehouseId;
        }
    }
}

public class InventorySystemDemo {
    public static void main(String[] args) {
        InventoryManager manager = new InventoryManager();
        Warehouse w1 = new Warehouse("W1"), w2 = new Warehouse("W2");
        manager.addWarehouse(w1); manager.addWarehouse(w2);
        
        // Queue some requests.
        manager.addRequest(new InventoryManager.InventoryRequest(RequestType.ADD, "P1", 100, "W1"));
        manager.addRequest(new InventoryManager.InventoryRequest(RequestType.REMOVE, "P1", 20, "W1"));
        manager.addRequest(new InventoryManager.InventoryRequest("P1", 50, "W1", "W2"));
        
        // Process all requests.
        manager.processRequests();
        
        System.out.println("W1 stock for P1: " + w1.getStock("P1"));
        System.out.println("W2 stock for P1: " + w2.getStock("P1"));
    }
}
