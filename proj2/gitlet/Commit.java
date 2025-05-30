package gitlet;

// TODO: any imports you need here

import java.io.Serializable;
import java.util.HashMap;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Lyrine Yang
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
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



    /* TODO: fill in the rest of this class. */
}
