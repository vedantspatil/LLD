from abc import ABC, abstractmethod
from collections import deque

class FileNode:
    def __init__(self, name, isDirectory, size=0):
        self.name = name
        self.isDirectory = isDirectory
        self.parentPath = ""
        self.path = self.parentPath+"/"+self.name+""

    def setFilePath(self,path):
        self.parentPath = path
        self.path = self.parentPath+"/"+self.name+""
    def getFilePath(self):
        return self.path

class Directory(FileNode):
    def __init__(self, name):
        super().__init__(name, True, 0)
        self.children = []

    def addSubDirectory(self, *subdirectories):
        self.children.extend(subdirectories)
        self.updatePaths(subdirectories)

    def updatePaths(self, subdirectories):
        for dir in subdirectories:
            dir.setFilePath(self.path)
            if dir.isDirectory:
                dir.updatePaths(dir.children)

class File(FileNode):
    def __init__(self, name, size=0):
        super().__init__(name, False, size)
        self.size = size
    
class Constraints(ABC):
    @abstractmethod
    def isSatisfied(self, file: File)->bool:
        pass

class NameConstraint(Constraints):
    def __init__(self, name):
        self.name = name
    
    def isSatisfied(self, file: File) -> bool:
        return self.name == file.name and not file.isDirectory


class SizeConstraint(Constraints):
    def __init__(self, minSize, maxSize):
        self.maxSize = maxSize
        self.minSize = minSize
    
    def isSatisfied(self, file: File) -> bool:
        return not file.isDirectory and self.minSize<=file.size<=self.maxSize


class ExtensionConstraint(Constraints):
    def __init__(self, extension):
        self.extension = extension
    
    def isSatisfied(self, file: File) -> bool:
        return file.name.endswith(self.extension) and not file.isDirectory


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
            if all(constraint.isSatisfied(currNode) for constraint in constraints):
                matches.append((currNode.path, currNode.name, currNode.size))
            if currNode.isDirectory:
                queue.extend(currNode.children)
        return matches

if __name__ == "__main__": 
    rootDirectory = Directory("root")
    file1 = File("file1.txt", 500)
    file2 = File("file2.mp4", 200)
    file3 = File("file3.json", 300)
    dir1 = Directory("dir1")
    dir2 = Directory("dir2")
    dir3 = Directory("dir3")
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
    print(searchApi.search(rootDirectory, c1))
