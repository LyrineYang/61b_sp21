package gitlet;

import java.io.Serializable;
import java.util.HashMap;


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

    public String parentID;
    /** The message of this Commit. */
    public String commitMessage;
    public String timeStamp;
    public String secondParentID;
    public HashMap<String, String> nameIDMap;
    public Commit(String p, String c, String t) {
        this(p, c, t, null);
    }
    public Commit(String p, String c, String t, String secondParentID) {
        parentID = p;
        commitMessage = c;
        timeStamp = t;
        this.secondParentID = secondParentID;
        this.nameIDMap = new HashMap<>();
    }

}
