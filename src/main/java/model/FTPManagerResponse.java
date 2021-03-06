package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FTPManagerResponse<T> implements Serializable {
    private T content;
    private List<Throwable> errors;

    public List<Throwable> getErrors() {
        return errors;
    }

    public void addError(Throwable error) {
        if (errors == null) {
            errors = new ArrayList<>();
        }
        errors.add(error);
    }

    public void setContent(T content) {
        this.content = content;
    }

    public T getContent() {
        return content;
    }

    public boolean isSuccess() {
        return (errors == null || errors.isEmpty());
    }
}
