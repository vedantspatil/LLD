from abc import ABC, abstractmethod
from collections import deque

class File:
    def __init__(self, name, isDirectory,  parentPath="", size=0):
        self.name = name
        self.size = size
        self.isDirectory = isDirectory
        self.children = []
        self.parentPath = parentPath
        self.filePath = self.parentPath+"/"+self.name+""
    
    def setFilePath(self,path):
        self.parentPath = path
        self.filePath = self.parentPath+"/"+self.name+""
    
    def getFilePath(self):
        return self.filePath

    def addSubDirectory(self, *subdirectories):
        self.children.extend(subdirectories)
        self.updatePaths()

    def updatePaths(self):
        for dir in self.children:
            dir.setFilePath(self.filePath)
            dir.updatePaths()
    
class Constraints(ABC):
    @abstractmethod
    def isSatisfied(self, file: File)->bool:
        pass

class NameConstraint(Constraints):
    def __init__(self, name):
        self.name = name
    
    def isSatisfied(self, file: File) -> bool:
        return self.name == file.name


class SizeConstraint(Constraints):
    def __init__(self, minSize, maxSize):
        self.maxSize = maxSize
        self.minSize = minSize
    
    def isSatisfied(self, file: File) -> bool:
        return self.minSize<=file.size<=self.maxSize


class ExtensionConstraint(Constraints):
    def __init__(self, extension):
        self.extension = extension
    
    def isSatisfied(self, file: File) -> bool:
        return file.name.endswith(self.extension)


class AndConstraint(Constraints):
    def __init__(self, *constraints):
        self.constraints = [constraint for constraint in constraints]
    
    def isSatisfied(self, file: File) -> bool:
        return all(constraint.isSatisfied(file) for constraint in self.constraints)

class OrConstraint(Constraints):
    def __init__(self, *constraints):
        self.constraints = [constraint for constraint in constraints]
    
    def isSatisfied(self, file: File) -> bool:
        return any(constraint.isSatisfied(file) for constraint in self.constraints)



class Search:
    # def __init__(self):
    #     queue  =  deque()

    def search(self, rootNode, *constraints):
        queue  =  deque()
        queue.append(rootNode)
        matches = []
        while queue:
            currNode = queue.popleft()
            # print(currNode.name)

            if all(constraint.isSatisfied(currNode) for constraint in constraints) and not currNode.isDirectory:
                matches.append((currNode.parentPath, currNode.name, currNode.size))
            if currNode.isDirectory:
                queue.extend(currNode.children)
        return matches

if __name__ == "__main__": 
    rootDirectory = File("root", True)
    file1 = File("file1.txt", False,"", 500)
    file2 = File("file2.mp4", False,"", 200)
    file3 = File("file3.json", False,"", 300)
    dir1 = File("dir1", True)
    dir2 = File("dir2", True)
    dir3 = File("dir3", True)
    rootDirectory.addSubDirectory(file1)
    dir2.addSubDirectory(file2)
    dir3.addSubDirectory(file3)
    dir1.addSubDirectory(dir2, dir3)
    rootDirectory.addSubDirectory(dir1)

    searchApi = Search()

    c1 = NameConstraint("file1.txt")
    c2 = SizeConstraint(0, 500)
    c3 = ExtensionConstraint("mp4")
    c4 = OrConstraint(c2, c3)
    print(searchApi.search(rootDirectory, c4))
