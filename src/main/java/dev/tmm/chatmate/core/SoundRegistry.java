package dev.tmm.chatmate.core;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import dev.tmm.chatmate.util.Pair;

import java.util.*;

public class SoundRegistry {
    private static LinkedHashMap<String, String> soundOpts = new LinkedHashMap<>();
    private static HashMap<Integer, String> soundCategories = new HashMap<>();

    static {
        soundCategories.put(0, "Mute");
        soundOpts.put("None", "");
    }

    public static Set<String> getKeySet() {
        return new HashSet<>(soundOpts.keySet());
    }

    public static List<String> getKeyList() {
        return Lists.newArrayList(soundOpts.keySet());
    }

    public static HashMap<Integer, String> getCategories() {
        return new HashMap<>(soundCategories);
    }

    public static String getSound_NotNull(String key) {
        String out = soundOpts.get(key);

        return out == null ? "" : out;
    }

    public static void loadSounds_1_8() {
        soundCategories.put(1, "Instrumental/Miscellaneous Sounds");
        soundOpts.put("Bass", "note.bass");
        soundOpts.put("Bell", "random.orb");
        soundOpts.put("Click", "random.click");
        soundOpts.put("Explosion", "random.explode");
        soundOpts.put("Firework Explosion", "fireworks.largeBlast");
        soundOpts.put("Firework Launch", "fireworks.launch");
        soundOpts.put("Firework Twinkle", "fireworks.twinkle");
        soundOpts.put("Flute", "mob.ghast.affectionate_scream");
        soundOpts.put("Glass", "dig.glass");
        soundOpts.put("Level Up", "random.levelup");
        soundOpts.put("Piano", "note.pling");
        soundOpts.put("Pop", "random.pop");
        soundOpts.put("Splash", "random.splash");
        soundOpts.put("Thunder", "ambient.weather.thunder");
        soundOpts.put("Whoosh", "item.fireCharge.use");

        soundCategories.put(16, "Block/Item Sounds");
        soundOpts.put("Anvil Fall", "random.anvil_land");
        soundOpts.put("Chest Close", "random.chestclosed");
        soundOpts.put("Chest Open", "random.chestopen");
        soundOpts.put("Ladder", "step.ladder");
        soundOpts.put("Tool Break", "random.break");
        soundOpts.put("Metal Knock", "mob.zombie.metal");
        soundOpts.put("Wood Knock", "mob.zombie.wood");

        soundCategories.put(23, "Hit Sounds");
        soundOpts.put("Blaze Hit", "mob.blaze.hit");
        soundOpts.put("Chicken Hit", "mob.chicken.hurt");
        soundOpts.put("Cow Hit", "mob.cow.hurt");
        soundOpts.put("Ender Dragon Hit", "mob.enderdragon.hit");
        soundOpts.put("Enderman Hit", "mob.endermen.hit");
        soundOpts.put("Horse Hit", "mob.horse.hit");
        soundOpts.put("Iron Golem Hit", "mob.irongolem.hit");
        soundOpts.put("Skeleton Hit", "mob.skeleton.hurt");
        soundOpts.put("Wither Hit", "mob.wither.hurt");
        soundOpts.put("Zombie Hit", "mob.zombie.hurt");
        soundOpts.put("Zombie Horse Hit", "mob.horse.zombie.hit");
        soundOpts.put("Zombie Pigman Hit", "mob.zombiepig.zpighurt");

        soundCategories.put(35, "Death Sounds");
        soundOpts.put("Bat Death", "mob.bat.death");
        soundOpts.put("Blaze Death", "mob.blaze.death");
        soundOpts.put("Enderman Death", "mob.endermen.death");
        soundOpts.put("Horse Death", "mob.horse.death");
        soundOpts.put("Pig Death", "mob.pig.death");
        soundOpts.put("Silverfish Death", "mob.silverfish.kill");
        soundOpts.put("Zombie Death", "mob.zombie.death");

        soundCategories.put(42, "Additional Creature Sounds");
        soundOpts.put("Angry Horse", "mob.horse.angry");
        soundOpts.put("Cat Meow", "mob.cat.meow");
        soundOpts.put("Cat Trill", "mob.cat.purreow");
        soundOpts.put("Ender Dragon Growl", "mob.enderdragon.growl");
        soundOpts.put("Ghast Charge", "mob.ghast.charge");
        soundOpts.put("Guardian Curse", "mob.guardian.curse");
        soundOpts.put("Guardian", "mob.guardian.land.idle");
        soundOpts.put("Wither Shooting", "mob.wither.shoot");
        soundOpts.put("Wolf Growl", "mob.wolf.growl");
    }

    public static void loadSounds_1_9() {
        soundCategories.put(1, "Instrumental/Miscellaneous Sounds");
        soundOpts.put("Bass", "block.note.bass");
        soundOpts.put("Bell", "entity.experience_orb.pickup");
        soundOpts.put("Click", "ui.button.click");
        soundOpts.put("Explosion", "entity.generic.explode");
        soundOpts.put("Firework Explosion", "entity.firework.large_blast");
        soundOpts.put("Firework Launch", "entity.firework.launch");
        soundOpts.put("Firework Twinkle", "entity.firework.twinkle");
        soundOpts.put("Flute", "entity.ghast.scream");
        soundOpts.put("Glass", "block.glass.break");
        soundOpts.put("Level Up", "entity.player.levelup");
        soundOpts.put("Piano", "block.note.pling");
        soundOpts.put("Pluck", "enchant.thorns.hit");
        soundOpts.put("Pop", "entity.item.pickup");
        soundOpts.put("Splash", "entity.bobber.splash");
        soundOpts.put("Thunder", "entity.lightning.thunder");
        soundOpts.put("Whoosh", "item.firecharge.use");

        soundCategories.put(17, "Block/Item Sounds");
        soundOpts.put("Anvil Fall", "block.anvil.land");
        soundOpts.put("Chest Close", "block.chest.close");
        soundOpts.put("Chest Open", "block.chest.open");
        soundOpts.put("Ladder", "block.ladder.step");
        soundOpts.put("Metal Knock", "entity.zombie.attack_iron_door");
        soundOpts.put("Shield Block", "item.shield.block");
        soundOpts.put("Tool Break", "entity.item.break");
        soundOpts.put("Wood Knock", "entity.zombie.attack_door_wood");

        soundCategories.put(25, "Hit Sounds");
        soundOpts.put("Blaze Hit", "entity.blaze.hurt");
        soundOpts.put("Chicken Hit", "entity.chicken.hurt");
        soundOpts.put("Cow Hit", "entity.cow.hurt");
        soundOpts.put("Ender Dragon Hit", "entity.enderdragon.hurt");
        soundOpts.put("Enderman Hit", "entity.endermen.hurt");
        soundOpts.put("Horse Hit", "entity.horse.hurt");
        soundOpts.put("Iron Golem Hit", "entity.irongolem.hurt");
        soundOpts.put("Shulker Hit", "entity.shulker.hurt");
        soundOpts.put("Skeleton Hit", "entity.skeleton.hurt");
        soundOpts.put("Wither Hit", "entity.wither.hurt");
        soundOpts.put("Zombie Hit", "entity.zombie.hurt");
        soundOpts.put("Zombie Horse Hit", "entity.zombie_horse.hurt");
        soundOpts.put("Zombie Pigman Hit", "entity.zombie_pig.hurt");

        soundCategories.put(38, "Death Sounds");
        soundOpts.put("Bat Death", "entity.bat.death");
        soundOpts.put("Blaze Death", "entity.blaze.death");
        soundOpts.put("Enderman Death", "entity.endermen.death");
        soundOpts.put("Horse Death", "entity.horse.death");
        soundOpts.put("Pig Death", "entity.pig.death");
        soundOpts.put("Silverfish Death", "entity.silverfish.death");
        soundOpts.put("Zombie Death", "entity.zombie.death");

        soundCategories.put(46, "Additional Creature Sounds");
        soundOpts.put("Angry Horse", "entity.horse.angry");
        soundOpts.put("Cat Meow", "entity.cat.ambient");
        soundOpts.put("Cat Trill", "entity.cat.purreow");
        soundOpts.put("Ender Dragon Growl", "entity.enderdragon.growl");
        soundOpts.put("Ghast Charge", "entity.ghast.warn");
        soundOpts.put("Guardian Curse", "entity.elder_guardian.curse");
        soundOpts.put("Guardian", "entity.guardian.ambient_land");
        soundOpts.put("Wither Shooting", "entity.wither.shoot");
        soundOpts.put("Wolf Growl", "entity.wolf.growl");
    }

    public static void loadSounds_1_10() {
        soundCategories.put(1, "Instrumental/Miscellaneous Sounds");
        soundOpts.put("Bass", "block.note.bass");
        soundOpts.put("Bell", "entity.experience_orb.pickup");
        soundOpts.put("Click", "ui.button.click");
        soundOpts.put("Explosion", "entity.generic.explode");
        soundOpts.put("Firework Explosion", "entity.firework.large_blast");
        soundOpts.put("Firework Launch", "entity.firework.launch");
        soundOpts.put("Firework Twinkle", "entity.firework.twinkle");
        soundOpts.put("Flute", "entity.ghast.scream");
        soundOpts.put("Glass", "block.glass.break");
        soundOpts.put("Level Up", "entity.player.levelup");
        soundOpts.put("Piano", "block.note.pling");
        soundOpts.put("Pluck", "enchant.thorns.hit");
        soundOpts.put("Pop", "entity.item.pickup");
        soundOpts.put("Splash", "entity.bobber.splash");
        soundOpts.put("Thunder", "entity.lightning.thunder");
        soundOpts.put("Whoosh", "item.firecharge.use");

        soundCategories.put(17, "Block/Item Sounds");
        soundOpts.put("Anvil Fall", "block.anvil.land");
        soundOpts.put("Chest Close", "block.chest.close");
        soundOpts.put("Chest Open", "block.chest.open");
        soundOpts.put("Ladder", "block.ladder.step");
        soundOpts.put("Metal Knock", "entity.zombie.attack_iron_door");
        soundOpts.put("Shield Block", "item.shield.block");
        soundOpts.put("Tool Break", "entity.item.break");
        soundOpts.put("Wood Knock", "entity.zombie.attack_door_wood");

        soundCategories.put(25, "Hit Sounds");
        soundOpts.put("Blaze Hit", "entity.blaze.hurt");
        soundOpts.put("Chicken Hit", "entity.chicken.hurt");
        soundOpts.put("Cow Hit", "entity.cow.hurt");
        soundOpts.put("Ender Dragon Hit", "entity.enderdragon.hurt");
        soundOpts.put("Enderman Hit", "entity.endermen.hurt");
        soundOpts.put("Horse Hit", "entity.horse.hurt");
        soundOpts.put("Iron Golem Hit", "entity.irongolem.hurt");
        soundOpts.put("Polar Bear Hit", "entity.polar_bear.hurt");
        soundOpts.put("Shulker Hit", "entity.shulker.hurt");
        soundOpts.put("Skeleton Hit", "entity.skeleton.hurt");
        soundOpts.put("Wither Hit", "entity.wither.hurt");
        soundOpts.put("Zombie Hit", "entity.zombie.hurt");
        soundOpts.put("Zombie Horse Hit", "entity.zombie_horse.hurt");
        soundOpts.put("Zombie Pigman Hit", "entity.zombie_pig.hurt");

        soundCategories.put(39, "Death Sounds");
        soundOpts.put("Bat Death", "entity.bat.death");
        soundOpts.put("Blaze Death", "entity.blaze.death");
        soundOpts.put("Enderman Death", "entity.endermen.death");
        soundOpts.put("Horse Death", "entity.horse.death");
        soundOpts.put("Pig Death", "entity.pig.death");
        soundOpts.put("Silverfish Death", "entity.silverfish.death");
        soundOpts.put("Zombie Death", "entity.zombie.death");

        soundCategories.put(46, "Additional Creature Sounds");
        soundOpts.put("Angry Horse", "entity.horse.angry");
        soundOpts.put("Cat Meow", "entity.cat.ambient");
        soundOpts.put("Cat Trill", "entity.cat.purreow");
        soundOpts.put("Ender Dragon Growl", "entity.enderdragon.growl");
        soundOpts.put("Ghast Charge", "entity.ghast.warn");
        soundOpts.put("Guardian Curse", "entity.elder_guardian.curse");
        soundOpts.put("Guardian", "entity.guardian.ambient_land");
        soundOpts.put("Polar Bear Growl", "entity.polar_bear.warning");
        soundOpts.put("Wither Shooting", "entity.wither.shoot");
        soundOpts.put("Wolf Growl", "entity.wolf.growl");
    }

    public static void loadSounds_1_11() {
        soundCategories.put(1, "Instrumental/Miscellaneous Sounds");
        soundOpts.put("Bass", "block.note.bass");
        soundOpts.put("Bell", "entity.experience_orb.pickup");
        soundOpts.put("Click", "ui.button.click");
        soundOpts.put("Explosion", "entity.generic.explode");
        soundOpts.put("Firework Explosion", "entity.firework.large_blast");
        soundOpts.put("Firework Launch", "entity.firework.launch");
        soundOpts.put("Firework Twinkle", "entity.firework.twinkle");
        soundOpts.put("Flute", "entity.ghast.scream");
        soundOpts.put("Glass", "block.glass.break");
        soundOpts.put("Level Up", "entity.player.levelup");
        soundOpts.put("Piano", "block.note.pling");
        soundOpts.put("Pluck", "enchant.thorns.hit");
        soundOpts.put("Pop", "entity.item.pickup");
        soundOpts.put("Splash", "entity.bobber.splash");
        soundOpts.put("Thunder", "entity.lightning.thunder");
        soundOpts.put("Whoosh", "item.firecharge.use");

        soundCategories.put(17, "Block/Item Sounds");
        soundOpts.put("Anvil Fall", "block.anvil.land");
        soundOpts.put("Chest Close", "block.chest.close");
        soundOpts.put("Chest Open", "block.chest.open");
        soundOpts.put("Ladder", "block.ladder.step");
        soundOpts.put("Metal Knock", "entity.zombie.attack_iron_door");
        soundOpts.put("Shield Block", "item.shield.block");
        soundOpts.put("Tool Break", "entity.item.break");
        soundOpts.put("Totem of Undying", "item.totem.use");
        soundOpts.put("Wood Knock", "entity.zombie.attack_door_wood");

        soundCategories.put(26, "Hit Sounds");
        soundOpts.put("Blaze Hit", "entity.blaze.hurt");
        soundOpts.put("Chicken Hit", "entity.chicken.hurt");
        soundOpts.put("Cow Hit", "entity.cow.hurt");
        soundOpts.put("Ender Dragon Hit", "entity.enderdragon.hurt");
        soundOpts.put("Enderman Hit", "entity.endermen.hurt");
        soundOpts.put("Horse Hit", "entity.horse.hurt");
        soundOpts.put("Iron Golem Hit", "entity.irongolem.hurt");
        soundOpts.put("Polar Bear Hit", "entity.polar_bear.hurt");
        soundOpts.put("Shulker Hit", "entity.shulker.hurt");
        soundOpts.put("Skeleton Hit", "entity.skeleton.hurt");
        soundOpts.put("Vindicator Hit", "entity.vindication_illager.hurt");
        soundOpts.put("Wither Hit", "entity.wither.hurt");
        soundOpts.put("Zombie Hit", "entity.zombie.hurt");
        soundOpts.put("Zombie Horse Hit", "entity.zombie_horse.hurt");
        soundOpts.put("Zombie Pigman Hit", "entity.zombie_pig.hurt");

        soundCategories.put(41, "Death Sounds");
        soundOpts.put("Bat Death", "entity.bat.death");
        soundOpts.put("Blaze Death", "entity.blaze.death");
        soundOpts.put("Enderman Death", "entity.endermen.death");
        soundOpts.put("Evoker Death", "entity.evocation_illager.death");
        soundOpts.put("Horse Death", "entity.horse.death");
        soundOpts.put("Pig Death", "entity.pig.death");
        soundOpts.put("Silverfish Death", "entity.silverfish.death");
        soundOpts.put("Zombie Death", "entity.zombie.death");

        soundCategories.put(49, "Additional Creature Sounds");
        soundOpts.put("Angry Horse", "entity.horse.angry");
        soundOpts.put("Angry Llama", "entity.llama.angry");
        soundOpts.put("Cat Meow", "entity.cat.ambient");
        soundOpts.put("Cat Trill", "entity.cat.purreow");
        soundOpts.put("Ender Dragon Growl", "entity.enderdragon.growl");
        soundOpts.put("Fang Attack", "entity.evocation_fangs.attack");
        soundOpts.put("Ghast Charge", "entity.ghast.warn");
        soundOpts.put("Guardian Curse", "entity.elder_guardian.curse");
        soundOpts.put("Guardian", "entity.guardian.ambient_land");
        soundOpts.put("Llama Hit", "entity.llama.hurt");
        soundOpts.put("Llama Spit", "entity.llama.spit");
        soundOpts.put("Polar Bear Growl", "entity.polar_bear.warning");
        soundOpts.put("Vindicator", "entity.vindication_illager.ambient");
        soundOpts.put("Wither Shooting", "entity.wither.shoot");
        soundOpts.put("Wolf Growl", "entity.wolf.growl");
    }
}
