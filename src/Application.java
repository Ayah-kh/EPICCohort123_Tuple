import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Application {

    public static void main(String[] args) {

        MyLinkedList<Integer> ids=MyLinkedList.of(1000,1001,1002,1003);
        MyLinkedList<String> names=MyLinkedList.of("Mohammed","Ruba","Yasmeen","Yazen");

        MyLinkedList<Tuple<Integer, String>> zippedNames = ids.zip(names);

        Tuple<MyLinkedList<Integer>, MyLinkedList<String>> tupleOfList = MyLinkedList.unZip(zippedNames);
        System.out.println("__________ids______________");
        tupleOfList._1.forEach(System.out::println);
        System.out.println("__________names______________");
        tupleOfList._2.forEach(System.out::println);
    }
}
