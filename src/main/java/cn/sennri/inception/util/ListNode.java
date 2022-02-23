package cn.sennri.inception.util;

public class ListNode<T> {
    T node;
    public ListNode<T> next;
    public ListNode<T> pre;
    ListNode(T node){this.node = node;}
    ListNode(T node, ListNode<T> pre, ListNode<T> next){
        this(node);
        this.pre = pre;
        this.next = next;
    }


    /**
     * todo
     * @param first
     * @param nodes
     * @param <T>
     * @return
     */
    @SafeVarargs
    public static <T> ListNode<T> connect(ListNode<T> first, ListNode<T> ... nodes){
        ListNode<T> node = first;
        for (ListNode<T> n:nodes){
            node.next = n;
            n.pre = node;
            node = n;
        }
        return first;
    }

    @SafeVarargs
    public static <T> ListNode<T> connect(T first, T ... nodes){
        ListNode<T> head = new ListNode<>(first);
        ListNode<T> node = head;
        for (T n:nodes){
            ListNode<T> temp = new ListNode<>(n);
            node.next = temp;
            temp.pre = node;
            node = temp;
        }
        return head;
    }

    public void connectTo(ListNode<T> next){
        next.pre = this;
        this.next = next;
    }

    public T getNode() {
        return node;
    }

    public void setNode(T node) {
        this.node = node;
    }

    public void setNext(ListNode<T> next) {
        this.next = next;
    }

    public void setPre(ListNode<T> pre) {
        this.pre = pre;
    }

    public ListNode<T> getNext() {
        return next;
    }

    public ListNode<T> getPre() {
        return pre;
    }
}
