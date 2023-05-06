package kg.java.spring.core.model;

import kg.java.spring.core.model.enums.ResultDB;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResponseDB {
    private ResultDB resultDB;
    private String message;

    @Override
    public String toString() {
        return "ResponseDB{" +
                "resultDB=" + resultDB +
                ", message='" + message + '\'' +
                '}';
    }
}
