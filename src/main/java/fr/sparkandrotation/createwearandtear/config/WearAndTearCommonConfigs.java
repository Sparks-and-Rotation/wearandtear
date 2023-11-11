package fr.sparkandrotation.createwearandtear.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class WearAndTearCommonConfigs {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static ForgeConfigSpec.ConfigValue<List<? extends String>> WEAR_WHITELIST;
    public static ForgeConfigSpec.ConfigValue<Integer> WRENCH_REPAIR;
    public static ForgeConfigSpec.ConfigValue<Integer> WRENCH_COOLDOWN;
    public static ForgeConfigSpec.ConfigValue<Integer> DEFAULT_DURABILITY;
    public static ForgeConfigSpec.ConfigValue<Integer> DEFAULT_DAMAGE_CHANCE;

    static {
        BUILDER.push("wearandtear");
        BUILDER.push("wrench");
        WRENCH_COOLDOWN = BUILDER
                .comment("How long you need to wait before using the wrench again on repair")
                .define("cooldown", 10);
        WRENCH_REPAIR = BUILDER
                .comment("How much of durability it repair of a machine (between 1 and 100)")
                        .define("repair_percent", 25);
        BUILDER.pop();

        BUILDER.push("wearing");
        DEFAULT_DURABILITY = BUILDER
                .comment("default durability of each machine bloc (144000 allow a machine to run at 256rpm for ~1H at 10% chance")
                .define("durability", 144000);
        DEFAULT_DAMAGE_CHANCE = BUILDER
                .comment("chance of machine getting damage (from 0 to 100 number over 100 are the same as 100)")
                .define("chance", 10);
        WEAR_WHITELIST = BUILDER
                .comment("This list contains block that should get damage over time")
                .defineList("whitelist", List.of(
                        "create:mechanical_press",
                        "create:mechanical_mixer"
                ), entry -> true);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
