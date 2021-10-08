package com.robivan.simplenote

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton

class AuthFragment : Fragment() {
    private var googleSignInClient: GoogleSignInClient? = null

    // Кнопка регистрации через Google
    private lateinit var buttonSignIn: SignInButton
    private lateinit var emailView: TextView
    private lateinit var continueBtn: MaterialButton
    private var account: GoogleSignInAccount? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_auth, container, false)
        initGoogleSign()
        initView(view)
        enableSign()
        return view
    }

    // Инициализация запроса на аутентификацию
    private fun initGoogleSign() {
        // Конфигурация запроса на регистрацию пользователя, чтобы получить
        // идентификатор пользователя, его почту и основной профайл
        // (регулируется параметром)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        // Получаем клиента для регистрации и данные по клиенту
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
    }

    // Инициализация пользовательских элементов
    private fun initView(view: View) {
        // Кнопка регистрации пользователя
        buttonSignIn = view.findViewById(R.id.sign_in_button)
        buttonSignIn.setOnClickListener { signIn() }
        emailView = view.findViewById(R.id.email)

        // Кнопка «Продолжить», будем показывать главный фрагмент
        continueBtn = view.findViewById(R.id.continue_btn)
        continueBtn.setOnClickListener { (requireActivity() as Controller).openMainScreen() }
    }

    override fun onStart() {
        super.onStart()
        // Проверим, входил ли пользователь в это приложение через Google
        account = GoogleSignIn.getLastSignedInAccount(requireContext())
        if (account != null) {
            // Пользователь уже входил, сделаем кнопку недоступной
            disableSign()
            // Обновим почтовый адрес этого пользователя и выведем его на экран
            updateUI()
        }
    }

    // Инициируем регистрацию пользователя
    private fun signIn() {
        val signInIntent = googleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    // Здесь получим ответ от системы, что пользователь вошёл
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            // Когда сюда возвращается Task, результаты аутентификации уже
            // готовы
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    interface Controller {
        fun openMainScreen()
    }

    // Получаем данные пользователя
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            account = completedTask.getResult(ApiException::class.java)

            // Регистрация прошла успешно
            disableSign()
            updateUI()
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure
            // reason. Please refer to the GoogleSignInStatusCodes class
            // reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
        }
    }

    // Обновляем данные о пользователе на экране
    private fun updateUI() {
        emailView.text = account!!.email
        User.getUserData(account!!.givenName, account!!.email)
    }

    // Разрешить аутентификацию и запретить остальные действия
    private fun enableSign() {
        buttonSignIn.isEnabled = true
        continueBtn.isEnabled = false
    }

    // Запретить аутентификацию (уже прошла) и разрешить остальные действия
    private fun disableSign() {
        buttonSignIn.isEnabled = false
        continueBtn.isEnabled = true
    }

    companion object {
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        // Используется, чтобы определить результат activity регистрации через
        // Google
        private const val RC_SIGN_IN = 40404
        private const val TAG = "GoogleAuth"
        fun newInstance(): AuthFragment {
            return AuthFragment()
        }
    }
}