import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by doctor-googler on 11/1/2016.
 */
public class FTPManagerResponse<T> implements Serializable {
    private T content;
    private List<Throwable> errors;

    public List<Throwable> getErrors() {
        return errors;
    }

    public void addError(Throwable error) {
        if (errors == null) {
            errors = new ArrayList<Throwable>();
        }
        errors.add(error);
    }

    public void setContent(T content) {
        this.content = content;
    }

    public T getContent() {
        return  content;
    }

    public boolean isSuccess() {
        return (errors == null || errors.isEmpty());
    }
}
