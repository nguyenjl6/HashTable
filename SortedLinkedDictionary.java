import java.util.Iterator;
import java.util.NoSuchElementException;

/**
   A class that implements a dictionary by using a sorted linked chain.
*/
public class SortedLinkedDictionary<K extends Comparable<? super K>, V> {
	private Node firstNode; // Reference to first node of chain
	private int  numberOfEntries; 
	
	public SortedLinkedDictionary()	{
      initializeDataFields();
	} 
	
   public V add(K key, V value)	{
		V result = null;
		
      // Search chain until you locate where it should be
		Node currentNode = firstNode;
		Node nodeBefore = null;
		
		while ((currentNode != null) && (key.compareTo(currentNode.getKey()) > 0))	{
			nodeBefore = currentNode;
			currentNode = currentNode.getNextNode();
		} 
		
		 // Add new node in proper order
			// Assumes key and value are not null
			Node newNode = new Node(key, value); // Create new node
			
			if (nodeBefore == null)	{  
			// Add at beginning (includes empty dictionary)
				newNode.setNextNode(firstNode);
				firstNode = newNode;
			}
			else	{					          // Add elsewhere in non-empty list
				newNode.setNextNode(currentNode); // currentNode is after new node
				nodeBefore.setNextNode(newNode);  // nodeBefore is before new node
			} 
         
			numberOfEntries++;                   
      
		return result;
	} 
   
   public V remove(K key)	{
   	V result = null;  // Return value
   	
		if (!isEmpty())	{	
	      // Find node before the one that contains or could contain key
			Node currentNode = firstNode;
			Node nodeBefore = null;
			
			while ((currentNode != null) && 
			        (key.compareTo(currentNode.getKey()) > 0))	{
				nodeBefore = currentNode;
				currentNode = currentNode.getNextNode();
			} 
			
			if ((currentNode != null) && key.equals(currentNode.getKey()))	{
				Node nodeAfter = currentNode.getNextNode(); 

				if (nodeBefore == null)	{
					firstNode = nodeAfter;
				}
				else{
					nodeBefore.setNextNode(nodeAfter);      
				} 

				result = currentNode.getValue();            
			   numberOfEntries--;                          
			} 
		} 
			
      return result;  
   } 

   public V getValue(K key)  {
   	V result = null;

      // Find node before the one that contains or could contain key
		Node currentNode = firstNode;
		Node nodeBefore = null;
		
		while ((currentNode != null) && 
		        (key.compareTo(currentNode.getKey()) > 0))	{
			nodeBefore = currentNode;
			currentNode = currentNode.getNextNode();
		} 

		if ((currentNode != null) && key.equals(currentNode.getKey())) {
			result = currentNode.getValue();
		} 
		
		return result;
   } 

	public boolean contains(K key)   {
		return getValue(key) != null; 
   } 

   public boolean isEmpty()  {
      return numberOfEntries == 0;
   } 
	
   public int getSize()   {
      return numberOfEntries;
   } 

	public final void clear()	{ 
      initializeDataFields();
   } 

	public Iterator<K> getKeyIterator()	{
		return new KeyIterator();
	} 
	
	public Iterator<V> getValueIterator()	{
		return new ValueIterator();
	} 

   // Initializes the class's data fields to indicate an empty list.
   private void initializeDataFields()   {
		firstNode = null;
		numberOfEntries = 0;
   } 
	
// Same as in LinkedDictionary.
// Since iterators implement Iterator, methods must be public.
	private class KeyIterator implements Iterator<K> {
		private Node nextNode;
		
		private KeyIterator() {
			nextNode = firstNode;
		} 
		
		public boolean hasNext() {
			return nextNode != null;
		} 
		
		public K next()	{
			K result;
			
			if (hasNext())	{
				result = nextNode.getKey();
				nextNode = nextNode.getNextNode();
			}
			else{
				throw new NoSuchElementException();
			} 
		
			return result;
		} 
		
		public void remove() {
			throw new UnsupportedOperationException();
		} 
	} 
	
	private class ValueIterator implements Iterator<V>	{
		private Node nextNode;
		
		private ValueIterator()	{
			nextNode = firstNode;
		} 
		
		public boolean hasNext() {
			return nextNode != null;
		} 
		
		public V next()	{
			V result;
			
			if (hasNext())	{
				result = nextNode.getValue();
				nextNode = nextNode.getNextNode();
			}
			else	{
				throw new NoSuchElementException();
			} 
		
			return result;
		} 
		
		public void remove() {
			throw new java.lang.UnsupportedOperationException();
		} 
	} 

	private class Node	{
		private K key;
		private V value;
		private Node next;

		private Node(K searchKey, V dataValue)	{
			key = searchKey;
			value = dataValue;
			next = null;	
		} 
		
		private Node(K searchKey, V dataValue, Node nextNode)	{
			key = searchKey;
			value = dataValue;
			next = nextNode;	
		} 
		
		private K getKey()	{
			return key;
		} 
		
		private V getValue()	{
			return value;
		} 

		
		private void setValue(V newValue)	{
			value = newValue;
		} 

		private Node getNextNode()	{
			return next;
		} 
		
		private void setNextNode(Node nextNode)	{
			next = nextNode;
		} 
	}
} 