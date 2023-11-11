package fr.sparkandrotation.createwearandtear.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import fr.sparkandrotation.createwearandtear.logic.WearAndTearLogicType;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CustomBlocConfig {
    public static Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping().create();
    public static Map<String, BlockInformation> registeredBlocks = new HashMap<>();
    public static void init(File jsonConfig) {
        try {
            if (!jsonConfig.exists() && jsonConfig.createNewFile()) {
                Map<String, BlockInformation> defaultMap = getDefaults();
                String json = gson.toJson(defaultMap, new TypeToken<Map<String, BlockInformation>>(){}.getType());
                FileWriter writer = new FileWriter(jsonConfig);
                writer.write(json);
                writer.close();
            }

            registeredBlocks = gson.fromJson(new FileReader(jsonConfig), new TypeToken<Map<String, BlockInformation>>(){}.getType());
        } catch (IOException e) {
            System.out.println("Error creating default configuration.");
        }
    }

    private static Map<String, BlockInformation> getDefaults() {
        Map<String, BlockInformation> ret = new HashMap<>();
        ret.put("create:mechanical_press", new BlockInformation(WearAndTearLogicType.REPAIR_WRENCH,100, 10, 0, 5));
        return ret;
    }

    public static class BlockInformation {
        public int durability;
        public int chance;
        public int min;
        public int max;
        public WearAndTearLogicType type;

        public BlockInformation(WearAndTearLogicType type,int durability, int chance, int min, int max) {
            this.type = type;
            this.durability = durability;
            this.chance = chance;
            this.min = min;
            this.max = max;
        }
    }
}
