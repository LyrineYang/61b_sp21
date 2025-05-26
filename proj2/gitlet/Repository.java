package gitlet;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
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

    /* TODO: fill in the rest of this class. */

    /** to create the gitlet directory and the file structure */
    public static void initRepository() {
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
            HashMap<String, String> addTree = new HashMap<>();
            writeObject(INDEX_FILE, addTree);
            MASTER_FILE.createNewFile();
        } catch (IOException excp) {
            throw new RuntimeException(excp);
        }
    }
    /** add the file need to add to blobs directory and add the key-value to index */
    public static void addRepository(String fileName) {
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
        /* check if there is file in staging area to commit */
        HashMap<String, String> stagingAreaMap = readObject(INDEX_FILE, HashMap.class);
        if (stagingAreaMap.isEmpty()) {
            System.out.println("No changes added to the commit.");
            return;
        }
        Commit headCommit = getHeadCommit();
        Commit newCommit = new Commit(sha1(headCommit), commitMessage, getTimeStampString());

        /* load the headCommit map and put the staging area: the stagingAreaMap */
        newCommit.nameIDMap = new HashMap<>(newCommit.nameIDMap);
        newCommit.nameIDMap.putAll(stagingAreaMap);

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

}
