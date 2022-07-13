package quizB.form;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Form
 * @author 3030678
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizForm {
	
	/** 識別ID */
	private Integer id;
	
	/** クイズの内容 */
	@NotBlank(message="必須入力です") //th:errors=のエラーメッセージで出力するメッセージ
	private String question;
	
	/** クイズの解答 */
	private Boolean answer;
	
	/** 作成者 */
	@NotBlank(message="必須入力です")
	private String author;
	
	/** 「登録」or「変更」判定用 
	 * trueが登録
	 */
	private Boolean newQuiz;
}