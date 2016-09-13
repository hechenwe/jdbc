package com.sooncode.usejdbc.entity;

import java.io.Serializable;

/**
 * 单科诊断记录
 * 
 * @author hechen
 * 
 */
public class EduSingleDiagnosisRecord implements Serializable {
	private static final long serialVersionUID = 1L;
	/** 单科诊断记录code */
	private String eduSingleDiagnosisRecordCode;
	/** 综合诊断记录code */
	private String complexDiagnosisRecordCode;
	/** 主观题复审得分 */
	private Double diagnosisScore;
	/** 学年code */
	private String gradeCode;
	/** 文理 */
	private Integer subjectType;
	/** 创建时间 */
	private java.util.Date createTime;
	/** 主观题2判得分 */
	private Double subjectivityTwoScore;
	/** 诊断试卷名称 */
	private String diagnosisPaperName;
	/** 诊断记录状态 */
	private Integer diagnosisRecordStatus;
	/** 修改时间 */
	private java.util.Date updateTime;
	/** 考试结束时间 */
	private java.util.Date examEndTime;
	/** 客观题总分 */
	private Double impersonalityScore;
	/** 诊断 短板诊断 */
	private Integer examType;
	/** 试卷分配状态 */
	private Integer diagnosisPaperAllocationStatus;
	/** 教材版本 */
	private String bookVersionCode;
	/** 主观题1判得分 */
	private Double subjectivityOneScore;
	/** 诊断试卷code */
	private String diagnosisPaperCode;
	/** 学生code */
	private String studentCode;
	/** 考试开始时间 */
	private java.util.Date examStartTime;
	/** 学科code */
	private String subjectCode;
	/** 考生类型 */
	private Integer candidateType;

	/** 综合诊断记录code */
	public String getComplexDiagnosisRecordCode() {
		return complexDiagnosisRecordCode;
	}

	/** 综合诊断记录code */
	public void setComplexDiagnosisRecordCode(String complexDiagnosisRecordCode) {
		this.complexDiagnosisRecordCode = complexDiagnosisRecordCode;
	}

	/** 主观题复审得分 */
	public Double getDiagnosisScore() {
		return diagnosisScore;
	}

	/** 主观题复审得分 */
	public void setDiagnosisScore(Double diagnosisScore) {
		this.diagnosisScore = diagnosisScore;
	}

	/** 学年code */
	public String getGradeCode() {
		return gradeCode;
	}

	/** 学年code */
	public void setGradeCode(String gradeCode) {
		this.gradeCode = gradeCode;
	}

	/** 文理 */
	public Integer getSubjectType() {
		return subjectType;
	}

	/** 文理 */
	public void setSubjectType(Integer subjectType) {
		this.subjectType = subjectType;
	}

	/** 创建时间 */
	public java.util.Date getCreateTime() {
		return createTime;
	}

	/** 创建时间 */
	public void setCreateTime(java.util.Date createTime) {
		this.createTime = createTime;
	}

	/** 主观题2判得分 */
	public Double getSubjectivityTwoScore() {
		return subjectivityTwoScore;
	}

	/** 主观题2判得分 */
	public void setSubjectivityTwoScore(Double subjectivityTwoScore) {
		this.subjectivityTwoScore = subjectivityTwoScore;
	}

	/** 诊断试卷名称 */
	public String getDiagnosisPaperName() {
		return diagnosisPaperName;
	}

	/** 诊断试卷名称 */
	public void setDiagnosisPaperName(String diagnosisPaperName) {
		this.diagnosisPaperName = diagnosisPaperName;
	}

	/** 诊断记录状态 */
	public Integer getDiagnosisRecordStatus() {
		return diagnosisRecordStatus;
	}

	/** 诊断记录状态 */
	public void setDiagnosisRecordStatus(Integer diagnosisRecordStatus) {
		this.diagnosisRecordStatus = diagnosisRecordStatus;
	}

	/** 修改时间 */
	public java.util.Date getUpdateTime() {
		return updateTime;
	}

	/** 修改时间 */
	public void setUpdateTime(java.util.Date updateTime) {
		this.updateTime = updateTime;
	}

	/** 考试结束时间 */
	public java.util.Date getExamEndTime() {
		return examEndTime;
	}

	/** 考试结束时间 */
	public void setExamEndTime(java.util.Date examEndTime) {
		this.examEndTime = examEndTime;
	}

	/** 客观题总分 */
	public Double getImpersonalityScore() {
		return impersonalityScore;
	}

	/** 客观题总分 */
	public void setImpersonalityScore(Double impersonalityScore) {
		this.impersonalityScore = impersonalityScore;
	}

	/** 诊断 短板诊断 */
	public Integer getExamType() {
		return examType;
	}

	/** 诊断 短板诊断 */
	public void setExamType(Integer examType) {
		this.examType = examType;
	}

	/** 试卷分配状态 */
	public Integer getDiagnosisPaperAllocationStatus() {
		return diagnosisPaperAllocationStatus;
	}

	/** 试卷分配状态 */
	public void setDiagnosisPaperAllocationStatus(Integer diagnosisPaperAllocationStatus) {
		this.diagnosisPaperAllocationStatus = diagnosisPaperAllocationStatus;
	}

	/** 单科诊断记录code */
	public String getEduSingleDiagnosisRecordCode() {
		return eduSingleDiagnosisRecordCode;
	}

	/** 单科诊断记录code */
	public void setEduSingleDiagnosisRecordCode(String eduSingleDiagnosisRecordCode) {
		this.eduSingleDiagnosisRecordCode = eduSingleDiagnosisRecordCode;
	}

	/** 教材版本 */
	public String getBookVersionCode() {
		return bookVersionCode;
	}

	/** 教材版本 */
	public void setBookVersionCode(String bookVersionCode) {
		this.bookVersionCode = bookVersionCode;
	}

	/** 主观题1判得分 */
	public Double getSubjectivityOneScore() {
		return subjectivityOneScore;
	}

	/** 主观题1判得分 */
	public void setSubjectivityOneScore(Double subjectivityOneScore) {
		this.subjectivityOneScore = subjectivityOneScore;
	}

	/** 诊断试卷code */
	public String getDiagnosisPaperCode() {
		return diagnosisPaperCode;
	}

	/** 诊断试卷code */
	public void setDiagnosisPaperCode(String diagnosisPaperCode) {
		this.diagnosisPaperCode = diagnosisPaperCode;
	}

	/** 学生code */
	public String getStudentCode() {
		return studentCode;
	}

	/** 学生code */
	public void setStudentCode(String studentCode) {
		this.studentCode = studentCode;
	}

	/** 考试开始时间 */
	public java.util.Date getExamStartTime() {
		return examStartTime;
	}

	/** 考试开始时间 */
	public void setExamStartTime(java.util.Date examStartTime) {
		this.examStartTime = examStartTime;
	}

	/** 学科code */
	public String getSubjectCode() {
		return subjectCode;
	}

	/** 学科code */
	public void setSubjectCode(String subjectCode) {
		this.subjectCode = subjectCode;
	}

	/** 考生类型 */
	public Integer getCandidateType() {
		return candidateType;
	}

	/** 考生类型 */
	public void setCandidateType(Integer candidateType) {
		this.candidateType = candidateType;
	}

}
