class PackageInstaller:
    def __init__(self):
        self.map = defaultdict(list)

    def addPackage(self, package, dependencies):
        self.map[package].append(dependencies)
        
        
    def installPackages(self):
        indegree = {}
        for key, value in self.map.items():
            if key in indegree:
                indegree[key]+=1
            else:
                indegree[key] = 0
            for d in values:
                indegree[d]+=1
        
        numOfpackages = len(indegree)
        q = deque()
        orderOfPackages = []

        for key, value in indegree.items():
            if value == 0:
                q.append(key)
                del indegree[package]
        
        while q:
            package = q.popleft()
            orderOfPackages.append(package)
            for dependency in self.map[package]:
                indegree[dependency]-=1
                if indegree == 0:
                    q.append(package)
                    del indegree[package]
        
        if len(orderOfPackages)!= numOfpackages:
            return "Cyclic Dependency cannot complete installation"
        return orderOfPackages
        
        
