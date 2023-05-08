package com.vincent.inc.VGame.dao.questionnaire;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.vincent.inc.VGame.model.questionnaire.Question;

public interface QuestionDao extends JpaRepository<Question, Integer>
{
	public Question findByQuestion(String question);
	public List<Question> findAllByQuestion(String question);

	public Question findByCategory(String category);
	public List<Question> findAllByCategory(String category);

	public Question findByOrderBy(int orderBy);
	public List<Question> findAllByOrderBy(int orderBy);

	@Query(value = "select * from Question as question where question.question = :question and question.category = :category and question.orderBy = :orderBy", nativeQuery = true)
	public List<Question> getAllByMatchAll(@Param("question") String question, @Param("category") String category, @Param("orderBy") int orderBy);

	@Query(value = "select * from Question as question where question.question = :question or question.category = :category or question.orderBy = :orderBy", nativeQuery = true)
	public List<Question> getAllByMatchAny(@Param("question") String question, @Param("category") String category, @Param("orderBy") int orderBy);
}