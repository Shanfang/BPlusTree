JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	treesearch.java \
	Node.java \
	LeafNode.java \
	IndexNode.java \
	BPlusTree.java 

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
