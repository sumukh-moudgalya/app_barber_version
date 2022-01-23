package com.defines.bloomerbarber


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    companion object{
        private const val RC_SIGN_IN=69
    }
    private val TAG="LoginActivity"
    private lateinit var auth:FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.hide()
        setContentView(R.layout.activity_login)

        val topAnim: Animation = AnimationUtils.loadAnimation(this,R.anim.top_anim)
        val bottomAnim: Animation = AnimationUtils.loadAnimation(this,R.anim.bottom_animation)


        val login_button:TextView=findViewById(R.id.login_activity_login_button)
        val book:TextView=findViewById(R.id.login_activity_booking_text)
        val simple:TextView=findViewById(R.id.login_activity_made_simple_text)
        val logo: ImageView =findViewById(R.id.login_activity_app_logo)
        val appName:TextView=findViewById(R.id.login_activity_app_name)




        login_button.animation=bottomAnim
        book.animation=topAnim
        simple.animation=topAnim
        logo.animation=topAnim
        appName.animation=topAnim

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth=FirebaseAuth.getInstance()

        login_button.setOnClickListener {
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
            val exception=task.exception
            if(task.isSuccessful){
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.w(TAG, "Google sign in failed", e)
                }
            }else{
                Log.d(TAG,"Error Logging in ${exception.toString()}")
            }

        }
    }
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    val uid=user!!.uid
                    val user_name= user!!.displayName
                    val profileImageUrl=user!!.photoUrl
                    val phoneNumber=user!!.phoneNumber
                    val email=user!!.email
                    val userType="barber"
                    val isshopDetails=false
                    Log.d(TAG,"prof uri:$profileImageUrl")
                    val ref= FirebaseDatabase.getInstance().getReference("/barber/$uid")
                    val customer= Barber(
                        uid,
                        user_name!!,
                        profileImageUrl.toString(),
                        email.toString(),"english",userType,phoneNumber.toString(),isshopDetails,
                        false
                    )


                    ref.setValue(customer).addOnSuccessListener {
                        Log.d(TAG, "Finally the user is saved to database")
                    }.addOnFailureListener{
                        Log.d(TAG,"Failed to add{${it.message}")
                    }
                    val ref2= FirebaseDatabase.getInstance().getReference("/wallet_barber/$uid")
                    val walletId= "BAR$uid"
                    val balance=0.0
                    val wallet=Wallet(
                        walletId,
                        balance
                    )
                    ref2.setValue(wallet).addOnSuccessListener {
                        Log.d(TAG, "Finally the user is saved to database")
                        val intent= Intent(this,ShopAdderActivity::class.java)
                        startActivity(intent)
                        finish()
                    }.addOnFailureListener{
                        Toast.makeText(this,"Unknown Error!Try again after some time",Toast.LENGTH_SHORT).show()
                        Log.d(TAG,"Failed to add{${it.message}")
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)

                }
            }
    }
}