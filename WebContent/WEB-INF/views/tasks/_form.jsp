<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${errors != null}">
    <div id="flush_error">
        入力内容にエラーがあります。<br />
        <c:forEach var="error" items="${errors}"> <!-- List<String>型の変数errorsの中身を1つずつ「・〇〇が違う」といった形でタテに表示 -->
            ・<c:out value="${error}" /><br />
        </c:forEach>
    </div>

</c:if>

<label for="content_task">タスク</label><br />
<input type="text" name="content" id="content_task" value="${task.content}" /> <!-- typeでフォームの種類を、nameでサーブレットとの共通名を、valueで初期値を設定 -->
<br /><br />

<input type="hidden" name="_token" value="${_token}" /> <!-- Newサーブレットでセットした「_token=セッションID」を初期値で入れ込む -->
<button type="submit">登録</button> <!-- このbuttonを押すと、action属性で設定されたURLに送信される content,_tokenというnameで入力値が送信！-->