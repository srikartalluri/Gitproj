package gitlet;

import java.io.Serializable;
import java.util.Objects;

/**
 * the.
 * @author srikar talluri
 */
public class Blob implements Serializable {

    /** the name.     */
    protected String fileName;

    /** the name.     */
    protected String contents;

    /** the name.     */
    protected String id;

    /**
     * the folder.
     * @param fileN the
     * @param cont the
     */
    public Blob(String fileN, String cont) {
        fileName = fileN;
        contents = cont;
        id = Utils.sha1(Utils.serialize(this));
    }

    /**
     * the cont.
     * @return the
     */
    public String getContents() {
        return contents;
    }

    /**
     * the.
     * @return the
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * the.
     * @param o o
     * @return the
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Blob blob = (Blob) o;
        return Objects.equals(fileName, blob.fileName)
                && Objects.equals(contents, blob.contents);
    }

    /**
     * the.
     * @return the
     */
    @Override
    public int hashCode() {
        return Objects.hash(fileName, contents);
    }


    /**
     * the.
     * @return the.
     */
    @Override
    public String toString() {
        return "Blob{"
                + "fileName='" + fileName + '\''
                + ", contents='" + contents + '\''
                + '}';
    }

    /**
     * the.
     * @return the
     */
    public String getId() {
        return id;
    }
}
