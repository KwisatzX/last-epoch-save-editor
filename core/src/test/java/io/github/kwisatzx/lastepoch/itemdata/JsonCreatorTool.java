package io.github.kwisatzx.lastepoch.itemdata;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Disabled("Code used for initially creating the json files, left over in case it's needed again")
class JsonCreatorTool {
    private Object jsonObj; //replace with the appropriate type

    @Test
    void inputData() {
//        jsonObj = new
        //load new data
    }

    @AfterEach
    void tearDown() {
        String name = "Skills"; //example
        Path file = Paths.get("core/src/main/resources/item data/" + name + ".json");
        ObjectMapper objectMapper = new ObjectMapper()
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                .configure(SerializationFeature.INDENT_OUTPUT, true);
        try {
            if (!Files.isDirectory(file.getName(0))) Files.createDirectory(file.getName(0));
            objectMapper.writeValue(file.toFile(), jsonObj);
        } catch (IOException e) {e.printStackTrace();}
    }
}