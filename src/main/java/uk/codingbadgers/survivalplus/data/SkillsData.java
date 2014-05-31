package uk.codingbadgers.survivalplus.data;

public class SkillsData {

    public Skill[] skills;

    public static class Skill {
        public String id;
        public String name;
        public int level;
        public float progress;
        public int maxLevel;
    }
}
