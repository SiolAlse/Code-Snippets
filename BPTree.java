import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Filename: Main.java Project: TeamProjectCS400 Authors: Rohit Potineni, Jacob
 * Schneider, Calvin Armstrong, Logan Crooks
 *
 * Semester: Fall 2018 Course: CS400 Lecture: 001
 * 
 * Due Date: Before 10pm on November 30th Version: 1.0
 * 
 * Credits:
 * 
 * Bugs: No known bugs
 */

/**
 * Implementation of a B+ tree to allow efficient access to many different
 * indexes of a large data set. BPTree objects are created for each type of
 * index needed by the program. BPTrees provide an efficient range search as
 * compared to other types of data structures due to the ability to perform
 * log_m N lookups and linear in-order traversals of the data items.
 * 
 * @author sapan (sapan@cs.wisc.edu)
 *
 * @param <K> key - expect a string that is the type of id for each item
 * @param <V> value - expect a user-defined type that stores all data for a food
 *        item
 */

public class BPTree<K extends Comparable<K>, V> implements BPTreeADT<K, V> {

	// Root of the tree
	private Node root;

	// Branching factor is the number of children nodes
	// for internal nodes of the tree
	private int branchingFactor;

	/**
	 * Public constructor
	 * 
	 * @param branchingFactor
	 */
	public BPTree(int branchingFactor) {
		// Throws IllegalArgumentException if the branching factor is too low
		if (branchingFactor <= 2) {
			throw new IllegalArgumentException("Illegal branching factor: " + branchingFactor);
		}
		// Initializes the branching factor and root values to creat an empty tree
		this.branchingFactor = branchingFactor;
		root = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see BPTreeADT#insert(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void insert(K key, V value) {
		// Will not allow insertion of null keys, but won't throw an exception
		if (key == null) {
			return;
		}
		// If the root is equal to null, make it a LeafNode and insert the key, value
		// pair
		if (root == null) {
			root = new LeafNode();
			root.insert(key, value);
			return;
		}
		// Insert the key, value pair into a non-null root
		root.insert(key, value);
		// Splits the root if it's needed, and sets the returned value to the root
		if (root.isOverflow()) {
			root = root.split();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see BPTreeADT#rangeSearch(java.lang.Object, java.lang.String)
	 */
	@Override
	public List<V> rangeSearch(K key, String comparator) {
		if (root == null) {
			return new ArrayList<V>();
		}
		// Calls rangeSearch as long as a valid comparator is used, otherwise returns an
		// empty list

		if (comparator.equals(">=") || comparator.equals("==") || comparator.equals("<=")) {
			return root.rangeSearch(key, comparator);
		}
		return new ArrayList<V>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		Queue<List<Node>> queue = new LinkedList<List<Node>>();
		queue.add(Arrays.asList(root));
		StringBuilder sb = new StringBuilder();
		while (!queue.isEmpty()) {
			Queue<List<Node>> nextQueue = new LinkedList<List<Node>>();
			while (!queue.isEmpty()) {
				List<Node> nodes = queue.remove();
				sb.append('{');
				Iterator<Node> it = nodes.iterator();
				while (it.hasNext()) {
					Node node = it.next();
					sb.append(node.toString());
					if (it.hasNext())
						sb.append(", ");
					if (node instanceof BPTree.InternalNode)
						nextQueue.add(((InternalNode) node).children);
				}
				sb.append('}');
				if (!queue.isEmpty())
					sb.append(", ");
				else {
					sb.append('\n');
				}
			}
			queue = nextQueue;
		}
		return sb.toString();
	}

	/**
	 * This abstract class represents any type of node in the tree This class is a
	 * super class of the LeafNode and InternalNode types.
	 * 
	 * @author sapan
	 */
	private abstract class Node {

		// List of keys
		List<K> keys;

		/**
		 * Package constructor
		 */
		Node() {
			// Initializes empty ArrayList to hold keys
			keys = new ArrayList<K>();
		}

		/**
		 * Inserts key and value in the appropriate leaf node and balances the tree if
		 * required by splitting
		 * 
		 * @param key
		 * @param value
		 */
		abstract void insert(K key, V value);

		/**
		 * Gets the first leaf key of the tree
		 * 
		 * @return key
		 */
		abstract K getFirstLeafKey();

		/**
		 * Gets the new sibling created after splitting the node
		 * 
		 * @return Node
		 */
		abstract Node split();

		/*
		 * (non-Javadoc)
		 * 
		 * @see BPTree#rangeSearch(java.lang.Object, java.lang.String)
		 */
		abstract List<V> rangeSearch(K key, String comparator);

		/**
		 * 
		 * @return boolean
		 */
		abstract boolean isOverflow();

		public String toString() {
			return keys.toString();
		}

	} // End of abstract class Node

	/**
	 * This class represents an internal node of the tree. This class is a concrete
	 * sub class of the abstract Node class and provides implementation of the
	 * operations required for internal (non-leaf) nodes.
	 * 
	 * @author sapan
	 */
	private class InternalNode extends Node {

		// List of children nodes
		List<Node> children;

		/**
		 * Package constructor
		 */
		InternalNode() {
			// Initializes the keys array through the Node class
			super();
			// Initializes the children to an empty ArrayList
			children = new ArrayList<Node>();
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see BPTree.Node#getFirstLeafKey()
		 */
		K getFirstLeafKey() {
			if (keys.size() == 0) {
				// First key in this subtree
				return children.get(0).getFirstLeafKey();
			}
			// returns null if there are no values in the list
			return null;
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see BPTree.Node#isOverflow()
		 */
		boolean isOverflow() {
			// Checks if the number of keys is equal to the branching factor or larger
			if (keys.size() > branchingFactor - 1) {
				return true;
			}
			return false;
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see BPTree.Node#insert(java.lang.Comparable, java.lang.Object)
		 */
		@SuppressWarnings("unchecked")
		void insert(K key, V value) {
			InternalNode tempNode; // Holds what is returned by splitting nodes
			// Loops through the keys to determine which child should take the new node
			for (int i = 0; i < keys.size(); ++i) {
				// If the key being inserted is less than the current key, it should be inserted
				// where the current key is
				if (key.compareTo(keys.get(i)) <= 0) {
					children.get(i).insert(key, value);
					// If the child is too big, split the child
					if (children.get(i).isOverflow()) {
						tempNode = (InternalNode) children.get(i).split();
						keys.add(i, tempNode.keys.get(0));
						// Makes sure the split node isn't lost
						children.add(i + 1, tempNode.children.get(1));
						children.set(i, tempNode.children.get(0));
					}
					return;
				}
			}
			// If the key being inserted is larger than all the other keys, insert at the
			// end
			children.get(children.size() - 1).insert(key, value);
			// Same idea as what happens in the loop
			if (children.get(children.size() - 1).isOverflow()) {
				tempNode = (InternalNode) children.get(children.size() - 1).split();
				keys.add(tempNode.keys.get(0));
				children.add(tempNode.children.get(1));
			}
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see BPTree.Node#split()
		 */
		Node split() {
			// Get middle key and set to key for new node
			// Create two child nodes each containing the keys and children of the keys on
			// either side of the key being moved up
			InternalNode child1 = new InternalNode();
			InternalNode toRet = new InternalNode();
			InternalNode child2 = new InternalNode();

			// Loops through all the keys adding them to one child or the other
			// Or filling the return node
			for (int i = 0; i < keys.size(); ++i) {
				if (i < keys.size() / 2) {
					child1.keys.add(keys.get(i));
					child1.children.add(children.get(i));
				} else if (i == keys.size() / 2) {
					toRet.keys.add(keys.get(i));
					child1.children.add(children.get(i));
				} else {
					child2.keys.add(keys.get(i));
					child2.children.add(children.get(i));
				}
			}
			// Adds the last child if it's needed
			if (children.size() > keys.size()) {
				child2.children.add(children.get(children.size() - 1));
			}
			// Sets the children to be the children of the returned node
			toRet.children.add(child1);
			toRet.children.add(child2);
			return toRet;
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see BPTree.Node#rangeSearch(java.lang.Comparable, java.lang.String)
		 */
		List<V> rangeSearch(K key, String comparator) {
			// The list of values being returned by this method
			List<V> toRet = new ArrayList<V>();
			// Gets values for <= because it iterates forward from the begining instead of
			// middle
			if (comparator.equals("<=")) {
				toRet = children.get(0).rangeSearch(key, comparator);
				return toRet;
			}
			// Loops through the children looking for where the key would be placed
			for (int i = 0; i < keys.size(); ++i) {
				if (key.compareTo(keys.get(i)) <= 0) {
					// List is returned back up by leaf node rangeSearch
					toRet = children.get(i).rangeSearch(key, comparator);
					return toRet;
				}
			}

			// Calls on the last child if it hasn't already found where the node belongs
			toRet = children.get(children.size() - 1).rangeSearch(key, comparator);
			return toRet;
		}

	} // End of class InternalNode

	/**
	 * This class represents a leaf node of the tree. This class is a concrete sub
	 * class of the abstract Node class and provides implementation of the
	 * operations that required for leaf nodes.
	 * 
	 * @author sapan
	 */
	private class LeafNode extends Node {

		// List of values
		List<V> values;

		// Reference to the next leaf node
		LeafNode next;

		// Reference to the previous leaf node
		LeafNode previous;

		/**
		 * Package constructor
		 */
		LeafNode() {
			// Calls the super constructor and initializes the values to an empty list
			super();
			values = new ArrayList<V>();
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see BPTree.Node#getFirstLeafKey()
		 */
		K getFirstLeafKey() {
			if (values.size() == 0) {
				// First key in this subtree
				return keys.get(0);
			}
			// returns null if there are no values in the list
			return null;
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see BPTree.Node#isOverflow()
		 */
		boolean isOverflow() {
			// Returns true if the number of keys and the reference to the next node is
			// larger than the
			// branching factor
			if (keys.size() > branchingFactor - 1) {
				return true;
			}
			return false;
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see BPTree.Node#insert(Comparable, Object)
		 */
		void insert(K key, V value) {
			// Loops through all the keys in the node
			for (int i = 0; i < keys.size(); ++i) {
				// If the current key is bigger than the key being inserted, insert the current
				// key at i
				if (keys.get(i).compareTo(key) >= 0) {
					keys.add(i, key);
					values.add(i, value);
					return;
				}
			}
			// Adds the value and key to the end of the list if they are bigger than all the
			// other keys
			values.add(value);
			keys.add(key);
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see BPTree.Node#split()
		 */
		Node split() {
			// The internal node created by the split and returned by the method
			InternalNode toRet = new InternalNode();
			// The new leaf node created by the split
			LeafNode newLeaf = new LeafNode();
			// Adds the middle key of the leaf node to the new internal node
			toRet.keys.add(keys.get(keys.size() / 2));
			// Adds all keys at the middle or higher to the new leaf and removes them
			// from the current leaf
			for (int i = values.size() / 2; i < values.size(); ++i) {
				newLeaf.values.add(values.get(i));
				newLeaf.keys.add(keys.get(i));
				values.remove(i);
				keys.remove(i);
				--i;
			}

			// Adds both this leaf node and the new leaf node to the internal node children
			toRet.children.add(this);
			toRet.children.add(newLeaf);

			// Sets next and previous values of this leaf node and the new leaf node
			newLeaf.next = next;
			if (next != null) {
				next.previous = newLeaf;
			}
			next = newLeaf;
			newLeaf.previous = this;

			return toRet;
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see BPTree.Node#rangeSearch(Comparable, String)
		 */
		List<V> rangeSearch(K key, String comparator) {
			// The list containing all values of the specified key and comparator
			List<V> toRet = new ArrayList<V>();
			// The current node of the iteration
			LeafNode curNode = this;
			// Checks which comparator is used and determines values appropriately
			if (comparator.equals("==")) {
				// Loops through nodes and values in nodes until it finds a key that is smaller
				// than the
				// key value. Adds each value it iterates over to the return value
				for (int i = 0; i < curNode.values.size(); ++i) {
					if (curNode.keys.get(i).equals(key) && i < curNode.values.size() - 1) {
						toRet.add(curNode.values.get(i));
					} else if (curNode.next != null && curNode.values.size() - 1 == i) { // Goes to next nod
						// When next node isn't null
						if (curNode.keys.get(i).equals(key)) {
							toRet.add(curNode.values.get(i));
						}
						curNode = curNode.next;
						i = -1;
					} else if (curNode.next == null) {
						if (curNode.keys.get(i).compareTo(key) >= 0) {
							toRet.add(curNode.values.get(i));
						}
						break;
					}
				}
				return toRet;

			} else if (comparator.equals("<=")) {
				// Loops through nodes and values in nodes until it finds a key that is smaller
				// than the
				// key value. Adds each value it iterates over to the return value
				for (int i = 0; i < curNode.values.size() && curNode.keys.get(i).compareTo(key) <= 0; ++i) {
					if (i < curNode.values.size() - 1) {
						toRet.add(curNode.values.get(i));
					} else if (curNode.next != null) {
						toRet.add(curNode.values.get(i));
						// Sets the current node to the next node when the last value is reached
						curNode = curNode.next;
						// Resets counter when reaching the end of the current node
						i = -1;
					} else {
						toRet.add(curNode.values.get(i));
						break;
					}
				}
				return toRet;

			} else if (comparator.equals(">=")) {
				// Loops through all values at given node and adds proper keys to the return
				// list, goes
				// to next node as needed
				for (int i = 0; i < curNode.values.size(); ++i) {
					if (curNode.keys.get(i).compareTo(key) >= 0 && i < curNode.values.size() - 1) {
						toRet.add(curNode.values.get(i));
					} else if (curNode.next != null && curNode.values.size() - 1 == i) { // Goes to next nod
						// When next node isn't null
						if (curNode.keys.get(i).compareTo(key) >= 0) {
							toRet.add(curNode.values.get(i));
						}
						curNode = curNode.next;
						i = -1;
					} else if (curNode.next == null) {
						if (curNode.keys.get(i).compareTo(key) >= 0) {
							toRet.add(curNode.values.get(i));
						} if(i == values.size() - 1) {
							break;
						}
					}
				}
				return toRet;
			}
			return toRet;
		}

	} // End of class LeafNode

	/**
	 * Contains a basic test scenario for a BPTree instance. It shows a simple
	 * example of the use of this class and its related types.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// create empty BPTree with branching factor of 3
		BPTree<Integer, Integer> bpTree = new BPTree<>(5);
		ArrayList<Integer> check = new ArrayList<Integer>();
		int[] hold = new int[50000];
		int toAdd = 0;
		for (int i = 0; i < 50000; ++i) {
			toAdd = (int) (10000 * Math.random() + 1);
			bpTree.insert(toAdd, toAdd);
			hold[i] = toAdd;
		}
		List<Integer> out = new ArrayList<Integer>();
		out = bpTree.rangeSearch(0, ">=");
		ArrayList<Integer> fin = new ArrayList<Integer>();
		ArrayList<Integer> diff = new ArrayList<Integer>();
		Arrays.sort(hold);
		for (int i = 0; i < out.size(); ++i) {
			if (out.get(i) != hold[i]) {
				System.out.println("Expected: " + out.get(i));
				System.out.println("Found: " + hold[i]);
				diff.add(out.get(i));
			}
		}
		
		BPTree<Integer, Integer> bp2 = new BPTree<>(5);
		for(int i = 0; i < 50000; ++i) {
			bp2.insert(toAdd, toAdd);
		}
		
		out = bp2.rangeSearch(0, ">=");
		System.out.println(out.size());
		
		BPTree<Integer, Integer> bp3 = new BPTree<>(5);
		
		bp3.insert(2, 2);
		bp3.insert(2, 2);
		bp3.insert(2, 2);
		bp3.insert(2, 2);
		bp3.insert(2, 2);
		bp3.insert(2, 2);
		bp3.insert(10, 10);
		bp3.insert(17, 17);
		bp3.insert(19, 19);
		
		System.out.println(bp3.rangeSearch(5, ">="));
		
		bp3 = new BPTree<>(5);
		bp3.insert(0, 0);
		bp3.insert(18, 18);
		System.out.println(bp3.rangeSearch(0, "=="));
		
		BPTree<String, Integer> bp4 = new BPTree<>(5);
		bp4.insert("0", 0);
		bp4.insert("18", 18);
		System.out.println(bp3.rangeSearch(5, "<="));
		
		bp3 = new BPTree<>(5);
		bp3.insert(5, 5);
		System.out.println(bp3.rangeSearch(0, ">="));
	}

} // End of class BPTree
