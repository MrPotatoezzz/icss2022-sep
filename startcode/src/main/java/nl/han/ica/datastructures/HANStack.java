package nl.han.ica.datastructures;

public class HANStack<T> implements IHANStack<T> {
    private IHANLinkedList<T> linkedList = new HANLinkedList<T>();
    @Override
    public void push(T value) {
        if(linkedList.getSize() == 0) {
            linkedList.addFirst(value);
        }else{
            linkedList.insert(linkedList.getSize(), value);
        }
    }

    @Override
    public T pop() {
        if (linkedList.getSize() == 0) {
            throw new IllegalStateException("Stack is empty");
        }
        T result = linkedList.get(linkedList.getSize() - 1);
        linkedList.delete(linkedList.getSize() - 1);
        return result;
    }

    @Override
    public T peek() {
        if (linkedList.getSize() == 0) {
            throw new IllegalStateException("Stack is empty");
        }
        return linkedList.get(linkedList.getSize() - 1);
    }
}
