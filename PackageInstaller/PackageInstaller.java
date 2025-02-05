package PackageInstaller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Package Class
class Package {
    String name;
    String version;
    List<Package> dependencies;
    boolean isInstalled;

    // Constructor
    public Package(String name, String version) {
        this.name = name;
        this.version = version;
        this.dependencies = new ArrayList<>();
        this.isInstalled = false;
    }

    // Add dependency to the package
    public void addDependency(Package dependency) {
        dependencies.add(dependency);
    }

    // Install the package, installing dependencies first
    public boolean install() {
        if (isInstalled) {
            System.out.println(name + " is already installed.");
            return false;
        }
        // Install dependencies first
        for (Package dep : dependencies) {
            if (!dep.isInstalled) {
                dep.install();
            }
        }
        // Install the package itself
        System.out.println("Installing " + name + " version " + version);
        isInstalled = true;
        return true;
    }

    // Uninstall the package
    public void uninstall() {
        if (!isInstalled) {
            System.out.println(name + " is not installed.");
            return;
        }
        System.out.println("Uninstalling " + name);
        isInstalled = false;
    }

    // Upgrade the package to a new version
    public boolean upgrade(String newVersion) {
        if (newVersion.equals(version)) {
            System.out.println(name + " is already at the latest version.");
            return false;
        }
        version = newVersion;
        System.out.println("Upgrading " + name + " to version " + newVersion);
        return true;
    }
}

// PackageManager Class
class PackageManager {
    Map<String, Package> installedPackages = new HashMap<>();

    // Install a package
    public void installPackage(Package pkg) {
        if (pkg.install()) {
            installedPackages.put(pkg.name, pkg);
        }
    }

    // Uninstall a package
    public void uninstallPackage(Package pkg) {
        pkg.uninstall();
        installedPackages.remove(pkg.name);
    }

    // Upgrade a package
    public void upgradePackage(Package pkg, String newVersion) {
        if (pkg.upgrade(newVersion)) {
            installedPackages.put(pkg.name, pkg);
        }
    }

    // List all installed packages
    public void listInstalledPackages() {
        if (installedPackages.isEmpty()) {
            System.out.println("No packages installed.");
            return;
        }
        System.out.println("Installed Packages:");
        for (Package pkg : installedPackages.values()) {
            System.out.println(pkg.name + " version " + pkg.version);
        }
    }
}

// Main class to test the Package Installer system
public class PackageInstaller {
    public static void main(String[] args) {
        // Create some sample packages
        Package packageA = new Package("PackageA", "1.0");
        Package packageB = new Package("PackageB", "1.0");
        Package packageC = new Package("PackageC", "2.0");

        // Adding dependencies
        packageB.addDependency(packageA);  // PackageB depends on PackageA
        packageC.addDependency(packageB);  // PackageC depends on PackageB

        // Create the package manager
        PackageManager pkgManager = new PackageManager();

        // Install package C (which will also install its dependencies: B and A)
        pkgManager.installPackage(packageC);

        // List installed packages
        pkgManager.listInstalledPackages();

        // Upgrade a package
        pkgManager.upgradePackage(packageA, "1.1");

        // Uninstall a package
        pkgManager.uninstallPackage(packageA);

        // List installed packages again
        pkgManager.listInstalledPackages();
    }
}
