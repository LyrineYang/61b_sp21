package gitlet;

import static gitlet.Utils.join;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Lyrine Yang
 */
public class Main {

    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND1> <OPERAND2> ...
     *
     */
    public static boolean argsCheck(String[] args, int length) {
        if (args.length != length) {
            System.out.println("Incorrect operands.");
            return false;
        }
        return true;
    }
    public static void main(String[] args) {
        // TODO: what if args is empty?
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        if (!firstArg.equals("init") && !Repository.GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
        switch(firstArg) {
            case "init":
                argsCheck(args, 1);
                Repository.init();
                break;
            case "add":
                argsCheck(args, 2);
                Repository.add(args[1]);
                break;
            case "commit":
                argsCheck(args, 2);
                Repository.commit(args[1]);
                break;
            case "rm":
                argsCheck(args, 2);
                Repository.remove(args[1]);
            case "log":
                argsCheck(args, 1);
                Repository.log();
            case "global-log":
                argsCheck(args, 1);
                Repository.globalLog();
            case "find":
                argsCheck(args, 2);
                Repository.find(args[1]);
            case "status":
                argsCheck(args, 1);
                Repository.status();
            case "check-out":
                Repository.checkOut(args);
            case "branch":
                argsCheck(args, 2);
                Repository.branch(args[1]);
            case "rm-branch":
                argsCheck(args, 2);
                Repository.rmBranch(args[1]);
            case "reset":
                argsCheck(args,2);
                Repository.reset(args[1]);
            case "merge":
            default:
                System.out.println("No command with that name exists.");
            // TODO: FILL THE REST IN
        }
    }
}
