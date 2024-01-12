package com.uniwaylabs.buildo.ui.authentication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.airbnb.lottie.LottieAnimationView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.uniwaylabs.buildo.BaseAppCompactActivity
import com.uniwaylabs.buildo.LocalDatabase.BDSharedPreferences
import com.uniwaylabs.buildo.R
import com.uniwaylabs.buildo.ToastMessages
import com.uniwaylabs.buildo.firebaseDatabase.Database.UserDB.UserDatabase
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseHandler
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseHandlerInterface
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.DrawerMenuDataModels.UserAccountModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.PermissionModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseUrls.DatabaseUrls
import com.uniwaylabs.buildo.firebaseDatabase.Repositories.RepositoryData
import com.uniwaylabs.buildo.ui.MainActivity
import com.uniwaylabs.buildo.ui.welcomeUI.GetStartedActivity
import com.uniwaylabs.buildo.utility.CustomToast
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class OTPValidationActivity : BaseAppCompactActivity() {
    private val mResendToken: PhoneAuthProvider.ForceResendingToken? = null
    private var verificationId: String? = null
    private var mAuth: FirebaseAuth? = null
    private var btnResend: Button? = null
    private var btnVerify: AppCompatButton? = null
    private var progressBar: LottieAnimationView? = null
    private var textViewShowPhone: TextView? = null
    private var textViewError: TextView? = null
    private var textViewTimer: TextView? = null
    private var googleSignInAccount: GoogleSignInAccount? = null
    private var phone: String? = null
    private var ecode1: EditText? = null
    private var ecode2: EditText? = null
    private var ecode3: EditText? = null
    private var ecode4: EditText? = null
    private var ecode5: EditText? = null
    private var ecode6: EditText? = null
    private val phoneText = "you have received verification code\nvia SMS at ****"
    private val dataKey = "PhoneNumber"
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_validation)
        textViewTimer = findViewById<View>(R.id.timeOtp) as TextView?
        ecode1 = findViewById<EditText>(R.id.ecode1Id)
        ecode2 = findViewById<EditText>(R.id.ecode2Id)
        ecode3 = findViewById<EditText>(R.id.ecode3Id)
        ecode4 = findViewById<EditText>(R.id.ecode4Id)
        ecode5 = findViewById<EditText>(R.id.ecode5Id)
        ecode6 = findViewById<EditText>(R.id.ecode6Id)
        btnResend = findViewById<Button>(R.id.resendbutton)
        btnVerify = findViewById<AppCompatButton>(R.id.verifybutton)
        textViewShowPhone = findViewById<TextView>(R.id.showPhoneId)
        textViewError = findViewById<TextView>(R.id.errorStatus)
        progressBar = findViewById(R.id.progressbar)
        setAutoFocusOnOtpDigits()
        setTextToShowLast4DigitsOfPhone()
        sendVerificationCodeToPhone()
        setVerifyBtnClickListener()
        setResendBtnClickListener()
    }

    private fun setAutoFocusOnOtpDigits() {
        ecode1?.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            textViewError?.visibility = View.GONE
            progressBar?.visibility = View.GONE
            if (ecode1?.text?.length == 1) ecode2?.requestFocus()
            false
        })
        ecode2?.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            textViewError?.visibility = View.GONE
            progressBar?.visibility = View.GONE
            if (ecode2?.text?.length == 1) ecode3?.requestFocus()
            false
        })
        ecode3?.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            textViewError?.visibility = View.GONE
            progressBar?.visibility = View.GONE
            if (ecode3?.text?.length == 1) ecode4?.requestFocus()
            false
        })
        ecode4?.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            textViewError?.visibility = View.GONE
            progressBar?.visibility = View.GONE
            if (ecode4?.text?.length == 1) ecode5?.requestFocus()
            false
        })
        ecode5?.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            textViewError?.visibility = View.GONE
            progressBar?.visibility = View.GONE
            if (ecode5?.text?.length == 1) ecode6?.requestFocus()
            false
        })
        ecode6?.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            textViewError?.visibility = View.GONE
            progressBar?.visibility = View.GONE
            if (ecode6?.text?.length == 1) {
                val imm =
                    (v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                onVerifyClick()
            }
            false
        })
    }

    private fun setTextToShowLast4DigitsOfPhone() {
        val phoneNumber: String? = intent.getStringExtra(dataKey)
        phone = phoneNumber
        if (phone != null) {
            val Last4digits = phoneNumber?.substring(9)
            val showPhone = phoneText + Last4digits
            textViewShowPhone?.text = showPhone
        } else {
            Toast.makeText(this@OTPValidationActivity, ToastMessages.wrongPhone, Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun sendVerificationCodeToPhone() {
        if (phone != null) {
            mAuth = FirebaseAuth.getInstance()
            sendVerrificationCode(phone!!)
        } else {
            Toast.makeText(this@OTPValidationActivity, ToastMessages.wrongPhone, Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun setVerifyBtnClickListener() {
        btnVerify!!.setOnClickListener(View.OnClickListener {
           onVerifyClick()
        })
    }

    private fun onVerifyClick(){
        if (progressBar?.visibility == View.VISIBLE) {
            val toast: Toast = Toast.makeText(
                this@OTPValidationActivity,
                ToastMessages.autoOtpDetection,
                Toast.LENGTH_LONG
            )
            toast.setGravity(Gravity.BOTTOM, 0, 150)
            toast.show()
        } else {
            val Code: String =
                (ecode1?.text.toString().trim { it <= ' ' } + ecode2?.text.toString()
                    .trim { it <= ' ' } + ecode3?.text.toString().trim { it <= ' ' }
                        + ecode4?.text.toString().trim { it <= ' ' } + ecode5?.text
                    .toString().trim { it <= ' ' } + ecode6?.text.toString()
                    .trim { it <= ' ' })
            if (Code.length != 6 || Code.contains(" ") || Code.contains("*") || Code.contains("#") || Code.contains(
                    "."
                )
            ) {
                textViewError?.visibility = View.VISIBLE
                return
            }
            progressBar?.visibility = View.VISIBLE
            verifyCode(Code)
        }
    }


    private fun addPermissions(){
        UserDatabase<String>().setDataToItemDatabase(this,DatabaseUrls.permissions, PermissionModel(), {})
    }

    private fun setResendBtnClickListener() {
        btnResend!!.setOnClickListener {
            if (progressBar?.visibility == View.VISIBLE) {
                val toast: Toast = Toast.makeText(
                    this@OTPValidationActivity,
                    ToastMessages.autoOtpDetection,
                    Toast.LENGTH_LONG
                )
                toast.setGravity(Gravity.BOTTOM, 0, 150)
                toast.show()
            } else if (timeLeft != 0L) {
                val toast: Toast = Toast.makeText(
                    this@OTPValidationActivity,
                    ToastMessages.timerNotComplete,
                    Toast.LENGTH_LONG
                )
                toast.setGravity(Gravity.BOTTOM, 0, 150)
                toast.show()
            } else {
                if (phone != null) {
                    textViewError?.visibility = View.GONE
                    resendVerificationCode(phone!!, mResendToken)
                }
            }
        }
    }

    private fun resendVerificationCode(
        phoneNumber: String,
        token: PhoneAuthProvider.ForceResendingToken?
    ) {
        setTextViewTimer(60000)
        textViewTimer?.visibility = View.VISIBLE
        if (mAuth == null){
            mAuth = FirebaseAuth.getInstance()
        }
        var options = token?.let {
            PhoneAuthOptions.newBuilder(mAuth!!)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallBack)
                .setForceResendingToken(it)
                .build()
        }
        if (options != null) {
            PhoneAuthProvider.verifyPhoneNumber(options)
        }
        else{
            sendVerificationCodeToPhone()
        }
    }

    private fun sendVerrificationCode(number: String) {
        setTextViewTimer(60000)
        textViewTimer?.visibility = View.VISIBLE

        if (mAuth == null){
            mAuth = FirebaseAuth.getInstance()
        }
        var options = PhoneAuthOptions.newBuilder(mAuth!!)
                .setPhoneNumber(number)
                .setTimeout(60, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallBack)
                .build()

        if (options != null) {
            PhoneAuthProvider.verifyPhoneNumber(options)
        }

    }

    private fun verifyCode(code: String) {
        val credential: PhoneAuthCredential? =
            verificationId?.let { PhoneAuthProvider.getCredential(it, code) }
        progressBar?.visibility = View.VISIBLE
        if (credential != null) {
            signInWithCredential(credential)
        }
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        mAuth?.signInWithCredential(credential)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    saveSignUpDataToDatabase()
                } else {
                    progressBar?.visibility = View.GONE
                    textViewError?.visibility = View.VISIBLE
                }
            }
    }

    private val mCallBack: PhoneAuthProvider.OnVerificationStateChangedCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onCodeSent(s: String, forceResendingToken: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(s, forceResendingToken)
                CustomToast.showToast(
                    this@OTPValidationActivity,
                    ToastMessages.successfulOTP,
                    Toast.LENGTH_SHORT
                )
                verificationId = s
            }

            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                val code: String? = phoneAuthCredential.smsCode
                if (code != null) {
                    ecode1?.setText("" + code[0])
                    ecode2?.setText("" + code[1])
                    ecode3?.setText("" + code[2])
                    ecode4?.setText("" + code[3])
                    ecode5?.setText("" + code[4])
                    ecode6?.setText("" + code[5])
                    progressBar?.visibility = View.VISIBLE
                    verifyCode(code)
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(this@OTPValidationActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }

    private fun saveSignUpDataToDatabase() {
        userAccount
        addPermissions()
        moveToNextActivity()
    }

    private fun setTextViewTimer(time: Long) {
        val countDownTimer: CountDownTimer = object : CountDownTimer(time, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                updateTimer(millisUntilFinished)
            }

            override fun onFinish() {
                timeLeft = 0
                textViewTimer?.visibility = View.INVISIBLE
                progressBar?.visibility = View.INVISIBLE
                textViewError?.visibility = View.GONE
            }
        }
        countDownTimer.start()
    }

    fun updateTimer(timerLeftinMillis: Long) {
        timeLeft = timerLeftinMillis
        val minutes = (timerLeftinMillis / 1000).toInt() / 60
        val seconds = (timerLeftinMillis / 1000).toInt() % 60
        val timeLeftformatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        textViewTimer?.text = timeLeftformatted
    }

    private val userAccount: Unit
        private get() {
            DatabaseHandler.getInstance().getUserDataFromDatabase(
                this@OTPValidationActivity,
                DatabaseUrls.account_path
            ) { data -> if (!data.exists()) saveAccountDataToDatabase() }
        }

    private fun saveAccountDataToDatabase() {
        try {
            googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this)
        } catch (e: Exception) {
//            Toast.makeText(this@OTPValidationActivity, ToastMessages.signInError, Toast.LENGTH_LONG)
//                .show()
        }
        if (googleSignInAccount != null) {
            var imgUrl = ""
            if (googleSignInAccount!!.photoUrl != null) imgUrl =
                googleSignInAccount!!.photoUrl.toString()
            val userAccountModel = UserAccountModel(
                googleSignInAccount!!.displayName,
                phone,
                imgUrl,
                googleSignInAccount!!.email,
                googleSignInAccount!!.givenName,
                googleSignInAccount!!.familyName,
                null,
                0.0,
                0.0
            )
            DatabaseHandler.getInstance().saveDataToDatabase(
                this@OTPValidationActivity,
                DatabaseUrls.account_path,
                userAccountModel
            )
        } else {
            var userAccountModel: UserAccountModel? =
                BDSharedPreferences.shared.getAccountData(this@OTPValidationActivity)
            if (userAccountModel != null) {
                userAccountModel.phone = phone
                DatabaseHandler.getInstance().saveDataToDatabase(
                    this@OTPValidationActivity,
                    DatabaseUrls.account_path,
                    userAccountModel
                )
            } else {
                userAccountModel =
                    UserAccountModel(phone, phone, " ", " ", " ", " ", Date(), 0.0, 0.0)
                DatabaseHandler.getInstance().saveDataToDatabase(
                    this@OTPValidationActivity,
                    DatabaseUrls.account_path,
                    userAccountModel
                )
            }
        }
        DatabaseHandler.getInstance()
            .saveDataToDatabase(this@OTPValidationActivity, DatabaseUrls.token_path, "")
        addPermissions()
        registerToCustomerSearch(
            phone,
            RepositoryData.getInstance().userId
        )
    }

    private fun moveToNextActivity() {
        val intent = Intent(this@OTPValidationActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun registerToCustomerSearch(phone: String?, uid: String) {

        if (phone != null) {
            FirebaseDatabase.getInstance().reference.child(DatabaseUrls.customer_search_path)
                .child(phone).setValue(uid)
        }
    }

    companion object {
        private var timeLeft: Long = 0
    }
}