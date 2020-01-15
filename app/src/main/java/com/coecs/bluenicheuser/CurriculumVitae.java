package com.coecs.bluenicheuser;

import java.util.ArrayList;

public class CurriculumVitae {

    private String objective;
    private ArrayList<String> skills;
    private CollegeInfo collegeInfo;

    public String getObjective() {
        return objective;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    public ArrayList<String> getSkills() {
        return skills;
    }

    public void setSkills(ArrayList<String> skills) {
        this.skills = skills;
    }

    public CollegeInfo getCollegeInfo() {
        return collegeInfo;
    }

    public void setCollegeInfo(CollegeInfo collegeInfo) {
        this.collegeInfo = collegeInfo;
    }

    public static class CollegeInfo{

        private String tertiary;
        private String year;
        private String university;
        private String Course;

        public CollegeInfo() {
        }

        public String getTertiary() {
            return tertiary;
        }

        public void setTertiary(String tertiary) {
            this.tertiary = tertiary;
        }

        public String getYear() {
            return year;
        }

        public void setYear(String year) {
            this.year = year;
        }

        public String getUniversity() {
            return university;
        }

        public void setUniversity(String university) {
            this.university = university;
        }

        public String getCourse() {
            return Course;
        }

        public void setCourse(String course) {
            Course = course;
        }
    }

}
