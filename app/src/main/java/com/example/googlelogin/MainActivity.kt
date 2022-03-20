package com.example.googlelogin


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    companion object {
        private const val RC_SIGN_IN: Int = 89
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /* Email Register */
        val email = findViewById<TextInputEditText>(R.id.login_email)
        val password = findViewById<TextInputEditText>(R.id.login_password)
        val registerBtn = findViewById<Button>(R.id.register_button)

        registerBtn.setOnClickListener {

            if(!email.text.toString().isEmpty() && !password.text.toString().isEmpty())
            {
                mAuth.createUserWithEmailAndPassword(

                    email.text.toString().trim(),
                    password.text.toString().trim()

                )
                val intent = Intent(this, SignedIn::class.java)
                startActivity(intent)
                finish()
                Log.d("niku"," send")
            }
            else{
                Toast.makeText(this,"Email or password is Empty" ,Toast.LENGTH_SHORT).show()
            }


        }


        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("89069143646-3e88fqf7o8lgakk9nntr0bh4gfn167uo.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser
        /* checks if user is signed in and then skip activity*/
        Handler().postDelayed({
            if (user != null) {
                val dashboardIntent = Intent(this, SignedIn::class.java)
                Toast.makeText(this, "Already Signed In ${user.displayName}", Toast.LENGTH_SHORT)
                    .show()
                startActivity(dashboardIntent)
            }
        }, 2000)

        googleBtn.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception = task.exception
            if (task.isSuccessful) {
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d("NIKU", "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.w("NIKU", "Google sign in failed", e)
                }
            } else {
                Log.w("NIKU", exception.toString())
            }

        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithCredential:success")
                    val intent = Intent(this, SignedIn::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this, "signInWithCredential:failure", Toast.LENGTH_SHORT).show()

                }
            }
    }
}