package quizB.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import quizB.entity.Quiz;
import quizB.form.QuizForm;
import quizB.service.QuizService;

/**
 * Controllerクラス
 * @author 3030678
 *
 */
@Controller
@RequestMapping("/quiz")
public class QuizController {
	
	/** DI対象 */
	@Autowired
	QuizService service;	//インターフェースが@Autowiredされている場合、それを実装しているクラスのインスタンスが自動で紐づけられる。
	
	/** 「form-backing bean」の初期化 */
	@ModelAttribute	//RequestMappingアノテーションがついたいずれかのメソッド実行前に呼ばれる
	public QuizForm setUpForm() {
		//System.out.println("QuizForm()通過");
		
		QuizForm form = new QuizForm();
		// ラジオボタンのデフォルト値設定
		form.setAnswer(true);
		return form;		//@ModelAttribute:戻り値を、Model に追加
	}
	
	/**
	 * Quizの一覧を表示します
	 * "/quiz"の@GetMapping
	 * 一覧画面を表示する
	 * @return 
	 */
	@GetMapping
	public String quiz(QuizForm quizForm, Model model) {
		//System.out.println("quiz()通過");
		
		//新規登録設定
		quizForm.setNewQuiz(true);
		//掲示板の一覧を取得する
		Iterable<Quiz> list = service.selectAll();
		// 表示用「Model」への格納
		model.addAttribute("list", list);
		model.addAttribute("title", "登録用フォーム");
		return "crud";
	}
	
	
	/**
	 * Quizデータを1件挿入
	 * "/insert"の@PostMapping
	 * 登録処理を実行する
	 * @return 
	 */
	@PostMapping(value = "/insert")
	public String insert(@Validated QuizForm quizForm, BindingResult bindingResult,
			Model model, RedirectAttributes redirectAttributes) {
						/* @Validated:@NotBlankなどのバリデーションの実行
						 * BindingResult:入力チェックのエラー情報はBindingResultインタフェースで表現される
						 * RedirectAttributes:リダイレクト先にパラメータを渡す
						 */
		//System.out.println("insert()通過");
		
		// FormからEntityへの詰め替え
		//Quiz quiz = makeQuiz(quizForm);
		Quiz quiz = new Quiz();
		quiz.setQuestion(quizForm.getQuestion());
		quiz.setAnswer(quizForm.getAnswer());
		quiz.setAuthor(quizForm.getAuthor());
		// 入力チェック
		if (!bindingResult.hasErrors()) {
			service.insertQuiz(quiz);	//クイズを登録
			redirectAttributes.addFlashAttribute("complete", "登録が完了しました");
			
			// リダイレクトで一覧画面に遷移する
	        // このとき、リダイレクト遷移先はGETメソッドとする
			return "redirect:/quiz";
		} else {
			// エラーがある場合は、一覧表示処理を呼びます
			return quiz(quizForm, model);
		}
	}
	
	
	/**
	 * Quizデータを１件取得し、フォーム内に表示する
	 * "/{id}"の@GetMapping
	 * 更新画面を表示する
	 * @return 
	 */
	@GetMapping(value = "/{id}")
	public String id(QuizForm quizForm, @PathVariable Integer id, Model model) {
						//@PathVariableURLに含まれる動的なパラメータを受け取ることができる
		//System.out.println("id()通過");
		
		//Quizを取得(Optionalでラップ)
		Optional<Quiz> quizOpt = service.selectOneById(id);
		//QuizFormへの詰め直し
		Optional<QuizForm> quizFormOpt = quizOpt.map(t -> makeQuizForm(t));//quizOpt.map:値が存在する場合は、指定されたマッピング関数をその値に適用し、結果がnullでなければ結果を記述するOptionalを返す
		//QuizFormがnullでなければ中身を取り出す
		if (quizFormOpt.isPresent()) {
			//このOptionalに値が存在する場合は値を返し、それ以外の場合はNoSuchElementExceptionをスロー
			quizForm = quizFormOpt.get();
		}
		// 更新用のModelを作成する
		makeUpdateModel(quizForm, model);
		return "crud";
	}
	
	/** 更新用のModelを作成する */
	private void makeUpdateModel(QuizForm quizForm, Model model) {
		model.addAttribute("id", quizForm.getId());
		quizForm.setNewQuiz(false);
		model.addAttribute("quizForm", quizForm);
		model.addAttribute("title", "更新用フォーム");
	}
	
	
	/**
	 * idをKeyにしてデータを更新する
	 * "/insert"の@PostMapping
	 * 更新処理を実行する
	 * @return 
	 */
	@PostMapping(value = "/update")
	public String update(@Validated QuizForm quizForm, BindingResult result,
							Model model, RedirectAttributes redirectAttributes) {
		//System.out.println("update()通過");
		
		//QuizFormからQuizに詰め直す
		Quiz quiz = makeQuiz(quizForm);
		// 入力チェック
		if (!result.hasErrors()) {
			//更新処理、フラッシュスコープの使用、リダイレクト（個々の編集ページ）
			service.updateQuiz(quiz);	//クイズ更新
			redirectAttributes.addFlashAttribute("complete", "更新が完了しました");
			// 更新画面を表示する
			return "redirect:/quiz/" + quiz.getId();
		} else {
			// 更新用のModelを作成する
			makeUpdateModel(quizForm, model);
			return "crud";
		}
	}
	
	
	/**
	 * idをKeyにしてデータを削除する
	 * "/delete"の@PostMapping
	 * 削除処理を実行する
	 * @return 
	 */
	@PostMapping(value = "/delete")
	public String delete(@RequestParam("id") String id,
							Model model, RedirectAttributes redirectAttributes) {
					//@RequestParam:指定したリクエストパラメータを取得
		//System.out.println("delete()通過");
		
		//タスクを1件削除してリダイレクト
		service.deleteQuizById(Integer.parseInt(id));
		redirectAttributes.addFlashAttribute("delcomplete", "削除が完了しました");
		return "redirect:/quiz";
	}
	
	
	/**
	 * Quizデータをランダムで１件取得し、画面に表示する
	 * "/play"の@GetMapping
	 * クイズ画面を表示する
	 * @return 
	 */
	@GetMapping(value = "/play")
	public String play(QuizForm quizForm, Model model) {
		//System.out.println("play()通過");
		
		//Quizを取得(Optionalでラップ)
		Optional<Quiz> quizOpt = service.selectOneRandomQuiz();
		// 値が入っているか判定する
		if (quizOpt.isPresent()) {
			// QuizFormへの詰め直し
			Optional<QuizForm> quizFormOpt = quizOpt.map(t -> makeQuizForm(t));
			quizForm = quizFormOpt.get();
		} else {
			model.addAttribute("msg", "問題がありません・・・");
			return "play";
		}
		// 表示用「Model」への格納
		model.addAttribute("quizForm", quizForm);
		return "play";
	}
	
	
	/**
	 * クイズの正解/不正解を判定する
	 * "/quiz"の@PostMapping
	 * クイズに回答する
	 * @return 
	 */
	@PostMapping(value = "/check")
	public String check(QuizForm quizForm, @RequestParam Boolean answer, Model model) {
		//System.out.println("check()通過");
		
		if (service.checkQuiz(quizForm.getId(), answer)) {
			model.addAttribute("msg", "正解です！");
		} else {
			model.addAttribute("msg", "残念、不正解です・・・");
		}
		return "answer";
	}
	
	
	
	// ---------- 【以下はFormとDomainObjectの詰めなおし】 ----------
		/** QuizFormからQuizに詰め直して戻り値とし返します */
		private Quiz makeQuiz(QuizForm quizForm) {
			Quiz quiz = new Quiz();
			quiz.setId(quizForm.getId());
			quiz.setQuestion(quizForm.getQuestion());
			quiz.setAnswer(quizForm.getAnswer());
			quiz.setAuthor(quizForm.getAuthor());
			return quiz;
		}

		/** QuizからQuizFormに詰め直して戻り値とし返します */
		private QuizForm makeQuizForm(Quiz quiz) {
			QuizForm form = new QuizForm();
			form.setId(quiz.getId());
			form.setQuestion(quiz.getQuestion());
			form.setAnswer(quiz.getAnswer());
			form.setAuthor(quiz.getAuthor());
			form.setNewQuiz(false);
			return form;
		}
}

