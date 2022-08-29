package anyrpc.framework.protocol;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author fzw
 * 传输对象
 * @date 2022-08-28 21:55
 */

@AllArgsConstructor
@Data
public class Invocation {

    private String interfaceName;

    private String methodName;

    private Class[] paramTypes;

    private Object[] params;
}
