package example.entity;

public class Teacher {
private Integer teacherId;
private String teacherName;
private Integer teacherAge;
private String clazzId;
public Integer getTeacherId() {
	return teacherId;
}
public void setTeacherId(Integer teacherId) {
	this.teacherId = teacherId;
}
public String getTeacherName() {
	return teacherName;
}
public void setTeacherName(String teacherName) {
	this.teacherName = teacherName;
}
public Integer getTeacherAge() {
	return teacherAge;
}
public void setTeacherAge(Integer teacherAge) {
	this.teacherAge = teacherAge;
}
public String getClazzId() {
	return clazzId;
}
public void setClazzId(String clazzId) {
	this.clazzId = clazzId;
}
@Override
public String toString() {
	return "{\"teacherId\":\"" + teacherId + "\",\"teacherName\":\"" + teacherName + "\",\"teacherAge\":\"" + teacherAge + "\",\"clazzId\":\"" + clazzId + "\"}  ";
}


}
