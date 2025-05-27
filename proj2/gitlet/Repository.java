package gitlet;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Lyrine Yang
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    private static final File OBJECTS_DIR = join(GITLET_DIR, "objects");
    private static final File COMMITS_DIR = join(OBJECTS_DIR, "commits");
    private static final File BLOBS_DIR = join(OBJECTS_DIR, "blobs");
    private static final File HEAD_FILE = join(GITLET_DIR, "HEAD");
    private static final File INDEX_FILE = join(GITLET_DIR, "index");
    private static final File BRANCHES_DIR = join(GITLET_DIR, "branches");
    private static final File MASTER_FILE = join(BRANCHES_DIR, "master");
    private static final String DELETE_MARKER = "DELETE_FILE";
    /* TODO: fill in the rest of this class. */

    /** to create the gitlet directory and the file structure */
    public static void init() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            return;
        }
        setupPersistence();
        /* create the first default commit */
        Date epochTime = new Date(0L);
        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        Commit initialCommit = new Commit(null, "initial commit", formatter.format(epochTime));
        String initialCommitID = sha1(initialCommit);
        File initialCommitFile = join(COMMITS_DIR, initialCommitID);
        writeObject(initialCommitFile, initialCommit);

        /* build up the branch structure by create branch file and the HEAD file */
        /* make branch file hold the branch new commitID */
        writeContents(MASTER_FILE, initialCommitID);
        /* make HEAD file hold the working branch information */
        writeContents(HEAD_FILE, "master");
    }
    /** help method to build up the gitlet directory structure */
    private static void setupPersistence() {
        GITLET_DIR.mkdir();
        OBJECTS_DIR.mkdir();
        COMMITS_DIR.mkdir();
        BLOBS_DIR.mkdir();
        BRANCHES_DIR.mkdir();
        try {
            HEAD_FILE.createNewFile();
            INDEX_FILE.createNewFile();
            HashMap<String, String> stagingArea = new HashMap<>();
            writeObject(INDEX_FILE, stagingArea);
            MASTER_FILE.createNewFile();
        } catch (IOException excp) {
            throw new RuntimeException(excp);
        }
    }
    /** add the file need to add to blobs directory and add the key-value to index */
    public static void add(String fileName) {
        File fileToAdd = join(CWD, fileName);
        if (!fileToAdd.exists()) {
            System.out.println("File does not exist.");
            return;
        }
        /* get the content of the file needed to add */
        byte[] fileToAddContent = readContents(fileToAdd);
        String blobID = sha1(fileToAddContent);

        /* inverse serialize the index map from index file */
        HashMap<String, String> stagingArea = readObject(INDEX_FILE, HashMap.class);
        Commit headCommit = getHeadCommit();

        /* check whether current working version of the file is identical to the version in the current commit */
        if (headCommit.nameIDMap.containsKey(fileName) && headCommit.nameIDMap.get(fileName).equals(blobID)) {
            if (stagingArea.containsKey(fileName)) {
                stagingArea.remove(fileName);
                writeObject(INDEX_FILE, stagingArea);
            }
            return;
        }
        /* add addFile content to blobs directory */
        if (!join(BLOBS_DIR, blobID).exists()) {
            Blob addBlob = new Blob(fileToAddContent);
            writeObject(join(BLOBS_DIR, blobID), addBlob);
        }
        /* add the addFile blob pointer to index map */
        stagingArea.put(fileName, blobID);
        writeObject(INDEX_FILE, stagingArea);
    }
    /** get the current commit object */
    private static Commit getHeadCommit() {
        File branchFile = getWorkingBranchFile();
        return readObject((join(COMMITS_DIR, readContentsAsString(branchFile))), Commit.class);
    }

    public static void commit(String commitMessage) {
        if (commitMessage.trim().isEmpty()) {
            System.out.println("Please enter a commit message.");
        }
        /* check if there is file in staging area to commit */
        HashMap<String, String> stagingAreaMap = readObject(INDEX_FILE, HashMap.class);
        if (stagingAreaMap.isEmpty()) {
            System.out.println("No changes added to the commit.");
            return;
        }
        Commit headCommit = getHeadCommit();
        Commit newCommit = new Commit(sha1(headCommit), commitMessage, getTimeStampString());

        /* load the headCommit map and put the staging area: the stagingAreaMap */
        newCommit.nameIDMap = new HashMap<>(headCommit.nameIDMap);
        for (HashMap.Entry<String, String> entry : stagingAreaMap.entrySet()) {
            String fileName = entry.getKey();
            String blobID = entry.getValue();
            /* if there are file need to delete, remove it from nameIDMap to cancel tracking of it */
            if (blobID.equals(DELETE_MARKER)) {
                newCommit.nameIDMap.remove(fileName);
            } else {
                newCommit.nameIDMap.put(fileName, blobID);
            }
        }

        /* build the newCommit File in Commits directory to save it */
        String newCommitID = sha1(newCommit);
        File newCommitFile = join(COMMITS_DIR, newCommitID);
        writeObject(newCommitFile, newCommit);

        /* clean the stagingArea */
        stagingAreaMap = new HashMap<>();
        writeObject(INDEX_FILE, stagingAreaMap);

        /* make the HEAD pointer to point at the new commit */
        File branchFile = getWorkingBranchFile();
        writeContents(branchFile, newCommitID);

    }
    private static File getWorkingBranchFile() {
        return join(BRANCHES_DIR, readContentsAsString(HEAD_FILE));
    }
    private static String getTimeStampString() {
        Date timeStamp = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        return formatter.format(timeStamp);
    }
    public static void remove(String fileName) {
        /* get the head commit and staging area map */
        boolean stagingAreaChanged = false;
        Commit headCommit = getHeadCommit();
        HashMap<String, String> stagingArea = readObject(INDEX_FILE, HashMap.class);
        if (stagingArea.containsKey(fileName) && !stagingArea.get(fileName).equals(DELETE_MARKER)) {
            stagingArea.remove(fileName);
            stagingAreaChanged = true;
        }
        if (headCommit.nameIDMap.containsKey(fileName)) {
            stagingArea.put(fileName, DELETE_MARKER);
            stagingAreaChanged = true;
            File CWDFileToRm = join(CWD, fileName);
            if (CWDFileToRm.exists()) {
                restrictedDelete(CWDFileToRm);
            }
        }
        if (!stagingAreaChanged) {
            System.out.println("No reason to remove the file.");
            return;
        }
        writeObject(INDEX_FILE, stagingArea);
    }
    public static void log() {
        Commit headCommit = getHeadCommit();
        logHelper(headCommit, sha1(headCommit));
    }
    private static void logHelper(Commit currentCommit, String commitID) {
        String parentCommitID = currentCommit.parentID;
        logPrintHelper(currentCommit, commitID);
        if (parentCommitID == null) {
            return;
        }
        File parentCommit = join(COMMITS_DIR, parentCommitID);
        logHelper(readObject(parentCommit, Commit.class), parentCommitID);
    }

    private static void logPrintHelper(Commit currentCommit, String commitID) {
        System.out.println("===");
        System.out.println("commit " + commitID);
        if (currentCommit.secondParentID != null) {
            System.out.println("Merge " + currentCommit.parentID.substring(0, 7) + " " + currentCommit.secondParentID.substring(0, 7));
        }
        System.out.println("Date: " + currentCommit.timeStamp);
        System.out.println(currentCommit.commitMessage);
        System.out.println();
    }

    public static void globalLog() {
        List<String> commitIDList = plainFilenamesIn(COMMITS_DIR);
        if (commitIDList != null) {
            for (String commitID: commitIDList) {
                Commit currentCommit = readObject(join(COMMITS_DIR, commitID), Commit.class);
                logPrintHelper(currentCommit, commitID);
            }
        }
    }

    public static void find(String commitMessageToFind) {
        List<String> commitIDList = plainFilenamesIn(COMMITS_DIR);
        boolean commitMessageExist = false;
        if (commitIDList != null) {
            for (String commitID : commitIDList) {
                Commit currentCommit = readObject(join(COMMITS_DIR, commitID), Commit.class);
                if (currentCommit.commitMessage.equals(commitMessageToFind)) {
                    System.out.println(commitID);
                    commitMessageExist = true;
                }
            }
        }
        if (!commitMessageExist) {
            System.out.println("Found no commit with that message.");
        }
    }

    public static void status() {
        System.out.println("=== " + "Branches" + " ===");
        List<String> branchesList = plainFilenamesIn(BRANCHES_DIR);
        if (branchesList != null) {
            for (String branch: branchesList) {
                if (branch.equals(readContentsAsString(HEAD_FILE))) {
                    System.out.println("*" + branch);
                } else {
                    System.out.println(branch);
                }
            }
        }
        System.out.println();
        System.out.println("=== " + "Staged Files" + " ===");
        HashMap<String, String> stagingArea = readObject(INDEX_FILE, HashMap.class);
        Set<String> stagingAreaKeys = stagingArea.keySet();
        for (String key: stagingAreaKeys) {
            if (!stagingArea.get(key).equals(DELETE_MARKER)) {
                System.out.println(key);
            }
        }
        System.out.println();
        System.out.println("=== " + "Removed Files" + " ===");
        for (String key: stagingAreaKeys) {
            if (stagingArea.get(key).equals(DELETE_MARKER)) {
                System.out.println(key);
            }
        }
        System.out.println();
        System.out.println("=== " + "Modifications Not Staged For Commit" + " ===");
        System.out.println();
        System.out.println("=== " + "Untracked Files" + " ===");
        List<String> untrackedFileList = getUntrackedFileList();
        for (String fileName: untrackedFileList) {
            System.out.println(fileName);
        }
        System.out.println();
    }
    public static void checkOut(String[] args) {
        if (Main.argsCheck(args, 3)) {
            checkOutHeadCommit(args[2]);
        } else if (Main.argsCheck(args, 4)) {
            checkOutSpecialCommit(args[1], args[3]);
        } else if (Main.argsCheck(args, 2)){
            checkOutBranch(args[1]);
        }
    }
    private static void checkOutHeadCommit(String fileName) {
        Commit headCommit = getHeadCommit();
        checkOutSpecialCommit(sha1(headCommit), fileName);
    }
    private static void checkOutSpecialCommit(String commitID, String fileName) {
        File specialCommitFile = join(COMMITS_DIR, commitID);
        if (!specialCommitFile.exists()) {
            System.out.println("No commit with that id exists.");
            return;
        }
        Commit specialCommit = readObject(specialCommitFile, Commit.class);
        if (!specialCommit.nameIDMap.containsKey(fileName)) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        String blobID = specialCommit.nameIDMap.get(fileName);
        restoreFromID(fileName, blobID);
    }
    private static void checkOutBranch(String givenBranchName) {
        File givenBranchFile = join(BRANCHES_DIR, givenBranchName);
        Commit givenHeadCommit = readObject(join(COMMITS_DIR, readContentsAsString(givenBranchFile)), Commit.class);
        if (!givenBranchFile.exists()) {
            System.out.println("No such branch exists.");
            return;
        } else if (givenBranchName.equals(readContentsAsString(HEAD_FILE))) {
            System.out.println("No need to checkout the current branch.");
            return;
        } else if (!getUntrackedFileList().isEmpty() && checkUntrackedFileOverwritten(getUntrackedFileList(), givenHeadCommit.nameIDMap)) {
            System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
            return;
        }

        checkOutFilesInCommit(givenHeadCommit);
        /* delete the file be tracked in headCommit but not tracked in given headCommit */
        for (String fileName: getHeadCommit().nameIDMap.keySet()) {
            if (!givenHeadCommit.nameIDMap.containsKey(fileName)) {
                restrictedDelete(join(CWD, fileName));
            }
        }
        /* view the given branch as working branch */
        writeContents(HEAD_FILE, givenBranchName);

        /* clean up the staging area */
        writeObject(INDEX_FILE, new HashMap<>());
    }
    /** check out all files in given Commit into CWD */
    private static void checkOutFilesInCommit(Commit givenCommit) {
        for (HashMap.Entry<String, String> entry : givenCommit.nameIDMap.entrySet()) {
            String fileName = entry.getKey();
            String blobID = entry.getValue();
            restoreFromID(fileName, blobID);
        }
    }

    /* check if the untrackedFile is tracked in given head commit map and will be overWritten*/
    private static boolean checkUntrackedFileOverwritten(List<String> untrackedFile, HashMap<String, String> Map) {
        for (String fileName: untrackedFile) {
            if (Map.containsKey(fileName)) {
                return true;
            }
        }
        return false;
    }
    private static void restoreFromID(String fileName, String blobID) {
        File fileToCheckOut = join(CWD, fileName);
        Blob checkOutBlob = readObject(join(BLOBS_DIR, blobID), Blob.class);
        writeContents(fileToCheckOut, checkOutBlob.getContent());
    }
    /* get the untracked file list in CWD */
    private static List<String> getUntrackedFileList() {
        List<String> filesInCWD = plainFilenamesIn(CWD);
        if (filesInCWD == null) {
            return new ArrayList<>();
        }
        List<String> untrackedFileList = new ArrayList<>();
        HashMap<String, String> stagingArea = readObject(INDEX_FILE, HashMap.class);
        Set<String> stagingAreaKeys = stagingArea.keySet();
        Set<String> headCommitMaps = getHeadCommit().nameIDMap.keySet();
        for (String fileName : filesInCWD) {
            if (!headCommitMaps.contains(fileName) && !stagingAreaKeys.contains(fileName)) {
                untrackedFileList.add(fileName);
            }
        }
        return untrackedFileList;
    }

    public static void branch(String branchName) {
        File newBranch = join(BRANCHES_DIR, branchName);
        if (newBranch.exists()) {
            System.out.println("A branch with that name already exists.");
        }
        try {
            newBranch.createNewFile();
        } catch (IOException excp) {
            throw new RuntimeException(excp);
        }
        Commit headCommit = getHeadCommit();
        writeContents(newBranch, sha1(headCommit));
    }
    public static void rmBranch(String rmBranchName) {
        File rmBranchFile = join(BRANCHES_DIR, rmBranchName);
        if (!rmBranchFile.exists()) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        if (rmBranchName.equals(readContentsAsString(HEAD_FILE))) {
            System.out.println("Cannot remove the current branch.");
            return;
        }
        restrictedDelete(rmBranchFile);
    }

    public static void reset(String resetCommitID) {
        File resetCommitFile = join(COMMITS_DIR, resetCommitID);
        if (!resetCommitFile.exists()) {
            System.out.println("No commit with that id exists.");
        }
        Commit resetCommit = readObject(resetCommitFile, Commit.class);
        if (!getUntrackedFileList().isEmpty() && checkUntrackedFileOverwritten(getUntrackedFileList(), resetCommit.nameIDMap)) {
            System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
        }
        checkOutFilesInCommit(resetCommit);
        writeContents(join(BRANCHES_DIR, readContentsAsString(HEAD_FILE)), resetCommitID);
        writeObject(INDEX_FILE, new HashMap<>());
    }
}
