package easyXO;

/**
 * Enum to define marks for the game field
 */
enum Mark{

    X(1), O(2), Empty(0);

    public final int value;

    Mark(int mark){
        this.value = mark;
    }

    public boolean isEmpty(){
        return this == Empty;
    }
}