# Gitlet Design Document

**Name**: Srikar Talluri

## Classes and Data Structures

###Node
This class stores a single file and a unique ID

**Fields**
1. `File nodeFile`: the File that the current Node consists of
2. `int id`: an integer of the hashCode

###Branch
This class represents a series of Nodes with a given splitPoint

**Fields**
1. `Node splitPoint`: the point at which this branch was created. Will be null for the master branch

###Directory

**Fields**
1. `Branch master`: the master branch


## Algorithms

###Node Class

1. Node(File file): constructor for the Node Class. Makes a unique id for the class.
2. merge (Node other): merges this Node with other
###NDirectory

1. merge(Directory other) merges this directory with other. Uses the merge method from the Node class

## Persistence
In order to persist the previous commits and changes to each
branch, we would need to save not only the previous commit
timestamps and messages, but also the previous files.