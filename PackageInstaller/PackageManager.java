package PackageInstaller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PackageManager {

    // Internal dependency graph: key = package, value = list of its dependencies.
    private final Map<String, List<String>> dependencyGraph;

    // Constructor: initializes an empty dependency graph.
    public PackageManager() {
        this.dependencyGraph = new HashMap<>();
    }

    /**
     * Adds a package to the graph.
     * If the package already exists, nothing changes.
     */
    public void addPackage(String pkg) {
        dependencyGraph.putIfAbsent(pkg, new ArrayList<>());
    }

    /**
     * Adds a dependency: package 'pkg' depends on 'dependency'.
     */
    public void addDependency(String pkg, String dependency) {
        // Ensure both packages are in the graph.
        addPackage(pkg);
        addPackage(dependency);
        // Add the dependency relationship.
        dependencyGraph.get(pkg).add(dependency);
    }

    /**
     * Computes a valid installation order.
     * @return List of package names in installation order.
     * @throws CycleDetectedException if a cyclic dependency is found.
     */
    public List<String> getInstallationOrder() throws CycleDetectedException {
        // Build the complete set of packages.
        Set<String> allPackages = new HashSet<>();
        for (String pkg : dependencyGraph.keySet()) {
            allPackages.add(pkg);
            allPackages.addAll(dependencyGraph.get(pkg));
        }

        List<String> order = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Set<String> recStack = new HashSet<>();

        // Perform DFS from each package not yet visited.
        for (String pkg : allPackages) {
            if (!visited.contains(pkg)) {
                dfs(pkg, visited, recStack, order);
            }
        }
        // Reverse the postorder to get a valid installation sequence.
        //Collections.reverse(order);
        return order;
    }

    /**
     * Helper DFS method that performs topological sort and cycle detection.
     */
    private void dfs(String pkg, Set<String> visited, Set<String> recStack, List<String> order)
            throws CycleDetectedException {
        visited.add(pkg);
        recStack.add(pkg);

        // Traverse each dependency of the current package.
        for (String dependency : dependencyGraph.getOrDefault(pkg, Collections.emptyList())) {
            if (!visited.contains(dependency)) {
                dfs(dependency, visited, recStack, order);
            } else if (recStack.contains(dependency)) {
                throw new CycleDetectedException("Cycle detected involving package: " + dependency);
            }
        }
        // Backtrack.
        recStack.remove(pkg);
        order.add(pkg);
    }

    /**
     * Custom exception to indicate a cycle in the dependency graph.
     */
    public static class CycleDetectedException extends Exception {
        public CycleDetectedException(String message) {
            super(message);
        }
    }
    
    // --- Example usage ---
    public static void main(String[] args) {
        // Create an instance of PackageManager.
        PackageManager pm = new PackageManager();
        
        // Example dependencies:
        // a -> [b, c, d] and d -> [e, f]
        pm.addDependency("a", "b");
        pm.addDependency("a", "c");
        pm.addDependency("a", "d");
        pm.addDependency("d", "e");
        pm.addDependency("d", "f");
        
        try {
            List<String> installOrder = pm.getInstallationOrder();
            System.out.println("Installation Order: " + installOrder);
        } catch (CycleDetectedException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
