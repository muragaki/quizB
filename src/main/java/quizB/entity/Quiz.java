package quizB.entity;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * QuizのEntity
 * @author 3030678
 */
@Data	//対象クラス内のインスタンス変数に対してgetter/setterでアクセスすることが可能となる、その他機能あり
@NoArgsConstructor		//デフォルトコンストラクタを自動生成することができる
@AllArgsConstructor		//全メンバへ値をセットするための引数付きコンストラクタを自動生成することができる
//@Table(name = "quiz")
public class Quiz {
	
	/** 識別ID */
	@Id		//主キーを指定
	private Integer id;
	
	/** クイズの内容 */
	private String question;
	
	/** クイズの解答 */
	private Boolean answer;
	
	/** 作成者 */
	private String author;
}
