package com.ten.lifecat.phone.view.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
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
import com.ten.lifecat.phone.presenter.AccountLocalPresenter
import com.ten.lifecat.phone.util.MyDatabaseHelper

class SignupActivity : BaseActivity() {

    private lateinit var _nameText: EditText
    private lateinit var _emailText: EditText
    private lateinit var _passwordText: EditText
    private lateinit var _signupButton: Button
    private lateinit var _loginLink: TextView
    private val account = AccountLocalPresenter(this)

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.requestWindowFeature(Window.FEATURE_NO_TITLE) // 隐藏标题栏
        setContentView(R.layout.activity_signup)
        initView()

    }

    /**
     * 获取组件
     */
    private fun initView() {
        _nameText = findViewById(R.id.input_name)
        _emailText = findViewById(R.id.input_email)
        _passwordText = findViewById(R.id.input_password)
        _signupButton = findViewById(R.id.btn_signup)
        _loginLink = findViewById(R.id.link_login)

        _signupButton.setOnClickListener {
            signup()
        }
        /* 登录链接按钮 */
        _loginLink.setOnClickListener {
            startActivity(Intent(applicationContext, LoginActivity::class.java))
            finish()
        }
    }

    /**
     * 注册
     */
    fun signup() {
        Log.d(TAG, "Signup")

        /* 验证表单格式 */
        if (!validate()) {
            onSignupFailed()
            return
        }

        _signupButton.isEnabled = false

        val progressDialog = ProgressDialog(this@SignupActivity, R.style.AppTheme_Dark_Dialog)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage("Creating Account...")
        progressDialog.show()

        val name = _nameText.text.toString()
        val email = _emailText.text.toString()
        val password = _passwordText.text.toString()

        //写入数据到User表中
        val dbHelper = MyDatabaseHelper(this, "Lifecat.db", 1)
        val db = dbHelper.writableDatabase
        val values1 = ContentValues().apply {
            // 开始组装第一条数据
            put("user_name", name)
            put("user_email", email)
            put("user_password", password)
            put("is_login", 1)
        }
        db.insert("User", null, values1) // 插入第一条数据

        //写入数据到sp中
        account.userEmail = email
        account.userPassword = password
        account.hasLogin = true

        android.os.Handler().postDelayed(
                {
                    // On complete call either onSignupSuccess or onSignupFailed
                    onSignupSuccess()
                    progressDialog.dismiss()
                    finish()
                }, 1000)
    }

    /**
     * @description 注册成功-->跳转到MainActivity
     */
    fun onSignupSuccess() {
        _signupButton.isEnabled = true
        setResult(Activity.RESULT_OK, null)



        /* 跳转 */
        val intent = Intent()
        intent.setClass(this@SignupActivity, MainActivity::class.java)
        startActivity(intent)

        /* 销毁当前Activity */
        finish()
    }

    /**
     * @description 注册失败
     */
    fun onSignupFailed() {
        _signupButton.isEnabled = true
        Toast.makeText(baseContext, "Singup failed", Toast.LENGTH_LONG).show()
    }

    /**
     * @description 表单格式验证
     */
    fun validate(): Boolean {
        var valid = true

        val name = _nameText.text.toString()
        val email = _emailText.text.toString()
        val password = _passwordText.text.toString()

        /* name */
        if (name.isEmpty() || name.length < 3) {
            _nameText.error = "at least 3 characters"
            valid = false
        } else {
            _nameText.error = null
        }

        /* email */
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.error = "enter a valid email address"
            valid = false
        } else {
            _emailText.error = null
        }

        /* password */
        if (password.isEmpty() || password.length < 4 || password.length > 10) {
            _passwordText.error = "between 4 and 10 alphanumeric characters"
            valid = false
        } else {
            _passwordText.error = null
        }

        return valid
    }

    companion object {
        /* 广播信息 */
        private val TAG = "lifecat SignupActivity"
    }
}