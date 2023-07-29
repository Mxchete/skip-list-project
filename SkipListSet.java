import java.util.*;

// ___________________________________________________
// class: SkipListSet
// purpose: used to implement a skip list set in Java
// SortedSet Documentation:
// https://docs.oracle.com/javase/8/docs/api/java/util/SortedSet.html
public class SkipListSet<T extends Comparable<T>> implements SortedSet<T> {

    // private variables used by the skip list
    private int height = -1;
    private int size = -1;
    private SkipListSetPayloadWrapper<T> root = null;

    // ____________________________________________
    // Constructor: SkipListSet
    // Parameters: none
    // Purpose: initialize a new skip list set with no items
    public SkipListSet() {
        root = new SkipListSetPayloadWrapper<T>(null);
        // height initialized to 8
        height = 8;
        size = 0;
    }

    // ____________________________________________
    // Constructor: SkipListSet
    // Parameters: collection of objects to add to set
    // Purpose: initialize a new skip list set with a given collection of items
    public SkipListSet(Collection<? extends T> objectCollection) {
        root = new SkipListSetPayloadWrapper<T>(null);
        height = 8;
        size = 0;
        addAll(objectCollection);
    }

    // ____________________________________________
    // Function: add
    // Parameters: generic object to add
    // Purpose: add an item to the list
    // Returns: boolean true if successful at adding, false otherwise
    // Documented Anomolies: none
    public boolean add(T object) {

        // case where list is empty
        if (isEmpty()) {
            // create object at root and set all links to null
            root = new SkipListSetPayloadWrapper<T>(object);
            for (int i = 0; i < height; i++) {
                root.setLinks(null, null);
            }
            size++;
            return true;
        }

        // case where object already exists in the set, return false
        if (contains(object)) {
            return false;
        }

        // case where a new item should be inserted before the root
        if (root.payload.compareTo(object) >= 0) {
            // save old root value, replace root value with new item, and re-add old root
            T oldRoot = root.payload;
            root.payload = object;
            return add(oldRoot);
        }

        // if size does not change after adding the object, return false
        int oldSize = size;
        search(object, true);
        if (oldSize == size) {
            return false;
        }
        // if size has increased far enough from height, call raiseHeight
        if (size > Math.pow(2, height)) {
            raiseHeight();
        }
        return true;

    }

    // ____________________________________________
    // Function: addAll
    // Parameters: collection of objects to add
    // Purpose: add all objects in a collection to the list
    // Returns: boolean true if list was changed, false otherwise
    // Documented Anomolies: none
    public boolean addAll(Collection<? extends T> objectCollection) {
        boolean returnValue = false;
        // foreach loop to add all objects
        for (T object : objectCollection) {
            // return value is result of combinational logic between each add
            // a single add returning true will set it to true
            returnValue = returnValue | add(object);
        }
        return returnValue;
    }

    // ____________________________________________
    // Function: clear
    // Parameters: none
    // Purpose: remove all items from the list
    // Returns: nothing
    // Documented Anomolies: none
    public void clear() {
        root = null;
        // root should hold all references, nullifying it and calling gc should free up
        // all memory
        System.gc();
        root = new SkipListSetPayloadWrapper<T>(null);
    }

    // Comparator is allowed to just return null according to project documentation
    public Comparator<T> comparator() {
        return null;
    }

    // ____________________________________________
    // Function: contains
    // Parameters: object to check
    // Purpose: check if an object exists in the set
    // Returns: boolean true if object exists, false otherwise
    // Documented Anomolies: none
    @SuppressWarnings("unchecked")
    public boolean contains(Object object) {
        return !isEmpty() && search((T) object, false).payload.compareTo((T) object) == 0;
    }

    // ____________________________________________
    // Function: containsAll
    // Parameters: collection of objects to check
    // Purpose: check if all objects exist in the set
    // Returns: boolean returns true iff all objects are in set
    // Documented Anomolies: none
    public boolean containsAll(Collection<?> objectCollection) {
        boolean returnValue = true;
        for (Object object : objectCollection) {
            // if returnvalue is ever false, break as it can never be true
            if (!returnValue)
                break;
            returnValue = returnValue & contains(object);
        }
        return returnValue;
    }

    // ____________________________________________
    // Function: equals
    // Parameters: collection of objects
    // Purpose: find if a collection is equal to the set
    // Returns: boolean true iff the given collection is equal
    // Documented Anomolies: none
    public boolean equals(Collection<T> objectCollection) {
        return objectCollection.size() == size && containsAll(objectCollection);
    }

    // ____________________________________________
    // Function: first
    // Parameters: none
    // Purpose: return the payload item of the root
    // Returns: root payload if it exists
    // Documented Anomolies: none
    public T first() {
        return (root != null) ? root.payload : null;
    }

    // ____________________________________________
    // Function: hashCode
    // Parameters: none
    // Purpose: get hash code value of the whole set
    // Returns: integer hashcode value
    // Documented Anomolies: none
    public int hashCode() {
        // iterator for list
        Iterator<T> hashIterator = iterator();
        int result = 0;
        while (hashIterator.hasNext()) {
            // for each item in list, get hash code and add it to the result
            result = result + Objects.hashCode(hashIterator.next());
        }
        return result;
    }

    // headset is allowed to throw an UnsupportedOperationException according to
    // project documentation
    public SkipListSet<T> headSet(T object) {
        throw new UnsupportedOperationException();
    }

    // ____________________________________________
    // Function: isEmpty
    // Parameters: none
    // Purpose: return whether or not the set is empty
    // Returns: boolean true iff list is empty
    // Documented Anomolies: none
    public boolean isEmpty() {
        return (root == null || root.payload == null);
    }

    // ____________________________________________
    // Function: iterator
    // Parameters: none
    // Purpose: initialize a new SkipListSetIterator object at the root
    // Returns: SkipListSetIterator object at root
    // Documented Anomolies: none
    public SkipListSetIterator<T> iterator() {
        return new SkipListSetIterator<T>(root);
    }

    // ____________________________________________
    // Function: last
    // Parameters: none
    // Purpose: find the last item in the list
    // Returns: payload of last item wrapper in the list
    // Documented Anomolies: none
    public T last() {
        SkipListSetPayloadWrapper<T> current = root;
        int srchHeight = height - 1;
        // search through list until node to the right is null & on lowest level
        while (current.links.get(srchHeight).right != null || srchHeight != 0) {
            if (current.links.get(srchHeight).right == null) {
                srchHeight--;
            } else {
                current = current.links.get(srchHeight).right;
            }
        }
        return current.payload;
    }

    // ____________________________________________
    // Function: remove
    // Parameters: object to remove from the set
    // Purpose: remove a given object from the list
    // Returns: boolean true iff object was successfully removed
    // Documented Anomolies: none
    @SuppressWarnings("unchecked")
    public boolean remove(Object object) {
        if (isEmpty())
            return false;
        // run search to find node to remove
        SkipListSetPayloadWrapper<T> removeMe = search((T) object, false);
        // if node was not found that contains the object, return false
        if (removeMe.payload.compareTo((T) object) != 0)
            return false;
        // case where object to remove is in root node
        if (removeMe.payload.compareTo(root.payload) == 0) {
            if (size == 1) {
                // if list is one node, nullify root
                root = null;
            } else {
                // if list has more than one node, remove value to the right of the node &
                // replace node payload with former right value
                T save = removeMe.links.get(0).right.payload;
                remove(save);
                removeMe.payload = save;
            }
            size--;
            return true;
        }
        // loop to relink nodes that were connected to remove object
        for (int i = 0; i < removeMe.links.size(); i++) {
            SkipListSetPayloadWrapper<T> right = removeMe.links.get(i).right;
            SkipListSetPayloadWrapper<T> left = removeMe.links.get(i).left;
            if (right != null) {
                right.setLinksAtIdx(i, left, right.links.get(i).right);
            }
            if (left != null) {
                left.setLinksAtIdx(i, left.links.get(i).left, right);
            }
            removeMe.setLinksAtIdx(i, null, null);
        }
        size--;
        return true;
    }

    // ____________________________________________
    // Function: removeAll
    // Parameters: collection of objects to remove from the set
    // Purpose: remove all objects in the given collection from the list
    // Returns: boolean true if any object was successfully removed
    // Documented Anomolies: none
    public boolean removeAll(Collection<?> objectCollection) {
        boolean returnValue = false;
        for (Object object : objectCollection)
            returnValue = returnValue | remove(object);
        return false;
    }

    // ____________________________________________
    // Function: retainAll
    // Parameters: collection of objects to retain in the set
    // Purpose: remove all objects NOT in the given collection from the list
    // Returns: boolean true if any object was successfully removed
    // Documented Anomolies: none
    public boolean retainAll(Collection<?> objectCollection) {
        boolean returnValue = false;
        Iterator<T> retainIterator = iterator();
        // iterate through set and remove all items that are not in given collection
        while (retainIterator.hasNext()) {
            T retain = retainIterator.next();
            if (!objectCollection.contains(retain)) {
                returnValue = returnValue | remove(retain);
            }
        }
        return returnValue;
    }

    // ____________________________________________
    // Function: size
    // Parameters: none
    // Purpose: return size of list
    // Returns: size of list
    // Documented Anomolies: none
    public int size() {
        return size;
    }

    // subSet is allowed to throw an UnsupportedOperationException according to
    // project documentation
    public SkipListSet<T> subSet(T fromObject, T toObject) {
        throw new UnsupportedOperationException();
    }

    // tailSet is allowed to throw an UnsupportedOperationException according to
    // project documentation
    public SkipListSet<T> tailSet(T object) {
        throw new UnsupportedOperationException();
    }

    // ____________________________________________
    // Function: toArray
    // Parameters: none
    // Purpose: return the skip list set as an array
    // Returns: array containing all items in the skip list set
    // Documented Anomolies: none
    public Object[] toArray() {
        SkipListSetPayloadWrapper<T> searcher = root;
        // create an array of size of skip list
        Object[] skipListAsArray = new Object[size];
        // iterate through list and add each item to the array
        for (int i = 0; i < size; i++) {
            if (searcher != null) {
                skipListAsArray[i] = searcher.payload;
                searcher = searcher.links.get(0).right;
            }
        }
        return skipListAsArray;
    }

    // ____________________________________________
    // Function: toArray
    // Parameters: array to put the skip list into
    // Purpose: put the skip list into a given array
    // Returns: the given array with all items in the skip list set
    // Documented Anomolies: none
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] array) {
        SkipListSetPayloadWrapper<?> searcher = root;
        // if array given was not big enough, resize the array
        if (array.length < size) {
            array = Arrays.copyOf(array, size);
        }
        // if array was too big, set position at size to null
        else if (array.length > size) {
            array = Arrays.copyOf(array, size);
        }
        // search skip list and add each item to the array
        for (int i = 0; i < size; i++) {
            if (searcher != null) {
                array[i] = (T) searcher.payload;
                searcher = searcher.links.get(0).right;
            }
        }
        return array;
    }

    // ____________________________________________
    // Function: reBalance
    // Parameters: none
    // Purpose: re-balance the list after too many items are deleted
    // Returns: nothing
    // Documented Anomolies: none
    public void reBalance() {
        SkipListSetPayloadWrapper<T> searcher = root;
        SkipListSet<T> newList = new SkipListSet<T>();
        // add every item to a new list
        while (searcher != null) {
            newList.add(searcher.payload);
            searcher = searcher.links.get(0).right;
        }
        // replace current list with new list
        root = newList.root;
        height = newList.height;
        System.gc();
    }

    // ____________________________________________
    // Function: raiseHeight
    // Parameters: none
    // Purpose: raise maximum height of the list
    // Returns: nothing
    // Documented Anomolies: none
    private void raiseHeight() {
        // add a new set of null links to root & raise max height
        root.setLinks(null, null);
        height++;
    }

    // ____________________________________________
    // Function: heightRandomizer
    // Parameters: none
    // Purpose: generate a random height for a node in the list
    // Returns: integer of height
    // Documented Anomolies: none
    private int heightRandomizer() {
        int randomHeight = 0;
        // height will have a 50% chance to grow one level for each level until height -
        // 1 is reached
        while (Math.round(Math.random()) == 1 && randomHeight != height - 1) {
            randomHeight++;
        }
        return randomHeight;
    }

    // ____________________________________________
    // Function: search
    // Parameters: object to search for, boolean value to know if the value is being
    // added to the list
    // Purpose: search through the list to find a given object
    // Returns: wrapper of the object that was found
    // Documented Anomolies: none
    private SkipListSetPayloadWrapper<T> search(T obj, boolean add) {
        SkipListSetPayloadWrapper<T> temp = root;
        // wrapper for the object is created and links are generated up to a random
        // height if add is true
        SkipListSetPayloadWrapper<T> itemWrapper = new SkipListSetPayloadWrapper<T>(obj);
        int addHeight = heightRandomizer();
        if (add) {
            for (int i = 0; i <= addHeight; i++) {
                itemWrapper.setLinks(null, null);
            }
            size++;
        }

        // for loop to iterate through the list vertically, starting at the top
        for (int i = height - 1; i >= 0; i--) {
            SkipListSetPayloadWrapper<T> right = temp.links.get(i).right;
            // if right exists and is less than or equal to object, move right without
            // dropping a level
            if (right != null && right.payload.compareTo(obj) <= 0) {
                temp = right;
                i++;
                continue;
            }
            // if add is true and current height is at or below height to add at
            if (add && i <= addHeight) {
                // if right exists and equals the object, return the right value
                if (right != null && right.payload.compareTo(obj) == 0) {
                    return right;
                }
                // otherwise set links at this height for the new item to add
                itemWrapper.setLinksAtIdx(i, temp, right);
                // update other item wrapper links to point to new item
                if (right != null) {
                    right.links.get(i).left = itemWrapper;
                }
                temp.links.get(i).right = itemWrapper;
            }
        }

        // return the new item if add is true, otherwise return the item wrapper that
        // was found
        if (add)
            return itemWrapper;
        else
            return temp;
    }

    // ___________________________________________________
    // class: SkipListSetIterator
    // purpose: used to create a working iterator to iterate through the bottom
    // level of a skip list
    // Iterator Documentation:
    // https://docs.oracle.com/javase/8/docs/api/java/util/Iterator.html
    private class SkipListSetIterator<T extends Comparable<T>> implements Iterator<T> {

        // private variable holding current item wrapper of the iterator
        private SkipListSetPayloadWrapper<T> iterItem;

        // ____________________________________________
        // Function: hasNext
        // Parameters: none
        // Purpose: check if current iterator value is null
        // Returns: boolean true iff current iterator value is not null
        // Documented Anomolies: none
        public boolean hasNext() {
            return iterItem != null;
        }

        // ____________________________________________
        // Function: next
        // Parameters: none
        // Purpose: advance to next wrapper in set
        // Returns: current wrapper payload
        // Documented Anomolies: none
        public T next() {
            T returner = (T) iterItem.payload;
            iterItem = iterItem.links.get(0).right;
            return returner;
        }

        // ____________________________________________
        // Function: remove
        // Parameters: none
        // Purpose: remove an item from the list as it iterates through
        // Returns: nothing
        // Documented Anomolies: none
        public void remove() {
            // if item does not exist, do nothing, otherwise call SkipListSet remove for
            // current iterator item
            if (iterItem == null || iterItem.payload == null)
                return;
            else {
                SkipListSet.this.remove((Object) iterItem.payload);
            }
        }

        // ____________________________________________
        // Constructor: SkipListSetIterator
        // Parameters: payload wrapper always containing root
        // Purpose: initialize a new iterator and set it to start at the root
        private SkipListSetIterator(SkipListSetPayloadWrapper<T> first) {
            iterItem = first;
        }

    }

    // ___________________________________________________
    // class: SkipListSetPayloadWrapper
    // purpose: Contains item stored in list and vertical list of links to other
    // nodes
    private class SkipListSetPayloadWrapper<T extends Comparable<T>> {

        // private variables to store generic payload & list of links to other wrappers
        T payload;
        ArrayList<Links> links;

        // ____________________________________________
        // Constructor: SkipListSetPayloadWrapper
        // Parameters: generic payload data
        // Purpose: create a new payload wrapper with payload & list of links
        SkipListSetPayloadWrapper(T payload) {
            this.payload = payload;
            this.links = new ArrayList<>();
        }

        // ____________________________________________
        // Function: setLinks
        // Parameters: left and right item wrappers to add as links
        // Purpose: add new links to this item wrapper
        // Returns: nothing
        // Documented Anomolies: none
        private void setLinks(SkipListSetPayloadWrapper<T> left, SkipListSetPayloadWrapper<T> right) {
            links.add(new Links(left, right));
        }

        // ____________________________________________
        // Function: setLinksAtIdx
        // Parameters: index to add at, left and right item wrappers to add as links
        // Purpose: add new links at a certain index to this item wrapper
        // Returns: nothing
        // Documented Anomolies: none
        private void setLinksAtIdx(int index, SkipListSetPayloadWrapper<T> left, SkipListSetPayloadWrapper<T> right) {
            links.set(index, new Links(left, right));
        }

        // ___________________________________________________
        // class: Links
        // purpose: Contain right and left links for skip list nodes
        private class Links {

            // private variables to store left and right links
            private SkipListSetPayloadWrapper<T> left;
            private SkipListSetPayloadWrapper<T> right;

            // ____________________________________________
            // Constructor: Links
            // Parameters: left and right payload wrappers
            // Purpose: create a left and right link
            Links(SkipListSetPayloadWrapper<T> left, SkipListSetPayloadWrapper<T> right) {
                this.left = left;
                this.right = right;
            }
        }

    }
}
