package gitlet;

import java.io.Serializable;
import java.util.TreeMap;


/** Represents a gitlet commit object.
 *  does at a high level.
 *
 *  @author Lyrine Yang
 */
public class Commit implements Serializable {
    /**
     *
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    private final String parentID;
    /** The message of this Commit. */
    private final String commitMessage;
    private final String timeStamp;
    private final String secondParentID;
    private TreeMap<String, String> nameIDMap;
    public Commit(String p, String c, String t) {
        this(p, c, t, null);
    }
    public Commit(String p, String c, String t, String secondParentID) {
        parentID = p;
        commitMessage = c;
        timeStamp = t;
        this.secondParentID = secondParentID;
        this.nameIDMap = new TreeMap<>();
    }
    public String getParentID() {
        return parentID;
    }
    public String getCommitMessage() {
        return commitMessage;
    }
    public String getTimeStamp() {
        return timeStamp;
    }

    public String getSecondParentID() {
        return secondParentID;
    }

    public TreeMap<String, String> getNameIDMap() {
        return nameIDMap;
    }
    public void loadParentCommitMap(Commit this, TreeMap<String, String> parentCommitMap) {
        this.nameIDMap = new TreeMap<>(parentCommitMap);
    }
}
