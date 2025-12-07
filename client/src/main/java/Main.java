import chess.*;
import datamodel.*;

public class Main {

    public static void main(String[] args) throws Exception {
        try {
            new ChessClient();
        } catch (Throwable ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }
    }
}





