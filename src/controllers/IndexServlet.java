package controllers;

import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.Task;
import utils.DBUtil;

/**
 * Servlet implementation class IndexServlet
 */
@WebServlet("/index")
public class IndexServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public IndexServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        EntityManager em = DBUtil.createEntityManager(); //データベースと接続

        // 開くページ数を取得
        int page = 1;  //デフォルトで「1」を変数pageに入力
        try {
            page = Integer.parseInt(request.getParameter("page")); //URL末尾「?page=2」などの数字をゲット
        } catch(NumberFormatException e ) {} //page=aなどの場合エラーが発生した場合にキャッチ

        // 最大件数と開始位置を指定してメッセージを取得
        List<Task> tasks = em.createNamedQuery("getAllTasks", Task.class)
                                   .setFirstResult(15 * (page - 1)) //指定されたpageにおいて、何件目からデータを取得するかを指定（page=3なら30件目から）
                                   .setMaxResults(15) //そのページで最大何件表示するか（今回は最大15件表示する）
                                   .getResultList();  //複数のデータを取得する

        // 全件数を取得
        long tasks_count = (long)em.createNamedQuery("getTasksCount", Long.class)
                                      .getSingleResult(); //取得結果が1件のみ期待されるクエリで、ここではメッセージの全件（例：60）をlong型で取得している

        em.close();  //データベースとの接続を切る

        request.setAttribute("tasks", tasks); //指定されたページで表示すべき最大15件のtaskオブジェクト（id,contentをもつ）
        request.setAttribute("tasks_count", tasks_count);   // 全件数
        request.setAttribute("page", page); //開くpage番号

        // フラッシュメッセージがセッションスコープにセットされていたら
        // リクエストスコープに保存する（セッションスコープからは削除）
        if(request.getSession().getAttribute("flush") != null) {
            request.setAttribute("flush", request.getSession().getAttribute("flush"));
            request.getSession().removeAttribute("flush");
        }

        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/tasks/index.jsp");
        rd.forward(request, response);
    }

}

