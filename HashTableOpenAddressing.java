import java.util.Iterator;
import java.util.NoSuchElementException;

public class HashTableOpenAddressing<K, V> implements DictionaryInterface<K, V> {
	private int numEntries;
	private static final int DEFAULT_CAPACITY = 5;
	private static final int MAX_CAPACITY = 10000;
	private TableEntry<K, V>[] table;
	private double loadFactor;
	private static final double DEFAULT_LOAD_FACTOR = 0.75;

	public HashTableOpenAddressing() {
		this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
	}

	public HashTableOpenAddressing(int initialCapacity, double loadFactorIn) {
		numEntries = 0;
		if (loadFactorIn <= 0 || initialCapacity <= 0) {
			throw new IllegalArgumentException("Initial capacity and load " +
					"factor must be greater than 0");
		}
		else if (initialCapacity > MAX_CAPACITY)
			throw new IllegalStateException("Attempt to create a dictionary " +
					"whose capacity is larger than " + MAX_CAPACITY);

		loadFactor = loadFactorIn;
		// Set up hash table:
		// Initial size of hash table is same as initialCapacity if it is prime;
		// otherwise increase it until it is prime size
		int tableSize = getNextPrime(initialCapacity);

		@SuppressWarnings("unchecked")
		TableEntry<K, V>[] temp = (TableEntry<K, V>[]) new TableEntry[tableSize];
		table = temp;
	}

	/** Task: Adds a new entry to the dictionary. If the given search
	 *        key already exists in the dictionary, replaces the 
	 *        corresponding value.
	 *  @param key    an object search key of the new entry
	 *  @param value  an object associated with the search key
	 *  @return either null if the new entry was added to the dictionary or the
	 *          value that was associated with key if that value was replaced */
	@Override
	public V add(K keyIn, V valueIn) {
		if (keyIn == null || valueIn == null) {
			throw new IllegalArgumentException();
		}
		V oldValue = null;
		int index = getHashIndex(keyIn);
		index = quadraticProbe(index, keyIn);
		if (table[index] == null || table[index].isRemoved()) {
			TableEntry newEntry = new TableEntry(keyIn, valueIn);
			table[index] = newEntry;
			numEntries++;
			
		}
		else {
			oldValue = (V) table[index];
			table[index].setValue(valueIn);
			if (numEntries > loadFactor * table.length) {
				enlargeHashTable();
			}
			
		}
		return oldValue;
		
	}

		private int linearProbe(int index, K keyIn) {
		boolean found = false;
		int removedStateIndex = -1; // Index of first removed location
		if(table[index] == null){ 
			return index;
		}
		while (!found && table[index] != null) {	
			if (table[index].isIn()) {
				if (keyIn.equals(table[index].getKey())) {
					found = true; 		// Key found
				}
				else{            		// Follow probe sequence
					index = (index + 1) % table.length;  
				}
			}
			else { 						// Skip entries that were removed
				// Save index of first location in removed state
				if (removedStateIndex == -1) {
					removedStateIndex = index;
				}
				index = (index + 1) % table.length;          
			} 
		} 
		if (found || (removedStateIndex == -1) ) {
			return index;                  // Index of either key or null
		}
		else {
			return removedStateIndex;      // Index of an available location
		}
	}

	 private int quadraticProbe(int index, K key) {
		 boolean found = false;
			int removedStateIndex = -1; // Index of first removed location
			int increment = 0;
			if(table[index] == null){ 
				return index;
			}
			while (!found && table[index] != null) {	
				if (table[index].isIn()) {
					if (key.equals(table[index].getKey())) {
						found = true; 		// Key found
					}
					else{            		// Follow probe sequence
						index = ((index + increment * increment) % table.length);
						increment++;
					}
				}
				else { 						// Skip entries that were removed
					// Save index of first location in removed state
					if (removedStateIndex == -1) {
						removedStateIndex = index;
					}
					index = (index + increment * increment) % table.length;  
					increment++;
				} 
			} 
			if (found || (removedStateIndex == -1) ) {
				return index;                  // Index of either key or null
			}
			else {
				return removedStateIndex;      // Index of an available location
			}
	 }

	private int getHashIndex(K key)	{
		int hashIndex = Math.abs(key.hashCode() % 10) % table.length;
		
		return hashIndex;
	}

	/** Task: Removes a specific entry from the dictionary.
	 *  @param key  an object search key of the entry to be removed
	 *  @return either the value that was associated with the search key 
	 *          or null if no such object exists */
	@Override
	public V remove(K key) {
		V removedValue = null;
		int index = getHashIndex(key);
		//index = linearProbe(index, key);
		index = quadraticProbe(index, key);

		if (index != -1){
			// Key found; flag entry as removed and return its value
			removedValue = table[index].getValue();
			table[index].setToRemoved();
			numEntries--;
		} 
		// Else not found; result is null
		return removedValue;
	} 

	/** Task: Retrieves the value associated with a given search key.
	 *  @param key  an object search key of the entry to be retrieved
	 *  @return either the value that is associated with the search key 
	 *          or null if no such object exists */
	@Override
	public V getValue(K key) {
		int index = getHashIndex(key);
		TableEntry item = table[index];
		if ((item != null) && item.isIn()) {
			return (V) item.getValue();
		}
		else {
			return null;
		}
	}

	/** Task: Sees whether a specific entry is in the dictionary.
	 *  @param key  an object search key of the desired entry
	 *  @return true if key is associated with an entry in the
	 *          dictionary */
	@Override
	public boolean contains(K keyIn) {
		return getValue(keyIn) != null;
	}

	/** Task: Creates an iterator that traverses all search keys in the 
	 *        dictionary.
	 *  @return an iterator that provides sequential access to the search 
	 *          keys in the dictionary */
	@Override
	public Iterator<K> getKeyIterator() {
		return new KeyIterator();
	}

	/** Task: Creates an iterator that traverses all values in the 
	 *        dictionary.
	 *  @return an iterator that provides sequential access to the values 
	 *          in the dictionary */
	@Override
	public Iterator<V> getValueIterator() {
		return new ValueIterator();
	}

	/** Task: Gets the size of the dictionary.
	 *  @return the number of entries (key-value pairs) currently
	 *          in the dictionary */
	@Override
	public int getSize() {
			return numEntries;
	}

	/** Task: Sees whether the dictionary is empty.
	 *  @return true if the dictionary is empty */
	@Override
	public boolean isEmpty() {
			if(numEntries == 0) {
				return true;
			}
		return false;
	}

	/** Task: Sees whether the dictionary is full.
	 *  @return true if the dictionary is full */
	@Override
	public boolean isFull() {
			if(numEntries > loadFactor) {
				return true;
			}
		return false;
	}

	/** Task: Removes all entries from the dictionary. */
	@Override
	public void clear() {
			for (int i = 0; i < table.length; i ++) {
				table[i] = null;
			}
			numEntries = 0;

	}

	private void enlargeHashTable() {
		TableEntry<K, V>[] oldTable = table;
		int capacity = getNextPrime(oldTable.length * 2);

		// The case is safe because the new array contains null entries
		@SuppressWarnings("unchecked")
		TableEntry<K, V>[] temp = (TableEntry<K, V>[]) new TableEntry[capacity];
		table = temp;
		numEntries = 0; 

		// Rehash dictionary entries from old array to the new 
		for (int index = 0; index < oldTable.length; index++) {
			if ((oldTable[index] != null) && oldTable[index].isIn()) {
				add(oldTable[index].getKey(), oldTable[index].getValue());  
			}
		} 
	} 

	// Returns a prime integer that is >= the given integer.
	private int getNextPrime(int integer) {
		// if even, add 1 to make odd
		if (integer % 2 == 0) {
			integer++;
		} 

		// test odd integers
		while (!isPrime(integer)) {
			integer = integer + 2;
		} 
		return integer;
	} 

	// Returns true if the given integer is prime.
	private boolean isPrime(int integer) {
		boolean result;
		boolean done = false;

		// 1 and even numbers are not prime
		if ((integer == 1) || (integer % 2 == 0)) {
			result = false; 
		}
		// 2 and 3 are prime
		else if ((integer == 2) || (integer == 3)) {
			result = true;
		}
		//TODO 
		else  {				// integer is odd and >= 5
			result = true; 	// assume prime
			for (int divisor = 3; !done && (divisor * divisor <= integer); 																	divisor = divisor + 2) {
				if (integer % divisor == 0) {
					result = false; // divisible; not prime
					done = true;
				} 
			} 
		} 
		return result;
	}

	public String toString() {
		String result = "";
		for(int i = 0; i < table.length; i++) {
			result += i + " ";
			if(table[i] == null)
				result += "null\n";
			else{
				if(table[i].isRemoved() )
					result += "has been set to \"removed\"\n";
				else
					result += table[i].getKey() + " " + table[i].getValue() + "\n";
			}
		}
		return result;
	}

	//****************************KeyIterator**************************
	private class KeyIterator implements Iterator<K>{
		private int currentIndex; // Current position in hash table
		private int numberLeft;   // Number of entries left in iteration

		private KeyIterator() 	{
			currentIndex = 0;
			numberLeft = numEntries;
		} 

		public boolean hasNext() {
			return numberLeft > 0;
		} 

		public K next()	{
			K result = null;

			if (hasNext()) {
				// Skip table locations that do not contain a current entry
				while ((table[currentIndex] == null) || table[currentIndex].isRemoved()){
					currentIndex++;
				} 
				result = table[currentIndex].getKey();
				numberLeft--;
				currentIndex++;
			}
			else {
				throw new NoSuchElementException();
			}

			return result;
		} 

		public void remove() {
			throw new UnsupportedOperationException();
		} 
	}  

	//****************************ValueIterator**************************
	private class ValueIterator implements Iterator<V> {
		private int currentIndex; // Current position in hash table
		private int numberLeft;   // Number of entries left in iteration
		
		private ValueIterator() 	{
			currentIndex = 0;
			numberLeft = numEntries;
		} 
		
		@Override
		public boolean hasNext() {
			return numberLeft > 0;
			
		}

		@Override
		public V next() {
			V result = null;

			if (hasNext()) {
				// Skip table locations that do not contain a current entry
				while ((table[currentIndex] == null) || table[currentIndex].isRemoved()){
					currentIndex++;
				} 
				result = table[currentIndex].getValue();
				numberLeft--;
				currentIndex++;
			}
			else {
				throw new NoSuchElementException();
			}

			return result;
		}
		public void remove() {
			throw new UnsupportedOperationException();
		} 
	} 
	
	//****************************TableEntry**************************
	public static class TableEntry<K, V> {
		private K key;
		private V value;
		private States state;    // Flags whether this entry is in the hash table
		private enum States {CURRENT, REMOVED} // Possible values of state

		public TableEntry(K key, V value) {
			this.key = key;
			this.value = value;
			state = States.CURRENT;
		}

		private K getKey() {
			return key;
		} 

		private V getValue()	{
			return value;
		} 

		private void setValue(V newValue)	{
			value = newValue;
		} 

		// Returns true if this entry is currently in the hash table.
		private boolean isIn() {
			if(state == States.CURRENT) {
				return true;
			}
		
			return false;
		} 

		// Returns true if this entry has been removed from the hash table.
		private boolean isRemoved()	{
			if(state == States.REMOVED) {
				return true;
			}
			return false;
		} 

		// Sets the state of this entry to removed.
		private void setToRemoved()	{
			state = States.REMOVED;
		}

		public String toString() {
			return "Key-" + key + ": Value-" + value;
		}
	}
	
	public static void main(String[] args){
		HashTableOpenAddressing<String, Integer> purchases = new HashTableOpenAddressing<String, Integer>(27, 0.75);

		String names[] = {"Pax", "Eleven", "Angel", "Abigail", "Jack"};
		purchases.add(names[0], 654);
		purchases.add(names[1], 341);
		purchases.add(names[2], 70);
		purchases.add(names[3], 867);
		purchases.add(names[4], 5309);
		// if("linear".equals(args[0])) {
		// 		System.out.println("Contents with linear probing:\n" + purchases);
		// }
		// else {
		// 	System.out.println("Contents with quadratic probing:\n" + purchases);
		// }
		System.out.println("Replaced: the old value was " + purchases.add(names[1], 170));
		System.out.println("Contents after changing Eleven to 170:\n" + purchases);

		System.out.println("Calling getValue() on Pax, Eleven, & Angel:");
		System.out.println("\tPax: " + purchases.getValue(names[0]));
		System.out.println("\tEleven: " + purchases.getValue(names[1]));
		System.out.println("\tAngel: " + purchases.getValue(names[2]));

		purchases.remove(names[0]);
		purchases.remove(names[2]);
		System.out.println("Contents after calling remove on Pax & Angel:\n" + purchases);

		purchases.add("Gino", 348);
		System.out.println("Contents after adding Gino:\n" + purchases);

		Iterator<String> keyIter = purchases.getKeyIterator();
		Iterator<Integer> valueIter = purchases.getValueIterator();
		System.out.println("Contents of the hash table:");
		while(keyIter.hasNext()) {
			System.out.println("Key-" + keyIter.next() + " : Value-" + valueIter.next());
		}
	}
}
