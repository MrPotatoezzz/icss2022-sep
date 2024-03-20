package nl.han.ica.datastructures;

public class HANQueue<T> implements IHANQueue<T> {
    private final IHANLinkedList<T> queue = new HANLinkedList<T>();
    @Override
    public void clear() {
        queue.clear();
    }

    @Override
    public boolean isEmpty() {
        return !(queue.getSize() > 1);
    }

    @Override
    public void enqueue(T value) {
        if(queue.getSize() == 0){
            queue.addFirst(value);
        }
        queue.insert(queue.getSize() - 1, value);
    }

    @Override
    public T dequeue() {
        T firstItem = queue.getFirst();
        queue.removeFirst();
        return firstItem;
    }

    @Override
    public T peek() {
        return queue.getFirst();
    }

    @Override
    public int getSize() {
        return queue.getSize();
    }
}
