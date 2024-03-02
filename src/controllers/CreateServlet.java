package controllers;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.Task;
import models.validators.MessageValidator;
import utils.DBUtil;

/**
 * Servlet implementation class CreateServlet
 */
@WebServlet("/create")
public class CreateServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String _token = request.getParameter("_token");  //new.jsp(form.jsp)で_tokenの名前でセッションIDを入力していたので、それをgetしている
        if(_token != null && _token.equals(request.getSession().getId())) { //Newサーブレットで取得したセッションIDとこのサーブレットで取得するIDの一致を確認

            EntityManager em = DBUtil.createEntityManager();  //データベースのトランザクションを始める
            em.getTransaction().begin();

            Task t = new Task();

            String content = request.getParameter("content");//name=contentとしたフォームの入力値String型の変数contentに入れる
            t.setContent(content);

            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            t.setCreated_at(currentTime);
            t.setUpdated_at(currentTime);

            // バリデーションを実行してエラーがあったら新規登録のフォームに戻る
            List<String> errors = MessageValidator.validate(t); //MessageValidatorクラスのvalidateメソッドからの戻り値を変数errorsに格納
            if(errors.size() > 0) {
                em.close();

                // フォームに初期値を設定、さらにエラーメッセージを送る(リクエストスコープの設定は1回のページ遷移のみ保存されているので、_tokenは改めて設定必要)
                request.setAttribute("_token", request.getSession().getId()); //「_token」の名前で改めてセッションIDをセット
                request.setAttribute("task", t); //「task」の名前でTaskオブジェクト（属性にcontentなどを持つ）をセット
                request.setAttribute("errors", errors); //

                RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/tasks/new.jsp");
                rd.forward(request, response);

            } else {
                // データベースに保存
                em.persist(t);  //Messageオブジェクトをデータベースに保存
                em.getTransaction().commit(); //トランザクションの完了
                request.getSession().setAttribute("flush", "登録が完了しました。");
                em.close(); //EntityManagerを閉じる

                // indexのページにリダイレクト
                response.sendRedirect(request.getContextPath() + "/index");
            }

        }
    }

}
