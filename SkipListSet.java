import java.util.*;

// https://docs.oracle.com/javase/8/docs/api/java/util/SortedSet.html
public class SkipListSet<T extends Comparable<T>> implements SortedSet<T> {

    private static int height = -1;
    private static int size = -1;
    private SkipListSetPayloadWrapper<T> root = null;
    // private int how_many_adds = 0;

    public SkipListSet() {
        root = new SkipListSetPayloadWrapper<T>(null);
        height = 25;
        size = 0;
    }

    // add done for now
    public boolean add(T object) {
        // how_many_adds++;
        // System.out.println(how_many_adds);
        if (root == null || root.payload == null) {
            root = new SkipListSetPayloadWrapper<T>(object);
            for (int i = 0; i < height; i++) {
                root.setLinks(null, null);
            }
            size++;
            return true;
        }
        if (root.payload.compareTo(object) >= 0) {
            SkipListSetPayloadWrapper<T> newRoot = new SkipListSetPayloadWrapper<T>(object);
            // System.out.println(object);
            // System.out.println(root.links.size());
            for (int i = 0; i < height - 1; i++) {
                newRoot.setLinks(null, root);
                root.links.get(i).left = newRoot;
            }
            newRoot.setLinks(null, null);
            root.links.remove(root.links.size() - 1);
            // System.out.println(root.links.size());
            root = newRoot;
            size++;
            return true;
        }
        int oldSize = size;
        SkipListSetPayloadWrapper<T> nearestWrapper = search(object, true, false);
        if (oldSize != size) {
            return false;
        }
        if (size > Math.pow(2, height)) {
            raiseHeight();
        }
        return true;

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
        if (search((T) object, false, false).payload.compareTo((T) object) == 0)
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
        SkipListSetPayloadWrapper<T> removeMe = search((T) object, false, false);
        if (removeMe.payload.compareTo((T) object) != 0)
            return false;
        // add remove method here
        int numLinks = removeMe.links.size();
        // if (root.payload.compareTo((T) object) == 0) {
        if (numLinks == root.links.size()) {
            // System.out.println("ROOT");
            // System.out.println(root.payload);
            // System.out.println(object);
            // System.out.println(numLinks);
            if (size == 1) {
                root = null;
            } else {
                int i = removeMe.links.get(0).right.links.size();
                while (i < height) {
                    // removeMe.links.get(0).right.setLinks(null, null);
                    removeMe.links.get(0).right.setLinks(null, removeMe.links.get(i - 1).right);
                    i++;
                }
                // System.out.println(i);
                root = removeMe.links.get(0).right;
                // root.setLinks(null, null);
            }
            return true;
        }
        removeMe.removeLinks();
        // for (int i = 0; i < numLinks; i++) {
        // if (removeMe.links.get(i).right != null) {
        // removeMe.links.get(i).right.links.get(i).left = removeMe.links.get(i).left;
        // }
        // if (removeMe.links.get(i).left != null) {
        // removeMe.links.get(i).left.links.get(i).right = removeMe.links.get(i).right;
        // }
        // // removeMe.links.get(i).left = null;
        // // removeMe.links.get(i).right = null;
        // }
        size--;
        // if (contains(object)) {
        // System.out.println(search((T) object, false, true).payload.compareTo((T)
        // object) == 0);
        // System.out.println(object);
        // // System.out.println(height - 1);
        // System.out.println(removeMe.links.size());
        // // System.out.println(numLinks);
        // for (int i = 0; i < numLinks; i++) {
        // // System.out.println(i + " left right: " +
        // // removeMe.links.get(i).left.links.get(i).right.payload);
        // // System.out.println(i + " right left: " +
        // // removeMe.links.get(i).right.links.get(i).left.payload);
        // }
        // }
        removeMe = null;
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
        // SkipListSet<T> oldSet = this;
        // root = null;
        // Iterator<T> i = oldSet.iterator();
        // while (i.hasNext()) {
        // add(i.next());
        // }
        // oldSet.clear();
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
        // increase the root height
        root.setLinks(null, null);
        height++;
    }

    private int heightRandomizer() {
        int randomHeight = 0;
        while (Math.round(Math.random()) == 1 && randomHeight != height - 2) {
            randomHeight++;
        }
        // System.out.println("random height: " + randomHeight);
        return randomHeight;
    }

    private SkipListSetPayloadWrapper<T> search(T obj, boolean add, boolean DEBUG) {
        SkipListSetPayloadWrapper<T> temp = root;
        SkipListSetPayloadWrapper<T> itemWrapper = new SkipListSetPayloadWrapper<T>(obj);
        int addHeight = heightRandomizer();
        if (add) {
            for (int i = 0; i <= addHeight; i++) {
                itemWrapper.setLinks(null, null);
            }
            size++;
        }
        // int i = temp.links.size() - 1;

        for (int i = temp.links.size() - 1; i >= 0; i--) {
            SkipListSetPayloadWrapper<T> right = null;
            if (temp.links.size() > i)
                right = temp.links.get(i).right;
            else
                i = 0;
            if (right != null && right.payload.compareTo(obj) <= 0) {
                temp = right;
                // if (DEBUG) {
                // System.out.println(temp.payload);
                // }
                i++;
                continue;
            }
            if (add && i <= addHeight) {
                if (right != null && right.payload.compareTo(obj) == 0) {
                    return right;
                }
                // if (right != null && right.payload.compareTo(obj) < 0) {
                itemWrapper.setLinksAtIdx(i, temp, right);
                if (right != null) {
                    right.links.get(i).left = itemWrapper;
                }
                temp.links.get(i).right = itemWrapper;
                // }
            }
            // if (DEBUG) {
            // System.out.println("Level: " + i);
            // System.out.println("of obj: " + temp.payload);
            // // if (i == 0) {
            // if (temp.links.get(i).right != null)
            // System.out.println("r: " + temp.links.get(i).right.payload);
            // if (temp.links.get(i).left != null)
            // System.out.println("l: " + temp.links.get(i).left.payload);
            // // System.out.println("rl: " +
            // // temp.links.get(0).right.links.get(0).left.payload);
            // // }
            // }
            // if (i > 0) {
            // i--;
            // } else
            // break;
        }

        if (add)
            return itemWrapper;
        else
            return temp;
    }

    // https://docs.oracle.com/javase/8/docs/api/java/util/Iterator.html
    @SuppressWarnings("hiding")
    private class SkipListSetIterator<T extends Comparable<T>> implements Iterator<T> {

        private SkipListSetPayloadWrapper<T> currentItem = null;

        public boolean hasNext() {
            if (currentItem == null)
                return false;
            return (currentItem.links.get(0).right == null);
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

        private void setLinks(SkipListSetPayloadWrapper<T> left, SkipListSetPayloadWrapper<T> right) {
            links.add(new Links(left, right));
        }

        private void setLinksAtIdx(int index, SkipListSetPayloadWrapper<T> left, SkipListSetPayloadWrapper<T> right) {
            links.set(index, new Links(left, right));
        }

        private void removeLinks() {
            for (int i = 0; i < links.size(); i++) {
                if (links.get(i).right != null) {
                    links.get(i).right.links.get(i).left = links.get(i).left;
                }
                if (links.get(i).left != null) {
                    links.get(i).left.links.get(i).right = links.get(i).right;
                }
                links.get(i).left = null;
                links.get(i).right = null;
            }

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
