package easyXO;

/**
 * Enum to define player turn
 */
enum PlayerTurn{
    FIRST, SECOND;

    public PlayerTurn switchTurn(){
        if(this == PlayerTurn.FIRST) {
            return SECOND;
        }else {
            return FIRST;
        }
    }
}

