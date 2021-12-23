package easyXO;

enum Mark{

    X(1), O(2), Empty(0);

    public final int mark;

    Mark(int mark){
        this.mark = mark;
    }

    public boolean isEmpty(){
        return this == Empty;
    }
}