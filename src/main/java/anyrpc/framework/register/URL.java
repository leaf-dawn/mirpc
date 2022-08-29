package anyrpc.framework.register;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author fzw
 * url
 * @date 2022-08-29 10:05
 */
@AllArgsConstructor
@Data
public class URL {

    private String hostname;

    private Integer port;
}
