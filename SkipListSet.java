import java.util.*;

// https://docs.oracle.com/javase/8/docs/api/java/util/SortedSet.html
public class SkipListSet<T extends Comparable<T>> implements SortedSet<T> {

    private static int height = -1;
    private static int size = -1;
    private SkipListSetPayloadWrapper<T> root = null;

    public SkipListSet() {
        root = new SkipListSetPayloadWrapper<T>(null);
        height = 5;
        size = 0;
        // root.setLinks(null, null);
    }

    // add done for now
    public boolean add(T object) {
        if (root == null || root.payload == null) {
            root = new SkipListSetPayloadWrapper<T>(object);
            for (int i = 0; i < height; i++) {
                root.setLinks(null, null);
            }
            size++;
            return true;
        }
        int addHeight = heightRandomizer();
        SkipListSetPayloadWrapper<T> nearestWrapperByHeight[] = srch_non_recursive(object, addHeight);
        if (nearestWrapperByHeight[0].payload.compareTo(object) != 0) {
            // add method here
            // add process for increasing height
            // when it is determined that height needs to increase, take head and increase
            // its height by one, then, for all nodes at the height level 1 below head, 50%
            // chance to increase height by 1
            // if (size + 1 >= Math.pow(height, 2)) {
            if (Math.round(Math.log(size) / Math.log(height)) >= height) {
                // System.out.println("Here");
                // raiseHeight();
            }
            // object should be inserted on the right
            // if (nearestWrapperByHeight.get(0).payload.compareTo(object) < 0) {
            SkipListSetPayloadWrapper<T> wrapperToAdd = new SkipListSetPayloadWrapper<T>(object);
            for (int i = 0; i <= addHeight; i++) {
                if (nearestWrapperByHeight[i].payload.compareTo(object) < 0) {
                    SkipListSetPayloadWrapper<T> right = null;
                    if (nearestWrapperByHeight[i].links.get(i).right != null) {
                        right = nearestWrapperByHeight[i].links.get(i).right;
                        right.links.get(i).left = wrapperToAdd;
                    }
                    nearestWrapperByHeight[i].links.get(i).right = wrapperToAdd;
                    wrapperToAdd.setLinks(nearestWrapperByHeight[i], right);
                }

                else {
                    SkipListSetPayloadWrapper<T> left = null;
                    if (nearestWrapperByHeight[i].links.get(i).left != null) {
                        left = nearestWrapperByHeight[i].links.get(i).left;
                        left.links.get(i).right = wrapperToAdd;
                    }
                    nearestWrapperByHeight[i].links.get(i).left = wrapperToAdd;
                    wrapperToAdd.setLinks(left, nearestWrapperByHeight[i]);
                }
            }
            // }
            // insert to left
            // else {
            // }
            size++;
            return true;
        }
        return false;
    }

    public boolean addAll(Collection<? extends T> objectCollection) {
        int oldSize = size;
        for (T object : objectCollection) {
            add(object);
        }
        if (oldSize != size)
            return true;
        return false;
    }

    public void clear() {
        root = null;
        // root should hold all references, nullifying it and calling gc should free up
        // all memory
        System.gc();
        root = new SkipListSetPayloadWrapper<T>(null);
    }

    // TODO
    public Comparator<T> comparator() {
        return null;
    }

    // TODO
    @SuppressWarnings("unchecked")
    public boolean contains(Object object) {
        // System.out.println(height);
        if (srch_non_recursive((T) object, 0)[0].payload.compareTo((T) object) == 0)
            return true;
        return false;
    }

    public boolean containsAll(Collection<?> objectCollection) {
        boolean returnValue = true;
        for (Object object : objectCollection) {
            if (!returnValue)
                break;
            returnValue = returnValue & contains(object);
        }
        return returnValue;
    }

    // TODO
    public boolean equals(Collection<T> object) {
        if (object.size() == size && containsAll(object))
            return true;
        return false;
    }

    public T first() {
        if (root != null)
            return root.payload;
        return null;
    }

    // TODO
    public int hashCode() {
        Iterator<T> hashIterator = iterator();
        int result = 0;
        while (hashIterator.hasNext()) {
            result = result + Objects.hash(hashIterator.next());
        }
        return result;
    }

    public SkipListSet<T> headSet(T object) {
        throw new UnsupportedOperationException();
    }

    public boolean isEmpty() {
        if (root == null || root.payload == null)
            return true;
        return false;
    }

    public SkipListSetIterator<T> iterator() {
        return new SkipListSetIterator<T>(root);
    }

    // TODO
    public T last() {
        SkipListSetPayloadWrapper<T> current = root;
        int srchHeight = height - 1;
        while (current.links.get(srchHeight).right != null || srchHeight != 0) {
            if (current.links.get(srchHeight).right == null) {
                srchHeight--;
            } else {
                current = current.links.get(srchHeight).right;
            }
        }
        return current.payload;
    }

    // TODO
    @SuppressWarnings("unchecked")
    public boolean remove(Object object) {
        SkipListSetPayloadWrapper<T> removeMe = root.search((T) object, height - 1);
        if (removeMe.payload.compareTo((T) object) != 0)
            return false;
        // add remove method here
        for (int i = 0; i < removeMe.links.size(); i++) {
            if (removeMe.links.get(i).right != null) {
                removeMe.links.get(i).right.links.get(i).left = removeMe.links.get(i).left;
            }
            if (removeMe.links.get(i).left != null) {
                removeMe.links.get(i).left.links.get(i).right = removeMe.links.get(i).right;
            }
        }
        return true;
    }

    // TODO
    public boolean removeAll(Collection<?> objectCollection) {
        boolean returnValue = false;
        for (Object object : objectCollection)
            returnValue = returnValue & remove(object);
        return false;
    }

    // TODO
    public boolean retainAll(Collection<?> objectCollection) {
        return false;
    }

    public int size() {
        return size;
    }

    public SkipListSet<T> subSet(T fromObject, T toObject) {
        throw new UnsupportedOperationException();
    }

    public SkipListSet<T> tailSet(T object) {
        throw new UnsupportedOperationException();
    }

    // TODO
    public Object[] toArray() {
        return new Object[0];
    }

    // TODO
    @SuppressWarnings("hiding")
    public <T> T[] toArray(T[] array) {
        return null;
    }

    // TODO
    public void reBalance() {
    }

    // TODO
    private void raiseHeight() {
        SkipListSetPayloadWrapper<T> currentWrapper = root;
        SkipListSetPayloadWrapper<T> oldWrapper = root;
        while (currentWrapper.links.get(height - 2).right != null) {
            // advance to next link
            currentWrapper = currentWrapper.links.get(height - 2).right;
            // if value equals 1
            if (Math.round(Math.random()) == 1) {
                // set old wrapper right link to point to this wrapper
                oldWrapper.links.get(height - 1).right = currentWrapper;
                // add new left link to old wrapper to this wrapper
                currentWrapper.setLinks(oldWrapper, null);
                // this is now the new old wrapper
                oldWrapper = currentWrapper;
            }

        }
        height++;
    }

    private int heightRandomizer() {
        int randomHeight = 0;
        while (Math.round(Math.random()) == 1 && randomHeight != height - 1) {
            randomHeight++;
        }
        // System.out.println("random height: " + randomHeight);
        return randomHeight;
    }

    @SuppressWarnings("unchecked")
    private SkipListSetPayloadWrapper<T>[] srch_non_recursive(T objectToFind, int addHeight) {
        SkipListSetPayloadWrapper<T> eachHeight[] = new SkipListSetPayloadWrapper[addHeight + 1];
        SkipListSetPayloadWrapper<T> searcher = root;
        int curHeight = height - 1;
        while (curHeight >= 0) {
            // System.out.println("payload: " + searcher.payload + " object: " +
            // objectToFind);
            if (searcher.payload.compareTo(objectToFind) == 0) {
                if (curHeight <= addHeight)
                    eachHeight[curHeight] = searcher;
                curHeight--;
            }

            else if (searcher.payload.compareTo(objectToFind) < 0) {
                if (searcher.links.get(curHeight).right != null) {
                    searcher = searcher.links.get(curHeight).right;
                    SkipListSetPayloadWrapper<T> nextRight = searcher.links.get(curHeight).right;
                    if (curHeight <= addHeight && (nextRight == null || nextRight.payload.compareTo(objectToFind) > 0))
                        // if (curHeight <= addHeight)
                        eachHeight[curHeight] = searcher;
                }

                else {
                    if (curHeight <= addHeight)
                        eachHeight[curHeight] = searcher;
                    curHeight--;
                }
            }

            else {
                if (curHeight == 0) {
                    eachHeight[curHeight] = searcher;
                }

                if (searcher.links.get(curHeight).left != null) {
                    if (curHeight <= addHeight)
                        eachHeight[curHeight] = searcher;
                    searcher = searcher.links.get(curHeight).left;
                    curHeight--;
                }

                else {
                    if (curHeight <= addHeight)
                        eachHeight[curHeight] = searcher;
                    curHeight--;
                }
            }
        }
        return eachHeight;
    }

    // https://docs.oracle.com/javase/8/docs/api/java/util/Iterator.html
    @SuppressWarnings("hiding")
    private class SkipListSetIterator<T extends Comparable<T>> implements Iterator<T> {

        private SkipListSetPayloadWrapper<T> currentItem = null;

        public boolean hasNext() {
            return currentItem.links.get(0).right == null;
        }

        public T next() {
            T previousPayload = currentItem.payload;
            currentItem = currentItem.links.get(0).right;
            return previousPayload;
        }

        // TODO
        public void remove() {
        }

        SkipListSetIterator(SkipListSetPayloadWrapper<T> iterateFrom) {
            this.currentItem = iterateFrom;
        }

    }

    @SuppressWarnings("hiding")
    private class SkipListSetPayloadWrapper<T extends Comparable<T>> {
        T payload;
        ArrayList<Links> links;

        SkipListSetPayloadWrapper(T payload) {
            this.payload = payload;
            this.links = new ArrayList<>();
        }

        private SkipListSetPayloadWrapper<T> search(T objectToFind, int curHeight) {

            // System.out.println(curHeight);
            // if (curHeight == height) {
            // curHeight--;
            // }

            if (payload.compareTo(objectToFind) == 0)
                return this;

            else if (payload.compareTo(objectToFind) < 0) {
                if (links.get(curHeight).right != null)
                    return links.get(curHeight).right.search(objectToFind, curHeight);
                else
                    return this;
            }

            else if (payload.compareTo(objectToFind) > 0) {
                if (curHeight == 0)
                    return this;
                if (links.get(curHeight).left != null)
                    return links.get(curHeight).left.search(objectToFind, curHeight - 1);
                else
                    return search(objectToFind, curHeight - 1);
            }

            else
                return this;

        }

        private void setLinks(SkipListSetPayloadWrapper<T> left, SkipListSetPayloadWrapper<T> right) {
            links.add(new Links(left, right));
        }

        private class Links {
            SkipListSetPayloadWrapper<T> left;
            SkipListSetPayloadWrapper<T> right;

            Links(SkipListSetPayloadWrapper<T> left, SkipListSetPayloadWrapper<T> right) {
                this.left = left;
                this.right = right;
            }
        }

    }
}
