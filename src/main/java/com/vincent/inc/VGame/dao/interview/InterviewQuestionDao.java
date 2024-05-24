package com.vincent.inc.VGame.dao.interview;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.vincent.inc.VGame.model.interview.InterviewQuestion;

public interface InterviewQuestionDao extends JpaRepository<InterviewQuestion, Integer> {

    public InterviewQuestion findBySub(String sub);
    public List<InterviewQuestion> findAllBySub(String sub);

	public InterviewQuestion findByTitle(String title);
	public List<InterviewQuestion> findAllByTitle(String title);

	public InterviewQuestion findByContent(String content);
	public List<InterviewQuestion> findAllByContent(String content);

	public InterviewQuestion findByAnswer(String answer);
	public List<InterviewQuestion> findAllByAnswer(String answer);

	@Query(value = "select * from  as  where .sub = :sub and .title = :title and .content = :content and .answer = :answer", nativeQuery = true)
	public List<InterviewQuestion> getAllByMatchAll(@Param("sub") String sub, @Param("title") String title, @Param("content") String content, @Param("answer") String answer);

	@Query(value = "select * from  as  where .sub = :sub or .title = :title or .content = :content or .answer = :answer", nativeQuery = true)
	public List<InterviewQuestion> getAllByMatchAny(@Param("sub") String sub, @Param("title") String title, @Param("content") String content, @Param("answer") String answer);
}
