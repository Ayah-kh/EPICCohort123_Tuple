import java.util.Comparator;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class MyLinkedList<E> {
    private int size;
    private Node first;
    private Node last;

    public static <E> MyLinkedList<E> of(E... data) {
        MyLinkedList<E> myLinkedList = new MyLinkedList<>();
        for (E datum : data) {
            myLinkedList.addLast(datum);
        }

        return myLinkedList;
    }

    public MyLinkedList<E> add(E data) {
        return addLast(data);
    }

    public Optional<E> first() {
        return first == null
                ? Optional.empty()
                : Optional.of(first.data);
    }

    public Optional<E> last() {
        return last == null
                ? Optional.empty()
                : Optional.of(last.data);
    }

    public MyLinkedList<E> addFirst(E data) {
        Node oldFirst = first;
        Node addNode = new Node(oldFirst, null, data);
        first = addNode;
        if (oldFirst == null)
            last = first;
        else
            oldFirst.prev = first;

        size++;
        return this;
    }

    public MyLinkedList<E> addLast(E data) {
        Node oldLast = last;
        Node addLast = new Node(null, oldLast, data);
        last = addLast;
        if (oldLast == null)
            first = last;
        else
            oldLast.next = last;

        size++;
        return this;
    }

    public Optional<E> removeFirst() {

        return first == null
                ? Optional.empty()
                : Optional.of(removeNode(first));
    }

    public Optional<E> removeLast() {

        return last == null
                ? Optional.empty()
                : Optional.of(removeNode(last));
    }

    private E removeNode(Node node) {
        Node nextNode = node.next;
        Node prevNode = node.prev;

        if (prevNode == null)
            first = nextNode;
        else
            prevNode.next = nextNode;

        if (nextNode == null)
            last = prevNode;
        else
            nextNode.prev = prevNode;

        size--;
        return node.data;
    }

    public <U> U reduceR(U seed, Function<E, Function<U, U>> function) {
        return reduceR(seed, function, last);
    }

    private <U> U reduceR(U acc, Function<E, Function<U, U>> function, Node node) {
        return node == null
                ? acc
                : reduceR(function.apply(node.data).apply(acc)
                , function
                , node.prev
        );
    }

    public <U> U reduceR(U seed,
                         Function<E, Function<U, U>> function,
                         Function<E, Function<U, Boolean>> condition) {
        return reduceR(seed
                , function
                , condition, last);
    }

    private <U> U reduceR(U acc,
                          Function<E, Function<U, U>> function,
                          Function<E, Function<U, Boolean>> condition
            , Node node) {
        return node == null || condition.apply(node.data).apply(acc)
                ? acc
                : reduceR(function.apply(node.data).apply(acc)
                , function
                , condition
                , node.prev
        );
    }

    public boolean anyMatch(Predicate<E> condition) {
        return !isEmpty() && reduceR(false, e -> acc -> condition.test(e) || acc, e -> acc -> acc);

    }

    public boolean isEmpty() {
        return size == 0;
    }


    public <U> U reduceL(U seed, Function<U, Function<E, U>> function) {
        return reduceL(seed, function, first);
    }

    private <U> U reduceL(U acc, Function<U, Function<E, U>> function, Node node) {
        return node == null
                ? acc
                : reduceL(function.apply(acc).apply(node.data)
                , function
                , node.next);
    }

    public void forEach(Consumer<E> consumer) {
        Node node = first;
        for (int i = 0; i < size; i++) {
            consumer.accept(node.data);
            node = node.next;
        }
    }

    public <U> MyLinkedList<U> map(Function<E, U> function) {
        return reduceL(new MyLinkedList<U>(), acc -> e -> acc.addLast(function.apply(e)));
    }

    public <U> MyLinkedList<U> flatMap(Function<E, MyLinkedList<U>> function) {
        return reduceL(new MyLinkedList<>(),
                aac -> e -> aac.addAll(function.apply(e)));
    }

    public MyLinkedList<E> addAll(MyLinkedList<E> anotherList) {
        return anotherList.reduceL(this, acc -> e -> acc.addLast(e));
    }

    public MyLinkedList<E> reversed() {
        return reduceR(new MyLinkedList<>(), e -> acc -> acc.addLast(e));
    }

    public boolean allMatch(Predicate<E> predicate) {
        return reduceL(true, acc -> e -> acc && predicate.test(e));
    }

    public Optional<E> min(Comparator<E> comparator) {
        return isEmpty() ?
                Optional.empty()
                : Optional.of(reduceR(last.data,
                e -> acc -> comparator.compare(acc, e) > 0 ? e : acc));
    }

    public Optional<E> max(Comparator<E> comparator) {
        return isEmpty() ?
                Optional.empty()
                : Optional.of(reduceR(last.data,
                e -> acc -> comparator.compare(acc, e) < 0 ? e : acc));
    }

    public MyLinkedList<E> filter(Predicate<E> predicate){
        return reduceL(new MyLinkedList<>(),
                acc->e->predicate.test(e)?acc.add(e):acc);
    }

    public <U> MyLinkedList<Tuple<E,U>> zip(MyLinkedList<U> anotherList){
        Node eFirst=first;
        Node uFirst=(Node) anotherList.first;

        return zip(new MyLinkedList<Tuple<E,U>>(),eFirst,uFirst);
    }



    private <U> MyLinkedList<Tuple<E,U>> zip
            (MyLinkedList<Tuple<E,U>> acc, Node eNode, Node uNode) {
        return eNode==null||uNode==null
                ?acc
                :zip(acc.add(new Tuple<E,U>(eNode.data,(U)uNode.data))
                ,eNode.next,uNode.next);
    }

    public Stream<E> stream(){
        return Stream.iterate(first,n->n!=null, n->n.next)
                .map(n->n.data);

    }
    private class Node {
        private Node next;
        private Node prev;
        private E data;

        public Node(Node next, Node prev, E data) {
            this.next = next;
            this.prev = prev;
            this.data = data;
        }
    }


}
