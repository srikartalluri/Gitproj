package gitlet;

import java.io.File;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author srikar talluri
 */
public class Main {

    /**
     * the.
     */
    protected static Repository repo;

    /**
     * the.
     */
    static final File CWD = new File(".");

    /**
     * Yes.
     * @param args yea
     */
    public static void initf(String[] args) {
        repo = new Repository();
        Utils.validateNumArgs(args, 1);
        repo.init();
        writeOut();
    }

    /**
     * Yes.
     * @param args yea
     */
    public static void addf(String[] args) {
        setUpPersistance();
        Utils.validateNumArgs(args, 2);
        repo.add(args[1]);
        writeOut();
    }

    /**
     * Yes.
     * @param args yea
     */
    public static void commitf(String[] args) {
        setUpPersistance();
        Utils.validateNumArgs(args, 2);
        repo.commit(args[1]);
        writeOut();
    }

    /**
     * Yes.
     * @param args yea
     */
    public static void logf(String[] args) {
        setUpPersistance();
        Utils.validateNumArgs(args, 1);
        repo.log();
        writeOut();
    }

    /**
     * Yes.
     * @param args yea
     */
    public static void rmf(String[] args) {
        setUpPersistance();
        Utils.validateNumArgs(args, 2);
        repo.remove(args[1]);
    }

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) {
        if (args.length == 0) {
            Utils.exitWithError("Please enter a command.");
        }
        switch (args[0]) {
        case "init":
            initf(args);
            break;
        case "add":
            addf(args);
            break;
        case "commit":
            commitf(args);
            break;
        case "log":
            logf(args);
            break;
        case "rm":
            rmf(args);
            break;
        case "global-log":
            setUpPersistance();
            Utils.validateNumArgs(args, 1);
            repo.globalLog();
            writeOut();
            break;
        case "find":
            setUpPersistance();
            Utils.validateNumArgs(args, 2);
            repo.find(args[1]);
            break;
        case "status":
            int a = setUpPersistance();
            if (a == -1) {
                break;
            }
            Utils.validateNumArgs(args, 1);
            repo.status();
            break;
        case "checkout":
            checkoutf(args);
            break;
        case "branch":
            setUpPersistance();
            Utils.validateNumArgs(args, 2);
            repo.branch(args[1]);
            break;
        case "rm-branch":
            rmbf(args);
            break;
        case "reset":
            resetf(args);
            break;
        case "merge":
            mergef(args);
            break;
        default:
            Utils.exitWithError("No command with that name exists.");
        }
    }

    /**
     * Yes.
     * @param args yea
     */
    public static void rmbf(String[] args) {
        setUpPersistance();
        Utils.validateNumArgs(args, 2);
        repo.removeBranch(args[1]);
    }

    /**
     * Yes.
     * @param args yea
     */
    public static void resetf(String[] args) {
        setUpPersistance();
        Utils.validateNumArgs(args, 2);
        repo.reset(args[1]);
    }

    /**
     * Yes.
     * @param args yea
     */
    public static void mergef(String[] args) {
        setUpPersistance();
        Utils.validateNumArgs(args, 2);
        repo.merge(args[1]);
        writeOut();
    }

    /**
     * Yes.
     * @param args yea
     */
    public static void checkoutf(String[] args) {
        setUpPersistance();
        if (args.length == 2) {
            repo.checkoutBranch(args[1]);
        } else if (args.length == 3 && args[1].equals("--")) {
            repo.checkout(args[2]);
        } else if (args.length == 4 && args[2].equals("--")) {
            repo.checkout(args[3], args[1]);
        } else {
            Utils.exitWithError("Incorrect operands.");
        }
    }

    /**
     * yes.
     * @return yes.
     */
    public static int setUpPersistance() {

        File gitletCheck = new File(".gitlet");

        if (!gitletCheck.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            return -1;
        }

        File repCheck = new File(".gitlet/Repository.txt");

        repo = Utils.readObject(repCheck, Repository.class);
        return 0;
    }

    /**
     * yea.
     */
    public static void writeOut() {

        File gitletCheck = new File(".gitlet");

        if (!gitletCheck.exists()) {
            System.out.println("writing out but no gitlet repo");
            return;
        }

        File repCheck = new File(".gitlet/Repository.txt");

        Utils.writeObject(repCheck, repo);

    }

}
