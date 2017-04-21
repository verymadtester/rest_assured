import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Created by Sergei_Shatilov on 4/7/2017.
 */
public class MessageUtil {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static Message convert(String message) throws IOException {
        message = message.substring(0, message.length() - 2).substring(3)
                .replace("\\\"", "\"");
        return mapper.readValue(message, Message.class);
    }

    public static String toTransfer(Message msg) throws JsonProcessingException {
        String str = mapper.writeValueAsString(msg);
        String result = str.replace("\"", "\\\"");
        return "[\"" + result + "\"]";
    }
}
