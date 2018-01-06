package softuniBlog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import softuniBlog.entity.Vote;

@Repository
public interface UsersVotesRepository extends JpaRepository<Vote,Integer>{
    public Vote findVoteByUserIdAndArticleId(Integer user_id, Integer article_id);
}
