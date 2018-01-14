package travelGuide.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import travelGuide.entity.Vote;

import java.util.List;

@Repository
public interface UsersVotesRepository extends JpaRepository<Vote,Integer>{
    Vote findVoteByUserIdAndArticleId(Integer user_id, Integer article_id);

    List<Vote> findByArticleId(Integer article_id);
}
