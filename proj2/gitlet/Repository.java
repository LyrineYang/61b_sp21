package gitlet;

import static gitlet.Utils.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/** Represents a gitlet repository. Includes file path in gitlet directory and implements of gitlet commands by
 *  store information in files.
 *  does at a high level.
 *
 *  @author Lyrine Yang
 */
public class Repository {
    /**
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
        String initialCommitID = sha1(serialize(initialCommit));
        File initialCommitFile = join(COMMITS_DIR, initialCommitID);
        writeObject(initialCommitFile, initialCommit);

        /* build up the branch structure by create branch file and the HEAD file */
        /* make branch file hold the branch new commitID */
        writeContents(MASTER_FILE, initialCommitID);
        /* make HEAD file hold the working branch information */
        writeContents(HEAD_FILE, "master");
    }

    /** build up the gitlet directory structure and store new hashmap in index file*/
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
        Commit headCommit = getBranchHeadCommit(readContentsAsString(HEAD_FILE));

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

    /** get commit object according its commitID */
    private static Commit getCommitByID(String commitID) {
        return readObject(join(COMMITS_DIR, commitID), Commit.class);
    }

    /** get the given branch head commit object */
    private static Commit getBranchHeadCommit(String branchName) {
        File branchFile = join(BRANCHES_DIR, branchName);
        return getCommitByID(readContentsAsString(branchFile));
    }
    public static void commit(String commitMessage) {
        commit(commitMessage, null);
    }
    private static void commit(String commitMessage, String secondParentCommitID) {
        if (commitMessage.trim().isEmpty()) {
            System.out.println("Please enter a commit message.");
            return;
        }
        /* check if there is file in staging area to commit */
        HashMap<String, String> stagingAreaMap = readObject(INDEX_FILE, HashMap.class);
        if (stagingAreaMap.isEmpty() && secondParentCommitID == null) {
            System.out.println("No changes added to the commit.");
            return;
        }
        Commit headCommit = getBranchHeadCommit(readContentsAsString(HEAD_FILE));
        Commit newCommit = new Commit(sha1(serialize(headCommit)), commitMessage, getTimeStampString(), secondParentCommitID);

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
        String newCommitID = sha1(serialize(newCommit));
        File newCommitFile = join(COMMITS_DIR, newCommitID);
        writeObject(newCommitFile, newCommit);

        /* clean the stagingArea */
        writeObject(INDEX_FILE, new HashMap<String, String>());

        /* make the HEAD pointer to point at the new commit */
        writeContents(join(BRANCHES_DIR, readContentsAsString(HEAD_FILE)), newCommitID);

    }
    private static String getTimeStampString() {
        Date timeStamp = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        return formatter.format(timeStamp);
    }
    public static void remove(String fileName) {
        /* get the head commit and staging area map */
        boolean stagingAreaChanged = false;
        Commit headCommit = getBranchHeadCommit(readContentsAsString(HEAD_FILE));
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
        Commit headCommit = getBranchHeadCommit(readContentsAsString(HEAD_FILE));
        logHelper(headCommit, sha1(serialize(headCommit)));
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
    private static String findFullCommitIDFromShort(String shortId) {
    if (shortId == null || shortId.length() > UID_LENGTH) { // UID_LENGTH from Utils
        return null; // Or invalid
    }
    if (shortId.length() == UID_LENGTH) { // Already a full ID
        if (join(COMMITS_DIR, shortId).exists()) {
            return shortId;
        }
        return null;
    }

    List<String> commitIds = plainFilenamesIn(COMMITS_DIR);
    if (commitIds == null) {
        return null;
    }

    String foundId = null;
    int matchCount = 0;
    for (String id : commitIds) {
        if (id.startsWith(shortId)) {
            matchCount++;
            foundId = id;
        }
    }

    if (matchCount == 1) {
        return foundId;
    } else if (matchCount > 1) {
        // Handle ambiguous short ID - spec might say what to do, or just fail
        // For now, returning null for "not found / ambiguous"
        return null;
    }
    return null; // No match
}
    public static void checkOut(String[] args) {
        if (args.length == 3 && args[1].equals("--")) {
            checkOutHeadCommit(args[2]);
        } else if (args.length == 4 && args[2].equals("--")) {
            checkOutSpecialCommit(args[1], args[3]);
        } else if (args.length == 2 ){
            checkOutBranch(args[1]);
        }
    }
    private static void checkOutHeadCommit(String fileName) {
        Commit headCommit = getBranchHeadCommit(readContentsAsString(HEAD_FILE));
        if (!headCommit.nameIDMap.containsKey(fileName)) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        String blobID = headCommit.nameIDMap.get(fileName);
        checkOutFile(fileName, blobID);
    }


    private static void checkOutSpecialCommit(String commitID, String fileName) {
    String fullCommitID = findFullCommitIDFromShort(commitID); // ★ 新增调用
    if (fullCommitID == null) {
        System.out.println("No commit with that id exists.");
        return;
    }

    File specialCommitFile = join(COMMITS_DIR, fullCommitID); // 使用完整ID
    // if (!specialCommitFile.exists()) { // 这个检查其实被 findFullCommitIDFromShort 包含了
    //     System.out.println("No commit with that id exists.");
    //     return;
    // }
    Commit specialCommit = readObject(specialCommitFile, Commit.class);
    if (!specialCommit.nameIDMap.containsKey(fileName)) {
        System.out.println("File does not exist in that commit.");
        return;
    }
    String blobID = specialCommit.nameIDMap.get(fileName);
    checkOutFile(fileName, blobID);
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
        for (String fileName: getBranchHeadCommit(readContentsAsString(HEAD_FILE)).nameIDMap.keySet()) {
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
            checkOutFile(fileName, blobID);
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
    private static void checkOutFile(String fileName, String blobID) {
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
        Set<String> headCommitMaps = getBranchHeadCommit(readContentsAsString(HEAD_FILE)).nameIDMap.keySet();
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
            return;
        }
        try {
            newBranch.createNewFile();
        } catch (IOException excp) {
            throw new RuntimeException(excp);
        }
        Commit headCommit = getBranchHeadCommit(readContentsAsString(HEAD_FILE));
        writeContents(newBranch, sha1(serialize(headCommit)));
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
            return;
        }
        Commit resetCommit = readObject(resetCommitFile, Commit.class);
        if (!getUntrackedFileList().isEmpty() && checkUntrackedFileOverwritten(getUntrackedFileList(), resetCommit.nameIDMap)) {
            System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
            return;
        }
        checkOutFilesInCommit(resetCommit);
        Commit headCommit = getBranchHeadCommit(readContentsAsString(HEAD_FILE));
        for (String fileName: headCommit.nameIDMap.keySet()) {
            if(!resetCommit.nameIDMap.containsKey(fileName)) {
                restrictedDelete(join(CWD, fileName));
            }
        }
        writeContents(join(BRANCHES_DIR, readContentsAsString(HEAD_FILE)), resetCommitID);
        writeObject(INDEX_FILE, new HashMap<>());
    }
    public static void merge(String givenBranchName) {
        HashMap<String, String> stagingArea = readObject(INDEX_FILE, HashMap.class);
        if (!stagingArea.isEmpty()) {
            System.out.println("You have uncommitted changes.");
            return;
        }
        File branchFile = join(BRANCHES_DIR, givenBranchName);
        if (!branchFile.exists()) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        if (givenBranchName.equals(readContentsAsString(HEAD_FILE))) {
            System.out.println("Cannot merge a branch with itself.");
            return;
        }
        Commit givenBranchHeadCommit = getBranchHeadCommit(givenBranchName);
        Commit headCommit = getBranchHeadCommit(readContentsAsString(HEAD_FILE));
        if (!getUntrackedFileList().isEmpty() && checkUntrackedFileOverwritten(getUntrackedFileList(), givenBranchHeadCommit.nameIDMap) && checkUntrackedFileOverwritten(getUntrackedFileList(), headCommit.nameIDMap)) {
            System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
            return;
        }
        String givenBranchHeadCommitID = sha1(serialize(givenBranchHeadCommit));
        String headCommitID = sha1(serialize(headCommit));
        String splitPointID = getSplitPointID(givenBranchHeadCommitID, headCommitID);
        Commit splitPoint = getCommitByID(splitPointID);
        if (splitPointID.equals(givenBranchHeadCommitID)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }
        if (splitPointID.equals(headCommitID)) {
            checkOutBranch(givenBranchName);
            System.out.println("Current branch fast-forwarded.");
            return;
        }
        boolean conflictHappened = false;
        HashMap<String, String> splitPointMap =  splitPoint.nameIDMap;
        HashMap<String, String> headCommitMap = headCommit.nameIDMap;
        HashMap<String, String> givenHeadCommitMap = givenBranchHeadCommit.nameIDMap;
        Set<String> allFiles = new HashSet<>(splitPointMap.keySet());
        allFiles.addAll(headCommitMap.keySet());
        allFiles.addAll(givenHeadCommitMap.keySet());
        Set<String> conflictFiles = new HashSet<>();
        for (String fileName: allFiles) {
            if (existInCommitMap(fileName, splitPointMap) && existInCommitMap(fileName, headCommitMap) && existInCommitMap(fileName, givenHeadCommitMap) && changedFromCommit(fileName, splitPointMap, givenHeadCommitMap) && !changedFromCommit(fileName, splitPointMap, headCommitMap)) {
                checkOutAndStage(fileName, givenHeadCommitMap, stagingArea);
            } else if (existInCommitMap(fileName, splitPointMap) && existInCommitMap(fileName, headCommitMap) && existInCommitMap(fileName, givenHeadCommitMap) && !changedFromCommit(fileName, splitPointMap, givenHeadCommitMap) && changedFromCommit(fileName, splitPointMap, headCommitMap)) {
                continue;
            } else if (existInCommitMap(fileName, headCommitMap) && existInCommitMap(fileName, givenHeadCommitMap) && !changedFromCommit(fileName, headCommitMap, givenHeadCommitMap)) {
                continue;
            } else if (existInCommitMap(fileName, headCommitMap) && !existInCommitMap(fileName, splitPointMap) && !existInCommitMap(fileName, givenHeadCommitMap)) {
                continue;
            } else if (existInCommitMap(fileName, givenHeadCommitMap) && !existInCommitMap(fileName, splitPointMap) && !existInCommitMap(fileName, headCommitMap)) {
                checkOutAndStage(fileName, givenHeadCommitMap, stagingArea);
            } else if (existInCommitMap(fileName, splitPointMap) && existInCommitMap(fileName, headCommitMap) && !existInCommitMap(fileName, givenHeadCommitMap) && !changedFromCommit(fileName, splitPointMap, headCommitMap)) {
                remove(fileName);
            } else if (existInCommitMap(fileName, splitPointMap) && !existInCommitMap(fileName, headCommitMap) && existInCommitMap(fileName, givenHeadCommitMap) && !changedFromCommit(fileName, splitPointMap, givenHeadCommitMap)) {
                continue;
            } else if (existInCommitMap(fileName, headCommitMap) && existInCommitMap(fileName, givenHeadCommitMap) && changedFromCommit(fileName, headCommitMap, givenHeadCommitMap)) {
                conflictHappened = true;
                conflictFiles.add(fileName);
            } else if (existInCommitMap(fileName, headCommitMap) && !existInCommitMap(fileName, givenHeadCommitMap) && changedFromCommit(fileName, splitPointMap, headCommitMap)) {
                conflictHappened = true;
                conflictFiles.add(fileName);
            } else if (!existInCommitMap(fileName, headCommitMap) && existInCommitMap(fileName, givenHeadCommitMap) && changedFromCommit(fileName, splitPointMap, givenHeadCommitMap)) {
                conflictHappened = true;
                conflictFiles.add(fileName);
            }
        }
        for (String fileName: conflictFiles) {
            File conflictFile = join(CWD, fileName);
            String contentInCurrentBranch;
            String contentInGivenBranch;
            if (headCommitMap.containsKey(fileName)) {
                String blobID = headCommitMap.get(fileName);
                File blob = join(BLOBS_DIR, blobID);
                contentInCurrentBranch = readContentsAsString(blob);
            } else {
                contentInCurrentBranch = "";
            }
            if (givenHeadCommitMap.containsKey(fileName)) {
                String blobID = givenHeadCommitMap.get(fileName);
                File blob = join(BLOBS_DIR, blobID);
                contentInGivenBranch = readContentsAsString(blob);
            } else {
                contentInGivenBranch = "";
            }
            writeContents(conflictFile, "<<<<<<< HEAD", contentInCurrentBranch, "=======", contentInGivenBranch, ">>>>>>>");
            byte[] blobContent = readContents(conflictFile);
            Blob newBlob = new Blob(blobContent);
            stagingArea.put(fileName, sha1(serialize(newBlob)));

        }
        if (conflictHappened) {
            System.out.println("Encountered a merge conflict.");
        }
        writeObject(INDEX_FILE, stagingArea);
        String commitMessage = String.format("Merged %s into %s.", givenBranchName, readContentsAsString(HEAD_FILE));
        commit(commitMessage, givenBranchHeadCommitID);
    }

    private static void checkOutAndStage(String fileName, HashMap<String, String> givenHeadCommitMap, HashMap<String, String> stagingArea) {
        String blobID = givenHeadCommitMap.get(fileName);
        checkOutFile(fileName, blobID);
        stagingArea.put(fileName, blobID);
    }

    private static boolean existInCommitMap(String fileName, HashMap<String, String> commitMap) {
        return commitMap.containsKey(fileName) ;
    }
    private static boolean changedFromCommit(String fileName, HashMap<String, String> commitChangedFrom, HashMap<String, String> commitChangedTo) {
        return !commitChangedFrom.get(fileName).equals(commitChangedTo.get(fileName));
    }
    /** get the split point commitID */
    private static String getSplitPointID(String givenBranchHeadCommitID, String headCommitID) {
        HashSet<String> commitIDSet = new HashSet<>();
        commitIDSet.add(headCommitID);
        Commit headCommit = getCommitByID(headCommitID);
        Commit givenBranchHeadCommit = getCommitByID(givenBranchHeadCommitID);
        while (headCommit.parentID != null) {
            commitIDSet.add(headCommit.parentID);
            headCommit = getCommitByID(headCommit.parentID);
        }
        if (commitIDSet.contains(givenBranchHeadCommitID)) {
            return givenBranchHeadCommitID;
        }
        while (givenBranchHeadCommit.parentID != null) {
            if (commitIDSet.contains(givenBranchHeadCommit.parentID)) {
                return givenBranchHeadCommit.parentID;
            }
            givenBranchHeadCommit = getCommitByID(givenBranchHeadCommit.parentID);
        }
        return null;
    }

}
