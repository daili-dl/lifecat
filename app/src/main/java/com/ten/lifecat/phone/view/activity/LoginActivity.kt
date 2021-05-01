package com.ten.lifecat.phone.view.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

import com.ten.lifecat.phone.R
import com.ten.lifecat.phone.presenter.AccountRemotePresenter
import com.ten.lifecat.phone.presenter.AccountLocalPresenter
import com.ten.lifecat.phone.util.MyDatabaseHelper
import org.jetbrains.anko.startActivity

/**
 * 用户登录注册界面
 */
class LoginActivity : BaseActivity() {

    /**
     * 登录邮箱
     */
    private lateinit var emailText: EditText
    /**
     * 登录密码
     */
    private lateinit var passwordText: EditText
    /**
     * 登录按钮
     */
    private lateinit var loginButton: Button

    /**
     * 跳转到注册界面
     */
    private lateinit var signupLink: TextView
    /**
     * 使用体验账户登录
     */
    private lateinit var experience: TextView

    private val dbHelper = MyDatabaseHelper(this, "Lifecat.db", 1) //创建数据库，并且只会创建一次

    companion object {
        /* 广播信息 */
        private val TAG = "lifecat LoginActivity"
        private val REQUEST_SIGNUP = 123
    }


    private val account = AccountLocalPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_login)
//        val dbHelper = MyDatabaseHelper(this, "Lifecat.db", 1) //创建数据库，并且只会创建一次
        dbHelper.writableDatabase
        initView()

        account.userEmail.let {
            emailText.setText(account.userEmail)
        }
        account.userPassword.let {
            passwordText.setText(account.userPassword)
        }
    }

    /**
     * 登录表单格式验证
     */
    private fun validate(): Boolean {
        val email = emailText.text.toString()
        val password = passwordText.text.toString()

        // email=null 或 email不符合格式
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.error = "enter a valid email address"
            return false
        } else {
            emailText.error = null
        }

        // password==null 或 password小于4字符 或 password大于10字符
        if (password.isEmpty() || password.length < 4 || password.length > 10) {
            passwordText.error = "between 4 and 10 alphanumeric characters"
            return false
        } else {
            passwordText.error = null
        }

        return true
    }

    private fun initView () {
        emailText = findViewById(R.id.input_email)!!
        passwordText = findViewById(R.id.input_password)
        loginButton = findViewById(R.id.btn_login)
        signupLink = findViewById(R.id.link_signup)
        experience = findViewById(R.id.experience)

        loginButton.setOnClickListener {
            login()
        }
        signupLink.setOnClickListener {
            startActivity<SignupActivity>()
            startActivityForResult(Intent(applicationContext, SignupActivity::class.java), REQUEST_SIGNUP)
            finish()
        }
    }

    /**
     * 登录button事件
     */
    fun login() {
        Log.d(TAG, "Login")

        /* 表单验证-->若失败，直接return */
        if (!validate()) {
            onLoginFailed()
            return
        }

        /* 锁定登录按钮直至验证完成 */
        loginButton.isEnabled = false

        /* 验证时:弹出框 */
        val progressDialog = ProgressDialog(this@LoginActivity,
                R.style.AppTheme_Dark_Dialog)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage("Authenticating...")
        progressDialog.show()

        /* 获取用户登录信息 */
        val email = emailText.text.toString()
        val password = passwordText.text.toString()

        // TODO: Implement your own authentication logic here.
        /*------ 验证逻辑 ------*/

        val db = dbHelper.writableDatabase
        val cursor = db.query("User", null, "user_email = ?", arrayOf(email), null, null, null)
//        val cursor = db.query("User", null, null, null, null, null, null)

        if (cursor.moveToFirst()) { //查询成功
            do {
                // 遍历Cursor对象，取出数据
                val user_password = cursor.getString(cursor.getColumnIndex("user_password"))
                if (password.equals(user_password)) { //验证成功
                    Log.d(TAG, "验证成功！")
                    onLoginSuccess()
                    progressDialog.dismiss()
                } else { //验证失败
                    Log.d(TAG, "验证失败！")
                    onLoginFailed()
                    progressDialog.dismiss()
                }

//                val name = cursor.getString(cursor.getColumnIndex("user_name"))
//                valadb loo email = cursor.getString(cursor.getColumnIndex("user_email"))
//                val password = cursor.getString(cursor.getColumnIndex("user_password"))
//                Log.d("fsh", "user name is $name")
//                Log.d("fsh", "user email is $email")
//                Log.d("fsh", "user password is $password")

            } while (cursor.moveToNext())
        } else { //查询失败
            Log.d(TAG, "查询失败！")
            onLoginFailed()
            progressDialog.dismiss()
        }
        cursor.close()

//        if (AccountRemotePresenter.validateUser(email, password)) {
//            //认证成功
//            android.os.Handler().postDelayed(
//                    {
//                        // On complete call either onLoginSuccess or onLoginFailed
//                        onLoginSuccess()
//                        progressDialog.dismiss()
//                    }, 1000)
//        } else {
//            //认证失败
//            android.os.Handler().postDelayed(
//                    {
//                        // On complete call either onLoginSuccess or onLoginFailed
//                        onLoginFailed()
//                        progressDialog.dismiss()
//                    }, 1000)
//        }
    }

    /*---------- Activity方法 ----------*/

    /**
     * 登录成功-->UserProfileActivity
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == Activity.RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                /*------ 登录成功 ------*/
                val intent = Intent()
                intent.setClass(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                this.finish()
            }
        }
    }

    /**
     *  登录成功
     */
    fun onLoginSuccess() {
        loginButton.isEnabled = true

        // 邮箱密码存储到本地数据库，设置登录标记为true
        account.userEmail = emailText.text.toString()
        account.userPassword = passwordText.text.toString()
        account.hasLogin = true
        Log.d(TAG, "hasLogin: $account.hasLogin")

        Toast.makeText(this, "welcome " + account.userEmail, Toast.LENGTH_LONG).show()

        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        finish()
    }

    /**
     * 登录失败
     */
    fun onLoginFailed() {
        Toast.makeText(baseContext, "Login failed", Toast.LENGTH_LONG).show()
        loginButton.isEnabled = true
    }


    /**
     * 返回键
     */
    override fun onBackPressed() {
        // Disable going back to the WelcomeActivity
        moveTaskToBack(true)
    }

}
