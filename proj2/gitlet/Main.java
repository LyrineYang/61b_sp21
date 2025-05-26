package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Lyrine Yang
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                // TODO: add the exception deal
                Repository.initRepository();
                break;
            case "add":
                // TODO: add the exception deal
                Repository.addRepository(args[1]);
                break;
            case "commit":
                // TODO: add the exception deal
                if (args[1] == null) {
                    System.out.println("No changes added to the commit.");
                    break;
                }
                Repository.commit(args[1]);
                break;
            default:
                System.out.println("No command with that name exists.");
            // TODO: FILL THE REST IN
        }
    }
}
