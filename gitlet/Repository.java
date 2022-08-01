package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Comparator;
import java.util.Set;

/**
 * The repository.
 * @author srikar talluri
 */
public class Repository implements Serializable {

    /**     */

    /**The gitlet Folder.     */
    static final File GITETFOLDER = new File(".gitlet");

    /**The gitlet Folder.     */
    static final File COMMITSFOLDER = new File(".gitlet/commits");

    /**The gitlet Folder.     */
    static final File REPFILE = new File(".gitlet/Repository.txt");

    /**The gitlet Folder.     */
    static final File STAGE = new File(".gitlet/staging");

    /**The gitlet Folder.     */
    static final File STAGETOADD = new File(".gitlet/staging/toAdd");

    /**The gitlet Folder.     */
    static final File STAGETOREM = new File(".gitlet/staging/toRem");

    /**The gitlet Folder.     */
    static final File GLOBALLOGFILE = new File(".gitlet/globalLog.txt");

    /**The gitlet Folder.     */
    static final File HEADFILE = new File(".gitlet/head.txt");

    /**The gitlet Folder.     */
    private static File branchesFile = new File(".gitlet/branches.txt");

    /**The gitlet Folder.*/
    private static File curBranchNameFile = new File(".gitlet/branchName.txt");

    /**The gitlet Folder.     */
    private static Comparator<String> com = new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            return o1.compareTo(o2);
        }
    };

    /**The gitlet Folder.     */
    private Commit _head;

    /**The gitlet Folder.     */
    private HashMap<String, Commit> branches = new HashMap<>();

    /**The gitlet Folder.     */
    private String curBranchName;

    /**
     * the const.
     */
    public Repository() {


    }

    /**
     * the init.
     */
    public void init() {

        if (GITETFOLDER.exists()) {

            System.out.println("A Gitlet version-control system already "
                    + "exists in the current directory.");
            return;
        }

        GITETFOLDER.mkdir();
        COMMITSFOLDER.mkdir();
        STAGE.mkdir();
        STAGETOADD.mkdir();
        STAGETOREM.mkdir();


        try {
            curBranchNameFile.createNewFile();
            REPFILE.createNewFile();
            GLOBALLOGFILE.createNewFile();
            HEADFILE.createNewFile();
            branchesFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Commit initial = new Commit();

        _head = initial;

        curBranchName = "master";

        branches.put(curBranchName, initial);


        Utils.writeContents(branchesFile, Utils.serialize(branches));
        Utils.writeContents(curBranchNameFile, Utils.serialize(curBranchName));

        Utils.writeContents(HEADFILE, Utils.serialize(_head));


        File newF = new File(COMMITSFOLDER + "/" + _head.getId() + ".txt");
        try {
            newF.createNewFile();
            Utils.writeContents(newF, Utils.serialize(_head));


        } catch (IOException e) {
            e.printStackTrace();
        }

        Utils.writeContents(GLOBALLOGFILE, _head.toString());

    }

    /**
     * yghkkjg.
     * @param fileName d.
     */
    public void add(String fileName) {
        File cwdFile = new File(fileName);

        if (!cwdFile.exists()) {
            System.out.println("File does not exist.");
            return;
        }

        String cwdFileContents = Utils.readContentsAsString(cwdFile);

        File toDele = new File(STAGETOREM + "/" + fileName);
        if (toDele.exists()) {
            toDele.delete();
        }

        Blob toAdd = new Blob(fileName, cwdFileContents);

        _head = Utils.readObject(HEADFILE, Commit.class);

        if (_head.getBlobs() != null) {
            for (Blob cur : _head.getBlobs()) {
                if (toAdd.equals(cur)) {
                    File toDel = new File(STAGETOADD + "/" + fileName);
                    if (toDel.exists()) {
                        toDel.delete();
                    }
                    return;
                }
            }
        }



        Utils.writeContents(new File(STAGETOADD
                + "/" + fileName), cwdFileContents);

    }

    /**
     * the commit.
     * @param msg the commit
     */
    public void commit(String msg) {

        _head = Utils.readObject(HEADFILE, Commit.class);
        branches = Utils.readObject(branchesFile, HashMap.class);
        curBranchName = Utils.readObject(curBranchNameFile, String.class);

        if (Utils.plainFilenamesIn(Repository.STAGETOADD).size() == 0
                && Utils.plainFilenamesIn(Repository.STAGETOREM).size() == 0) {
            System.out.println("No changes added to the commit.");
            return;
        }

        if (msg.length() == 0) {
            System.out.println("Please enter a commit message.");
            return;
        }


        Commit newCommit = new Commit(_head, msg);


        String ex = Utils.readContentsAsString(GLOBALLOGFILE);
        Utils.writeContents(GLOBALLOGFILE, newCommit.toString(), "\n", ex);


        File newF = new File(COMMITSFOLDER + "/" + newCommit.getId() + ".txt");
        try {
            newF.createNewFile();
            Utils.writeContents(newF, Utils.serialize(newCommit));


        } catch (IOException e) {
            e.printStackTrace();
        }

        branches.put(curBranchName, newCommit);

        _head = newCommit;
        Utils.writeContents(HEADFILE, Utils.serialize(_head));
        Utils.writeContents(branchesFile, Utils.serialize(branches));

    }

    /**
     * the checkoutFile.
     * @param file the File
     * @param id the ID
     */
    public void checkout(String file, String id) {

        id = getFullCommitID(id);


        File commitFile = new File(".gitlet/commits/" + id);
        if (!commitFile.exists()) {
            System.out.println("No commit with that id exists.");
            return;
        }


        Commit cur = Utils.readObject(commitFile, Commit.class);

        if (cur.getBlobs() == null || cur.getBlobs().size() == 0) {

            return;
        }

        for (Blob curBlob: cur.getBlobs()) {

            if (curBlob.getFileName().equals(file)) {

                Utils.writeContents(new File(file), curBlob.getContents());
                return;
            }
        }

        System.out.println("File does not exist in that commit.");

    }

    /**
     * the checkout.
     * @param file the checkout
     */
    public void checkout(String file) {
        _head = Utils.readObject(HEADFILE, Commit.class);
        if (_head.getBlobs() == null) {
            return;
        }

        for (Blob curBlob: _head.getBlobs()) {
            if (curBlob.getFileName().equals(file)) {
                Utils.writeContents(new File(file), curBlob.getContents());
                return;
            }
        }

        System.out.println("File does not exist in that commit.");


    }

    /**
     * the checkoutBranch.
     * @param branchName the checkoutBranch
     */
    public void checkoutBranch(String branchName) {
        _head = Utils.readObject(HEADFILE, Commit.class);
        branches = Utils.readObject(branchesFile, HashMap.class);
        curBranchName = Utils.readObject(curBranchNameFile, String.class);
        if (!branches.keySet().contains(branchName)) {
            System.out.println("No such branch exists.");
            return;
        } else if (branchName.equals(curBranchName)) {
            System.out.println("No need to checkout the current branch.");
            return;
        }
        Commit branchHead = branches.get(branchName);
        List<Blob> toChangeBlobs = branchHead.getBlobs();
        _head = Utils.readObject(HEADFILE, Commit.class);
        List<Blob> headBlobs = _head.getBlobs();
        ArrayList<String> headNames = new ArrayList<String>();
        for (Blob cur: headBlobs) {
            headNames.add(cur.getFileName());
        }
        for (Blob curCommitBlob: toChangeBlobs) {
            String cwdFileName = curCommitBlob.getFileName();
            File cwdFile = new File(cwdFileName);
            File toStage = new File(".gitlet/staging/toAdd/" + cwdFileName);
            if (cwdFile.exists() && !toStage.exists()
                    && !headNames.contains(cwdFileName)) {
                System.out.println("There is an "
                        + "untracked file in the way; "
                        + "delete it, or add and commit it first.");
                return;
            }
        }
        ArrayList<String> curTrackedFileNames = Commit
                .getCommitFileNames(_head);
        for (String curHeadName: curTrackedFileNames) {
            if (curHeadName.equals(".gitignore")
                    || curHeadName.equals("Makefile")
                    || curHeadName.equals("proj3.iml")) {
                int a = 0;
            } else {
                File toDel = new File(curHeadName);
                if (toDel.exists()) {
                    toDel.delete();
                }
            }
        }
        ArrayList<Blob> branchBlobs = branchHead.getBlobs();

        for (Blob cur: branchBlobs) {
            File toAdd = new File(cur.getFileName());
            Utils.writeContents(toAdd, cur.getContents());
        }

        helper();
        _head = branchHead;
        curBranchName = branchName;
        Utils.writeContents(branchesFile, Utils.serialize(branches));
        Utils.writeContents(curBranchNameFile, Utils.serialize(curBranchName));
        Utils.writeContents(HEADFILE, Utils.serialize(_head));
    }

    /**
     * the global log.
     */
    public void globalLog() {
        List<String> commitFiles = Utils.plainFilenamesIn(COMMITSFOLDER);

        for (String curCommitFileName: commitFiles) {
            File curCommit = new File(".gitlet/commits/" + curCommitFileName);
            Commit cur = Utils.readObject(curCommit, Commit.class);
            System.out.println(cur.toString());
            System.out.println();
        }

    }

    /**
     * the log.
     */
    public void log() {
        _head = Utils.readObject(HEADFILE, Commit.class);

        String builder = "";

        Commit iter = _head;

        while (iter != null) {
            builder += iter.toString();
            builder += "\n";

            iter = iter.getParent();
        }

        System.out.println(builder);

    }

    /**
     * the find.
     * @param findMsg the find
     */
    public void find(String findMsg) {
        List<String> allCommits = Utils.plainFilenamesIn(COMMITSFOLDER);
        boolean none = true;
        for (String curCommitFileName: allCommits) {
            File curCommitFile = new File(
                    Repository.COMMITSFOLDER + "/" + curCommitFileName);

            Commit curCommit = Utils.readObject(curCommitFile, Commit.class);

            if (curCommit.getMsg().equals(findMsg)) {
                System.out.println(curCommit.getId());
                none = false;
            }

        }
        if (none) {
            System.out.println("Found no commit with that message.");
        }


    }

    /**
     * The remove.
     * @param fileName the remove
     */
    public void remove(String fileName) {
        _head = Utils.readObject(HEADFILE, Commit.class);
        File curFile = new File(fileName);
        File checkStageAdd = new File(".gitlet/staging/toAdd/" + fileName);
        boolean err = true;
        if (checkStageAdd.exists()) {
            checkStageAdd.delete();
            err = false;
        }
        for (Blob curBlob: _head.getBlobs()) {
            if (curBlob.getFileName().equals(fileName)) {
                err = false;
                File stageRem = new File(".gitlet/staging/toRem/" + fileName);
                if (!stageRem.exists()) {
                    try {
                        stageRem.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (curFile.exists()) {
                    curFile.delete();
                }
            }
        }

        if (err) {
            System.out.println("No reason to remove the file.");
        }
    }

    /**
     * the branch.
     * @param branchName the branchName
     */
    public void branch(String branchName) {
        branches = Utils.readObject(branchesFile, HashMap.class);
        _head = Utils.readObject(HEADFILE, Commit.class);

        if (branches.keySet().contains(branchName)) {
            System.out.println("A branch with that name already exists.");
            return;
        }

        branches.put(branchName, _head);

        Utils.writeContents(branchesFile, Utils.serialize(branches));
    }

    /**
     * the status branch.
     * @return the branch status
     */
    public ArrayList<String> statusBranchNames() {
        branches = Utils.readObject(branchesFile, HashMap.class);
        _head = Utils.readObject(HEADFILE, Commit.class);
        curBranchName = Utils.readObject(curBranchNameFile, String.class);

        ArrayList<String> branchNamesToPrint = new ArrayList<>();
        Set<String> branchNames = branches.keySet();
        for (String curBranch : branchNames) {
            branchNamesToPrint.add(curBranch);
        }

        branchNamesToPrint.sort(com);

        int i = branchNamesToPrint.indexOf(curBranchName);
        branchNamesToPrint.set(i, "*" + branchNamesToPrint.get(i));
        return branchNamesToPrint;
    }

    /**
     * the stage status.
     * @return the stage of status
     */
    public List<String> statusStageNames() {
        List<String> stageNames = Utils.plainFilenamesIn(STAGETOADD);
        stageNames.sort(com);
        return stageNames;
    }

    /**
     * the rem status.
     * @return the rem status
     */
    public List<String> statusRemName() {
        List<String> stageNames = Utils.plainFilenamesIn(STAGETOREM);
        stageNames.sort(com);
        return stageNames;
    }

    /**
     * the status.
     */
    public void status() {
        branches = Utils.readObject(branchesFile, HashMap.class);
        _head = Utils.readObject(HEADFILE, Commit.class);
        curBranchName = Utils.readObject(curBranchNameFile, String.class);

        ArrayList<String> branchNamesToPrint = statusBranchNames();
        List<String> add = statusStageNames();
        List<String> rem = statusRemName();

        List<String> cwdF = Utils.plainFilenamesIn(".");
        ArrayList<Blob> headBlobs = _head.getBlobs();
        HashMap<String, String> contents = new HashMap<String, String>();
        for (Blob cur: headBlobs) {
            contents.put(cur.getFileName(), cur.getContents());
        }

        String builder = "=== Branches ===\n";
        for (String cur : branchNamesToPrint) {
            builder += cur + "\n";
        }
        builder += "\n=== Staged Files ===\n";

        for (String cur: add) {
            builder += cur + "\n";
        }

        builder += "\n=== Removed Files ===\n";
        for (String cur: rem) {
            builder += cur + "\n";
        }

        builder += "\n=== Modifications Not Staged For Commit ===\n";

        builder += "\n=== Untracked Files ===\n";

        System.out.println(builder);

    }

    /**
     * the rm Branch.
     * @param branchName the branchName
     */
    public void removeBranch(String branchName) {
        branches = Utils.readObject(branchesFile, HashMap.class);
        _head = Utils.readObject(HEADFILE, Commit.class);
        curBranchName = Utils.readObject(curBranchNameFile, String.class);

        if (!branches.keySet().contains(branchName)) {
            System.out.println("A branch with that name does not exist.");
            return;
        } else if (branchName.equals(curBranchName)) {
            System.out.println("Cannot remove the current branch.");
            return;
        }

        branches.remove(branchName);
        Utils.writeContents(branchesFile, Utils.serialize(branches));

    }

    /**
     * the reset.
     * @param commitId the commidID
     */
    public void reset(String commitId) {
        commitId = getFullCommitID(commitId);
        curBranchName = Utils.readObject(curBranchNameFile, String.class);
        File commitFile = new File(".gitlet/commits/" + commitId);
        if (!commitFile.exists()) {
            System.out.println("No commit with that id exists.");
            return;
        }
        branches = Utils.readObject(branchesFile, HashMap.class);
        _head = Utils.readObject(HEADFILE, Commit.class);
        Commit toChange = Utils.readObject(commitFile, Commit.class);
        List<Blob> toChangeBlobs = toChange.getBlobs();
        _head = Utils.readObject(HEADFILE, Commit.class);
        List<Blob> headBlobs = _head.getBlobs();
        ArrayList<String> headNames = new ArrayList<String>();
        for (Blob cur: headBlobs) {
            headNames.add(cur.getFileName());
        }
        for (Blob curCommitBlob: toChangeBlobs) {
            String cwdFileName = curCommitBlob.getFileName();
            File cwdFile = new File(cwdFileName);
            File toStage = new File(".gitlet/staging/toAdd/" + cwdFileName);
            if (cwdFile.exists()
                    && !toStage.exists() && !headNames.contains(cwdFileName)) {
                System.out.println("There is an untracked file in the way; "
                        + "delete it, or add and commit it first.");
                return;
            }
        }
        ArrayList<String> curTrackedFileNames = Commit
                .getCommitFileNames(_head);
        for (String curHeadName: curTrackedFileNames) {
            if (curHeadName.equals(".gitignore")
                    || curHeadName.equals("Makefile")
                    || curHeadName.equals("proj3.iml")) {
                int a = 0;
            } else {
                File toDel = new File(curHeadName);
                if (toDel.exists()) {
                    toDel.delete();
                }
            }
        }
        ArrayList<Blob> branchBlobs = toChange.getBlobs();
        for (Blob cur: branchBlobs) {
            File toAdd = new File(cur.fileName);
            Utils.writeContents(toAdd, cur.contents);
        }
        helper();
        branches.put(curBranchName, toChange);
        _head = toChange;
        Utils.writeContents(branchesFile, Utils.serialize(branches));
        Utils.writeContents(curBranchNameFile, Utils.serialize(curBranchName));
        Utils.writeContents(HEADFILE, Utils.serialize(_head));
    }

    /**
     * yes.
     */
    private void helper() {
        for (String curFileName
                : Utils.plainFilenamesIn(Repository.STAGETOADD)) {
            File toDel = new File(Repository.STAGETOADD + "/" + curFileName);
            toDel.delete();
        }
        for (String curFileName
                : Utils.plainFilenamesIn(Repository.STAGETOREM)) {
            File toDel = new File(Repository.STAGETOREM + "/" + curFileName);
            toDel.delete();
        }
    }

    /**
     * the full commit id.
     * @param abb the abb
     * @return the return
     */
    public static String getFullCommitID(String abb) {
        List<String> commitFiles = Utils.plainFilenamesIn(".gitlet/commits");
        for (String curCommit : commitFiles) {
            if (curCommit.startsWith(abb)) {
                return curCommit;
            }
        }
        return abb;
    }

    /**
     * e.
     * @param branchName e
     */
    public void persist(String branchName) {
        curBranchName = Utils.readObject(curBranchNameFile, String.class);
        branches = Utils.readObject(branchesFile, HashMap.class);
        _head = Utils.readObject(HEADFILE, Commit.class);
        if (Utils.plainFilenamesIn(STAGETOADD).size() != 0
                || Utils.plainFilenamesIn(STAGETOREM).size() != 0) {
            System.out.println("You have uncommitted changes.");
        }
        if (!branches.keySet().contains(branchName)) {
            System.out.println("A branch with that name does not exist.");
            return;
        } else if (branchName.equals(curBranchName)) {
            System.out.println("Cannot merge a branch with itself.");
            return;
        }
    }

    /**
     * e.
     * @param branchHead e
     */
    public void persister(Commit branchHead) {
        ArrayList<Blob> headBlobs = _head.getBlobs();
        ArrayList<String> headNames = new ArrayList<String>();
        for (Blob cur: headBlobs) {
            headNames.add(cur.fileName);
        }
        List<Blob> toChangeBlobs = branchHead.getBlobs();
        for (Blob curCommitBlob: toChangeBlobs) {
            String cwdFileName = curCommitBlob.fileName;
            File cwdFile = new File(cwdFileName);
            File toStage = new File(".gitlet/staging/toAdd/" + cwdFileName);
            if (cwdFile.exists() && !toStage.exists()
                    && !headNames.contains(cwdFileName)) {
                System.out.println("There is an untracked file in the way; "
                        + "delete it, or add and commit it first.");
                return;
            }
        }
    }

    /**
     * the merge.
     * @param branchName the branchname
     */
    public void merge(String branchName) {
        persist(branchName);
        Commit branchHead = branches.get(branchName);
        persister(branchHead);
        Commit split = getSplit(_head, branchHead);
        ArrayList<Commit> headParent = getAllParentsOf(_head);
        if (split == _head) {
            checkoutBranch(branchName);
            System.out.println("Current branch fast-forwarded");
            return;
        }
        if (headParent.contains(branchHead)) {
            System.out.println("Given "
                    + "branch is an ancestor of the current branch");
        }
        if (split == branchHead) {
            System.out.println("Given branch "
                    + "is an ancestor of the current branch");
            return;
        }

        ArrayList<Blob> splitBlobs = split.getBlobs();
        HashMap<String, String> splitFileNames = new HashMap<String, String>();
        for (Blob cur : splitBlobs) {
            splitFileNames.put(cur.fileName, cur.contents);
        }

        merge2(split, branchHead, branchName, splitFileNames);
    }

    /**
     * e.
     * @param split e
     * @param branchHead e
     * @param branchName e
     * @param splitFileNames e
     */
    public void merge2(Commit split, Commit branchHead, String branchName,
                       HashMap<String, String> splitFileNames) {
        ArrayList<Blob> curBlobs = _head.getBlobs();
        HashMap<String, String> curFileNames = new HashMap<String, String>();
        for (Blob cur : curBlobs) {
            curFileNames.put(cur.fileName, cur.contents);
        }
        ArrayList<Blob> branchBlobs = branchHead.getBlobs();
        HashMap<String, String> branchFiles = new HashMap<String, String>();
        for (Blob cur : branchBlobs) {
            branchFiles.put(cur.fileName, cur.contents);
        }
        boolean conflict = false;
        for (Blob curBranchBlob : branchBlobs) {
            String branchFileName = curBranchBlob.fileName;
            String branchFileContents = curBranchBlob.contents;
            if (splitFileNames.containsKey(branchFileName)
                    && !splitFileNames.get(branchFileName)
                    .equals(branchFileContents)) {
                if (curFileNames.containsKey(branchFileName)
                        && curFileNames.get(branchFileName)
                        .equals(splitFileNames.get(branchFileName))) {
                    checkout(branchFileName, branchHead.getId());
                    add(branchFileName);
                }
                if (curFileNames.containsKey(branchFileName)
                        && !curFileNames.get(branchFileName)
                        .equals(branchFileContents)) {
                    String builder = "<<<<<<< HEAD\n"
                            + curFileNames.get(branchFileName)
                            + "=======\n";
                    builder += branchFileContents + ">>>>>>>\n";
                    Utils.writeContents(new File(branchFileName), builder);
                    add(branchFileName);
                    conflict = true;
                }
                if (!curFileNames.containsKey(branchFileName)) {
                    String builder = "<<<<<<< HEAD\n" + "" + "=======\n";
                    builder += branchFileContents + ">>>>>>>\n";
                    Utils.writeContents(new File(branchFileName), builder);
                    add(branchFileName);
                    conflict = true;
                }
            }
            if (!splitFileNames.containsKey(branchFileName)) {
                if (curFileNames.containsKey(branchFileName)) {
                    if (!curFileNames.get(branchFileName)
                            .equals(branchFileContents)) {
                        conflict(new File(branchFileName), curFileNames
                                .get(branchFileName), branchFileContents);
                        conflict = true;
                    }
                } else {
                    checkout(branchFileName, branchHead.getId());
                    add(branchFileName);
                }
            }
        }
        merge3(curBlobs, splitFileNames, branchFiles,
                conflict, branchName, branchHead);
    }

    /**
     * e.
     * @param curBlobs e
     * @param splitFileNames e
     * @param branchFiles e
     * @param conflict e
     * @param branchName e
     * @param branchHead e
     */
    public void merge3(ArrayList<Blob> curBlobs,
                       HashMap<String, String> splitFileNames,
                       HashMap<String, String> branchFiles,
                       boolean conflict, String branchName,
                       Commit branchHead) {
        for (Blob curFileBlob : curBlobs) {
            String curFileName = curFileBlob.fileName;
            String curFileContents = curFileBlob.contents;
            if (splitFileNames.containsKey(curFileName)
                    && !branchFiles.containsKey(curFileName)) {
                if (splitFileNames.get(curFileName).equals(curFileContents)) {
                    remove(curFileName);
                } else {
                    conflict(new File(curFileName), curFileContents, "");
                    conflict = true;
                }
            }
        }
        if (conflict) {
            System.out.println("Encountered a merge conflict.");
        }
        String msg = "Merged " + branchName + " into " + curBranchName + ".";
        makeCommit(branchHead, msg);
    }

    /**
     * e.
     * @param branchHead t
     * @param msg t
     */
    public void makeCommit(Commit branchHead, String msg) {
        Commit newCommit = new Commit(_head, branchHead, msg);
        File newF = new File(COMMITSFOLDER + "/" + newCommit.getId() + ".txt");
        try {
            newF.createNewFile();
            Utils.writeContents(newF, Utils.serialize(newCommit));
        } catch (IOException e) {
            e.printStackTrace();
        }
        writeOuter(newCommit);
    }

    /**
     * t.
     * @param newCommit tn
     */
    public void writeOuter(Commit newCommit) {
        branches.put(curBranchName, newCommit);
        _head = newCommit;
        Utils.writeContents(HEADFILE, Utils.serialize(_head));
        Utils.writeContents(branchesFile, Utils.serialize(branches));
    }

    /**
     * hi.
     * @param fileName t
     * @param str1 t
     * @param str2 t
     */
    public void conflict(File fileName, String str1, String str2) {
        String builder = "<<<<<<< HEAD\n" + str1 + "=======\n";
        builder += str2 + ">>>>>>>\n";
        Utils.writeContents(fileName, builder);
        add(fileName + "");
    }

    /**
     * The get split.
     * @param curHead the cur head
     * @param givenHead the given head
     * @return the return value
     */
    public static Commit getSplit(Commit curHead, Commit givenHead) {
        HashMap<Commit, Integer> thisParents = new HashMap<Commit, Integer>();
        getParents(0, curHead, thisParents);

        HashMap<Commit, Integer> givenParents = new HashMap<Commit, Integer>();
        getParents(0, givenHead, givenParents);

        int lowDist = 0;

        while (true) {
            for (Commit cur: thisParents.keySet()) {
                if (thisParents.get(cur) == lowDist
                        && givenParents.containsKey(cur)) {
                    return cur;
                }
            }
            lowDist++;
        }


    }

    /**
     * get all parents of commit.
     * @param curDepth the current depth of search
     * @param child the child
     * @param dists the distances
     */
    public static void getParents(int curDepth, Commit child,
                                  HashMap<Commit, Integer> dists) {
        if (dists.containsKey(child) && dists.get(child) <= curDepth) {
            return;
        }

        dists.put(child, curDepth);
        if (child.getParent() != null) {
            getParents(curDepth + 1, child.getParent(), dists);
        }

        if (child.getParent2() != null) {
            getParents(curDepth + 1, child.getParent2(), dists);
        }


    }

    /**
     * the split of the commits.
     * @param curHead the cur head
     * @param thisParents the parents
     * @param givenParents their parents.
     * @return the splitpoint of the commits.
     */
    public static Commit getSplit(Commit curHead, ArrayList<Commit> thisParents,
                                  ArrayList<Commit> givenParents) {
        ArrayList<Commit> commons = getCommons(thisParents, givenParents);
        Commit lowCommit = curHead;
        int lowDist = Integer.MAX_VALUE;
        for (Commit cur: commons) {
            int curDist = getDist(curHead, cur);
            if (curDist < lowDist) {
                lowDist = curDist;
                lowCommit = cur;
            }
        }

        return lowCommit;

    }

    /**
     * the dist between child and target.
     * @param child the child
     * @param tar the target
     * @return the distance
     */
    public static int getDist(Commit child, Commit tar) {
        if (child == null || tar == null) {
            return -1;
        }
        if (child == tar) {
            return 0;
        }
        int par1 = getDist(child.getParent(), tar);
        int par2 = getDist(child.getParent2(), tar);
        if (par1 == -1 && par2 == -1) {
            return -1;
        }

        if (par1 == -1) {
            return par2 + 1;
        }

        if (par2 == -1) {
            return par1 + 1;
        } else {
            return Math.min(par1, par2);
        }
    }

    /**
     * The commons of parents.
     * @param thisParents the parents
     * @param givenParents their parents
     * @return the commons of parents.
     */
    public static ArrayList<Commit> getCommons(ArrayList<Commit> thisParents,
                                               ArrayList<Commit> givenParents) {
        ArrayList<Commit> returner = new ArrayList<Commit>();
        for (Commit curCommit: thisParents) {
            if (givenParents.contains(curCommit)) {
                returner.add(curCommit);
            }
        }

        return returner;
    }

    /**
     * Gets all parents of child.
     * @param child the child
     * @return the parents
     */
    public static ArrayList<Commit> getAllParentsOf(Commit child) {
        if (child == null) {
            return new ArrayList<Commit>();
        }
        ArrayList<Commit> returner = new ArrayList<Commit>();
        if (child != null) {
            returner.add(child);
        }
        if (child.getDist() < 1) {
            return returner;
        }

        if (child.getParent2() != null
                && !returner.contains(child.getParent2())) {
            returner.addAll(getAllParentsOf(child.getParent2()));
        }

        if (child.getParent() != null
                && !returner.contains(child.getParent())) {
            returner.addAll(getAllParentsOf(child.getParent()));
        }

        return returner;
    }

}
