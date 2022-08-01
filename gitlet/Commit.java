package gitlet;



import java.io.File;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

import java.util.Objects;

/**
 * the.
 * @author srikar
 */
public class Commit implements Serializable {

    /**the.  */
    private ArrayList<Blob> blobs;

    /**the.  */
    private String id;

    /**the.  */
    private LocalDateTime dateTime;

    /**the.  */
    private String msg;

    /**the.  */
    private Commit parent;

    /**the.  */
    private Commit parent2;

    /**the.  */
    private int dist;

    /**the.  */
    public Commit() {
        blobs = new ArrayList<>();
        msg = "initial commit";
        parent = null;
        parent2 = null;
        id = Utils.sha1(Utils.serialize(this));
        dist = 0;


    }

    /**
     * the.
     * @param par1 the.
     * @param par2 the
     * @param message the.
     */
    public Commit(Commit par1, Commit par2, String message) {
        this(par1, message);
        parent2 = par2;


    }

    /**
     * the.
     * @param par the
     * @param message the
     */
    public Commit(Commit par, String message) {
        msg = message;
        blobs = par.getBlobs();
        parent = par;
        parent2 = null;
        dist = par.dist + 1;
        for (String curFileName
                : Utils.plainFilenamesIn(Repository.STAGETOADD)) {
            String curFileContents = Utils.readContentsAsString(
                    new File(Repository.STAGETOADD + "/" + curFileName));

            Blob toAddFromStage = new Blob(curFileName, curFileContents);

            addBlob(toAddFromStage);

            File toDel = new File(Repository.STAGETOADD + "/" + curFileName);

            toDel.delete();

        }

        for (String curFileName
                : Utils.plainFilenamesIn(Repository.STAGETOREM)) {
            for (Blob curBlob : getBlobs()) {
                if (curFileName.equals(curBlob.fileName)) {
                    blobs.remove(curBlob);
                }
            }
            File toDel = new File(Repository.STAGETOREM + "/" + curFileName);
            toDel.delete();

        }



        id = Utils.sha1(Utils.serialize(this));
    }

    /**
     * the.
     * @param toAdd the
     */
    public void addBlob(Blob toAdd) {
        for (int i = 0; i < blobs.size(); i++) {
            if (blobs.get(i).fileName.equals(toAdd.fileName)) {
                blobs.set(i, toAdd);
                return;
            }
        }

        blobs.add(toAdd);

    }


    /**
     * the.
     * @return the
     */
    public ArrayList<Blob> getBlobs() {
        ArrayList<Blob> returner = new ArrayList<Blob>();
        for (Blob cur: blobs) {
            returner.add(cur);
        }

        return returner;
    }

    /**
     * the.
     * @return the
     */
    @Override
    public String toString() {
        String equals = "===\n";
        String commit = "commit " + id + "\n";
        String date = "Date: Wed Dec 31 16:00:00 1969 -0800\n";
        String m = msg + "\n";


        return equals + commit + date + m;
    }

    /**
     * The func.
     * @param o the o
     * @return the return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Commit commit = (Commit) o;
        return Objects.equals(blobs, commit.blobs)
                && Objects.equals(id, commit.id)
                && Objects.equals(dateTime, commit.dateTime)
                && Objects.equals(msg, commit.msg)
                && Objects.equals(parent, commit.parent);
    }

    /**
     * the hashh.
     * @return the hash.
     */
    @Override
    public int hashCode() {
        return Objects.hash(blobs, id, dateTime, msg, parent);
    }

    /**
     * get commit.
     * @param check the check
     * @return the check
     */
    public static ArrayList<String> getCommitFileNames(Commit check) {
        ArrayList<Blob> checkBlobs = check.getBlobs();

        ArrayList<String> nameReturner = new ArrayList<>();

        for (Blob curBlob: checkBlobs) {
            nameReturner.add(curBlob.fileName);
        }

        return nameReturner;

    }

    /**
     * get parent.
     * @return the paret
     */
    public Commit getParent() {
        return parent;
    }

    /**
     * get parent.
     * @return the paret
     */
    public Commit getParent2() {
        return parent2;
    }

    /**
     * get parent.
     * @return the paret
     */
    public int getDist() {
        return dist;
    }

    /**
     * get parent.
     * @return the paret
     */
    public String getId() {
        return id;
    }

    /**
     * get parent.
     * @return the paret
     */
    public String getMsg() {
        return msg;
    }
}
