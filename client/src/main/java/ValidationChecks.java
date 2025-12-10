public class ValidationChecks {



    public ValidationChecks() {

    }

    public static String loginCheck(String[] inputs) {
//        System.out.println("login <USERNAME> <PASSWORD> - to play chess");
        if (inputs.length < 3) {
            return """
                    Oh no! Looks like we don't have enough the username AND password🤭
                    Please make sure to format your input like this:
                    --->'login <USERNAME> <PASSWORD>'
                    """;

        } else if (inputs.length > 3) {
            return "Oh no! Looks like you put in too many inputs:)" +
                    "\nPlease make sure to format your input like this: " +
                    "\n     'login <USERNAME> <PASSWORD>'";
        }
        return null;
    }

    public static String registerCheck(String[] inputs) {
        if (inputs.length < 4) {
            return """
                    Oh no! Looks like we're missing a username, password, or email!\
                    
                    Please make sure you format your input like this: \
                    
                    'register <USERNAME> <PASSWORD> <EMAIL>'""";
        } else if (inputs.length > 4) {
            return "Oh no! Looks like you put in too many inputs:)" +
                    "\nPlease make sure to format your input like this: " +
                    "\n      'register <USERNAME> <PASSWORD> <EMAIL>'";
        }
        return null;
    }

    public static String createCheck(String[] inputs) {
        if (inputs.length < 2) {
            return """
                     Oh no! Looks like you're missing the name of the game:)\
                                    \s
                     What would you like to name the game?\
                    \s
                     Please format your input like this: \
                    \s
                         'create <GAME NAME>'""";
        } else if (inputs.length > 2) {
            return "Oh no! Looks like you have too many inputs:)" +
                    "\n Please format your input like this: " +
                    "\n    'create <GAME NAME>'";
        }
        return null;
    }

    public static String joinCheck(String[] inputs, int listLen) {
//        System.out.println("join <ID> [WHITE|BLACK] - a game");

        //check arg lengths
        if (inputs.length < 3) {
            return """
                    Oh no! Looks like you're missing something:)
                    Please format your input like this:
                        'join <ID> [WHITE|BLACK]'
                    """;
        } else if (inputs.length > 3) {
            return "Oh no! Looks like you have TOO many inputs:)" +
                    "\n Please format your input like this: " +
                    "\n    'join <ID> [WHITE|BLACK]'";
        }
        //check order of args
        if (inputs[1].equals("WHITE") || inputs[1].equals("BLACK")) {
            return """
                    Oh no! Looks like your arguments got mixed up.
                    Please format your input like this:
                        'join <ID> [WHITE|BLACK]'
                    """;
        } else if (!inputs[2].equals("WHITE") && !inputs[2].equals("BLACK")) {
            return """
                    Oh no! Looks like your arguments got mixed up.
                    Please format your input like this:
                        'join <ID> [WHITE|BLACK]'
                    """;
        }
        //check game number is valid
        if (checkGameNumber(inputs, listLen) != null) {
            return checkGameNumber(inputs, listLen);
        }

        return null;
    }

    public static String observeCheck(String[] inputs, int listLen) {

        if (inputs.length < 2) {
            return """
                    Oh no! Looks like you're missing the game number:)
                    Please format your input like this:
                    --->'observe <GAME NUMBER>'
                    """;
        } else if (inputs.length > 2) {
            return """
                    Oh no! Looks like you have too many inputs:)
                    Please format your input like this:
                    --->'observe <GAME NUMBER>'
                    """;
        }
        //check game number is valid
        if (checkGameNumber(inputs, listLen) != null) {
            return checkGameNumber(inputs, listLen);
        }

        return null;
    }

    public static String checkGameNumber(String[] inputs, int listLen) {
        int ind = 0;
        try {
            ind = Integer.parseInt(inputs[1]);
        } catch (NumberFormatException e) {
            System.out.println("Game Number was not a valid integer. Please pass in a valid integer.");
            System.out.println("""
                    Please format your input like this:
                    --->'join <ID> [WHITE|BLACK]'
                    """);
        }

        ind = Integer.parseInt(inputs[1]);
        if (ind > listLen || ind < 1) {
            return """
                    Oh no! Looks like that game doesn't exist:)
                    Please enter a valid game number with your query:
                    """;
        }

        return null;
    }

    public static String checkRedraw(String[] inputs) {

        if (inputs.length > 2) {
            return "Oh no! Looks like you have TOO many inputs:)" +
                    "\n Please format your input like this: " +
                    "\n--->'redraw'";
        }
        return null;
    }

    public static String checkMakeMove(String[] inputs) {

        if (inputs.length > 3) {
            return "Oh no! Looks like you have TOO many inputs:)" +
                    "\n Please format your input like this: " +
                    "\n--->'make_move <START POSITION> <END POSITION>'";
        } else if (inputs.length < 3) {
            return """
                    Oh no! Looks like you're missing a field:)
                    Please format your input like this:
                    --->'make_move <START POSITION> <END POSITION>'
                    """;
        }

        if (!inputs[1].matches("[a-h][1-8]")) {
            return """
                    Wait a second, your first move doesn't look like a valid move🤔
                    Please format your moves as a letter and number, like this: a5, b3, h6, d1, f7
                    """;
        } else if (!inputs[2].matches("[a-h][1-8]")) {
            return """
                    Wait a second, your second move doesn't look like a valid move🤔
                    Please format your moves as a letter and number, like this: a5, b3, h6, d1, f7
                    """;
        }
        return null;
    }

    public static String checkLeave(String[] inputs) {
        if (inputs.length > 2) {
            return """
                    Oh no! Looks like you have too many inputs:)\
                    
                     Please format your input like this: \
                    
                    --->'leave'""";
        }
        return null;
    }

    public static String checkResign(String[] inputs) {
        if (inputs.length > 2) {
            return """
                    Oh no! Looks like you have too many inputs:)\
                    
                     Please format your input like this: \
                    
                    --->'resign'""";
        }
        return null;
    }

    public static String checkHighlight(String[] inputs) {
        if (inputs.length > 2) {
            return """ 
                    Oh no! Looks like ou have too many inputs:)
                    Please format your input like this:
                    ---->'highlight <CHESS POSITION>'
                    """;
        } else if (inputs.length < 2) {
            return """ 
                    Oh no! Looks like you have too few inputs:)
                    Please format your input like this:
                    ---->'highlight <CHESS POSITION>'
                    """;
        }



        if (!inputs[1].matches("[a-h][1-8]")) {
            return """
                    Wait a second, your first move doesn't look like a valid position🤔
                    Please format your position as a letter and number, like this: a5, b3, h6, d1, f7
                    """;
        }
        return null;
    }
}
