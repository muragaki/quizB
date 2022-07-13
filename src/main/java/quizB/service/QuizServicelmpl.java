package quizB.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import quizB.entity.Quiz;
import quizB.repository.QuizRepository;

@Service
@Transactional	//ロールバック機能
public class QuizServicelmpl implements QuizService{
	
	/** Repository：注入 */
	@Autowired
	QuizRepository repository;
	
	@Override
	public Iterable<Quiz> selectAll() {		//Iterator<T>:型Tの要素のイテレータを返す
		return repository.findAll();		//インテレータ:配列のようなデータ構造の要素を順に走査していく繰り返し処理を簡潔に記述できる構文やオブジェクトなど
	}

	@Override
	public Optional<Quiz> selectOneById(Integer id) {
		return repository.findById(id);		//idでエンティティを取得
	}

	@Override
	public Optional<Quiz> selectOneRandomQuiz() {
		// ランダムでidの値を取得する		
		Integer randId = repository.getRandomId();
		// 問題がある場合
		if (randId == null) {
			// Optional.empty():空のOptionalインスタンスを返します。
			return Optional.empty();
		}
		return repository.findById(randId);
	}

	@Override
	public Boolean checkQuiz(Integer id, Boolean myAnswer) {
		// クイズの正解/不正解を判定用変数
		Boolean check = false;
		// 対象のクイズを取得
		Optional<Quiz> optQuiz = repository.findById(id);
		// 値存在チェック
		if (optQuiz.isPresent()) {
			Quiz quiz = optQuiz.get();
			// クイズの解答チェック
			if (quiz.getAnswer().equals(myAnswer)) {
				check = true;
			}
		}
		return check;
	}

	@Override
	public void insertQuiz(Quiz quiz) {
		repository.save(quiz);
		
	}

	@Override
	public void updateQuiz(Quiz quiz) {
		repository.save(quiz);
	}

	@Override
	public void deleteQuizById(Integer id) {
		repository.deleteById(id);
	}

}
